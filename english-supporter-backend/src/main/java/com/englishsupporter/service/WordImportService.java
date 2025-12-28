package com.englishsupporter.service;

import com.englishsupporter.dto.ImportWordsResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class WordImportService {
    
    private static final Logger logger = LoggerFactory.getLogger(WordImportService.class);
    
    private final WordService wordService;
    
    @Autowired
    public WordImportService(WordService wordService) {
        this.wordService = wordService;
    }
    
    /**
     * Import words from a text file (one word per line)
     */
    public ImportWordsResponse importFromFile(String filePath) {
        logger.info("Importing words from file: {}", filePath);
        
        try {
            List<String> words = readWordsFromFile(filePath);
            logger.info("Read {} words from file", words.size());
            
            return wordService.importWords(words);
            
        } catch (IOException e) {
            logger.error("Error reading file {}: {}", filePath, e.getMessage(), e);
            throw new RuntimeException("Failed to read file: " + filePath, e);
        }
    }
    
    /**
     * Import words from a list of strings
     */
    public ImportWordsResponse importFromList(List<String> words) {
        logger.info("Importing {} words from list", words.size());
        return wordService.importWords(words);
    }
    
    /**
     * Read words from file (supports .txt, .csv files)
     * Each line should contain one word, or CSV format with word in first column
     */
    private List<String> readWordsFromFile(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        
        if (!Files.exists(path)) {
            throw new IOException("File not found: " + filePath);
        }
        
        List<String> words = new ArrayList<>();
        
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                
                // Skip empty lines and comments
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                
                // Handle CSV format (word in first column)
                if (line.contains(",")) {
                    String[] parts = line.split(",");
                    if (parts.length > 0) {
                        line = parts[0].trim();
                    }
                }
                
                if (!line.isEmpty()) {
                    words.add(line.toLowerCase());
                }
            }
        }
        
        return words;
    }
    
    /**
     * Read words from CSV file (word in first column, header row optional)
     */
    public ImportWordsResponse importFromCsv(String filePath) {
        logger.info("Importing words from CSV file: {}", filePath);
        
        try {
            List<String> words = new ArrayList<>();
            Path path = Paths.get(filePath);
            
            if (!Files.exists(path)) {
                throw new IOException("File not found: " + filePath);
            }
            
            try (Stream<String> lines = Files.lines(path)) {
                words = lines
                        .skip(1) // Skip header if exists
                        .map(line -> {
                            String[] parts = line.split(",");
                            return parts.length > 0 ? parts[0].trim().toLowerCase() : "";
                        })
                        .filter(word -> !word.isEmpty())
                        .collect(Collectors.toList());
            }
            
            logger.info("Read {} words from CSV file", words.size());
            return wordService.importWords(words);
            
        } catch (IOException e) {
            logger.error("Error reading CSV file {}: {}", filePath, e.getMessage(), e);
            throw new RuntimeException("Failed to read CSV file: " + filePath, e);
        }
    }
}

