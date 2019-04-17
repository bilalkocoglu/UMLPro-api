package com.umlpro.api.languageSupport.impl;

import com.umlpro.api.dto.Dependency;
import com.umlpro.api.dto.GenerateRequestDTO;
import com.umlpro.api.dto.Table;

import java.io.File;
import java.util.List;

public interface LanguageSupport {

    void createClasses(File file, GenerateRequestDTO generateRequestDTO);

    StringBuilder createBody(StringBuilder classBody, boolean isConstructor, boolean isLombok,
                             boolean isGetterSetter, Table table, List<Dependency> dependencies);

    StringBuilder addDependecy(StringBuilder classBody, Table table, List<Dependency> dependencies);

    StringBuilder addProperty(StringBuilder classBody, Table table);

    StringBuilder addConstructor(StringBuilder classBody, Table table);

    StringBuilder addMethods(StringBuilder classBody, Table table);

    StringBuilder addGetterSetter(StringBuilder classBody, Table table);
}
