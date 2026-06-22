package com.veggie.service;

import com.veggie.model.*;
import com.veggie.repository.*;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Service
public class WarehouseService {
    private final WarehouseRepository whRepo;
    private final PlantFruitRepository fruitRepo;
    private final PlantRepository plantRepo;
    private final VegetableRepository vegRepo;

    public WarehouseService(WarehouseRepository whRepo, PlantFruitRepository fruitRepo,
                            PlantRepository plantRepo, VegetableRepository vegRepo) {
        this.whRepo = whRepo;
        this.fruitRepo = fruitRepo;
        this.plantRepo = plantRepo;
        this.vegRepo = vegRepo;
    }

    public Warehouse harvestFruitToWarehouse(Long fruitId, Long userId) {
        PlantFruit fruit = fruitRepo.findById(fruitId).orElseThrow();
        Plant plant = plantRepo.findById(fruit.getPlantId()).orElseThrow();

        BigDecimal quality = fruit.getQualityScore();
        BigDecimal health = plant.getHealthScore();
        BigDecimal value = calculateValue(plant.getVegetableId(), quality, health);

        Warehouse wh = whRepo.findByUserIdAndVegetableId(userId, plant.getVegetableId())
            .orElseGet(() -> {
                Warehouse w = new Warehouse();
                w.setUserId(userId);
                w.setVegetableId(plant.getVegetableId());
                w.setQuantity(0);
                w.setQualityAvg(BigDecimal.ZERO);
                return w;
            });

        BigDecimal newAvg = wh.getQualityAvg()
            .multiply(new BigDecimal(wh.getQuantity()))
            .add(quality)
            .divide(new BigDecimal(wh.getQuantity() + 1), 2, RoundingMode.HALF_UP);
        wh.setQualityAvg(newAvg);
        wh.setQuantity(wh.getQuantity() + 1);
        return whRepo.save(wh);
    }

    public BigDecimal calculateValue(Integer vegetableId, BigDecimal quality, BigDecimal health) {
        BigDecimal basePrice = getBasePrice(vegetableId);
        BigDecimal qualityCoeff = quality.add(new BigDecimal("0.5"));
        BigDecimal healthCoeff = health.multiply(new BigDecimal("0.5")).add(new BigDecimal("0.5"));
        return basePrice.multiply(qualityCoeff).multiply(healthCoeff).setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal getBasePrice(Integer vegetableId) {
        return switch (vegetableId) {
            case 1 -> new BigDecimal("3.0");
            case 2 -> new BigDecimal("2.5");
            case 3 -> new BigDecimal("5.0");
            case 4 -> new BigDecimal("4.0");
            case 5 -> new BigDecimal("2.0");
            case 6 -> new BigDecimal("4.5");
            case 7 -> new BigDecimal("3.5");
            case 8 -> new BigDecimal("1.5");
            case 9 -> new BigDecimal("6.0");
            case 10 -> new BigDecimal("3.0");
            default -> new BigDecimal("3.0");
        };
    }

    public List<Warehouse> getUserWarehouse(Long userId) {
        return whRepo.findByUserId(userId);
    }

    public Map<String, Object> getWarehouseSummary(Long userId) {
        List<Warehouse> items = whRepo.findByUserId(userId);
        BigDecimal totalValue = BigDecimal.ZERO;
        int totalQuantity = 0;
        for (Warehouse wh : items) {
            totalQuantity += wh.getQuantity();
            Vegetable veg = vegRepo.findById(wh.getVegetableId()).orElse(null);
            if (veg != null) {
                totalValue = totalValue.add(
                    calculateValue(wh.getVegetableId(), wh.getQualityAvg(),
                        new BigDecimal("1.00")).multiply(new BigDecimal(wh.getQuantity())));
            }
        }
        return Map.of("totalQuantity", totalQuantity, "totalValue", totalValue,
            "itemCount", items.size());
    }
}
