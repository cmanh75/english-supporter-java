package com.englishsupporter.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImportWordsResponse {
    private int totalWords;
    private int successCount;
    private int failedCount;
    private int skippedCount; // Words that already exist
    private List<String> successWords;
    private List<String> failedWords;
    private List<String> skippedWords;
}


