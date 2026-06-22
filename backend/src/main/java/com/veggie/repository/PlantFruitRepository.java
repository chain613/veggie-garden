package com.veggie.repository;

import com.veggie.model.PlantFruit;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PlantFruitRepository extends JpaRepository<PlantFruit, Long> {
    List<PlantFruit> findByPlantIdAndHarvestedAtIsNull(Long plantId);
    List<PlantFruit> findByPlantId(Long plantId);
}
