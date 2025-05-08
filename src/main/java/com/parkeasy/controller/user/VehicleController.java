package main.java.com.parkeasy.controller.user;

import main.java.com.parkeasy.model.Reservation;
import main.java.com.parkeasy.model.Vehicle;
import main.java.com.parkeasy.service.ReservationService;
import main.java.com.parkeasy.service.VehicleService;
import main.java.com.parkeasy.util.ValidationUtils;

import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controller for managing user vehicles
 */
public class VehicleController {
    private static final Logger LOGGER = Logger.getLogger(VehicleController.class.getName());

    private final VehicleService vehicleService;
    private final ReservationService reservationService;

    /**
     * Constructor with dependency injection
     */
    public VehicleController(VehicleService vehicleService, ReservationService reservationService) {
        this.vehicleService = vehicleService;
        this.reservationService = reservationService;
    }

    /**
     * Get all vehicles for a user
     *
     * @param userId User ID
     * @return List of vehicles
     */
    public List<Vehicle> getUserVehicles(int userId) {
        try {
            if (userId <= 0) {
                LOGGER.log(Level.WARNING, "Invalid user ID provided: {0}", userId);
                return Collections.emptyList();
            }

            List<Vehicle> vehicles = vehicleService.getVehiclesByUserId(userId);
            return vehicles != null ? vehicles : Collections.emptyList();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error retrieving vehicles for user: " + userId, e);
            return Collections.emptyList();
        }
    }

    /**
     * Get vehicle details
     *
     * @param vehicleId Vehicle ID
     * @param userId User ID (for verification)
     * @return Vehicle or null if not found
     */
    public Vehicle getVehicleDetails(String vehicleId, int userId) {
        try {
            if (vehicleId == null || vehicleId.isEmpty() || userId <= 0) {
                LOGGER.log(Level.WARNING, "Invalid parameters for vehicle details");
                return null;
            }

            Vehicle vehicle = vehicleService.getVehicleById(vehicleId);
            if (vehicle == null) {
                LOGGER.log(Level.WARNING, "Vehicle not found: {0}", vehicleId);
                return null;
            }

            // Verify vehicle belongs to user
            if (vehicle.getUserID() != userId) {
                LOGGER.log(Level.WARNING, "Vehicle does not belong to user: {0}", userId);
                return null;
            }

            return vehicle;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error retrieving vehicle details: " + vehicleId, e);
            return null;
        }
    }

    /**
     * Add a new vehicle for a user
     *
     * @param vehicle Vehicle to add
     * @return true if successful, false otherwise
     */
    public boolean addVehicle(Vehicle vehicle) {
        try {
            // Validate input
            if (vehicle == null || vehicle.getVehicleID() == null || vehicle.getVehicleID().isEmpty() ||
                    vehicle.getUserID() <= 0) {
                LOGGER.log(Level.WARNING, "Invalid vehicle data provided");
                return false;
            }

            // Validate vehicle ID format (e.g., license plate)
            if (!ValidationUtils.isValidVehicleId(vehicle.getVehicleID())) {
                LOGGER.log(Level.WARNING, "Invalid vehicle ID format: {0}", vehicle.getVehicleID());
                return false;
            }

            // Check if vehicle already exists
            Vehicle existingVehicle = vehicleService.getVehicleById(vehicle.getVehicleID());
            if (existingVehicle != null) {
                LOGGER.log(Level.WARNING, "Vehicle already exists: {0}", vehicle.getVehicleID());
                return false;
            }

            // Add vehicle
            boolean added = vehicleService.addVehicle(vehicle);
            if (added) {
                LOGGER.log(Level.INFO, "Vehicle added successfully: {0} for user: {1}",
                        new Object[]{vehicle.getVehicleID(), vehicle.getUserID()});
            } else {
                LOGGER.log(Level.WARNING, "Failed to add vehicle: {0}", vehicle.getVehicleID());
            }

            return added;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error adding vehicle: " + vehicle.getVehicleID(), e);
            return false;
        }
    }

    /**
     * Update vehicle details
     *
     * @param vehicle Updated vehicle data
     * @param userId User ID (for verification)
     * @return true if successful, false otherwise
     */
    public boolean updateVehicle(Vehicle vehicle, int userId) {
        try {
            // Validate input
            if (vehicle == null || vehicle.getVehicleID() == null || vehicle.getVehicleID().isEmpty() ||
                    userId <= 0) {
                LOGGER.log(Level.WARNING, "Invalid parameters for vehicle update");
                return false;
            }

            // Check if vehicle exists
            Vehicle existingVehicle = vehicleService.getVehicleById(vehicle.getVehicleID());
            if (existingVehicle == null) {
                LOGGER.log(Level.WARNING, "Vehicle not found: {0}", vehicle.getVehicleID());
                return false;
            }

            // Verify vehicle belongs to user
            if (existingVehicle.getUserID() != userId) {
                LOGGER.log(Level.WARNING, "Vehicle does not belong to user: {0}", userId);
                return false;
            }

            // Ensure userId is not changed
            vehicle.setUserID(userId);

            // Update vehicle
            boolean updated = vehicleService.updateVehicle(vehicle);
            if (updated) {
                LOGGER.log(Level.INFO, "Vehicle updated successfully: {0}", vehicle.getVehicleID());
            } else {
                LOGGER.log(Level.WARNING, "Failed to update vehicle: {0}", vehicle.getVehicleID());
            }

            return updated;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error updating vehicle: " + vehicle.getVehicleID(), e);
            return false;
        }
    }

    /**
     * Remove a vehicle
     *
     * @param vehicleId Vehicle ID to remove
     * @param userId User ID (for verification)
     * @return true if successful, false otherwise
     */
    public boolean removeVehicle(String vehicleId, int userId) {
        try {
            // Validate input
            if (vehicleId == null || vehicleId.isEmpty() || userId <= 0) {
                LOGGER.log(Level.WARNING, "Invalid parameters for vehicle removal");
                return false;
            }

            // Check if vehicle exists
            Vehicle vehicle = vehicleService.getVehicleById(vehicleId);
            if (vehicle == null) {
                LOGGER.log(Level.WARNING, "Vehicle not found: {0}", vehicleId);
                return false;
            }

            // Verify vehicle belongs to user
            if (vehicle.getUserID() != userId) {
                LOGGER.log(Level.WARNING, "Vehicle does not belong to user: {0}", userId);
                return false;
            }

            // Check if vehicle has active reservations
            List<Reservation> activeReservations = reservationService.getActiveReservationsByVehicleId(vehicleId);
            if (activeReservations != null && !activeReservations.isEmpty()) {
                LOGGER.log(Level.WARNING, "Cannot remove vehicle with active reservations: {0}", vehicleId);
                return false;
            }

            // Remove vehicle
            boolean removed = vehicleService.removeVehicle(vehicleId);
            if (removed) {
                LOGGER.log(Level.INFO, "Vehicle removed successfully: {0}", vehicleId);
            } else {
                LOGGER.log(Level.WARNING, "Failed to remove vehicle: {0}", vehicleId);
            }

            return removed;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error removing vehicle: " + vehicleId, e);
            return false;
        }
    }

    /**
     * Get reservation history for a vehicle
     *
     * @param vehicleId Vehicle ID
     * @param userId User ID (for verification)
     * @return List of reservations
     */
    public List<Reservation> getVehicleReservationHistory(String vehicleId, int userId) {
        try {
            // Validate input
            if (vehicleId == null || vehicleId.isEmpty() || userId <= 0) {
                LOGGER.log(Level.WARNING, "Invalid parameters for vehicle reservation history");
                return Collections.emptyList();
            }

            // Check if vehicle exists
            Vehicle vehicle = vehicleService.getVehicleById(vehicleId);
            if (vehicle == null) {
                LOGGER.log(Level.WARNING, "Vehicle not found: {0}", vehicleId);
                return Collections.emptyList();
            }

            // Verify vehicle belongs to user
            if (vehicle.getUserID() != userId) {
                LOGGER.log(Level.WARNING, "Vehicle does not belong to user: {0}", userId);
                return Collections.emptyList();
            }

            // Get reservations
            List<Reservation> reservations = reservationService.getReservationsByVehicleId(vehicleId);
            return reservations != null ? reservations : Collections.emptyList();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error retrieving reservation history for vehicle: " + vehicleId, e);
            return Collections.emptyList();
        }
    }

    /**
     * Check if a vehicle is currently parked
     *
     * @param vehicleId Vehicle ID
     * @param userId User ID (for verification)
     * @return true if currently parked, false otherwise
     */
    public boolean isVehicleCurrentlyParked(String vehicleId, int userId) {
        try {
            // Validate input
            if (vehicleId == null || vehicleId.isEmpty() || userId <= 0) {
                LOGGER.log(Level.WARNING, "Invalid parameters for vehicle parking check");
                return false;
            }

            // Check if vehicle exists
            Vehicle vehicle = vehicleService.getVehicleById(vehicleId);
            if (vehicle == null) {
                LOGGER.log(Level.WARNING, "Vehicle not found: {0}", vehicleId);
                return false;
            }

            // Verify vehicle belongs to user
            if (vehicle.getUserID() != userId) {
                LOGGER.log(Level.WARNING, "Vehicle does not belong to user: {0}", userId);
                return false;
            }

            // Check if there is an active reservation for this vehicle
            return reservationService.isVehicleCurrentlyParked(vehicleId);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error checking if vehicle is currently parked: " + vehicleId, e);
            return false;
        }
    }

    /**
     * Get current parking information for a vehicle
     *
     * @param vehicleId Vehicle ID
     * @param userId User ID (for verification)
     * @return Reservation object if currently parked, null otherwise
     */
    public Reservation getCurrentParkingInfo(String vehicleId, int userId) {
        try {
            // Validate input
            if (vehicleId == null || vehicleId.isEmpty() || userId <= 0) {
                LOGGER.log(Level.WARNING, "Invalid parameters for current parking info");
                return null;
            }

            // Check if vehicle exists
            Vehicle vehicle = vehicleService.getVehicleById(vehicleId);
            if (vehicle == null) {
                LOGGER.log(Level.WARNING, "Vehicle not found: {0}", vehicleId);
                return null;
            }

            // Verify vehicle belongs to user
            if (vehicle.getUserID() != userId) {
                LOGGER.log(Level.WARNING, "Vehicle does not belong to user: {0}", userId);
                return null;
            }

            // Get current active reservation
            return reservationService.getCurrentActiveReservationForVehicle(vehicleId);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error retrieving current parking info for vehicle: " + vehicleId, e);
            return null;
        }
    }

    /**
     * Set default vehicle for a user
     *
     * @param vehicleId Vehicle ID to set as default
     * @param userId User ID
     * @return true if successful, false otherwise
     */
    public boolean setDefaultVehicle(String vehicleId, int userId) {
        try {
            // Validate input
            if (vehicleId == null || vehicleId.isEmpty() || userId <= 0) {
                LOGGER.log(Level.WARNING, "Invalid parameters for setting default vehicle");
                return false;
            }

            // Check if vehicle exists
            Vehicle vehicle = vehicleService.getVehicleById(vehicleId);
            if (vehicle == null) {
                LOGGER.log(Level.WARNING, "Vehicle not found: {0}", vehicleId);
                return false;
            }

            // Verify vehicle belongs to user
            if (vehicle.getUserID() != userId) {
                LOGGER.log(Level.WARNING, "Vehicle does not belong to user: {0}", userId);
                return false;
            }

            // Set as default
            boolean updated = vehicleService.setDefaultVehicle(userId, vehicleId);
            if (updated) {
                LOGGER.log(Level.INFO, "Default vehicle set to {0} for user: {1}",
                        new Object[]{vehicleId, userId});
            } else {
                LOGGER.log(Level.WARNING, "Failed to set default vehicle for user: {0}", userId);
            }

            return updated;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error setting default vehicle: " + vehicleId, e);
            return false;
        }
    }

    /**
     * Get default vehicle for a user
     *
     * @param userId User ID
     * @return Default vehicle or null if none set
     */
    public Vehicle getDefaultVehicle(int userId) {
        try {
            if (userId <= 0) {
                LOGGER.log(Level.WARNING, "Invalid user ID provided: {0}", userId);
                return null;
            }

            return vehicleService.getDefaultVehicle(userId);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error retrieving default vehicle for user: " + userId, e);
            return null;
        }
    }
}