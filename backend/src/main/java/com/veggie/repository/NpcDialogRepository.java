package com.veggie.repository;

import com.veggie.model.NpcDialog;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NpcDialogRepository extends JpaRepository<NpcDialog, Integer> {
    List<NpcDialog> findByTriggerEvent(String triggerEvent);
    List<NpcDialog> findByTriggerEventAndVegetableIdAndSeason(String triggerEvent, Integer vegetableId, String season);
}
