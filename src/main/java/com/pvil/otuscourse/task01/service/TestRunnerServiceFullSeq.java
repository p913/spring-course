package com.pvil.otuscourse.task01.service;

import com.pvil.otuscourse.task01.domain.Question;
import com.pvil.otuscourse.task01.domain.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

/**
 * Запустить тест по всем вопросам последовательно
 */
@Service
public class TestRunnerServiceFullSeq implements TestRunnerService {

    @Autowired
    private MessageSource messageSource;

    private final TestingService testingService;

    private final UIService uiService;

    private final LoginService loginService;

    private Student student;

    public TestRunnerServiceFullSeq(TestingService testingService, UIService uiService, LoginService loginService) {
        this.testingService = testingService;
        this.uiService = uiService;
        this.loginService = loginService;
    }

    private void welcome() {
        uiService.showLineLocalized("testing.welcome.1",
                new Object[] {student, testingService.getQuestionsCount()});
        uiService.showLineLocalized("testing.welcome.2", null);
    }

    private void showQuestion(int index, Question question) {
        uiService.showLineLocalized("testing.question",
                new Object[] { index + 1, testingService.getQuestionsCount(), question.getText()});

        for (var i = 0; i < question.getVariantsCount(); i++) {
            uiService.showLineLocalized("testing.variant", new Object[] {i + 1 , question.getVariant(i)});
        }
    }

    private int askAnswer(int variantsCount) {
        int variant = -1;
        do {
            try {
                variant = Integer.parseInt(uiService.inputValueLocalized("testing.askForAnswer",
                        new Object[] {variantsCount}));
            } catch (NumberFormatException e) {
            }
        } while (variant < 0 || variant > variantsCount);

        return variant;
    }

    @Override
    public boolean runTests() {
        student = loginService.login();
        if (student == null)
            return false;

        welcome();

        for (var q = 0; q < testingService.getQuestionsCount(); q++) {
            Question question = testingService.getQuestion(q);

            showQuestion(q, question);

            int variant = askAnswer(question.getVariantsCount());

            if (variant == 0) {
                uiService.showLineLocalized("testing.userInterrupt", null);
                return false;
            }

            testingService.setQuestionAnswer(q, variant - 1);
        }

        try {
            testingService.complete(student);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }

    @Override
    public void showResults() {
        if (student == null)
            uiService.showLineLocalized("testing.none", null);
        else {
            uiService.showLineLocalized(testingService.isTestPassing() ? "testing.resultOk" : "testing.resultFail",
                    new Object[] {testingService.getTestingResult()});

            for (var q = 0; q < testingService.getQuestionsCount(); q++) {
                Question question = testingService.getQuestion(q);
                uiService.showLineLocalized("testing.question",
                        new Object[] {q + 1, testingService.getQuestionsCount(), question.getText()});

                if (question.isVariantCorrect(testingService.getQuestionAnswer(q)))
                    uiService.showLineLocalized("testing.answerOk",
                            new Object[] { question.getVariant(testingService.getQuestionAnswer(q))} );
                else {
                    uiService.showLineLocalized("testing.answerReal",
                            new Object[] { question.getVariant(testingService.getQuestionAnswer(q))});
                    uiService.showLineLocalized("testing.answerExpected",
                            new Object[] { question.getVariant(question.getCorrectVariant())});
                }
            }

        }
    }
}
