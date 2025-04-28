package main.java.com.parkeasy.service;

import main.java.com.parkeasy.model.User;
import main.java.com.parkeasy.model.Vehicle;
import main.java.com.parkeasy.model.Reservation;
import main.java.com.parkeasy.model.Card;
import main.java.com.parkeasy.repository.UserRepository;
import main.java.com.parkeasy.repository.VehicleRepository;
import main.java.com.parkeasy.repository.ReservationRepository;
import main.java.com.parkeasy.repository.CardRepository;
import main.java.com.parkeasy.util.DatabaseConnection;

import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Service class for handling user-specific operations in the ParkEasy system.
 * Provides functionality for managing user profiles, vehicles, cards,
 * reservations, and account balance.
 */
public class UserService {

    private final UserRepository userRepository;
    private final VehicleRepository vehicleRepository;
    private final ReservationRepository reservationRepository;
    private final CardRepository cardRepository;

    /**
     * Constructor for UserService.
     */
    public UserService() {
        this.userRepository = new UserRepository();
        this.vehicleRepository = new VehicleRepository();
        this.reservationRepository = new ReservationRepository();
        this.cardRepository = new CardRepository();
    }

    /**
     * Gets a User by their ID.
     *
     * @param userID The user's unique identifier
     * @return The User object if found, null otherwise
     */
    public User getUserById(int userID) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM USER WHERE UserID = ?";

            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, userID);

                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        User user = new User();
                        user.setUserID(resultSet.getInt("UserID"));
                        user.setUserName(resultSet.getString("UserName"));
                        user.setEmail(resultSet.getString("Email"));
                        user.setPhone(resultSet.getString("Phone"));
                        user.setPassword(resultSet.getString("Password"));
                        user.setBalance(new BigDecimal(resultSet.getString("Balance")));

                        return user;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving user: " + e.getMessage());
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            System.err.println("Validation error while creating user object: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Gets a User by their email.
     *
     * @param email The user's email address
     * @return The User object if found, null otherwise
     */
    public User getUserByEmail(String email) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM USER WHERE Email = ?";

            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, email);

                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        User user = new User();
                        user.setUserID(resultSet.getInt("UserID"));
                        user.setUserName(resultSet.getString("UserName"));
                        user.setEmail(resultSet.getString("Email"));
                        user.setPhone(resultSet.getString("Phone"));
                        user.setPassword(resultSet.getString("Password"));
                        user.setBalance(new BigDecimal(resultSet.getString("Balance")));

                        return user;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving user by email: " + e.getMessage());
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            System.err.println("Validation error while creating user object: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Updates a user's profile information in the database.
     *
     * @param user The user object with updated information
     * @return true if update successful, false otherwise
     */
    public boolean updateUser(User user) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "UPDATE USER SET UserName = ?, Email = ?, Phone = ?, Password = ?, Balance = ? WHERE UserID = ?";

            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, user.getUserName());
                statement.setString(2, user.getEmail());
                statement.setString(3, user.getPhone());
                statement.setString(4, user.getPassword());
                statement.setBigDecimal(5, user.getBalance());
                statement.setInt(6, user.getUserID());

                int rowsAffected = statement.executeUpdate();
                return rowsAffected > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error updating user profile: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Checks if an email is already taken by another user.
     *
     * @param email The email to check
     * @return true if email is taken, false otherwise
     */
    public boolean isEmailTaken(String email) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "SELECT COUNT(*) FROM USER WHERE Email = ?";

            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, email);

                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getInt(1) > 0;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error checking email: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Checks if a phone number is already taken by another user.
     *
     * @param phone The phone number to check
     * @return true if phone number is taken, false otherwise
     */
    public boolean isPhoneNumberTaken(String phone) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "SELECT COUNT(*) FROM USER WHERE Phone = ?";

            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, phone);

                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getInt(1) > 0;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error checking phone number: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Verifies a user's password.
     *
     * @param email    The user's email
     * @param password The password to verify
     * @return true if password is correct, false otherwise
     */
    public boolean verifyPassword(String email, String password) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "SELECT Password FROM USER WHERE Email = ?";

            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, email);

                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        String storedPassword = resultSet.getString("Password");
                        return storedPassword.equals(hashPassword(password));
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error verifying password: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Hashes a password using SHA-256.
     *
     * @param password The password to hash
     * @return The hashed password
     */
    public String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();

            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1)
                    hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            System.err.println("Error hashing password: " + e.getMessage());
            e.printStackTrace();
            return password; // Fallback to plain text if hashing fails
        }
    }

    /**
     * Updates a user's password.
     *
     * @param userID          The user's unique identifier
     * @param currentPassword The current password (for verification)
     * @param newPassword     The new password
     * @return true if update successful, false otherwise
     */
    public boolean updatePassword(int userID, String currentPassword, String newPassword) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            // First get the user
            User user = getUserById(userID);
            if (user == null) {
                return false;
            }

            // Verify current password
            if (!user.getPassword().equals(hashPassword(currentPassword))) {
                return false;
            }

            // Update the password
            user.setPassword(hashPassword(newPassword));
            return updateUser(user);
        } catch (SQLException e) {
            System.err.println("Error updating password: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Adds funds to a user's account balance.
     *
     * @param userID The user's unique identifier
     * @param amount The amount to add
     * @return The new balance if update successful, null otherwise
     */
    public BigDecimal addFunds(int userID, BigDecimal amount) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            // First get the current balance
            User user = getUserById(userID);
            if (user == null) {
                return null;
            }

            // Validate amount
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Amount must be positive");
            }

            // Calculate new balance
            BigDecimal newBalance = user.getBalance().add(amount);
            user.setBalance(newBalance);

            // Update the user
            boolean updated = updateUser(user);
            if (updated) {
                return newBalance;
            }
        } catch (SQLException e) {
            System.err.println("Error adding funds: " + e.getMessage());
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            System.err.println("Validation error: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Deducts funds from a user's account balance.
     *
     * @param userID The user's unique identifier
     * @param amount The amount to deduct
     * @return The new balance if update successful, null otherwise
     */
    public BigDecimal deductFunds(int userID, BigDecimal amount) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            // First get the current balance
            User user = getUserById(userID);
            if (user == null) {
                return null;
            }

            // Validate amount
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Amount must be positive");
            }

            // Check if user has sufficient balance
            if (user.getBalance().compareTo(amount) < 0) {
                throw new IllegalArgumentException("Insufficient balance");
            }

            // Calculate new balance
            BigDecimal newBalance = user.getBalance().subtract(amount);
            user.setBalance(newBalance);

            // Update the user
            boolean updated = updateUser(user);
            if (updated) {
                return newBalance;
            }
        } catch (SQLException e) {
            System.err.println("Error deducting funds: " + e.getMessage());
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            System.err.println("Validation error: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Gets all vehicles owned by a user.
     *
     * @param userID The user's unique identifier
     * @return List of vehicles owned by the user
     */
    public List<Vehicle> getUserVehicles(int userID) {
        return vehicleRepository.getListOfVehiclesByUserId(userID);
    }

    /**
     * Gets a specific vehicle by its ID and user ID.
     *
     * @param vehicleID The vehicle's unique identifier
     * @param userID    The user's unique identifier
     * @return The Vehicle object if found, null otherwise
     */
    public Vehicle getVehicleById(String vehicleID, int userID) {
        Vehicle vehicle = vehicleRepository.getVehicleById(vehicleID);
        if (vehicle != null && vehicle.getUserID() == userID) {
            return vehicle;
        }
        return null;
    }

    /**
     * Adds a new vehicle for a user.
     *
     * @param vehicle The vehicle to add
     * @return true if addition successful, false otherwise
     */
    public boolean addVehicle(Vehicle vehicle) {
        return vehicleRepository.insertVehicle(vehicle);
    }

    /**
     * Removes a vehicle for a user.
     *
     * @param vehicleID The vehicle's unique identifier
     * @param userID    The user's unique identifier
     * @return true if removal successful, false otherwise
     */
    public boolean removeVehicle(String vehicleID, int userID) {
        // Check if vehicle belongs to user
        Vehicle vehicle = vehicleRepository.getVehicleById(vehicleID);
        if (vehicle == null || vehicle.getUserID() != userID) {
            return false;
        }

        // Check for active reservations
        List<Reservation> vehicleReservations = reservationRepository.getReservationsByVehicleId(vehicleID);
        for (Reservation reservation : vehicleReservations) {
            if ("ACTIVE".equals(reservation.getStatus())) {
                return false; // Cannot remove vehicle with active reservations
            }
        }

        return vehicleRepository.deleteVehicle(vehicleID);
    }

    /**
     * Gets all cards associated with a user.
     *
     * @param userID The user's unique identifier
     * @return List of cards associated with the user
     */
    public List<Card> getUserCards(int userID) {
        return cardRepository.getListOfCardsByUserId(userID);
    }

    /**
     * Adds a new card for a user.
     *
     * @param card The card to add
     * @return true if addition successful, false otherwise
     */
    public boolean addCard(Card card) {
        return cardRepository.insertCard(card);
    }

    /**
     * Removes a card for a user.
     *
     * @param cardNumber The card's unique identifier
     * @param userID     The user's unique identifier
     * @return true if removal successful, false otherwise
     */
    public boolean removeCard(String cardNumber, int userID) {
        // Check if card belongs to user
        Card card = cardRepository.getCardByCardNumber(cardNumber);
        if (card == null || card.getUserID() != userID) {
            return false;
        }

        return cardRepository.deleteCard(cardNumber);
    }

    /**
     * Gets all active reservations for a user.
     *
     * @param userID The user's unique identifier
     * @return List of active reservations for the user
     */
    public List<Reservation> getActiveReservations(int userID) {
        List<Reservation> allReservations = reservationRepository.getReservationsByUserId(userID);
        List<Reservation> activeReservations = new ArrayList<>();

        for (Reservation reservation : allReservations) {
            if ("ACTIVE".equals(reservation.getStatus())) {
                activeReservations.add(reservation);
            }
        }

        return activeReservations;
    }

    /**
     * Gets the reservation history for a user.
     *
     * @param userID The user's unique identifier
     * @return List of all past reservations for the user
     */
    public List<Reservation> getReservationHistory(int userID) {
        List<Reservation> allReservations = reservationRepository.getReservationsByUserId(userID);
        List<Reservation> historyReservations = new ArrayList<>();

        for (Reservation reservation : allReservations) {
            if (!"ACTIVE".equals(reservation.getStatus())) {
                historyReservations.add(reservation);
            }
        }

        return historyReservations;
    }

    /**
     * Gets a specific reservation by its ID and user ID.
     *
     * @param reservationID The reservation's unique identifier
     * @param userID        The user's unique identifier
     * @return The Reservation object if found, null otherwise
     */
    public Reservation getReservationById(int reservationID, int userID) {
        Reservation reservation = reservationRepository.getReservationById(reservationID);

        // Check if reservation exists and belongs to user (via vehicle)
        if (reservation != null) {
            Vehicle vehicle = vehicleRepository.getVehicleById(reservation.getVehicleID());
            if (vehicle != null && vehicle.getUserID() == userID) {
                return reservation;
            }
        }

        return null;
    }

    /**
     * Cancels a reservation.
     *
     * @param reservationID The reservation's unique identifier
     * @param userID        The user's unique identifier
     * @return true if cancellation successful, false otherwise
     */
    public boolean cancelReservation(int reservationID, int userID) {
        // Check if reservation exists and belongs to user
        Reservation reservation = getReservationById(reservationID, userID);
        if (reservation == null) {
            return false;
        }

        // Check if reservation is active
        if (!"ACTIVE".equals(reservation.getStatus())) {
            return false;
        }

        return reservationRepository.updateReservationById(reservationID, reservation);
    }

    /**
     * Checks if a user has sufficient balance for a payment.
     *
     * @param userID The user's unique identifier
     * @param amount The amount to check against
     * @return true if user has sufficient balance, false otherwise
     */
    public boolean hasSufficientBalance(int userID, BigDecimal amount) {
        User user = getUserById(userID);
        if (user == null) {
            return false;
        }

        return user.getBalance().compareTo(amount) >= 0;
    }

    /**
     * Gets the current account balance for a user.
     *
     * @param userID The user's unique identifier
     * @return The current balance if user found, null otherwise
     */
    public BigDecimal getUserBalance(int userID) {
        User user = getUserById(userID);
        if (user == null) {
            return null;
        }

        return user.getBalance();
    }
}