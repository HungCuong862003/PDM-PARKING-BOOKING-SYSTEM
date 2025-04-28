package main.java.com.parkeasy.controller.user;

import main.java.com.parkeasy.model.ParkingSpace;
import main.java.com.parkeasy.model.Reservation;
import main.java.com.parkeasy.model.Vehicle;
import main.java.com.parkeasy.service.ParkingSpaceService;
import main.java.com.parkeasy.service.ReservationService;
import main.java.com.parkeasy.service.VehicleService;
import main.java.com.parkeasy.service.PaymentService;
import main.java.com.parkeasy.repository.PaymentRepository;
import main.java.com.parkeasy.util.DateTimeUtil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller for the user dashboard which displays an overview of parking activities
 */
public class UserDashboardController {
    private ReservationService reservationService;
    private ParkingSpaceService parkingSpaceService;
    private VehicleService vehicleService;
    private PaymentService paymentService;
    private int currentUserId;

    /**
     * Constructor for UserDashboardController
     * @param userId The ID of the current logged-in user
     */
    public UserDashboardController(int userId) {
        this.reservationService = new ReservationService();
        this.parkingSpaceService = new ParkingSpaceService();
        this.vehicleService = new VehicleService();
        this.paymentService = new PaymentService(new PaymentRepository());
        this.currentUserId = userId;
    }

    /**
     * Gets all active parking information for the current user
     * @return A list of maps containing parking information for each vehicle
     */
    public List<Map<String, Object>> getActiveParkingInfo() {
        List<Map<String, Object>> parkingInfoList = new ArrayList<>();

        // Get all vehicles for the user
        List<Vehicle> userVehicles = vehicleService.getVehiclesByUserId(currentUserId);

        for (Vehicle vehicle : userVehicles) {
            // Get active reservations for this vehicle
            List<Reservation> activeReservations = reservationService.getReservationsByVehicleId(vehicle.getVehicleID());

            for (Reservation reservation : activeReservations) {
                if ("ACTIVE".equals(reservation.getStatus())) {
                    Map<String, Object> parkingInfo = new HashMap<>();

                    // Get parking space details
                    String parkingId = reservationService.getParkingIDFromSlot(reservation.getSlotID());
                    ParkingSpace parkingSpace = parkingSpaceService.getParkingSpaceById(parkingId);

                    // Calculate parking duration and cost
                    long durationInMinutes = DateTimeUtil.calculateDurationInMinutes(
                            reservation.getStartDate().toString(),
                            reservation.getStartTime().toString(),
                            reservation.getEndDate().toString(),
                            reservation.getEndTime().toString());

                    BigDecimal cost = paymentService.calculateCost(parkingSpace.getCostOfParking(), durationInMinutes);

                    // Populate parking info
                    parkingInfo.put("vehicleId", vehicle.getVehicleID());
                    parkingInfo.put("parkingAddress", parkingSpace.getParkingAddress());
                    parkingInfo.put("startDate", reservation.getStartDate());
                    parkingInfo.put("startTime", reservation.getStartTime());
                    parkingInfo.put("duration", DateTimeUtil.formatDuration(durationInMinutes));
                    parkingInfo.put("cost", cost);
                    parkingInfo.put("reservationId", reservation.getReservationID());

                    parkingInfoList.add(parkingInfo);
                }
            }
        }

        return parkingInfoList;
    }

    /**
     * Gets the count of currently active parking spots used by the user
     * @return The number of active reservations
     */
    public int getActiveParkingCount() {
        int count = 0;

        // Get all vehicles for the user
        List<Vehicle> userVehicles = vehicleService.getVehiclesByUserId(currentUserId);

        for (Vehicle vehicle : userVehicles) {
            // Get active reservations for this vehicle
            List<Reservation> activeReservations = reservationService.getReservationsByVehicleId(vehicle.getVehicleID());

            for (Reservation reservation : activeReservations) {
                if ("ACTIVE".equals(reservation.getStatus())) {
                    count++;
                }
            }
        }

        return count;
    }

    /**
     * Gets total spending on parking fees for the current user
     * @return The total amount spent on parking
     */
    public BigDecimal getTotalSpending() {
        return paymentService.getTotalPaymentsByUser(currentUserId);
    }

    /**
     * Gets the reservation history for the current user
     * @param limit The maximum number of records to return
     * @return A list of maps containing reservation history information
     */
    public List<Map<String, Object>> getReservationHistory(int limit) {
        List<Map<String, Object>> historyList = new ArrayList<>();

        // Get all reservations for the user
        List<Reservation> userReservations = reservationService.getReservationsByUser(currentUserId);

        // Sort by creation date (newest first) and limit results
        userReservations.sort((r1, r2) -> r2.getCreatedAt().compareTo(r1.getCreatedAt()));

        int count = 0;
        for (Reservation reservation : userReservations) {
            if (count >= limit) break;

            Map<String, Object> historyItem = new HashMap<>();

            // Get parking space details
            String parkingId = reservationService.getParkingIDFromSlot(reservation.getSlotID());
            ParkingSpace parkingSpace = parkingSpaceService.getParkingSpaceById(parkingId);

            // Get payment amount
            BigDecimal paymentAmount = reservationService.getPaymentAmount(reservation.getReservationID());

            // Populate history item
            historyItem.put("startDate", reservation.getStartDate());
            historyItem.put("endDate", reservation.getEndDate());
            historyItem.put("status", reservation.getStatus());
            historyItem.put("amount", paymentAmount);
            historyItem.put("reservationId", reservation.getReservationID());

            historyList.add(historyItem);
            count++;
        }

        return historyList;
    }
}