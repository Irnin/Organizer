package irnin.controlers;

import irnin.classes.Group;
import irnin.classes.User;
import irnin.organizer.QueryExecutor;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.fxml.FXML;
import javafx.scene.control.TextInputDialog;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class ProgramController {
    @FXML
    private ListView<String> groupsList;

    @FXML
    private Label groupNameLabel;
    private MainController mainController;
    public User user;
    public void logOut(){
        mainController.loadLoginScreen();
    }

    public void exit(){
        Platform.exit();
    }

    public void initialize() {

    }

    public void setMainController(MainController mainController, User user) {
        this.mainController = mainController;
        this.user = user;

        reload();
    }

    public void addGroup() throws SQLException {
        boolean loop = true;
        TextInputDialog inputText = new TextInputDialog();
        inputText.setTitle("");
        inputText.setHeaderText("Tworzenie nowej grupy");
        inputText.setContentText("Nazwa grupy: ");

        Optional<String> gropuName = inputText.showAndWait();

        if(gropuName.isPresent()) {
            Connection connection = null;
            String query = String.format("SELECT id FROM groups WHERE name = '%s'", gropuName.get());

            ResultSet result = QueryExecutor.executeSelect(query);
            result.beforeFirst();

            if (result.next() == false){
                query = String.format("INSERT INTO groups VALUES (null, '%s', %d)", gropuName.get(), user.id);
                QueryExecutor.executeQuery(query);

                query = String.format("SELECT id FROM groups WHERE name = '%s'", gropuName.get());
                ResultSet RS1 = QueryExecutor.executeSelect(query);
                RS1.next();

                query = String.format("INSERT INTO groupAssignment VALUES (null, %d, %d)", RS1.getInt("id"), user.id);
                QueryExecutor.executeQuery(query);

            }
            else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Wystąpił problem");
                alert.setHeaderText("Nie można utworzyć nowej grupy");
                alert.setContentText("Grupa z podaną nazwą już istnieje");

                alert.showAndWait();
            }
        }
    }

    private void reload()
    {
        for(Group group : user.userGroups) {
            groupsList.getItems().add(group.name);
        }

        groupsList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                String name = groupsList.getSelectionModel().getSelectedItem();
                groupNameLabel.setText("Nazwa grupy: " + name);
            }
        });
    }
}
