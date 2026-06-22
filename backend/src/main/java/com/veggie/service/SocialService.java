package com.veggie.service;

import com.veggie.model.*;
import com.veggie.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Service
public class SocialService {
    private final FriendRepository friendRepo;
    private final VisitRepository visitRepo;
    private final UserRepository userRepo;
    private final GardenRepository gardenRepo;
    private final PlantRepository plantRepo;
    private final InventoryService invService;

    public SocialService(FriendRepository friendRepo, VisitRepository visitRepo,
                         UserRepository userRepo, GardenRepository gardenRepo,
                         PlantRepository plantRepo, InventoryService invService) {
        this.friendRepo = friendRepo;
        this.visitRepo = visitRepo;
        this.userRepo = userRepo;
        this.gardenRepo = gardenRepo;
        this.plantRepo = plantRepo;
        this.invService = invService;
    }

    @Transactional
    public Map<String, Object> addFriend(Long userId, String phone) {
        User target = userRepo.findByPhone(phone)
            .orElseThrow(() -> new RuntimeException("该手机号未注册"));

        if (userId.equals(target.getId())) {
            throw new RuntimeException("不能添加自己为好友");
        }

        if (friendRepo.existsByUserIdAndFriendId(userId, target.getId())) {
            throw new RuntimeException("已经是好友了");
        }

        // 双向添加
        Friend f1 = new Friend();
        f1.setUserId(userId);
        f1.setFriendId(target.getId());
        friendRepo.save(f1);

        Friend f2 = new Friend();
        f2.setUserId(target.getId());
        f2.setFriendId(userId);
        friendRepo.save(f2);

        return Map.of("success", true, "friendId", target.getId(),
            "friendNickname", target.getNickname());
    }

    public List<Map<String, Object>> getFriends(Long userId) {
        List<Friend> friends = friendRepo.findByUserId(userId);
        List<Map<String, Object>> result = new ArrayList<>();
        for (Friend f : friends) {
            User friendUser = userRepo.findById(f.getFriendId()).orElse(null);
            if (friendUser == null) continue;
            result.add(Map.of(
                "id", f.getId(),
                "friendId", friendUser.getId(),
                "nickname", friendUser.getNickname(),
                "phone", friendUser.getPhone(),
                "createdAt", f.getCreatedAt()
            ));
        }
        return result;
    }

    @Transactional
    public void removeFriend(Long userId, Long friendId) {
        friendRepo.deleteByUserIdAndFriendId(userId, friendId);
        friendRepo.deleteByUserIdAndFriendId(friendId, userId);
    }

    public Map<String, Object> visitGarden(Long visitorId, Long gardenId) {
        Garden garden = gardenRepo.findById(gardenId)
            .orElseThrow(() -> new RuntimeException("菜园不存在"));

        User owner = userRepo.findById(garden.getUserId()).orElse(null);
        List<Plant> plants = plantRepo.findByGardenId(gardenId);

        // 记录访问
        Visit visit = new Visit();
        visit.setVisitorId(visitorId);
        visit.setGardenId(gardenId);
        visitRepo.save(visit);

        return Map.of(
            "garden", garden,
            "owner", owner != null ? Map.of("id", owner.getId(),
                "nickname", owner.getNickname()) : null,
            "plants", plants,
            "plantCount", plants.size()
        );
    }

    public Map<String, Object> sendGift(Long fromUserId, Long toUserId,
                                         Integer itemId, int quantity) {
        boolean ok = invService.useItem(fromUserId, itemId, quantity);
        if (!ok) {
            throw new RuntimeException("背包物资不足");
        }
        invService.addItem(toUserId, itemId, quantity);
        return Map.of("success", true,
            "message", "礼物已送出",
            "toUserId", toUserId,
            "itemId", itemId,
            "quantity", quantity);
    }
}
