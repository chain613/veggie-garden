package com.veggie.controller;

import com.veggie.model.Inventory;
import com.veggie.service.InventoryService;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
public class InventoryController {
    private final InventoryService invService;

    public InventoryController(InventoryService invService) {
        this.invService = invService;
    }

    @GetMapping("/api/inventory")
    public List<Inventory> list(@RequestHeader("Authorization") String auth) {
        return invService.getUserInventory(extractUserId(auth));
    }

    @PostMapping("/api/inventory/use")
    public Map<String, Object> useItem(@RequestBody Map<String, Object> body,
                                       @RequestHeader("Authorization") String auth) {
        boolean ok = invService.useItem(extractUserId(auth),
            (Integer) body.get("itemId"), (Integer) body.get("quantity"));
        return Map.of("success", ok, "message", ok ? "使用成功" : "物资不足");
    }

    @PostMapping("/api/inventory/ad-reward")
    public Map<String, Object> adReward(@RequestHeader("Authorization") String auth) {
        return invService.adReward(extractUserId(auth));
    }

    @PostMapping("/api/ad/reward")
    public Map<String, Object> adRewardShort(@RequestHeader("Authorization") String auth) {
        return invService.adReward(extractUserId(auth));
    }

    private Long extractUserId(String auth) {
        try {
            return Long.parseLong(auth.replace("Bearer ", "").split(":")[0]);
        } catch (Exception e) {
            throw new RuntimeException("无效的认证令牌");
        }
    }
}
