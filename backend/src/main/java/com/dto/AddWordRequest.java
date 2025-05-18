package com.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import com.model.CategoryModel;
import com.model.EngDefinitionModel;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddWordRequest {
    private String text;
    private String type;
    private String pronunciation;
    private List<CategoryModel> categories;
    private List<EngDefinitionModel> engDefinitions;
}
