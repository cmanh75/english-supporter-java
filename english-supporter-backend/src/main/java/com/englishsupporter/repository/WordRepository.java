package com.englishsupporter.repository;

import com.englishsupporter.entity.Word;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WordRepository extends JpaRepository<Word, Integer> {
    Optional<Word> findByText(String text);
    boolean existsByText(String text);
}


