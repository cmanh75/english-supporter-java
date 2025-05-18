package com.service;

import com.model.WordModel;
import com.dto.AddWordRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface WordService {
    WordModel getWord(String text);
    WordModel addWord(AddWordRequest request);
    void deleteWord(String text);
    List<WordModel> getAllWords();
}
