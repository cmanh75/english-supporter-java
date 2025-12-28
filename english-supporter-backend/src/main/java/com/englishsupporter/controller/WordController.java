package com.englishsupporter.controller;

import com.englishsupporter.dto.SearchRequest;
import com.englishsupporter.dto.WordDTO;
import com.englishsupporter.entity.Word;
import com.englishsupporter.service.MyWordService;
import com.englishsupporter.service.WordService;
import com.englishsupporter.util.DTOMapper;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/words")
@CrossOrigin(origins = "*")
public class WordController {
    
    private static final Logger logger = LoggerFactory.getLogger(WordController.class);
    
    private final WordService wordService;
    private final MyWordService myWordService;
    private final DTOMapper dtoMapper;
    
    @Autowired
    public WordController(WordService wordService, MyWordService myWordService, DTOMapper dtoMapper) {
        this.wordService = wordService;
        this.myWordService = myWordService;
        this.dtoMapper = dtoMapper;
    }
    
    @GetMapping("/{word}")
    public ResponseEntity<WordDTO> getWord(@PathVariable String word) {
        logger.info("Getting word: {}", word);
        
        Optional<Word> wordEntity = wordService.getWordByText(word);
        if (wordEntity.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        Word wordObj = wordEntity.get();
        boolean inMyWords = myWordService.isWordInMyWords(wordObj.getId());
        WordDTO wordDTO = dtoMapper.toWordDTO(wordObj, inMyWords);
        
        return ResponseEntity.ok(wordDTO);
    }
    
    @PostMapping("/search")
    public ResponseEntity<WordDTO> searchWord(@Valid @RequestBody SearchRequest request) {
        logger.info("Searching for word: {}", request.getWord());
        
        try {
            Word word = wordService.searchAndScrapeWord(request.getWord());
            if (word == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .build();
            }
            
            boolean inMyWords = myWordService.isWordInMyWords(word.getId());
            WordDTO wordDTO = dtoMapper.toWordDTO(word, inMyWords);
            
            return ResponseEntity.ok(wordDTO);
        } catch (Exception e) {
            logger.error("Error searching word: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
        }
    }
}


