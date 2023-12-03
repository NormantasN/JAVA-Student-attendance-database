package com.example.laboras4;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import java.io.*;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;


public class mainController implements Initializable {
    @FXML
    private TableView<Student> table;

    @FXML
    private TableColumn<Student, Integer> groupTable;

    @FXML
    private TableColumn<Student, Integer> studentTable;

    @FXML
    private TableColumn<Student, Character> attendanceTable;

    @FXML
    private TableColumn<Student, LocalDate> dataTable;
    @FXML
    private TextField groupText;

    @FXML
    private TextField studNumberText;

    @FXML
    private DatePicker attendanceDate;

    @FXML
    private CheckBox notPresent;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        groupTable.setCellValueFactory(new PropertyValueFactory<Student, Integer>("groupId"));
        studentTable.setCellValueFactory(new PropertyValueFactory<Student, Integer>("id"));
        dataTable.setCellValueFactory(new PropertyValueFactory<Student, LocalDate>("date"));
        attendanceTable.setCellValueFactory(new PropertyValueFactory<Student, Character>("presence"));

    }

    @FXML
    void submitText() {
        char present;
        if (notPresent.isSelected()) {
            present = 'n';
        } else {
            present = ' ';
        }
        Student student = new Student(Integer.parseInt(studNumberText.getText()),
                Integer.parseInt(groupText.getText()),
                attendanceDate.getValue(),
                present
        );
        ObservableList<Student> students = table.getItems();
        students.add(student);
        table.setItems(students);
    }

    @FXML
    void removeStudent() {
        int selectedID = table.getSelectionModel().getSelectedIndex();
        table.getItems().remove(selectedID);
    }


    @FXML
    private TextField filterGroupNumber;

    @FXML
    private TextField filterStudNumber;
    @FXML
    private DatePicker filterFromDate;
    @FXML
    private DatePicker filterToDate;
    @FXML
    private CheckBox filterAttendance;

    ObservableList<Student> primaryStudents;

    @FXML
    void filterStudents() {
        String studentIdFilter = filterStudNumber.getText().trim();
        String groupFilter = filterGroupNumber.getText().trim();
        LocalDate fromDate = filterFromDate.getValue();
        LocalDate toDate = filterToDate.getValue();
        boolean attendance = filterAttendance.isSelected();

        ObservableList<Student> students = table.getItems();
        primaryStudents = students;
        ObservableList<Student> filteredStudents = FXCollections.observableArrayList();
        if (attendance) {
            for (Student student : students) {
                if (matchesFilter(student, studentIdFilter, groupFilter) &&
                        isWithinDateRange(student, fromDate, toDate) && isAbsent(student)) {
                    filteredStudents.add(student);
                }
            }
        } else {
            for (Student student : students) {
                if (matchesFilter(student, studentIdFilter, groupFilter) &&
                        isWithinDateRange(student, fromDate, toDate)) {
                    filteredStudents.add(student);
                }
            }
        }


        table.setItems(filteredStudents);
    }

    private boolean matchesFilter(Student student, String studentIdFilter, String groupFilter) {
        if (!studentIdFilter.isEmpty() && !studentIdFilter.equals(String.valueOf(student.getId()))) {
            return false;
        } else if (!groupFilter.isEmpty() && !groupFilter.equals(String.valueOf(student.getGroupId()))) {
            return false;
        }

        return true;
    }

    private boolean isWithinDateRange(Student student, LocalDate startDate, LocalDate endDate) {
        LocalDate studentDate = student.getDate();
        return (startDate == null || studentDate.isEqual(startDate) || studentDate.isAfter(startDate)) &&
                (endDate == null || studentDate.isEqual(endDate) || studentDate.isBefore(endDate));
    }

    private boolean isAbsent(Student student) {
        return student.getPresence() == 'n';
    }


    @FXML
    void resetTable() {
        table.setItems(primaryStudents);
    }

    @FXML
    void generatePDF() throws IOException {
        ObservableList<Student> students = table.getItems();

        PDDocument document = new PDDocument();
        PDPage page = new PDPage(PDRectangle.A4);
        document.addPage(page);

        PDPageContentStream contentStream = new PDPageContentStream(document, page);
        contentStream.beginText();
        contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.TIMES_ROMAN), 18);
        contentStream.setLeading(16.0f);

        contentStream.newLineAtOffset(25, page.getMediaBox().getHeight() - 25);
        contentStream.showText("Studentu lankomumo sarasas");
        contentStream.newLine();
        contentStream.newLine();

        for (Student student : students) {
            String studentInfo = "Studento ID: " + student.getId() +
                    ", Grupe: " + student.getGroupId() +
                    ", Data: " + student.getDate() +
                    ", Lankomumas: " + student.getPresence();

            contentStream.showText(studentInfo);
            contentStream.newLine();
        }

        contentStream.endText();
        contentStream.close();

        document.save("student_attendance.pdf");
        document.close();

    }

    @FXML
    void loadExcel() throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"));
        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            DataSource dataSource = new ExcelDataSource();
            ObservableList<Student> excelStudents;
            excelStudents = dataSource.loadFromFile(selectedFile);
            table.setItems(excelStudents);
        }

    }

    @FXML
    void generateExcel() throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"));
        File selectedFile = fileChooser.showSaveDialog(null);

        if (selectedFile != null) {
            DataSource dataSource = new ExcelDataSource();
            ObservableList<Student> students = table.getItems();
            dataSource.saveToFile(selectedFile, students);
            System.out.println("Excel failas sukurtas");

        }

    }

    @FXML
    void loadCSV() throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            DataSource dataSource = new CsvDataSource();
            ObservableList<Student> csvStudents;
            csvStudents = dataSource.loadFromFile(selectedFile);
            table.setItems(csvStudents);
        }
    }

    @FXML
    void generateCSV() throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File selectedFile = fileChooser.showSaveDialog(null);

        if (selectedFile != null) {
            DataSource dataSource = new CsvDataSource();
            ObservableList<Student> students = table.getItems();
            dataSource.saveToFile(selectedFile, students);
            System.out.println("CSV failas sukurtas");
        }
    }


}