package com.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.entity.Word;

public interface WordRepository extends JpaRepository<Word, Long> {
    Optional <Word> findByText(String text);
    Optional <Word> findByUserId(Long userId);
}
