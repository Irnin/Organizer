package irnin.classes;

import irnin.organizer.QueryExecutor;

import java.sql.ResultSet;
import java.sql.SQLException;

public class toDo {
    int id;
    int groupId;
    String subject;
    String status;
    int completedBy;
    String completedDate;

    public toDo(int id, int groupId, String subject, String status, int completedBy, String completedDate) {
        this.id = id;
        this.groupId = groupId;
        this.subject = subject;
        this.status = status;
        this.completedBy = completedBy;
        this.completedDate = completedDate;
    }

    public boolean isComplete()
    {
        if(status.equals("w trakcie")) {
            return false;
        }
        else {
            return true;
        }
    }

    public String getSubject(){
        return subject;
    }

    public static void removeItem(int groupId, String subject) {
        String query = String.format("DELETE FROM toDoList WHERE groupId = %d AND subject = '%s'", groupId, subject);
        QueryExecutor.executeQuery(query);
    }

    public static void markItemAsComplete(int groupId, String subject, int userId) {
        String query = String.format("UPDATE toDoList SET status = 'ukonczono', completedBy = %d, completedDate = NOW() WHERE groupId = %d AND subject = '%s'", userId, groupId, subject);
        QueryExecutor.executeQuery(query);
    }

    public static String getCompletedDate(int groupId, String subject) throws SQLException {
        String query = String.format("SELECT completedDate FROM toDoList WHERE groupId = %d AND subject = '%s'", groupId, subject);
        ResultSet RS = QueryExecutor.executeSelect(query);

        RS.next();
        return RS.getString("completedDate");
    }

    public static String getCompletedUserName(int groupId, String subject) throws SQLException {
        String query = String.format("SELECT CONCAT(name, ' ', surname) AS 'fullName' FROM employees JOIN toDoList tDL on employees.id = tDL.completedBy WHERE groupId = %d AND subject = '%s'", groupId, subject);
        ResultSet RS = QueryExecutor.executeSelect(query);

        RS.next();
        return RS.getString("fullName");
    }
}
