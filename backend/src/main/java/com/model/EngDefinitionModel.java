package com.model;

import java.util.List;

import com.entity.EngDefinition;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EngDefinitionModel {
    private Long id;
    private String definition;
    private List<ExampleModel> examples;

    public static EngDefinitionModel toEngDefinitionModel(EngDefinition engDefinition) {
        return EngDefinitionModel.builder()
                .id(engDefinition.getId())
                .definition(engDefinition.getDefinition())
                .examples(engDefinition.getExamples().stream().map(ExampleModel::toExampleModel).collect(Collectors.toList()))
                .build();
    }

    public static EngDefinition toEngDefinition(EngDefinitionModel engDefinitionModel) {
        return EngDefinition.builder()
                .id(engDefinitionModel.getId())
                .definition(engDefinitionModel.getDefinition())
                .examples(engDefinitionModel.getExamples()
                            .stream()
                            .map(ExampleModel::toExample)
                            .collect(Collectors.toList()))
                .build();
    }
}
