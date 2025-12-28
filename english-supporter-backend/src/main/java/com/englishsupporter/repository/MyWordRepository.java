package com.englishsupporter.repository;

import com.englishsupporter.entity.MyWord;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MyWordRepository extends JpaRepository<MyWord, Integer> {
    Optional<MyWord> findByWordId(Integer wordId);
    
    /**
     * Find all MyWords ordered by last_shown (NULLS FIRST, then oldest first)
     * This query is optimized with index on last_shown column
     */
    @Query("SELECT m FROM MyWord m ORDER BY m.lastShown ASC NULLS FIRST")
    List<MyWord> findAllOrderByLastShownAsc(Pageable pageable);
}

