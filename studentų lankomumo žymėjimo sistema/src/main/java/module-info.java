module com.example.laboras4 {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.apache.pdfbox;
    requires org.apache.poi.poi;
    requires org.apache.poi.ooxml;
    requires com.opencsv;


    opens com.example.laboras4 to javafx.fxml;
    exports com.example.laboras4;
}