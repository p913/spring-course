package com.pvil.otuscourse.task01.service;

import com.pvil.otuscourse.task01.domain.Question;
import com.pvil.otuscourse.task01.domain.Student;

public interface TestingService {
    /**
     * Кол-во вопросов в тесте
     * @return Кол-во
     */
    int getQuestionsCount();

    /**
     * Получить вопрос по индексу
     * @param index Индекс, начиная с 0
     * @return Вопрос
     */
    Question getQuestion(int index);

    /**
     * Ответ студента
     * @param questionIndex Индекс вопроса, начиная с 0
     * @param variantIndex Индекс ответа, начиная с 0
     */
    void setQuestionAnswer(int questionIndex, int variantIndex);

    /**
     * Получить ответ студента
     * @param indexQuestion Индекс вопроса, начиная с 0
     * @return Номер ответа, начиная с 0, или -1, если ответа нет
     */
    int getQuestionAnswer(int indexQuestion);

    /**
     * Результат прохождения теста
     * @return % правильных ответов
     */
    int getTestingResult();

    /**
     * Пройден ли тест студентом, т.е. на все ли вопросы даны ответы и
     * достаточно ли набранных баллов
     * @return
     */
    boolean isTestPassing();

    /**
     * Зафиксировать прохождение теста
     * @param student Студент
     */
    void complete(Student student) throws Exception;
}
