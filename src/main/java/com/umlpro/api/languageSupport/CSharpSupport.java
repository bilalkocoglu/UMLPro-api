package com.umlpro.api.languageSupport;

import com.umlpro.api.dto.Dependency;
import com.umlpro.api.dto.GenerateRequestDTO;
import com.umlpro.api.dto.Property;
import com.umlpro.api.dto.Table;
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
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
@PropertySource("classpath:messages.properties")
public class CSharpSupport implements LanguageSupport {
    private static final Logger LOGGER = LoggerFactory.getLogger(CSharpSupport.class);
    private static final String enter = "\n";
    private static final String tab = "\t";

    @Value("${extention.csharp}")
    private String extention;

    private LanguageHelper languageHelper;

    CSharpSupport(LanguageHelper languageHelper) {
        this.languageHelper = languageHelper;
    }

    @Override
    public void createClasses(File file, GenerateRequestDTO generateRequestDTO) {
        try {
            ZipOutputStream out = new ZipOutputStream(new FileOutputStream(file));

            for (Table table: generateRequestDTO.getTableList()) {
                StringBuilder classBody = new StringBuilder();

                ZipEntry zipEntry = new ZipEntry(table.getName() + extention);
                out.putNextEntry(zipEntry);
                //success create new class

                classBody = createBody(classBody, generateRequestDTO.getIsConstructor(), generateRequestDTO.getIsLombok(),
                        generateRequestDTO.getIsGetterSetter(), table, generateRequestDTO.getDependencyList());

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
        classBody.append("public " + table.getType() + " " + table.getName());  //public class/interface xxxx

        //dependency
        classBody = addDependecy(classBody, table, dependencies);  // public class/interface xxx: aaa,bbb,ccc
        classBody.append(" {");                                     // public class/interface xxx: aaa,bbb,ccc {


        //property
        classBody = addProperty(classBody, table);

        //constructor
        if (table.getType().equals("class") && isConstructor) {
            classBody = addConstructor(classBody, table);
        }

        //methods
        classBody = addMethods(classBody, table);

        //get-set
        if (table.getType().equals("class") && isGetterSetter) {
            classBody = addGetterSetter(classBody, table);
        }

        classBody.append(enter + "}");

        return classBody;
    }

    @Override
    public StringBuilder addDependecy(StringBuilder classBody, Table table, List<Dependency> dependencies) {
        List<Dependency> currentExtends = languageHelper.getCurrentDependenct(table,dependencies,"extends");
        List<Dependency> currentImplements = languageHelper.getCurrentDependenct(table,dependencies,"implements");

        int dependenctCount = 0;

        for (Dependency dpn: currentExtends) {
            if (dependenctCount == 0) {
                classBody.append(": " + dpn.getDestination().getName());
                dependenctCount++;
            }else {
                classBody.append(", " + dpn.getDestination().getName());
                dependenctCount++;
            }
        }

        for (Dependency dpn: currentImplements) {
            if (dependenctCount == 0) {
                classBody.append(": " + dpn.getDestination().getName());
            }else {
                classBody.append(", " + dpn.getDestination().getName());
                dependenctCount++;
            }
        }

        return classBody;
    }

    @Override
    public StringBuilder addProperty(StringBuilder classBody, Table table) {
        for (Property prop: table.getProperties()) {
            classBody.append(enter + tab);
            if (prop.getAccess().equals("+")){
                classBody.append("public ");
            } else if (prop.getAccess().equals("-")) {
                classBody.append("private ");
            }

            classBody.append(prop.getType() + " " + prop.getName() + " ");
            classBody.append("{ get; set; }");
        }

        classBody.append(enter);
        return classBody;
    }

    @Override
    public StringBuilder addConstructor(StringBuilder classBody, Table table) {
        classBody.append(enter + tab);

        classBody = languageHelper.addJavaStyleConstructors(classBody, table);

        return classBody;
    }

    @Override
    public StringBuilder addMethods(StringBuilder classBody, Table table) {
        classBody = languageHelper.addJavaStyleMethods(classBody, table, Language.CHARP);

        return classBody;
    }

    @Override
    public StringBuilder addGetterSetter(StringBuilder classBody, Table table) {
        List<Property> privateProperties = new ArrayList<>();

        for (Property prop: table.getProperties()) {
            if (prop.getAccess().equals("-")) {
                privateProperties.add(prop);
            }
        }

        if (!privateProperties.isEmpty()) {
            classBody.append(enter + tab + "//GETTER-SETTER FUNCTIONS");
        }

        //sadece private değişkenler için get-set oluşturulması gerek.
        classBody = languageHelper.addJavaStyleGetterSetter(classBody, privateProperties);

        return classBody;
    }
}
