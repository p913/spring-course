package com.pvil.otuscourse.task01.service;

import com.pvil.otuscourse.task01.config.TestingProperties;
import com.pvil.otuscourse.task01.dao.QuestionsDao;
import com.pvil.otuscourse.task01.domain.Answer;
import com.pvil.otuscourse.task01.domain.Question;
import com.pvil.otuscourse.task01.domain.Student;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TestingServiceGeneral implements TestingService {
    private int minRatingForPass;

    private final QuestionsDao dao;

    private List<Question> questions;

    private Map<Integer, Integer> answers;

    public TestingServiceGeneral(QuestionsDao dao, TestingProperties testingProperties) throws Exception {
        this.dao = dao;
        this.minRatingForPass = testingProperties.getRatingForPass();

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

        if (variantIndex == -1)
            answers.remove(questionIndex);
        else
            answers.put(questionIndex, variantIndex);
    }

    @Override
    public Answer getQuestionAnswer(int indexQuestion) {
        Question question = questions.get(indexQuestion);
        Integer a = answers.get(indexQuestion);
        if (a == null)
            return new Answer(indexQuestion, question.getText(), question.getVariant(question.getCorrectVariant()),
                    question.getCorrectVariant());
        else
            return new Answer(indexQuestion, question.getText(), question.getVariant(question.getCorrectVariant()),
                    question.getVariant(a), question.getCorrectVariant(), a);
    }

    @Override
    public int getTestingResult() {
        return (int)(100.0 * answers.entrySet().stream().mapToInt(e -> getQuestion(e.getKey()).isVariantCorrect(e.getValue()) ? 1 : 0).sum() / getQuestionsCount());
    }

    @Override
    public boolean isTestPassing() {
        return answers.size() == questions.size() && getTestingResult() >= minRatingForPass;
    }

    @Override
    public void complete(Student student) throws Exception {
        dao.storeTestingResult(student, getTestingResult(), isTestPassing());
    }
}
