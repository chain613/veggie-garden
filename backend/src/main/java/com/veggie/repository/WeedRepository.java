package com.veggie.repository;

import com.veggie.model.Weed;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface WeedRepository extends JpaRepository<Weed, Long> {
    List<Weed> findByGardenId(Long gardenId);
    int countByGardenId(Long gardenId);
    boolean existsByGardenIdAndGridXAndGridY(Long gardenId, Integer gridX, Integer gridY);
}
