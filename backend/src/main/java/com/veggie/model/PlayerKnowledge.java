package com.veggie.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "t_player_knowledge")
public class PlayerKnowledge {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "knowledge_id", nullable = false)
    private Integer knowledgeId;

    @Column(name = "unlocked_at")
    private LocalDateTime unlockedAt = LocalDateTime.now();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Integer getKnowledgeId() { return knowledgeId; }
    public void setKnowledgeId(Integer knowledgeId) { this.knowledgeId = knowledgeId; }
    public LocalDateTime getUnlockedAt() { return unlockedAt; }
}
