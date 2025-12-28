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
@RequestMapping("/api/flashcards")
@CrossOrigin(origins = "*")
public class FlashcardController {
    
    private static final Logger logger = LoggerFactory.getLogger(FlashcardController.class);
    
    private final MyWordService myWordService;
    private final DTOMapper dtoMapper;
    
    private static final int DEFAULT_LIMIT = 30;
    
    @Autowired
    public FlashcardController(MyWordService myWordService, DTOMapper dtoMapper) {
        this.myWordService = myWordService;
        this.dtoMapper = dtoMapper;
    }
    
    @GetMapping
    public ResponseEntity<List<MyWordDTO>> getFlashcards(
            @RequestParam(defaultValue = "30") int limit) {
        logger.info("Getting flashcards with limit: {}", limit);
        
        List<MyWord> flashcards = myWordService.getFlashcards(limit);
        List<MyWordDTO> dtos = flashcards.stream()
                .map(mw -> dtoMapper.toMyWordDTO(mw, true))
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(dtos);
    }
}

