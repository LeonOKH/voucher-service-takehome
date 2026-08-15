package com.prizm.campaign.repository;

import com.prizm.campaign.model.Campaign;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CampaignRepository extends JpaRepository<Campaign, Long> {

    List<Campaign> findByClientCode(String clientCode);
}
