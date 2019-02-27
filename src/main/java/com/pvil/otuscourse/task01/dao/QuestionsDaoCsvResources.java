package com.pvil.otuscourse.task01.dao;

import com.opencsv.CSVReader;
import com.pvil.otuscourse.task01.domain.Question;
import com.pvil.otuscourse.task01.domain.Student;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class QuestionsDaoCsvResources implements QuestionsDao {
    @Override
    public List<Question> getQuestions() throws Exception {
        List<Question> list = new ArrayList<>();
        try (var csvReader = new CSVReader(new InputStreamReader(getClass().getClassLoader().getResourceAsStream("medical.csv")))) {
             String[] line;

             while ((line = csvReader.readNext()) != null) {
                 var question = new Question(line[0],
                        Arrays.asList(Arrays.copyOfRange(line, 2, line.length)),
                        Integer.parseInt(line[1]) - 1);

                 list.add(question);
             }
        }

        return list;
    }

    @Override
    public void storeTestingResult(Student student, int rating, boolean passed) {
        //nothing...
    }
}
