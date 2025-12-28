package com.englishsupporter.util;

import com.englishsupporter.dto.*;
import com.englishsupporter.entity.*;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class DTOMapper {
    
    public WordDTO toWordDTO(Word word, boolean inMyWords) {
        WordDTO dto = new WordDTO();
        dto.setId(word.getId());
        dto.setText(word.getText());
        dto.setType(word.getType());
        dto.setPronunciation(word.getPronunciation());
        dto.setInMyWords(inMyWords);
        
        if (word.getDefinitions() != null) {
            dto.setDefinitions(word.getDefinitions().stream()
                    .map(this::toEngDefinitionDTO)
                    .collect(Collectors.toList()));
        }
        
        if (word.getCategories() != null) {
            dto.setCategories(word.getCategories().stream()
                    .map(this::toCategoryDTO)
                    .collect(Collectors.toList()));
        }
        
        return dto;
    }
    
    public EngDefinitionDTO toEngDefinitionDTO(EngDefinition definition) {
        EngDefinitionDTO dto = new EngDefinitionDTO();
        dto.setId(definition.getId());
        dto.setDefinition(definition.getDefinition());
        
        if (definition.getExamples() != null) {
            dto.setExamples(definition.getExamples().stream()
                    .map(this::toExampleDTO)
                    .collect(Collectors.toList()));
        }
        
        return dto;
    }
    
    public ExampleDTO toExampleDTO(Example example) {
        ExampleDTO dto = new ExampleDTO();
        dto.setId(example.getId());
        dto.setExample(example.getExample());
        return dto;
    }
    
    public CategoryDTO toCategoryDTO(Category category) {
        CategoryDTO dto = new CategoryDTO();
        dto.setId(category.getId());
        dto.setCategory(category.getCategory());
        
        if (category.getMeanings() != null) {
            dto.setMeanings(category.getMeanings().stream()
                    .map(this::toMeaningDTO)
                    .collect(Collectors.toList()));
        }
        
        return dto;
    }
    
    public MeaningDTO toMeaningDTO(Meaning meaning) {
        MeaningDTO dto = new MeaningDTO();
        dto.setId(meaning.getId());
        dto.setMeaning(meaning.getMeaning());
        return dto;
    }
    
    public MyWordDTO toMyWordDTO(MyWord myWord, boolean includeWordDetails) {
        MyWordDTO dto = new MyWordDTO();
        dto.setId(myWord.getId());
        dto.setLastShown(myWord.getLastShown());
        dto.setShowCount(myWord.getShowCount());
        
        if (includeWordDetails && myWord.getWord() != null) {
            dto.setWord(toWordDTO(myWord.getWord(), true));
        }
        
        return dto;
    }
}


