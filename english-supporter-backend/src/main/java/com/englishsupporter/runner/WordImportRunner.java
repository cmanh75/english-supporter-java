package com.englishsupporter.runner;

import com.englishsupporter.dto.ImportWordsResponse;
import com.englishsupporter.service.WordImportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * CommandLineRunner to import words from file on application startup
 * 
 * Usage:
 * - Set property: word.import.enabled=true
 * - Set property: word.import.file=path/to/words.txt
 * 
 * Or run with: java -jar app.jar --word.import.enabled=true --word.import.file=words.txt
 */
@Component
@Order(1)
@ConditionalOnProperty(name = "word.import.enabled", havingValue = "true", matchIfMissing = false)
public class WordImportRunner implements CommandLineRunner {
    
    private static final Logger logger = LoggerFactory.getLogger(WordImportRunner.class);
    
    private final WordImportService wordImportService;
    
    @Autowired
    public WordImportRunner(WordImportService wordImportService) {
        this.wordImportService = wordImportService;
    }
    
    @Override
    public void run(String... args) throws Exception {
        String filePath = System.getProperty("word.import.file");
        
        if (filePath == null || filePath.isEmpty()) {
            // Try to find words.txt in current directory or resources
            if (Files.exists(Paths.get("words.txt"))) {
                filePath = "words.txt";
            } else if (Files.exists(Paths.get("word.csv"))) {
                filePath = "word.csv";
            } else {
                logger.warn("No word import file specified. Set word.import.file property or place words.txt in current directory.");
                return;
            }
        }
        
        if (!Files.exists(Paths.get(filePath))) {
            logger.error("Word import file not found: {}", filePath);
            return;
        }
        
        logger.info("Starting word import from file: {}", filePath);
        
        try {
            ImportWordsResponse response;
            
            if (filePath.endsWith(".csv")) {
                response = wordImportService.importFromCsv(filePath);
            } else {
                response = wordImportService.importFromFile(filePath);
            }
            
            logger.info("Word import completed!");
            logger.info("Total words: {}", response.getTotalWords());
            logger.info("Successfully imported: {}", response.getSuccessCount());
            logger.info("Failed: {}", response.getFailedCount());
            logger.info("Skipped (already exist): {}", response.getSkippedCount());
            
            if (!response.getFailedWords().isEmpty()) {
                logger.warn("Failed words: {}", String.join(", ", response.getFailedWords()));
            }
            
        } catch (Exception e) {
            logger.error("Error during word import: {}", e.getMessage(), e);
        }
    }
}


