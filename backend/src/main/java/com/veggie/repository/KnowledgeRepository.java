package com.veggie.repository;

import com.veggie.model.Knowledge;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface KnowledgeRepository extends JpaRepository<Knowledge, Integer> {
    List<Knowledge> findByVegetableId(Integer vegetableId);
}
