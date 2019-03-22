package com.pvil.otuscourse.task01.service;

import com.pvil.otuscourse.task01.domain.Answer;
import com.pvil.otuscourse.task01.domain.Question;
import com.pvil.otuscourse.task01.domain.Student;

import java.util.List;

/**
 * Организует цикл тестирования. Вопросы могут идти по порядку, быть в случайном порядке,
 * могут быть не отвеченными в принципе или же спрашиваться повторно
 */
public interface TestRunnerService {
    /**
     * Начать тестирование. Забывает все ответы и начинает с первого вопроса
     */
    void run(Student student);

    /**
     * Перейти к следующему вопросу
     * @return {@code true} если есть вопросы
     */
    boolean next() throws Exception;

    /**
     * Текущий вопрос
     */
    Question getQuestion();

    /**
     * Индекс текущего вопроса
     */
    int getQuestionIndex();

    /**
     * Кол-во вопросов в тесте
     */
    int getQuestionsCount();

    /**
     * Кол-во вариантов ответа для текущего вопроса
     */
    int getVariantsCount();

    /**
     * Установить вариант ответа на текущий вопрос
     * @param variant Вариант, начиная с 0
     */
    void setAnswer(int variant);

    /**
     * Прлучить все ответы пользователя
     * @return Список, м.б. пустым, но всегда {@not null}
     */
    List<Answer> getAnswers();

    /**
     * Результат в баллах
     */
    int getTestingResult();

    /**
     * Пройден ли тест
     */
    boolean isTestPassing();
}
