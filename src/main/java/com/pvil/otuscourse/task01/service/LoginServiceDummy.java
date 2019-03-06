package com.pvil.otuscourse.task01.service;

import com.pvil.otuscourse.task01.domain.Student;
import org.springframework.stereotype.Service;

@Service
public class LoginServiceDummy implements LoginService {

    private final UIService uiService;

    public LoginServiceDummy(UIService uiService) {
        this.uiService = uiService;
    }

    @Override
    public Student login() {
        String firstName;
        do {
            firstName = uiService.inputValueLocalized("login.inputFirstName", null);
        } while (firstName.isEmpty());

        String lastName;
        do {
            lastName = uiService.inputValueLocalized("login.inputLastName", null);
        } while (lastName.isEmpty());

        return new Student(firstName, lastName);
    }
}
