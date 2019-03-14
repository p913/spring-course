package com.pvil.otuscourse.task01;

import com.pvil.otuscourse.task01.domain.Question;
import com.pvil.otuscourse.task01.domain.Student;
import com.pvil.otuscourse.task01.service.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TestRunnerServiceTest {
    private static final int QUESTIONS_COUNT = 100;
    private static final int VARIANTS_IN_QUESTION = 5;

    private final List<Question> questions = new ArrayList<>();

    final List<Integer> answers = new ArrayList<>();

    @MockBean
    private LoginService loginService;

    @MockBean
    private UIService uiService;

    @MockBean
    private TestingService testingService;

    @Before
    public void init() {
        initQuestions();
        initLoginService();
        initUiServiceAnswers();
        initTestingService();
    }

    private void initQuestions() {
        for (int q = 0; q < QUESTIONS_COUNT; q++) {
            var variants = new ArrayList<String>(VARIANTS_IN_QUESTION);
            for (int v = 0; v < VARIANTS_IN_QUESTION; v++) {
                variants.add(String.format("V %d.%d", q, v));
            }
            questions.add(new Question(String.format("Q %d", q), variants, (int)(Math.random() * VARIANTS_IN_QUESTION)));
        }
    }

    private void initLoginService() {
        when(loginService.login()).thenReturn(new Student("Ivan", "Petrov"));
    }

    private void initUiServiceAnswers() {
        when(uiService.inputValueLocalized(anyString(), any())).thenAnswer(a -> {
            int answer;
            if (answers.size() < QUESTIONS_COUNT - 2)
                //генерируем и запоминаем случайные ответы
                answer = (int)(Math.random() * 3);
            else //на предпоследнем вопросе даем ответ "0", чтобы тест считался прерванным
                answer = -1;
            answers.add(answer);

            return String.valueOf(answer + 1);
        } );
    }

    private void initTestingService() {
        when(testingService.getQuestionsCount()).thenReturn(QUESTIONS_COUNT);
        when(testingService.getQuestion(anyInt())).thenAnswer(i -> {return questions.get((int)i.getArguments()[0]); } );
    }

    @Test
    public void testRun() throws Exception {
        TestRunnerService runner = new TestRunnerServiceFullSeq(testingService, uiService, loginService);
        boolean res = runner.runTests();
        //тест должен быть прерваным, т.к. на предпоследний вопрос отказались отвечать
        assertEquals(false, res);
        //Должно быть задано вопросов как QUESTIONS_COUNT - 1
        assertEquals(QUESTIONS_COUNT - 1, answers.size());

        for (int i = 0; i < answers.size(); i++) {
            //проверяем как применялись ответы, кроме отказа от ответов
            if (answers.get(i) >= 0)
                verify(testingService).setQuestionAnswer(i, answers.get(i));
        }
    }
}
