module com.example.hackathon2023 {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.hackathon2023 to javafx.fxml;
    exports com.example.hackathon2023;
}