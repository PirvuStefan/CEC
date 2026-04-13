module org.example.cec {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.dlsc.formsfx;
    requires org.apache.poi.ooxml;
    requires java.desktop;
    requires org.apache.poi.poi;
    requires com.fasterxml.jackson.databind;

    requires org.apache.commons.compress;
    requires org.apache.xmlbeans;
    requires org.apache.logging.log4j;

    uses org.apache.poi.ss.usermodel.WorkbookProvider;

    opens org.example.cec to javafx.fxml;
    exports org.example.cec;
}
