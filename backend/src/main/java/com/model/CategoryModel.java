package com.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.entity.Category;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryModel {
    private Long id;
    private String name;
    private List<MeaningModel> meanings;

    public static CategoryModel toCategoryModel(Category category) {
        return CategoryModel.builder()
                .id(category.getId())
                .name(category.getName())
                .meanings(category.getMeanings().stream().map(MeaningModel::toMeaningModel).collect(Collectors.toList()))
                .build();
    }

    public static Category toCategory(CategoryModel categoryModel) {
        return Category.builder()
                .id(categoryModel.getId())
                .name(categoryModel.getName())
                .meanings(categoryModel.getMeanings().stream().map(MeaningModel::toMeaning).collect(Collectors.toList()))
                .build();
    }
}
