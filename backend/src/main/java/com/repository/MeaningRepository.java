package com.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.entity.Meaning;
import java.util.List;

public interface MeaningRepository extends JpaRepository<Meaning, Long> {
    List<Meaning> findByCategoryId(Long categoryId);
}