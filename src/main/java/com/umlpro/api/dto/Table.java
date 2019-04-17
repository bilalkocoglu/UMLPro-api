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
public class Table {
    private int id;
    private String name;
    private String type;
    private int propertyIdCount;
    private List<Property> properties;
    private int functionIdCount;
    private List<Function> functions;
}
