module com.example.applicationform {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;

    opens com.example.applicationform to javafx.fxml;
    exports com.example.applicationform;
}