package main.java.com.parkeasy.service;

import main.java.com.parkeasy.model.ParkingSlot;
import main.java.com.parkeasy.repository.ParkingSlotRepository;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Service class for handling parking slot operations in the ParkEasy system.
 * Provides functionality for creating, retrieving, updating, and deleting
 * parking slots.
 */
public class ParkingSlotService {
    private final ParkingSlotRepository parkingSlotRepository;

    /**
     * Constructor for ParkingSlotService.
     */
    public ParkingSlotService() {
        this.parkingSlotRepository = new ParkingSlotRepository();
    }

    /**
     * Inserts a new parking slot into the database.
     *
     * @param parkingSlot The parking slot to insert
     * @return true if insertion was successful, false otherwise
     */
    public boolean insertParkingSlot(ParkingSlot parkingSlot) {
        // Validate input
        if (parkingSlot == null || parkingSlot.getParkingID() == null || parkingSlot.getSlotNumber() == null) {
            System.err.println("Invalid parking slot data provided");
            return false;
        }

        try {
            return parkingSlotRepository.insertParkingSlot(parkingSlot);
        } catch (Exception e) {
            System.err.println("Error inserting parking slot: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Gets a parking slot by its ID.
     *
     * @param slotID The ID of the parking slot to retrieve
     * @return The parking slot if found, null otherwise
     */
    public ParkingSlot getParkingSlotById(int slotID) {
        if (slotID <= 0) {
            System.err.println("Invalid slot ID provided");
            return null;
        }

        try {
            return parkingSlotRepository.getParkingSlotById(slotID);
        } catch (Exception e) {
            System.err.println("Error retrieving parking slot: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Gets all parking slots for a specific parking space.
     *
     * @param parkingID The ID of the parking space
     * @return List of parking slots in the specified parking space
     */
    public List<ParkingSlot> getParkingSlotsByParkingSpaceId(String parkingID) {
        if (parkingID == null || parkingID.trim().isEmpty()) {
            System.err.println("Invalid parking ID provided");
            return new ArrayList<>();
        }

        try {
            return parkingSlotRepository.getListOfParkingSlotsByParkingSpaceId(parkingID);
        } catch (Exception e) {
            System.err.println("Error retrieving parking slots: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Deletes a parking slot by its ID.
     *
     * @param slotID The ID of the parking slot to delete
     * @return true if deletion was successful, false otherwise
     */
    public boolean deleteParkingSlotById(int slotID) {
        if (slotID <= 0) {
            System.err.println("Invalid slot ID provided");
            return false;
        }

        try {
            return parkingSlotRepository.deleteParkingSlotById(slotID);
        } catch (Exception e) {
            System.err.println("Error deleting parking slot: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Updates a parking slot by its ID.
     *
     * @param slotID      The ID of the parking slot to update
     * @param parkingSlot The updated parking slot information
     * @return true if update was successful, false otherwise
     */
    public boolean updateParkingSlotById(int slotID, ParkingSlot parkingSlot) {
        // Validate inputs
        if (slotID <= 0 || parkingSlot == null) {
            System.err.println("Invalid parking slot data provided");
            return false;
        }

        try {
            return parkingSlotRepository.updateParkingSlotById(slotID, parkingSlot);
        } catch (Exception e) {
            System.err.println("Error updating parking slot: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Updates the availability status of a parking slot.
     *
     * @param slotID       The ID of the parking slot to update
     * @param availability The new availability status
     * @return true if update was successful, false otherwise
     */
    public boolean updateSlotAvailability(int slotID, boolean availability) {
        if (slotID <= 0) {
            System.err.println("Invalid slot ID provided");
            return false;
        }

        try {
            ParkingSlot slot = parkingSlotRepository.getParkingSlotById(slotID);
            if (slot == null) {
                System.err.println("Parking slot not found with ID: " + slotID);
                return false;
            }

            slot.setAvailability(availability);
            return parkingSlotRepository.updateParkingSlotById(slotID, slot);
        } catch (Exception e) {
            System.err.println("Error updating slot availability: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Counts the number of available slots in a parking space.
     *
     * @param parkingID The ID of the parking space
     * @return The number of available slots, or -1 if an error occurs
     */
    public int countAvailableSlots(String parkingID) {
        if (parkingID == null || parkingID.trim().isEmpty()) {
            System.err.println("Invalid parking ID provided");
            return -1;
        }

        try {
            List<ParkingSlot> slots = parkingSlotRepository.getListOfParkingSlotsByParkingSpaceId(parkingID);
            return (int) slots.stream().filter(ParkingSlot::isAvailability).count();
        } catch (Exception e) {
            System.err.println("Error counting available slots: " + e.getMessage());
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * Gets the next available slot in a parking space.
     *
     * @param parkingID The ID of the parking space
     * @return The next available parking slot, or null if none available
     */
    public ParkingSlot getNextAvailableSlot(String parkingID) {
        if (parkingID == null || parkingID.trim().isEmpty()) {
            System.err.println("Invalid parking ID provided");
            return null;
        }

        try {
            List<ParkingSlot> slots = parkingSlotRepository.getListOfParkingSlotsByParkingSpaceId(parkingID);
            return slots.stream()
                    .filter(ParkingSlot::isAvailability)
                    .findFirst()
                    .orElse(null);
        } catch (Exception e) {
            System.err.println("Error finding available slot: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}