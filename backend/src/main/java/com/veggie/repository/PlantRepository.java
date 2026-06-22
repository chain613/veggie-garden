package com.veggie.repository;

import com.veggie.model.Plant;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PlantRepository extends JpaRepository<Plant, Long> {
    List<Plant> findByGardenId(Long gardenId);
    boolean existsByGardenIdAndGridXAndGridY(Long gardenId, Integer gridX, Integer gridY);
}
