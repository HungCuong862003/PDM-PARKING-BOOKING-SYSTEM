package main.java.com.parkeasy.repository;

import javax.xml.crypto.Data;

import main.java.com.parkeasy.model.Admin;
import main.java.com.parkeasy.util.DatabaseConnection;

public class AdminRepository {

    private DatabaseConnection databaseConnection;
    private Data data;

    public void save(Admin admin) {
        String sql = "INSERT INTO teacher (TeacherName, PhoneNumber,Email) VALUES (?, ?, ?)";
        // Assuming you have a method to get a connection
        // connection = databaseConnection.getConnection();
        // 
    }

    public void deleteById(int adminID) {

    }

    public void update(Admin admin) {

    }
}
