package com.umlpro.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GenerateRequestDTO {
    private String language;
    private Boolean isConstructor;
    private Boolean isGetterSetter;
    private Boolean isLombok;
    private List<Table> tableList;
    private List<Dependency> dependencyList;
}
