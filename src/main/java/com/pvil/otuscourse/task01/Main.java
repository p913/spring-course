package com.pvil.otuscourse.task01;

import com.pvil.otuscourse.task01.domain.Question;
import com.pvil.otuscourse.task01.domain.Student;
import com.pvil.otuscourse.task01.service.TestingService;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.NoSuchElementException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("/spring-context.xml");
        TestingService testing = context.getBean(TestingService.class);

        var input = new Scanner(System.in);

        System.out.print("Введите имя: ");
        var firstName = input.nextLine();

        System.out.print("Введите фамилию: ");
        var lastName = input.nextLine();

        var student = new Student(firstName, lastName);

        System.out.format("Г-н %s, вам приготовлен тест из %d вопросов.", student, testing.getQuestionsCount())
                .println(" Для ответа вводите номер вашего варианта (0 для прерывания теста), затем Enter.");

        for (var q = 0; q < testing.getQuestionsCount(); q++) {
            Question question = testing.getQuestion(q);
            System.out.format("Вопрос %d из %d: %s\n", q + 1, testing.getQuestionsCount(), question.getText());

            for (var i = 0; i < question.getVariantsCount(); i++) {
                System.out.format(String.format("[%d] %s\n", i + 1 , question.getVariant(i)));
            }

            int variant = -1;
            do {
                System.out.format("Введите вариант ответа от 1 до %d:", question.getVariantsCount());
                try {
                    variant = input.nextInt();
                } catch (NoSuchElementException e) {
                }
            } while (variant < 0 || variant > question.getVariantsCount());

            if (variant == 0) {
                System.out.println("Тест прерван пользователем.");
                return;
            }

            testing.setQuestionAnswer(q, variant - 1);
        }

        try {
            testing.complete(student);
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.format("Тест %s с результатом %d%%.\n", testing.isTestPassing() ? "пройден" : "НЕ пройден", testing.getTestingResult());

        for (var q = 0; q < testing.getQuestionsCount(); q++) {
            Question question = testing.getQuestion(q);
            System.out.format("Вопрос %d из %d: %s\n", q + 1, testing.getQuestionsCount(), question.getText());

            if (question.isVariantCorrect(testing.getQuestionAnswer(q)))
                System.out.format("  Ваш ответ верный: %s\n", question.getVariant(testing.getQuestionAnswer(q)));
            else
                System.out.format("  Ваш ответ: %s\n  Верный ответ: %s\n",
                        question.getVariant(testing.getQuestionAnswer(q)),
                        question.getVariant(question.getCorrectVariant()));
        }

    }
}
