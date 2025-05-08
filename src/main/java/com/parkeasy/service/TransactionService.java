package main.java.com.parkeasy.service;

import main.java.com.parkeasy.model.*;
import main.java.com.parkeasy.repository.AdminRepository;
import main.java.com.parkeasy.repository.ReservationRepository;
import main.java.com.parkeasy.repository.TransactionRepository;
import main.java.com.parkeasy.repository.UserRepository;
import main.java.com.parkeasy.repository.ParkingSlotRepository;
import main.java.com.parkeasy.util.Constants;
import main.java.com.parkeasy.util.TransactionUtil;
import main.java.com.parkeasy.repository.VehicleRepository;


import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service class for handling transaction-related business logic
 */
public class TransactionService {
    private static final Logger LOGGER = Logger.getLogger(TransactionService.class.getName());

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final AdminRepository adminRepository;
    private final ReservationRepository reservationRepository;
    private final ParkingSlotRepository parkingSlotRepository;

    /**
     * Constructor with dependency injection
     */
    public TransactionService(TransactionRepository transactionRepository,
                              UserRepository userRepository,
                              AdminRepository adminRepository,
                              ReservationRepository reservationRepository,
                              ParkingSlotRepository parkingSlotRepository) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
        this.adminRepository = adminRepository;
        this.reservationRepository = reservationRepository;
        this.parkingSlotRepository = parkingSlotRepository;
    }

    /**
     * Default constructor
     */
    public TransactionService() {
        this.transactionRepository = new TransactionRepository();
        this.userRepository = new UserRepository();
        this.adminRepository = new AdminRepository();
        this.reservationRepository = new ReservationRepository();
        this.parkingSlotRepository = new ParkingSlotRepository();
    }

    /**
     * Process a payment for a reservation
     * This method:
     * 1. Creates a transaction record
     * 2. Updates the user's balance (deducts the fee)
     * 3. Updates the admin's balance (adds the fee)
     * 4. Updates the reservation status to paid
     *
     * @param reservationId ID of the reservation
     * @return true if successful, false otherwise
     */
    public boolean processPayment(int reservationId) {
        try {
            LOGGER.log(Level.INFO, "Processing payment for reservation: {0}", reservationId);

            // Get reservation
            Reservation reservation = reservationRepository.getReservationById(reservationId);
            if (reservation == null) {
                LOGGER.log(Level.WARNING, "Reservation not found: {0}", reservationId);
                return false;
            }

            // Get user who owns the vehicle
            String vehicleId = reservation.getVehicleID();
            User user = userRepository.findUserByVehicleId(vehicleId);
            if (user == null) {
                LOGGER.log(Level.WARNING, "User not found for vehicle: {0}", vehicleId);
                return false;
            }

            // Get admin who owns the parking space
            String slotNumber = reservation.getSlotNumber();
            String parkingId = parkingSlotRepository.getParkingIdBySlotNumber(slotNumber);
            if (parkingId == null) {
                LOGGER.log(Level.WARNING, "Parking ID not found for slot: {0}", slotNumber);
                return false;
            }

            int adminId = reservationRepository.getAdminIdByParkingId(parkingId);
            Admin admin = adminRepository.getAdminById(adminId);
            if (admin == null) {
                LOGGER.log(Level.WARNING, "Admin not found for parking ID: {0}", parkingId);
                return false;
            }

            // Check if user has sufficient balance
            if (user.getBalance() < reservation.getFee()) {
                LOGGER.log(Level.WARNING, "User {0} has insufficient balance for reservation {1}",
                        new Object[]{user.getUserID(), reservationId});
                return false;
            }

            // Create transaction
            Transaction transaction = new Transaction();
            transaction.setAmount(reservation.getFee());
            transaction.setReservationID(reservationId);

            // Save transaction
            boolean transactionCreated = transactionRepository.createTransaction(transaction);
            if (!transactionCreated) {
                LOGGER.log(Level.WARNING, "Failed to create transaction for reservation: {0}", reservationId);
                return false;
            }

            // Transfer funds from user to admin
            user.setBalance(user.getBalance() - reservation.getFee());
            admin.setBalance(admin.getBalance() + reservation.getFee());

            // Update user and admin
            boolean userUpdated = userRepository.updateUser(user);
            boolean adminUpdated = adminRepository.updateAdmin(admin);

            if (!userUpdated || !adminUpdated) {
                LOGGER.log(Level.WARNING, "Failed to update user/admin balances for reservation: {0}", reservationId);
                return false;
            }

            // Update reservation status
            reservation.setStatus(Constants.RESERVATION_PAID);
            boolean reservationUpdated = reservationRepository.updateReservationById(reservationId, reservation);

            if (!reservationUpdated) {
                LOGGER.log(Level.WARNING, "Failed to update reservation status: {0}", reservationId);
                return false;
            }

            LOGGER.log(Level.INFO, "Payment processed successfully for reservation: {0}", reservationId);
            return true;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error processing payment for reservation: " + reservationId, e);
            return false;
        }
    }

    /**
     * Get a transaction by ID
     *
     * @param transactionId ID of the transaction
     * @return The transaction or null if not found
     */
    public Transaction getTransactionById(int transactionId) {
        try {
            LOGGER.log(Level.INFO, "Getting transaction by ID: {0}", transactionId);
            return transactionRepository.getTransactionById(transactionId);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error getting transaction by ID: " + transactionId, e);
            return null;
        }
    }

    /**
     * Get transactions for a specific reservation
     *
     * @param reservationId ID of the reservation
     * @return List of transactions
     */
    public List<Transaction> getTransactionsByReservationId(int reservationId) {
        try {
            LOGGER.log(Level.INFO, "Getting transactions for reservation: {0}", reservationId);
            return transactionRepository.getTransactionsByReservationId(reservationId);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error getting transactions for reservation: " + reservationId, e);
            return List.of(); // Return empty list on error
        }
    }

    /**
     * Get transactions for an admin
     *
     * @param adminId ID of the admin
     * @return List of transactions
     */
    public List<Transaction> getTransactionsByAdminId(int adminId) {
        try {
            LOGGER.log(Level.INFO, "Getting transactions for admin: {0}", adminId);
            return transactionRepository.getTransactionsByAdminId(adminId);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error getting transactions for admin: " + adminId, e);
            return List.of(); // Return empty list on error
        }
    }

    /**
     * Calculate total revenue for an admin
     *
     * @param adminId ID of the admin
     * @return Total revenue
     */
    public float calculateTotalRevenueForAdmin(int adminId) {
        try {
            LOGGER.log(Level.INFO, "Calculating total revenue for admin: {0}", adminId);
            return transactionRepository.calculateTotalRevenueForAdmin(adminId);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error calculating total revenue for admin: " + adminId, e);
            return 0.0f;
        }
    }

    /**
     * Get transactions for an admin within a date range
     *
     * @param adminId ID of the admin
     * @param startDate Start date of the range
     * @param endDate End date of the range
     * @return List of transactions within the date range
     */
    public List<Transaction> getTransactionsByAdminIdAndDateRange(int adminId, LocalDateTime startDate, LocalDateTime endDate) {
        try {
            LOGGER.log(Level.INFO, "Getting transactions for admin {0} between {1} and {2}",
                    new Object[]{adminId, startDate, endDate});

            List<Transaction> allTransactions = transactionRepository.getTransactionsByAdminId(adminId);
            List<Transaction> filteredTransactions = new ArrayList<>();

            for (Transaction transaction : allTransactions) {
                Reservation reservation = reservationRepository.getReservationById(transaction.getReservationID());
                if (reservation != null) {
                    LocalDateTime reservationDateTime = reservation.getCreatedAt().toLocalDateTime();
                    if (reservationDateTime.isAfter(startDate) && reservationDateTime.isBefore(endDate)) {
                        filteredTransactions.add(transaction);
                    }
                }
            }

            return filteredTransactions;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error getting transactions for admin by date range: " + adminId, e);
            return List.of(); // Return empty list on error
        }
    }

    /**
     * Calculate total revenue for an admin within a date range
     *
     * @param adminId ID of the admin
     * @param startDate Start date of the range
     * @param endDate End date of the range
     * @return Total revenue within the date range
     */
    public float calculateRevenueForAdminByDateRange(int adminId, LocalDateTime startDate, LocalDateTime endDate) {
        try {
            LOGGER.log(Level.INFO, "Calculating revenue for admin {0} between {1} and {2}",
                    new Object[]{adminId, startDate, endDate});

            List<Transaction> transactions = getTransactionsByAdminIdAndDateRange(adminId, startDate, endDate);
            float totalRevenue = 0.0f;

            for (Transaction transaction : transactions) {
                totalRevenue += transaction.getAmount();
            }

            return totalRevenue;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error calculating revenue for admin by date range: " + adminId, e);
            return 0.0f;
        }
    }

    /**
     * Get transactions for a specific parking space
     *
     * @param parkingId ID of the parking space
     * @return List of transactions
     */
    public List<Transaction> getTransactionsByParkingId(String parkingId) {
        try {
            LOGGER.log(Level.INFO, "Getting transactions for parking space: {0}", parkingId);

            // Get all slots for this parking space
            List<ParkingSlot> slots = parkingSlotRepository.getParkingSlotsByParkingId(parkingId);
            List<Transaction> transactions = new ArrayList<>();

            for (ParkingSlot slot : slots) {
                // Get reservations for this slot
                List<Reservation> reservations = reservationRepository.getReservationsByParkingSlotNumber(slot.getSlotNumber());

                for (Reservation reservation : reservations) {
                    // Get transactions for this reservation
                    List<Transaction> reservationTransactions = transactionRepository.getTransactionsByReservationId(reservation.getReservationID());
                    transactions.addAll(reservationTransactions);
                }
            }

            return transactions;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error getting transactions for parking space: " + parkingId, e);
            return List.of(); // Return empty list on error
        }
    }

    /**
     * Get transactions for a specific parking space within a date range
     *
     * @param parkingId ID of the parking space
     * @param startDate Start date of the range
     * @param endDate End date of the range
     * @return List of transactions within the date range
     */
    public List<Transaction> getTransactionsByParkingIdAndDateRange(String parkingId, LocalDateTime startDate, LocalDateTime endDate) {
        try {
            LOGGER.log(Level.INFO, "Getting transactions for parking space {0} between {1} and {2}",
                    new Object[]{parkingId, startDate, endDate});

            List<Transaction> allTransactions = getTransactionsByParkingId(parkingId);
            List<Transaction> filteredTransactions = new ArrayList<>();

            for (Transaction transaction : allTransactions) {
                Reservation reservation = reservationRepository.getReservationById(transaction.getReservationID());
                if (reservation != null) {
                    LocalDateTime reservationDateTime = reservation.getCreatedAt().toLocalDateTime();
                    if (reservationDateTime.isAfter(startDate) && reservationDateTime.isBefore(endDate)) {
                        filteredTransactions.add(transaction);
                    }
                }
            }

            return filteredTransactions;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error getting transactions for parking space by date range: " + parkingId, e);
            return List.of(); // Return empty list on error
        }
    }

    /**
     * Calculate total revenue for a parking space
     *
     * @param parkingId ID of the parking space
     * @return Total revenue
     */
    public float calculateTotalRevenueForParkingSpace(String parkingId) {
        try {
            LOGGER.log(Level.INFO, "Calculating total revenue for parking space: {0}", parkingId);

            List<Transaction> transactions = getTransactionsByParkingId(parkingId);
            float totalRevenue = 0.0f;

            for (Transaction transaction : transactions) {
                totalRevenue += transaction.getAmount();
            }

            return totalRevenue;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error calculating total revenue for parking space: " + parkingId, e);
            return 0.0f;
        }
    }

    /**
     * Calculate total revenue for a parking space within a date range
     *
     * @param parkingId ID of the parking space
     * @param startDate Start date of the range
     * @param endDate End date of the range
     * @return Total revenue within the date range
     */
    public float calculateRevenueForParkingSpaceByDateRange(String parkingId, LocalDateTime startDate, LocalDateTime endDate) {
        try {
            LOGGER.log(Level.INFO, "Calculating revenue for parking space {0} between {1} and {2}",
                    new Object[]{parkingId, startDate, endDate});

            List<Transaction> transactions = getTransactionsByParkingIdAndDateRange(parkingId, startDate, endDate);
            float totalRevenue = 0.0f;

            for (Transaction transaction : transactions) {
                totalRevenue += transaction.getAmount();
            }

            return totalRevenue;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error calculating revenue for parking space by date range: " + parkingId, e);
            return 0.0f;
        }
    }

    /**
     * Get transactions for a specific user
     *
     * @param userId ID of the user
     * @return List of transactions
     */
    public List<Transaction> getTransactionsByUserId(int userId) {
        try {
            LOGGER.log(Level.INFO, "Getting transactions for user: {0}", userId);

            // Get user's vehicles
            List<Vehicle> vehicles = VehicleRepository.getVehiclesByUserId(userId);
            List<Transaction> transactions = new ArrayList<>();

            for (Vehicle vehicle : vehicles) {
                // Get reservations for this vehicle
                List<Reservation> reservations = reservationRepository.getReservationsByVehicleId(vehicle.getVehicleID());

                for (Reservation reservation : reservations) {
                    // Get transactions for this reservation
                    List<Transaction> reservationTransactions = transactionRepository.getTransactionsByReservationId(reservation.getReservationID());
                    transactions.addAll(reservationTransactions);
                }
            }

            return transactions;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error getting transactions for user: " + userId, e);
            return List.of(); // Return empty list on error
        }
    }

    /**
     * Create a new transaction
     *
     * @param transaction The transaction to create
     * @return true if successful, false otherwise
     */
    public boolean createTransaction(Transaction transaction) {
        try {
            LOGGER.log(Level.INFO, "Creating new transaction for reservation: {0}", transaction.getReservationID());
            return transactionRepository.createTransaction(transaction);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error creating transaction", e);
            return false;
        }
    }
}