package main.java.com.parkeasy.view.admin;

import main.java.com.parkeasy.controller.admin.ParkingManagementController;
import main.java.com.parkeasy.model.Admin;
import main.java.com.parkeasy.model.ParkingSlot;
import main.java.com.parkeasy.model.ParkingSpace;
import main.java.com.parkeasy.util.DatabaseConnection;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
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

        // First create the table with the model
        slotsTable = new JTable(model) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component comp = super.prepareRenderer(renderer, row, column);

                // Removed color effects for slot number and name row
                // All cells now have default white background
                comp.setBackground(Color.WHITE);

                // Only apply custom styling to Status column
                if (column == 1) {
                    // Status column with color based on status value
                    String status = (String) getValueAt(row, 1);
                    if ("Available".equals(status)) {
                        comp.setBackground(new Color(204, 255, 204)); // Brighter green seafoam
                        comp.setForeground(new Color(0, 100, 0)); // Dark green text
                    } else if ("Occupied".equals(status)) {
                        comp.setBackground(new Color(255, 204, 204)); // Brighter light red
                        comp.setForeground(new Color(139, 0, 0)); // Dark red text
                    }
                }

                return comp;
            }
        };
        slotsTable.setRowHeight(40); // Match the UI's row height

        // Now that the table is created, set up the sorter
        setupCustomRowSorter();
        setupSortableColumns();
        setupSlotClickListener();

        // Make the header row basic with no special styling
        JTableHeader header = slotsTable.getTableHeader();
        header.setPreferredSize(new Dimension(header.getWidth(), 40)); // Standard header height
        header.setFont(new Font("Arial", Font.PLAIN, 14)); // Regular font
        header.setBackground(Color.WHITE); // White background
        header.setForeground(Color.BLACK); // Black text

        // Set column widths to match the UI
        slotsTable.getColumnModel().getColumn(0).setPreferredWidth(150);
        slotsTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        slotsTable.getColumnModel().getColumn(2).setPreferredWidth(300);

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
        // Add button renderer and editor
        slotsTable.getColumnModel().getColumn(2).setCellRenderer(new ButtonRenderer());
        slotsTable.getColumnModel().getColumn(2).setCellEditor(new ButtonEditor(new JCheckBox()));
    }
    /**
     * Sets up sortable columns for the slots table with improved visual indicators
     */
    private void setupSortableColumns() {
        slotsTable.setAutoCreateRowSorter(true);
        JTableHeader header = slotsTable.getTableHeader();

        // Set basic header appearance
        header.setBackground(Color.WHITE); // White background
        header.setForeground(Color.BLACK); // Black text

        // Simple border
        header.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        // Add mouse listener to handle column header clicks
        header.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int columnIndex = slotsTable.columnAtPoint(evt.getPoint());
                if (columnIndex != -1) {
                    // The Actions column (index 2) shouldn't be sortable
                    if (columnIndex == 2) {
                        return;
                    }

                    // Get current sort keys
                    RowSorter<? extends TableModel> sorter = slotsTable.getRowSorter();
                    List<? extends RowSorter.SortKey> sortKeys = sorter.getSortKeys();

                    // Determine sort order (toggle between ascending and descending)
                    SortOrder sortOrder = SortOrder.ASCENDING;
                    if (!sortKeys.isEmpty() && sortKeys.get(0).getColumn() == columnIndex) {
                        // If already sorting by this column, toggle the order
                        sortOrder = sortKeys.get(0).getSortOrder() == SortOrder.ASCENDING ?
                                SortOrder.DESCENDING : SortOrder.ASCENDING;
                    }

                    // Create new sort key
                    ArrayList<RowSorter.SortKey> newSortKeys = new ArrayList<>();
                    newSortKeys.add(new RowSorter.SortKey(columnIndex, sortOrder));

                    // Apply the sort
                    sorter.setSortKeys(newSortKeys);

                    // Update header appearance to show sort direction
                    updateHeaderSortIndicator(columnIndex, sortOrder);
                }
            }

            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                // Highlight the column header on hover for better user feedback
                int columnIndex = slotsTable.columnAtPoint(evt.getPoint());
                if (columnIndex != -1 && columnIndex != 2) { // Skip Actions column
                    // You could change cursor or add other visual feedback here
                    header.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                }
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                // Reset cursor when leaving header area
                header.setCursor(Cursor.getDefaultCursor());
            }
        });

        // Make the header show "clickable" cursor by default
        header.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }
    /**
     * Updates the table header to indicate sort direction with
     * simple black arrow indicators for white background
     */
    private void updateHeaderSortIndicator(int columnIndex, SortOrder sortOrder) {
        JTableHeader header = slotsTable.getTableHeader();
        TableColumnModel columnModel = slotsTable.getColumnModel();

        // Reset all column headers
        for (int i = 0; i < columnModel.getColumnCount(); i++) {
            TableColumn column = columnModel.getColumn(i);
            String columnName = slotsTable.getModel().getColumnName(i);
            column.setHeaderValue(columnName);
        }

        // Update the sorted column's header
        if (columnIndex >= 0 && columnIndex < columnModel.getColumnCount()) {
            TableColumn column = columnModel.getColumn(columnIndex);
            String columnName = slotsTable.getModel().getColumnName(columnIndex);

            // Add black arrows for white background
            if (sortOrder == SortOrder.ASCENDING) {
                column.setHeaderValue(columnName + " ↑"); // Simple up arrow
            } else if (sortOrder == SortOrder.DESCENDING) {
                column.setHeaderValue(columnName + " ↓"); // Simple down arrow
            }
        }

        // Repaint header
        header.repaint();
    }
    /**
     * Custom row sorter that handles different data types appropriately
     * Sorts slot numbers by their numeric prefix in pure numerical order,
     * regardless of number of digits (1P, 2P, ..., 9P, 10P, 11P, ..., 100P, ...)
     */
    private void setupCustomRowSorter() {
        DefaultTableModel model = (DefaultTableModel) slotsTable.getModel();
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);

        // Custom comparator for slot numbers that correctly handles numerical order for any length number
        sorter.setComparator(0, new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                try {
                    // Extract the numeric part before 'P'
                    int p1Index = s1.indexOf('P');
                    int p2Index = s2.indexOf('P');

                    if (p1Index > 0 && p2Index > 0) {
                        // Parse the numeric prefixes as integers - this automatically handles
                        // numbers of any length and leading zeros
                        int num1 = Integer.parseInt(s1.substring(0, p1Index));
                        int num2 = Integer.parseInt(s2.substring(0, p2Index));

                        // Compare the numeric values as pure numbers
                        return Integer.compare(num1, num2);
                    }
                } catch (NumberFormatException e) {
                    System.err.println("Error parsing numeric part of slot number: " + e.getMessage());
                    // Fall back to string comparison
                } catch (Exception e) {
                    System.err.println("Unexpected error comparing slot numbers: " + e.getMessage());
                }

                // Default to string comparison if parsing fails
                return s1.compareTo(s2);
            }
        });

        // For the Status column (Available/Occupied)
        sorter.setComparator(1, (String s1, String s2) -> s1.compareTo(s2));

        // Disable sorting for the Actions column
        sorter.setSortable(2, false);

        // Apply the sorter to the table
        slotsTable.setRowSorter(sorter);

        // Apply initial sort by slot number in ASCENDING numerical order
        List<RowSorter.SortKey> sortKeys = new ArrayList<>();
        sortKeys.add(new RowSorter.SortKey(0, SortOrder.ASCENDING));
        sorter.setSortKeys(sortKeys);
        sorter.sort();

        // Update the header to show the sort direction
        updateHeaderSortIndicator(0, SortOrder.ASCENDING);
    }

    private void addNewSlot() {
        try {
            // Get all existing slots for this parking space
            List<ParkingSlot> existingSlots = parkingManagementController.getParkingSlotsByParkingId(currentParkingSpace.getParkingID());

            // Find the maximum slot number
            String maxSlotNumber = "";
            String parkingPrefix = "P" + currentParkingSpace.getParkingID().replaceAll("[^0-9]", "");
            int maxNumber = 0;

            for (ParkingSlot slot : existingSlots) {
                String slotNum = slot.getSlotNumber();
                // Extract the numeric part assuming format like "10P1"
                if (slotNum.contains(parkingPrefix)) {
                    String numericPart = slotNum.substring(0, slotNum.indexOf(parkingPrefix));
                    try {
                        int num = Integer.parseInt(numericPart);
                        if (num > maxNumber) {
                            maxNumber = num;
                            maxSlotNumber = slotNum;
                        }
                    } catch (NumberFormatException e) {
                        // Skip if not in expected format
                        continue;
                    }
                }
            }

            // Generate new slot number
            String newSlotNumber = (maxNumber + 1) + parkingPrefix;

            // Show confirmation dialog with the generated slot number
            int confirm = JOptionPane.showConfirmDialog(this,
                    "New slot will be created with number: " + newSlotNumber + "\nDo you want to continue?",
                    "Confirm New Slot",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                // Create new slot
                ParkingSlot newSlot = new ParkingSlot();
                newSlot.setSlotNumber(newSlotNumber);
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
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error adding slot: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Helper method to format reservation information as HTML for better tooltip display
     * With added null checks and error handling
     */
    private String formatReservationTooltip(Map<String, Object> reservation) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

            StringBuilder html = new StringBuilder();
            html.append("<html>");
            html.append("<div style='background-color:#FFF8DC; padding:10px; border:1px solid #FFEBCD; max-width:300px;'>");
            html.append("<h3 style='margin:2px 0; color:#8B0000;'>Reservation Details</h3>");
            html.append("<hr style='border:1px solid #FFEBCD; margin:3px 0;'>");

            // Reservation ID
            if (reservation.containsKey("reservationID")) {
                html.append("<div><b>Reservation ID:</b> #").append(reservation.get("reservationID")).append("</div>");
            }

            // Vehicle Info
            if (reservation.containsKey("vehicleID")) {
                html.append("<div><b>Vehicle ID:</b> ").append(reservation.get("vehicleID")).append("</div>");
            }

            // User Info (if available)
            if (reservation.containsKey("userName")) {
                html.append("<div><b>Reserved by:</b> ").append(reservation.get("userName")).append("</div>");
            }

            // Dates - with null checks
            if (reservation.containsKey("startDate") && reservation.containsKey("startTime")) {
                Object startDateObj = reservation.get("startDate");
                Object startTimeObj = reservation.get("startTime");

                if (startDateObj instanceof java.sql.Date && startTimeObj instanceof java.sql.Time) {
                    java.sql.Date startDate = (java.sql.Date) startDateObj;
                    java.sql.Time startTime = (java.sql.Time) startTimeObj;

                    html.append("<div><b>Start:</b> ").append(dateFormat.format(startDate))
                            .append(" ").append(timeFormat.format(startTime)).append("</div>");
                } else {
                    html.append("<div><b>Start:</b> Date information unavailable</div>");
                }
            }

            if (reservation.containsKey("endDate") && reservation.containsKey("endTime")) {
                Object endDateObj = reservation.get("endDate");
                Object endTimeObj = reservation.get("endTime");

                if (endDateObj instanceof java.sql.Date && endTimeObj instanceof java.sql.Time) {
                    java.sql.Date endDate = (java.sql.Date) endDateObj;
                    java.sql.Time endTime = (java.sql.Time) endTimeObj;

                    html.append("<div><b>End:</b> ").append(dateFormat.format(endDate))
                            .append(" ").append(timeFormat.format(endTime)).append("</div>");
                } else {
                    html.append("<div><b>End:</b> Date information unavailable</div>");
                }
            }

            // Fee information - ADDED
            if (reservation.containsKey("fee")) {
                Object feeObj = reservation.get("fee");
                if (feeObj instanceof Number) {
                    float fee = ((Number) feeObj).floatValue();
                    html.append("<div><b>Fee:</b> $").append(String.format("%.2f", fee)).append("</div>");
                } else if (feeObj != null) {
                    html.append("<div><b>Fee:</b> ").append(feeObj).append("</div>");
                }
            }

            // Status
            if (reservation.containsKey("status")) {
                String status = String.valueOf(reservation.get("status"));
                String statusColor = "green";
                if ("PENDING".equalsIgnoreCase(status)) {
                    statusColor = "#FF8C00"; // Dark orange
                } else if ("CANCELLED".equalsIgnoreCase(status)) {
                    statusColor = "red";
                }

                html.append("<div><b>Status:</b> <span style='color:").append(statusColor)
                        .append(";font-weight:bold;'>").append(status).append("</span></div>");
            }

            html.append("</div>");
            html.append("</html>");

            return html.toString();
        } catch (Exception e) {
            System.err.println("Error formatting tooltip: " + e.getMessage());
            e.printStackTrace();
            return "<html><div style='color:red;padding:5px;'>Error displaying reservation details</div></html>";
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
    private void setupSlotClickListener() {
        System.out.println("Setting up slot click listener for reservation info...");

        // Add mouse listener to the table
        slotsTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    int row = slotsTable.rowAtPoint(e.getPoint());
                    int col = slotsTable.columnAtPoint(e.getPoint());

                    // Only respond to clicks on the Slot Number or Status columns (not the Actions column)
                    if (row >= 0 && (col == 0 || col == 1)) {
                        // Get the model row in case table is sorted
                        int modelRow = slotsTable.convertRowIndexToModel(row);
                        DefaultTableModel model = (DefaultTableModel) slotsTable.getModel();

                        if (modelRow < model.getRowCount()) {
                            String status = (String) model.getValueAt(modelRow, 1);

                            // Only show info for occupied slots
                            if ("Occupied".equals(status)) {
                                String slotNumber = (String) model.getValueAt(modelRow, 0);
                                System.out.println("Clicked on occupied slot: " + slotNumber);

                                // Get reservation details for this slot
                                Map<String, Object> reservationInfo =
                                        parkingManagementController.getActiveReservationInfoForSlot(slotNumber);

                                if (reservationInfo != null && !reservationInfo.isEmpty()) {
                                    System.out.println("Found reservation info with " +
                                            reservationInfo.size() + " fields");

                                    // Show the reservation info in a dialog
                                    showReservationInfoDialog(slotNumber, reservationInfo);
                                } else {
                                    System.out.println("No reservation info found for slot: " + slotNumber);
                                    JOptionPane.showMessageDialog(ParkingPlotDetailView.this,
                                            "No active reservation details found for slot " + slotNumber,
                                            "No Reservation Info",
                                            JOptionPane.INFORMATION_MESSAGE);
                                }
                            }
                        }
                    }
                } catch (Exception ex) {
                    System.err.println("Error in click handler: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
        });

        System.out.println("Slot click listener setup complete");
    }
    /**
     * Displays detailed information about a parking slot's active reservation
     * Shows reservation details including schedule, timing, fee and vehicle information
     *
     * @param slotNumber The slot number to display information for
     * @param reservation The reservation data map containing all details
     */
    private void showReservationInfoDialog(String slotNumber, Map<String, Object> reservation) {
        try {
            // Create a dialog with improved styling
            JDialog dialog = new JDialog(this, "Slot Information", true);
            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

            // Main panel with more sophisticated border layout
            JPanel panel = new JPanel(new BorderLayout(0, 0));

            // Header panel with slot number and status
            JPanel headerPanel = new JPanel(new BorderLayout());
            headerPanel.setBackground(new Color(50, 50, 50));
            headerPanel.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));

            JLabel titleLabel = new JLabel("Parking Slot " + slotNumber);
            titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
            titleLabel.setForeground(Color.WHITE);

            JLabel statusLabel = new JLabel("OCCUPIED");
            statusLabel.setFont(new Font("Arial", Font.BOLD, 14));
            statusLabel.setForeground(new Color(255, 102, 102)); // Light red

            headerPanel.add(titleLabel, BorderLayout.WEST);
            headerPanel.add(statusLabel, BorderLayout.EAST);

            panel.add(headerPanel, BorderLayout.NORTH);

            // Content panel with elegant spacing
            JPanel contentPanel = new JPanel();
            contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
            contentPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

            // Main info area with white background and subtle borders
            JPanel mainInfoPanel = new JPanel();
            mainInfoPanel.setLayout(new BoxLayout(mainInfoPanel, BoxLayout.Y_AXIS));
            mainInfoPanel.setBackground(Color.WHITE);
            mainInfoPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

            // 1. Reservation header section
            JPanel reservationHeaderPanel = new JPanel(new BorderLayout());
            reservationHeaderPanel.setBackground(Color.WHITE);
            reservationHeaderPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

            JPanel reservationInfoPanel = new JPanel(new GridLayout(2, 1, 0, 8));
            reservationInfoPanel.setOpaque(false);

            JLabel reservationIdLabel = new JLabel("Reservation #" + reservation.get("reservationID"));
            reservationIdLabel.setFont(new Font("Arial", Font.BOLD, 18));

            JLabel reservationStatusLabel = new JLabel("Status: Processing");
            reservationStatusLabel.setFont(new Font("Arial", Font.BOLD, 14));
            reservationStatusLabel.setForeground(new Color(255, 140, 0)); // Orange for processing

            reservationInfoPanel.add(reservationIdLabel);
            reservationInfoPanel.add(reservationStatusLabel);

            reservationHeaderPanel.add(reservationInfoPanel, BorderLayout.CENTER);

            mainInfoPanel.add(reservationHeaderPanel);

            // Separator 1
            JSeparator separator1 = new JSeparator(JSeparator.HORIZONTAL);
            separator1.setForeground(new Color(230, 230, 230));
            separator1.setBackground(new Color(230, 230, 230));
            mainInfoPanel.add(separator1);

            // 2. Customer info section
            JPanel customerPanel = new JPanel(new BorderLayout());
            customerPanel.setBackground(Color.WHITE);
            customerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

            JPanel customerInfoPanel = new JPanel(new GridLayout(2, 1, 0, 10));
            customerInfoPanel.setOpaque(false);

            JLabel customerLabel = new JLabel("Customer: Vu Huu Hai");
            customerLabel.setFont(new Font("Arial", Font.PLAIN, 14));

            JLabel vehicleLabel = new JLabel("Vehicle License: 30WT-97676");
            vehicleLabel.setFont(new Font("Arial", Font.PLAIN, 14));

            customerInfoPanel.add(customerLabel);
            customerInfoPanel.add(vehicleLabel);

            customerPanel.add(customerInfoPanel, BorderLayout.CENTER);

            mainInfoPanel.add(customerPanel);

            // Separator 2
            JSeparator separator2 = new JSeparator(JSeparator.HORIZONTAL);
            separator2.setForeground(new Color(230, 230, 230));
            separator2.setBackground(new Color(230, 230, 230));
            mainInfoPanel.add(separator2);

            // 3. Reservation schedule section - IMPROVED
            JPanel schedulePanel = new JPanel(new BorderLayout());
            schedulePanel.setBackground(Color.WHITE);
            schedulePanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 10, 20));

            JLabel scheduleLabel = new JLabel("Reservation Schedule");
            scheduleLabel.setFont(new Font("Arial", Font.BOLD, 14));

            schedulePanel.add(scheduleLabel, BorderLayout.NORTH);

            // Create a grid panel for the time details with better spacing
            JPanel timeDetailsPanel = new JPanel(new GridLayout(2, 2, 20, 10));
            timeDetailsPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0));
            timeDetailsPanel.setOpaque(false);

            // Format for dates and times
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy");
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

            // Start date and time values
            JPanel startDatePanel = new JPanel(new BorderLayout());
            startDatePanel.setOpaque(false);

            JLabel startDateLabel = new JLabel("Start Date:");
            startDateLabel.setFont(new Font("Arial", Font.BOLD, 13));

            JLabel startDateValue = null;
            if (reservation.containsKey("startDate")) {
                java.sql.Date startDate = (java.sql.Date) reservation.get("startDate");
                startDateValue = new JLabel(dateFormat.format(startDate));
            } else {
                startDateValue = new JLabel("Not available");
            }
            startDateValue.setFont(new Font("Arial", Font.PLAIN, 13));

            startDatePanel.add(startDateLabel, BorderLayout.NORTH);
            startDatePanel.add(startDateValue, BorderLayout.CENTER);

            // Start time panel
            JPanel startTimePanel = new JPanel(new BorderLayout());
            startTimePanel.setOpaque(false);

            JLabel startTimeLabel = new JLabel("Start Time:");
            startTimeLabel.setFont(new Font("Arial", Font.BOLD, 13));

            JLabel startTimeValue = null;
            if (reservation.containsKey("startTime")) {
                java.sql.Time startTime = (java.sql.Time) reservation.get("startTime");
                startTimeValue = new JLabel(timeFormat.format(startTime));
            } else {
                startTimeValue = new JLabel("Not available");
            }
            startTimeValue.setFont(new Font("Arial", Font.PLAIN, 13));

            startTimePanel.add(startTimeLabel, BorderLayout.NORTH);
            startTimePanel.add(startTimeValue, BorderLayout.CENTER);

            // End date panel
            JPanel endDatePanel = new JPanel(new BorderLayout());
            endDatePanel.setOpaque(false);

            JLabel endDateLabel = new JLabel("End Date:");
            endDateLabel.setFont(new Font("Arial", Font.BOLD, 13));

            JLabel endDateValue = null;
            if (reservation.containsKey("endDate")) {
                java.sql.Date endDate = (java.sql.Date) reservation.get("endDate");
                endDateValue = new JLabel(dateFormat.format(endDate));
            } else {
                endDateValue = new JLabel("Not available");
            }
            endDateValue.setFont(new Font("Arial", Font.PLAIN, 13));

            endDatePanel.add(endDateLabel, BorderLayout.NORTH);
            endDatePanel.add(endDateValue, BorderLayout.CENTER);

            // End time panel
            JPanel endTimePanel = new JPanel(new BorderLayout());
            endTimePanel.setOpaque(false);

            JLabel endTimeLabel = new JLabel("End Time:");
            endTimeLabel.setFont(new Font("Arial", Font.BOLD, 13));

            JLabel endTimeValue = null;
            if (reservation.containsKey("endTime")) {
                java.sql.Time endTime = (java.sql.Time) reservation.get("endTime");
                endTimeValue = new JLabel(timeFormat.format(endTime));
            } else {
                endTimeValue = new JLabel("Not available");
            }
            endTimeValue.setFont(new Font("Arial", Font.PLAIN, 13));

            endTimePanel.add(endTimeLabel, BorderLayout.NORTH);
            endTimePanel.add(endTimeValue, BorderLayout.CENTER);

            // Add all panels to the time details grid
            timeDetailsPanel.add(startDatePanel);
            timeDetailsPanel.add(startTimePanel);
            timeDetailsPanel.add(endDatePanel);
            timeDetailsPanel.add(endTimePanel);

            // Add to schedule panel
            schedulePanel.add(timeDetailsPanel, BorderLayout.CENTER);

            // Calculate duration if available
            if (reservation.containsKey("startDate") && reservation.containsKey("endDate") &&
                    reservation.containsKey("startTime") && reservation.containsKey("endTime")) {

                try {
                    // Calculate reservation duration for display
                    Calendar startCal = Calendar.getInstance();
                    startCal.setTime((java.sql.Date) reservation.get("startDate"));
                    Calendar startTimeCal = Calendar.getInstance();
                    startTimeCal.setTime((java.sql.Time) reservation.get("startTime"));

                    startCal.set(Calendar.HOUR_OF_DAY, startTimeCal.get(Calendar.HOUR_OF_DAY));
                    startCal.set(Calendar.MINUTE, startTimeCal.get(Calendar.MINUTE));

                    Calendar endCal = Calendar.getInstance();
                    endCal.setTime((java.sql.Date) reservation.get("endDate"));
                    Calendar endTimeCal = Calendar.getInstance();
                    endTimeCal.setTime((java.sql.Time) reservation.get("endTime"));

                    endCal.set(Calendar.HOUR_OF_DAY, endTimeCal.get(Calendar.HOUR_OF_DAY));
                    endCal.set(Calendar.MINUTE, endTimeCal.get(Calendar.MINUTE));

                    long durationMs = endCal.getTimeInMillis() - startCal.getTimeInMillis();
                    long durationHours = durationMs / (60 * 60 * 1000);
                    long durationMinutes = (durationMs % (60 * 60 * 1000)) / (60 * 1000);

                    String durationText = "Duration: " + durationHours + " hour" + (durationHours != 1 ? "s" : "");
                    if (durationMinutes > 0) {
                        durationText += " " + durationMinutes + " minute" + (durationMinutes != 1 ? "s" : "");
                    }

                    JLabel durationLabel = new JLabel(durationText);
                    durationLabel.setFont(new Font("Arial", Font.ITALIC, 12));
                    durationLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));

                    JPanel durationPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
                    durationPanel.setOpaque(false);
                    durationPanel.add(durationLabel);

                    schedulePanel.add(durationPanel, BorderLayout.SOUTH);
                } catch (Exception e) {
                    // Ignore duration calculation errors
                    System.err.println("Error calculating duration: " + e.getMessage());
                }
            }

            mainInfoPanel.add(schedulePanel);

            // Separator 3
            JSeparator separator3 = new JSeparator(JSeparator.HORIZONTAL);
            separator3.setForeground(new Color(230, 230, 230));
            separator3.setBackground(new Color(230, 230, 230));
            mainInfoPanel.add(separator3);

            // 4. Payment section - HIGHLIGHTED
            JPanel paymentPanel = new JPanel(new BorderLayout());
            paymentPanel.setBackground(Color.WHITE);
            paymentPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

            JLabel paymentLabel = new JLabel("Payment Information");
            paymentLabel.setFont(new Font("Arial", Font.BOLD, 14));

            paymentPanel.add(paymentLabel, BorderLayout.NORTH);

            // Fee panel with highlight
            JPanel feePanel = new JPanel(new BorderLayout());
            feePanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

            JPanel feeHighlightPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
            feeHighlightPanel.setBackground(new Color(240, 248, 255)); // Light blue background
            feeHighlightPanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(200, 220, 240)),
                    BorderFactory.createEmptyBorder(8, 12, 8, 12)));

            // Add fee with large font and currency formatting
            JLabel feeLabel = new JLabel();
            if (reservation.containsKey("fee")) {
                Object feeObj = reservation.get("fee");
                if (feeObj instanceof Number) {
                    double fee = ((Number) feeObj).doubleValue();
                    feeLabel.setText("Reservation Fee: " + String.format("%,.0f VND", fee));
                } else if (feeObj != null) {
                    feeLabel.setText("Reservation Fee: " + feeObj.toString());
                }
            } else {
                feeLabel.setText("No payment information available");
                feeLabel.setFont(new Font("Arial", Font.ITALIC, 14));
                feeLabel.setForeground(Color.GRAY);
            }

            feeLabel.setFont(new Font("Arial", Font.BOLD, 16));
            feeHighlightPanel.add(feeLabel);

            feePanel.add(feeHighlightPanel, BorderLayout.CENTER);
            paymentPanel.add(feePanel, BorderLayout.CENTER);

            mainInfoPanel.add(paymentPanel);

            // Add main info panel to content panel
            contentPanel.add(mainInfoPanel);

            // Add scroll capability
            JScrollPane scrollPane = new JScrollPane(contentPanel);
            scrollPane.setBorder(null);
            scrollPane.getVerticalScrollBar().setUnitIncrement(16);
            panel.add(scrollPane, BorderLayout.CENTER);

            // Button panel
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 15));
            buttonPanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 220, 220)),
                    BorderFactory.createEmptyBorder(10, 0, 10, 0)));

            JButton changeStatusButton = new JButton("Change to Available");
            changeStatusButton.setFont(new Font("Arial", Font.BOLD, 12));
            changeStatusButton.setPreferredSize(new Dimension(140, 32));
            changeStatusButton.addActionListener(e -> {
                dialog.dispose();
                toggleSlotStatus(slotNumber, false);
            });

            JButton closeButton = new JButton("Close");
            closeButton.setFont(new Font("Arial", Font.BOLD, 12));
            closeButton.setPreferredSize(new Dimension(100, 32));
            closeButton.addActionListener(e -> dialog.dispose());

            buttonPanel.add(changeStatusButton);
            buttonPanel.add(closeButton);

            panel.add(buttonPanel, BorderLayout.SOUTH);

            // Set dialog properties
            dialog.setContentPane(panel);
            dialog.setSize(480, 580);
            dialog.setLocationRelativeTo(this);
            dialog.setResizable(false);
            dialog.setVisible(true);

        } catch (Exception e) {
            System.err.println("Error showing slot information dialog: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error displaying slot information: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // Helper method to create a panel for when no reservation is found
    private JPanel createNoReservationPanel(String slotNumber) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel iconLabel = new JLabel(UIManager.getIcon("OptionPane.informationIcon"));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextArea messageArea = new JTextArea();
        messageArea.setText(
                "This slot is marked as occupied, but no active reservation was found in the system.\n\n" +
                        "Possible reasons:\n" +
                        "• The reservation status may not be set as 'ACTIVE'\n" +
                        "• The vehicle may be parked without a reservation\n" +
                        "• There may be a data synchronization issue\n" +
                        "• The reservation might have expired but the slot status wasn't updated\n\n" +
                        "You may want to check the slot physically or review recent reservations."
        );
        messageArea.setWrapStyleWord(true);
        messageArea.setLineWrap(true);
        messageArea.setOpaque(false);
        messageArea.setEditable(false);
        messageArea.setFocusable(false);
        messageArea.setFont(new Font("SansSerif", Font.PLAIN, 14));
        messageArea.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Add components to the panel
        JPanel iconPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        iconPanel.setOpaque(false);
        iconPanel.add(iconLabel);

        panel.add(iconPanel);
        panel.add(Box.createVerticalStrut(15));
        panel.add(messageArea);

        // Add a button to view reservation history
        JButton viewHistoryButton = new JButton("View Reservation History");
        viewHistoryButton.setFont(new Font("Arial", Font.BOLD, 12));
        viewHistoryButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setOpaque(false);
        buttonPanel.add(viewHistoryButton);

        panel.add(Box.createVerticalStrut(15));
        panel.add(buttonPanel);

        return panel;
    }

    /**
     * Creates a panel to display reservation details with formal styling
     * Including time period and cost/fee information with separate start/end time display
     */
    private JPanel createReservationPanel(Map<String, Object> reservation) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Reservation ID and Status section
        JPanel headerPanel = new JPanel(new GridLayout(2, 1, 0, 3));
        headerPanel.setOpaque(false);
        headerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        headerPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

        if (reservation.containsKey("reservationID")) {
            JLabel reservationIdLabel = new JLabel("Reservation #" + reservation.get("reservationID"));
            reservationIdLabel.setFont(new Font("Arial", Font.BOLD, 18));
            headerPanel.add(reservationIdLabel);
        }

        if (reservation.containsKey("status")) {
            String status = String.valueOf(reservation.get("status"));
            JLabel statusLabel = new JLabel("Status: " + status);
            statusLabel.setFont(new Font("Arial", Font.BOLD, 14));

            // Set color based on status
            if ("In Use".equalsIgnoreCase(status)) {
                statusLabel.setForeground(new Color(0, 128, 0)); // Green
            } else if ("Processing".equalsIgnoreCase(status)) {
                statusLabel.setForeground(new Color(255, 140, 0)); // Dark orange
            } else if ("Cancelled".equalsIgnoreCase(status)) {
                statusLabel.setForeground(new Color(178, 34, 34)); // Dark red
            }

            headerPanel.add(statusLabel);
        }

        panel.add(headerPanel);
        panel.add(Box.createVerticalStrut(10));

        // First separator
        JSeparator separator1 = new JSeparator();
        separator1.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        panel.add(separator1);
        panel.add(Box.createVerticalStrut(10));

        // Customer and Vehicle Information Section
        JPanel infoPanel = new JPanel(new GridLayout(0, 1, 0, 8));
        infoPanel.setOpaque(false);
        infoPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, infoPanel.getPreferredSize().height));

        // Customer Information
        if (reservation.containsKey("userName")) {
            JLabel userLabel = new JLabel("Customer: " + reservation.get("userName"));
            userLabel.setFont(new Font("Arial", Font.PLAIN, 14));
            infoPanel.add(userLabel);
        }

        // Vehicle Information
        if (reservation.containsKey("vehicleID")) {
            JLabel vehicleLabel = new JLabel("Vehicle License: " + reservation.get("vehicleID"));
            vehicleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
            infoPanel.add(vehicleLabel);
        }

        panel.add(infoPanel);
        panel.add(Box.createVerticalStrut(10));

        // Second separator
        JSeparator separator2 = new JSeparator();
        separator2.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        panel.add(separator2);
        panel.add(Box.createVerticalStrut(10));

        // Reservation Period Section - ENHANCED
        JPanel periodPanel = new JPanel();
        periodPanel.setLayout(new BoxLayout(periodPanel, BoxLayout.Y_AXIS));
        periodPanel.setOpaque(false);
        periodPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        periodPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, periodPanel.getPreferredSize().height));

        // Add a title for the section
        JLabel periodTitle = new JLabel("Reservation Schedule");
        periodTitle.setFont(new Font("Arial", Font.BOLD, 14));
        periodTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        periodPanel.add(periodTitle);
        periodPanel.add(Box.createVerticalStrut(10));

        // Create a table-like display for the time information
        JPanel timeGrid = new JPanel(new GridLayout(3, 2, 10, 8));
        timeGrid.setOpaque(false);
        timeGrid.setAlignmentX(Component.LEFT_ALIGNMENT);
        timeGrid.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        timeGrid.setMaximumSize(new Dimension(Integer.MAX_VALUE, timeGrid.getPreferredSize().height));

        // Add header cells with background color
        JPanel startHeaderPanel = new JPanel(new BorderLayout());
        startHeaderPanel.setBackground(new Color(240, 240, 240));
        JLabel headerStartLabel = new JLabel("Start", JLabel.CENTER);
        headerStartLabel.setFont(new Font("Arial", Font.BOLD, 13));
        startHeaderPanel.add(headerStartLabel);

        JPanel endHeaderPanel = new JPanel(new BorderLayout());
        endHeaderPanel.setBackground(new Color(240, 240, 240));
        JLabel headerEndLabel = new JLabel("End", JLabel.CENTER);
        headerEndLabel.setFont(new Font("Arial", Font.BOLD, 13));
        endHeaderPanel.add(headerEndLabel);

        timeGrid.add(startHeaderPanel);
        timeGrid.add(endHeaderPanel);

        // Parse the dates and times
        if (reservation.containsKey("startDate") && reservation.containsKey("endDate")) {
            java.sql.Date startDate = (java.sql.Date) reservation.get("startDate");
            java.sql.Date endDate = (java.sql.Date) reservation.get("endDate");

            // Date labels
            JLabel startDateLabel = new JLabel(dateFormat.format(startDate), JLabel.CENTER);
            startDateLabel.setFont(new Font("Arial", Font.PLAIN, 13));

            JLabel endDateLabel = new JLabel(dateFormat.format(endDate), JLabel.CENTER);
            endDateLabel.setFont(new Font("Arial", Font.PLAIN, 13));

            timeGrid.add(startDateLabel);
            timeGrid.add(endDateLabel);
        } else {
            // Fallback if dates are not available
            timeGrid.add(new JLabel("Date not available", JLabel.CENTER));
            timeGrid.add(new JLabel("Date not available", JLabel.CENTER));
        }

        // Time labels
        if (reservation.containsKey("startTime") && reservation.containsKey("endTime")) {
            java.sql.Time startTime = (java.sql.Time) reservation.get("startTime");
            java.sql.Time endTime = (java.sql.Time) reservation.get("endTime");

            JLabel startTimeLabel = new JLabel(timeFormat.format(startTime), JLabel.CENTER);
            startTimeLabel.setFont(new Font("Arial", Font.PLAIN, 13));

            JLabel endTimeLabel = new JLabel(timeFormat.format(endTime), JLabel.CENTER);
            endTimeLabel.setFont(new Font("Arial", Font.PLAIN, 13));

            timeGrid.add(startTimeLabel);
            timeGrid.add(endTimeLabel);
        } else {
            // Fallback if times are not available
            timeGrid.add(new JLabel("Time not available", JLabel.CENTER));
            timeGrid.add(new JLabel("Time not available", JLabel.CENTER));
        }

        periodPanel.add(timeGrid);

        // Calculate and display duration if possible
        if (reservation.containsKey("startDate") && reservation.containsKey("endDate") &&
                reservation.containsKey("startTime") && reservation.containsKey("endTime")) {

            try {
                java.sql.Date startDate = (java.sql.Date) reservation.get("startDate");
                java.sql.Time startTime = (java.sql.Time) reservation.get("startTime");
                java.sql.Date endDate = (java.sql.Date) reservation.get("endDate");
                java.sql.Time endTime = (java.sql.Time) reservation.get("endTime");

                Calendar startCal = Calendar.getInstance();
                startCal.setTime(startDate);
                Calendar startTimeCal = Calendar.getInstance();
                startTimeCal.setTime(startTime);
                startCal.set(Calendar.HOUR_OF_DAY, startTimeCal.get(Calendar.HOUR_OF_DAY));
                startCal.set(Calendar.MINUTE, startTimeCal.get(Calendar.MINUTE));

                Calendar endCal = Calendar.getInstance();
                endCal.setTime(endDate);
                Calendar endTimeCal = Calendar.getInstance();
                endTimeCal.setTime(endTime);
                endCal.set(Calendar.HOUR_OF_DAY, endTimeCal.get(Calendar.HOUR_OF_DAY));
                endCal.set(Calendar.MINUTE, endTimeCal.get(Calendar.MINUTE));

                long durationMs = endCal.getTimeInMillis() - startCal.getTimeInMillis();
                long durationHours = durationMs / (60 * 60 * 1000);
                long durationMinutes = (durationMs % (60 * 60 * 1000)) / (60 * 1000);

                JPanel durationPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
                durationPanel.setOpaque(false);
                durationPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
                durationPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, durationPanel.getPreferredSize().height));

                String durationText = "Total Duration: " + durationHours + " hour" + (durationHours != 1 ? "s" : "");
                if (durationMinutes > 0) {
                    durationText += " " + durationMinutes + " minute" + (durationMinutes != 1 ? "s" : "");
                }

                JLabel durationLabel = new JLabel(durationText);
                durationLabel.setFont(new Font("Arial", Font.ITALIC, 13));
                durationPanel.add(durationLabel);

                periodPanel.add(Box.createVerticalStrut(10));
                periodPanel.add(durationPanel);
            } catch (Exception e) {
                // Ignore duration calculation errors
                System.err.println("Error calculating reservation duration: " + e.getMessage());
            }
        }

        panel.add(periodPanel);
        panel.add(Box.createVerticalStrut(15));

        // Payment/Fee information section
        JSeparator separator3 = new JSeparator();
        separator3.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        panel.add(separator3);
        panel.add(Box.createVerticalStrut(10));

        JPanel paymentPanel = new JPanel();
        paymentPanel.setLayout(new BoxLayout(paymentPanel, BoxLayout.Y_AXIS));
        paymentPanel.setOpaque(false);
        paymentPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        paymentPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, paymentPanel.getPreferredSize().height));

        JLabel paymentTitle = new JLabel("Payment Information");
        paymentTitle.setFont(new Font("Arial", Font.BOLD, 14));
        paymentTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        paymentPanel.add(paymentTitle);
        paymentPanel.add(Box.createVerticalStrut(5));

        // Create a panel for the fee with a background color to make it stand out
        JPanel feeHighlightPanel = new JPanel();
        feeHighlightPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        feeHighlightPanel.setBackground(new Color(245, 250, 255)); // Light blue background
        feeHighlightPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(200, 220, 240)),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        feeHighlightPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        feeHighlightPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, feeHighlightPanel.getPreferredSize().height));

        // Check for fee first (from the Parking_Reservation table)
        boolean feeDisplayed = false;
        if (reservation.containsKey("fee")) {
            Object feeObj = reservation.get("fee");
            if (feeObj instanceof Number) {
                float fee = ((Number) feeObj).floatValue();
                // Format with commas for thousands
                JLabel feeLabel = new JLabel("Reservation Fee: " + String.format("%,.0f VND", fee));
                feeLabel.setFont(new Font("Arial", Font.BOLD, 14));
                feeHighlightPanel.add(feeLabel);
                feeDisplayed = true;
            } else if (feeObj != null) {
                JLabel feeLabel = new JLabel("Reservation Fee: " + feeObj);
                feeLabel.setFont(new Font("Arial", Font.BOLD, 14));
                feeHighlightPanel.add(feeLabel);
                feeDisplayed = true;
            }
        }

        // If no fee found, check for amount (from the Payment_Transaction table)
        if (!feeDisplayed && reservation.containsKey("amount")) {
            Object amountObj = reservation.get("amount");
            if (amountObj instanceof Number) {
                float amount = ((Number) amountObj).floatValue();
                // Format with commas for thousands
                JLabel amountLabel = new JLabel("Total Fee: " + String.format("%,.0f VND", amount));
                amountLabel.setFont(new Font("Arial", Font.BOLD, 14));
                feeHighlightPanel.add(amountLabel);
                feeDisplayed = true;
            }
        }

        // If no payment information found
        if (!feeDisplayed) {
            JLabel noFeeLabel = new JLabel("No payment information available");
            noFeeLabel.setFont(new Font("Arial", Font.ITALIC, 14));
            noFeeLabel.setForeground(Color.GRAY);
            feeHighlightPanel.add(noFeeLabel);
        }

        paymentPanel.add(feeHighlightPanel);
        panel.add(paymentPanel);

        // Add padding at the bottom
        panel.add(Box.createVerticalStrut(10));

        return panel;
    }

    private void showSlotReservationHistory(String slotNumber) {
        try {
            // Retrieve all reservations for this slot (past and present)
            List<Map<String, Object>> reservations = parkingManagementController.getAllReservationsForSlot(slotNumber);

            if (reservations == null || reservations.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "No reservation history found for slot " + slotNumber,
                        "No History",
                        JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            // Create a dialog to display reservation history
            JDialog historyDialog = new JDialog(this, "Reservation History - Slot " + slotNumber, true);
            historyDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

            JPanel panel = new JPanel(new BorderLayout(10, 10));
            panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

            // Create a table to display reservation history
            String[] columnNames = {"Reservation ID", "Status", "Start Date", "End Date", "Vehicle"};
            DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false; // Make all cells non-editable
                }
            };

            // Format for dates
            SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

            // Add reservations to the table
            for (Map<String, Object> res : reservations) {
                String startDateTime = "";
                String endDateTime = "";

                // Format start date/time
                if (res.containsKey("startDate") && res.containsKey("startTime")) {
                    Object startDateObj = res.get("startDate");
                    Object startTimeObj = res.get("startTime");

                    if (startDateObj instanceof java.sql.Date && startTimeObj instanceof java.sql.Time) {
                        java.util.Date startDate = new java.util.Date(
                                ((java.sql.Date) startDateObj).getTime() +
                                        ((java.sql.Time) startTimeObj).getTime());
                        startDateTime = dateTimeFormat.format(startDate);
                    }
                }

                // Format end date/time
                if (res.containsKey("endDate") && res.containsKey("endTime")) {
                    Object endDateObj = res.get("endDate");
                    Object endTimeObj = res.get("endTime");

                    if (endDateObj instanceof java.sql.Date && endTimeObj instanceof java.sql.Time) {
                        java.util.Date endDate = new java.util.Date(
                                ((java.sql.Date) endDateObj).getTime() +
                                        ((java.sql.Time) endTimeObj).getTime());
                        endDateTime = dateTimeFormat.format(endDate);
                    }
                }

                // Add row to table
                Object[] row = {
                        res.get("reservationID"),
                        res.get("status"),
                        startDateTime,
                        endDateTime,
                        res.get("vehicleID")
                };

                model.addRow(row);
            }

            // Create the table and apply some styling
            JTable historyTable = new JTable(model);
            historyTable.setRowHeight(25);
            historyTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
            historyTable.setFont(new Font("Arial", Font.PLAIN, 14));
            historyTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

            // Set up cell renderers for color-coding statuses
            historyTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value,
                                                               boolean isSelected, boolean hasFocus,
                                                               int row, int column) {
                    Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                    // If this is the status column, color code it
                    if (column == 1 && value != null) {
                        String status = value.toString();
                        if ("ACTIVE".equalsIgnoreCase(status)) {
                            c.setForeground(new Color(0, 128, 0)); // Green
                        } else if ("PENDING".equalsIgnoreCase(status)) {
                            c.setForeground(new Color(255, 140, 0)); // Dark orange
                        } else if ("CANCELLED".equalsIgnoreCase(status)) {
                            c.setForeground(new Color(178, 34, 34)); // Dark red
                        } else if ("COMPLETED".equalsIgnoreCase(status)) {
                            c.setForeground(new Color(0, 0, 139)); // Dark blue
                        } else {
                            c.setForeground(Color.BLACK);
                        }

                        // Make status column bold
                        c.setFont(new Font("Arial", Font.BOLD, 14));
                    } else if (isSelected) {
                        c.setForeground(table.getSelectionForeground());
                    } else {
                        c.setForeground(table.getForeground());
                    }

                    return c;
                }
            });

            // Add the table to a scroll pane
            JScrollPane scrollPane = new JScrollPane(historyTable);
            scrollPane.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createEmptyBorder(10, 0, 10, 0),
                    scrollPane.getBorder()
            ));

            panel.add(new JLabel("Reservation History for Slot " + slotNumber), BorderLayout.NORTH);
            panel.add(scrollPane, BorderLayout.CENTER);

            // Button panel
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            JButton closeButton = new JButton("Close");
            closeButton.addActionListener(e -> historyDialog.dispose());
            buttonPanel.add(closeButton);
            panel.add(buttonPanel, BorderLayout.SOUTH);

            // Set dialog properties
            historyDialog.setContentPane(panel);
            historyDialog.setSize(700, 400);
            historyDialog.setLocationRelativeTo(this);
            historyDialog.setVisible(true);

        } catch (Exception e) {
            System.err.println("Error showing reservation history: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error retrieving reservation history: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
    /**
     * Create a section with a title for the details panel
     */
    private JPanel createSection(String title, Color accentColor) {
        JPanel section = new JPanel();
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setOpaque(false);

        // Add a title with an accent colored background
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(accentColor);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel, BorderLayout.WEST);

        // Make the title panel full width
        titlePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, titlePanel.getPreferredSize().height));
        section.add(titlePanel);
        section.add(Box.createVerticalStrut(10));

        return section;
    }


    /**
     * Helper method to add a field to a section panel
     */
    private void addDetailField(JPanel panel, String label, String value) {
        JPanel fieldPanel = new JPanel(new BorderLayout(10, 0));
        fieldPanel.setOpaque(false);

        JLabel labelComponent = new JLabel(label);
        labelComponent.setFont(new Font("Arial", Font.BOLD, 14));

        JLabel valueComponent = new JLabel(value);
        valueComponent.setFont(new Font("Arial", Font.PLAIN, 14));

        fieldPanel.add(labelComponent, BorderLayout.WEST);
        fieldPanel.add(valueComponent, BorderLayout.CENTER);

        // Make the field panel full width
        fieldPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, fieldPanel.getPreferredSize().height));
        panel.add(fieldPanel);
        panel.add(Box.createVerticalStrut(5));
    }

    private void removeSlot(String slotNumber) {
        try {
            System.out.println("\n========== ATTEMPTING TO REMOVE SLOT: " + slotNumber + " ==========");

            // First check if this slot has any active reservations
            List<Map<String, Object>> activeReservations = parkingManagementController.getActiveReservationsForSlot(slotNumber);

            if (activeReservations == null) {
                System.out.println("Warning: Received null from getActiveReservationsForSlot");
                // Handle null case more gracefully - create empty list
                activeReservations = new ArrayList<>();
            }

            System.out.println("Active reservations found: " + activeReservations.size());

            if (!activeReservations.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Cannot remove slot with active reservations.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Get all existing slots to determine if we're removing largest/medium/smallest
            List<ParkingSlot> existingSlots = parkingManagementController.getParkingSlotsByParkingId(currentParkingSpace.getParkingID());
            String parkingPrefix = "P" + currentParkingSpace.getParkingID().replaceAll("[^0-9]", "");

            // Parse all slot numbers into a map of numeric value -> slot
            Map<Integer, ParkingSlot> slotMap = new HashMap<>();
            for (ParkingSlot slot : existingSlots) {
                String slotNum = slot.getSlotNumber();
                if (slotNum.contains(parkingPrefix)) {
                    try {
                        String numericPart = slotNum.substring(0, slotNum.indexOf(parkingPrefix));
                        int numValue = Integer.parseInt(numericPart);
                        slotMap.put(numValue, slot);
                    } catch (Exception e) {
                        // Skip slots that don't match our expected format
                        System.out.println("Skipping malformed slot: " + slotNum);
                        continue;
                    }
                }
            }

            // Find min, max, and the slot being removed
            int minValue = Integer.MAX_VALUE;
            int maxValue = Integer.MIN_VALUE;
            int removeValue = -1;

            for (Integer num : slotMap.keySet()) {
                if (num < minValue) minValue = num;
                if (num > maxValue) maxValue = num;

                if (slotMap.get(num).getSlotNumber().equals(slotNumber)) {
                    removeValue = num;
                }
            }

            System.out.println("Slot analysis: minValue=" + minValue + ", maxValue=" + maxValue + ", removeValue=" + removeValue);

            // Determine if we need to renumber (based on if it's the largest slot or not)
            boolean needsRenumbering = (removeValue != maxValue);

            // Prepare warning message based on what we're removing
            String warningMessage = "Are you sure you want to remove this slot?";
            if (removeValue == maxValue) {
                warningMessage = "Warning: You are removing the slot with the largest number. Are you sure?";
            } else if (removeValue == minValue) {
                warningMessage = "Warning: You are removing the slot with the smallest number. Slots will be renumbered. Are you sure?";
            } else {
                // It's a "medium" slot (not min or max)
                warningMessage = "Warning: You are removing a middle-numbered slot. Slots will be renumbered. Are you sure?";
            }

            int confirm = JOptionPane.showConfirmDialog(this,
                    warningMessage,
                    "Confirm Removal",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

            if (confirm == JOptionPane.YES_OPTION) {
                boolean success;

                if (needsRenumbering) {
                    // Use the new two-phase removal method
                    System.out.println("Using two-phase removal for slot: " + slotNumber);
                    success = parkingManagementController.twoPhaseRemoveSlot(slotNumber, removeValue);
                } else {
                    // For largest slot, use simple removal (no renumbering)
                    System.out.println("Using simple removal for slot (no renumbering): " + slotNumber);
                    success = parkingManagementController.removeParkingSlot(slotNumber);
                }

                if (success) {
                    JOptionPane.showMessageDialog(this,
                            needsRenumbering ?
                                    "Slot removed successfully and slots renumbered." :
                                    "Slot removed successfully.",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);

                    // Update the currentParkingSpace object
                    currentParkingSpace.setNumberOfSlots(currentParkingSpace.getNumberOfSlots() - 1);

                    // Refresh the view
                    loadParkingData();
                } else {
                    // Try direct removal as last resort
                    System.out.println("Standard removal failed, trying direct force removal as last resort");
                    boolean forcedRemovalSuccess = parkingManagementController.forceRemoveSlot(slotNumber);

                    if (forcedRemovalSuccess) {
                        JOptionPane.showMessageDialog(this,
                                "Slot removed successfully (forced removal).",
                                "Success",
                                JOptionPane.INFORMATION_MESSAGE);

                        // Update the currentParkingSpace object
                        currentParkingSpace.setNumberOfSlots(currentParkingSpace.getNumberOfSlots() - 1);

                        // Refresh the view
                        loadParkingData();
                    } else {
                        JOptionPane.showMessageDialog(this,
                                "Failed to remove slot. See console for details.",
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        } catch (Exception ex) {
            System.out.println("Exception in removeSlot: " + ex.getMessage());
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error removing slot: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
        System.out.println("=========== END OF SLOT REMOVAL PROCESS ===========\n");
    }

    private void updateParkingInfo() {
        // Show a dialog to edit parking information
        JTextField addressField = new JTextField(currentParkingSpace.getParkingAddress(), 20);
        JTextField costField = new JTextField(String.valueOf(currentParkingSpace.getCostOfParking()), 20);
        JTextArea descriptionArea = new JTextArea(currentParkingSpace.getDescription(), 5, 20);
        descriptionArea.setLineWrap(true);
        JScrollPane descScrollPane = new JScrollPane(descriptionArea);

        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Address:"));
        panel.add(addressField);
        panel.add(new JLabel("Cost per Hour:"));
        panel.add(costField);
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

            toggleButton = new JButton("Change Status");
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

            toggleButton = new JButton("Change Status");
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
        mockParkingSpace.setDescription("Bãi đỗ xe ngoài trời, có camera an ninh");
        mockParkingSpace.setAdminID(1);

        SwingUtilities.invokeLater(() -> {
            new ParkingPlotDetailView(mockAdmin, mockParkingSpace);
        });
    }
}