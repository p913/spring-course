package com.pvil.otuscourse.task01;

import com.pvil.otuscourse.task01.domain.Student;
import com.pvil.otuscourse.task01.service.TestingService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.MessageSource;
import org.springframework.shell.MethodTarget;
import org.springframework.shell.Shell;
import org.springframework.shell.jline.InteractiveShellApplicationRunner;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Locale;

import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
@SpringBootTest(properties = { InteractiveShellApplicationRunner.SPRING_SHELL_INTERACTIVE_ENABLED + "=" + false })
public class ShellCommandsTest {
    @SpyBean
    private TestingService testingService;

    @Autowired
    private Shell shell;

    @Autowired
    private MessageSource messageSource;

    @Test
    public void test() throws Exception {
        Student vasiaPupkin = new Student("Vasia", "Pupkin");
        Student petyaChushkin = new Student("Petya", "Chushkin");

        assertUnavailability("start", "login.required");
        assertUnavailability("a", "login.required");
        assertUnavailability("b", "login.required");
        assertUnavailability("c", "login.required");
        assertUnavailability("d", "login.required");
        assertUnavailability("e", "login.required");
        assertUnavailability("f", "login.required");
        assertUnavailability("skip", "login.required");
        assertAvailability("login");
        assertAvailability("result");

        shell.evaluate(() -> "login " + vasiaPupkin.toString());

        assertAvailability("start");
        assertUnavailability("a", "testing.notStarted");
        assertUnavailability("b", "testing.notStarted");
        assertUnavailability("c", "testing.notStarted");
        assertUnavailability("d", "testing.notStarted");
        assertUnavailability("e", "testing.notStarted");
        assertUnavailability("f", "testing.notStarted");
        assertUnavailability("skip", "testing.notStarted");
        assertAvailability("login");
        assertAvailability("result");

        shell.evaluate(() -> "start");

        assertUnavailability("start", "testing.cantStartAgain");
        assertAvailability("a");
        assertAvailability("b");
        assertAvailability("c");
        assertAvailability("d");
        assertUnavailability("e", "testing.variantUnavailable");
        assertUnavailability("f", "testing.variantUnavailable");
        assertAvailability("skip");
        assertAvailability("login");
        assertAvailability("result");

        shell.evaluate(() -> "skip");
        assertEquals(0, testingService.getTestingResult());

        shell.evaluate(() -> "b");
        assertEquals(25, testingService.getTestingResult());

        shell.evaluate(() -> "c");
        assertEquals(50, testingService.getTestingResult());

        shell.evaluate(() -> "d");
        assertEquals(75, testingService.getTestingResult());

        shell.evaluate(() -> "a");
        assertEquals(100, testingService.getTestingResult());

        verify(testingService).complete(vasiaPupkin);

        shell.evaluate(() -> "login " + petyaChushkin.toString());

        assertAvailability("start");
        assertUnavailability("a", "testing.notStarted");
        assertUnavailability("b", "testing.notStarted");
        assertUnavailability("c", "testing.notStarted");
        assertUnavailability("d", "testing.notStarted");
        assertUnavailability("e", "testing.notStarted");
        assertUnavailability("f", "testing.notStarted");
        assertUnavailability("skip", "testing.notStarted");
        assertAvailability("login");
        assertAvailability("result");

    }

    private void assertUnavailability(String command, String expectedMessageBundleName) {
        MethodTarget mt = shell.listCommands().get(command);
        assertNotNull(command, mt);
        assertFalse(command, mt.getAvailability().isAvailable());

        if (expectedMessageBundleName != null)
            assertEquals(command,
                    messageSource.getMessage(expectedMessageBundleName, null, Locale.getDefault()),
                    mt.getAvailability().getReason());
    }

    private void assertAvailability(String command) {
        MethodTarget mt = shell.listCommands().get(command);
        assertNotNull(command, mt);
        assertTrue(command, mt.getAvailability().isAvailable());
    }

}
