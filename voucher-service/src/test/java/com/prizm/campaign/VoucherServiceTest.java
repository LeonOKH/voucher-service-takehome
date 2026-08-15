package com.prizm.campaign;

import com.prizm.campaign.dto.RedeemResponse;
import com.prizm.campaign.model.Campaign;
import com.prizm.campaign.model.Voucher;
import com.prizm.campaign.repository.CampaignRepository;
import com.prizm.campaign.repository.RedemptionRepository;
import com.prizm.campaign.repository.VoucherRepository;
import com.prizm.campaign.service.AuditClient;
import com.prizm.campaign.service.VoucherService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class VoucherServiceTest {

    @Autowired
    private VoucherService voucherService;

    @Autowired
    private CampaignRepository campaignRepository;

    @Autowired
    private VoucherRepository voucherRepository;

    @Autowired
    private RedemptionRepository redemptionRepository;

    // Keep the (untimed) audit HTTP call out of the tests, so nothing here
    // depends on the network and the concurrency test stays deterministic.
    @MockBean
    private AuditClient auditClient;

    @Test
    void redeemActiveVoucherSucceeds() {
        RedeemResponse res = voucherService.redeem("RAYA-0001", "user-1");
        assertEquals("OK", res.getResult());
    }

    @Test
    void redeemAlreadyRedeemedVoucherFails() {
        RedeemResponse res = voucherService.redeem("RAYA-0004", "user-2");
        assertEquals("FAILED", res.getResult());
    }

    @Test
    void redeemUnknownCodeFails() {
        RedeemResponse res = voucherService.redeem("NOPE-9999", "user-3");
        assertEquals("FAILED", res.getResult());
    }

    @Test
    @Transactional
    void redeemUnderLimitSucceeds() {
        RedeemResponse res = voucherService.redeem("RAYA-0002", "limit-user-a");
        assertEquals("OK", res.getResult());
    }

    @Test
    @Transactional
    void redeemAtLimitIsRejected() {
        String user = "limit-user-b";
        assertEquals("OK", voucherService.redeem("RAYA-0002", user).getResult());
        assertEquals("OK", voucherService.redeem("RAYA-0003", user).getResult());

        RedeemResponse third = voucherService.redeem("RAYA-0006", user);
        assertEquals("FAILED", third.getResult());
        assertEquals("Per-user redemption limit reached", third.getMessage());
    }

    @Test
    @Transactional
    void seededUserAlreadyAtLimitIsRejected() {
        // user-99 already has two redemptions on campaign 1 in the seed data.
        RedeemResponse res = voucherService.redeem("RAYA-0006", "user-99");
        assertEquals("FAILED", res.getResult());
        assertEquals("Per-user redemption limit reached", res.getMessage());
    }

    @Test
    @Transactional
    void limitIsCountedPerCampaign() {
        String user = "cross-campaign-user";
        // Reach the limit on campaign 1...
        assertEquals("OK", voucherService.redeem("RAYA-0002", user).getResult());
        assertEquals("OK", voucherService.redeem("RAYA-0003", user).getResult());
        // ...a redemption on campaign 2 has its own separate count.
        RedeemResponse other = voucherService.redeem("MRDK-0001", user);
        assertEquals("OK", other.getResult());
    }

    @Test
    void concurrentRedemptionsAreCappedAtPerUserLimit() throws Exception {
        // Dedicated fixtures (high ids) so committed writes don't touch seed data.
        Long campaignId = 9001L;
        Campaign campaign = new Campaign();
        campaign.setId(campaignId);
        campaign.setName("Concurrency Test");
        campaign.setClientCode("TEST");
        campaign.setTotalStock(100);
        campaign.setRemainingStock(100);
        campaign.setActive(true);
        campaign.setPerUserLimit(2);
        campaignRepository.save(campaign);

        List<String> codes = List.of("CONC-0001", "CONC-0002", "CONC-0003", "CONC-0004", "CONC-0005");
        long voucherId = 9001L;
        for (String code : codes) {
            Voucher v = new Voucher();
            v.setId(voucherId++);
            v.setCampaignId(campaignId);
            v.setCode(code);
            v.setStatus("ACTIVE");
            voucherRepository.save(v);
        }

        String userId = "conc-user";
        ExecutorService pool = Executors.newFixedThreadPool(codes.size());
        CountDownLatch startGate = new CountDownLatch(1);
        List<Future<RedeemResponse>> futures = new ArrayList<>();
        for (String code : codes) {
            futures.add(pool.submit(() -> {
                startGate.await();
                try {
                    return voucherService.redeem(code, userId);
                } catch (Exception e) {
                    return RedeemResponse.fail("error: " + e.getMessage());
                }
            }));
        }

        startGate.countDown(); // release all threads at once
        pool.shutdown();
        pool.awaitTermination(10, TimeUnit.SECONDS);

        long succeeded = 0;
        for (Future<RedeemResponse> f : futures) {
            if ("OK".equals(f.get().getResult())) {
                succeeded++;
            }
        }

        // The per-user limit holds under concurrency: exactly two get through,
        // and exactly two redemption rows are committed.
        assertEquals(2, succeeded);
        assertEquals(2, redemptionRepository.countByCampaignIdAndUserId(campaignId, userId));
    }
}
