package application;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static DBConnection instance;
    private Connection connection;

    private final String url = "jdbc:mysql://localhost:3306/responsibility_tracker"; // DB name
    private final String user = "root"; // MySQL username
    private final String password = "upp@sh@na1!"; // MySQL password

    private DBConnection() {
        try {
            connection = DriverManager.getConnection(url, user, password);
            System.out.println("Database connected successfully");
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Cannot connect to database");
        }
    }

    public static DBConnection getInstance() {
        if (instance == null) instance = new DBConnection();
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }
}