package com.veggie.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "t_weed")
public class Weed {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "garden_id", nullable = false)
    private Long gardenId;

    @Column(name = "grid_x", nullable = false)
    private Integer gridX;

    @Column(name = "grid_y", nullable = false)
    private Integer gridY;

    @Column(name = "growth_stage", columnDefinition = "TINYINT")
    private Integer growthStage = 0;

    @Column(name = "appeared_at", nullable = false)
    private LocalDate appearedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getGardenId() { return gardenId; }
    public void setGardenId(Long gardenId) { this.gardenId = gardenId; }
    public Integer getGridX() { return gridX; }
    public void setGridX(Integer gridX) { this.gridX = gridX; }
    public Integer getGridY() { return gridY; }
    public void setGridY(Integer gridY) { this.gridY = gridY; }
    public Integer getGrowthStage() { return growthStage; }
    public void setGrowthStage(Integer growthStage) { this.growthStage = growthStage; }
    public LocalDate getAppearedAt() { return appearedAt; }
    public void setAppearedAt(LocalDate appearedAt) { this.appearedAt = appearedAt; }
}
