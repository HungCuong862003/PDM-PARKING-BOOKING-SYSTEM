package main.java.com.parkeasy.util;

import java.sql.*;

public class DatabaseConnection {

    private static Connection connection;
    private static Statement statement;
    private static ResultSet resultSet;
    private static PreparedStatement preparedStatement;
    private static final String URL = Constants.URL;
    private static final String USERNAME = Constants.USERNAME;
    private static final String PASSWORD = Constants.PASSWORD;

    private static DatabaseConnection instance = null;

    public static DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

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
                // Ensure the MySQL driver is loaded
                try {
                    Class.forName("com.mysql.cj.jdbc.Driver");
                } catch (ClassNotFoundException e) {
                    System.err.println("MySQL Driver not found: " + e.getMessage());
                    return null;
                }

                // Establish the connection
                connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                System.out.println("Database connection established successfully.");
            }
        } catch (SQLException e) {
            System.err.println("Error establishing connection: " + e.getMessage());
        }
        return connection;
    }

    public static Statement getStatement() {
        try {
            if (statement == null || statement.isClosed()) {
                Connection conn = getConnection();
                if (conn != null) {
                    statement = conn.createStatement();
                } else {
                    System.err.println("Cannot create statement: Connection is null.");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error creating statement: " + e.getMessage());
        }
        return statement;
    }

    public static PreparedStatement getPreparedStatement(String sql) {
        try {
            if (preparedStatement == null || preparedStatement.isClosed()) {
                Connection conn = getConnection();
                if (conn != null) {
                    preparedStatement = conn.prepareStatement(sql);
                } else {
                    System.err.println("Cannot create prepared statement: Connection is null.");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error creating prepared statement: " + e.getMessage());
        }
        return preparedStatement;
    }

    public static ResultSet getResultSet(String sql) {
        try {
            Statement stmt = getStatement();
            if (stmt != null) {
                resultSet = stmt.executeQuery(sql);
            } else {
                System.err.println("Cannot execute query: Statement is null.");
            }
        } catch (SQLException e) {
            System.err.println("Error executing query: " + e.getMessage());
        }
        return resultSet;
    }

    public static ResultSet getResultSet(PreparedStatement preparedStatement) {
        try {
            if (preparedStatement != null) {
                resultSet = preparedStatement.executeQuery();
            } else {
                System.err.println("Cannot execute query: PreparedStatement is null.");
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

    // use to test connection
    public static void showQuery(String query) {
        try {
            ResultSet rs = getResultSet(query);
            if (rs != null) {
                while (rs.next()) {
                    String ParkingID = rs.getString("ParkingID");
                    String ParkingAddress = rs.getString("ParkingAddress");
                    float CostOfParking = rs.getFloat("CostOfParking");
                    int NumberOfSlots = rs.getInt("NumberOfSlots");
                    int MaxDuration = rs.getInt("MaxDuration");
                    String Description = rs.getString("Description");
                    int AdminID = rs.getInt("AdminID");

                    // print
                    System.out.println("ParkingID: " + ParkingID);
                    System.out.println("ParkingAddress: " + ParkingAddress);
                    System.out.println("CostOfParking: " + CostOfParking);
                    System.out.println("NumberOfSlots: " + NumberOfSlots);
                    System.out.println("MaxDuration: " + MaxDuration);
                    System.out.println("Description: " + Description);
                    System.out.println("AdminID: " + AdminID);
                }
            } else {
                System.err.println("ResultSet is null. Query execution failed.");
            }
        } catch (SQLException e) {
            System.err.println("Error executing query: " + e.getMessage());
        }
    }
}