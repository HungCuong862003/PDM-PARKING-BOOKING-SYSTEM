package main.java.com.parkeasy.view.admin;

import main.java.com.parkeasy.controller.admin.AdminDashboardController;
import main.java.com.parkeasy.model.Admin;
import main.java.com.parkeasy.model.ParkingSpace;
import main.java.com.parkeasy.service.AdminService;
import main.java.com.parkeasy.service.ParkingSpaceService;
import main.java.com.parkeasy.service.ReservationService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * View for managing parking spaces by admin
 */
public class ParkingSpaceManagementView extends JFrame {
    private JTable parkingSpacesTable;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton backButton;
    
    private AdminDashboardController adminDashboardController;
    private ParkingSpaceService parkingSpaceService;
    private Admin currentAdmin;
    
    public ParkingSpaceManagementView(Admin admin) {
        this.currentAdmin = admin;
        
        // Initialize services and controller
        AdminService adminService = new AdminService();
        parkingSpaceService = new ParkingSpaceService();
        ReservationService reservationService = new ReservationService();
        adminDashboardController = new AdminDashboardController(adminService, parkingSpaceService, reservationService);
        
        // Set up the frame
        setTitle("ParkEasy - Parking Space Management");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Create components
        initComponents();
        
        // Layout the components
        layoutComponents();
        
        // Load data
        loadParkingSpaces();
        
        // Make the frame visible
        setVisible(true);
    }
    
    private void initComponents() {
        // Create table for parking spaces
        String[] columnNames = {"Parking ID", "Address", "Cost", "Slots", "Max Duration", "Description"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
        };
        parkingSpacesTable = new JTable(model);
        parkingSpacesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Buttons
        addButton = new JButton("Add Parking Space");
        editButton = new JButton("Edit Parking Space");
        deleteButton = new JButton("Delete Parking Space");
        backButton = new JButton("Back to Dashboard");
        
        // Add action listeners
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showAddParkingSpaceDialog();
            }
        });
        
        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editSelectedParkingSpace();
            }
        });
        
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteSelectedParkingSpace();
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
        
        // Top panel with title
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel titleLabel = new JLabel("Manage Parking Spaces");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        topPanel.add(titleLabel, BorderLayout.WEST);
        
        add(topPanel, BorderLayout.NORTH);
        
        // Center panel with table
        JScrollPane tableScrollPane = new JScrollPane(parkingSpacesTable);
        add(tableScrollPane, BorderLayout.CENTER);
        
        // Bottom panel with buttons
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        bottomPanel.add(addButton);
        bottomPanel.add(editButton);
        bottomPanel.add(deleteButton);
        bottomPanel.add(backButton);
        
        add(bottomPanel, BorderLayout.SOUTH);
    }
    
    private void loadParkingSpaces() {
        try {
            // Get parking spaces from controller
            List<ParkingSpace> parkingSpaces = adminDashboardController.getAdminParkingSpaces(currentAdmin.getAdminID());
            
            // Update table
            DefaultTableModel model = (DefaultTableModel) parkingSpacesTable.getModel();
            model.setRowCount(0); // Clear table
            
            for (ParkingSpace space : parkingSpaces) {
                Object[] row = {
                        space.getParkingID(),
                        space.getParkingAddress(),
                        String.format("$%.2f", space.getCostOfParking()),
                        space.getNumberOfSlots(),
                        space.getDescription()
                };
                
                model.addRow(row);
            }
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error loading parking spaces: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
    
    private void showAddParkingSpaceDialog() {
        // Create a form for adding a new parking space
        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JTextField idField = new JTextField(10);
        JTextField addressField = new JTextField(20);
        JTextField costField = new JTextField(10);
        JTextField slotsField = new JTextField(10);
        JTextField durationField = new JTextField(10);
        JTextArea descriptionArea = new JTextArea(4, 20);
        JScrollPane descriptionScrollPane = new JScrollPane(descriptionArea);
        
        panel.add(new JLabel("Parking ID:"));
        panel.add(idField);
        panel.add(new JLabel("Address:"));
        panel.add(addressField);
        panel.add(new JLabel("Cost (per hour):"));
        panel.add(costField);
        panel.add(new JLabel("Number of Slots:"));
        panel.add(slotsField);
        panel.add(new JLabel("Max Duration (mins):"));
        panel.add(durationField);
        panel.add(new JLabel("Description:"));
        panel.add(descriptionScrollPane);
        
        int result = JOptionPane.showConfirmDialog(this, panel, "Add New Parking Space",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            try {
                // Validate input
                String id = idField.getText().trim();
                String address = addressField.getText().trim();
                float cost = Float.parseFloat(costField.getText().trim());
                int slots = Integer.parseInt(slotsField.getText().trim());
                int duration = Integer.parseInt(durationField.getText().trim());
                String description = descriptionArea.getText().trim();
                
                if (id.isEmpty() || address.isEmpty()) {
                    JOptionPane.showMessageDialog(this,
                            "ID and Address fields cannot be empty",
                            "Validation Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                if (cost <= 0) {
                    JOptionPane.showMessageDialog(this,
                            "Cost must be greater than zero",
                            "Validation Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                if (slots <= 0) {
                    JOptionPane.showMessageDialog(this,
                            "Number of slots must be greater than zero",
                            "Validation Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Create new parking space
                ParkingSpace newSpace = new ParkingSpace();
                newSpace.setParkingID(id);
                newSpace.setParkingAddress(address);
                newSpace.setCostOfParking((float) cost);
                newSpace.setNumberOfSlots(slots);
                newSpace.setDescription(description);
                newSpace.setAdminID(currentAdmin.getAdminID());
                
                // Add to database
                boolean success = parkingSpaceService.addParkingSpace(newSpace);
                
                if (success) {
                    JOptionPane.showMessageDialog(this,
                            "Parking space added successfully",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                    loadParkingSpaces(); // Refresh the table
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Failed to add parking space",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
                
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this,
                        "Please enter valid numbers for cost, slots, and duration",
                        "Validation Error",
                        JOptionPane.ERROR_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                        "Error adding parking space: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }
    
    private void editSelectedParkingSpace() {
        int selectedRow = parkingSpacesTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this,
                    "Please select a parking space to edit",
                    "Selection Required",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String parkingId = (String) parkingSpacesTable.getValueAt(selectedRow, 0);
        
        try {
            // Get the parking space from the service
            ParkingSpace space = parkingSpaceService.getParkingSpaceById(parkingId);
            
            if (space == null) {
                JOptionPane.showMessageDialog(this,
                        "Parking space not found",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Create a form for editing the parking space
            JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
            panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            
            JTextField idField = new JTextField(space.getParkingID(), 10);
            idField.setEditable(false); // Don't allow ID to be changed
            JTextField addressField = new JTextField(space.getParkingAddress(), 20);
            JTextField costField = new JTextField(String.valueOf(space.getCostOfParking()), 10);
            JTextField slotsField = new JTextField(String.valueOf(space.getNumberOfSlots()), 10);
            JTextArea descriptionArea = new JTextArea(space.getDescription(), 4, 20);
            JScrollPane descriptionScrollPane = new JScrollPane(descriptionArea);
            
            panel.add(new JLabel("Parking ID:"));
            panel.add(idField);
            panel.add(new JLabel("Address:"));
            panel.add(addressField);
            panel.add(new JLabel("Cost (per hour):"));
            panel.add(costField);
            panel.add(new JLabel("Number of Slots:"));
            panel.add(slotsField);
            panel.add(new JLabel("Description:"));
            panel.add(descriptionScrollPane);
            
            int result = JOptionPane.showConfirmDialog(this, panel, "Edit Parking Space",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            
            if (result == JOptionPane.OK_OPTION) {
                try {
                    // Validate input
                    String address = addressField.getText().trim();
                    float cost = Float.parseFloat(costField.getText().trim());
                    int slots = Integer.parseInt(slotsField.getText().trim());
                    String description = descriptionArea.getText().trim();
                    
                    if (address.isEmpty()) {
                        JOptionPane.showMessageDialog(this,
                                "Address field cannot be empty",
                                "Validation Error",
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    
                    if (cost <= 0) {
                        JOptionPane.showMessageDialog(this,
                                "Cost must be greater than zero",
                                "Validation Error",
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    
                    if (slots <= 0) {
                        JOptionPane.showMessageDialog(this,
                                "Number of slots must be greater than zero",
                                "Validation Error",
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    
                    // Update parking space
                    space.setParkingAddress(address);
                    space.setCostOfParking((float) cost);
                    space.setNumberOfSlots(slots);
                    space.setDescription(description);
                    
                    // Update in database
                    boolean success = parkingSpaceService.updateParkingSpace(space);
                    
                    if (success) {
                        JOptionPane.showMessageDialog(this,
                                "Parking space updated successfully",
                                "Success",
                                JOptionPane.INFORMATION_MESSAGE);
                        loadParkingSpaces(); // Refresh the table
                    } else {
                        JOptionPane.showMessageDialog(this,
                                "Failed to update parking space",
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                    
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this,
                            "Please enter valid numbers for cost, slots, and duration",
                            "Validation Error",
                            JOptionPane.ERROR_MESSAGE);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this,
                            "Error updating parking space: " + e.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    e.printStackTrace();
                }
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error loading parking space details: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private void deleteSelectedParkingSpace() {
        int selectedRow = parkingSpacesTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this,
                    "Please select a parking space to delete",
                    "Selection Required",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String parkingId = (String) parkingSpacesTable.getValueAt(selectedRow, 0);
        String address = (String) parkingSpacesTable.getValueAt(selectedRow, 1);
        
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete the parking space?\n\n" +
                        "ID: " + parkingId + "\n" +
                        "Address: " + address + "\n\n" +
                        "This action cannot be undone and will delete all related data.",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                boolean success = parkingSpaceService.deleteParkingSpace(parkingId);
                
                if (success) {
                    JOptionPane.showMessageDialog(this,
                            "Parking space deleted successfully",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                    loadParkingSpaces(); // Refresh the table
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Failed to delete parking space. It may have active reservations.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                        "Error deleting parking space: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }
    
    public static void main(String[] args) {
        // For testing purposes
        Admin mockAdmin = new Admin();
        mockAdmin.setAdminID(1);
        mockAdmin.setAdminName("Test Admin");
        
        SwingUtilities.invokeLater(() -> {
            new ParkingSpaceManagementView(mockAdmin);
        });
    }
}