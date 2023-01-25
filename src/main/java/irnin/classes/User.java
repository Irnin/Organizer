package irnin.classes;

import irnin.organizer.QueryExecutor;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class User {
    public int id;
    public String name;
    private String surname;
    private String email;
    public String userName;

    public List<Group> userGroups;

    private void reloadUsersGroups() throws SQLException {
        String query = "SELECT g.id, g.name, g.ownerId FROM groupAssignment ga JOIN groups g ON ga.groupId = g.id WHERE ga.employeeId = " + id;
        ResultSet result = QueryExecutor.executeSelect(query);

        result.beforeFirst();

        userGroups.clear();
        while(result.next()) {
            int groupId = result.getInt("id");
            String groupName = result.getString("name");
            int groupOwnerId = result.getInt("ownerId");

            userGroups.add(new Group(groupId, groupName, groupOwnerId));
        }
    }

    public List<Group> getUserGroups() throws SQLException {
        reloadUsersGroups();
        return userGroups;
    }

    public User(String userName, String hashedPassword) throws SQLException {
        Connection connection = null;

        String query = "SELECT id, name, surname, email FROM employees WHERE userName = '" + userName + "' AND password = '" + hashedPassword + "'";
        ResultSet result = QueryExecutor.executeSelect(query);

        result.next();

        this.userName = userName;
        this.id = result.getInt("id");
        this.name = result.getString("name");
        this.surname = result.getString("surname");
        this.email = result.getString("email");

        userGroups = new ArrayList<Group>();
        reloadUsersGroups();
    }
}
