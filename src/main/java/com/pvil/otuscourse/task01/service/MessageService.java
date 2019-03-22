package com.pvil.otuscourse.task01.service;

/**
 * Локализованные сообщения
 */
public interface MessageService {
    /**
     * Получить строку по наименованию свойства
     * @param bundlePropName Наименование свойства из бандла
     * @param params Аргументы для форматирования строки
     */
    String getMessageLocalized(String bundlePropName, Object[] params);

    /**
     * Получить строку по наименованию свойства
     * @param bundlePropName Наименование свойства из бандла
     */
    String getMessageLocalized(String bundlePropName);

}
