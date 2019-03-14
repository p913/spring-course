package com.pvil.otuscourse.task01;

import com.pvil.otuscourse.task01.config.TestingProperties;
import com.pvil.otuscourse.task01.dao.QuestionsDao;
import com.pvil.otuscourse.task01.domain.Question;
import com.pvil.otuscourse.task01.domain.Student;
import com.pvil.otuscourse.task01.service.TestingService;
import com.pvil.otuscourse.task01.service.TestingServiceGeneral;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TestingServiceTest {
    List<Question> questions = new ArrayList<>();

    @Before
    public void prepare() throws Exception {
        questions.add(new Question("Вопрос 1",
                Arrays.asList(new String[] {"Вариант 1.1", "Вариант 1.2", "Вариант 1.3"}), 2));
        questions.add(new Question("Вопрос 2",
                Arrays.asList(new String[] {"Вариант 2.1", "Вариант 2.2", "Вариант 2.3"}), 1));
        questions.add(new Question("Вопрос 3",
                Arrays.asList(new String[] {"Вариант 3.1", "Вариант 3.2", "Вариант 3.3"}), 0));

        when(dao.getQuestions()).thenReturn(questions);

        when(props.getRatingForPass()).thenReturn(60);
    }

    @MockBean
    private QuestionsDao dao;

    @MockBean
    private TestingProperties props;

    @Test
    public void testAccessToQuestions() throws Exception {
        TestingService testing = new TestingServiceGeneral(dao, props);

        assertEquals(questions.size(), testing.getQuestionsCount());
        assertEquals(questions.get(2), testing.getQuestion(2));
        assertEquals(true, testing.getQuestion(2).isVariantCorrect(questions.get(2).getCorrectVariant()));

    }

    @Test
    public void testTesting() throws Exception {
        TestingService testing = new TestingServiceGeneral(dao, props);

        //Отвечаем на вопросы
        testing.setQuestionAnswer(0, 0);
        testing.setQuestionAnswer(1, 1);
        testing.setQuestionAnswer(2, 2);

        //Проверяем, как зафиксировались ответы, д.б. 1 правильный, т.е. 33%, и тест провален
        assertEquals(2, testing.getQuestionAnswer(2));
        assertEquals((int)(100.0 * 1 / 3), testing.getTestingResult());
        assertEquals(false, testing.isTestPassing());

        //Исправляем ответ и перепроверяем, д.б. 2 правильных, т.е. 66%, и тест пройден
        testing.setQuestionAnswer(2, 0);

        assertEquals(0, testing.getQuestionAnswer(2));
        assertEquals((int)(100.0 * 2 / 3), testing.getTestingResult());
        assertEquals(true, testing.isTestPassing());

        //Завершение теста должно где-то сохранить результаты
        var student = new Student("Петр", "Иванов");
        testing.complete(student);
        verify(dao).storeTestingResult(student, testing.getTestingResult(), testing.isTestPassing());

    }

}
