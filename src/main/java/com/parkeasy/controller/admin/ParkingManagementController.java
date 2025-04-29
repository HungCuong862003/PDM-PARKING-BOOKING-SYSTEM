package main.java.com.parkeasy.controller.admin;

import main.java.com.parkeasy.model.ParkingSlot;
import main.java.com.parkeasy.model.ParkingSpace;
import main.java.com.parkeasy.service.AdminService;
import main.java.com.parkeasy.service.ParkingSlotService;
import main.java.com.parkeasy.service.ParkingSpaceService;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ParkingManagementController {
    private static final Logger LOGGER = Logger.getLogger(ParkingManagementController.class.getName());
    private final AdminService adminService;
    private final ParkingSpaceService parkingSpaceService;
    private final ParkingSlotService parkingSlotService;

    public ParkingManagementController() {
        this.adminService = new AdminService();
        this.parkingSpaceService = new ParkingSpaceService();
        this.parkingSlotService = new ParkingSlotService();
    }

    public List<ParkingSpace> getAllParkingSpaces() {
        try {
            return parkingSpaceService.getAllParkingSpaces();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error retrieving all parking spaces", e);
            return null;
        }
    }

    public ParkingSpace getParkingSpaceById(String parkingId) {
        try {
            return parkingSpaceService.getParkingSpaceById(parkingId);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error retrieving parking space by ID", e);
            return null;
        }
    }

    public boolean createParkingSpace(ParkingSpace parkingSpace) {
        try {
            return adminService.createParkingSpace(parkingSpace);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error creating parking space", e);
            return false;
        }
    }

    public boolean updateParkingSpace(ParkingSpace parkingSpace) {
        try {
            return adminService.updateParkingSpace(parkingSpace);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating parking space", e);
            return false;
        }
    }

    public boolean deleteParkingSpace(String parkingId) {
        try {
            return adminService.deleteParkingSpace(parkingId);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error deleting parking space", e);
            return false;
        }
    }

    public List<ParkingSlot> getParkingSlotsByParkingId(String parkingId) {
        try {
            return parkingSlotService.getParkingSlotsByParkingSpaceId(parkingId);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error retrieving parking slots by parking ID", e);
            return null;
        }
    }

    /**
     * Update a parking slot's availability status
     *
     * @param slotNumber The slot number to update
     * @param isAvailable New availability status
     * @return true if successful, false otherwise
     */
    public boolean updateSlotStatus(String slotNumber, boolean isAvailable) {
        try {
            return adminService.updateSlotStatus(slotNumber, isAvailable);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating slot status", e);
            return false;
        }
    }


    /**
     * Add a new parking slot
     *
     * @param parkingSlot The parking slot to add
     * @return true if successful, false otherwise
     */
    public boolean addParkingSlot(ParkingSlot parkingSlot) {
        try {
            parkingSlotService.insertParkingSlot(parkingSlot);
            return true;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error adding parking slot", e);
            return false;
        }
    }

    /**
     * Remove a parking slot by its slot number
     *
     * @param slotNumber The slot number to remove
     * @return true if successful, false otherwise
     */
    public boolean removeParkingSlot(String slotNumber) {
        try {
            parkingSlotService.deleteParkingSlotByNumber(slotNumber);
            return true;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error removing parking slot", e);
            return false;
        }
    }
}