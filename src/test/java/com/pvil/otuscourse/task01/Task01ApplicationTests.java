package com.pvil.otuscourse.task01;

import com.pvil.otuscourse.task01.config.DaoProperties;
import com.pvil.otuscourse.task01.dao.QuestionsDao;
import com.pvil.otuscourse.task01.domain.Student;
import com.pvil.otuscourse.task01.service.LoginService;
import com.pvil.otuscourse.task01.service.TestRunnerService;
import com.pvil.otuscourse.task01.service.TestingService;
import com.pvil.otuscourse.task01.service.UIService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;

/**
 * Здесь мы проводим "полные" тесты приложения как оно должно работать в реальности,
 * с поднятием контекста, загрузкой вопросов из файла и т.д.
 * Все тесты ориентированы на работу с вопросами и ответами из teststub.csv
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class Task01ApplicationTests {
	private static final int QUESTIONS_COUNT = 4;
	private static final int QUESTION_VARIANTS_COUNT = 4;

	@Autowired
	private DaoProperties daoProperties;

	@Autowired
	private QuestionsDao questionsDao;

	@Autowired
	private TestRunnerService testRunnerService;

	@SpyBean
	private LoginService loginService;

	@SpyBean
	private UIService uiService;

	@Autowired
	private TestingService testingService;

	@Before
	public void init() {
		doReturn(new Student("A", "I")).when(loginService).login();
	}

	@Test
	public void loadCsvTest() throws Exception {
		assertEquals("/teststub", daoProperties.getCsvResourceName());
		assertEquals(4, questionsDao.getQuestions().size());
		for (int q = 1; q <= QUESTIONS_COUNT; q++) {
			//Текст вопроса и правильный вариант
			assertEquals("Q" + q, questionsDao.getQuestions().get(q - 1).getText());
			assertEquals(q - 1, questionsDao.getQuestions().get(q - 1).getCorrectVariant());
			//Тексты ответов
			for (int a = 1; a <= QUESTION_VARIANTS_COUNT; a++) {
				assertEquals("A" + q + "." + a, questionsDao.getQuestions().get(q - 1).getVariant(a - 1));
			}
		}
	}

	@Test
	public void runFailedTest() throws Exception {
		//Всегда выбираем 1й вариант, он является правильным только для одного вопроса из
		doReturn("1").when(uiService).inputValueLocalized(anyString(), any());

		if (testRunnerService.runTests())
			testRunnerService.showResults();

		assertEquals(false, testingService.isTestPassing());
		assertEquals(25, testingService.getTestingResult());
	}

	@Test
	public void runAndSkipTest() throws Exception {
		//Выбор варианта "0" прерывает тест
		doReturn("0").when(uiService).inputValueLocalized(anyString(), any());

		assertEquals(false, testRunnerService.runTests());
	}

	@Test
	public void runPassedTest() throws Exception {
		//Выбор варианта ответа соответсвует номеру вопроса - это является правильным ответом для teststub.csv
		final AtomicInteger answer = new AtomicInteger(1); //и да - это странное применение AtomicInteger
		doAnswer(a -> { return String.valueOf(answer.getAndAdd(1) ); }).when(uiService).inputValueLocalized(anyString(), any());

		if (testRunnerService.runTests())
			testRunnerService.showResults();

		assertEquals(true, testingService.isTestPassing());
		assertEquals(100, testingService.getTestingResult());
	}

}