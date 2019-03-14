package com.pvil.otuscourse.task01;

import com.pvil.otuscourse.task01.service.TestRunnerService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class Task01Application {
	public static void main(String[] args) {
		ApplicationContext context = SpringApplication.run(Task01Application.class, args);

		//Делаем по-старинке, и не используем CommandLineRunner, т.к. его имплементация
		//выполняется и при тестах, а это нам не надо...
		TestRunnerService testRunnerService = (context.getBean(TestRunnerService.class));
		if (testRunnerService.runTests())
			testRunnerService.showResults();
	}

}
