package com.example.hackathon2023;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.sql.SQLException;

public class CreateUserViewController {

    @FXML
    private TextField emailTextField;

    @FXML
    private Label msgLabel;

    @FXML
    private TextField userNameTextField;

    @FXML
    private TextField passwordTextField;

    @FXML
    void submitPressed(ActionEvent event) {
        if(!userNameTextField.getText().isEmpty() && !emailTextField.getText().isEmpty() && !passwordTextField.getText().isEmpty()) {
            //there is a username, email, and password in the fields
            try {
                User user = new User(emailTextField.getText(), userNameTextField.getText(), passwordTextField.getText());
                String msg = DBUtility.saveUserToDB(user);
                msgLabel.setText(msg);
            } catch (IllegalArgumentException e) {
                msgLabel.setText(e.getMessage());
            } catch (SQLException e) {
                msgLabel.setText("SQL Error: " + e.getMessage());
            } catch (Exception e) {
                msgLabel.setText("Unknown Error: " + e.getMessage());
            }
        } else {
            msgLabel.setText("Both user name and email are required.");
        }
    }

}
