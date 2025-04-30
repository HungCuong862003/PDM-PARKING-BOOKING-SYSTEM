package main.java.com.parkeasy.controller.user;

import main.java.com.parkeasy.model.User;
import main.java.com.parkeasy.model.Vehicle;
import main.java.com.parkeasy.service.UserService;
import main.java.com.parkeasy.service.VehicleService;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controller for handling user profile operations
 */
public class UserProfileController {
    private static final Logger LOGGER = Logger.getLogger(UserProfileController.class.getName());

    private final UserService userService;
    private final VehicleService vehicleService;

    /**
     * Constructor with dependency injection
     */
    public UserProfileController(UserService userService, VehicleService vehicleService) {
        this.userService = userService;
        this.vehicleService = vehicleService;
    }

    /**
     * Get user profile information
     *
     * @param userId The ID of the user
     * @return User object or null if not found
     */
    public User getUserProfile(int userId) {
        try {
            return userService.getUserById(userId);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error retrieving user profile: " + userId, e);
            return null;
        }
    }

    /**
     * Update user profile information
     *
     * @param userId   The ID of the user
     * @param userName New user name (or null to keep current)
     * @param phone    New phone number (or null to keep current)
     * @param email    New email address (or null to keep current)
     * @return Map containing result of the update process
     */
    public Map<String, Object> updateUserProfile(int userId, String userName, String phone, String email) {
        try {
            // Get current user data
            User user = userService.getUserById(userId);
            if (user == null) {
                return Map.of(
                        "success", false,
                        "message", "User not found");
            }

            boolean updated = false;

            // Update only provided fields
            if (userName != null && !userName.isEmpty() && !userName.equals(user.getUserName())) {
                user.setUserName(userName);
                updated = true;
            }

            if (phone != null && !phone.isEmpty() && !phone.equals(user.getPhone())) {
                // Check if phone number is already in use by another user
                if (userService.isPhoneNumberTaken(phone, userId)) {
                    return Map.of(
                            "success", false,
                            "message", "Phone number is already in use");
                }
                user.setPhone(phone);
                updated = true;
            }

            if (email != null && !email.isEmpty() && !email.equals(user.getEmail())) {
                // Check if email is already in use by another user
                if (userService.isEmailTaken(email, userId)) {
                    return Map.of(
                            "success", false,
                            "message", "Email address is already in use");
                }
                user.setEmail(email);
                updated = true;
            }

            if (!updated) {
                return Map.of(
                        "success", true,
                        "message", "No changes made");
            }

            // Save updated user
            boolean success = userService.updateUser(user);

            if (success) {
                return Map.of(
                        "success", true,
                        "message", "Profile updated successfully");
            } else {
                return Map.of(
                        "success", false,
                        "message", "Error updating profile");
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error updating user profile: " + userId, e);
            return Map.of(
                    "success", false,
                    "message", "Error updating profile: " + e.getMessage());
        }
    }

    /**
     * Change user password
     *
     * @param userId          The ID of the user
     * @param currentPassword The current password
     * @param newPassword     The new password
     * @return Map containing result of the password change process
     */
    public Map<String, Object> changePassword(int userId, String currentPassword, String newPassword) {
        try {
            // Get current user data
            User user = userService.getUserById(userId);
            if (user == null) {
                return Map.of(
                        "success", false,
                        "message", "User not found");
            }

            // Verify current password (plain text comparison)
            if (!currentPassword.equals(user.getPassword())) {
                return Map.of(
                        "success", false,
                        "message", "Current password is incorrect");
            }

            // Validate new password
            if (newPassword == null || newPassword.length() < 8) {
                return Map.of(
                        "success", false,
                        "message", "New password must be at least 8 characters long");
            }

            // Store new password in plain text
            user.setPassword(newPassword);

            // Save updated user
            boolean success = userService.updateUser(user);

            if (success) {
                return Map.of(
                        "success", true,
                        "message", "Password changed successfully");
            } else {
                return Map.of(
                        "success", false,
                        "message", "Error changing password");
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error changing password for user: " + userId, e);
            return Map.of(
                    "success", false,
                    "message", "Error changing password: " + e.getMessage());
        }
    }

    /**
     * Add funds to user balance
     *
     * @param userId The ID of the user
     * @param amount The amount to add
     * @return Map containing result of the add funds process
     */
    public Map<String, Object> addFunds(int userId, double amount) {
        try {
            // Validate amount
            if (amount <= 0) {
                return Map.of(
                        "success", false,
                        "message", "Amount must be greater than zero");
            }

            // Get current user data
            User user = userService.getUserById(userId);
            if (user == null) {
                return Map.of(
                        "success", false,
                        "message", "User not found");
            }

            // Update balance
            double newBalance = user.getBalance() + amount;
            user.setBalance(newBalance);

            // Save updated user
            boolean success = userService.updateUser(user);

            if (success) {
                return Map.of(
                        "success", true,
                        "message", "Funds added successfully",
                        "newBalance", newBalance);
            } else {
                return Map.of(
                        "success", false,
                        "message", "Error adding funds");
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error adding funds for user: " + userId, e);
            return Map.of(
                    "success", false,
                    "message", "Error adding funds: " + e.getMessage());
        }
    }

    /**
     * Get vehicles owned by the user
     *
     * @param userId The ID of the user
     * @return List of vehicles
     */
    public List<Vehicle> getUserVehicles(int userId) {
        try {
            return vehicleService.getVehiclesByUserId(userId);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error retrieving vehicles for user: " + userId, e);
            return List.of(); // Return empty list on error
        }
    }

    /**
     * Add a new vehicle for the user
     *
     * @param userId    The ID of the user
     * @param vehicleId The vehicle ID/plate number
     * @return Map containing result of the add vehicle process
     */
    public Map<String, Object> addVehicle(int userId, String vehicleId) {
        try {
            // Validate vehicle ID
            if (vehicleId == null || vehicleId.trim().isEmpty()) {
                return Map.of(
                        "success", false,
                        "message", "Vehicle ID is required");
            }

            // Check if vehicle ID is already registered
            Vehicle existingVehicle = vehicleService.getVehicleById(vehicleId);
            if (existingVehicle != null) {
                return Map.of(
                        "success", false,
                        "message", "Vehicle is already registered");
            }

            // Check if user has not exceeded vehicle limit
            int vehicleCount = vehicleService.countUserVehicles(userId);
            if (vehicleCount >= 5) { // Assuming a limit of 5 vehicles per user
                return Map.of(
                        "success", false,
                        "message", "Maximum number of vehicles reached (5)");
            }

            // Create new vehicle
            Vehicle vehicle = new Vehicle(vehicleId, userId);

            // Save vehicle
            boolean success = vehicleService.addVehicle(vehicle);

            if (success) {
                return Map.of(
                        "success", true,
                        "message", "Vehicle added successfully",
                        "vehicle", vehicle);
            } else {
                return Map.of(
                        "success", false,
                        "message", "Error adding vehicle");
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error adding vehicle for user: " + userId, e);
            return Map.of(
                    "success", false,
                    "message", "Error adding vehicle: " + e.getMessage());
        }
    }

    /**
     * Remove a vehicle
     *
     * @param userId    The ID of the user
     * @param vehicleId The ID of the vehicle to remove
     * @return Map containing result of the remove vehicle process
     */
    public Map<String, Object> removeVehicle(int userId, String vehicleId) {
        try {
            // Verify vehicle belongs to user
            if (!vehicleService.isVehicleOwnedByUser(vehicleId, userId)) {
                return Map.of(
                        "success", false,
                        "message", "You can only remove your own vehicles");
            }

            // Check if vehicle has active reservations
            boolean hasActiveReservations = vehicleService.hasActiveReservations(vehicleId);
            if (hasActiveReservations) {
                return Map.of(
                        "success", false,
                        "message", "Vehicle has active reservations and cannot be removed");
            }

            // Remove vehicle
            boolean success = vehicleService.removeVehicle(vehicleId);

            if (success) {
                return Map.of(
                        "success", true,
                        "message", "Vehicle removed successfully");
            } else {
                return Map.of(
                        "success", false,
                        "message", "Error removing vehicle");
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error removing vehicle: " + vehicleId, e);
            return Map.of(
                    "success", false,
                    "message", "Error removing vehicle: " + e.getMessage());
        }
    }

    /**
     * Delete user account
     *
     * @param userId   The ID of the user
     * @param password User's password for verification
     * @return Map containing result of the account deletion process
     */
    public Map<String, Object> deleteAccount(int userId, String password) {
        try {
            // Get user data
            User user = userService.getUserById(userId);
            if (user == null) {
                return Map.of(
                        "success", false,
                        "message", "User not found");
            }

            // Verify password (plain text comparison)
            if (!password.equals(user.getPassword())) {
                return Map.of(
                        "success", false,
                        "message", "Password is incorrect");
            }

            // Check if user has active reservations
            boolean hasActiveReservations = userService.hasActiveReservations(String.valueOf(userId));
            if (hasActiveReservations) {
                return Map.of(
                        "success", false,
                        "message", "You have active reservations. Please cancel them before deleting your account.");
            }

            // Delete user's vehicles
            List<Vehicle> userVehicles = vehicleService.getVehiclesByUserId(userId);
            for (Vehicle vehicle : userVehicles) {
                vehicleService.removeVehicle(vehicle.getVehicleID());
            }

            // Delete user
            boolean success = userService.deleteUser(userId);

            if (success) {
                return Map.of(
                        "success", true,
                        "message", "Account deleted successfully");
            } else {
                return Map.of(
                        "success", false,
                        "message", "Error deleting account");
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error deleting user account: " + userId, e);
            return Map.of(
                    "success", false,
                    "message", "Error deleting account: " + e.getMessage());
        }
    }

    public boolean updateUserBalance(int userID, double balance) {
        try {
            User user = userService.getUserById(userID);
            if (user == null) {
                return false;
            }
            user.setBalance(balance);
            return userService.updateUser(user);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error updating user balance: " + userID, e);
            return false;
        }
    }
}