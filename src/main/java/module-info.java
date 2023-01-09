module irnin.organizer {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens irnin.organizer to javafx.fxml;
    exports irnin.organizer;
    exports irnin.controlers;
    opens irnin.controlers to javafx.fxml;
}