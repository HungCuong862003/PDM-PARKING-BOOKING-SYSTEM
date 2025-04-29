package main.java.com.parkeasy.service;

import main.java.com.parkeasy.model.User;
import main.java.com.parkeasy.repository.UserRepository;
import main.java.com.parkeasy.repository.VehicleRepository;
import main.java.com.parkeasy.repository.ReservationRepository;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service class for user-related operations
 */
public class UserService {
    private static final Logger LOGGER = Logger.getLogger(UserService.class.getName());

    private final UserRepository userRepository;
    private final VehicleRepository vehicleRepository;
    private final ReservationRepository reservationRepository;

    /**
     * Constructor with dependency injection
     */
    public UserService(UserRepository userRepository, VehicleRepository vehicleRepository,
                       ReservationRepository reservationRepository) {
        this.userRepository = userRepository;
        this.vehicleRepository = vehicleRepository;
        this.reservationRepository = reservationRepository;
    }

    /**
     * Default constructor
     */
    public UserService() {
        this.userRepository = new UserRepository();
        this.vehicleRepository = new VehicleRepository();
        this.reservationRepository = new ReservationRepository();
    }

    /**
     * Create a new user
     *
     * @param user User object with data to save
     * @return true if successful, false otherwise
     */
    public boolean createUser(User user) {
        try {
            LOGGER.log(Level.INFO, "Creating new user: {0}", user.getEmail());
            return userRepository.createUser(user);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error creating user", e);
            return false;
        }
    }

    /**
     * Update an existing user
     *
     * @param user User object with updated data
     * @return true if successful, false otherwise
     */
    public boolean updateUser(User user) {
        try {
            LOGGER.log(Level.INFO, "Updating user: {0}", user.getUserID());
            return userRepository.updateUser(user);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating user", e);
            return false;
        }
    }

    /**
     * Delete a user by ID
     *
     * @param userId ID of the user to delete
     * @return true if successful, false otherwise
     */
    public boolean deleteUser(int userId) {
        try {
            LOGGER.log(Level.INFO, "Deleting user: {0}", userId);
            return userRepository.deleteUser(userId);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting user", e);
            return false;
        }
    }

    /**
     * Get a user by ID
     *
     * @param userId ID of the user to get
     * @return User object if found, null otherwise
     */
    public User getUserById(int userId) {
        try {
            LOGGER.log(Level.FINE, "Getting user by ID: {0}", userId);
            return userRepository.getUserById(userId);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting user by ID", e);
            return null;
        }
    }

    /**
     * Get a user by email
     *
     * @param email Email of the user to get
     * @return User object if found, null otherwise
     */
    public User getUserByEmail(String email) {
        LOGGER.log(Level.FINE, "Getting user by email: {0}", email);
        return userRepository.getUserByEmail(email);
    }

    /**
     * Check if an email already exists in the database
     *
     * @param email Email to check
     * @return true if exists, false otherwise
     */
    public boolean isEmailExists(String email) {
        try {
            return userRepository.isEmailExists(email);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error checking if email exists", e);
            return false;
        }
    }

    /**
     * Check if a phone number already exists in the database
     *
     * @param phone Phone number to check
     * @return true if exists, false otherwise
     */
    public boolean isPhoneExists(String phone) {
        try {
            return userRepository.isPhoneExists(phone);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error checking if phone exists", e);
            return false;
        }
    }

    /**
     * Search for users by name or email
     *
     * @param searchTerm Term to search for
     * @return List of matching users
     */
    public List<User> searchUsers(String searchTerm) {
        try {
            LOGGER.log(Level.INFO, "Searching users with term: {0}", searchTerm);
            return userRepository.searchUsers(searchTerm);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error searching users", e);
            return List.of(); // Return empty list on error
        }
    }

    /**
     * Get all users in the system
     *
     * @return List of all users
     */
    public List<User> getAllUsers() throws SQLException {
        try {
            LOGGER.log(Level.FINE, "Getting all users");
            return userRepository.findAll();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting all users", e);
            throw e;
        }
    }

    /**
     * Get user by vehicle ID
     *
     * @param vehicleId Vehicle ID
     * @return User who owns the vehicle, or null if not found
     */
    public User getUserByVehicleId(String vehicleId) {
        try {
            LOGGER.log(Level.FINE, "Getting user by vehicle ID: {0}", vehicleId);
            return userRepository.findUserByVehicleId(vehicleId);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error getting user by vehicle ID", e);
            return null;
        }
    }

    /**
     * Check if a phone number is already taken by another user
     *
     * @param phone Phone number to check
     * @param currentUserId ID of the current user (to exclude from check)
     * @return true if taken, false otherwise
     */
    public boolean isPhoneNumberTaken(String phone, int currentUserId) {
        try {
            // Find user with this phone number
            User user = userRepository.getUserByPhone(phone);

            // If no user found with this phone, it's not taken
            if (user == null) {
                return false;
            }

            // If found user ID matches current user ID, it's the same user, so not taken
            return user.getUserID() != currentUserId;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error checking if phone number is taken", e);
            return true; // Assume taken on error to be safe
        }
    }

    /**
     * Check if an email is already taken by another user
     *
     * @param email Email to check
     * @param currentUserId ID of the current user (to exclude from check)
     * @return true if taken, false otherwise
     */
    public boolean isEmailTaken(String email, int currentUserId) {
        // Find user with this email
        User user = userRepository.getUserByEmail(email);

        // If no user found with this email, it's not taken
        if (user == null) {
            return false;
        }

        // If found user ID matches current user ID, it's the same user, so not taken
        return user.getUserID() != currentUserId;
    }

    /**
     * Check if a user has active reservations
     *
     * @param userId The user ID to check
     * @return true if user has active reservations, false otherwise
     */
    public boolean hasActiveReservations(String userId) {
        try {
            int userIdInt = Integer.parseInt(userId);

            // SQL query to check if user has active reservations
            String sql = "SELECT COUNT(*) FROM parking_reservation pr " +
                    "JOIN vehicle v ON pr.VehicleID = v.VehicleID " +
                    "WHERE v.UserID = ? AND pr.Status IN ('PENDING', 'PAID', 'ACTIVE')";

            LOGGER.log(Level.INFO, "Executing SQL: {0} with parameter: {1}", new Object[]{sql, userId});

            // Use repository to check if any active reservations
            int count = reservationRepository.getActiveReservationCountByUserId(userIdInt);
            return count > 0;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error checking for active reservations for user: " + userId, e);
            return false; // Assume no active reservations on error
        }
    }

    /**
     * Add funds to user's balance
     *
     * @param userId User ID
     * @param amount Amount to add
     * @return true if successful, false otherwise
     */
    public boolean addFunds(int userId, double amount) {
        try {
            if (amount <= 0) {
                LOGGER.log(Level.WARNING, "Cannot add negative or zero amount: {0}", amount);
                return false;
            }

            return userRepository.updateBalance(userId, amount);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error adding funds to user account", e);
            return false;
        }
    }

    /**
     * Deduct funds from user's balance
     *
     * @param userId User ID
     * @param amount Amount to deduct
     * @return true if successful, false otherwise
     */
    public boolean deductFunds(int userId, double amount) {
        try {
            if (amount <= 0) {
                LOGGER.log(Level.WARNING, "Cannot deduct negative or zero amount: {0}", amount);
                return false;
            }

            // Get current balance
            User user = getUserById(userId);
            if (user == null) {
                return false;
            }

            // Check if user has sufficient balance
            if (user.getBalance() < amount) {
                LOGGER.log(Level.WARNING, "Insufficient balance. Required: {0}, Available: {1}",
                        new Object[]{amount, user.getBalance()});
                return false;
            }

            // Deduct amount (negative value)
            return userRepository.updateBalance(userId, -amount);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error deducting funds from user account", e);
            return false;
        }
    }
}