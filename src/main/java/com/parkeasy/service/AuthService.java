package main.java.com.parkeasy.service;

import main.java.com.parkeasy.model.User;
import main.java.com.parkeasy.repository.UserRepository;

import java.util.Optional;

public class AuthService {

    private final UserRepository userRepository;

    public AuthService() {
        this.userRepository = new UserRepository();
    }

    
    public User login(String email, String password) {
        
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            
            if (user.getPassword().equals(password)) {
                System.out.println("Login successfully!");
                return user;
            } else {
                System.out.println("Invalid password or email");
            }
        } else {
            System.out.println("This email has not been registered");
        }
        return null;
    }

    
    public void logout(User user) {
        if (user != null) {
            System.out.println("Logged out!");
            
        } else {
            System.out.println("No user is logged in.");
        }
    }
}
