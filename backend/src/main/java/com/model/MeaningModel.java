package com.model;

import lombok.Builder;
import lombok.Data;
import com.entity.Meaning;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MeaningModel {
    private Long id;
    private String meaning;

    public static MeaningModel toMeaningModel(Meaning meaning) {
        return MeaningModel.builder()
                .id(meaning.getId())
                .meaning(meaning.getMeaning())
                .build();
    }

    public static Meaning toMeaning(MeaningModel meaningModel) {
        return Meaning.builder()
                .id(meaningModel.getId())
                .meaning(meaningModel.getMeaning())
                .build();
    }
}
