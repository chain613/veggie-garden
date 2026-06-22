package com.veggie.service;

import com.veggie.model.*;
import com.veggie.repository.*;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class PlantService {
    private final PlantRepository plantRepo;
    private final VegetableRepository vegRepo;
    private final PlantFruitRepository fruitRepo;

    public PlantService(PlantRepository plantRepo, VegetableRepository vegRepo,
                        PlantFruitRepository fruitRepo) {
        this.plantRepo = plantRepo;
        this.vegRepo = vegRepo;
        this.fruitRepo = fruitRepo;
    }

    public Plant plantSeed(Long gardenId, Integer vegetableId, Integer gridX, Integer gridY) {
        if (plantRepo.existsByGardenIdAndGridXAndGridY(gardenId, gridX, gridY)) {
            throw new RuntimeException("该格子已经种了东西");
        }
        Vegetable veg = vegRepo.findById(vegetableId)
            .orElseThrow(() -> new RuntimeException("蔬菜品种不存在"));

        Plant plant = new Plant();
        plant.setGardenId(gardenId);
        plant.setVegetableId(vegetableId);
        plant.setGridX(gridX);
        plant.setGridY(gridY);
        plant.setPlantedAt(LocalDate.now());
        return plantRepo.save(plant);
    }

    public List<Plant> getGardenPlants(Long gardenId) {
        return plantRepo.findByGardenId(gardenId);
    }

    public Plant water(Long plantId) {
        Plant p = plantRepo.findById(plantId).orElseThrow();
        p.setWaterLevel(new BigDecimal("1.00"));
        return plantRepo.save(p);
    }

    public Plant fertilize(Long plantId, String itemType) {
        Plant p = plantRepo.findById(plantId).orElseThrow();
        p.setFertilizerLevel(new BigDecimal("1.00"));
        return plantRepo.save(p);
    }

    public PlantFruit harvestFruit(Long fruitId, Long userId, Long warehouseService) {
        PlantFruit f = fruitRepo.findById(fruitId).orElseThrow();
        if (f.getHarvestedAt() != null) {
            throw new RuntimeException("该果实已采收");
        }
        f.setHarvestedAt(LocalDate.now());
        fruitRepo.save(f);
        return f;
    }

    public Plant harvestWhole(Long plantId) {
        Plant p = plantRepo.findById(plantId).orElseThrow();
        Vegetable veg = vegRepo.findById(p.getVegetableId()).orElseThrow();
        if (veg.getHarvestType() != Vegetable.HarvestType.whole) {
            throw new RuntimeException("该蔬菜为连续采收型，不能整株采收");
        }
        plantRepo.delete(p);
        return p;
    }

    public List<PlantFruit> getPlantFruits(Long plantId) {
        return fruitRepo.findByPlantId(plantId);
    }
}
