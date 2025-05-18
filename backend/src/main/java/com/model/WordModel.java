package com.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.entity.Word;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WordModel {
    private Long id;
    private String text;
    private String type;
    private String pronunciation;
    private List<CategoryModel> categories;
    private List<EngDefinitionModel> engDefinitions;

    public static WordModel toWordModel(Word word) {
        return WordModel.builder()
                .id(word.getId())
                .text(word.getText())
                .type(word.getType())
                .pronunciation(word.getPronunciation())
                .categories(word.getCategories().stream().map(CategoryModel::toCategoryModel).collect(Collectors.toList()))
                .engDefinitions(word.getEngDefinitions().stream().map(EngDefinitionModel::toEngDefinitionModel).collect(Collectors.toList()))
                .build();
    }

    public static Word toWord(WordModel wordModel) {
        return Word.builder()
                .id(wordModel.getId())
                .text(wordModel.getText())
                .type(wordModel.getType())
                .pronunciation(wordModel.getPronunciation())
                .engDefinitions(wordModel.getEngDefinitions()
                                .stream()
                                .map(EngDefinitionModel::toEngDefinition)
                                .collect(Collectors.toList()))
                .categories(wordModel.getCategories()
                                .stream()
                                .map(CategoryModel::toCategory)
                                .collect(Collectors.toList()))
                .build();
    }
}
