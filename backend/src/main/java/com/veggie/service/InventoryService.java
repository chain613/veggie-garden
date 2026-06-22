package com.veggie.service;

import com.veggie.model.Inventory;
import com.veggie.model.Item;
import com.veggie.repository.*;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class InventoryService {
    private final InventoryRepository invRepo;
    private final ItemRepository itemRepo;

    public InventoryService(InventoryRepository invRepo, ItemRepository itemRepo) {
        this.invRepo = invRepo;
        this.itemRepo = itemRepo;
    }

    public List<Inventory> getUserInventory(Long userId) {
        return invRepo.findByUserId(userId);
    }

    public Inventory addItem(Long userId, Integer itemId, int quantity) {
        Inventory inv = invRepo.findByUserIdAndItemId(userId, itemId)
            .orElseGet(() -> {
                Inventory i = new Inventory();
                i.setUserId(userId);
                i.setItemId(itemId);
                i.setQuantity(0);
                return i;
            });
        inv.setQuantity(inv.getQuantity() + quantity);
        return invRepo.save(inv);
    }

    public boolean useItem(Long userId, Integer itemId, int quantity) {
        Inventory inv = invRepo.findByUserIdAndItemId(userId, itemId).orElse(null);
        if (inv == null || inv.getQuantity() < quantity) return false;
        inv.setQuantity(inv.getQuantity() - quantity);
        invRepo.save(inv);
        return true;
    }

    public Map<String, Object> adReward(Long userId) {
        List<Item> allItems = itemRepo.findAll();
        Item randomItem = allItems.get(new Random().nextInt(allItems.size()));
        int qty = randomItem.getType() == Item.ItemType.seed ? 3 : 1;
        addItem(userId, randomItem.getId(), qty);
        return Map.of("itemId", randomItem.getId(),
            "itemName", randomItem.getName(),
            "type", randomItem.getType().name(),
            "quantity", qty);
    }

    public void giveNewPlayerGift(Long userId) {
        addItem(userId, 1, 5);  // 油菜种子×5
        addItem(userId, 11, 1); // 水壶
        addItem(userId, 13, 2); // 有机肥×2
    }
}
