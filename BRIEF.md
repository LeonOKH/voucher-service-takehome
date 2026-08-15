# Take-Home Exercise — Backend Developer

Thanks for your interest in the role. This exercise is designed to take **3–4 hours**. Please submit within **1 day** of receiving it.

If it's taking much longer than 4 hours, stop and send us what you have with a note on where you got to — that's a perfectly good outcome, and we'd rather see honest scoping than a rushed weekend.

---

## Context

`voucher-service` is an internal service we use for client promotional campaigns. A client runs a campaign with a fixed stock of vouchers; end users redeem a voucher code through the client's storefront, which calls this service.

The code has been around a while and has been worked on by several people. Treat it as you would any existing production codebase you've just been handed.

## Your task

**Add a per-user redemption limit.**

Each campaign should define the maximum number of vouchers a single user may redeem in that campaign. When a user tries to redeem beyond their limit, the request should be rejected with a clear reason.

Use `2` as the default limit for existing campaigns.

That's the whole feature. How you model, store, and enforce it is your call.

## What we'd like back

Please send a link to a **Git repository** (GitHub/GitLab, public or invite us) containing:

**1. Your code**
Commit as you work — we'd like to see the real history. Messy is fine; we're not judging commit hygiene, we're interested in how the work actually progressed. Please don't squash it into one commit.

**2. A short `NOTES.md`** covering:
- What you changed and why
- Anything you noticed but deliberately **did not** change, and your reasoning
- What you'd do next if you had another day

Plus three short reflection answers:
- What did you get wrong first, and how did you notice?
- Which AI suggestion did you reject, and why?
- What took you longest?

**3. Your AI conversation log**, exported as markdown or text.

We expect you to use AI — that's how we work. Please export the **raw, uncurated** log, including the dead ends and the bits where it gave you something wrong. We're genuinely more interested in how you work with the tool than in a tidy transcript. Claude Code, Cursor, ChatGPT and Copilot all support exporting.

**4. A screen recording** covering exactly three things:
- Walk us through your change (~2 min)
- One thing in this codebase you would change but deliberately didn't — and why
- What would break if we ran this service on 3 instances behind a load balancer?

**Aim for 5–10 minutes; 30 minutes is the hard maximum.** Shorter is genuinely better — please don't script or edit it. One unrehearsed take is exactly what we want, and we're not assessing production value. Loom, OBS, QuickTime, anything.

## Ground rules

- **Use AI freely.** It's expected, not tolerated.
- **Java + Spring Boot.** If you haven't used them before, that's fine and we know some of you haven't — show us how you got up to speed. That's a legitimate part of what we're assessing.
- Keep the change proportionate to a 3–4 hour exercise.
- If something in this brief is unclear, **email us** — that's not a penalty.

## Running it

```
./gradlew bootRun     # starts on :8080, H2 in-memory, seeded on boot
./gradlew test
```

See `README.md` in the repo for endpoints and sample data.

**Email attachment note:** `voucher-service/gradlew.bat` was renamed to `gradlew.bat.txt` so corporate email filters do not block the zip. On Windows, rename it back to `gradlew.bat` before running. macOS/Linux can ignore this and use `./gradlew` as usual.

---

Any questions, reply to this email. Good luck — we're looking forward to seeing how you work.
