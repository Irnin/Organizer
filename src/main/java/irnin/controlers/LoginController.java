package irnin.controlers;

import irnin.organizer.Main;
import irnin.organizer.QueryExecutor;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginController {
    @FXML
    private Label loginInfo;
    @FXML
    private TextField email;
    @FXML
    private PasswordField password;
    private MainController mainController;


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

            try {
                FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("ProgramView.fxml"));
                Pane pane = fxmlLoader.load();

                ProgramController programController = fxmlLoader.getController();
                programController.setMainController(mainController);
                mainController.setScreen(pane);
            } catch (IOException IOe) {
                IOe.printStackTrace();
            }

        } catch (SQLException er)
        {
            loginInfo.setText("Incorrect Login");
            er.printStackTrace();
        }
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }
}
