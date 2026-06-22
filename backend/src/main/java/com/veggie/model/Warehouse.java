package com.veggie.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "t_warehouse")
public class Warehouse {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "vegetable_id", nullable = false)
    private Integer vegetableId;

    @Column(nullable = false)
    private Integer quantity = 0;

    @Column(name = "quality_avg", precision = 3, scale = 2)
    private BigDecimal qualityAvg = BigDecimal.ZERO;

    @Column(name = "harvested_at")
    private LocalDateTime harvestedAt = LocalDateTime.now();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Integer getVegetableId() { return vegetableId; }
    public void setVegetableId(Integer vegetableId) { this.vegetableId = vegetableId; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public BigDecimal getQualityAvg() { return qualityAvg; }
    public void setQualityAvg(BigDecimal qualityAvg) { this.qualityAvg = qualityAvg; }
    public LocalDateTime getHarvestedAt() { return harvestedAt; }
    public void setHarvestedAt(LocalDateTime harvestedAt) { this.harvestedAt = harvestedAt; }
}
