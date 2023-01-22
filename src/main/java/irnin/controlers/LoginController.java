package irnin.controlers;

import irnin.classes.User;
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
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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

    private String hash(String inputPassword) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");

            byte[] messageDigest = md.digest(inputPassword.getBytes());

            BigInteger no = new BigInteger(1, messageDigest);

            String hashedPassword = no.toString(16);

            while (hashedPassword.length() < 32) {
                hashedPassword = "0" + hashedPassword;
            }

            return hashedPassword;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void onEnter(ActionEvent ae){
        login(ae);
    }

    @FXML
    void login(ActionEvent e) {
        Connection connection = null;

        String userLogin = email.getText();
        String userPassword = hash(password.getText());

        try {
            String query = "SELECT * FROM employees WHERE userName = '" + userLogin + "' AND password = '" + userPassword + "'";
            ResultSet result = QueryExecutor.executeSelect(query);

            result.next();

            User user = new User(userLogin, userPassword);

            try {
                FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("ProgramView.fxml"));
                Pane pane = fxmlLoader.load();

                ProgramController programController = fxmlLoader.getController();
                programController.setMainController(mainController, user);
                mainController.setScreen(pane);
            } catch (IOException IOe) {
                IOe.printStackTrace();
            }

        } catch (SQLException er) {
            loginInfo.setText("Incorrect Login");
            er.printStackTrace();
        }
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }
}
