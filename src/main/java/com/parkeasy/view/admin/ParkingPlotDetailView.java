package main.java.com.parkeasy.view.admin;

import main.java.com.parkeasy.controller.admin.ParkingManagementController;
import main.java.com.parkeasy.model.Admin;
import main.java.com.parkeasy.model.ParkingSlot;
import main.java.com.parkeasy.model.ParkingSpace;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
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
        setSize(1140, 880);
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

    private void initComponents() {
        titleLabel = new JLabel("Parking Plot: " + currentParkingSpace.getParkingAddress());
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));

        infoPanel = new JPanel();
        TitledBorder infoBorder = BorderFactory.createTitledBorder("Parking Information");
        infoBorder.setTitleFont(new Font("Arial", Font.BOLD, 14));
        infoPanel.setBorder(infoBorder);

        // Create table for slots
        String[] columnNames = {"Slot Number", "Status", "Actions"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 2; // Only allow editing of actions column
            }
        };
        slotsTable = new JTable(model);
        slotsTable.setRowHeight(40); // Match the UI's row height
        slotsTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));

        addSlotButton = new JButton("Add Slot");
        addSlotButton.setFont(new Font("Arial", Font.PLAIN, 14));
        addSlotButton.setPreferredSize(new Dimension(150, 40));

        updateParkingButton = new JButton("Update Parking Information");
        updateParkingButton.setFont(new Font("Arial", Font.PLAIN, 14));
        updateParkingButton.setPreferredSize(new Dimension(250, 40));

        backButton = new JButton("Back");
        backButton.setFont(new Font("Arial", Font.PLAIN, 14));
        backButton.setPreferredSize(new Dimension(120, 30));
        backButton.setBorder(BorderFactory.createLineBorder(Color.BLUE, 1));
        backButton.setForeground(Color.BLUE);
        backButton.setContentAreaFilled(false);

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

        // Top panel with title and back button
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        topPanel.add(titleLabel, BorderLayout.WEST);

        JPanel backButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        backButtonPanel.add(backButton);
        topPanel.add(backButtonPanel, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        // Center panel with information and slots
        JPanel centerPanel = new JPanel(new BorderLayout(10, 20));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // Info panel setup
        infoPanel.setLayout(new GridLayout(3, 4, 10, 10));
        infoPanel.setPreferredSize(new Dimension(getWidth(), 140));
        centerPanel.add(infoPanel, BorderLayout.NORTH);

        // Slots table setup
        JScrollPane tableScrollPane = new JScrollPane(slotsTable);
        TitledBorder slotsBorder = BorderFactory.createTitledBorder("Parking Slots");
        slotsBorder.setTitleFont(new Font("Arial", Font.BOLD, 14));
        tableScrollPane.setBorder(slotsBorder);
        centerPanel.add(tableScrollPane, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        // Bottom panel with buttons
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        bottomPanel.add(addSlotButton);
        bottomPanel.add(updateParkingButton);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void loadParkingData() {
        try {
            // Update the parking info panel
            infoPanel.removeAll();

            addInfoField("Parking ID:", currentParkingSpace.getParkingID());
            addInfoField("Address:", currentParkingSpace.getParkingAddress());
            addInfoField("Cost per Hour:", "$" + String.format("%.2f", currentParkingSpace.getCostOfParking()));
            addInfoField("Number of Slots:", Integer.toString(currentParkingSpace.getNumberOfSlots()));
            addInfoField("Max Duration:", currentParkingSpace.getMaxDuration() + " hours");
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
                        slot.getSlotNumber(),
                        slot.getAvailability() ? "Available" : "Occupied",
                        "Actions"  // Placeholder for action buttons
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
            ex.printStackTrace();
        }
    }

    private void addInfoField(String label, String value) {
        JLabel labelComponent = new JLabel(label);
        labelComponent.setFont(new Font("Arial", Font.BOLD, 14));

        JLabel valueComponent = new JLabel(value);
        valueComponent.setFont(new Font("Arial", Font.PLAIN, 14));

        infoPanel.add(labelComponent);
        infoPanel.add(valueComponent);
    }

    private void addButtonsToTable() {
        // Set column widths to match the UI
        slotsTable.getColumnModel().getColumn(0).setPreferredWidth(150);
        slotsTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        slotsTable.getColumnModel().getColumn(2).setPreferredWidth(300);

        // Add button renderer and editor
        slotsTable.getColumnModel().getColumn(2).setCellRenderer(new ButtonRenderer());
        slotsTable.getColumnModel().getColumn(2).setCellEditor(new ButtonEditor(new JCheckBox()));
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
    class ButtonRenderer extends JPanel implements TableCellRenderer {
        private JButton toggleButton;
        private JButton removeButton;

        public ButtonRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 10, 0));

            toggleButton = new JButton("Toggle");
            toggleButton.setFont(new Font("Arial", Font.PLAIN, 12));
            toggleButton.setPreferredSize(new Dimension(100, 30));

            removeButton = new JButton("Remove");
            removeButton.setFont(new Font("Arial", Font.PLAIN, 12));
            removeButton.setPreferredSize(new Dimension(100, 30));

            add(toggleButton);
            add(removeButton);
            setBackground(Color.WHITE);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            return this;
        }
    }

    class ButtonEditor extends DefaultCellEditor {
        private JPanel panel;
        private JButton toggleButton;
        private JButton removeButton;
        private int currentRow;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));

            toggleButton = new JButton("Toggle");
            toggleButton.setFont(new Font("Arial", Font.PLAIN, 12));
            toggleButton.setPreferredSize(new Dimension(100, 30));

            removeButton = new JButton("Remove");
            removeButton.setFont(new Font("Arial", Font.PLAIN, 12));
            removeButton.setPreferredSize(new Dimension(100, 30));

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
            panel.setBackground(Color.WHITE);
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            currentRow = row;
            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            return "Actions";
        }
    }

    // Main method for testing
    public static void main(String[] args) {
        // Create a mock admin and parking space for testing
        Admin mockAdmin = new Admin();
        mockAdmin.setAdminID(1);
        mockAdmin.setAdminName("Test Admin");

        ParkingSpace mockParkingSpace = new ParkingSpace();
        mockParkingSpace.setParkingID("P66");
        mockParkingSpace.setParkingAddress("215 Le Duan, District 6, Ha Noi");
        mockParkingSpace.setCostOfParking(30000.0f);
        mockParkingSpace.setNumberOfSlots(7);
        mockParkingSpace.setMaxDuration(1440);
        mockParkingSpace.setDescription("Bãi đỗ xe ngoài trời, có camera an ninh");
        mockParkingSpace.setAdminID(1);

        SwingUtilities.invokeLater(() -> {
            new ParkingPlotDetailView(mockAdmin, mockParkingSpace);
        });
    }
}