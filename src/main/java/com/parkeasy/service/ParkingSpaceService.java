package main.java.com.parkeasy.service;

import main.java.com.parkeasy.model.ParkingSpace;
import main.java.com.parkeasy.repository.ParkingSpaceRepository;

import java.util.List;

public class ParkingSpaceService {
    private ParkingSpaceRepository parkingSpaceRepository;

    public ParkingSpaceService() {
        this.parkingSpaceRepository = new ParkingSpaceRepository();
    }

    // insert a new parking space into the database
    public void insertParkingSpace(ParkingSpace parkingSpace) {
        parkingSpaceRepository.insertParkingSpace(parkingSpace);
    }

    // get a parking space by its ID
    public ParkingSpace getParkingSpaceById(String parkingID) {
        return parkingSpaceRepository.getParkingSpaceById(parkingID);
    }

    // get all parking spaces
    public List<ParkingSpace> getAllParkingSpaces() {
        return parkingSpaceRepository.getAllParkingSpaces();
    }

    // get all parking spaces by admin ID
    public List<ParkingSpace> getAllParkingSpacesByAdminId(int adminID) {
        return parkingSpaceRepository.getParkingSpacesByAdminId(adminID);
    }

    // delete a parking space by its ID
    public void deleteParkingSpaceById(String parkingID) {
        parkingSpaceRepository.deleteParkingSpaceById(parkingID);
    }

    // update a parking space by its ID
    public void updateParkingSpaceById(String parkingID, ParkingSpace parkingSpace) {
        parkingSpaceRepository.updateParkingSpaceById(parkingID, parkingSpace);
    }
}