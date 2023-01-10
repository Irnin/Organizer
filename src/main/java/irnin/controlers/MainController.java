package irnin.controlers;

import irnin.organizer.QueryExecutor;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MainController {
    @FXML
    private Label welcomeText;
    @FXML
    private TextField email;
    @FXML
    private PasswordField password;

    @FXML
    void login(ActionEvent e){
        Connection connection = null;
        String userLogin = email.getText();
        String userPassword = password.getText();
        String query = "SELECT * FROM employees WHERE userName = '" + userLogin + "' AND password = '" + userPassword + "'";

        try {
            ResultSet result = QueryExecutor.executeSelect(query);

            result.next();
            String userEmail = result.getString("email");

            System.out.println(userEmail);
        } catch (SQLException er)
        {
            er.printStackTrace();
        }

    }
}