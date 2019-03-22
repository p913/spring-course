package com.pvil.otuscourse.task01.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "dao")
public class DaoProperties {
    private String csvResourceName;

    private String resultsFileName;

    public String getCsvResourceName() {
        return csvResourceName;
    }

    public void setCsvResourceName(String csvResourceName) {
        this.csvResourceName = csvResourceName;
    }

    public String getResultsFileName() {
        return resultsFileName;
    }

    public void setResultsFileName(String resultsFileName) {
        this.resultsFileName = resultsFileName;
    }
}
