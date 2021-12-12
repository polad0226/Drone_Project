module com.cs420.group7.agribot {
    requires javafx.controls;
    requires javafx.fxml;
    requires drone;


    opens com.cs420.group7.agribot to javafx.fxml;
    exports com.cs420.group7.agribot;
}