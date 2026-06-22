package com.veggie.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "t_plant_fruit")
public class PlantFruit {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "plant_id", nullable = false)
    private Long plantId;

    @Column(name = "appeared_at", nullable = false)
    private LocalDate appearedAt;

    @Column(name = "mature_at", nullable = false)
    private LocalDate matureAt;

    @Column(name = "overripe_at", nullable = false)
    private LocalDate overripeAt;

    @Column(name = "harvested_at")
    private LocalDate harvestedAt;

    @Column(name = "quality_score", precision = 3, scale = 2)
    private BigDecimal qualityScore = new BigDecimal("1.00");

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getPlantId() { return plantId; }
    public void setPlantId(Long plantId) { this.plantId = plantId; }
    public LocalDate getAppearedAt() { return appearedAt; }
    public void setAppearedAt(LocalDate appearedAt) { this.appearedAt = appearedAt; }
    public LocalDate getMatureAt() { return matureAt; }
    public void setMatureAt(LocalDate matureAt) { this.matureAt = matureAt; }
    public LocalDate getOverripeAt() { return overripeAt; }
    public void setOverripeAt(LocalDate overripeAt) { this.overripeAt = overripeAt; }
    public LocalDate getHarvestedAt() { return harvestedAt; }
    public void setHarvestedAt(LocalDate harvestedAt) { this.harvestedAt = harvestedAt; }
    public BigDecimal getQualityScore() { return qualityScore; }
    public void setQualityScore(BigDecimal qualityScore) { this.qualityScore = qualityScore; }
}
