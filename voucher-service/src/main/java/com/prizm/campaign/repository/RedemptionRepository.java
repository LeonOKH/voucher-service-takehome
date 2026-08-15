package com.prizm.campaign.repository;

import com.prizm.campaign.model.Redemption;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RedemptionRepository extends JpaRepository<Redemption, Long> {

    List<Redemption> findByVoucherId(Long voucherId);

    List<Redemption> findByCampaignId(Long campaignId);

    long countByCampaignIdAndUserId(Long campaignId, String userId);
}
