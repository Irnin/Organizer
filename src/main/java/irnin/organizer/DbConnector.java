package irnin.organizer;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DbConnector {
    public static Connection connect() {

        Connection connection = null;
        String url = "jdbc:mariadb://192.168.192.42:3306/bies";
        Properties info = new Properties();
        info.put("user", "root");
        info.put("password", "Aa444444");


        try {

            connection = DriverManager.getConnection(url, info);
            System.out.println("Połączono");
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        return connection;
    }
}
