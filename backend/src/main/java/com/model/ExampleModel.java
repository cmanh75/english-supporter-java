package com.model;

import lombok.Builder;
import lombok.Data;
import com.entity.Example;

@Data
@Builder
public class ExampleModel {
    private Long id;
    private String example;

    public static ExampleModel toExampleModel(Example example) {
        return ExampleModel.builder()
                .id(example.getId())
                .example(example.getExample())
                .build();
    }

    public static Example toExample(ExampleModel exampleModel) {
        return Example.builder()
                .id(exampleModel.getId())
                .example(exampleModel.getExample())
                .build();
    }
}
