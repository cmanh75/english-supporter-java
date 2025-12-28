package com.englishsupporter.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EngDefinitionDTO {
    private Integer id;
    private String definition;
    private List<ExampleDTO> examples = new ArrayList<>();
}


