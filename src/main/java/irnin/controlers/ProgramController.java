package irnin.controlers;

import irnin.classes.Group;
import irnin.classes.User;
import irnin.organizer.QueryExecutor;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.fxml.FXML;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

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
    private Button removeUser;
    @FXML
    private Button leftGroup;
    @FXML
    private ComboBox toDoGroup;
    private MainController mainController;
    public User user;

    private Group selectedGroup;

    public void logOut(){

        mainController.loadLoginScreen();
    }

    public void exit(){
        Platform.exit();
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

                user.userGroups.add(new Group(gropuName.get()));
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

    private void displayUsers(int groupId) throws SQLException {
        usersList.getItems().clear();

        for(String user : selectedGroup.getUsers()){
            usersList.getItems().add(user);
        }
    }

    public void removeGroup() {
        selectedGroup.removeGroup();

        user.userGroups.removeIf(obj -> obj.id == selectedGroup.id);
        reload();
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

        // Sprawdzenie, czy użytkownik nie jest już członkiem grupy
        int userId = result.getInt("id");

        query = String.format("SELECT e.id FROM employees e JOIN groupAssignment gA on e.id = gA.employeeId WHERE gA.groupId = %d AND userName = '%s';", selectedGroup.id, userName);
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

        selectedGroup.addUser(userId);
        displayUsers(selectedGroup.id);
    }


    @FXML
    private void removeUser() throws SQLException {
        String inputUserName = usersList.getSelectionModel().getSelectedItem();

        String query = String.format("SELECT id FROM employees WHERE CONCAT(name, ' ', surname) = '%s'", inputUserName);
        ResultSet result = QueryExecutor.executeSelect(query);
        result.next();
        int userId = result.getInt("id");

        if(userId == selectedGroup.ownerId) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Wystąpił problem");
            alert.setHeaderText("Nie można usunąć użytkownika");
            alert.setContentText("Nie można usunąć właściciela grupy");

            alert.showAndWait();
            return;
        }

        selectedGroup.removeUser(userId);
        displayUsers(selectedGroup.id);
    }

    public void leftGroup() {
        String query = String.format("DELETE FROM groupAssignment WHERE employeeId = %d AND groupId = %d", user.id, selectedGroup.id);
        QueryExecutor.executeQuery(query);
        reload();
    }

    private void setGroupDetail(String name) {
        for(Group group : user.userGroups){
            if(group.name == name) {
                selectedGroup = new Group(group.id, group.name, group.ownerId);
            }
        }
    }

    private void getGroupsDetails() {
        groupsList.getItems().clear();

        for(Group group : user.userGroups) {
            groupsList.getItems().add(group.name);
            toDoGroup.getItems().add(group.name);
        }
    }

    private void reload() {
        getGroupsDetails();

        toDoGroup.setOnAction((event) -> {
            setGroupDetail((String)toDoGroup.getValue());
        });

        groupsList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                String name = groupsList.getSelectionModel().getSelectedItem();

                removeGroup.setDisable(true);
                addUser.setDisable(true);
                leftGroup.setDisable(true);
                removeUser.setDisable(true);

                setGroupDetail(name);

                try {
                    displayUsers(selectedGroup.id);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }

                if(selectedGroup.ownerId == user.id) {
                    removeGroup.setDisable(false);
                    addUser.setDisable(false);
                    removeUser.setDisable(false);
                }
                else {
                    leftGroup.setDisable(false);
                }
            }
        });
    }

    private void refrestGroupsData() {

    }

    public void toDoSelectGroup(ActionEvent actionEvent) {

    }
}
