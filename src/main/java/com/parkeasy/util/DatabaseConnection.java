package main.java.com.parkeasy.util;

import java.sql.*;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://127.0.0.1:3306/test"; // TODO: change to your database name
    private static final String USERNAME = "root"; // TODO: change to your database username
    private static final String PASSWORD = "password"; // TODO: change to your database password
    private static Connection connection;
    private static Statement statement;
    private static ResultSet resultSet;
    private static PreparedStatement preparedStatement;
    // query
    private static String query = "SELECT * FROM admin";

    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
    }

    public static void closeStatement(Statement statement) {
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                System.err.println("Error closing statement: " + e.getMessage());
            }
        }
    }

    public static void closePreparedStatement(PreparedStatement preparedStatement) {
        if (preparedStatement != null) {
            try {
                preparedStatement.close();
            } catch (SQLException e) {
                System.err.println("Error closing prepared statement: " + e.getMessage());
            }
        }
    }

    public static void closeResultSet(ResultSet resultSet) {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                System.err.println("Error closing result set: " + e.getMessage());
            }
        }
    }

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Error establishing connection: " + e.getMessage());
        }
        return connection;
    }

    public static Statement getStatement() {
        try {
            if (statement == null || statement.isClosed()) {
                statement = getConnection().createStatement();
            }
        } catch (SQLException e) {
            System.err.println("Error creating statement: " + e.getMessage());
        }
        return statement;
    }

    public static PreparedStatement getPreparedStatement(String sql) {
        try {
            if (preparedStatement == null || preparedStatement.isClosed()) {
                preparedStatement = getConnection().prepareStatement(sql);
            }
        } catch (SQLException e) {
            System.err.println("Error creating prepared statement: " + e.getMessage());
        }
        return preparedStatement;
    }

    public static ResultSet getResultSet(String sql) {
        try {
            if (resultSet == null || resultSet.isClosed()) {
                resultSet = getStatement().executeQuery(sql);
            }
        } catch (SQLException e) {
            System.err.println("Error executing query: " + e.getMessage());
        }
        return resultSet;
    }

    public static ResultSet getResultSet(PreparedStatement preparedStatement) {
        try {
            if (resultSet == null || resultSet.isClosed()) {
                resultSet = preparedStatement.executeQuery();
            }
        } catch (SQLException e) {
            System.err.println("Error executing prepared statement: " + e.getMessage());
        }
        return resultSet;
    }

    public static void closeAll() {
        closeConnection(connection);
        closeStatement(statement);
        closePreparedStatement(preparedStatement);
        closeResultSet(resultSet);
    }

    public static void showQuery(String query) {
        try {
            ResultSet rs = getResultSet(query);
            while (rs.isBeforeFirst()) {
                rs.next();
                int adminID = rs.getInt("adminID");
                String adminName = rs.getString("adminName");
                String phone = rs.getString("Phone");
                String email = rs.getString("Email");
                String password = rs.getString("Password");
                // Print the results
                System.out.println("admin ID: " + adminID);
                System.out.println("admin Name: " + adminName);
                System.out.println("Phone Number: " + phone);
                System.out.println("Email: " + email);
                System.out.println("Password: " + password);
            }
        } catch (SQLException e) {
            System.err.println("Error executing query: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        // Test the database connection
        Connection conn = DatabaseConnection.getConnection();
        if (conn != null) {
            System.out.println("Database connection established successfully.");
        } else {
            System.out.println("Failed to establish database connection.");
        }

        // test get info in database
        showQuery(query);

        DatabaseConnection.closeAll();
    }
}