package main.java.com.parkeasy.service;

import main.java.com.parkeasy.model.Reservation;
import main.java.com.parkeasy.repository.ReservationRepository;

import java.util.List;

public class ReservationService {
    private ReservationRepository reservationRepository;

    public ReservationService() {
        this.reservationRepository = new ReservationRepository();
    }

    // insert a new reservation
    public void insertReservation(Reservation reservation) {
        reservationRepository.insertReservation(reservation);
    }

    // get a reservation by ID
    public Reservation getReservationById(int reservationID) {
        return reservationRepository.getReservationById(reservationID);
    }

    // get all reservations by vehicle ID
    public List<Reservation> getReservationsByVehicleId(String vehicleID) {
        return reservationRepository.getReservationsByVehicleId(vehicleID);
    }

    // get all reservations by parking slot ID
    public List<Reservation> getReservationsBySlotId(int slotID) {
        return reservationRepository.getReservationsByParkingSlotId(slotID);
    }

    // delete a reservation by ID
    public void deleteReservationById(int reservationID) {
        reservationRepository.deleteReservationById(reservationID);
    }

    // update a reservation by ID
    public void updateReservationById(int reservationID, Reservation reservation) {
        reservationRepository.updateReservationById(reservationID, reservation);
    }

}