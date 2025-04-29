package main.java.com.parkeasy.controller.admin;

import main.java.com.parkeasy.model.User;
import main.java.com.parkeasy.model.Reservation;
import main.java.com.parkeasy.service.AdminService;
import main.java.com.parkeasy.service.UserService;
import main.java.com.parkeasy.service.ReservationService;

import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controller for managing customer operations by admins
 */
public class CustomerManagementController {
    private static final Logger LOGGER = Logger.getLogger(CustomerManagementController.class.getName());

    private final AdminService adminService;
    private final UserService userService;
    private final ReservationService reservationService;

    /**
     * Constructor with dependency injection
     */
    public CustomerManagementController(AdminService adminService,
                                        UserService userService,
                                        ReservationService reservationService) {
        this.adminService = adminService;
        this.userService = userService;
        this.reservationService = reservationService;
    }

    /**
     * Get all users in the system
     *
     * @return List of all users
     */
    public List<User> getAllUsers() {
        try {
            List<User> users = userService.getAllUsers();
            return users != null ? users : Collections.emptyList();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving all users", e);
            return Collections.emptyList();
        }
    }

    /**
     * Get user by ID
     *
     * @param userId User ID
     * @return User object or null if not found
     */
    public User getUserById(int userId) {
        try {
            return userService.getUserById(userId);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error retrieving user by ID: " + userId, e);
            return null;
        }
    }

    /**
     * Search users by criteria (name, email, phone)
     *
     * @param searchTerm Search term
     * @return List of matching users
     */
    public List<User> searchUsers(String searchTerm) {
        try {
            if (searchTerm == null || searchTerm.trim().isEmpty()) {
                return Collections.emptyList();
            }

            return userService.searchUsers(searchTerm);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error searching users with term: " + searchTerm, e);
            return Collections.emptyList();
        }
    }


    /**
     * Get user reservations
     *
     * @param userId User ID
     * @return List of reservations
     */
    public List<Reservation> getUserReservations(int userId) {
        try {
            return reservationService.getReservationsByUserId(userId);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error retrieving reservations for user: " + userId, e);
            return Collections.emptyList();
        }
    }


    /**
     * Get detailed user activity
     *
     * @param userId User ID
     * @return Map of user activity data
     */
    public Map<String, Object> getUserActivity(int userId) {
        try {
            User user = userService.getUserById(userId);
            if (user == null) {
                return Collections.emptyMap();
            }

            Map<String, Object> activity = new HashMap<>();
            activity.put("user", user);

            // Get recent reservations
            List<Reservation> reservations = reservationService.getReservationsByUserId(userId);
            activity.put("reservations", reservations);
            activity.put("reservationCount", reservations.size());

            // Calculate average reservation duration
            double avgDuration = reservations.stream()
                    .mapToLong(r -> {
                        long startTime = r.getStartDate().getTime() + r.getStartTime().getTime();
                        long endTime = r.getEndDate().getTime() + r.getEndTime().getTime();
                        return (endTime - startTime) / (1000 * 60); // Minutes
                    })
                    .average()
                    .orElse(0);
            activity.put("averageReservationDuration", avgDuration);

            // More statistics as needed

            return activity;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error retrieving activity for user: " + userId, e);
            return Collections.emptyMap();
        }
    }
}