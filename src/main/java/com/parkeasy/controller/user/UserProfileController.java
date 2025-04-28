package main.java.com.parkeasy.controller.user;

import main.java.com.parkeasy.model.User;
import main.java.com.parkeasy.model.Vehicle;
import main.java.com.parkeasy.model.Card;
import main.java.com.parkeasy.service.UserService;
import main.java.com.parkeasy.service.VehicleService;
import main.java.com.parkeasy.service.PaymentService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Controller for managing user profile information and settings
 */
public class UserProfileController {
    private UserService userService;
    private VehicleService vehicleService;
    private PaymentService paymentService;
    private int currentUserId;

    /**
     * Constructor for UserProfileController
     * 
     * @param userId The ID of the current logged-in user
     */
    public UserProfileController(int userId) {
        this.userService = new UserService();
        this.vehicleService = new VehicleService();
        this.paymentService = new PaymentService();
        this.currentUserId = userId;
    }

    /**
     * Gets the current user's profile information
     * 
     * @return User object containing the user's information
     */
    public User getUserProfile() {
        return userService.getUserById(currentUserId);
    }

    /**
     * Updates the user's profile information
     * 
     * @param updatedUserInfo Map containing the updated user information
     * @return boolean indicating success or failure
     */
    public boolean updateUserProfile(Map<String, Object> updatedUserInfo) {
        User currentUser = userService.getUserById(currentUserId);

        // Update the user object with new information
        if (updatedUserInfo.containsKey("userName")) {
            currentUser.setUserName((String) updatedUserInfo.get("userName"));
        }

        if (updatedUserInfo.containsKey("phone")) {
            String phone = (String) updatedUserInfo.get("phone");
            // Check if phone is unique before updating
            if (!userService.isPhoneNumberTaken(phone) || phone.equals(currentUser.getPhone())) {
                currentUser.setPhone(phone);
            } else {
                return false; // Phone number is already taken by another user
            }
        }

        if (updatedUserInfo.containsKey("email")) {
            String email = (String) updatedUserInfo.get("email");
            // Check if email is unique before updating
            if (!userService.isEmailTaken(email) || email.equals(currentUser.getEmail())) {
                currentUser.setEmail(email);
            } else {
                return false; // Email is already taken by another user
            }
        }

        // Update password if provided with the current password for verification
        if (updatedUserInfo.containsKey("newPassword") && updatedUserInfo.containsKey("currentPassword")) {
            String currentPassword = (String) updatedUserInfo.get("currentPassword");
            String newPassword = (String) updatedUserInfo.get("newPassword");

            if (!userService.verifyPassword(currentUser.getEmail(), currentPassword)) {
                return false; // Current password is incorrect
            }

            currentUser.setPassword(userService.hashPassword(newPassword));
        }

        // Save updated user information
        return userService.updateUser(currentUser);
    }

    /**
     * Gets the list of vehicles registered to the user
     * 
     * @return List of Vehicle objects
     */
    public List<Vehicle> getUserVehicles() {
        return vehicleService.getVehiclesByUserId(currentUserId);
    }

    /**
     * Adds a new vehicle for the user
     * 
     * @param vehicleId The license plate or identification of the vehicle
     * @return boolean indicating success or failure
     */
    public boolean addVehicle(String vehicleId) {
        // Check if the vehicle ID is already registered
        if (vehicleService.isVehicleRegistered(vehicleId)) {
            return false;
        }

        // Create and save the new vehicle
        Vehicle vehicle = new Vehicle(vehicleId, currentUserId);
        return vehicleService.addVehicle(vehicle);
    }

    /**
     * Removes a vehicle from the user's profile
     * 
     * @param vehicleId The ID of the vehicle to remove
     * @return boolean indicating success or failure
     */
    public boolean removeVehicle(String vehicleId) {
        // Verify the vehicle belongs to the current user
        Optional<Vehicle> vehicle = vehicleService.getVehicleById(vehicleId);
        if (!vehicle.isPresent() || vehicle.get().getUserID() != currentUserId) {
            return false;
        }

        return vehicleService.removeVehicle(vehicleId);
    }

    /**
     * Gets the payment cards registered to the user
     * 
     * @return List of Card objects
     */
    public List<Card> getUserCards() {
        return paymentService.getListOfCardsByUserId(currentUserId);
    }

    /**
     * Adds a new payment card for the user
     * 
     * @param cardInfo Map containing the card information
     * @return boolean indicating success or failure
     */
    public boolean addPaymentCard(Map<String, String> cardInfo) {
        // Create a new card object
        Card card = new Card();
        card.setCardNumber(cardInfo.get("cardNumber"));
        card.setCardHolder(cardInfo.get("cardHolder"));
        card.setValidTo(cardInfo.get("validTo"));
        card.setUserID(currentUserId);

        return paymentService.addCard(card);
    }

    /**
     * Removes a payment card from the user's profile
     * 
     * @param cardNumber The number of the card to remove
     * @return boolean indicating success or failure
     */
    public boolean removePaymentCard(String cardNumber) {
        // Verify the card belongs to the current user
        Card card = paymentService.getListOfCardByNumber(cardNumber);
        if (card == null || card.getUserID() != currentUserId) {
            return false;
        }

        return paymentService.removeCard(cardNumber);
    }
}