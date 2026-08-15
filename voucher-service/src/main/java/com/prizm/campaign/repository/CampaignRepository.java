package com.prizm.campaign.repository;

import com.prizm.campaign.model.Campaign;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CampaignRepository extends JpaRepository<Campaign, Long> {

    List<Campaign> findByClientCode(String clientCode);

    /**
     * Loads a campaign with a pessimistic write lock so concurrent redemptions
     * for the same campaign serialize. This is what makes the stock decrement
     * and the per-user limit check race-safe within a single shared database.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from Campaign c where c.id = :id")
    Optional<Campaign> findByIdForUpdate(@Param("id") Long id);
}
