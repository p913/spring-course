package com.pvil.otuscourse.task01.dao;

import com.opencsv.CSVReader;
import com.pvil.otuscourse.task01.domain.Question;
import com.pvil.otuscourse.task01.domain.Student;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class QuestionsDaoCsvResources implements QuestionsDao {

    private final String resourceName;

    private final String resultFileName;

    public QuestionsDaoCsvResources(@Value("${dao.csv.resourceName}") String resourceName,
                                    @Value("${dao.resultFileName}") String resultFileName) {
        this.resourceName = resourceName;
        this.resultFileName = resultFileName;
    }

    @Override
    public List<Question> getQuestions() throws Exception {
        List<Question> list = new ArrayList<>();
        String localizedName = DaoUtils.getLocalizedResourceName(resourceName, "csv");
        if (localizedName == null)
            throw new DaoException("Resource " + resourceName + " not found.");

        try (var csvReader = new CSVReader(new InputStreamReader(getClass().getResourceAsStream(localizedName)))) {
             String[] line;

             while ((line = csvReader.readNext()) != null) {
                 if (line.length < 4)
                     throw new DaoException("Expected in csv-line: a question, an index of correct variant, 2 or more variants.");

                 int correct;
                 try {
                     correct = Integer.parseInt(line[1]) - 1;
                     if (correct < 0 || correct >= line.length - 2)
                         throw new DaoException("The index of correct variant is out of bounds.");
                 } catch (NumberFormatException e) {
                     throw new DaoException("The index of correct variant in csv-line is not a number.", e);
                 }

                 var question = new Question(line[0],
                         Arrays.asList(Arrays.copyOfRange(line, 2, line.length)),
                         correct);

                 list.add(question);
             }
        }

        return list;
    }

    @Override
    public void storeTestingResult(Student student, int rating, boolean passed) throws IOException {
        try (var f = new FileWriter(resultFileName, true)) {
            f.write(String.format("%s %s (%d%%)\n", passed ? "+" : "-", student, rating));
        }
    }
}
