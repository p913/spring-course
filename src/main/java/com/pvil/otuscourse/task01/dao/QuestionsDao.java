package com.pvil.otuscourse.task01.dao;

import com.pvil.otuscourse.task01.domain.Question;
import com.pvil.otuscourse.task01.domain.Student;

import java.io.IOException;
import java.util.List;

public interface QuestionsDao {
    /**
     * Получить список вопросов теста
     * @return Список, всегда {@code not null}
     */
    List<Question> getQuestions() throws Exception;

    /**
     * Сохранить результаты прохождения теста
     * @param student Студент
     * @param rating Результат теста в процентах
     */
    void storeTestingResult(Student student, int rating, boolean passed) throws Exception;
}
