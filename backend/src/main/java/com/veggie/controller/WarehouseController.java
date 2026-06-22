package com.veggie.controller;

import com.veggie.model.Warehouse;
import com.veggie.service.WarehouseService;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/warehouse")
public class WarehouseController {
    private final WarehouseService whService;

    public WarehouseController(WarehouseService whService) {
        this.whService = whService;
    }

    @GetMapping
    public List<Warehouse> list(@RequestHeader("Authorization") String auth) {
        return whService.getUserWarehouse(extractUserId(auth));
    }

    @GetMapping("/summary")
    public Map<String, Object> summary(@RequestHeader("Authorization") String auth) {
        return whService.getWarehouseSummary(extractUserId(auth));
    }

    @PostMapping("/harvest-fruit/{fruitId}")
    public Warehouse harvestFruit(@PathVariable Long fruitId,
                                  @RequestHeader("Authorization") String auth) {
        return whService.harvestFruitToWarehouse(fruitId, extractUserId(auth));
    }

    private Long extractUserId(String auth) {
        try {
            return Long.parseLong(auth.replace("Bearer ", "").split(":")[0]);
        } catch (Exception e) {
            throw new RuntimeException("无效的认证令牌");
        }
    }
}
