package main.java.com.parkeasy.repository;

import main.java.com.parkeasy.model.Transaction;
import main.java.com.parkeasy.util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Repository class for handling database operations related to transactions
 */
public class TransactionRepository {
    private static final Logger LOGGER = Logger.getLogger(TransactionRepository.class.getName());

    /**
     * Create a new transaction in the database
     * 
     * @param transaction Transaction object to create
     * @return true if successful, false otherwise
     */
    public boolean createTransaction(Transaction transaction) {
        String sql = "INSERT INTO TRANSACTION (Amount, ReservationID) VALUES (?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setFloat(1, transaction.getAmount());
            pstmt.setInt(2, transaction.getReservationID());
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        transaction.setTransactionID(rs.getInt(1));
                        return true;
                    }
                }
            }
            
            return false;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error creating transaction", e);
            return false;
        }
    }
    
    /**
     * Get a transaction by ID
     * 
     * @param transactionId ID of the transaction to get
     * @return Transaction object if found, null otherwise
     */
    public Transaction getTransactionById(int transactionId) {
        String sql = "SELECT * FROM TRANSACTION WHERE TransactionID = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, transactionId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToTransaction(rs);
                }
            }
            
            return null;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting transaction by ID: " + transactionId, e);
            return null;
        }
    }
    
    /**
     * Get transactions by reservation ID
     * 
     * @param reservationId ID of the reservation
     * @return List of transactions for the reservation
     */
    public List<Transaction> getTransactionsByReservationId(int reservationId) {
        String sql = "SELECT * FROM TRANSACTION WHERE ReservationID = ?";
        List<Transaction> transactions = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, reservationId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    transactions.add(mapResultSetToTransaction(rs));
                }
            }
            
            return transactions;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting transactions by reservation ID: " + reservationId, e);
            return transactions;
        }
    }
    
    /**
     * Get transactions for parking spaces managed by an admin
     * 
     * @param adminId ID of the admin
     * @return List of transactions
     */
    public List<Transaction> getTransactionsByAdminId(int adminId) {
        String sql = "SELECT t.* FROM TRANSACTION t " +
                "JOIN PARKING_RESERVATION r ON t.ReservationID = r.ReservationID " +
                "JOIN PARKING_SLOT s ON r.SlotNumber = s.SlotNumber " +
                "JOIN PARKING_SPACE ps ON s.ParkingID = ps.ParkingID " +
                "WHERE ps.AdminID = ?";
        
        List<Transaction> transactions = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, adminId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    transactions.add(mapResultSetToTransaction(rs));
                }
            }
            
            return transactions;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting transactions by admin ID: " + adminId, e);
            return transactions;
        }
    }
    // Add this method to TransactionRepository
    public Map<String, Float> calculateRevenueForAllParkingSpaces(int adminId, LocalDateTime startDate, LocalDateTime endDate) {
        Map<String, Float> revenueBySpace = new HashMap<>();
        String sql = "SELECT ps.ParkingID, SUM(t.Amount) AS Revenue FROM TRANSACTION t " +
                "JOIN PARKING_RESERVATION r ON t.ReservationID = r.ReservationID " +
                "JOIN PARKING_SLOT s ON r.SlotNumber = s.SlotNumber " +
                "JOIN PARKING_SPACE ps ON s.ParkingID = ps.ParkingID " +
                "WHERE ps.AdminID = ? AND r.CreatedAt BETWEEN ? AND ? " +
                "GROUP BY ps.ParkingID";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, adminId);
            pstmt.setTimestamp(2, Timestamp.valueOf(startDate));
            pstmt.setTimestamp(3, Timestamp.valueOf(endDate));

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    revenueBySpace.put(rs.getString("ParkingID"), rs.getFloat("Revenue"));
                }
            }

            return revenueBySpace;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error calculating batch revenue", e);
            return revenueBySpace;
        }
    }
    /**
     * Calculate total revenue for an admin
     * 
     * @param adminId ID of the admin
     * @return Total revenue
     */
    public float calculateTotalRevenueForAdmin(int adminId) {
        String sql = "SELECT SUM(t.Amount) AS TotalRevenue FROM TRANSACTION t " +
                "JOIN PARKING_RESERVATION r ON t.ReservationID = r.ReservationID " +
                "JOIN PARKING_SLOT s ON r.SlotNumber = s.SlotNumber " +
                "JOIN PARKING_SPACE ps ON s.ParkingID = ps.ParkingID " +
                "WHERE ps.AdminID = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, adminId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getFloat("TotalRevenue");
                }
            }
            
            return 0.0f;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error calculating total revenue for admin: " + adminId, e);
            return 0.0f;
        }
    }


    /**
     * Map a result set to a Transaction object
     * 
     * @param rs Result set
     * @return Transaction object
     * @throws SQLException if a database error occurs
     */
    private Transaction mapResultSetToTransaction(ResultSet rs) throws SQLException {
        Transaction transaction = new Transaction();
        transaction.setTransactionID(rs.getInt("TransactionID"));
        transaction.setAmount(rs.getFloat("Amount"));
        transaction.setReservationID(rs.getInt("ReservationID"));
        return transaction;
    }
}