package com.umlpro.api.languageSupport.helper;

import com.umlpro.api.dto.Dependency;
import com.umlpro.api.dto.Function;
import com.umlpro.api.dto.Property;
import com.umlpro.api.dto.Table;
import com.umlpro.api.enums.Language;
import com.umlpro.api.languageSupport.impl.LanguageSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class LanguageHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(LanguageSupport.class);
    private static final String enter = "\n";
    private static final String tab = "\t";

    public List<Dependency> getCurrentDependenct(Table table, List<Dependency> dependencies ,String dependencyType) {
        List<Dependency> currentDependencies = new ArrayList<>();

        for (Dependency dpn: dependencies) {
            if (dpn.getFrom().getId() == table.getId()){
                if (dpn.getRelation().equals(dependencyType)){
                    currentDependencies.add(dpn);
                }
            }
        }

        return currentDependencies;
    }

    public StringBuilder addJavaStyleConstructors(StringBuilder classBody, Table table) {
        //no arg constructor
        classBody.append(table.getName() + "() {" + enter + tab + "}" + enter);

        classBody.append(enter);

        if (!table.getProperties().isEmpty()) {
            //all arg constructor
            classBody.append(tab);
            classBody.append(table.getName() + " (");

            int propCount = 0;
            for (Property prop: table.getProperties()) {
                if (propCount == 0) {
                    classBody.append(prop.getType() + " " + prop.getName());
                    propCount++;
                }else {
                    classBody.append(", " + prop.getType() + " " + prop.getName());
                    propCount++;
                }
            }

            classBody.append(") {" + enter);

            for (Property prop: table.getProperties()) {
                classBody.append(tab + tab);
                classBody.append("this." + prop.getName() + " = " + prop.getName() + ";");
                classBody.append(enter);
            }
            classBody.append(tab + "}" + enter);
        }

        return classBody;
    }

    public StringBuilder addJavaStyleMethods(StringBuilder classBody, Table table, Language lang) {
        for (Function fun: table.getFunctions()) {
            classBody.append(enter + tab);


            if (lang == Language.PHP || ((lang == Language.JAVA || lang == Language.CHARP) && table.getType().equals("class"))) {
                if (fun.getAccess().equals("+")) {
                    classBody.append("public ");
                }else if (fun.getAccess().equals("-")) {
                    classBody.append("private ");
                }
            }


            if (lang == Language.PHP) {
                classBody.append("function " + fun.getName());
            } else if (lang == Language.JAVA || lang == Language.CHARP) {
                classBody.append(fun.getType() + " " + fun.getName());
            }

            if (table.getType().equals("class")) {
                classBody.append( " {" + enter + tab + "}");
            }else if (table.getType().equals("interface")) {
                classBody.append(";");
            }
            classBody.append(enter);
        }

        return classBody;
    }

    public StringBuilder addJavaStyleGetterSetter(StringBuilder classBody, List<Property> properties) {
        for (Property prop: properties) {
            classBody.append(enter);

            classBody.append(tab + "public " + prop.getType() + " get" + prop.getName() + "() {" + enter);
            classBody.append(tab + tab + "return this." + prop.getName() + ";" + enter);
            classBody.append(tab + "}" + enter);

            classBody.append(enter);

            classBody.append(tab + "public void set" + prop.getName() + "(" + prop.getType() + " " + prop.getName() +") {" + enter);
            classBody.append(tab + tab + "this." + prop.getName() + " = " + prop.getName() + ";" + enter);
            classBody.append(tab + "}" + enter);
        }
        return classBody;
    }

    public StringBuilder addJavaStyleDependencies(StringBuilder classBody, Table table, List<Dependency> dependencies) {
        List<Dependency> currentImplements = getCurrentDependenct(table, dependencies, "implements");
        List<Dependency> currentExtends = getCurrentDependenct(table, dependencies, "extends");

        //sadece içinde bulunduğumuz object ile ilgili bağımlılıklar alındı.
        /*
        arayüzde set edilen kurallar;
            class -> max. 1 extends & n implement
            interface -> n extends & 0 implement
         */
        int extendsCount = 0;
        int implementsCount = 0;

        for (Dependency dpn: currentExtends) {
            if (extendsCount == 0) {
                classBody.append(" extends " + dpn.getDestination().getName());
                extendsCount++;
            } else {
                classBody.append("," + dpn.getDestination().getName());
                extendsCount++;
            }
        }

        for (Dependency dpn: currentImplements) {
            if (implementsCount == 0) {
                classBody.append(" implements " + dpn.getDestination().getName());
                implementsCount++;
            } else {
                classBody.append("," + dpn.getDestination().getName());
                implementsCount++;
            }
        }
        //public class/interface xxxx extends yyyy implements ddd,eeee
        classBody.append(" ");
        return classBody;
    }
}
