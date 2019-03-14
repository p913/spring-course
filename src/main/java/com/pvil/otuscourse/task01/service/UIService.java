package com.pvil.otuscourse.task01.service;

/**
 * Интерфейс с пользователем
 */
public interface UIService {
    /**
     * Вывести строку
     * @param line
     */
    void showLine(String line);

    /**
     * Вывести строку по наименованию свойства
     * @param bundlePropName Наименование свойства из бандла
     * @param params Аргументы для форматирования строки
     */
    void showLineLocalized(String bundlePropName, Object[] params);

    /**
     * Запросить у пользователя значение
     * @param caption Приглашение, м.б. {@code null}
     * @return Введенное пользователем значение, в виде строки, м.б. пустой строкой, но не {@code null}
     */
    String inputValue(String caption);

    /**
     * Запросить у пользователя значение, но приглашение берется из бандла по указанному наименованию свойства
     * @param bundlePropName
     * @param params
     * @return
     */
    String inputValueLocalized(String bundlePropName, Object[] params);

}
