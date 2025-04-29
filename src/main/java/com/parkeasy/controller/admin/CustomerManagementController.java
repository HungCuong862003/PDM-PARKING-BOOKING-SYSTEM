package main.java.com.parkeasy.controller.admin;

import main.java.com.parkeasy.model.Admin;
import main.java.com.parkeasy.model.User;
import main.java.com.parkeasy.service.AdminService;
import main.java.com.parkeasy.model.ParkingSpace;
import main.java.com.parkeasy.model.Reservation;
import main.java.com.parkeasy.service.ReservationService;
import main.java.com.parkeasy.service.UserService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller class for managing customer information and interactions.
 * Handles the interaction between the admin customer management interface and
 * the service layer.
 */
public class CustomerManagementController {

    private final AdminService adminService;
    private final UserService userService;
    private final ReservationService reservationService;
    private Admin currentAdmin;

    /**
     * Constructor for CustomerManagementController.
     *
     * @param currentAdmin The currently logged-in admin
     */
    public CustomerManagementController(Admin currentAdmin) throws SQLException {
        this.adminService = new AdminService();
        this.userService = new UserService();
        this.reservationService = new ReservationService();
        this.currentAdmin = currentAdmin;
    }

    /**
     * Gets all users in the system.
     *
     * @return List of all users
     */
    public List<User> getAllUsers() {
        return adminService.getAllUsers();
    }

    /**
     * Gets detailed information about a specific user.
     *
     * @param userID The ID of the user
     * @return The user if found, null otherwise
     */
    public User getUserById(int userID) {
        return userService.getUserById(userID);
    }

    /**
     * Gets all reservations for a specific user.
     *
     * @param userID The ID of the user
     * @return List of reservations made by the user
     */
    public List<Reservation> getUserReservations(int userID) {
        return reservationService.getReservationsByUser(userID);
    }

    /**
     * Gets all parking spaces managed by the current admin.
     *
     * @return List of parking spaces managed by the current admin
     */
    public List<ParkingSpace> getAdminParkingSpaces() {
        return adminService.getParkingSpacesByAdmin(currentAdmin.getAdminID());
    }

    /**
     * Gets all users who have made reservations at parking spaces managed by the
     * current admin.
     *
     * @return List of users who have made reservations at admin's parking spaces
     */
    public List<User> getCustomersWithReservations() {
        // Get all spaces managed by this admin
        List<ParkingSpace> adminSpaces = getAdminParkingSpaces();
        List<String> parkingIDs = new ArrayList<>();
        for (ParkingSpace space : adminSpaces) {
            parkingIDs.add(space.getParkingID());
        }

        // Get all reservations for these spaces
        List<Reservation> reservations = new ArrayList<>();
        for (String parkingID : parkingIDs) {
            reservations.addAll(reservationService.getReservationsByParkingSpace(parkingID));
        }

        // Get unique users from reservations
        Map<Integer, User> userMap = new HashMap<>();
        for (Reservation reservation : reservations) {
            int userID = reservation.getUserID();
            if (!userMap.containsKey(userID)) {
                User user = userService.getUserById(userID);
                if (user != null) {
                    userMap.put(userID, user);
                }
            }
        }

        return new ArrayList<>(userMap.values());
    }

    /**
     * Gets customer usage statistics: number of reservations and total spending.
     *
     * @param userID The ID of the user
     * @return Map with statistics: "reservationCount" and "totalSpending"
     */
    public Map<String, Object> getCustomerStats(int userID) {
        Map<String, Object> stats = new HashMap<>();

        // Get all reservations for this user at admin's parking spaces
        List<ParkingSpace> adminSpaces = getAdminParkingSpaces();
        List<String> parkingIDs = new ArrayList<>();
        for (ParkingSpace space : adminSpaces) {
            parkingIDs.add(space.getParkingID());
        }

        List<Reservation> userReservations = reservationService.getReservationsByUser(userID);
        List<Reservation> relevantReservations = new ArrayList<>();

        for (Reservation reservation : userReservations) {
            String parkingID = reservationService.getParkingIDFromSlot(reservation.getSlotID());
            if (parkingIDs.contains(parkingID)) {
                relevantReservations.add(reservation);
            }
        }

        // Calculate statistics
        int reservationCount = relevantReservations.size();
        BigDecimal totalSpending = BigDecimal.ZERO;

        for (Reservation reservation : relevantReservations) {
            BigDecimal paymentAmount = reservationService.getPaymentAmount(reservation.getReservationID());
            if (paymentAmount != null) {
                totalSpending = totalSpending.add(paymentAmount);
            }
        }

        stats.put("reservationCount", reservationCount);
        stats.put("totalSpending", totalSpending);

        return stats;
    }

    /**
     * Gets the most frequent customers based on number of reservations.
     *
     * @param limit Maximum number of customers to return
     * @return List of users sorted by reservation count (highest first)
     */
    public List<Map<String, Object>> getTopCustomers(int limit) {
        List<Map<String, Object>> topCustomers = new ArrayList<>();

        // Get all customers with reservations
        List<User> customers = getCustomersWithReservations();

        // Calculate statistics for each customer
        List<Map<String, Object>> customerStats = new ArrayList<>();
        for (User customer : customers) {
            Map<String, Object> stats = getCustomerStats(customer.getUserID());
            stats.put("user", customer);
            customerStats.add(stats);
        }

        // Sort by reservation count (descending)
        customerStats.sort((a, b) -> {
            Integer countA = (Integer) a.get("reservationCount");
            Integer countB = (Integer) b.get("reservationCount");
            return countB.compareTo(countA);
        });

        // Take the top results
        int resultCount = Math.min(limit, customerStats.size());
        for (int i = 0; i < resultCount; i++) {
            topCustomers.add(customerStats.get(i));
        }

        return topCustomers;
    }

    /**
     * Populates a table model with customer data for display in a JTable.
     *
     * @param tableModel The table model to populate
     * @param customers  The list of customers
     */
    public void populateCustomerTable(DefaultTableModel tableModel, List<User> customers) {
        // Clear existing rows
        tableModel.setRowCount(0);

        // Add column headers if needed
        if (tableModel.getColumnCount() == 0) {
            tableModel.addColumn("User ID");
            tableModel.addColumn("Name");
            tableModel.addColumn("Email");
            tableModel.addColumn("Phone");
            tableModel.addColumn("Balance");
        }

        // Add rows for each customer
        for (User user : customers) {
            Object[] row = {
                    user.getUserID(),
                    user.getUserName(),
                    user.getEmail(),
                    user.getPhone(),
                    "$" + user.getBalance().toString()
            };
            tableModel.addRow(row);
        }
    }

    /**
     * Populates a table model with customer reservation data for display in a
     * JTable.
     *
     * @param tableModel The table model to populate
     * @param userID     The ID of the user whose reservations to display
     */
    public void populateUserReservationsTable(DefaultTableModel tableModel, int userID) {
        // Clear existing rows
        tableModel.setRowCount(0);

        // Add column headers if needed
        if (tableModel.getColumnCount() == 0) {
            tableModel.addColumn("Reservation ID");
            tableModel.addColumn("Parking Space");
            tableModel.addColumn("Start Date");
            tableModel.addColumn("End Date");
            tableModel.addColumn("Status");
            tableModel.addColumn("Amount");
        }

        // Get all reservations for this user at admin's parking spaces
        List<ParkingSpace> adminSpaces = getAdminParkingSpaces();
        List<String> parkingIDs = new ArrayList<>();
        Map<String, String> parkingAddresses = new HashMap<>();

        for (ParkingSpace space : adminSpaces) {
            parkingIDs.add(space.getParkingID());
            parkingAddresses.put(space.getParkingID(), space.getParkingAddress());
        }

        List<Reservation> userReservations = reservationService.getReservationsByUser(userID);

        // Add rows for each reservation at admin's parking spaces
        for (Reservation reservation : userReservations) {
            String parkingID = reservationService.getParkingIDFromSlot(reservation.getSlotID());

            if (parkingIDs.contains(parkingID)) {
                BigDecimal amount = reservationService.getPaymentAmount(reservation.getReservationID());

                Object[] row = {
                        reservation.getReservationID(),
                        parkingAddresses.getOrDefault(parkingID, parkingID),
                        reservation.getStartDate() + " " + reservation.getStartTime(),
                        reservation.getEndDate() + " " + reservation.getEndTime(),
                        reservation.getStatus(),
                        amount != null ? "$" + amount.toString() : "N/A"
                };
                tableModel.addRow(row);
            }
        }
    }

    /**
     * Populates a table model with top customer data for display in a JTable.
     *
     * @param tableModel The table model to populate
     * @param limit      Maximum number of customers to display
     */
    public void populateTopCustomersTable(DefaultTableModel tableModel, int limit) {
        // Clear existing rows
        tableModel.setRowCount(0);

        // Add column headers if needed
        if (tableModel.getColumnCount() == 0) {
            tableModel.addColumn("User ID");
            tableModel.addColumn("Name");
            tableModel.addColumn("Email");
            tableModel.addColumn("Reservations");
            tableModel.addColumn("Total Spending");
        }

        // Get top customers with stats
        List<Map<String, Object>> topCustomers = getTopCustomers(limit);

        // Add rows for each customer
        for (Map<String, Object> customerData : topCustomers) {
            User user = (User) customerData.get("user");
            int reservationCount = (Integer) customerData.get("reservationCount");
            BigDecimal totalSpending = (BigDecimal) customerData.get("totalSpending");

            Object[] row = {
                    user.getUserID(),
                    user.getUserName(),
                    user.getEmail(),
                    reservationCount,
                    "$" + totalSpending.toString()
            };
            tableModel.addRow(row);
        }
    }

    /**
     * Sets the current admin user.
     *
     * @param admin The new admin user
     */
    public void setCurrentAdmin(Admin admin) {
        this.currentAdmin = admin;
    }
}