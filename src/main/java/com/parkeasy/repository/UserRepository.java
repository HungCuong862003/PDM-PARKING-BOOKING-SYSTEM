package main.java.com.parkeasy.repository;

import main.java.com.parkeasy.model.User;
import main.java.com.parkeasy.util.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserRepository {
    private String sql;
    private PreparedStatement preparedStatement;
    private ResultSet resultSet;
    private Connection databaseConnection;
    private Statement statement;

    // Method to save a user
    public void saveUser(User user) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = DatabaseConnection.getConnection();
            sql = "INSERT INTO user (userName, phone, email, password) VALUES (?, ?, ?, ?)";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, user.getUserName());
            preparedStatement.setString(2, user.getPhone());
            preparedStatement.setString(3, user.getEmail());
            preparedStatement.setString(4, user.getPassword());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeStatement(preparedStatement);
            DatabaseConnection.closeConnection(connection);
        }
    }

    // find all user
    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            connection = DatabaseConnection.getConnection();
            statement = connection.createStatement();
            sql = "SELECT * FROM user";
            resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                int userID = resultSet.getInt("userID");
                String userName = resultSet.getString("userName");
                String phone = resultSet.getString("phone");
                String email = resultSet.getString("email");
                String password = resultSet.getString("password");
                User user = new User(userID, phone, userName, email, password);
                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeResultSet(resultSet);
            DatabaseConnection.closeStatement(statement);
            DatabaseConnection.closeConnection(connection);
        }
        return users;
    }

    // Find user by email
    public Optional<User> findByEmail(String email) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = DatabaseConnection.getConnection();
            sql = "SELECT * FROM user WHERE email = ?";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, email);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                int userID = resultSet.getInt("userID");
                String userName = resultSet.getString("userName");
                String phone = resultSet.getString("phone");
                String password = resultSet.getString("password");
                return Optional.of(new User(userID, phone, userName, email, password));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeResultSet(resultSet);
            DatabaseConnection.closeStatement(preparedStatement);
            DatabaseConnection.closeConnection(connection);
        }
        return Optional.empty();
    }
}