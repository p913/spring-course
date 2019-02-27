package com.pvil.otuscourse.task01.domain;

import java.util.List;

public class Question {
    private final String text;

    private final List<String> variants;

    private final int correctVariant;

    public Question(String text, List<String> variants, int correctVariant) {
        this.text = text;
        this.correctVariant = correctVariant;
        this.variants = variants;
    }

    public String getText() {
        return text;
    }

    public int getVariantsCount() {
        return variants.size();
    }

    public String getVariant(int index) {
        return variants.get(index);
    }

    public int getCorrectVariant() {
        return correctVariant;
    }

    public boolean isVariantCorrect(int index) {
        return index == correctVariant;
    }

}
