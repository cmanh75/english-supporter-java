package com.englishsupporter.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MyWordDTO {
    private Integer id;
    private WordDTO word;
    private LocalDateTime lastShown;
    private Integer showCount;
}


