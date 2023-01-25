package irnin.classes;

import irnin.organizer.QueryExecutor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.util.Pair;

public class CalendarEvent {
    private int id;
    private String groupName;
    private String subject;
    private String date;
    private boolean edited;

    public CalendarEvent(int id, String groupName, String subject, String date) {
        this.id = id;
        this.groupName = groupName;
        this.subject = subject;
        this.date = date;
        edited = false;
    }

    public String getSubject() {
        return subject;
    }

    private void removeEvent() {
        String query = String.format("DELETE FROM calendarEvents WHERE id = %d", id);
        QueryExecutor.executeQuery(query);
        edited = true;
    }

    private void editEvent() {
        Dialog<String> dialog = new Dialog<>();
        dialog.setHeaderText("Edycja rekordy");

        ButtonType loginButtonType = new ButtonType("Edytuj", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().add(loginButtonType);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField newSubject = new TextField();
        newSubject.setText(subject);

        TextField newTime = new TextField();

        grid.add(new Label("Tytuł:"), 0, 0);
        grid.add(newSubject, 1, 0);

        Node updateButton = dialog.getDialogPane().lookupButton(loginButtonType);
        updateButton.setDisable(true);

        newSubject.textProperty().addListener((observable, oldValue, newValue) -> {
            updateButton.setDisable(newValue.trim().isEmpty());
        });

        dialog.getDialogPane().setContent(grid);

        Platform.runLater(() -> newSubject.requestFocus());

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == loginButtonType) {
                return newSubject.getText();
            }
            return null;
        });

        Optional<String> result = dialog.showAndWait();

        result.ifPresent(data -> {
            String query = String.format("UPDATE calendarEvents SET subject='%s' WHERE id = %d", data, id);
            QueryExecutor.executeQuery(query);
            edited = true;
        });
    }

    public VBox getEventView() {
        VBox view = new VBox(10);
        Label subjectView = new Label(subject);
        Label details = new Label(groupName);
        Button removeEvent = new Button("Usuń");
        Button editEvent = new Button("Edytuj");

        removeEvent.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                removeEvent();
            }
        });

        editEvent.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                editEvent();
            }
        });

        view.getChildren().add(subjectView);
        view.getChildren().add(details);
        view.getChildren().add(removeEvent);
        view.getChildren().add(editEvent);

        view.setStyle("-fx-background-color: lightBlue; -fx-padding: 5;");

        return view;
    }

    public static List<CalendarEvent> getCalendarEventsForUser(User user, String date) throws SQLException {
        List<CalendarEvent> events = new ArrayList<CalendarEvent>();

        String query = String.format("SELECT name, date, subject, cE.id FROM groupAssignment ga JOIN calendarEvents cE on ga.groupId = cE.groupId JOIN groups g ON g.id = ga.groupId WHERE ga.employeeId = %d AND date = '%s'", user.id, date);
        ResultSet RS = QueryExecutor.executeSelect(query);
        RS.beforeFirst();

        while(RS.next()) {

            int id = RS.getInt("id");
            String subject = RS.getString("subject");
            String name = RS.getString("name");
            String eventDate = RS.getString("date");

            events.add(new CalendarEvent(id, name, subject, eventDate));
        }

        return events;
    }
}
