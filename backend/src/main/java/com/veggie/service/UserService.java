package com.veggie.service;

import com.veggie.model.User;
import com.veggie.repository.UserRepository;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {
    private final UserRepository userRepo;
    private final InventoryService invService;

    public UserService(UserRepository userRepo, InventoryService invService) {
        this.userRepo = userRepo;
        this.invService = invService;
    }

    public User loginOrRegister(String phone) {
        return userRepo.findByPhone(phone)
            .orElseGet(() -> {
                User user = new User();
                user.setPhone(phone);
                user.setNickname("菜友" + phone.substring(phone.length() - 4));
                user = userRepo.save(user);
                invService.giveNewPlayerGift(user.getId());
                return user;
            });
    }

    public Optional<User> findById(Long id) {
        return userRepo.findById(id);
    }

    public String generateToken(User user) {
        return user.getId() + ":" + UUID.randomUUID().toString().substring(0, 8);
    }
}
