package com.veggie.repository;

import com.veggie.model.Visit;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface VisitRepository extends JpaRepository<Visit, Long> {
    List<Visit> findByGardenIdOrderByVisitedAtDesc(Long gardenId);
    List<Visit> findByVisitorIdOrderByVisitedAtDesc(Long visitorId);
}
