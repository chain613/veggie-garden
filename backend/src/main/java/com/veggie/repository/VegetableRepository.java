package com.veggie.repository;

import com.veggie.model.Vegetable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VegetableRepository extends JpaRepository<Vegetable, Integer> {
}
