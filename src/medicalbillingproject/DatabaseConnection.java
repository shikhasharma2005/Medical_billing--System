package medicalbillingproject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    public static Connection getConnection() {

        Connection con = null;

        try {
            String url = "jdbc:mysql://localhost:3306/medicalbilling";
            String user = "root";
            String password = "root@123";

            con = DriverManager.getConnection(url, user, password);

            System.out.println("Connection established");

        } catch (SQLException e) {
            System.out.println("Connection failed");
            e.printStackTrace();
        }

        return con;
    }

    public static void main(String[] args) {
        getConnection();
    }
}



