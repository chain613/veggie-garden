package com.veggie.controller;

import com.veggie.model.Plant;
import com.veggie.model.PlantFruit;
import com.veggie.service.GrowthTickService;
import com.veggie.service.PlantService;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/plant")
public class PlantController {
    private final PlantService plantService;
    private final GrowthTickService growthTickService;

    public PlantController(PlantService plantService, GrowthTickService growthTickService) {
        this.plantService = plantService;
        this.growthTickService = growthTickService;
    }

    @PostMapping("/plant")
    public Plant plant(@RequestBody Map<String, Object> body) {
        return plantService.plantSeed(
            ((Number) body.get("gardenId")).longValue(),
            (Integer) body.get("vegetableId"),
            (Integer) body.get("gridX"),
            (Integer) body.get("gridY")
        );
    }

    @GetMapping("/garden/{gardenId}")
    public List<Plant> list(@PathVariable Long gardenId) {
        return plantService.getGardenPlants(gardenId);
    }

    @PostMapping("/{id}/water")
    public Plant water(@PathVariable Long id) {
        return plantService.water(id);
    }

    @PostMapping("/{id}/fertilize")
    public Plant fertilize(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return plantService.fertilize(id, body.get("type"));
    }

    @PostMapping("/fruit/{fruitId}/harvest")
    public PlantFruit harvestFruit(@PathVariable Long fruitId) {
        return plantService.harvestFruit(fruitId, null, null);
    }

    @PostMapping("/{id}/harvest")
    public Plant harvestWhole(@PathVariable Long id) {
        return plantService.harvestWhole(id);
    }

    @GetMapping("/{id}/fruits")
    public List<PlantFruit> fruits(@PathVariable Long id) {
        return plantService.getPlantFruits(id);
    }

    @PostMapping("/tick")
    public String manualTick() {
        growthTickService.dailyTick();
        return "tick completed";
    }
}
