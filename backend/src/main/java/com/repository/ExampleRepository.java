package com.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.entity.Example;
import java.util.List;

public interface ExampleRepository extends JpaRepository<Example, Long> {
    List<Example> findByEngDefinitionId(Long engDefinitionId);
}
