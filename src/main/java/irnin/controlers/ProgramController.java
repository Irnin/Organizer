package irnin.controlers;

import irnin.classes.Group;
import irnin.classes.User;
import irnin.organizer.QueryExecutor;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.*;
import javafx.fxml.FXML;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

import static java.lang.String.valueOf;

public class ProgramController {
    @FXML
    private ListView<String> groupsList;
    @FXML
    private ListView<String> usersList;
    @FXML
    private Button removeGroup;
    @FXML
    private Button addUser;
    @FXML
    private Label debug;
    private MainController mainController;
    public User user;

    //Grupy
    private int groupId;
    private int groupOwnerId;
    private int selectedGroupId;
    private int groupMenuId;

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

                groupsList.getItems().add(gropuName.get());
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

    private void displayUsers(int groupId)
    {
        usersList.getItems().clear();
        String query = String.format("SELECT name, surname FROM employees e JOIN groupAssignment gA on e.id = gA.employeeId WHERE gA.groupId = %d", groupId);
        ResultSet RS = QueryExecutor.executeSelect(query);
        try {
            RS.beforeFirst();

            while(RS.next()) {
                String userName = RS.getString("name");
                String userSurname = RS.getString("surName");
                usersList.getItems().add(userName + " " + userSurname);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void removeGroup()
    {
        String query = String.format("DELETE FROM groupAssignment WHERE groupId = %d", groupId);
        QueryExecutor.executeQuery(query);

        query = String.format("DELETE FROM groups WHERE id = %d", groupId);
        QueryExecutor.executeQuery(query);

        groupsList.getItems().remove(groupMenuId);
    }

    @FXML
    private void addUser() throws SQLException {
        TextInputDialog inputText = new TextInputDialog();
        inputText.setTitle("");
        inputText.setHeaderText("Dodawanie nowego użytkownika");
        inputText.setContentText("Nazwa użytkownika: ");

        Optional<String> inputUserName = inputText.showAndWait();
        String userName = inputUserName.get();

        // Sprawdzenie, czy istnieje taki użytkownik
        String query = String.format("SELECT id FROM employees e WHERE e.userName = '%s'", userName);
        ResultSet result = QueryExecutor.executeSelect(query);
        result.beforeFirst();

        if(result.next() == false) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Wystąpił problem");
            alert.setHeaderText("Nie można dodać użytkownika");
            alert.setContentText("Nie istnieje użytkownik z takim loginem");

            alert.showAndWait();
            return;
        }

        int userId = result.getInt("id");

        query = String.format("SELECT e.id FROM employees e JOIN groupAssignment gA on e.id = gA.employeeId WHERE gA.groupId = %d AND userName = '%s';", groupId, userName);
        result = QueryExecutor.executeSelect(query);
        result.beforeFirst();

        if(result.next() == true) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Wystąpił problem");
            alert.setHeaderText("Nie można dodać użytkownika");
            alert.setContentText("Użytkownik już jest członkiem grupy");

            alert.showAndWait();
            return;
        }

        query = String.format("INSERT INTO groupAssignment VALUES (null, %d, %d)", groupId, userId);
        QueryExecutor.executeQuery(query);
        displayUsers(groupId);
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

                removeGroup.setVisible(false);
                addUser.setVisible(false);

                String query = String.format("SELECT id, name, ownerId FROM groups WHERE name = '%s'", name);
                ResultSet RS = QueryExecutor.executeSelect(query);
                try {
                    RS.next();
                    groupId = RS.getInt("id");
                    groupOwnerId = RS.getInt("ownerId");
                    groupMenuId = groupsList.getSelectionModel().getSelectedIndex();

                    debug.setText(valueOf(groupId));

                    displayUsers(groupId);

                    if(RS.getInt("ownerId") == user.id) {
                        removeGroup.setVisible(true);
                        addUser.setVisible(true);
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }

            }
        });
    }
}
