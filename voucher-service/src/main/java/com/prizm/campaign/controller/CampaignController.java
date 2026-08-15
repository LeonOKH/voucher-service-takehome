package com.prizm.campaign.controller;

import com.prizm.campaign.dto.CampaignStats;
import com.prizm.campaign.service.CampaignStatsService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/campaigns")
public class CampaignController {

    private final CampaignStatsService campaignStatsService;

    public CampaignController(CampaignStatsService campaignStatsService) {
        this.campaignStatsService = campaignStatsService;
    }

    @GetMapping("/{id}/stats")
    public CampaignStats stats(@PathVariable Long id) {
        return campaignStatsService.getStats(id);
    }

    @GetMapping("/by-client/{clientCode}/stats")
    public List<CampaignStats> statsByClient(@PathVariable String clientCode) {
        return campaignStatsService.getStatsForClient(clientCode);
    }
}
