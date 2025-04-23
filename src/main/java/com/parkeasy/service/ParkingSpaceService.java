package main.java.com.parkeasy.service;

import main.java.com.parkeasy.model.ParkingSpace;
import main.java.com.parkeasy.repository.ParkingSpaceRepository;

import java.sql.SQLException;
import java.util.List;

public class ParkingSpaceService {
    private ParkingSpaceRepository parkingSpaceRepository;

    public ParkingSpaceService() {
        this.parkingSpaceRepository = new ParkingSpaceRepository();
    }

    // insert a new parking space into the database
    public void insertParkingSpace(ParkingSpace parkingSpace) {
        try {
            parkingSpaceRepository.addParkingSpace(parkingSpace);
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle the exception appropriately, e.g., log it or rethrow it as a runtime
            // exception
            throw new RuntimeException("Error inserting parking space", e);
        }
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

}