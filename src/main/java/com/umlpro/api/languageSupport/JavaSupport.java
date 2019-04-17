package com.umlpro.api.languageSupport;


import com.umlpro.api.dto.*;
import com.umlpro.api.enums.Language;
import com.umlpro.api.enums.LombokType;
import com.umlpro.api.languageSupport.helper.LanguageHelper;
import com.umlpro.api.languageSupport.helper.LombokHelper;
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
public class JavaSupport implements LanguageSupport {
    private static final Logger LOGGER = LoggerFactory.getLogger(JavaSupport.class);
    private static final String enter = "\n";
    private static final String tab = "\t";

    @Value("${extention.java}")
    private String extention;

    private LombokHelper lombokHelper;
    private LanguageHelper languageHelper;

    JavaSupport(LombokHelper lombokHelper,
                LanguageHelper languageHelper) {
        this.lombokHelper = lombokHelper;
        this.languageHelper = languageHelper;
    }

    public void createClasses(File file, GenerateRequestDTO generateRequestDTO) {
        try {
            ZipOutputStream out = new ZipOutputStream(new FileOutputStream(file));

            for (Table table: generateRequestDTO.getTableList()) {
                StringBuilder classBody = new StringBuilder();

                ZipEntry zipEntry = new ZipEntry(table.getName() + extention);
                out.putNextEntry(zipEntry);
                //success create new class

                classBody.append(enter);

                if (table.getType().equals("class") && generateRequestDTO.getIsLombok()) {
                    if (generateRequestDTO.getIsConstructor() && generateRequestDTO.getIsGetterSetter() ) {
                        classBody = lombokHelper.addAttribute(classBody, LombokType.ALL);
                    } else if (generateRequestDTO.getIsConstructor()) {
                        classBody = lombokHelper.addAttribute(classBody, LombokType.CONST);
                    } else if (generateRequestDTO.getIsGetterSetter()) {
                        classBody = lombokHelper.addAttribute(classBody, LombokType.GETSET);
                    } else {
                        classBody = lombokHelper.addAttribute(classBody, LombokType.ALL);
                    }
                }


                classBody = createBody(classBody, generateRequestDTO.getIsConstructor(), generateRequestDTO.getIsLombok(),
                        generateRequestDTO.getIsGetterSetter(), table, generateRequestDTO.getDependencyList());

                byte[] data = classBody.toString().getBytes();
                out.write(data, 0 , data.length);

                out.closeEntry();
            }

            out.close();
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    public StringBuilder createBody(StringBuilder classBody, boolean isConstructor, boolean isLombok,
                                    boolean isGetterSetter, Table table, List<Dependency> dependencies) {
        classBody.append("public " + table.getType() + " " + table.getName());  //public class/interface xxxx

        classBody = addDependecy(classBody, table, dependencies);  //public class/interface xxxx extends yyyy implements ddd,eeee

        classBody.append("{" + enter);
        //properties - constructor(!lombok) - getter/setter(!lombok)

        //properties
        classBody = addProperty(classBody, table);

        //constructor
        if (table.getType().equals("class") && !isLombok && isConstructor) {
            //interface'lerin constructor'ı olmaz.
            //classta constructor istenmişse ve lombok istenmemişse constuctor oluşturulması gerekir
            classBody = addConstructor(classBody, table);
        }

        //methods
        classBody = addMethods(classBody, table);


        //get-set
        if (table.getType().equals("class") && !isLombok && isGetterSetter) {
            //interface get set olmaz.
            //classta get set istenmişse ve lombok istenmemişse
            classBody = addGetterSetter(classBody, table);
        }

        //class end
        classBody.append("}");

        return classBody;
    }

    public StringBuilder addDependecy(StringBuilder classBody, Table table, List<Dependency> dependencies) {
        classBody = languageHelper.addJavaStyleDependencies(classBody, table, dependencies);

        return classBody;
    }

    public StringBuilder addProperty(StringBuilder classBody, Table table) {
        for (Property prop: table.getProperties()) {
            classBody.append(tab);
            if (prop.getAccess().equals("+")){
                classBody.append("public ");
            }else if (prop.getAccess().equals("-")) {
                classBody.append("private ");
            }

            classBody.append(prop.getType() + " " + prop.getName() + ";" + enter);
        }

        return classBody;
    }

    public StringBuilder addConstructor(StringBuilder classBody, Table table) {
        classBody.append(enter + tab);

        classBody = languageHelper.addJavaStyleConstructors(classBody, table);

        return classBody;
    }

    public StringBuilder addMethods(StringBuilder classBody, Table table) {
        classBody = languageHelper.addJavaStyleMethods(classBody, table, Language.JAVA);

        return classBody;
    }

    public StringBuilder addGetterSetter(StringBuilder classBody, Table table) {
        if (!table.getProperties().isEmpty()) {
            classBody.append(enter + tab + "//GETTER-SETTER METHODS");
        }

        classBody = languageHelper.addJavaStyleGetterSetter(classBody, table.getProperties());

        return classBody;
    }
}
