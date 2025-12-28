package com.englishsupporter.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WordDTO {
    private Integer id;
    private String text;
    private String type;
    private String pronunciation;
    private List<EngDefinitionDTO> definitions = new ArrayList<>();
    private List<CategoryDTO> categories = new ArrayList<>();
    private boolean inMyWords;
}


