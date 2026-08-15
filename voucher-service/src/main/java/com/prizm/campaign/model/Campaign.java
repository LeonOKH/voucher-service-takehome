package com.prizm.campaign.model;

import jakarta.persistence.*;

@Entity
@Table(name = "campaign")
public class Campaign {

    @Id
    private Long id;

    private String name;

    @Column(name = "client_code")
    private String clientCode;

    @Column(name = "total_stock")
    private int totalStock;

    @Column(name = "remaining_stock")
    private int remainingStock;

    private boolean active;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getClientCode() { return clientCode; }
    public void setClientCode(String clientCode) { this.clientCode = clientCode; }
    public int getTotalStock() { return totalStock; }
    public void setTotalStock(int totalStock) { this.totalStock = totalStock; }
    public int getRemainingStock() { return remainingStock; }
    public void setRemainingStock(int remainingStock) { this.remainingStock = remainingStock; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
