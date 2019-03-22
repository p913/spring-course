package com.pvil.otuscourse.task01.service;

import com.pvil.otuscourse.task01.domain.Answer;
import com.pvil.otuscourse.task01.domain.Question;
import com.pvil.otuscourse.task01.domain.Student;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TestRunnerServiceAllSeq implements TestRunnerService {

    private int currentQuestionNumber = -1;

    private Student student;

    private boolean complete = true;

    private final TestingService testingService;

    public TestRunnerServiceAllSeq(TestingService testingService) {
        this.testingService = testingService;
    }

    @Override
    public void run(Student student) {
        this.student = student;

        currentQuestionNumber = 0;
        clearAnswers();
    }

    @Override
    public boolean next() throws Exception {
        if (complete)
            return false;
        int pass = 1;
        //Перебираем неотвеченные (пропущенные) вопросы
        while (pass <= 2) {
            while (++currentQuestionNumber < testingService.getQuestionsCount()) {
                if (!testingService.getQuestionAnswer(currentQuestionNumber).isAnswered())
                    return true;
            }
            currentQuestionNumber = -1;
            pass++;
        }

        complete = true;
        testingService.complete(student);

        return false;
    }

    @Override
    public Question getQuestion() {
        if (currentQuestionNumber < 0)
            throw new IllegalStateException("Testing not runned");
        return testingService.getQuestion(currentQuestionNumber);
    }

    @Override
    public int getQuestionIndex() {
        if (currentQuestionNumber < 0)
            throw new IllegalStateException("Testing not runned");
        return currentQuestionNumber;
    }

    @Override
    public int getQuestionsCount() {
        return testingService.getQuestionsCount();
    }

    @Override
    public int getVariantsCount() {
        if (currentQuestionNumber < 0)
            throw new IllegalStateException("Testing not runned");
        return getQuestion().getVariantsCount();
    }

    @Override
    public void setAnswer(int variant) {
        if (currentQuestionNumber < 0)
            throw new IllegalStateException("Testing not runned");
        testingService.setQuestionAnswer(currentQuestionNumber, variant);
    }

    @Override
    public List<Answer> getAnswers() {
        List<Answer> res = new ArrayList<>();
        for (int i = 0; i < testingService.getQuestionsCount(); i++) {
            Answer answer = testingService.getQuestionAnswer(i);
            if (answer.isAnswered())
                res.add(answer);
        }
        return res;
    }

    @Override
    public int getTestingResult() {
        return testingService.getTestingResult();
    }

    @Override
    public boolean isTestPassing() {
        return testingService.isTestPassing();
    }

    private void clearAnswers() {
        currentQuestionNumber = -1;
        complete = false;
        for (var i = 0; i < testingService.getQuestionsCount(); i++)
            testingService.setQuestionAnswer(i, -1);
    }

}
