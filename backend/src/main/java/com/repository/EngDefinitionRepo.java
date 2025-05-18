package com.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.entity.EngDefinition;
import java.util.List;

public interface EngDefinitionRepo extends JpaRepository<EngDefinition, Long> {
    List<EngDefinition> findByWordId(Long wordId);
}
