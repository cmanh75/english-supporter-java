package com.englishsupporter.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImportWordsRequest {
    @NotEmpty(message = "Word list cannot be empty")
    private List<String> words;
}


