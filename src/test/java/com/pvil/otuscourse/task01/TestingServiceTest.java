package com.pvil.otuscourse.task01;

import com.pvil.otuscourse.task01.config.TestingProperties;
import com.pvil.otuscourse.task01.dao.QuestionsDao;
import com.pvil.otuscourse.task01.domain.Question;
import com.pvil.otuscourse.task01.domain.Student;
import com.pvil.otuscourse.task01.service.TestingService;
import com.pvil.otuscourse.task01.service.TestingServiceGeneral;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.shell.jline.InteractiveShellApplicationRunner;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest(properties = { InteractiveShellApplicationRunner.SPRING_SHELL_INTERACTIVE_ENABLED + "=" + false })
public class TestingServiceTest {

    @TestConfiguration
    static class TestingServiceGeneralTestContextConfiguration {
        @MockBean
        private QuestionsDao dao;

        @MockBean
        private TestingProperties props;

        @Bean
        public TestingService testingService() throws Exception {
            when(dao.getQuestions()).thenReturn(questions);
            when(props.getRatingForPass()).thenReturn(60);

            return new TestingServiceGeneral(dao, props);
        }
    }

    private static List<Question> questions = Arrays.asList(new Question[] {
            new Question("Вопрос 1", Arrays.asList(new String[] {"Вариант 1.1", "Вариант 1.2", "Вариант 1.3"}), 2),
            new Question("Вопрос 2", Arrays.asList(new String[] {"Вариант 2.1", "Вариант 2.2", "Вариант 2.3"}), 1),
            new Question("Вопрос 3", Arrays.asList(new String[] {"Вариант 3.1", "Вариант 3.2", "Вариант 3.3"}), 0)
    });

    @Autowired
    private QuestionsDao dao;

    @Autowired
    private TestingProperties props;

    @Autowired
    private TestingService testingService;

    @Test
    public void testAccessToQuestions() throws Exception {
        assertEquals(questions.size(), testingService.getQuestionsCount());
        assertEquals(questions.get(2), testingService.getQuestion(2));
        assertEquals(true, testingService.getQuestion(2).isVariantCorrect(questions.get(2).getCorrectVariant()));

    }

    @Test
    public void testTesting() throws Exception {
        //Отвечаем на вопросы
        testingService.setQuestionAnswer(0, 0);
        testingService.setQuestionAnswer(1, 1);
        testingService.setQuestionAnswer(2, 2);

        //Проверяем, как зафиксировались ответы, д.б. 1 правильный, т.е. 33%, и тест провален
        assertEquals(2, testingService.getQuestionAnswer(2));
        assertEquals((int)(100.0 * 1 / 3), testingService.getTestingResult());
        assertEquals(false, testingService.isTestPassing());

        //Исправляем ответ и перепроверяем, д.б. 2 правильных, т.е. 66%, и тест пройден
        testingService.setQuestionAnswer(2, 0);

        assertEquals(0, testingService.getQuestionAnswer(2));
        assertEquals((int)(100.0 * 2 / 3), testingService.getTestingResult());
        assertEquals(true, testingService.isTestPassing());

        //Завершение теста должно где-то сохранить результаты
        var student = new Student("Петр", "Иванов");
        testingService.complete(student);
        verify(dao).storeTestingResult(student, testingService.getTestingResult(), testingService.isTestPassing());

    }

}
