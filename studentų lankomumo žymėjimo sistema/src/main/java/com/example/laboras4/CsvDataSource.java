package com.example.laboras4;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


public class CsvDataSource implements DataSource {

    @Override
    public ObservableList<Student> loadFromFile(File file) throws IOException {
        ObservableList<Student> csvStudents = FXCollections.observableArrayList();

        try (CSVReader reader = new CSVReader(new FileReader(file))) {
            String[] nextLine;
            while ((nextLine = reader.readNext()) != null) {
                int groupId = Integer.parseInt(nextLine[0]);
                int studentId = Integer.parseInt(nextLine[1]);
                LocalDate date = LocalDate.parse(nextLine[2]);
                char presence = nextLine[3].charAt(0);

                Student stud = new Student(studentId, groupId, date, presence);
                csvStudents.add(stud);
            }
        } catch (CsvValidationException e) {
            throw new IOException(e);
        }

        return csvStudents;
    }

    @Override
    public void saveToFile(File file, ObservableList<Student> students) throws IOException {
        CSVWriter writer = new CSVWriter(new FileWriter(file));
            List<String[]> data = new ArrayList<>();

            for (Student student : students) {
                String[] record = {
                        String.valueOf(student.getGroupId()),
                        String.valueOf(student.getId()),
                        student.getDate().toString(),
                        String.valueOf(student.getPresence())
                };
                data.add(record);
            }

            writer.writeAll(data);

    }
}


