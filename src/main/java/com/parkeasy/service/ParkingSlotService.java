package main.java.com.parkeasy.service;

import main.java.com.parkeasy.model.ParkingSlot;
import main.java.com.parkeasy.repository.ParkingSlotRepository;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service class for parking slot operations
 */
public class ParkingSlotService {
    private static final Logger LOGGER = Logger.getLogger(ParkingSlotService.class.getName());
    private ParkingSlotRepository parkingSlotRepository;

    /**
     * Default constructor
     */
    public ParkingSlotService() {
        this.parkingSlotRepository = new ParkingSlotRepository();
    }

    /**
     * Insert a new parking slot into the database
     *
     * @param parkingSlot The parking slot to insert
     */
    public void insertParkingSlot(ParkingSlot parkingSlot) {
        parkingSlotRepository.addParkingSlot(parkingSlot);
    }

    /**
     * Get a parking slot by its slot number
     *
     * @param slotNumber The slot number
     * @return The parking slot or null if not found
     */
    public ParkingSlot getParkingSlotByNumber(String slotNumber) {
        return parkingSlotRepository.findParkingSlotByNumber(slotNumber);
    }

    /**
     * Get all parking slots by parking space ID
     *
     * @param parkingID The parking space ID
     * @return List of parking slots
     */
    public List<ParkingSlot> getParkingSlotsByParkingSpaceId(String parkingID) {
        return parkingSlotRepository.getParkingSlotsByParkingSpaceId(parkingID);
    }

    /**
     * Delete a parking slot by its slot number
     *
     * @param slotNumber The slot number
     */
    public void deleteParkingSlotByNumber(String slotNumber) {
        parkingSlotRepository.deleteParkingSlot(slotNumber);
    }

    /**
     * Update a parking slot by its slot number
     *
     * @param slotNumber The slot number
     * @param parkingSlot The updated parking slot data
     */
    public void updateParkingSlotByNumber(String slotNumber, ParkingSlot parkingSlot) {
        parkingSlotRepository.updateParkingSlotByNumber(slotNumber, parkingSlot);
    }
}