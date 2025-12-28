package com.englishsupporter.controller;

import com.englishsupporter.dto.MyWordDTO;
import com.englishsupporter.entity.MyWord;
import com.englishsupporter.service.MyWordService;
import com.englishsupporter.util.DTOMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/mywords")
@CrossOrigin(origins = "*")
public class MyWordController {
    
    private static final Logger logger = LoggerFactory.getLogger(MyWordController.class);
    
    private final MyWordService myWordService;
    private final DTOMapper dtoMapper;
    
    @Autowired
    public MyWordController(MyWordService myWordService, DTOMapper dtoMapper) {
        this.myWordService = myWordService;
        this.dtoMapper = dtoMapper;
    }
    
    @GetMapping
    public ResponseEntity<List<MyWordDTO>> getAllMyWords() {
        logger.info("Getting all my words");
        
        List<MyWord> myWords = myWordService.getAllMyWords();
        List<MyWordDTO> dtos = myWords.stream()
                .map(mw -> dtoMapper.toMyWordDTO(mw, true))
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(dtos);
    }
    
    @PostMapping("/{wordId}")
    public ResponseEntity<MyWordDTO> addWordToMyWords(@PathVariable Integer wordId) {
        logger.info("Adding word {} to my words", wordId);
        
        try {
            MyWord myWord = myWordService.addWordToMyWords(wordId);
            MyWordDTO dto = dtoMapper.toMyWordDTO(myWord, true);
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            logger.error("Error adding word to my words: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }
    
    @DeleteMapping("/{wordId}")
    public ResponseEntity<Void> deleteMyWord(@PathVariable Integer wordId) {
        logger.info("Deleting my word {}", wordId);
        
        try {
            myWordService.deleteMyWord(wordId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            logger.error("Error deleting my word: {}", e.getMessage(), e);
            return ResponseEntity.notFound().build();
        }
    }
}


