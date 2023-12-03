package com.example.laboras4;
import javafx.collections.ObservableList;
import java.io.File;
import java.io.IOException;

public interface DataSource {
    ObservableList<Student> loadFromFile(File file) throws IOException;
    void saveToFile(File file, ObservableList<Student> students) throws IOException;
}