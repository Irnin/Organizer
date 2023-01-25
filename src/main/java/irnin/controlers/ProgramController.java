package irnin.controlers;

import irnin.classes.*;
import irnin.organizer.QueryExecutor;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.*;

import static irnin.classes.CalendarEvent.getCalendarEventsForUser;

public class ProgramController {
    @FXML private ListView<String> groupsList;
    @FXML private ListView<String> usersList;
    @FXML ListView<String> toDoItemsList;
    @FXML ListView<String> toDoItemsListComplete;
    @FXML private Button removeGroup;
    @FXML private Button addUser;
    @FXML private Button addToDoItem;
    @FXML private Button removeToDoItem;
    @FXML private Button markToDoItemComplete;
    @FXML private Button removeUser;
    @FXML private Button leftGroup;
    @FXML private ComboBox toDoGroup;
    @FXML private Label completedBy;
    @FXML private Label CompletedDate;
    @FXML private Label calendarMonth;
    @FXML private Label calendarYear;
    @FXML private VBox toDoInformation;
    private MainController mainController;
    public User user;
    private Cal calendar;

    @FXML private Button bc1;
    @FXML private Button bc2;
    @FXML private Button bc3;
    @FXML private Button bc4;
    @FXML private Button bc5;
    @FXML private Button bc6;
    @FXML private Button bc7;
    @FXML private Button bc8;
    @FXML private Button bc9;
    @FXML private Button bc10;
    @FXML private Button bc11;
    @FXML private Button bc12;
    @FXML private Button bc13;
    @FXML private Button bc14;
    @FXML private Button bc15;
    @FXML private Button bc16;
    @FXML private Button bc17;
    @FXML private Button bc18;
    @FXML private Button bc19;
    @FXML private Button bc20;
    @FXML private Button bc21;
    @FXML private Button bc22;
    @FXML private Button bc23;
    @FXML private Button bc24;
    @FXML private Button bc25;
    @FXML private Button bc26;
    @FXML private Button bc27;
    @FXML private Button bc28;
    @FXML private Button bc29;
    @FXML private Button bc30;
    @FXML private Button bc31;
    @FXML private Button bc32;
    @FXML private Button bc33;
    @FXML private Button bc34;
    @FXML private Button bc35;
    @FXML private Button bc36;
    @FXML private Button bc37;
    @FXML private Button bc38;
    @FXML private Button bc39;
    @FXML private Button bc40;
    @FXML private Button bc41;
    @FXML private Button bc42;
    @FXML private VBox eventsList;
    @FXML private TextField CalendarSubject;
    @FXML private ComboBox calendarGroup;
    @FXML private Pane createNewEvent;
    @FXML private Button calendarAddEvent;

    List<CalendarEvent> events;

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
        refreshToDoData();
    }

    // ================================================================
    // Zarządzanie grupami
    // ================================================================

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

    public void removeGroup() {
        selectedGroup.removeGroup();

        user.userGroups.removeIf(obj -> obj.id == selectedGroup.id);
        refrestGroupsData();
    }

    @FXML private void addUser() throws SQLException {
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
        displayUsersInGroup(selectedGroup.id);
    }

    @FXML private void removeUser() throws SQLException {
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
        displayUsersInGroup(selectedGroup.id);
    }

    public void leftGroup() {
        String query = String.format("DELETE FROM groupAssignment WHERE employeeId = %d AND groupId = %d", user.id, selectedGroup.id);
        QueryExecutor.executeQuery(query);
        user.userGroups.removeIf(obj -> obj.id == selectedGroup.id);
        refrestGroupsData();
    }

    private void getGroupDetail(String name) {
        for(Group group : user.userGroups){
            if(group.name == name) {
                selectedGroup = new Group(group.id, group.name, group.ownerId);
            }
        }
    }

    // ================================================================
    // Zarządzanie listą To Do
    // ================================================================

    public void toDoDisplayLists() throws SQLException {
        List<ToDoItem> list = selectedGroup.getToDos();

        toDoItemsListComplete.getItems().clear();
        toDoItemsList.getItems().clear();

        for(ToDoItem item : list) {
            if(item.isComplete()) {
                toDoItemsListComplete.getItems().add(item.getSubject());
            }
            else {
                toDoItemsList.getItems().add(item.getSubject());
            }
        }
    }

    public void addToDoItem() throws SQLException {
        TextInputDialog inputText = new TextInputDialog();
        inputText.setTitle("");
        inputText.setHeaderText("Dodawanie nowej rzeczy do listy");
        inputText.setContentText("Podaj tytuł: ");

        Optional<String> inputSubject = inputText.showAndWait();

        String query = String.format("SELECT id FROM toDoList WHERE groupId = %d AND subject = '%s';", selectedGroup.id, inputSubject.get());
        ResultSet result = QueryExecutor.executeSelect(query);
        result.beforeFirst();

        if(result.next() == true) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Wystąpił problem");
            alert.setHeaderText("Nie można dodać do listy");
            alert.setContentText("Taki wpis już istnieje");

            alert.showAndWait();
            return;
        }

        selectedGroup.addToDoItem(inputSubject.get());
        refreshToDoData();
    }

    public void removeToDoItem() {
        String itemName = toDoItemsList.getSelectionModel().getSelectedItem();
        ToDoItem.removeItem(selectedGroup.id, itemName);
        refreshToDoData();
    }

    public void markToDoItemComplete() {
        String itemName = toDoItemsList.getSelectionModel().getSelectedItem();
        ToDoItem.markItemAsComplete(selectedGroup.id, itemName, user.id);
        refreshToDoData();
    }

    // ================================================================
    // Zarządzanie Kalendarzem
    // ================================================================

    @FXML private void subtractMonth() throws ParseException, SQLException {
        calendar.subtractMonth();
        calendar.clearDay();
        refreshCalendarData();
    }

    @FXML private void addMonth() throws ParseException, SQLException {
        calendar.addMonth();
        calendar.clearDay();
        refreshCalendarData();
    }

    @FXML private void displayEvents(ActionEvent ae) throws ParseException, SQLException {
        int day = Integer.parseInt(((Button)ae.getSource()).getText());
        calendar.setDay(day);
        refreshCalendarData();
    }

    @FXML private void addEvent() throws SQLException, ParseException {
        //CalendarEvent.addEvent(user, calendar.getDate());
        refreshCalendarData();
    }

    // ================================================================
    // Aktualizacja zawartości zakładek
    // ================================================================
    private void displayUsersInGroup(int groupId) throws SQLException {
        usersList.getItems().clear();

        for(String user : selectedGroup.getUsers()){
            usersList.getItems().add(user);
        }
    }
    @FXML private void refrestGroupsData() {
        groupsList.getItems().clear();

        for(Group group : user.userGroups) {
            groupsList.getItems().add(group.name);
        }

        groupsList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                String name = groupsList.getSelectionModel().getSelectedItem();

                removeGroup.setDisable(true);
                addUser.setDisable(true);
                leftGroup.setDisable(true);
                removeUser.setDisable(true);

                getGroupDetail(name);

                try {
                    displayUsersInGroup(selectedGroup.id);
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

    @FXML private void refreshToDoData() {

        if(user == null) {
            return;
        }

        toDoGroup.getItems().clear();
        for(Group group : user.userGroups) {
            toDoGroup.getItems().add(group.name);
        }

        if(selectedGroup != null) {
            toDoGroup.setValue(selectedGroup.name);
            addToDoItem.setDisable(false);
        }
        else {
            addToDoItem.setDisable(true);
        }

        toDoGroup.setOnAction((event) -> {
            getGroupDetail((String)toDoGroup.getValue());
            toDoItemsList.getSelectionModel().clearSelection();
            toDoItemsListComplete.getSelectionModel().clearSelection();

            removeToDoItem.setDisable(true);
            markToDoItemComplete.setDisable(true);
            toDoInformation.setVisible(false);

            try {
                addToDoItem.setDisable(false);
                toDoDisplayLists();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

        // Kliknięcie na item nie kompletny
        toDoItemsList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                String name = toDoItemsList.getSelectionModel().getSelectedItem();

                removeToDoItem.setDisable(false);
                markToDoItemComplete.setDisable(false);
                toDoInformation.setVisible(false);
            }
        });

        // Kliknięcie na item nie kompletny
        toDoItemsListComplete.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                String name = toDoItemsListComplete.getSelectionModel().getSelectedItem();

                toDoInformation.setVisible(true);

                String itemName = toDoItemsListComplete.getSelectionModel().getSelectedItem();

                try {
                    String createdDate = ToDoItem.getCompletedDate(selectedGroup.id, itemName);
                    String createdBy = ToDoItem.getCompletedUserName(selectedGroup.id, itemName);

                    completedBy.setText(createdBy);
                    CompletedDate.setText(createdDate);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }

            }
        });
    }

    public void refreshCalendarData() throws ParseException, SQLException {
        List <Button> buttons = Arrays.asList(bc1, bc2, bc3, bc4, bc5, bc6, bc7, bc8, bc9, bc10, bc11, bc12, bc13, bc14, bc15, bc16, bc17, bc18, bc19, bc20, bc21, bc22, bc23, bc24, bc25, bc26, bc27, bc28, bc29, bc30, bc31, bc32, bc33, bc34, bc35, bc36, bc37, bc38, bc39, bc40, bc41, bc42);

        eventsList.getChildren().clear();
        calendarGroup.getItems().clear();

        if(calendar == null) {
            calendar = new Cal();
        }

        if(calendar.getDay() != -1) {
            events = getCalendarEventsForUser(user, calendar.getDate());

            for(CalendarEvent event : events) {
                eventsList.getChildren().add(event.getEventView());
            }
        }

        int i = 1;
        int day = 1;
        int firstDay = calendar.getFirstDayNumber();
        int days = calendar.getLengthOfMonth();
        for(Button button : buttons) {

            button.setStyle("-fx-background-color: none;");

            if(day <= days && i >= firstDay) {

                if(day == calendar.getDay()) {
                    button.setStyle("-fx-background-color: lightBlue;");
                }

                button.setDisable(false);
                button.setText(Integer.toString(day));
                day += 1;
            }
            else {
                button.setDisable(true);
                button.setText(" ");
            }
            i += 1;
        }

        for(Group group : user.userGroups) {
            calendarGroup.getItems().add(group.name);
        }

        calendarMonth.setText(calendar.getMonth());
        calendarYear.setText(calendar.getYear());

        if(calendar.getDay() != 0) {
            createNewEvent.setDisable(false);
        }
        else {
            createNewEvent.setDisable(true);
        }
    }
}
