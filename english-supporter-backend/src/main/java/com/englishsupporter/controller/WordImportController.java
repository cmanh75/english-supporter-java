package com.englishsupporter.controller;

import com.englishsupporter.dto.ImportWordsRequest;
import com.englishsupporter.dto.ImportWordsResponse;
import com.englishsupporter.service.WordImportService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/words/import")
@CrossOrigin(origins = "*")
public class WordImportController {
    
    private static final Logger logger = LoggerFactory.getLogger(WordImportController.class);
    
    private final WordImportService wordImportService;
    
    @Autowired
    public WordImportController(WordImportService wordImportService) {
        this.wordImportService = wordImportService;
    }
    
    /**
     * Import words from a list in request body
     * POST /api/words/import
     * Body: { "words": ["hello", "world", "test"] }
     */
    @PostMapping
    public ResponseEntity<ImportWordsResponse> importWords(@Valid @RequestBody ImportWordsRequest request) {
        logger.info("Importing {} words via API", request.getWords().size());
        
        try {
            ImportWordsResponse response = wordImportService.importFromList(request.getWords());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error importing words: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Import words from uploaded file
     * POST /api/words/import/file
     * Content-Type: multipart/form-data
     * Body: file (text file with one word per line)
     */
    @PostMapping("/file")
    public ResponseEntity<ImportWordsResponse> importFromFile(@RequestParam("file") MultipartFile file) {
        logger.info("Importing words from uploaded file: {}", file.getOriginalFilename());
        
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        try {
            List<String> words = new ArrayList<>();
            
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
                
                String line;
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    
                    // Skip empty lines and comments
                    if (line.isEmpty() || line.startsWith("#")) {
                        continue;
                    }
                    
                    // Handle CSV format
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
            
            logger.info("Read {} words from uploaded file", words.size());
            ImportWordsResponse response = wordImportService.importFromList(words);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error importing words from file: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}


