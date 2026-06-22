package com.veggie.controller;

import com.veggie.model.Item;
import com.veggie.repository.ItemRepository;
import com.veggie.service.InventoryService;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/shop")
public class ShopController {
    private final ItemRepository itemRepo;
    private final InventoryService invService;

    public ShopController(ItemRepository itemRepo, InventoryService invService) {
        this.itemRepo = itemRepo;
        this.invService = invService;
    }

    @GetMapping("/items")
    public List<Item> listItems() {
        return itemRepo.findAll();
    }

    @PostMapping("/buy")
    public Map<String, Object> buy(@RequestBody Map<String, Object> body,
                                   @RequestHeader("Authorization") String auth) {
        Long userId = Long.parseLong(auth.replace("Bearer ", "").split(":")[0]);
        Integer itemId = (Integer) body.get("itemId");
        int qty = body.containsKey("quantity") ? (Integer) body.get("quantity") : 1;

        invService.addItem(userId, itemId, qty);
        return Map.of("success", true, "message", "购买成功");
    }
}
