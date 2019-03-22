package com.pvil.otuscourse.task01.shell;

import com.pvil.otuscourse.task01.domain.Answer;
import com.pvil.otuscourse.task01.domain.Student;
import com.pvil.otuscourse.task01.service.FormatterService;
import com.pvil.otuscourse.task01.service.LoginService;
import com.pvil.otuscourse.task01.service.MessageService;
import com.pvil.otuscourse.task01.service.TestRunnerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

@ShellComponent
public class ShellTestRunner {
    private Student student;
    private boolean complete;

    private final LoginService loginService;

    private final MessageService messageService;

    private final TestRunnerService testRunnerService;

    private final FormatterService formatterService;

    @Autowired
    public ShellTestRunner(LoginService loginService, TestRunnerService testRunnerService,
                           FormatterService formatterService, MessageService messageService) {
        this.loginService = loginService;
        this.testRunnerService = testRunnerService;
        this.formatterService = formatterService;
        this.messageService = messageService;
    }

    @ShellMethod("Login for testing")
    public String login(@ShellOption String firstName, @ShellOption String lastName) throws Exception {
        StringBuilder res = new StringBuilder();

        student = loginService.login(firstName, lastName);
        if (student != null) {
            res.append(messageService.getMessageLocalized("login.ok")).append("\n");
            res.append(messageService.getMessageLocalized("testing.questionsTotal",
                    new Object[] {testRunnerService.getQuestionsCount()})).append("\n");

            testRunnerService.run(student);
            complete = false;
            if (testRunnerService.next())
                res.append(formatterService.formatQuestion(testRunnerService.getQuestion(),
                        testRunnerService.getQuestionIndex()))
                        .append("\n");
            else {
                res.append(messageService.getMessageLocalized("testing.notReady")).append("\n");
                complete = true;
            }
        } else
            res.append(messageService.getMessageLocalized("login.fail"));

        return res.toString();
    }

    @ShellMethod("Logout so others cannot see your results")
    public void logout() {
        student = null;
    }

    private String answer(int variant) throws Exception {
        testRunnerService.setAnswer(variant);
        if (complete = !testRunnerService.next()) {
            return getResults();
        } else {
            return formatterService.formatQuestion(testRunnerService.getQuestion(), testRunnerService.getQuestionIndex());
        }
    }

    @ShellMethod("Variant A for current question")
    public String a() throws Exception {
        return answer(0);
    }

    public Availability aAvailability() {
        return variantsAvailability(0);
    }

    @ShellMethod("Variant B for current question")
    public String b() throws Exception {
        return answer(1);
    }

    public Availability bAvailability() {
        return variantsAvailability(1);
    }

    @ShellMethod("Variant C for current question")
    public String c() throws Exception {
        return answer(2);
    }

    public Availability cAvailability() {
        return variantsAvailability(2);
    }

    @ShellMethod("Variant D for current question")
    public String d() throws Exception {
        return answer(3);
    }

    public Availability dAvailability() {
        return variantsAvailability(3);
    }

    @ShellMethod("Variant E for current question")
    public String e() throws Exception {
        return answer(4);
    }

    public Availability eAvailability() {
        return variantsAvailability(4);
    }

    @ShellMethod("Variant F for current question")
    public String f() throws Exception {
        return answer(5);
    }

    public Availability fAvailability() {
        return variantsAvailability(6);
    }

    @ShellMethod("Skip current question (will be asked later)")
    public String skip() throws Exception {
        testRunnerService.next();
        return formatterService.formatQuestion(testRunnerService.getQuestion(), testRunnerService.getQuestionIndex());
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
            return Availability.unavailable(messageService.getMessageLocalized("login.required"));
        else if (isTestComplete())
            return Availability.unavailable(messageService.getMessageLocalized("testing.complete"));
        else if (variant >= testRunnerService.getQuestion().getVariantsCount())
            return Availability.unavailable(messageService.getMessageLocalized("testing.variantUnavailable"));
        else
            return Availability.available();
    }

    private boolean isLogged() {
        return (student != null);
    }

    private boolean isTestComplete() {
        return complete;
    }

    private String getResults() {
        StringBuilder res = new StringBuilder();
        if (student == null)
            res.append(messageService.getMessageLocalized("testing.none"));
        else {
            res.append(messageService.getMessageLocalized(testRunnerService.isTestPassing() ? "testing.resultOk" : "testing.resultFail",
                    new Object[] {testRunnerService.getTestingResult()}))
                    .append("\n");

            for (Answer a: testRunnerService.getAnswers())
                res.append(formatterService.formatAnswer(a)).append("\n");
        }
        return res.toString();
    }
}
