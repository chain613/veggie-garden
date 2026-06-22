package com.veggie.repository;

import com.veggie.model.Garden;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface GardenRepository extends JpaRepository<Garden, Long> {
    List<Garden> findByUserId(Long userId);
    int countByUserId(Long userId);
}
