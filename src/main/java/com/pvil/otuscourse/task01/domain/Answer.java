package com.pvil.otuscourse.task01.domain;

public class Answer {
    private int questionIndex;

    private String question;

    private String correctVariant;

    private String answeredVariant = "";

    private int correctVariantIndex;

    private int answeredVariantIndex = -1;

    public Answer(int index, String question, String correctVariant, int correctVariantIndex) {
        this.questionIndex = index;
        this.question = question;
        this.correctVariant = correctVariant;
        this.correctVariantIndex = correctVariantIndex;
    }

    public Answer(int index, String question, String correctVariant, String answeredVariant, int correctVariantIndex, int answeredVariantIndex) {
        this.questionIndex = index;
        this.question = question;
        this.correctVariant = correctVariant;
        this.answeredVariant = answeredVariant;
        this.correctVariantIndex = correctVariantIndex;
        this.answeredVariantIndex = answeredVariantIndex;
    }

    public int getQuestionIndex() {
        return questionIndex;
    }

    public String getQuestion() {
        return question;
    }

    public boolean isAnswered() {
        return answeredVariantIndex >= 0;
    }

    public boolean isCorrect() {
        return answeredVariantIndex == correctVariantIndex;
    }

    public String getCorrectVariant() {
        return correctVariant;
    }

    public String getAnsweredVariant() {
        return answeredVariant;
    }

    public int getCorrectVariantIndex() {
        return correctVariantIndex;
    }

    public int getAnsweredVariantIndex() {
        return answeredVariantIndex;
    }
}
