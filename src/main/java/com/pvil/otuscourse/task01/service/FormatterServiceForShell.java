package com.pvil.otuscourse.task01.service;

import com.pvil.otuscourse.task01.domain.Answer;
import com.pvil.otuscourse.task01.domain.Question;
import org.springframework.stereotype.Service;

@Service
public class FormatterServiceForShell implements FormatterService {

    private final MessageService messageService;

    public FormatterServiceForShell(MessageService messageService) {
        this.messageService = messageService;
    }

    @Override
    public String formatQuestion(Question question, int index) {
        StringBuilder res = new StringBuilder();

        res.append(messageService.getMessageLocalized("testing.question",
                new Object[] { index + 1, question.getText()}))
                .append("\n");

        for (var i = 0; i < question.getVariantsCount(); i++) {
            res.append(messageService.getMessageLocalized("testing.variant",
                    new Object[] {Character.forDigit(10 + i, 16), question.getVariant(i)}))
                    .append("\n");
        }

        return res.toString();
    }

    @Override
    public String formatAnswer(Answer answer) {
        StringBuilder res = new StringBuilder();
        if (answer.isAnswered()) {
            res.append(messageService.getMessageLocalized("testing.question",
                    new Object[]{answer.getQuestionIndex() + 1, answer.getQuestion()})).append("\n");

            if (answer.isCorrect())
                res.append(messageService.getMessageLocalized("testing.answerOk",
                        new Object[]{answer.getCorrectVariant()}))
                        .append("\n");
            else {
                res.append(messageService.getMessageLocalized("testing.answerReal",
                        new Object[]{answer.getAnsweredVariant()}))
                        .append("\n");
                res.append(messageService.getMessageLocalized("testing.answerExpected",
                        new Object[]{answer.getCorrectVariant()}))
                        .append("\n");
            }
        } else
            messageService.getMessageLocalized("testing.answerAbsent");

        return res.toString();
    }
}
