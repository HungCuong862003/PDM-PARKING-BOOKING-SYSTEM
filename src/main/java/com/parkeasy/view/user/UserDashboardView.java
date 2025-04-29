package main.java.com.parkeasy.view.user;

import main.java.com.parkeasy.controller.user.UserDashboardController;
import main.java.com.parkeasy.controller.user.VehicleController;
import main.java.com.parkeasy.model.Reservation;
import main.java.com.parkeasy.model.User;
import main.java.com.parkeasy.model.Vehicle;
import main.java.com.parkeasy.service.PaymentService;
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

    public UserDashboardView(User user) {
        this.currentUser = user;

        // Initialize services and controllers
        UserService userService = new UserService();
        VehicleService vehicleService = new VehicleService();
        ReservationService reservationService = new ReservationService();
        ParkingSpaceService parkingSpaceService = new ParkingSpaceService();
        PaymentService paymentService = new PaymentService();

        userDashboardController = new UserDashboardController(
                userService, vehicleService, reservationService, parkingSpaceService, paymentService);
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

        // Load data
        loadDashboardData();

        // Make the frame visible
        setVisible(true);
    }

    private void initComponents() {
        welcomeLabel = new JLabel("Welcome, " + currentUser.getUserName() + "!");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 18));

        balanceLabel = new JLabel("Balance: $" + String.format("%.2f", currentUser.getBalance()));
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

    private void loadDashboardData() {
        try {
            // Load summary data
            Map<String, Object> dashboardSummary = userDashboardController.getDashboardSummary(currentUser.getUserID());

            // Update statistics panel
            statisticsPanel.removeAll();

            // Update balance label
            balanceLabel.setText("Balance: $" + String.format("%.2f", currentUser.getBalance()));

            // Add statistics
            int vehicleCount = (int) dashboardSummary.getOrDefault("vehicleCount", 0);
            addStatisticItem("Vehicles", String.valueOf(vehicleCount));

            int activeReservationsCount = (int) dashboardSummary.getOrDefault("activeReservationCount", 0);
            addStatisticItem("Active Reservations", String.valueOf(activeReservationsCount));

            int upcomingReservations = (int) dashboardSummary.getOrDefault("upcomingReservationCount", 0);
            addStatisticItem("Upcoming Reservations", String.valueOf(upcomingReservations));

            double totalSpent = (double) dashboardSummary.getOrDefault("totalSpent", 0.0);
            addStatisticItem("Total Spent", "$" + String.format("%.2f", totalSpent));

            statisticsPanel.revalidate();
            statisticsPanel.repaint();

            // Update active reservations table
            List<Map<String, Object>> activeReservationsList =
                    (List<Map<String, Object>>) dashboardSummary.getOrDefault("activeReservations", new java.util.ArrayList<>());

            DefaultTableModel model = (DefaultTableModel) activeReservationsTable.getModel();
            model.setRowCount(0); // Clear table

            for (Map<String, Object> reservationData : activeReservationsList) {
                Reservation reservation = (Reservation) reservationData.get("reservation");
                Vehicle vehicle = (Vehicle) reservationData.get("vehicle");

                Object[] row = {
                        reservation.getReservationID(),
                        vehicle.getVehicleID(),
                        reservationData.get("parkingAddress"),
                        reservationData.get("slotNumber"),
                        reservationData.get("startDateTime").toString(),
                        reservationData.get("endDateTime").toString(),
                        reservation.getStatus()
                };

                model.addRow(row);
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error loading dashboard data: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
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
        // Create a mock user for testing
        User mockUser = new User();
        mockUser.setUserID(1);
        mockUser.setUserName("Test User");
        mockUser.setBalance(100.0);

        SwingUtilities.invokeLater(() -> {
            new UserDashboardView(mockUser);
        });
    }
}