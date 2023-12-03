package com.example.laboras4;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.io.*;
import java.util.Date;
import java.time.LocalDate;
import java.time.ZoneId;


public class ExcelDataSource implements DataSource {

    @Override
    public ObservableList<Student> loadFromFile(File file) throws IOException {
        ObservableList<Student> excelStudents = FXCollections.observableArrayList();

        try (FileInputStream fis = new FileInputStream(file); Workbook wb = new XSSFWorkbook(fis)) {
            Sheet sheet = wb.getSheetAt(0);

            for (Row row : sheet) {
                int groupId = (int) row.getCell(0).getNumericCellValue();
                int studentId = (int) row.getCell(1).getNumericCellValue();
                Date dateValue = row.getCell(2).getDateCellValue();
                LocalDate date = dateValue.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                Cell presenceCell = row.getCell(3);
                char presence = ' ';

                if (presenceCell != null) {
                    if (presenceCell.getCellType() == CellType.STRING) {
                        String presenceStr = presenceCell.getStringCellValue();
                        if (!presenceStr.isEmpty()) {
                            presence = presenceStr.charAt(0);
                        }
                    }
                }

                Student stud = new Student(groupId, studentId, date, presence);
                excelStudents.add(stud);
            }
        }

        return excelStudents;
    }

    @Override
    public void saveToFile(File file, ObservableList<Student> students) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(file); Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Studentu lankomumas");


            for (int i = 0; i < students.size(); i++) {
                Student student = students.get(i);
                Row dataRow = sheet.createRow(i + 1);

                dataRow.createCell(0).setCellValue(student.getId());
                dataRow.createCell(1).setCellValue(student.getGroupId());

                LocalDate studentDate = student.getDate();
                dataRow.createCell(2).setCellValue(Date.from(studentDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));

                dataRow.createCell(3).setCellValue(student.getPresence() == 'n' ? "n" : " ");
            }

            workbook.write(fos);
        }
    }
}
