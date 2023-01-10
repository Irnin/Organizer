package irnin.controlers;

import irnin.organizer.Main;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

import java.io.IOException;

public class  MainController {
    @FXML
    private StackPane mainStackPane;

    public void initialize() {
        loadLoginScreen();
    }
    public void loadLoginScreen() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("LoginView.fxml"));
            Pane pane = fxmlLoader.load();
            LoginController loginController = fxmlLoader.getController();
            loginController.setMainController(this);
            setScreen(pane);
        } catch (IOException IOe) {
            IOe.printStackTrace();
        }
    }

    public void setScreen(Pane pane) {
        mainStackPane.getChildren().clear();
        mainStackPane.getChildren().add(pane);
    }
}