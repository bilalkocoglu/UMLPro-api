package com.umlpro.api.languageSupport.helper;

import com.umlpro.api.enums.LombokType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

@Service
@PropertySource("classpath:messages.properties")
public class LombokHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(LombokHelper.class);
    private String enter = "\n";
    private String tab = "\t";

    public StringBuilder addAttribute(StringBuilder classBody, LombokType type) {
        classBody.append(enter);

        classBody = imports(classBody, type);

        classBody.append(enter);
        classBody.append(enter);

        classBody = annotations(classBody, type);

        return classBody;
    }

    private StringBuilder imports(StringBuilder classBody, LombokType type) {
        return getStringBuilder(classBody, type, allArgImp, noArgImp, dataImp, buildImp);
    }

    private StringBuilder annotations(StringBuilder classBody, LombokType type) {
        return getStringBuilder(classBody, type, allArg, noArg, data, builder);
    }

    private StringBuilder getStringBuilder(StringBuilder classBody, LombokType type, String allArg, String noArg, String data, String builder) {
        if (type == LombokType.ALL){
            classBody.append(allArg + enter);
            classBody.append(noArg + enter);
            classBody.append(data + enter);
            classBody.append(builder + enter);
        } else if (type == LombokType.CONST) {
            classBody.append(allArg + enter);
            classBody.append(noArg + enter);
            classBody.append(builder + enter);
        } else if (type == LombokType.GETSET) {
            classBody.append(builder + enter);
            classBody.append(data + enter);
        }

        return classBody;
    }

    @Value("${lombok.import.constructor.all}")
    private String allArgImp;

    @Value("${lombok.import.constructor.no}")
    private String noArgImp;

    @Value("${lombok.import.builder}")
    private String buildImp;

    @Value("${lombok.import.data}")
    private String dataImp;

    @Value("${lombok.annotation.data}")
    private String data;

    @Value("${lombok.annotation.constructor.no}")
    private String noArg;

    @Value("${lombok.annotation.constructor.all}")
    private String allArg;

    @Value("${lombok.annotation.builder}")
    private String builder;
}
