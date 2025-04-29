package main.java.com.parkeasy.view.admin;

import main.java.com.parkeasy.controller.admin.ParkingManagementController;
import main.java.com.parkeasy.model.Admin;
import main.java.com.parkeasy.model.ParkingSlot;
import main.java.com.parkeasy.model.ParkingSpace;
import main.java.com.parkeasy.service.ParkingSpaceService;
import main.java.com.parkeasy.util.DatabaseConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * Detailed view of a parking plot for admin users
 * Shows slot information and allows modifications
 */
public class ParkingPlotDetailView extends JFrame {
    private JLabel titleLabel;
    private JPanel infoPanel;
    private JTable slotsTable;
    private JButton addSlotButton;
    private JButton updateParkingButton;
    private JButton backButton;
    
    private ParkingManagementController parkingManagementController;
    private Admin currentAdmin;
    private ParkingSpace currentParkingSpace;
    
    public ParkingPlotDetailView(Admin admin, ParkingSpace parkingSpace) {
        this.currentAdmin = admin;
        this.currentParkingSpace = parkingSpace;
        
        // Initialize controller
        parkingManagementController = new ParkingManagementController();
        
        // Set up the frame
        setTitle("ParkEasy - Parking Plot Details");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Create components
        initComponents();
        
        // Layout the components
        layoutComponents();
        
        // Load data
        loadParkingData();
        
        // Make the frame visible
        setVisible(true);
    }

    /**
     * Update the initComponents method to change the table columns
     */
    private void initComponents() {
        titleLabel = new JLabel("Parking Plot: " + currentParkingSpace.getParkingAddress());
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));

        infoPanel = new JPanel();
        infoPanel.setBorder(BorderFactory.createTitledBorder("Parking Information"));

        // Create table for slots - Update column names
        String[] columnNames = {"Slot Number", "Status", "Actions"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 2; // Only allow editing of actions column
            }
        };
        slotsTable = new JTable(model);

        // Rest of the method remains the same
        addSlotButton = new JButton("Add Slot");
        updateParkingButton = new JButton("Update Parking Information");
        backButton = new JButton("Back");

        // Add action listeners
        addSlotButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addNewSlot();
            }
        });

        updateParkingButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateParkingInfo();
            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose(); // Close the detail window
            }
        });
    }

    
    private void layoutComponents() {
        setLayout(new BorderLayout(10, 10));
        
        // Top panel with title
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        topPanel.add(titleLabel, BorderLayout.WEST);
        
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonsPanel.add(backButton);
        topPanel.add(buttonsPanel, BorderLayout.EAST);
        
        add(topPanel, BorderLayout.NORTH);
        
        // Center panel with information and slots
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        
        // Info panel setup
        infoPanel.setLayout(new GridLayout(4, 2, 10, 10));
        centerPanel.add(infoPanel, BorderLayout.NORTH);
        
        // Slots table
        JScrollPane tableScrollPane = new JScrollPane(slotsTable);
        tableScrollPane.setBorder(BorderFactory.createTitledBorder("Parking Slots"));
        centerPanel.add(tableScrollPane, BorderLayout.CENTER);
        
        add(centerPanel, BorderLayout.CENTER);
        
        // Bottom panel with buttons
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        bottomPanel.add(addSlotButton);
        bottomPanel.add(updateParkingButton);
        
        add(bottomPanel, BorderLayout.SOUTH);
    }

    /**
     * Update the loadParkingData method to use slotNumber as the primary key
     * and modify the table columns
     */
    private void loadParkingData() {
        try {
            // Update the parking info panel
            infoPanel.removeAll();

            addInfoField("Parking ID:", currentParkingSpace.getParkingID());
            addInfoField("Address:", currentParkingSpace.getParkingAddress());
            addInfoField("Cost per Hour:", "$" + String.format("%.2f", currentParkingSpace.getCostOfParking()));
            addInfoField("Max Duration:", currentParkingSpace.getMaxDuration() + " hours");
            addInfoField("Number of Slots:", Integer.toString(currentParkingSpace.getNumberOfSlots()));
            addInfoField("Description:", currentParkingSpace.getDescription());

            infoPanel.revalidate();
            infoPanel.repaint();

            // Load slots data
            List<ParkingSlot> slots = parkingManagementController.getParkingSlotsByParkingId(currentParkingSpace.getParkingID());

            // Update slots table
            DefaultTableModel model = (DefaultTableModel) slotsTable.getModel();
            model.setRowCount(0); // Clear table

            for (ParkingSlot slot : slots) {
                Object[] row = {
                        slot.getSlotNumber(),  // Use slotNumber as the primary key column
                        slot.getAvailability() ? "Available" : "Occupied",
                        // The action buttons will be added in a separate step
                };

                model.addRow(row);
            }

            // Add action buttons to the table
            addButtonsToTable();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error loading parking data: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    
    private void addInfoField(String label, String value) {
        JLabel labelComponent = new JLabel(label);
        labelComponent.setFont(new Font("Arial", Font.BOLD, 12));
        
        JLabel valueComponent = new JLabel(value);
        valueComponent.setFont(new Font("Arial", Font.PLAIN, 12));
        
        infoPanel.add(labelComponent);
        infoPanel.add(valueComponent);
    }
    
    private void addButtonsToTable() {
        // This method adds the action buttons to the last column of the table
        slotsTable.getColumnModel().getColumn(3).setCellRenderer(new ButtonRenderer());
        slotsTable.getColumnModel().getColumn(3).setCellEditor(new ButtonEditor(new JCheckBox()));
    }
    
    private void addNewSlot() {
        // Show a dialog to add a new slot
        String slotNumber = JOptionPane.showInputDialog(this, 
            "Enter slot number:", 
            "Add New Slot", 
            JOptionPane.PLAIN_MESSAGE);
        
        if (slotNumber != null && !slotNumber.trim().isEmpty()) {
            try {
                // Create new slot
                ParkingSlot newSlot = new ParkingSlot();
                newSlot.setSlotNumber(slotNumber);
                newSlot.setAvailability(true); // Default to available
                newSlot.setParkingID(currentParkingSpace.getParkingID());
                
                // Add slot to database
                boolean success = parkingManagementController.addParkingSlot(newSlot);
                
                if (success) {
                    JOptionPane.showMessageDialog(this, 
                        "Slot added successfully.", 
                        "Success", 
                        JOptionPane.INFORMATION_MESSAGE);
                    
                    // Update the currentParkingSpace object
                    currentParkingSpace.setNumberOfSlots(currentParkingSpace.getNumberOfSlots() + 1);
                    
                    // Refresh the view
                    loadParkingData();
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Failed to add slot.", 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, 
                    "Error adding slot: " + ex.getMessage(), 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Update the toggleSlotStatus method to use slotNumber instead of slotID
     */
    private void toggleSlotStatus(String slotNumber, boolean currentStatus) {
        try {
            // Toggle the status
            boolean newStatus = !currentStatus;

            // Update in database
            boolean success = parkingManagementController.updateSlotStatus(slotNumber, newStatus);

            if (success) {
                // Refresh the view
                loadParkingData();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Failed to update slot status.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error updating slot status: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }


    /**
     * Update the removeSlot method to use slotNumber instead of slotID
     */
    private void removeSlot(String slotNumber) {
        try {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to remove this slot?",
                    "Confirm Removal",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                boolean success = parkingManagementController.removeParkingSlot(slotNumber);

                if (success) {
                    JOptionPane.showMessageDialog(this,
                            "Slot removed successfully.",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);

                    // Update the currentParkingSpace object
                    currentParkingSpace.setNumberOfSlots(currentParkingSpace.getNumberOfSlots() - 1);

                    // Refresh the view
                    loadParkingData();
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Failed to remove slot.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error removing slot: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }


    private void updateParkingInfo() {
        // Show a dialog to edit parking information
        JTextField addressField = new JTextField(currentParkingSpace.getParkingAddress(), 20);
        JTextField costField = new JTextField(String.valueOf(currentParkingSpace.getCostOfParking()), 20);
        JTextField maxDurationField = new JTextField(String.valueOf(currentParkingSpace.getMaxDuration()), 20);
        JTextArea descriptionArea = new JTextArea(currentParkingSpace.getDescription(), 5, 20);
        descriptionArea.setLineWrap(true);
        JScrollPane descScrollPane = new JScrollPane(descriptionArea);
        
        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Address:"));
        panel.add(addressField);
        panel.add(new JLabel("Cost per Hour:"));
        panel.add(costField);
        panel.add(new JLabel("Max Duration (hours):"));
        panel.add(maxDurationField);
        panel.add(new JLabel("Description:"));
        panel.add(descScrollPane);
        
        int result = JOptionPane.showConfirmDialog(this, panel, 
            "Update Parking Information", 
            JOptionPane.OK_CANCEL_OPTION);
        
        if (result == JOptionPane.OK_OPTION) {
            try {
                // Update the parking space object
                currentParkingSpace.setParkingAddress(addressField.getText());
                currentParkingSpace.setCostOfParking(Float.parseFloat(costField.getText()));
                currentParkingSpace.setMaxDuration(Integer.parseInt(maxDurationField.getText()));
                currentParkingSpace.setDescription(descriptionArea.getText());
                
                // Update in database
                boolean success = parkingManagementController.updateParkingSpace(currentParkingSpace);
                
                if (success) {
                    JOptionPane.showMessageDialog(this, 
                        "Parking information updated successfully.", 
                        "Success", 
                        JOptionPane.INFORMATION_MESSAGE);
                    
                    // Update title
                    titleLabel.setText("Parking Plot: " + currentParkingSpace.getParkingAddress());
                    
                    // Refresh the view
                    loadParkingData();
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Failed to update parking information.", 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, 
                    "Please enter valid numbers for cost and duration.", 
                    "Input Error", 
                    JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, 
                    "Error updating parking information: " + ex.getMessage(), 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    // Custom button renderer for the table
    class ButtonRenderer extends JPanel implements javax.swing.table.TableCellRenderer {
        private JButton toggleButton;
        private JButton removeButton;
        
        public ButtonRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER));
            toggleButton = new JButton("Toggle");
            removeButton = new JButton("Remove");
            add(toggleButton);
            add(removeButton);
        }
        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            return this;
        }
    }

    /**
     * Update the ButtonEditor class to use slotNumber
     */
    class ButtonEditor extends DefaultCellEditor {
        private JPanel panel;
        private JButton toggleButton;
        private JButton removeButton;
        private int currentRow;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            toggleButton = new JButton("Toggle");
            removeButton = new JButton("Remove");

            toggleButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String slotNumber = (String) slotsTable.getValueAt(currentRow, 0);
                    String status = (String) slotsTable.getValueAt(currentRow, 1);
                    boolean currentStatus = status.equals("Available");
                    toggleSlotStatus(slotNumber, currentStatus);
                    fireEditingStopped();
                }
            });

            removeButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String slotNumber = (String) slotsTable.getValueAt(currentRow, 0);
                    removeSlot(slotNumber);
                    fireEditingStopped();
                }
            });

            panel.add(toggleButton);
            panel.add(removeButton);
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            currentRow = row;
            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            return true;
        }
    }
    
    // Main method for testing
    public static void main(String[] args) {
        // Create a mock admin and parking space for testing
        Admin mockAdmin = new Admin();
        mockAdmin.setAdminID(1);
        mockAdmin.setAdminName("Test Admin");
        ParkingSpace mockParkingSpace = new ParkingSpace();
        mockParkingSpace.setParkingID("P001");
        mockParkingSpace.setParkingAddress("123 Test Street");
        mockParkingSpace.setCostOfParking(5.0f);
        mockParkingSpace.setNumberOfSlots(10);
        mockParkingSpace.setMaxDuration(8);
        mockParkingSpace.setDescription("Test parking space");
        mockParkingSpace.setAdminID(1);
        
        SwingUtilities.invokeLater(() -> {
            new ParkingPlotDetailView(mockAdmin, mockParkingSpace);
        });
    }
}