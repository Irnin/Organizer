package irnin.organizer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbConnector {

    private static String URL = "jdbc:mariadb://192.168.192.42:3306/bies";
    private static String USER = "root";
    private static String PASSWORD = "Aa444444";

    public static Connection connect() {
        Connection connection = null;

        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Connected");
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        return connection;
    }
}
