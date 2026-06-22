package com.veggie.repository;

import com.veggie.model.PlayerKnowledge;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PlayerKnowledgeRepository extends JpaRepository<PlayerKnowledge, Long> {
    List<PlayerKnowledge> findByUserId(Long userId);
    boolean existsByUserIdAndKnowledgeId(Long userId, Integer knowledgeId);
}
