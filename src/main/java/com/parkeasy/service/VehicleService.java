package main.java.com.parkeasy.service;

import main.java.com.parkeasy.model.Vehicle;
import main.java.com.parkeasy.repository.VehicleRepository;
import main.java.com.parkeasy.repository.ReservationRepository;
import main.java.com.parkeasy.util.ValidationUtils;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service class for vehicle operations
 */
public class VehicleService {
    private static final Logger LOGGER = Logger.getLogger(VehicleService.class.getName());

    private final VehicleRepository vehicleRepository;
    private final ReservationRepository reservationRepository;

    /**
     * Constructor with dependency injection
     */
    public VehicleService(VehicleRepository vehicleRepository, ReservationRepository reservationRepository) {
        this.vehicleRepository = vehicleRepository;
        this.reservationRepository = reservationRepository;
    }

    /**
     * Default constructor
     */
    public VehicleService() {
        this.vehicleRepository = new VehicleRepository();
        this.reservationRepository = new ReservationRepository();
    }

    /**
     * Add a new vehicle
     *
     * @param vehicle The vehicle to add
     * @return true if successful, false otherwise
     */
    public boolean addVehicle(Vehicle vehicle) {
        try {
            LOGGER.log(Level.INFO, "Adding vehicle: {0} for user: {1}",
                    new Object[]{vehicle.getVehicleID(), vehicle.getUserID()});

            // Validate vehicle data
            if (!isValidVehicle(vehicle)) {
                LOGGER.log(Level.WARNING, "Invalid vehicle data");
                return false;
            }

            // Check if vehicle already exists
            if (isVehicleExists(vehicle.getVehicleID())) {
                LOGGER.log(Level.WARNING, "Vehicle already exists: {0}", vehicle.getVehicleID());
                return false;
            }

            return vehicleRepository.insertVehicle(vehicle);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error adding vehicle", e);
            return false;
        }
    }

    /**
     * Get a vehicle by ID
     *
     * @param vehicleId The ID of the vehicle
     * @return The vehicle or null if not found
     */
    public Vehicle getVehicleById(String vehicleId) {
        try {
            LOGGER.log(Level.INFO, "Getting vehicle by ID: {0}", vehicleId);
            return vehicleRepository.getVehicleById(vehicleId);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting vehicle by ID", e);
            return null;
        }
    }

    /**
     * Get all vehicles for a user
     *
     * @param userId The ID of the user
     * @return List of vehicles
     */
    public List<Vehicle> getVehiclesByUserId(int userId) {
        try {
            LOGGER.log(Level.INFO, "Getting vehicles for user: {0}", userId);
            return vehicleRepository.getVehiclesByUserId(userId);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting vehicles for user", e);
            return List.of(); // Return empty list on error
        }
    }

    /**
     * Update a vehicle
     *
     * @param vehicle The vehicle with updated information
     * @return true if successful, false otherwise
     */
    public boolean updateVehicle(Vehicle vehicle) {
        try {
            LOGGER.log(Level.INFO, "Updating vehicle: {0}", vehicle.getVehicleID());

            // Validate vehicle data
            if (!isValidVehicle(vehicle)) {
                LOGGER.log(Level.WARNING, "Invalid vehicle data");
                return false;
            }

            // Check if vehicle exists
            if (!isVehicleExists(vehicle.getVehicleID())) {
                LOGGER.log(Level.WARNING, "Vehicle not found: {0}", vehicle.getVehicleID());
                return false;
            }

            return vehicleRepository.updateVehicle(vehicle);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating vehicle", e);
            return false;
        }
    }

    /**
     * Remove a vehicle
     *
     * @param vehicleId The ID of the vehicle to remove
     * @return true if successful, false otherwise
     */
    public boolean removeVehicle(String vehicleId) {
        try {
            LOGGER.log(Level.INFO, "Removing vehicle: {0}", vehicleId);

            // Check if vehicle exists
            if (!isVehicleExists(vehicleId)) {
                LOGGER.log(Level.WARNING, "Vehicle not found: {0}", vehicleId);
                return false;
            }

            // Check if vehicle has active reservations
            if (hasActiveReservations(vehicleId)) {
                LOGGER.log(Level.WARNING, "Vehicle has active reservations: {0}", vehicleId);
                return false;
            }

            return vehicleRepository.deleteVehicleById(vehicleId);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error removing vehicle", e);
            return false;
        }
    }

    /**
     * Check if a vehicle exists
     *
     * @param vehicleId The ID of the vehicle
     * @return true if exists, false otherwise
     */
    public boolean isVehicleExists(String vehicleId) {
        try {
            LOGGER.log(Level.FINE, "Checking if vehicle exists: {0}", vehicleId);
            return vehicleRepository.isVehicleExists(vehicleId);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error checking if vehicle exists", e);
            return false;
        }
    }

    /**
     * Check if a vehicle has active reservations
     *
     * @param vehicleId The ID of the vehicle
     * @return true if has active reservations, false otherwise
     */
    public boolean hasActiveReservations(String vehicleId) {
        try {
            LOGGER.log(Level.INFO, "Checking if vehicle has active reservations: {0}", vehicleId);
            return ReservationRepository.hasActiveReservations(vehicleId);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error checking if vehicle has active reservations", e);
            return true; // Assume it has reservations on error to be safe
        }
    }

    /**
     * Check if a vehicle is owned by a specific user
     *
     * @param vehicleId The ID of the vehicle
     * @param userId The ID of the user
     * @return true if owned by the user, false otherwise
     */
    public boolean isVehicleOwnedByUser(String vehicleId, int userId) {
        try {
            LOGGER.log(Level.FINE, "Checking if vehicle {0} is owned by user {1}",
                    new Object[]{vehicleId, userId});
            return vehicleRepository.isVehicleOwnedByUser(vehicleId, userId);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error checking if vehicle is owned by user", e);
            return false;
        }
    }

    /**
     * Count the number of vehicles owned by a user
     *
     * @param userId The ID of the user
     * @return Number of vehicles
     */
    public int countUserVehicles(int userId) {
        try {
            LOGGER.log(Level.FINE, "Counting vehicles for user: {0}", userId);
            return vehicleRepository.countUserVehicles(userId);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error counting user vehicles", e);
            return 0;
        }
    }

    /**
     * Get the most frequently used vehicle for a user
     *
     * @param userId The ID of the user
     * @return The vehicle or null if not found
     */
    public Vehicle getMostUsedVehicle(int userId) {
        try {
            LOGGER.log(Level.INFO, "Getting most used vehicle for user: {0}", userId);

            String vehicleId = vehicleRepository.getMostUsedVehicleForUser(userId);
            if (vehicleId != null) {
                return getVehicleById(vehicleId);
            }

            return null;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting most used vehicle for user", e);
            return null;
        }
    }

    /**
     * Set default vehicle for a user
     *
     * @param userId The ID of the user
     * @param vehicleId The ID of the vehicle to set as default
     * @return true if successful, false otherwise
     */
    public boolean setDefaultVehicle(int userId, String vehicleId) {
        try {
            LOGGER.log(Level.INFO, "Setting default vehicle {0} for user {1}",
                    new Object[]{vehicleId, userId});

            // In a real application, you would store this in a separate table
            // or add a 'default' flag to the vehicle table. Since our schema
            // doesn't have this, we'll return true to simulate success.

            // First verify that the vehicle exists and belongs to the user
            if (!isVehicleOwnedByUser(vehicleId, userId)) {
                return false;
            }

            // Simulating success
            return true;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error setting default vehicle", e);
            return false;
        }
    }

    /**
     * Get default vehicle for a user
     *
     * @param userId The ID of the user
     * @return The default vehicle or null if none set
     */
    public Vehicle getDefaultVehicle(int userId) {
        try {
            LOGGER.log(Level.INFO, "Getting default vehicle for user: {0}", userId);

            // In a real application, you would retrieve this from database
            // Since our schema doesn't have this, we'll return the most used vehicle

            return getMostUsedVehicle(userId);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error getting default vehicle", e);
            return null;
        }
    }

    /**
     * Validate vehicle data
     *
     * @param vehicle The vehicle to validate
     * @return true if valid, false otherwise
     */
    private boolean isValidVehicle(Vehicle vehicle) {
        // Check for null values
        if (vehicle == null || vehicle.getVehicleID() == null || vehicle.getUserID() <= 0) {
            return false;
        }

        // Validate vehicle ID format
        return ValidationUtils.isValidVehicleId(vehicle.getVehicleID());
    }
}