package com.pvil.otuscourse.task01.service;

import com.pvil.otuscourse.task01.dao.QuestionsDao;
import com.pvil.otuscourse.task01.domain.Question;
import com.pvil.otuscourse.task01.domain.Student;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestingServiceGeneral implements TestingService {
    private final static int MINIMAL_RATING_FOR_PASSING_TEST = 50;

    private final QuestionsDao dao;

    private List<Question> questions;

    private Map<Integer, Integer> answers;

    public TestingServiceGeneral(QuestionsDao dao) throws Exception {
        this.dao = dao;

        questions = dao.getQuestions();
        answers = new HashMap<>();
    }

    @Override
    public int getQuestionsCount() {
        return questions.size();
    }

    @Override
    public Question getQuestion(int index) {
        return questions.get(index);
    }

    @Override
    public void setQuestionAnswer(int questionIndex, int variantIndex) {
        if (variantIndex >= getQuestion(questionIndex).getVariantsCount())
            throw new IndexOutOfBoundsException("Указан номер ответа, превышающий кол-во вариантов");

        answers.put(questionIndex, variantIndex);
    }

    @Override
    public int getQuestionAnswer(int indexQuestion) {
        Integer a = answers.get(indexQuestion);
        return a == null ? -1 : a;
    }

    @Override
    public int getTestingResult() {
        return (int)(100.0 * answers.entrySet().stream().mapToInt(e -> getQuestion(e.getKey()).isVariantCorrect(e.getValue()) ? 1 : 0).sum() / getQuestionsCount());
    }

    @Override
    public boolean isTestPassing() {
        return answers.size() == questions.size() && getTestingResult() >= MINIMAL_RATING_FOR_PASSING_TEST;
    }

    @Override
    public void complete(Student student) throws Exception {
        dao.storeTestingResult(student, getTestingResult(), isTestPassing());
    }
}
