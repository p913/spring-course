package com.pvil.otuscourse.task01.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "testing")
public class TestingProperties {
    private int ratingForPass;

    public int getRatingForPass() {
        return ratingForPass;
    }

    public void setRatingForPass(int ratingForPass) {
        this.ratingForPass = ratingForPass;
    }
}
