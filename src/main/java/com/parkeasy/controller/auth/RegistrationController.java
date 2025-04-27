package main.java.com.parkeasy.controller.auth;

import main.java.com.parkeasy.model.User;
import main.java.com.parkeasy.repository.UserRepository;
import main.java.com.parkeasy.repository.AdminRepository;

public class RegistrationController {
    private final UserRepository userRepository;
    private final AdminRepository adminRepository;

    public RegistrationController() {
        this.userRepository = new UserRepository();
        this.adminRepository = new AdminRepository();
    }

    /**
     * Registers a new user
     * @param userName The user's name
     * @param phone The user's phone number
     * @param email The user's email address
     * @param password The user's password
     * @return true if registration was successful, false otherwise
     */
    public boolean registerUser(String userName, String phone, String email, String password) {
        // Basic validation
        if (userName == null || userName.trim().isEmpty()) {
            System.out.println("Username cannot be empty");
            return false;
        }

        if (phone == null || phone.length() != 10) {
            System.out.println("Phone number must be 10 digits");
            return false;
        }

        if (email == null || !email.contains("@")) {
            System.out.println("Invalid email format");
            return false;
        }

        if (password == null || password.length() < 6) {
            System.out.println("Password must be at least 6 characters long");
            return false;
        }

        // Check if email already exists in either user or admin database
        if (userRepository.findByEmail(email).isPresent() || adminRepository.getAdminByEmail(email) != null) {
            System.out.println("Email already registered");
            return false;
        }

        // Check if phone number already exists
        if (adminRepository.getAdminByPhone(phone) != null) {
            System.out.println("Phone number already registered");
            return false;
        }

        try {
            // Create and save new user
            User newUser = new User(0, phone, userName, email, password);
            userRepository.saveUser(newUser);
            System.out.println("User registered successfully");
            return true;
        } catch (Exception e) {
            System.out.println("Error during registration: " + e.getMessage());
            return false;
        }
    }
}
