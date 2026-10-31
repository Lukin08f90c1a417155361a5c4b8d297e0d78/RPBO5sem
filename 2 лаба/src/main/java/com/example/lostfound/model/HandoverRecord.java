package com.example.lostfound.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class HandoverRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "claim_id")
    private ItemClaim claim;

    private LocalDateTime handoverDate;
    private boolean confirmed;

    // Геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public ItemClaim getClaim() { return claim; }
    public void setClaim(ItemClaim claim) { this.claim = claim; }
    public LocalDateTime getHandoverDate() { return handoverDate; }
    public void setHandoverDate(LocalDateTime handoverDate) { this.handoverDate = handoverDate; }
    public boolean isConfirmed() { return confirmed; }
    public void setConfirmed(boolean confirmed) { this.confirmed = confirmed; }
}