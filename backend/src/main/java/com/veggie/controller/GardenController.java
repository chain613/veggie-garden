package com.veggie.controller;

import com.veggie.model.Garden;
import com.veggie.model.Plant;
import com.veggie.service.GardenService;
import com.veggie.service.PlantService;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/garden")
public class GardenController {
    private final GardenService gardenService;
    private final PlantService plantService;

    public GardenController(GardenService gardenService, PlantService plantService) {
        this.gardenService = gardenService;
        this.plantService = plantService;
    }

    @GetMapping
    public List<Garden> list(@RequestHeader("Authorization") String auth) {
        return gardenService.getUserGardens(extractUserId(auth));
    }

    @GetMapping("/{id}")
    public Garden get(@PathVariable Long id) {
        return gardenService.getById(id);
    }

    @GetMapping("/plants")
    public List<Plant> plants(@RequestHeader("Authorization") String auth) {
        Long userId = extractUserId(auth);
        List<Garden> gardens = gardenService.getUserGardens(userId);
        if (gardens.isEmpty()) return List.of();
        return plantService.getGardenPlants(gardens.get(0).getId());
    }

    @PostMapping("/plant")
    public Plant plant(@RequestBody Map<String, Object> body,
                       @RequestHeader("Authorization") String auth) {
        Long userId = extractUserId(auth);
        List<Garden> gardens = gardenService.getUserGardens(userId);
        Long gardenId = gardens.get(0).getId();
        return plantService.plantSeed(
            gardenId,
            (Integer) body.get("seedId"),
            (Integer) body.get("gridX"),
            (Integer) body.get("gridZ")
        );
    }

    @PostMapping("/water")
    public Plant water(@RequestBody Map<String, Object> body) {
        return plantService.water(((Number) body.get("plantId")).longValue());
    }

    @PostMapping("/fertilize")
    public Plant fertilize(@RequestBody Map<String, Object> body) {
        return plantService.fertilize(
            ((Number) body.get("plantId")).longValue(),
            body.get("type") != null ? body.get("type").toString() : null
        );
    }

    @PostMapping("/harvest")
    public Plant harvest(@RequestBody Map<String, Object> body) {
        return plantService.harvestWhole(((Number) body.get("plantId")).longValue());
    }

    private Long extractUserId(String auth) {
        return Long.parseLong(auth.replace("Bearer ", "").split(":")[0]);
    }
}
