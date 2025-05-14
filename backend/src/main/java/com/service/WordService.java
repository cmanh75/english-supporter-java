package com.service;

import com.entity.Word;
import com.dto.WordRequest;
import java.util.List;

public interface WordService {
    Word getWord(String text);
    Word addWord(WordRequest request);
    Word updateWord(Long id, WordRequest request);
    void deleteWord(Long id);
    List<Word> getAllWord();
}
