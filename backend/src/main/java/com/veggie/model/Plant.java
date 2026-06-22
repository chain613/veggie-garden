package com.veggie.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "t_plant")
public class Plant {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "garden_id", nullable = false)
    private Long gardenId;

    @Column(name = "vegetable_id", nullable = false)
    private Integer vegetableId;

    @Column(name = "grid_x", nullable = false)
    private Integer gridX;

    @Column(name = "grid_y", nullable = false)
    private Integer gridY;

    @Column(name = "planted_at", nullable = false)
    private LocalDate plantedAt;

    @Column(name = "growth_stage", columnDefinition = "TINYINT")
    private Integer growthStage = 0;

    @Column(name = "health_score", precision = 3, scale = 2)
    private BigDecimal healthScore = new BigDecimal("1.00");

    @Column(name = "water_level", precision = 3, scale = 2)
    private BigDecimal waterLevel = new BigDecimal("0.50");

    @Column(name = "fertilizer_level", precision = 3, scale = 2)
    private BigDecimal fertilizerLevel = new BigDecimal("0.50");

    @Column(name = "root_mass", precision = 5, scale = 2)
    private BigDecimal rootMass = new BigDecimal("0.05");

    @Column(name = "leaf_mass", precision = 5, scale = 2)
    private BigDecimal leafMass = new BigDecimal("0.05");

    @Column(name = "stem_mass", precision = 5, scale = 2)
    private BigDecimal stemMass = new BigDecimal("0.02");

    @Column(name = "glucose", precision = 5, scale = 2)
    private BigDecimal glucose = BigDecimal.ZERO;

    @Column(name = "micro_index", precision = 3, scale = 2)
    private BigDecimal microIndex = new BigDecimal("0.80");

    @Column(name = "ventilation", precision = 3, scale = 2)
    private BigDecimal ventilation = new BigDecimal("1.00");

    @Column(name = "is_fruiting")
    private Boolean isFruiting = false;

    @Column(name = "bolting")
    private Boolean bolting = false;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getGardenId() { return gardenId; }
    public void setGardenId(Long gardenId) { this.gardenId = gardenId; }
    public Integer getVegetableId() { return vegetableId; }
    public void setVegetableId(Integer vegetableId) { this.vegetableId = vegetableId; }
    public Integer getGridX() { return gridX; }
    public void setGridX(Integer gridX) { this.gridX = gridX; }
    public Integer getGridY() { return gridY; }
    public void setGridY(Integer gridY) { this.gridY = gridY; }
    public LocalDate getPlantedAt() { return plantedAt; }
    public void setPlantedAt(LocalDate plantedAt) { this.plantedAt = plantedAt; }
    public Integer getGrowthStage() { return growthStage; }
    public void setGrowthStage(Integer growthStage) { this.growthStage = growthStage; }
    public BigDecimal getHealthScore() { return healthScore; }
    public void setHealthScore(BigDecimal healthScore) { this.healthScore = healthScore; }
    public BigDecimal getWaterLevel() { return waterLevel; }
    public void setWaterLevel(BigDecimal waterLevel) { this.waterLevel = waterLevel; }
    public BigDecimal getFertilizerLevel() { return fertilizerLevel; }
    public void setFertilizerLevel(BigDecimal fertilizerLevel) { this.fertilizerLevel = fertilizerLevel; }
    public Boolean getIsFruiting() { return isFruiting; }
    public void setIsFruiting(Boolean isFruiting) { this.isFruiting = isFruiting; }
    public Boolean getBolting() { return bolting; }
    public void setBolting(Boolean bolting) { this.bolting = bolting; }
    public BigDecimal getRootMass() { return rootMass; }
    public void setRootMass(BigDecimal rootMass) { this.rootMass = rootMass; }
    public BigDecimal getLeafMass() { return leafMass; }
    public void setLeafMass(BigDecimal leafMass) { this.leafMass = leafMass; }
    public BigDecimal getStemMass() { return stemMass; }
    public void setStemMass(BigDecimal stemMass) { this.stemMass = stemMass; }
    public BigDecimal getGlucose() { return glucose; }
    public void setGlucose(BigDecimal glucose) { this.glucose = glucose; }
    public BigDecimal getMicroIndex() { return microIndex; }
    public void setMicroIndex(BigDecimal microIndex) { this.microIndex = microIndex; }
    public BigDecimal getVentilation() { return ventilation; }
    public void setVentilation(BigDecimal ventilation) { this.ventilation = ventilation; }
}
