package com.pvil.otuscourse.task01;

import com.pvil.otuscourse.task01.service.MessageService;
import com.pvil.otuscourse.task01.service.TestRunnerService;
import org.junit.FixMethodOrder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.shell.MethodTarget;
import org.springframework.shell.Shell;
import org.springframework.shell.jline.InteractiveShellApplicationRunner;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest(properties = { InteractiveShellApplicationRunner.SPRING_SHELL_INTERACTIVE_ENABLED + "=" + false })
@DisplayName("Тест путем вызова команд shell ")
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ShellCommandsTest {
    @Autowired
    private TestRunnerService testRunnerService;

    @Autowired
    private Shell shell;

    @Autowired
    private MessageService messageService;

    @BeforeEach
    public void resetShellLoginState() {
        shell.evaluate(() -> "logout");
    }

    @ParameterizedTest
    @MethodSource("testCommandsAvailabilityTestParameters")
    @DisplayName("проверяет доступность команд на разных этапах тестирования (д.б. запущен в \"чистом\") shell")
    public void testCommandsAvailabilityTest(String[] commands, ExpectedAvailability[] expected) throws Exception {
        //Выполняем последовательно команды шелла чтобы дойти до места проверки доступности команд
        for (String command: commands)
            shell.evaluate(() -> command);

        //Проверяем доступность команд, и если недоступны - то проверяем сообщение о причине
        for (ExpectedAvailability availability: expected)
            assertAvailability(availability.command, availability.available, availability.expectedMessageBundleName);
    }

    @Test
    @DisplayName("проверяет повторный логин")
    public void testReLogin() {
        shell.evaluate(() -> "login A V");
        shell.evaluate(() -> "a");

        shell.evaluate(() -> "login B D");

        assertThat(testRunnerService.getAnswers())
                .as("Не должно быть отвеченных вопросов")
                .isEmpty();
        assertThat(testRunnerService.isTestPassing())
                .as("Тест не должен считаться пройденным")
                .isFalse();
        assertThat(testRunnerService.getTestingResult())
                .as("Результат непройденного теста д.б. 0%")
                .isEqualTo(0);
    }

    @Test
    @DisplayName("проверяет прохождение теста")
    public void testPassing() {
        shell.evaluate(() -> "login A V");

        //Для 4х вопросов из тестового csv - номер ответа равен номеру вопроса
        for (int i = 0; i < 4; i++) {
            String command = String.valueOf((char)((int)'a' + i));
            shell.evaluate(() -> command);

            assertThat(testRunnerService.getAnswers())
                    .as("На итерации %d кол-во ответов д.б. %d", i, i + 1)
                    .hasSize(i + 1);
            assertThat(testRunnerService.isTestPassing())
                    .as("Тест должен считаться пройденным только после последнего ответа (итерация %d)", i)
                    .isEqualTo(i == 3);
            assertThat(testRunnerService.getTestingResult())
                    .as("На итерации %d Результат непройденного теста д.б. %d%%", i, 100 * (i + 1) / 4)
                    .isEqualTo(100 * (i + 1) / 4);
        }

    }

    @Test
    @DisplayName("проверяет провал теста")
    public void testFail() {
        shell.evaluate(() -> "login A V");

        //Для 4х вопросов из тестового csv - номер ответа равен номеру вопроса,
        // всегда отвечаем одинаково - будет 1 верный ответ
        for (int i = 0; i < 4; i++) {
            shell.evaluate(() -> "a");
        }

        assertThat(testRunnerService.getAnswers())
                    .as("Кол-во ответов д.б. равно кол-ву вопросов")
                    .hasSize(4);
        assertThat(testRunnerService.isTestPassing())
                    .as("Тест должен считаться проваленным")
                    .isFalse();
        assertThat(testRunnerService.getTestingResult())
                    .as("Результат непройденного теста д.б. 25%")
                    .isEqualTo(25);
    }

    private void assertAvailability(String command, boolean availability, String expectedMessageBundleName) {
        MethodTarget mt = shell.listCommands().get(command);
        assertThat(mt).as("Command=%s", command).isNotNull();
        assertThat(mt.getAvailability().isAvailable()).as("Command=%s", command).isEqualTo(availability);

        if (expectedMessageBundleName != null)
            assertThat(mt.getAvailability().getReason()).as("Command=%s", command)
                    .isEqualTo(messageService.getMessageLocalized(expectedMessageBundleName));
    }

    static Stream<Arguments> testCommandsAvailabilityTestParameters() {
        return Stream.of(
                Arguments.of(
                        //Пока не залогинились - ответы должны быть недоступны, а login доступен
                        new String[] {}, new ExpectedAvailability[] {
                                new ExpectedAvailability("a", "login.required"),
                                new ExpectedAvailability("b", "login.required"),
                                new ExpectedAvailability("c", "login.required"),
                                new ExpectedAvailability("d", "login.required"),
                                new ExpectedAvailability("e", "login.required"),
                                new ExpectedAvailability("f", "login.required"),
                                new ExpectedAvailability("login"),
                                new ExpectedAvailability("logout"),
                                new ExpectedAvailability("result")}
                                ),
                Arguments.of(
                        //Как только залогинились - становятся доступны ответы, но не все
                        new String[] {"login Vasia Pupkin"}, new ExpectedAvailability[] {
                                new ExpectedAvailability("a"),
                                new ExpectedAvailability("b"),
                                new ExpectedAvailability("c"),
                                new ExpectedAvailability("d"),
                                new ExpectedAvailability("e", "testing.variantUnavailable"),
                                new ExpectedAvailability("f", "testing.variantUnavailable"),
                                new ExpectedAvailability("login"),
                                new ExpectedAvailability("logout"),
                                new ExpectedAvailability("result")}
                ),
                Arguments.of(
                        //После логина ответили на вопросы (из teststub.csv в ресурсах теста), тест должен считаться пройденным
                        new String[] {"login Vasia Pupkin", "a", "b", "c", "d"}, new ExpectedAvailability[] {
                                new ExpectedAvailability("a", "testing.complete"),
                                new ExpectedAvailability("b", "testing.complete"),
                                new ExpectedAvailability("c", "testing.complete"),
                                new ExpectedAvailability("d", "testing.complete"),
                                new ExpectedAvailability("e", "testing.complete"),
                                new ExpectedAvailability("f", "testing.complete"),
                                new ExpectedAvailability("login"),
                                new ExpectedAvailability("logout"),
                                new ExpectedAvailability("result")}
                )
        );
    }

    static class ExpectedAvailability {
        String command;
        boolean available;
        String expectedMessageBundleName;

        ExpectedAvailability(String command, String expectedMessageBundleName) {
            this.command = command;
            this.available = false;
            this.expectedMessageBundleName = expectedMessageBundleName;
        }

        ExpectedAvailability(String command) {
            this.command = command;
            this.available = true;
        }

        @Override
        public String toString() {
            return "Expected that \'" + command + "\' is " + (available ? "" : "not ") + "available" +
                    (expectedMessageBundleName == null ? "" : " with " + expectedMessageBundleName);
        }
    }

}
