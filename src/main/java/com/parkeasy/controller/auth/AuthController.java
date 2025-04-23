package main.java.com.parkeasy.controller.auth;

import main.java.com.parkeasy.model.Admin;
import main.java.com.parkeasy.repository.AdminRepository;

public class AuthController {

    private AdminRepository adminRepository;

    public AuthController() {
        this.adminRepository = new AdminRepository(); // Ensure AdminRepository is properly implemented
    }

    // Method to add a new admin
    public void addAdmin(String adminName, String phone, String email, String password) {
        // This could involve creating an Admin object and saving it to the database
        if (!email.contains("@")) {
            System.out.println("Invalid email format");
        } else if (phone.length() != 10) {
            System.out.println("Phone number must be 10 digits");
        } else {
            // Check if the email and phone number already exist in the database
            if (adminRepository.getAdminByEmail(email) != null) {
                // If exists, do not allow adding the admin
                System.out.println("Email already exists");
            } else if (adminRepository.getAdminByPhone(phone) != null) {
                System.out.println("Phone number already exists");
            } else {
                // Save the new admin to the database
                Admin newAdmin = new Admin(0, adminName, phone, email, password);
                adminRepository.save(newAdmin);
                System.out.println("Admin added successfully");
            }
        }
    }

    // Method to update admin details
    public void updateAdmin(int adminID, String adminName, String phone, String email, String password) {
        // Logic to update an admin's details in the database
        // This could involve creating an Admin object and updating it in the database
        Admin updatedAdmin = new Admin(adminID, adminName, phone, email, password);
        adminRepository.update(updatedAdmin);
        System.out.println("Admin updated successfully");
    }

    // Method to delete an admin
    public void deleteAdmin(int adminID) {
        // This could involve removing the Admin object from the database using its ID
        adminRepository.deleteById(adminID);
        System.out.println("Attempting to delete admin with ID: " + adminID);
        // call the database or repository to delete the admin
        System.out.println("Admin deleted successfully");
    }

    public static void main(String[] args) {
        AuthController authControl = new AuthController();
        // Example usage of the AdminService class
        // adminService.addAdmin("John Doe", "0921384748", "a@gmail.com",
        // "password123");
        authControl.updateAdmin(6, "John Doe", "0921384748", "b@hah.com", "newpassword123");
    }
}