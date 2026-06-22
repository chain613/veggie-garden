package com.veggie.controller;

import com.veggie.service.SocialService;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/social")
public class SocialController {
    private final SocialService socialService;

    public SocialController(SocialService socialService) {
        this.socialService = socialService;
    }

    @PostMapping("/friends")
    public Map<String, Object> addFriend(@RequestBody Map<String, String> body,
                                         @RequestHeader("Authorization") String auth) {
        Long userId = extractUserId(auth);
        return socialService.addFriend(userId, body.get("phone"));
    }

    @GetMapping("/friends")
    public Object listFriends(@RequestHeader("Authorization") String auth) {
        return socialService.getFriends(extractUserId(auth));
    }

    @DeleteMapping("/friends/{id}")
    public Map<String, Object> removeFriend(@PathVariable Long id,
                                            @RequestHeader("Authorization") String auth) {
        socialService.removeFriend(extractUserId(auth), id);
        return Map.of("success", true);
    }

    @GetMapping("/visit/{gardenId}")
    public Map<String, Object> visitGarden(@PathVariable Long gardenId,
                                           @RequestHeader("Authorization") String auth) {
        return socialService.visitGarden(extractUserId(auth), gardenId);
    }

    @PostMapping("/gift")
    public Map<String, Object> sendGift(@RequestBody Map<String, Object> body,
                                        @RequestHeader("Authorization") String auth) {
        Long fromUserId = extractUserId(auth);
        Long toUserId = ((Number) body.get("toUserId")).longValue();
        Integer itemId = (Integer) body.get("itemId");
        int quantity = body.containsKey("quantity") ? (Integer) body.get("quantity") : 1;
        return socialService.sendGift(fromUserId, toUserId, itemId, quantity);
    }

    private Long extractUserId(String auth) {
        try {
            return Long.parseLong(auth.replace("Bearer ", "").split(":")[0]);
        } catch (Exception e) {
            throw new RuntimeException("无效的认证令牌");
        }
    }
}
