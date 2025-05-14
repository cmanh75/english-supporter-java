package com.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional <Category> findByWordId(Long wordId);
}
