package com.veggie.service;

import com.veggie.model.Garden;
import com.veggie.model.Garden.Region;
import com.veggie.repository.GardenRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class GardenService {
    private final GardenRepository gardenRepo;

    public GardenService(GardenRepository gardenRepo) {
        this.gardenRepo = gardenRepo;
    }

    public List<Garden> getUserGardens(Long userId) {
        List<Garden> gardens = gardenRepo.findByUserId(userId);
        if (gardens.isEmpty()) {
            gardens = List.of(
                createGarden(userId, Region.zhongyuan),
                createGarden(userId, Region.zhongnan)
            );
        }
        return gardens;
    }

    private Garden createGarden(Long userId, Region region) {
        Garden g = new Garden();
        g.setUserId(userId);
        g.setRegion(region);
        return gardenRepo.save(g);
    }

    public Garden getById(Long gardenId) {
        return gardenRepo.findById(gardenId).orElse(null);
    }
}
