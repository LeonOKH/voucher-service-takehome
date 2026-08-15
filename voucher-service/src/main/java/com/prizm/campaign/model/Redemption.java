package com.prizm.campaign.model;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "redemption")
public class Redemption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "voucher_id")
    private Long voucherId;

    @Column(name = "campaign_id")
    private Long campaignId;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "created_at")
    private Date createdAt;

    public Redemption() {}

    public Redemption(Long voucherId, Long campaignId, String userId, Date createdAt) {
        this.voucherId = voucherId;
        this.campaignId = campaignId;
        this.userId = userId;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public Long getVoucherId() { return voucherId; }
    public Long getCampaignId() { return campaignId; }
    public String getUserId() { return userId; }
    public Date getCreatedAt() { return createdAt; }
}
