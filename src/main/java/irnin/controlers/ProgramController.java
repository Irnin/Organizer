package irnin.controlers;

import javafx.application.Platform;

public class ProgramController {
    private MainController mainController;
    public void logOut(){
        mainController.loadLoginScreen();
    }

    public void exit(){
        Platform.exit();
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }
}
