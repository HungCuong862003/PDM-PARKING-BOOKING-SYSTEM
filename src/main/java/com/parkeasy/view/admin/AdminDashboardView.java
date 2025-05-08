package main.java.com.parkeasy.view.admin;

import main.java.com.parkeasy.controller.admin.AdminDashboardController;
import main.java.com.parkeasy.model.Admin;
import main.java.com.parkeasy.model.ParkingSpace;
import main.java.com.parkeasy.service.AdminService;
import main.java.com.parkeasy.service.ParkingSpaceService;
import main.java.com.parkeasy.service.ReservationService;
import main.java.com.parkeasy.view.auth.LoginView;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Dashboard view for admin users
 * Shows statistics and management options for parking spaces
 */
public class AdminDashboardView extends JFrame {
    private static final Logger LOGGER = Logger.getLogger(AdminDashboardView.class.getName());
    private JLabel welcomeLabel;
    private JPanel statisticsPanel;
    private JTable parkingSpacesTable;
    private JButton profileButton;
    private JButton logoutButton;
    private JButton refreshButton;
    private JButton manageParkingButton;
    private JPanel revenuePanel;
    private JComboBox<String> revenuePeriodComboBox;
    private JLabel revenueLabel;
    private JLabel revenueTitleLabel;

    private AdminDashboardController adminDashboardController;
    private Admin currentAdmin;

    public AdminDashboardView(Admin admin) {
        this.currentAdmin = admin;

        // Initialize services and controller
        AdminService adminService = new AdminService();
        ParkingSpaceService parkingSpaceService = new ParkingSpaceService();
        ReservationService reservationService = new ReservationService();
        adminDashboardController = new AdminDashboardController(adminService, parkingSpaceService, reservationService);

        // Set up the frame
        setTitle("ParkEasy - Admin Dashboard");
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
        welcomeLabel = new JLabel("Welcome, " + currentAdmin.getAdminName() + "!");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 18));

        statisticsPanel = new JPanel();
        statisticsPanel.setBorder(BorderFactory.createTitledBorder("Statistics"));

        // Create table for parking spaces
        String[] columnNames = {"Parking ID", "Address", "Slots", "Occupied", "Occupancy Rate", "Daily Revenue"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
        };
        parkingSpacesTable = new JTable(model);
        parkingSpacesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Add double-click listener to open parking space details
        parkingSpacesTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = parkingSpacesTable.getSelectedRow();
                    if (row >= 0) {
                        String parkingId = (String) parkingSpacesTable.getValueAt(row, 0);
                        openParkingSpaceDetails(parkingId);
                    }
                }
            }
        });

        profileButton = new JButton("Profile");
        logoutButton = new JButton("Logout");
        refreshButton = new JButton("Refresh");
        manageParkingButton = new JButton("Manage Parking Spaces");

        // Create revenue selection panel
        revenuePanel = new JPanel(new BorderLayout(5, 5));
        revenueTitleLabel = new JLabel("Revenue");
        revenueTitleLabel.setFont(new Font("Arial", Font.PLAIN, 12));

        revenueLabel = new JLabel("$0.00");
        revenueLabel.setFont(new Font("Arial", Font.BOLD, 16));

        revenuePeriodComboBox = new JComboBox<>(new String[]{"Daily", "Weekly", "Monthly"});
        revenuePeriodComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateRevenueData();
            }
        });

        JPanel revenueSelectorPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        revenueSelectorPanel.add(revenuePeriodComboBox);

        revenuePanel.add(revenueTitleLabel, BorderLayout.NORTH);
        revenuePanel.add(revenueLabel, BorderLayout.CENTER);
        revenuePanel.add(revenueSelectorPanel, BorderLayout.SOUTH);

        revenuePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));

        // Add action listeners
        profileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openAdminProfile();
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
        manageParkingButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openParkingManagement();
            }
        });
    }

    private void layoutComponents() {
        setLayout(new BorderLayout(10, 10));

        // Top panel with welcome message and buttons
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        topPanel.add(welcomeLabel, BorderLayout.WEST);

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonsPanel.add(refreshButton);
        buttonsPanel.add(profileButton);
        buttonsPanel.add(manageParkingButton);
        buttonsPanel.add(logoutButton);
        topPanel.add(buttonsPanel, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        // Statistics panel in the center
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

        // Add statistics panel
        statisticsPanel.setLayout(new GridLayout(2, 3, 10, 10));
        centerPanel.add(statisticsPanel, BorderLayout.NORTH);

        // Add table with scroll pane
        JScrollPane tableScrollPane = new JScrollPane(parkingSpacesTable);
        tableScrollPane.setBorder(BorderFactory.createTitledBorder("Managed Parking Spaces"));
        centerPanel.add(tableScrollPane, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        // Add instruction panel at the bottom
        JPanel instructionPanel = new JPanel();
        JLabel instructionLabel = new JLabel("Double-click on a parking space to view details");
        instructionPanel.add(instructionLabel);
        add(instructionPanel, BorderLayout.SOUTH);
    }

    private void loadDashboardData() {
        try {
            // Show loading indicator
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

            // Get comprehensive dashboard data in a single call to reduce database queries
            Map<String, Object> dashboardSummary = adminDashboardController.getDashboardSummary(currentAdmin.getAdminID());

            if (dashboardSummary == null || !((boolean) dashboardSummary.getOrDefault("success", false))) {
                JOptionPane.showMessageDialog(this,
                        "Error retrieving dashboard data: " + dashboardSummary.getOrDefault("message", "Unknown error"),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Extract data from the summary
            int totalParkingSpaces = (int) dashboardSummary.getOrDefault("parkingSpaceCount", 0);
            int totalSlots = (int) dashboardSummary.getOrDefault("totalSlots", 0);
            int occupiedSlots = (int) dashboardSummary.getOrDefault("occupiedSlots", 0);
            int availableSlots = totalSlots - occupiedSlots;
            float occupancyRate = (float) dashboardSummary.getOrDefault("occupancyRate", 0.0f);

            // Get the list of parking spaces
            @SuppressWarnings("unchecked")
            List<ParkingSpace> parkingSpaces = (List<ParkingSpace>) dashboardSummary.getOrDefault("parkingSpaces", Collections.emptyList());

            // Update table
            updateParkingSpacesTable(parkingSpaces);

            // Update statistics panel
            statisticsPanel.removeAll();

            addStatisticItem("Total Parking Spaces", String.valueOf(totalParkingSpaces));
            addStatisticItem("Total Slots", String.valueOf(totalSlots));
            addStatisticItem("Occupied Slots", String.valueOf(occupiedSlots));
            addStatisticItem("Available Slots", String.valueOf(availableSlots));
            addStatisticItem("Occupancy Rate", String.format("%.2f%%", occupancyRate));

            // Add the revenue panel instead of single revenue item
            statisticsPanel.add(revenuePanel);

            // Initial update of revenue data
            updateRevenueData();

            statisticsPanel.revalidate();
            statisticsPanel.repaint();


        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error loading dashboard data: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        } finally {
            // Reset cursor
            setCursor(Cursor.getDefaultCursor());
        }
    }

    private void updateParkingSpacesTable(List<ParkingSpace> parkingSpaces) {
        DefaultTableModel model = (DefaultTableModel) parkingSpacesTable.getModel();
        model.setRowCount(0); // Clear table

        // Get the selected revenue period to show appropriate revenue data
        String selectedPeriod = (String) revenuePeriodComboBox.getSelectedItem();

        // Update revenue column header to reflect the period
        model.setColumnIdentifiers(new String[]{
                "Parking ID", "Address", "Slots", "Occupied",
                "Occupancy Rate", selectedPeriod + " Revenue"
        });

        for (ParkingSpace space : parkingSpaces) {
            int spaceSlots = space.getNumberOfSlots();

            // Get occupied slots count for this specific parking space
            int spaceOccupiedSlots = adminDashboardController.getOccupiedSlots(space.getParkingID());

            // Calculate occupancy rate for this parking space
            float spaceOccupancyRate = spaceSlots > 0 ? ((float) spaceOccupiedSlots / spaceSlots) * 100 : 0.0f;

            // Get revenue based on selected period
            float spaceRevenue = getParkingSpaceRevenue(space.getParkingID(), selectedPeriod);

            Object[] row = {
                    space.getParkingID(),
                    space.getParkingAddress(),
                    spaceSlots,
                    spaceOccupiedSlots,
                    String.format("%.2f%%", spaceOccupancyRate),
                    String.format("%,.0f VND", spaceRevenue)
            };

            model.addRow(row);
        }
    }

    private float getParkingSpaceRevenue(String parkingId, String period) {
        try {
            if ("Daily".equals(period)) {
                return adminDashboardController.getParkingSpaceDailyRevenue(parkingId);
            } else if ("Weekly".equals(period)) {
                return adminDashboardController.getParkingSpaceWeeklyRevenue(parkingId);
            } else if ("Monthly".equals(period)) {
                return adminDashboardController.getParkingSpaceMonthlyRevenue(parkingId);
            }
            return 0.0f;
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error getting " + period + " revenue for parking space: " + parkingId, e);
            return 0.0f;
        }
    }

    private void updateRevenueData() {
        try {
            String selectedPeriod = (String) revenuePeriodComboBox.getSelectedItem();
            float revenue = 0.0F;

            if ("Daily".equals(selectedPeriod)) {
                revenueTitleLabel.setText("Daily Revenue");
                revenue = adminDashboardController.getDailyRevenue(currentAdmin.getAdminID());
            } else if ("Weekly".equals(selectedPeriod)) {
                revenueTitleLabel.setText("Weekly Revenue");
                revenue = adminDashboardController.getWeeklyRevenue(currentAdmin.getAdminID());
            } else if ("Monthly".equals(selectedPeriod)) {
                revenueTitleLabel.setText("Monthly Revenue");
                revenue = adminDashboardController.getMonthlyRevenue(currentAdmin.getAdminID());
            }

            // Format revenue in VND with thousands separators
            revenueLabel.setText(String.format("%,.0f VND", revenue));

            // Update parking spaces table to reflect the selected period
            List<ParkingSpace> parkingSpaces = adminDashboardController.getAdminParkingSpaces(currentAdmin.getAdminID());
            updateParkingSpacesTable(parkingSpaces);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error updating revenue data: " + ex.getMessage(),
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

    private void openParkingSpaceDetails(String parkingId) {
        try {
            ParkingSpace parkingSpace = getParkingSpaceById(parkingId);
            if (parkingSpace != null) {
                new ParkingPlotDetailView(currentAdmin, parkingSpace);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error opening parking space details: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private ParkingSpace getParkingSpaceById(String parkingId) {
        List<ParkingSpace> parkingSpaces = adminDashboardController.getAdminParkingSpaces(currentAdmin.getAdminID());
        for (ParkingSpace space : parkingSpaces) {
            if (space.getParkingID().equals(parkingId)) {
                return space;
            }
        }
        return null;
    }

    private void openAdminProfile() {
        new AdminProfileView(currentAdmin);
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

    private void openParkingManagement() {
        new ParkingSpaceManagementView(currentAdmin);
    }

    // Main method for testing
    public static void main(String[] args) {
        // Create a mock admin for testing
        Admin mockAdmin = new Admin();
        mockAdmin.setAdminID(1);
        mockAdmin.setAdminName("Test Admin");

        SwingUtilities.invokeLater(() -> {
            new AdminDashboardView(mockAdmin);
        });
    }
}