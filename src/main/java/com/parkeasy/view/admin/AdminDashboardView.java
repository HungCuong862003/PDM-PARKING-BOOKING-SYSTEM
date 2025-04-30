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
import java.util.List;
import java.util.Map;

/**
 * Dashboard view for admin users
 * Shows statistics and management options for parking spaces
 */
public class AdminDashboardView extends JFrame {
    private JLabel welcomeLabel;
    private JPanel statisticsPanel;
    private JTable parkingSpacesTable;
    private JButton profileButton;
    private JButton logoutButton;
    private JButton refreshButton;

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
            // Load parking spaces data first to calculate statistics properly
            List<ParkingSpace> parkingSpaces = adminDashboardController.getAdminParkingSpaces(currentAdmin.getAdminID());

            // Calculate dashboard summary based on actual parking spaces data
            int totalParkingSpaces = parkingSpaces.size();
            int totalSlots = 0;
            int occupiedSlots = 0;
            double dailyRevenue = 0.0;

            // Update table
            DefaultTableModel model = (DefaultTableModel) parkingSpacesTable.getModel();
            model.setRowCount(0); // Clear table

            for (ParkingSpace space : parkingSpaces) {
                int spaceSlots = space.getNumberOfSlots();
                totalSlots += spaceSlots;

                // Get occupied slots count for this specific parking space
                int spaceOccupiedSlots = adminDashboardController.getOccupiedSlots(space.getParkingID());
                occupiedSlots += spaceOccupiedSlots;

                // Calculate occupancy rate for this parking space
                double spaceOccupancyRate = spaceSlots > 0 ? ((double) spaceOccupiedSlots / spaceSlots) * 100 : 0.0;

                // Calculate daily revenue for this parking space
                double spaceDailyRevenue = adminDashboardController.getParkingSpaceDailyRevenue(space.getParkingID());
                dailyRevenue += spaceDailyRevenue;

                Object[] row = {
                        space.getParkingID(),
                        space.getParkingAddress(),
                        spaceSlots,
                        spaceOccupiedSlots,
                        String.format("%.2f%%", spaceOccupancyRate),
                        String.format("$%.2f", spaceDailyRevenue)
                };

                model.addRow(row);
            }

            // Calculate available slots and overall occupancy rate
            int availableSlots = totalSlots - occupiedSlots;
            double occupancyRate = totalSlots > 0 ? ((double) occupiedSlots / totalSlots) * 100 : 0.0;

            // Update statistics panel
            statisticsPanel.removeAll();

            addStatisticItem("Total Parking Spaces", String.valueOf(totalParkingSpaces));
            addStatisticItem("Total Slots", String.valueOf(totalSlots));
            addStatisticItem("Occupied Slots", String.valueOf(occupiedSlots));
            addStatisticItem("Available Slots", String.valueOf(availableSlots));
            addStatisticItem("Occupancy Rate", String.format("%.2f%%", occupancyRate));
            addStatisticItem("Daily Revenue", String.format("$%.2f", dailyRevenue));

            statisticsPanel.revalidate();
            statisticsPanel.repaint();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error loading dashboard data: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
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