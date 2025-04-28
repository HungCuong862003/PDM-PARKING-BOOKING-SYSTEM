package main.java.com.parkeasy.service;

import main.java.com.parkeasy.model.Vehicle;
import main.java.com.parkeasy.repository.VehicleRepository;
import main.java.com.parkeasy.util.Constants;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service class for managing vehicle operations
 */
public class VehicleService {
    private static final Logger LOGGER = Logger.getLogger(VehicleService.class.getName());

    private final VehicleRepository vehicleRepository;

    /**
     * Constructor for VehicleService with dependency injection
     * 
     * @param vehicleRepository The repository to use for vehicle operations
     */
    public VehicleService(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    /**
     * Default constructor that creates its own repository instance
     * For backward compatibility and simpler instantiation when DI is not available
     */
    public VehicleService() {
        this(new VehicleRepository());
    }

    /**
     * Gets all vehicles registered to a user
     * 
     * @param userId The ID of the user
     * @return List of vehicles belonging to the user, empty list if none found
     */
    public List<Vehicle> getVehiclesByUserId(int userId) {
        try {
            if (userId <= 0) {
                LOGGER.warning("Invalid user ID provided: " + userId);
                return Collections.emptyList();
            }

            return vehicleRepository.getListOfVehiclesByUserId(userId);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error retrieving vehicles for user: " + userId, e);
            return Collections.emptyList();
        }
    }

    /**
     * Gets a specific vehicle by its ID
     * 
     * @param vehicleId The ID of the vehicle
     * @return Optional containing Vehicle if found, empty Optional otherwise
     */
    public Optional<Vehicle> getVehicleById(String vehicleId) {
        try {
            if (vehicleId == null || vehicleId.trim().isEmpty()) {
                LOGGER.warning("Invalid vehicle ID provided: " + vehicleId);
                return Optional.empty();
            }

            Vehicle vehicle = vehicleRepository.getVehicleById(vehicleId);
            return Optional.ofNullable(vehicle);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error retrieving vehicle: " + vehicleId, e);
            return Optional.empty();
        }
    }

    /**
     * Adds a new vehicle
     * 
     * @param vehicle The vehicle to add
     * @return boolean indicating success or failure
     * @throws IllegalArgumentException if vehicle is null or has invalid properties
     */
    public boolean addVehicle(Vehicle vehicle) {
        try {
            // Validate vehicle object
            validateVehicle(vehicle);

            // Check if the vehicle already exists
            if (isVehicleRegistered(vehicle.getVehicleID())) {
                LOGGER.warning("Vehicle already exists: " + vehicle.getVehicleID());
                return false;
            }

            return vehicleRepository.insertVehicle(vehicle);
        } catch (IllegalArgumentException e) {
            LOGGER.warning(e.getMessage());
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error adding vehicle: " + (vehicle != null ? vehicle.getVehicleID() : "null"), e);
            return false;
        }
    }

    /**
     * Removes a vehicle
     * 
     * @param vehicleId The ID of the vehicle to remove
     * @return boolean indicating success or failure
     */
    public boolean removeVehicle(String vehicleId) {
        try {
            if (vehicleId == null || vehicleId.trim().isEmpty()) {
                LOGGER.warning("Invalid vehicle ID provided for removal: " + vehicleId);
                return false;
            }

            return vehicleRepository.deleteVehicle(vehicleId);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error removing vehicle: " + vehicleId, e);
            return false;
        }
    }

    /**
     * Checks if a vehicle is already registered
     * 
     * @param vehicleId The ID to check
     * @return boolean indicating if the vehicle is registered
     */
    public boolean isVehicleRegistered(String vehicleId) {
        try {
            if (vehicleId == null || vehicleId.trim().isEmpty()) {
                return false;
            }

            return vehicleRepository.getVehicleById(vehicleId) != null;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error checking if vehicle is registered: " + vehicleId, e);
            return false;
        }
    }

    /**
     * Checks if a vehicle belongs to a specific user
     * 
     * @param vehicleId The ID of the vehicle
     * @param userId    The ID of the user
     * @return boolean indicating if the vehicle belongs to the user
     */
    public boolean isVehicleOwnedByUser(String vehicleId, int userId) {
        try {
            if (vehicleId == null || vehicleId.trim().isEmpty() || userId <= 0) {
                return false;
            }

            Optional<Vehicle> vehicleOpt = getVehicleById(vehicleId);
            return vehicleOpt.isPresent() && vehicleOpt.get().getUserID() == userId;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error checking if vehicle is owned by user: " +
                    vehicleId + ", User: " + userId, e);
            return false;
        }
    }

    /**
     * Gets count of vehicles owned by a user
     * 
     * @param userId The ID of the user
     * @return The number of vehicles owned by the user
     */
    public int getVehicleCountByUser(int userId) {
        try {
            if (userId <= 0) {
                LOGGER.warning("Invalid user ID provided for count: " + userId);
                return 0;
            }

            return vehicleRepository.getVehicleCountByUserId(userId);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error getting vehicle count for user: " + userId, e);
            return 0;
        }
    }

    /**
     * Updates a vehicle's information
     * 
     * @param vehicleId      The ID of the vehicle to update
     * @param updatedVehicle The updated vehicle information
     * @return boolean indicating success or failure
     * @throws IllegalArgumentException if vehicle has invalid properties
     */
    public boolean updateVehicle(String vehicleId, Vehicle updatedVehicle) {
        try {
            if (vehicleId == null || vehicleId.trim().isEmpty() || updatedVehicle == null) {
                LOGGER.warning("Invalid parameters for update: vehicleId=" +
                        vehicleId + ", updatedVehicle=" + updatedVehicle);
                return false;
            }

            // Verify vehicle exists
            Optional<Vehicle> existingVehicleOpt = getVehicleById(vehicleId);
            if (existingVehicleOpt.isEmpty()) {
                LOGGER.warning("Vehicle does not exist for update: " + vehicleId);
                return false;
            }

            Vehicle existingVehicle = existingVehicleOpt.get();

            // Create a new vehicle instance to avoid modifying the input parameter
            Vehicle vehicleToUpdate = new Vehicle();
            vehicleToUpdate.setVehicleID(vehicleId);
            vehicleToUpdate.setUserID(existingVehicle.getUserID());

            // Copy appropriate fields from updated vehicle
            // Add additional fields that need to be copied as per Vehicle model

            validateVehicle(vehicleToUpdate);

            return vehicleRepository.updateVehicle(vehicleId, vehicleToUpdate);
        } catch (IllegalArgumentException e) {
            LOGGER.warning(e.getMessage());
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error updating vehicle: " + vehicleId, e);
            return false;
        }
    }

    /**
     * Gets all vehicles in the system (admin function)
     * 
     * @return List of all vehicles
     */
    public List<Vehicle> getAllVehicles() {
        try {
            return vehicleRepository.getAllVehicles();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error retrieving all vehicles", e);
            return Collections.emptyList();
        }
    }

    /**
     * Checks if a user has reached the maximum allowed vehicles
     * 
     * @param userId             The ID of the user
     * @param maxVehiclesPerUser The maximum number of vehicles allowed per user
     * @return boolean indicating if the user has reached the limit
     */
    public boolean hasReachedVehicleLimit(int userId, int maxVehiclesPerUser) {
        try {
            if (userId <= 0 || maxVehiclesPerUser <= 0) {
                LOGGER.warning("Invalid parameters: userId=" + userId +
                        ", maxVehiclesPerUser=" + maxVehiclesPerUser);
                return true; // Conservative approach - if parameters are invalid, assume limit is reached
            }

            int currentCount = getVehicleCountByUser(userId);
            return currentCount >= maxVehiclesPerUser;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error checking vehicle limit for user: " + userId, e);
            return true; // Conservative approach - if error occurs, assume limit is reached
        }
    }

    /**
     * Checks if a user has reached the maximum allowed vehicles using system
     * constant
     * 
     * @param userId The ID of the user
     * @return boolean indicating if the user has reached the limit
     */
    public boolean hasReachedVehicleLimit(int userId) {
        return hasReachedVehicleLimit(userId, Constants.MAX_VEHICLES_PER_USER);
    }

    /**
     * Validates vehicle object properties
     * 
     * @param vehicle The vehicle to validate
     * @throws IllegalArgumentException if validation fails
     */
    private void validateVehicle(Vehicle vehicle) {
        if (vehicle == null) {
            throw new IllegalArgumentException("Vehicle cannot be null");
        }

        if (vehicle.getVehicleID() == null || vehicle.getVehicleID().trim().isEmpty()) {
            throw new IllegalArgumentException("Vehicle ID cannot be empty");
        }

        if (vehicle.getUserID() <= 0) {
            throw new IllegalArgumentException("Invalid user ID for vehicle: " + vehicle.getUserID());
        }

        // Add additional validation as needed based on the Vehicle model
    }
}