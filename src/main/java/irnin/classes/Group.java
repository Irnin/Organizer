package irnin.classes;

import irnin.organizer.QueryExecutor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Group {
    public int id;
    public String name;
    public int ownerId;

    public Group(int id, String name, int ownerId)
    {
        this.id = id;
        this.name = name;
        this.ownerId = ownerId;
    }

    public Group(String name) throws SQLException {
        String query = String.format("SELECT id, ownerId FROM groups WHERE name = '%s'", name);
        ResultSet RS = QueryExecutor.executeSelect(query);
        RS.next();

        this.id = RS.getInt("id");
        this.name = name;
        this.ownerId = RS.getInt("ownerId");
    }

    public List<String> getUsers() throws SQLException {
        List<String> users = new ArrayList<String>();

        String query = String.format("SELECT name, surname FROM employees e JOIN groupAssignment gA on e.id = gA.employeeId WHERE gA.groupId = %d", id);
        ResultSet RS = QueryExecutor.executeSelect(query);
        RS.beforeFirst();

        while(RS.next()) {
            String userName = RS.getString("name");
            String userSurname = RS.getString("surName");
            users.add(userName + " " + userSurname);
        }

        return users;
    }

    public void removeGroup() {
        String query = String.format("DELETE FROM groupAssignment WHERE groupId = %d", id);
        QueryExecutor.executeQuery(query);

        query = String.format("DELETE FROM groups WHERE id = %d", id);
        QueryExecutor.executeQuery(query);

        query = String.format("DELETE FROM toDoList WHERE groupId = %d", id);
        QueryExecutor.executeQuery(query);
    }

    public void addUser(int userId) {
        String query = String.format("INSERT INTO groupAssignment VALUES (null, %d, %d)", id, userId);
        QueryExecutor.executeQuery(query);
    }

    public int removeUser(int userId) {
        if(userId == ownerId) {
            return -1;
        }
        else {
            String query = String.format("DELETE FROM groupAssignment WHERE employeeId = %d AND groupId = %d", userId, id);
            QueryExecutor.executeQuery(query);
            return 1;
        }
    }
}
