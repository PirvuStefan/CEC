module org.example.cec {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.dlsc.formsfx;
    requires org.apache.poi.ooxml;

    opens org.example.cec to javafx.fxml;
    exports org.example.cec;
}