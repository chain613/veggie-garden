package com.veggie.service;

import com.veggie.model.*;
import com.veggie.repository.*;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class KnowledgeService {
    private final KnowledgeRepository knowledgeRepo;
    private final NpcDialogRepository dialogRepo;
    private final PlayerKnowledgeRepository playerKnowledgeRepo;

    public KnowledgeService(KnowledgeRepository knowledgeRepo,
                            NpcDialogRepository dialogRepo,
                            PlayerKnowledgeRepository playerKnowledgeRepo) {
        this.knowledgeRepo = knowledgeRepo;
        this.dialogRepo = dialogRepo;
        this.playerKnowledgeRepo = playerKnowledgeRepo;
    }

    public List<Knowledge> getAll() {
        return knowledgeRepo.findAll();
    }

    public List<Map<String, Object>> getUnlocked(Long userId) {
        List<PlayerKnowledge> unlocked = playerKnowledgeRepo.findByUserId(userId);
        Set<Integer> unlockedIds = unlocked.stream()
            .map(PlayerKnowledge::getKnowledgeId).collect(Collectors.toSet());
        return knowledgeRepo.findAll().stream().map(k -> {
            Map<String, Object> m = new HashMap<>();
            m.put("id", k.getId());
            m.put("title", k.getTitle());
            m.put("category", k.getCategory());
            m.put("vegetableId", k.getVegetableId());
            m.put("unlocked", unlockedIds.contains(k.getId()));
            return m;
        }).collect(Collectors.toList());
    }

    public List<NpcDialog> getDialog(String event, Integer vegetableId, String season) {
        if (vegetableId != null && season != null) {
            return dialogRepo.findByTriggerEventAndVegetableIdAndSeason(event, vegetableId, season);
        }
        return dialogRepo.findByTriggerEvent(event);
    }

    public Map<String, Object> unlock(Long userId, Integer knowledgeId) {
        if (playerKnowledgeRepo.existsByUserIdAndKnowledgeId(userId, knowledgeId)) {
            return Map.of("success", false, "message", "已解锁");
        }
        PlayerKnowledge pk = new PlayerKnowledge();
        pk.setUserId(userId);
        pk.setKnowledgeId(knowledgeId);
        playerKnowledgeRepo.save(pk);
        return Map.of("success", true, "message", "解锁成功");
    }
}
