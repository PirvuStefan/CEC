module org.example.cec {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.dlsc.formsfx;

    opens org.example.cec to javafx.fxml;
    exports org.example.cec;
}