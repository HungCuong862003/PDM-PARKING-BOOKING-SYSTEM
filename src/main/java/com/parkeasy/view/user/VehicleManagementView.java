package main.java.com.parkeasy.view.user;

import main.java.com.parkeasy.controller.user.VehicleController;
import main.java.com.parkeasy.model.User;
import main.java.com.parkeasy.model.Vehicle;
import main.java.com.parkeasy.service.ReservationService;
import main.java.com.parkeasy.service.VehicleService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * View for managing user vehicles
 * Allows users to add, view, and remove vehicles
 */
public class VehicleManagementView extends JFrame {
    private User currentUser;
    private VehicleController vehicleController;
    
    private JTable vehiclesTable;
    private JButton addVehicleButton;
    private JButton removeVehicleButton;
    private JButton setDefaultButton;
    private JButton backButton;
    
    public VehicleManagementView(User user) {
        this.currentUser = user;
        
        // Initialize services and controller
        VehicleService vehicleService = new VehicleService();
        ReservationService reservationService = new ReservationService();
        vehicleController = new VehicleController(vehicleService, reservationService);
        
        // Set up the frame
        setTitle("ParkEasy - Vehicle Management");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Create components
        initComponents();
        
        // Layout the components
        layoutComponents();
        
        // Load data
        loadVehicles();
        
        // Make the frame visible
        setVisible(true);
    }
    
    private void initComponents() {
        // Create table for vehicles
        String[] columnNames = {"Vehicle ID", "Default", "Status"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        vehiclesTable = new JTable(model);
        vehiclesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Create buttons
        addVehicleButton = new JButton("Add Vehicle");
        removeVehicleButton = new JButton("Remove Vehicle");
        setDefaultButton = new JButton("Set as Default");
        backButton = new JButton("Back to Dashboard");
        
        // Add action listeners
        addVehicleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addVehicle();
            }
        });
        
        removeVehicleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                removeVehicle();
            }
        });
        
        setDefaultButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setDefaultVehicle();
            }
        });
        
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
    }
    
    private void layoutComponents() {
        setLayout(new BorderLayout(10, 10));
        
        // Add table with scroll pane
        JScrollPane scrollPane = new JScrollPane(vehiclesTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(scrollPane, BorderLayout.CENTER);
        
        // Add buttons in a panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.add(addVehicleButton);
        buttonPanel.add(removeVehicleButton);
        buttonPanel.add(setDefaultButton);
        buttonPanel.add(backButton);
        
        add(buttonPanel, BorderLayout.SOUTH);
        
        // Add instruction panel
        JPanel instructionPanel = new JPanel();
        instructionPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        JLabel instructionLabel = new JLabel("Manage your vehicles to use for parking reservations");
        instructionPanel.add(instructionLabel);
        
        add(instructionPanel, BorderLayout.NORTH);
    }
    
    private void loadVehicles() {
        try {
            // Get user's vehicles
            List<Vehicle> userVehicles = vehicleController.getUserVehicles(currentUser.getUserID());
            
            DefaultTableModel model = (DefaultTableModel) vehiclesTable.getModel();
            model.setRowCount(0); // Clear table
            
            if (userVehicles.isEmpty()) {
                // Show message if no vehicles
                JOptionPane.showMessageDialog(this,
                    "You don't have any vehicles registered yet.",
                    "No Vehicles",
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                // Get default vehicle
                Vehicle defaultVehicle = vehicleController.getDefaultVehicle(currentUser.getUserID());
                String defaultVehicleId = defaultVehicle != null ? defaultVehicle.getVehicleID() : null;
                
                // Populate table
                for (Vehicle vehicle : userVehicles) {
                    boolean isDefault = vehicle.getVehicleID().equals(defaultVehicleId);
                    boolean isCurrentlyParked = vehicleController.isVehicleCurrentlyParked(
                        vehicle.getVehicleID(), currentUser.getUserID());
                    
                    Object[] row = {
                        vehicle.getVehicleID(),
                        isDefault ? "Yes" : "No",
                        isCurrentlyParked ? "Currently Parked" : "Available"
                    };
                    
                    model.addRow(row);
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Error loading vehicles: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void addVehicle() {
        // Create a panel for the vehicle information
        JPanel vehiclePanel = new JPanel(new GridLayout(1, 2, 5, 5));
        
        JTextField vehicleIdField = new JTextField(15);
        
        vehiclePanel.add(new JLabel("Vehicle ID (License Plate):"));
        vehiclePanel.add(vehicleIdField);
        
        int result = JOptionPane.showConfirmDialog(this, vehiclePanel,
            "Add New Vehicle", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            String vehicleId = vehicleIdField.getText().trim();
            
            if (vehicleId.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Please enter a valid vehicle ID.",
                    "Input Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            try {
                // Create a Vehicle object
                Vehicle newVehicle = new Vehicle();
                newVehicle.setVehicleID(vehicleId);
                newVehicle.setUserID(currentUser.getUserID());
                
                boolean added = vehicleController.addVehicle(newVehicle);
                
                if (added) {
                    JOptionPane.showMessageDialog(this,
                        "Vehicle added successfully.",
                        "Vehicle Added",
                        JOptionPane.INFORMATION_MESSAGE);
                    
                    // Reload vehicles
                    loadVehicles();
                } else {
                    JOptionPane.showMessageDialog(this,
                        "Failed to add vehicle. It may already exist.",
                        "Vehicle Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                    "Error adding vehicle: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void removeVehicle() {
        int selectedRow = vehiclesTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this,
                "Please select a vehicle to remove.",
                "Selection Required",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String vehicleId = (String) vehiclesTable.getValueAt(selectedRow, 0);
        String status = (String) vehiclesTable.getValueAt(selectedRow, 2);
        
        // Check if vehicle is currently parked
        if (status.equals("Currently Parked")) {
            JOptionPane.showMessageDialog(this,
                "Cannot remove a vehicle that is currently parked.",
                "Vehicle in Use",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to remove this vehicle?\nVehicle ID: " + vehicleId,
            "Confirm Removal",
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                boolean removed = vehicleController.removeVehicle(vehicleId, currentUser.getUserID());
                
                if (removed) {
                    JOptionPane.showMessageDialog(this,
                        "Vehicle removed successfully.",
                        "Vehicle Removed",
                        JOptionPane.INFORMATION_MESSAGE);
                    
                    // Reload vehicles
                    loadVehicles();
                } else {
                    JOptionPane.showMessageDialog(this,
                        "Failed to remove vehicle. It may have active reservations.",
                        "Removal Failed",
                        JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                    "Error removing vehicle: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void setDefaultVehicle() {
        int selectedRow = vehiclesTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this,
                "Please select a vehicle to set as default.",
                "Selection Required",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String vehicleId = (String) vehiclesTable.getValueAt(selectedRow, 0);
        String isDefault = (String) vehiclesTable.getValueAt(selectedRow, 1);
        
        // Check if already default
        if (isDefault.equals("Yes")) {
            JOptionPane.showMessageDialog(this,
                "This vehicle is already set as default.",
                "Information",
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        try {
            boolean set = vehicleController.setDefaultVehicle(vehicleId, currentUser.getUserID());
            
            if (set) {
                JOptionPane.showMessageDialog(this,
                    "Default vehicle set successfully.",
                    "Default Set",
                    JOptionPane.INFORMATION_MESSAGE);
                
                // Reload vehicles
                loadVehicles();
            } else {
                JOptionPane.showMessageDialog(this,
                    "Failed to set default vehicle.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Error setting default vehicle: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // Main method for testing
    public static void main(String[] args) {
        // Create a mock user for testing
        User mockUser = new User();
        mockUser.setUserID(1);
        mockUser.setUserName("Test User");
        
        SwingUtilities.invokeLater(() -> {
            new VehicleManagementView(mockUser);
        });
    }
}