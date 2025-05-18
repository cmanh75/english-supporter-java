package com.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.service.WordService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.RequiredArgsConstructor;
import com.entity.Word;
import com.dto.AddWordRequest;
import com.dto.UpdateWordRequest;
import java.util.List;
import com.model.WordModel;

@RestController
@RequestMapping("/api/words")
@RequiredArgsConstructor
public class WordController {
    private final WordService wordService;

    @GetMapping("/{text}")
    public ResponseEntity<WordModel> getWord(@PathVariable String text) {
        WordModel word = wordService.getWord(text);
        return ResponseEntity.ok(word);
    }

    @PostMapping
    public ResponseEntity<WordModel> addWord(@RequestBody AddWordRequest request) {
        WordModel newWord = wordService.addWord(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(newWord);
    }

    @DeleteMapping("/{text}")
    public ResponseEntity<Void> deleteWord(@PathVariable String text) {
        wordService.deleteWord(text);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<WordModel>> getAllWords() {
        List<WordModel> words = wordService.getAllWords();
        return ResponseEntity.ok(words);
    }
}
