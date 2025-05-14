package com.service.impl;

import com.service.WordService;
import com.entity.Word;
import com.dto.AddWordRequest;
import com.repository.WordRepository;
import java.util.List;

public class WordServiceImpl implements WordService {
    private final WordRepository wordRepository;

    @Override
    public Word getWord(String text) {
        return wordRepository.findByText(text);
    }

    @Override
    public void addWord()
}
