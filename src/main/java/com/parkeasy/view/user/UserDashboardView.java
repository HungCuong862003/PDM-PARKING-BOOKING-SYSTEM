package main.java.com.parkeasy.view.user;

import main.java.com.parkeasy.controller.user.UserDashboardController;
import main.java.com.parkeasy.controller.user.VehicleController;
import main.java.com.parkeasy.model.Reservation;
import main.java.com.parkeasy.model.User;
import main.java.com.parkeasy.model.Vehicle;
import main.java.com.parkeasy.service.ParkingSpaceService;
import main.java.com.parkeasy.service.ReservationService;
import main.java.com.parkeasy.service.UserService;
import main.java.com.parkeasy.service.VehicleService;
import main.java.com.parkeasy.view.auth.LoginView;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * Dashboard view for regular users
 * Shows active reservations, vehicle information, and balance
 */
public class UserDashboardView extends JFrame {
    private JLabel welcomeLabel;
    private JLabel balanceLabel;
    private JPanel statisticsPanel;
    private JTable activeReservationsTable;
    private JButton searchParkingButton;
    private JButton profileButton;
    private JButton vehiclesButton;
    private JButton historyButton;
    private JButton logoutButton;
    private JButton refreshButton;

    private UserDashboardController userDashboardController;
    private VehicleController vehicleController;
    private User currentUser;
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public UserDashboardView(User user) {
        this.currentUser = user;

        // Initialize services - we'll use constructor injection rather than creating new instances
        // This allows for proper dependency injection and testing
        UserService userService = new UserService();
        VehicleService vehicleService = new VehicleService();
        ReservationService reservationService = new ReservationService();
        ParkingSpaceService parkingSpaceService = new ParkingSpaceService();

        userDashboardController = new UserDashboardController(
                userService, vehicleService, reservationService, parkingSpaceService);
        vehicleController = new VehicleController(vehicleService, reservationService);

        // Set up the frame
        setTitle("ParkEasy - User Dashboard");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create components
        initComponents();

        // Layout the components
        layoutComponents();

        // Load data from database
        loadDashboardData();

        // Make the frame visible
        setVisible(true);
    }

    private void initComponents() {
        // Fetch fresh user data from database to ensure we have up-to-date information
        currentUser = refreshUserData(currentUser.getUserID());

        welcomeLabel = new JLabel("Welcome, " + currentUser.getUserName() + "!");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 18));

        balanceLabel = new JLabel("Balance: " + String.format("%,.0f VND", currentUser.getBalance()));
        balanceLabel.setFont(new Font("Arial", Font.BOLD, 16));

        statisticsPanel = new JPanel();
        statisticsPanel.setBorder(BorderFactory.createTitledBorder("Quick Stats"));

        // Create table for active reservations
        String[] columnNames = {"Reservation ID", "Vehicle", "Parking Address", "Slot", "Start Time", "End Time", "Status"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
        };
        activeReservationsTable = new JTable(model);
        activeReservationsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Buttons
        searchParkingButton = new JButton("Search Parking");
        profileButton = new JButton("Profile");
        vehiclesButton = new JButton("My Vehicles");
        historyButton = new JButton("Reservation History");
        logoutButton = new JButton("Logout");
        refreshButton = new JButton("Refresh");

        // Add action listeners
        searchParkingButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openParkingSearch();
            }
        });

        profileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openUserProfile();
            }
        });

        vehiclesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openVehicleManagement();
            }
        });

        historyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openReservationHistory();
            }
        });

        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logout();
            }
        });

        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadDashboardData();
            }
        });
    }

    private void layoutComponents() {
        setLayout(new BorderLayout(10, 10));

        // Top panel with welcome message and balance
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        topPanel.add(welcomeLabel, BorderLayout.WEST);

        JPanel topRightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        topRightPanel.add(balanceLabel);

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonsPanel.add(refreshButton);
        buttonsPanel.add(profileButton);
        buttonsPanel.add(logoutButton);

        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.add(topRightPanel, BorderLayout.NORTH);
        rightPanel.add(buttonsPanel, BorderLayout.SOUTH);

        topPanel.add(rightPanel, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        // Center panel with statistics and active reservations
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

        // Add statistics panel
        statisticsPanel.setLayout(new GridLayout(1, 4, 10, 10));
        centerPanel.add(statisticsPanel, BorderLayout.NORTH);

        // Add table with scroll pane
        JScrollPane tableScrollPane = new JScrollPane(activeReservationsTable);
        tableScrollPane.setBorder(BorderFactory.createTitledBorder("Active Reservations"));
        centerPanel.add(tableScrollPane, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        // Bottom panel with action buttons
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        bottomPanel.add(searchParkingButton);
        bottomPanel.add(vehiclesButton);
        bottomPanel.add(historyButton);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    /**
     * Refreshes user data from the database
     * @param userId The user ID
     * @return Updated User object
     */
    private User refreshUserData(int userId) {
        try {
            UserService userService = new UserService();
            User updatedUser = userService.getUserById(userId);
            if (updatedUser == null) {
                throw new Exception("User not found in database");
            }
            return updatedUser;
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error refreshing user data: " + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);

            // Return the current user if we couldn't refresh
            return currentUser;
        }
    }

    /**
     * Load dashboard data from database
     */
    private void loadDashboardData() {
        try {
            // Refresh user data to get current balance
            currentUser = refreshUserData(currentUser.getUserID());

            // Update welcome and balance labels
            welcomeLabel.setText("Welcome, " + currentUser.getUserName() + "!");
            balanceLabel.setText("Balance: " + String.format("%,.0f VND", currentUser.getBalance()));

            // Load vehicle count from the database
            VehicleService vehicleService = new VehicleService();
            List<Vehicle> userVehicles = vehicleService.getVehiclesByUserId(currentUser.getUserID());
            int vehicleCount = userVehicles.size();

            // Load reservation data from the database
            ReservationService reservationService = new ReservationService();

            // Get active reservations count
            int activeReservationsCount = reservationService.countActiveReservationsForUser(currentUser.getUserID());

            // Get upcoming reservations (using appropriate method from ReservationService)
            // This is a placeholder - you'll need to implement this method in ReservationService
            int upcomingReservations = getUpcomingReservationsCount(reservationService, currentUser.getUserID());

            // Calculate total spent on reservations
            float totalSpent = calculateTotalSpent(reservationService, currentUser.getUserID());

            // Update statistics panel
            statisticsPanel.removeAll();
            addStatisticItem("Vehicles", String.valueOf(vehicleCount));
            addStatisticItem("Active Reservations", String.valueOf(activeReservationsCount));
            addStatisticItem("Upcoming Reservations", String.valueOf(upcomingReservations));
            addStatisticItem("Total Spent", String.format("%,.0f VND", totalSpent));

            statisticsPanel.revalidate();
            statisticsPanel.repaint();

            // Update active reservations table
            updateActiveReservationsTable(reservationService, vehicleService);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error loading dashboard data: " + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    /**
     * Get count of upcoming reservations from database
     */
    private int getUpcomingReservationsCount(ReservationService reservationService, int userId) {
        try {
            // Get all reservations for this user
            List<Reservation> allReservations = reservationService.getReservationsByUserId(userId);

            // Current time
            LocalDateTime now = LocalDateTime.now();

            // Count upcoming reservations (those that haven't started yet)
            int count = 0;
            for (Reservation reservation : allReservations) {
                // Convert SQL date/time to LocalDateTime
                LocalDateTime startDateTime = LocalDateTime.of(
                        reservation.getStartDate().toLocalDate(),
                        reservation.getStartTime().toLocalTime()
                );

                // If start time is in the future, it's an upcoming reservation
                if (startDateTime.isAfter(now)) {
                    count++;
                }
            }

            return count;
        } catch (Exception ex) {
            System.err.println("Error counting upcoming reservations: " + ex.getMessage());
            ex.printStackTrace();
            return 0;
        }
    }

    /**
     * Calculate total spent on reservations by a user
     * @param reservationService The reservation service
     * @param userId The user ID
     * @return Total amount spent on completed reservations
     */
    private float calculateTotalSpent(ReservationService reservationService, int userId) {
        try {
            // Get all reservations for this user
            List<Reservation> userReservations = reservationService.getReservationsByUserId(userId);

            // Sum up the fees for completed reservations
            float total = 0.0f;
            for (Reservation reservation : userReservations) {
                // Only count completed reservations based on the schema's Status field
                if (reservation.getStatus().equals("Completed")) {
                    total += reservation.getFee();
                }
            }

            return total;
        } catch (Exception ex) {
            System.err.println("Error calculating total spent: " + ex.getMessage());
            ex.printStackTrace();
            return 0.0f;
        }
    }

    /**
     * Update the active reservations table with data from database
     */
    private void updateActiveReservationsTable(ReservationService reservationService, VehicleService vehicleService) {
        try {
            // Get all reservations for this user
            List<Reservation> allReservations = reservationService.getReservationsByUserId(currentUser.getUserID());

            // Clear the table
            DefaultTableModel model = (DefaultTableModel) activeReservationsTable.getModel();
            model.setRowCount(0);

            // Current time
            LocalDateTime now = LocalDateTime.now();

            // Add active reservations to the table
            for (Reservation reservation : allReservations) {
                // Match the exact status values from your database - "Processing" instead of "IN_PROCESS"
                if (reservation.getStatus().equals("Processing")) {
                    // Convert SQL date/time to LocalDateTime
                    LocalDateTime startDateTime = LocalDateTime.of(
                            reservation.getStartDate().toLocalDate(),
                            reservation.getStartTime().toLocalTime()
                    );

                    LocalDateTime endDateTime = LocalDateTime.of(
                            reservation.getEndDate().toLocalDate(),
                            reservation.getEndTime().toLocalTime()
                    );

                    // Only show reservations that haven't ended yet
                    if (endDateTime.isAfter(now)) {
                        // Get vehicle information
                        Vehicle vehicle = vehicleService.getVehicleById(reservation.getVehicleID());

                        // Get parking address
                        String parkingAddress = reservationService.getParkingAddressByParkingId(
                                reservationService.getParkingIdBySlotNumber(reservation.getSlotNumber()));

                        // Add row to table
                        Object[] row = {
                                reservation.getReservationID(),
                                vehicle != null ? vehicle.getVehicleID() : "Unknown",
                                parkingAddress != null ? parkingAddress : "Unknown",
                                reservation.getSlotNumber(),
                                startDateTime.format(dateTimeFormatter),
                                endDateTime.format(dateTimeFormatter),
                                reservation.getStatus()
                        };

                        model.addRow(row);
                    }
                }
            }
        } catch (Exception ex) {
            System.err.println("Error updating active reservations table: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void addStatisticItem(String label, String value) {
        JPanel itemPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel(label);
        titleLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 16));

        itemPanel.add(titleLabel, BorderLayout.NORTH);
        itemPanel.add(valueLabel, BorderLayout.CENTER);
        itemPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));

        statisticsPanel.add(itemPanel);
    }

    private void openParkingSearch() {
        new ParkingPlotListView(currentUser);
    }

    private void openUserProfile() {
        new UserProfileView(currentUser);
    }

    private void openVehicleManagement() {
        new VehicleManagementView(currentUser);
    }

    private void openReservationHistory() {
        new ReservationHistoryView(currentUser);
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to logout?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
            new LoginView();
        }
    }

    // Main method for testing
    public static void main(String[] args) {
        // For testing only - normally the user would come from login
        try {
            UserService userService = new UserService();
            User testUser = userService.getUserById(1); // Get a real user from database

            if (testUser == null) {
                System.err.println("Test user not found in database");
                return;
            }

            SwingUtilities.invokeLater(() -> {
                new UserDashboardView(testUser);
            });
        } catch (Exception ex) {
            System.err.println("Error starting application: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}