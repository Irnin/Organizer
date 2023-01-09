package irnin.controlers;

import irnin.organizer.QueryExecutor;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

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
        System.out.println("Login");
        System.out.println("Password: " + email.getText());
        System.out.println("Login: " + password.getText());

        try {
            ResultSet result = QueryExecutor.executeSelect("SELECT * FROM bies.users");

            result.next();
            String userEmail = result.getString("email");

            System.out.println(userEmail);
        } catch (SQLException er)
        {
            er.printStackTrace();
        }


    }
}