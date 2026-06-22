package com.veggie.repository;

import com.veggie.model.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface WarehouseRepository extends JpaRepository<Warehouse, Long> {
    List<Warehouse> findByUserId(Long userId);
    Optional<Warehouse> findByUserIdAndVegetableId(Long userId, Integer vegetableId);
}
