# NOTES

## What I changed and why

**The feature: a per-user redemption limit, per campaign, default 2.**

- **`per_user_limit` column on `campaign`** (`schema.sql`, `data.sql`, `Campaign` entity), `NOT NULL DEFAULT 2`. I stored it as a column rather than a separate table because the limit is a per-campaign attribute; a separate table only earns its keep if limits vary by user or tier, which the brief doesn't ask for.

- **Counting rule: all redemption rows count.** `RedemptionRepository.countByCampaignIdAndUserId` counts every redemption row for a user in a campaign, regardless of the voucher's current status. This treats *the redemption event* as the thing being limited, and it's the simplest, most predictable definition. Consequence: `user-99` in the seed data has one `REDEEMED` and one `VOID` row, so they are already at the default limit of 2 — which the tests assert.

- **Enforcement in `VoucherService.redeem`:** the method is now `@Transactional` and takes a **pessimistic write lock on the campaign row** (`CampaignRepository.findByIdForUpdate`) before any checks. It rejects with `RedeemResponse.fail("Per-user redemption limit reached")` when `count >= perUserLimit`, keeping the existing response envelope (HTTP 200 + `result:"FAILED"`) so this failure looks like every other one.

- **Why the lock, not just `@Transactional`:** the check is count-then-act. Without mutual exclusion, two concurrent redemptions by the same user both read the same stale count and both pass. `@Transactional` alone does **not** fix this — under `READ_COMMITTED` each transaction still reads the latest committed value, i.e. the same stale count. Locking the campaign row serializes all redemptions for that campaign, so the count is evaluated against committed state one redemption at a time. The same lock also closes two **pre-existing** races for free: the `remaining_stock` decrement and double-redeeming a single voucher (same voucher ⇒ same campaign ⇒ same lock).

- **Refreshing the voucher after locking:** I have to read the voucher before I know its campaign (I need its `campaignId` to take the lock), so its status can be stale by the time I hold the lock. `entityManager.refresh(voucher)` re-reads committed state so the `REDEEMED`/`VOID` checks are correct. A plain re-query would not have helped — the JPA first-level cache returns the already-managed (stale) instance.

- **Tests** (`VoucherServiceTest`): limit behaviour, per-campaign scoping, the seeded `user-99` case, and a concurrency test that fires five simultaneous same-user redemptions and asserts exactly **two** commit — which is what actually proves the lock works. `AuditClient` is mocked so the suite never makes the (untimed) audit HTTP call.

## Noticed but deliberately did NOT change

- **The audit call runs inside the transaction / lock window.** It's a synchronous `RestTemplate` call with no timeout, so a slow audit host now extends how long the campaign row lock is held. My change makes this worse than before. I left it to keep the change proportionate, but it's the first thing I'd move (see next section). Flagged prominently because it directly interacts with the locking I added.
- **Hardcoded audit API key** in `AuditClient` source, also sent in the request body. It belongs in config/a secret and an `Authorization` header. Security issue, but out of scope for this feature.
- **All failures return HTTP 200** with `result:"FAILED"`. I kept the over-limit rejection consistent with every other failure path rather than introduce a lone `409`. Fixing REST semantics properly means touching all the failure paths — a separate change.
- **`voidVoucher` is unguarded:** it will void an already-`REDEEMED` voucher and doesn't restore `remaining_stock`, further desyncing the counter. Not in scope.
- **Two sources of truth for availability:** `remaining_stock` (a counter) vs the actual voucher rows. The seed says campaign 1 has stock 100 but only 6 voucher rows exist. Pre-existing modelling choice; I didn't reconcile it.
- **N+1 in `CampaignStatsService`** (a query per voucher), no unique constraint on `voucher.code` despite `findByCode` assuming uniqueness, and `java.util.Date` on entities. All noted, none touched.
- **Field injection in `VoucherService`.** The class uses `@Autowired` fields; I added `EntityManager` via `@PersistenceContext` in the same style rather than refactoring the whole class to constructor injection — surgical.

## What would break on 3 instances behind a load balancer

The lock makes the limit safe **within a single instance against a shared database**. But the app is configured with **H2 in-memory** (`jdbc:h2:mem:campaigndb`), so each instance gets its **own** database, seeded independently. Behind a load balancer across three instances:

- the same voucher can be redeemed **once per instance**,
- the per-user limit is counted **per instance** (so effectively `limit × 3`),
- `/stats` diverges depending on which instance answers.

The locking is **necessary but not sufficient** — it only holds once all instances point at one shared datastore. This is a deployment/configuration fact, not something the feature code can fix on its own.

## If I had another day

- Move the audit call to **after commit** (`ApplicationEventPublisher` + `@TransactionalEventListener(AFTER_COMMIT)`) and add connect/read timeouts to `RestTemplate`; make audit failures retryable/queued rather than silently swallowed.
- Point at a **real shared DB** (Postgres) with a migration tool (Flyway/Liquibase) instead of `schema.sql`/`data.sql`, and add a **DB-level backstop** for the limit rather than relying solely on app-level locking.
- Add a **unique constraint on `voucher.code`**.
- Allow setting `per_user_limit` at campaign creation (there's no campaign-write endpoint today) and validate it (`>= 1`).
- Consider **optimistic locking** (`@Version`) if contention on hot campaigns becomes a throughput problem.
- Broaden tests: a `MockMvc` controller-layer test, and one asserting the stock and limit races together.

## Reflection

**What did I get wrong first, and how did I notice?**
I first framed the fix as "make `redeem` `@Transactional` and add a count check," assuming the transaction would be enough. Walking through the actual interleaving showed it wasn't: under `READ_COMMITTED` both transactions still read the same stale count — a transaction gives atomicity, not mutual exclusion. That's what drove the pessimistic row lock. A second miss surfaced when I reasoned about the same-voucher double-redeem case: the voucher is read *before* the lock is held, so its status can be stale even with the lock, and a re-query wouldn't refresh it because of the JPA first-level cache — which is why `entityManager.refresh` went in.

**Which AI suggestion did I reject, and why?**
The AI recommended the proportionate option of doing a simple count check and just *documenting* the concurrency gap. I rejected that and required the limit to be genuinely race-safe with a pessimistic lock, accepting the extra work and a concurrency test — a limit that silently fails under concurrency isn't really a limit.

**What took longest?**
Test isolation. The tests share one seeded in-memory DB with no rollback, and an existing test permanently redeems a voucher. Getting reliable, order-independent tests — `@Transactional` rollback for the simple cases, dedicated committed fixtures for the concurrency case, and mocking the audit client — took the most iteration.
