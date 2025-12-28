package com.englishsupporter.service;

import com.englishsupporter.dto.ImportWordsResponse;
import com.englishsupporter.entity.Word;
import com.englishsupporter.repository.WordRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class WordService {
    
    private static final Logger logger = LoggerFactory.getLogger(WordService.class);
    
    private final WordRepository wordRepository;
    private final ScrapingService scrapingService;
    
    @Autowired
    public WordService(WordRepository wordRepository, ScrapingService scrapingService) {
        this.wordRepository = wordRepository;
        this.scrapingService = scrapingService;
    }
    
    public Optional<Word> getWordByText(String text) {
        return wordRepository.findByText(text);
    }
    
    public Optional<Word> getWordById(Integer id) {
        return wordRepository.findById(id);
    }
    
    @Transactional
    public Word searchAndScrapeWord(String wordText) {
        // Check if word exists
        Optional<Word> existingWord = wordRepository.findByText(wordText);
        if (existingWord.isPresent()) {
            return existingWord.get();
        }
        
        // Scrape new word
        return scrapingService.scrapeWord(wordText);
    }
    
    public ImportWordsResponse importWords(List<String> words) {
        logger.info("Importing {} words", words.size());
        
        List<String> successWords = new ArrayList<>();
        List<String> failedWords = new ArrayList<>();
        List<String> skippedWords = new ArrayList<>();
        
        int totalWords = words.size();
        int successCount = 0;
        int failedCount = 0;
        int skippedCount = 0;
        
        for (String wordText : words) {
            if (wordText == null || wordText.trim().isEmpty()) {
                continue;
            }
            
            wordText = wordText.trim().toLowerCase();
            
            try {
                // Import each word in its own transaction
                Boolean result = importSingleWord(wordText);
                
                if (result == null) {
                    // Word already exists
                    skippedWords.add(wordText);
                    skippedCount++;
                } else if (result) {
                    // Success
                    successWords.add(wordText);
                    successCount++;
                    logger.info("Successfully imported word: {}", wordText);
                } else {
                    // Failed
                    failedWords.add(wordText);
                    failedCount++;
                    logger.warn("Failed to scrape word: {}", wordText);
                }
                
            } catch (Exception e) {
                failedWords.add(wordText);
                failedCount++;
                logger.error("Error importing word {}: {}", wordText, e.getMessage(), e);
            }
        }
        
        logger.info("Import completed: {} total, {} success, {} failed, {} skipped", 
                totalWords, successCount, failedCount, skippedCount);
        
        return new ImportWordsResponse(
                totalWords,
                successCount,
                failedCount,
                skippedCount,
                successWords,
                failedWords,
                skippedWords
        );
    }
    
    /**
     * Import a single word in its own transaction
     * @param wordText the word to import
     * @return true if success, false if failed, null if already exists
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public Boolean importSingleWord(String wordText) {
        // Check if word already exists
        if (wordRepository.existsByText(wordText)) {
            logger.debug("Word {} already exists, skipping", wordText);
            return null;
        }
        
        // Scrape word
        Word word = scrapingService.scrapeWord(wordText);
        
        if (word != null) {
            return true;
        } else {
            return false;
        }
    }
}

