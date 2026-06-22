package com.veggie.controller;

import com.veggie.model.Knowledge;
import com.veggie.model.NpcDialog;
import com.veggie.service.KnowledgeService;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
public class KnowledgeController {
    private final KnowledgeService knowledgeService;

    public KnowledgeController(KnowledgeService knowledgeService) {
        this.knowledgeService = knowledgeService;
    }

    @GetMapping("/api/knowledge")
    public List<Knowledge> all() {
        return knowledgeService.getAll();
    }

    @GetMapping("/api/knowledge/unlocked")
    public List<Map<String, Object>> unlocked(@RequestHeader("Authorization") String auth) {
        return knowledgeService.getUnlocked(extractUserId(auth));
    }

    @GetMapping("/api/knowledge/dialog")
    public List<NpcDialog> dialog(@RequestParam(required = false) String event,
                                  @RequestParam(required = false) Integer vegetableId,
                                  @RequestParam(required = false) String season) {
        return knowledgeService.getDialog(event, vegetableId, season);
    }

    @PostMapping("/api/knowledge/unlock/{id}")
    public Map<String, Object> unlock(@PathVariable Integer id,
                                      @RequestHeader("Authorization") String auth) {
        return knowledgeService.unlock(extractUserId(auth), id);
    }

    private Long extractUserId(String auth) {
        try {
            return Long.parseLong(auth.replace("Bearer ", "").split(":")[0]);
        } catch (Exception e) {
            throw new RuntimeException("无效的认证令牌");
        }
    }
}
