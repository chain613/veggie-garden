package com.veggie.controller;

import com.veggie.model.Weed;
import com.veggie.repository.WeedRepository;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/weed")
public class WeedController {
    private final WeedRepository weedRepo;

    public WeedController(WeedRepository weedRepo) {
        this.weedRepo = weedRepo;
    }

    @GetMapping("/garden/{gardenId}")
    public List<Weed> list(@PathVariable Long gardenId) {
        return weedRepo.findByGardenId(gardenId);
    }

    @PostMapping("/removeBatch")
    public Map<String, Object> removeBatch(@RequestBody Map<String, Object> body) {
        @SuppressWarnings("unchecked")
        List<Integer> ids = (List<Integer>) body.get("weedIds");
        for (int id : ids) weedRepo.deleteById((long) id);
        return Map.of("success", true, "removed", ids.size());
    }

    @GetMapping("/garden/{gardenId}/check")
    public Map<String, Object> checkDensity(@PathVariable Long gardenId) {
        int count = weedRepo.countByGardenId(gardenId);
        return Map.of("weedCount", count, "alert", count >= 30);
    }
}
