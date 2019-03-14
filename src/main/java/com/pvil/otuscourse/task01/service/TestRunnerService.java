package com.pvil.otuscourse.task01.service;

/**
 * Организует цикл тестирования - задает вопросы, показывает результаты
 */
public interface TestRunnerService {

    /**
     * Запустить тест
     * @return {@code true} если тест завершен полностью или {@code false} если был прерван
     */
    boolean runTests();

    /**
     * Показать результаты прохождения теста
     */
    void showResults();

}
