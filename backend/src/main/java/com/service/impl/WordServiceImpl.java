package com.service.impl;

import com.service.WordService;
import com.entity.Word;
import com.repository.WordRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.exception.InvalidWordException;
import com.exception.WordAlreadyExistsException;
import com.model.WordModel;
import com.model.EngDefinitionModel;
import com.model.CategoryModel;
import com.dto.AddWordRequest;
import com.entity.EngDefinition;
import com.entity.Category;
import com.entity.Meaning;
import com.entity.Example;

@Service
@RequiredArgsConstructor
public class WordServiceImpl implements WordService {
    private final WordRepository wordRepository;

    @Override
    public WordModel getWord(String text) {
        Word word = wordRepository.findByText(text.trim())
                .orElseThrow(() -> new RuntimeException("Word not found"));
        return WordModel.toWordModel(word);
    }

    @Override
    public WordModel addWord(AddWordRequest request) {
        if (request.getText() == null || request.getText().trim().isEmpty()) {
            throw new InvalidWordException("Word text cannot be empty");
        }
        if (request.getType() == null || request.getType().trim().isEmpty()) {
            throw new InvalidWordException("Word type cannot be empty");
        }
        if (request.getPronunciation() == null || request.getPronunciation().trim().isEmpty()) {
            throw new InvalidWordException("Word pronunciation cannot be empty");
        }
        if (wordRepository.findByText(request.getText()).isPresent()) {
            throw new WordAlreadyExistsException(request.getText());
        }

        try {
            System.out.println("Request: " + request);
            System.out.println("EngDefinitions: " + request.getEngDefinitions());
            System.out.println("Categories: " + request.getCategories());
            Word word = Word.builder()
                    .text(request.getText())
                    .type(request.getType())
                    .pronunciation(request.getPronunciation())
                    .build();

            List<Category> categories = request
                    .getCategories()
                    .stream()
                    .map(categoryModel -> {
                        Category category = CategoryModel.toCategory(categoryModel);
                        for (Meaning meaning : category.getMeanings()) {
                            meaning.setCategory(category);
                        }
                        category.setWord(word);
                        return category;
                    })
                    .collect(Collectors.toList());

            List<EngDefinition> definitions = request
                    .getEngDefinitions()
                    .stream()
                    .map(engModel -> {
                        EngDefinition engDefinition = EngDefinitionModel.toEngDefinition(engModel);
                        for (Example example : engDefinition.getExamples()) {
                            example.setEngDefinition(engDefinition);
                        }
                        engDefinition.setWord(word);
                        return engDefinition;
                    })
                    .collect(Collectors.toList());
            word.setCategories(categories);
            word.setEngDefinitions(definitions);
            Word savedWord = wordRepository.save(word);
            return WordModel.toWordModel(savedWord);
        } catch (Exception e) {
            System.err.println("Error in addWord: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public void deleteWord(String text) {
        Word word = wordRepository.findByText(text)
                .orElseThrow(() -> new RuntimeException("Word not found"));
        wordRepository.delete(word);
    }

    @Override
    public List<WordModel> getAllWords() {
        return wordRepository.findAll().stream()
                .map(WordModel::toWordModel)
                .collect(Collectors.toList());
    }
}

