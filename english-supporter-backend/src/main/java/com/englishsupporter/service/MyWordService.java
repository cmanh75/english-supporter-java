package com.englishsupporter.service;

import com.englishsupporter.entity.MyWord;
import com.englishsupporter.entity.Word;
import com.englishsupporter.repository.MyWordRepository;
import com.englishsupporter.repository.WordRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@Service
public class MyWordService {
    
    private static final Logger logger = LoggerFactory.getLogger(MyWordService.class);
    
    private final MyWordRepository myWordRepository;
    private final WordRepository wordRepository;
    
    @Autowired
    public MyWordService(MyWordRepository myWordRepository, WordRepository wordRepository) {
        this.myWordRepository = myWordRepository;
        this.wordRepository = wordRepository;
    }
    
    public List<MyWord> getAllMyWords() {
        return myWordRepository.findAll();
    }
    
    public Optional<MyWord> getMyWordByWordId(Integer wordId) {
        return myWordRepository.findByWordId(wordId);
    }
    
    public boolean isWordInMyWords(Integer wordId) {
        return myWordRepository.findByWordId(wordId).isPresent();
    }
    
    @Transactional
    public MyWord addWordToMyWords(Integer wordId) {
        // Check if already exists
        Optional<MyWord> existing = myWordRepository.findByWordId(wordId);
        if (existing.isPresent()) {
            return existing.get();
        }
        
        Word word = wordRepository.findById(wordId)
                .orElseThrow(() -> new RuntimeException("Word not found with id: " + wordId));
        
        MyWord myWord = new MyWord();
        myWord.setWord(word);
        myWord.setShowCount(0);
        
        return myWordRepository.save(myWord);
    }
    
    @Transactional
    public void deleteMyWord(Integer wordId) {
        MyWord myWord = myWordRepository.findByWordId(wordId)
                .orElseThrow(() -> new RuntimeException("MyWord not found for word id: " + wordId));
        myWordRepository.delete(myWord);
    }
    
    @Transactional
    public List<MyWord> getFlashcards(int limit) {
        // Use database-level sorting with pagination for better performance
        // This leverages the index on last_shown column
        Pageable pageable = PageRequest.of(0, limit);
        List<MyWord> selected = myWordRepository.findAllOrderByLastShownAsc(pageable);
        
        // Shuffle for variety (optional - can be removed if not needed)
        Collections.shuffle(selected);
        
        // Update last_shown and show_count
        LocalDateTime now = LocalDateTime.now();
        selected.forEach(myWord -> {
            myWord.setLastShown(now);
            myWord.setShowCount((myWord.getShowCount() == null ? 0 : myWord.getShowCount()) + 1);
        });
        
        // Batch save for better performance
        myWordRepository.saveAll(selected);
        
        return selected;
    }
}


