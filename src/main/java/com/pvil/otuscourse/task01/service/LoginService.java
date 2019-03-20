package com.pvil.otuscourse.task01.service;

import com.pvil.otuscourse.task01.domain.Student;

/**
 * Сервис аутентифицирует пользователя
 */
public interface LoginService {

    /**
     * Авторизовать
     * @param firstName Имя
     * @param lastName Фамилия
     * @return Авторизованный студент или {@code null} при неудаче
     */
    Student login(String firstName, String lastName);
}
