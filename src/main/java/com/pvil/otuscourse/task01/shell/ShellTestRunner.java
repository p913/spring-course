package com.pvil.otuscourse.task01.shell;

import com.pvil.otuscourse.task01.domain.Question;
import com.pvil.otuscourse.task01.domain.Student;
import com.pvil.otuscourse.task01.service.LoginService;
import com.pvil.otuscourse.task01.service.TestingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import java.util.Locale;

@ShellComponent
public class ShellTestRunner {
    private Student student;
    private int currentQuestionNumber = -1;
    private boolean complete;

    private final LoginService loginService;

    private final TestingService testingService;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    public ShellTestRunner(LoginService loginService, TestingService testingService) {
        this.loginService = loginService;
        this.testingService = testingService;
    }

    @ShellMethod("Login for testing")
    public String login(@ShellOption String firstName, @ShellOption String lastName) {
        student = loginService.login(firstName, lastName);
        if (student != null) {
            if (complete)
                clearAnswers();
            return messageSource.getMessage("login.ok", null, Locale.getDefault());
        } else
            return messageSource.getMessage("login.fail", null, Locale.getDefault());
    }

    @ShellMethod("Start testing")
    public String start() throws Exception {
        if (gotoNextQuestion()) {
            return getQuestionAndVariants();
        }
        return messageSource.getMessage("testing.notReady", null, Locale.getDefault());
    }

    public Availability startAvailability() {
        if (!isLogged())
            return Availability.unavailable(messageSource.getMessage("login.required", null, Locale.getDefault()));
        else if (isTestComplete() || currentQuestionNumber >= 0)
            return Availability.unavailable(messageSource.getMessage("testing.cantStartAgain", null, Locale.getDefault()));
        else
            return Availability.available();
    }

    @ShellMethod("Variant A for current question")
    public String a() throws Exception {
        testingService.setQuestionAnswer(currentQuestionNumber, 0);
        if (gotoNextQuestion()) {
            return getQuestionAndVariants();
        } else {
            return getResults();
        }
    }

    public Availability aAvailability() {
        return variantsAvailability(0);
    }

    @ShellMethod("Variant B for current question")
    public String b() throws Exception {
        testingService.setQuestionAnswer(currentQuestionNumber, 1);
        if (gotoNextQuestion()) {
            return getQuestionAndVariants();
        } else {
            return getResults();
        }
    }

    public Availability bAvailability() {
        return variantsAvailability(1);
    }

    @ShellMethod("Variant C for current question")
    public String c() throws Exception {
        testingService.setQuestionAnswer(currentQuestionNumber, 2);
        if (gotoNextQuestion()) {
            return getQuestionAndVariants();
        } else {
            return getResults();
        }
    }

    public Availability cAvailability() {
        return variantsAvailability(2);
    }

    @ShellMethod("Variant D for current question")
    public String d() throws Exception {
        testingService.setQuestionAnswer(currentQuestionNumber, 3);
        if (gotoNextQuestion()) {
            return getQuestionAndVariants();
        } else {
            return getResults();
        }
    }

    public Availability dAvailability() {
        return variantsAvailability(3);
    }

    @ShellMethod("Variant E for current question")
    public String e() throws Exception {
        testingService.setQuestionAnswer(currentQuestionNumber, 4);
        if (gotoNextQuestion()) {
            return getQuestionAndVariants();
        } else {
            return getResults();
        }
    }

    public Availability eAvailability() {
        return variantsAvailability(4);
    }

    @ShellMethod("Variant F for current question")
    public String f() throws Exception {
        testingService.setQuestionAnswer(currentQuestionNumber, 5);
        if (gotoNextQuestion()) {
            return getQuestionAndVariants();
        } else {
            return getResults();
        }
    }

    public Availability fAvailability() {
        return variantsAvailability(6);
    }

    @ShellMethod("Skip current question (will be asked later)")
    public String skip() throws Exception {
        gotoNextQuestion();
        return getQuestionAndVariants();
    }

    public Availability skipAvailability() {
        return variantsAvailability(0);
    }

    @ShellMethod("Show test result")
    public String result() {
        return getResults();
    }

    private Availability variantsAvailability(int variant) {
        if (!isLogged())
            return Availability.unavailable(messageSource.getMessage("login.required", null, Locale.getDefault()));
        else if (isTestComplete())
            return Availability.unavailable(messageSource.getMessage("testing.complete", null, Locale.getDefault()));
        else if (currentQuestionNumber == -1)
            return Availability.unavailable(messageSource.getMessage("testing.notStarted", null, Locale.getDefault()));
        else if (variant >= testingService.getQuestion(currentQuestionNumber).getVariantsCount())
            return Availability.unavailable(messageSource.getMessage("testing.variantUnavailable", null, Locale.getDefault()));
        else
            return Availability.available();
    }

    private boolean isLogged() {
        return (student != null);
    }

    private boolean isTestComplete() {
        return complete;
    }

    private boolean gotoNextQuestion() throws Exception {
        if (isTestComplete())
            return false;
        int pass = 1;
        //Перебираем неотвеченные (пропущенные) вопросы
        while (pass <= 2) {
            while (++currentQuestionNumber < testingService.getQuestionsCount()) {
                if (testingService.getQuestionAnswer(currentQuestionNumber) == -1)
                    return true;
            }
            currentQuestionNumber = -1;
            pass++;
        }

        complete = true;
        testingService.complete(student);

        return false;
    }

    private String getQuestionAndVariants() {
        StringBuilder res = new StringBuilder();

        Question question = testingService.getQuestion(currentQuestionNumber);
        res.append(messageSource.getMessage("testing.question",
                new Object[] { currentQuestionNumber + 1, testingService.getQuestionsCount(), question.getText()},
                Locale.getDefault())).append("\n");

        for (var i = 0; i < question.getVariantsCount(); i++) {
            res.append(messageSource.getMessage("testing.variant",
                    new Object[] {Character.forDigit(10 + i, 16), question.getVariant(i)},
                    Locale.getDefault())).append("\n");
        }

        return res.toString();
    }

    private String getResults() {
        StringBuilder res = new StringBuilder();
        if (student == null)
            res.append(messageSource.getMessage("testing.none", null, Locale.getDefault()));
        else {
            res.append(messageSource.getMessage(testingService.isTestPassing() ? "testing.resultOk" : "testing.resultFail",
                    new Object[] {testingService.getTestingResult()},
                    Locale.getDefault())).append("\n");

            for (var q = 0; q < testingService.getQuestionsCount(); q++) {
                if (testingService.getQuestionAnswer(q) != -1) {
                    Question question = testingService.getQuestion(q);
                    res.append(messageSource.getMessage("testing.question",
                            new Object[]{q + 1, testingService.getQuestionsCount(), question.getText()},
                            Locale.getDefault())).append("\n");

                    if (question.isVariantCorrect(testingService.getQuestionAnswer(q)))
                        res.append(messageSource.getMessage("testing.answerOk",
                                new Object[]{question.getVariant(testingService.getQuestionAnswer(q))},
                                Locale.getDefault())).append("\n");
                    else {
                        res.append(messageSource.getMessage("testing.answerReal",
                                new Object[]{question.getVariant(testingService.getQuestionAnswer(q))},
                                Locale.getDefault())).append("\n");
                        res.append(messageSource.getMessage("testing.answerExpected",
                                new Object[]{question.getVariant(question.getCorrectVariant())},
                                Locale.getDefault())).append("\n");
                    }
                }
            }

        }
        return res.toString();
    }

    private void clearAnswers() {
        currentQuestionNumber = -1;
        complete = false;
        for (var i = 0; i < testingService.getQuestionsCount(); i++)
            testingService.setQuestionAnswer(i, -1);
    }

}
