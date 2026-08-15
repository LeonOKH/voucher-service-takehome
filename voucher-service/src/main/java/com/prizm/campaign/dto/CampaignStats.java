package com.prizm.campaign.dto;

public class CampaignStats {

    private Long campaignId;
    private String name;
    private int totalStock;
    private int remainingStock;
    private int redeemedCount;
    private int activeVoucherCount;

    public Long getCampaignId() { return campaignId; }
    public void setCampaignId(Long campaignId) { this.campaignId = campaignId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getTotalStock() { return totalStock; }
    public void setTotalStock(int totalStock) { this.totalStock = totalStock; }
    public int getRemainingStock() { return remainingStock; }
    public void setRemainingStock(int remainingStock) { this.remainingStock = remainingStock; }
    public int getRedeemedCount() { return redeemedCount; }
    public void setRedeemedCount(int redeemedCount) { this.redeemedCount = redeemedCount; }
    public int getActiveVoucherCount() { return activeVoucherCount; }
    public void setActiveVoucherCount(int activeVoucherCount) { this.activeVoucherCount = activeVoucherCount; }
}
