package com.veggie.model;

import jakarta.persistence.*;

@Entity
@Table(name = "t_vegetable")
public class Vegetable {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(name = "growth_days", nullable = false)
    private Integer growthDays;

    @Column(nullable = false, columnDefinition = "SET('春','夏','秋','冬')")
    private String season;

    @Column(name = "water_need")
    private Integer waterNeed;

    @Column(name = "sun_need")
    private Integer sunNeed;

    @Column(name = "nutrient_need")
    private Integer nutrientNeed;

    @Column(name = "height_level")
    private Integer heightLevel;

    @Column(name = "is_vine")
    private Boolean isVine = false;

    @Column(name = "need_frame")
    private Boolean needFrame = false;

    @Column(name = "need_topping")
    private Boolean needTopping = false;

    @Column(name = "topping_forbidden")
    private Boolean toppingForbidden = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "harvest_type", nullable = false)
    private HarvestType harvestType = HarvestType.whole;

    @Column(name = "fruit_interval_days")
    private Integer fruitIntervalDays;

    @Column(name = "fruit_mature_days")
    private Integer fruitMatureDays;

    @Column(name = "fruit_overripe_days")
    private Integer fruitOverripeDays;

    public enum HarvestType { whole, continuous }

    public Integer getId() { return id; }
    public String getName() { return name; }
    public Integer getGrowthDays() { return growthDays; }
    public String getSeason() { return season; }
    public Integer getWaterNeed() { return waterNeed; }
    public Integer getSunNeed() { return sunNeed; }
    public Integer getNutrientNeed() { return nutrientNeed; }
    public Integer getHeightLevel() { return heightLevel; }
    public Boolean getIsVine() { return isVine; }
    public Boolean getNeedFrame() { return needFrame; }
    public Boolean getNeedTopping() { return needTopping; }
    public Boolean getToppingForbidden() { return toppingForbidden; }
    public HarvestType getHarvestType() { return harvestType; }
    public Integer getFruitIntervalDays() { return fruitIntervalDays; }
    public Integer getFruitMatureDays() { return fruitMatureDays; }
    public Integer getFruitOverripeDays() { return fruitOverripeDays; }
}
