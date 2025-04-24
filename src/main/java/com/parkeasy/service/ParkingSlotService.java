package main.java.com.parkeasy.service;

import main.java.com.parkeasy.model.ParkingSlot;
import main.java.com.parkeasy.repository.ParkingSlotRepository;

import java.util.List;

public class ParkingSlotService {
    private ParkingSlotRepository parkingSlotRepository;

    public ParkingSlotService() {
        this.parkingSlotRepository = new ParkingSlotRepository();
    }

    // insert a new parking slot into the database
    public void insertParkingSlot(ParkingSlot parkingSlot) {
        parkingSlotRepository.addParkingSlot(parkingSlot);
    }

    // get a parking slot by its ID
    public ParkingSlot getParkingSlotById(int slotID) {
        return parkingSlotRepository.findParkingSlotById(slotID);
    }

    // get all parking slots by parking space ID
    public List<ParkingSlot> getParkingSlotsByParkingSpaceId(String parkingID) {
        return parkingSlotRepository.getParkingSlotsByParkingSpaceId(parkingID);
    }

    // delete a parking slot by its ID
    public void deleteParkingSlotById(String slotID) {
        parkingSlotRepository.deleteParkingSlot(slotID);
    }

    // update a parking slot by its ID
    public void updateParkingSlotById(int slotID, ParkingSlot parkingSlot) {
        parkingSlotRepository.updateParkingSlotById(slotID, parkingSlot);
    }
}
