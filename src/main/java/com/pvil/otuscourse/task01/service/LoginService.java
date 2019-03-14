package com.pvil.otuscourse.task01.service;

import com.pvil.otuscourse.task01.domain.Student;

/**
 * Сервис аутентифицирует пользователя
 */
public interface LoginService {

    /**
     * Авторизовать
     * @return Авторизованный студент или {@code null} при неудаче
     */
    Student login();
}
