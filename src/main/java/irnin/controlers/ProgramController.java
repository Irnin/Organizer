package irnin.controlers;

import javafx.application.Platform;
import javafx.scene.control.ListView;
import javafx.fxml.FXML;

public class ProgramController {
    @FXML
    private ListView<String> groupsList;
    private MainController mainController;
    public void logOut(){
        mainController.loadLoginScreen();
    }

    public void exit(){
        Platform.exit();
    }

    public void initialize() {

        groupsList.getItems().addAll();
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }
}
