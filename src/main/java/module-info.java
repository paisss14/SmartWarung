module com.mycompany.smartwarung {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.mycompany.smartwarung to javafx.fxml;
    exports com.mycompany.smartwarung;
}
