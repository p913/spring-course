package com.pvil.otuscourse.task01.service;

import com.pvil.otuscourse.task01.domain.Answer;
import com.pvil.otuscourse.task01.domain.Question;

public interface FormatterService {
    String formatQuestion(Question question, int index);

    String formatAnswer(Answer answer);
}
