package com.pvil.otuscourse.task01.service;

import com.pvil.otuscourse.task01.domain.Student;
import org.springframework.stereotype.Service;

@Service
public class LoginServiceDummy implements LoginService {

    @Override
    public Student login(String firstName, String lastName) {
        return new Student(firstName, lastName);
    }
}
