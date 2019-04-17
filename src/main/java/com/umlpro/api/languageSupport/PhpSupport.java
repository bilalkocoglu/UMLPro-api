package com.umlpro.api.languageSupport;

import com.umlpro.api.dto.*;
import com.umlpro.api.enums.Language;
import com.umlpro.api.languageSupport.helper.LanguageHelper;
import com.umlpro.api.languageSupport.impl.LanguageSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
@PropertySource("classpath:messages.properties")
public class PhpSupport implements LanguageSupport {
    private static final Logger LOGGER = LoggerFactory.getLogger(PhpSupport.class);
    private static final String enter = "\n";
    private static final String tab = "\t";

    @Value("${extntion.php}")
    private String extention = ".php";

    private LanguageHelper languageHelper;

    PhpSupport(LanguageHelper languageHelper) {
        this.languageHelper = languageHelper;
    }

    @Override
    public void createClasses(File file, GenerateRequestDTO generateRequestDTO) {
        try {
            ZipOutputStream out = new ZipOutputStream(new FileOutputStream(file));

            for (Table table: generateRequestDTO.getTableList()) {
                StringBuilder classBody = new StringBuilder();
                classBody.append("<?php" + enter);

                ZipEntry zipEntry = new ZipEntry(table.getName() + extention);
                out.putNextEntry(zipEntry);


                classBody = createBody(classBody, generateRequestDTO.getIsConstructor(), generateRequestDTO.getIsLombok(),
                      generateRequestDTO.getIsGetterSetter(), table, generateRequestDTO.getDependencyList());

                classBody.append(enter + "?>");

                byte[] data = classBody.toString().getBytes();
                out.write(data, 0 , data.length);

                out.closeEntry();
            }

            out.close();

        }catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    @Override
    public StringBuilder createBody(StringBuilder classBody, boolean isConstructor, boolean isLombok, boolean isGetterSetter, Table table, List<Dependency> dependencies) {
        classBody.append(enter);
        classBody.append(table.getType() + " " + table.getName());  //class/interface xxxx

        //dependency
        classBody = addDependecy(classBody, table, dependencies);
        classBody.append("{ " + enter);

        //properties
        classBody = addProperty(classBody,table);

        //constructor
        if (table.getType().equals("class") && isConstructor) {
            //interface'lerin constructor'ı olmaz.
            //classta constructor istenmişse constuctor oluşturulması gerekir
            //OverloadedConstructors sebebiyle tek cons yapıldı
            classBody = addConstructor(classBody, table);
        }

        //methods
        classBody = addMethods(classBody, table);

        //get-set
        if (table.getType().equals("class") && isGetterSetter) {
            classBody = addGetterSetter(classBody, table);
        }

        //class end
        classBody.append("}");


        return classBody;
    }

    @Override
    public StringBuilder addDependecy(StringBuilder classBody, Table table, List<Dependency> dependencies) {
        classBody = languageHelper.addJavaStyleDependencies(classBody, table, dependencies);

        return classBody;
    }

    @Override
    public StringBuilder addProperty(StringBuilder classBody, Table table) {
        for (Property prop: table.getProperties()) {
            classBody.append(tab);
            if (prop.getAccess().equals("+")){
                classBody.append("public ");
            }else if (prop.getAccess().equals("-")) {
                classBody.append("private ");
            }

            classBody.append("$" + prop.getName() + ";" + enter);
        }

        return classBody;
    }

    @Override
    public StringBuilder addConstructor(StringBuilder classBody, Table table) {
        classBody.append(enter + tab);

        //all arg cons
        classBody.append("public function __construct( ");

        int propCount = 0;
        for (Property prop: table.getProperties()) {
            if (propCount == 0) {
                classBody.append("$"+prop.getName());
                propCount++;
            }else {
                classBody.append(", $" + prop.getName());
            }
        }

        classBody.append(" ) {");

        for (Property prop: table.getProperties()) {
            classBody.append(enter + tab + tab);
            classBody.append("$this->" + prop.getName() + " = $" + prop.getName() + ";");
        }

        classBody.append(enter + tab + "}");
        classBody.append(enter);

        return classBody;
    }

    @Override
    public StringBuilder addMethods(StringBuilder classBody, Table table) {
        classBody = languageHelper.addJavaStyleMethods(classBody, table, Language.PHP);

        return classBody;
    }

    @Override
    public StringBuilder addGetterSetter(StringBuilder classBody, Table table) {
        if (!table.getProperties().isEmpty()){
            classBody.append(enter + tab + "//GETTER-SETTER FUNCTIONS");
        }
        for (Property prop: table.getProperties()) {
            classBody.append(enter);

            //get
            classBody.append(tab + "public function get" + prop.getName() + "(){" + enter);
            classBody.append(tab + tab + "return $this->" + prop.getName() + ";" + enter);
            classBody.append(tab + "}" + enter);

            classBody.append(enter);

            //set
            classBody.append(tab +"public function set" + prop.getName() + "($" + prop.getName() + ") {" + enter);
            classBody.append(tab + tab + "$this->" + prop.getName() + " = " + "$" + prop.getName() +";" + enter);
            classBody.append(tab + "}" + enter);
        }

        return classBody;
    }
}
