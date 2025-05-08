package main.java.com.parkeasy.view.user;

import main.java.com.parkeasy.controller.user.ReservationController;
import main.java.com.parkeasy.controller.user.ReviewController;
import main.java.com.parkeasy.model.Reservation;
import main.java.com.parkeasy.model.User;
import main.java.com.parkeasy.service.ParkingSpaceService;
import main.java.com.parkeasy.service.ReservationService;
import main.java.com.parkeasy.service.ReviewService;
import main.java.com.parkeasy.service.VehicleService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * View for displaying user's reservation history
 * Shows past and upcoming reservations with details
 */
public class ReservationHistoryView extends JFrame {
    private User currentUser;
    private ReservationController reservationController;
    private ReviewController reviewController;

    private JTabbedPane tabbedPane;
    private JTable activeReservationsTable;
    private JTable pastReservationsTable;
    private JButton viewDetailsButton;
    private JButton backButton;

    public ReservationHistoryView(User user) {
        this.currentUser = user;

        // Initialize services and controller
        VehicleService vehicleService = new VehicleService();
        ReservationService reservationService = new ReservationService();
        ParkingSpaceService parkingSpaceService = new ParkingSpaceService();
        ReviewService reviewService = new ReviewService();

        reservationController = new ReservationController(
                reservationService, parkingSpaceService, vehicleService);
        reviewController = new ReviewController(reviewService);

        // Set up the frame
        setTitle("ParkEasy - Reservation History");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create components
        initComponents();

        // Layout the components
        layoutComponents();

        // Load data
        loadReservations();

        // Make the frame visible
        setVisible(true);
    }

    private void initComponents() {
        tabbedPane = new JTabbedPane();

        // Create tables for active and past reservations
        String[] columnNames = {"Reservation ID", "Vehicle", "Parking Address", "Slot", "Start Time", "End Time", "Status"};

        DefaultTableModel activeModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        activeReservationsTable = new JTable(activeModel);
        activeReservationsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        DefaultTableModel pastModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        pastReservationsTable = new JTable(pastModel);
        pastReservationsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Add double-click listeners to tables
        activeReservationsTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int selectedRow = activeReservationsTable.getSelectedRow();
                    if (selectedRow >= 0) {
                        int reservationId = (int) activeReservationsTable.getValueAt(selectedRow, 0);
                        showReservationDetailsWithActions(reservationId);
                    }
                }
            }
        });

        pastReservationsTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int selectedRow = pastReservationsTable.getSelectedRow();
                    if (selectedRow >= 0) {
                        int reservationId = (int) pastReservationsTable.getValueAt(selectedRow, 0);
                        showReservationDetailsWithActions(reservationId);
                    }
                }
            }
        });

        // Create buttons
        viewDetailsButton = new JButton("View Details");
        backButton = new JButton("Back to Dashboard");

        // Add action listeners
        viewDetailsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                viewReservationDetails();
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

        // Add tables to scroll panes
        JScrollPane activeScrollPane = new JScrollPane(activeReservationsTable);
        JScrollPane pastScrollPane = new JScrollPane(pastReservationsTable);

        // Add scroll panes to tabbed pane
        tabbedPane.addTab("Active Reservations", activeScrollPane);
        tabbedPane.addTab("Past Reservations", pastScrollPane);

        // Add tabbed pane to the center
        add(tabbedPane, BorderLayout.CENTER);

        // Create button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.add(viewDetailsButton);
        buttonPanel.add(backButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadReservations() {
        try {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

            // Get active reservations
            List<Map<String, Object>> activeReservations = reservationController.getActiveReservations(currentUser.getUserID());

            // Update table
            updateReservationsTable(activeReservationsTable, activeReservations);

            // Get reservation history
            List<Map<String, Object>> pastReservations = reservationController.getReservationHistory(currentUser.getUserID());
            updateReservationsTable(pastReservationsTable, pastReservations);

            // Update tab titles with count
            tabbedPane.setTitleAt(0, "Active Reservations (" + activeReservations.size() + ")");
            tabbedPane.setTitleAt(1, "Past Reservations (" + pastReservations.size() + ")");

            // Apply custom row colors for better readability
            applyTableColors(activeReservationsTable);
            applyTableColors(pastReservationsTable);

            setCursor(Cursor.getDefaultCursor());
        } catch (Exception ex) {
            setCursor(Cursor.getDefaultCursor());
            JOptionPane.showMessageDialog(this,
                    "Error loading reservations: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void applyTableColors(JTable table) {
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                if (!isSelected) {
                    // Get status from the status column (index 6)
                    String status = (String) table.getValueAt(row, 6);

                    if (status != null) {
                        if (status.equalsIgnoreCase("ACTIVE") || status.equalsIgnoreCase("IN USE")) {
                            c.setBackground(new Color(230, 255, 230)); // Light green
                        } else if (status.equalsIgnoreCase("PENDING")) {
                            c.setBackground(new Color(255, 255, 230)); // Light yellow
                        } else if (status.equalsIgnoreCase("COMPLETED")) {
                            c.setBackground(new Color(240, 240, 240)); // Light gray
                        } else if (status.equalsIgnoreCase("CANCELLED")) {
                            c.setBackground(new Color(255, 230, 230)); // Light red
                        } else {
                            c.setBackground(Color.WHITE);
                        }
                    }
                }

                return c;
            }
        });
    }

    private void showReservationDetailsWithActions(int reservationId) {
        try {
            Map<String, Object> details = reservationController.getReservationDetails(
                    reservationId, currentUser.getUserID());

            if ((boolean) details.get("success")) {
                Reservation reservation = (Reservation) details.get("reservation");
                LocalDateTime startDateTime = (LocalDateTime) details.get("startDateTime");
                LocalDateTime endDateTime = (LocalDateTime) details.get("endDateTime");
                LocalDateTime now = LocalDateTime.now();

                // Create dialog
                JDialog detailsDialog = new JDialog(this, "Reservation Details", true);
                detailsDialog.setLayout(new BorderLayout());

                JPanel mainPanel = new JPanel();
                mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
                mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

                // Add header
                JLabel titleLabel = new JLabel("Reservation Details");
                titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
                titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                mainPanel.add(titleLabel);
                mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));

                // Create status panel with colored dot for status
                JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

                // Create a colored dot indicator
                JPanel statusDot = new JPanel() {
                    @Override
                    protected void paintComponent(Graphics g) {
                        super.paintComponent(g);
                        // Green for processing/active, blue for completed, red for cancelled
                        if (reservation.getStatus().equalsIgnoreCase("Processing")) {
                            g.setColor(new Color(46, 204, 113)); // Green
                        } else if (reservation.getStatus().equalsIgnoreCase("Completed")) {
                            g.setColor(new Color(52, 152, 219)); // Blue
                        } else {
                            g.setColor(new Color(231, 76, 60)); // Red
                        }
                        g.fillOval(0, 0, 12, 12);
                    }
                };
                statusDot.setPreferredSize(new Dimension(12, 12));

                JLabel statusLabel = new JLabel("Status: ");
                statusLabel.setFont(new Font("Arial", Font.BOLD, 14));

                JLabel statusValue = new JLabel(reservation.getStatus());
                statusValue.setFont(new Font("Arial", Font.PLAIN, 14));

                statusPanel.add(statusDot);
                statusPanel.add(Box.createRigidArea(new Dimension(5, 0)));
                statusPanel.add(statusLabel);
                statusPanel.add(statusValue);

                mainPanel.add(statusPanel);
                mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));

                // Add basic info
                JPanel infoPanel = new JPanel(new GridLayout(0, 2, 10, 5));
                addDetailRow(infoPanel, "Reservation ID:", String.valueOf(reservation.getReservationID()));
                addDetailRow(infoPanel, "Location:", (String) details.get("parkingAddress"));
                addDetailRow(infoPanel, "Slot:", (String) details.get("slotNumber"));

                // Extract and display vehicle ID
                String vehicleString = details.get("vehicle").toString();
                String vehicleId = vehicleString;

                // Extract vehicle ID from string if needed
                if (vehicleString.contains("vehicleID=")) {
                    int startIndex = vehicleString.indexOf("vehicleID='") + 11; // +11 to skip "vehicleID='"
                    int endIndex = vehicleString.indexOf("'", startIndex);
                    if (endIndex != -1) {
                        vehicleId = vehicleString.substring(startIndex, endIndex);
                    }
                }

                addDetailRow(infoPanel, "Vehicle:", vehicleId);

                // Format times
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy 'at' h:mm a");
                addDetailRow(infoPanel, "Start Time:", startDateTime.format(formatter));
                addDetailRow(infoPanel, "End Time:", endDateTime.format(formatter));

                // Add duration and cost
                long durationHours = (long) details.get("durationHours");
                addDetailRow(infoPanel, "Duration:", durationHours + " hour(s)");

                // Format the hourly rate with VND currency format
                float hourlyRate = (float) details.get("hourlyRate");
                addDetailRow(infoPanel, "Hourly Rate:", String.format("%,.0f VND", hourlyRate));

                // Format the total cost with VND currency format and highlight it
                float totalCost = (float) details.get("totalCost");

                // Highlight total cost with color
                JLabel totalCostLabel = new JLabel("Total Cost:");
                totalCostLabel.setFont(new Font("Arial", Font.BOLD, 12));

// Format with VND currency format
                JLabel totalCostValue = new JLabel(String.format("%,.0f VND", totalCost));
                totalCostValue.setFont(new Font("Arial", Font.BOLD, 12));
                totalCostValue.setForeground(new Color(52, 152, 219)); // Blue color

                infoPanel.add(totalCostLabel);
                infoPanel.add(totalCostValue);

                mainPanel.add(infoPanel);
                mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));

                // Calculate progress
                long totalDuration = Duration.between(startDateTime, endDateTime).toMinutes();
                long elapsedDuration = Duration.between(startDateTime, now).toMinutes();
                int progressPercent = (int) Math.min(100, Math.max(0, (elapsedDuration * 100) / totalDuration));

                // Create custom progress panel
                JPanel progressPanel = new JPanel(new BorderLayout(0, 5));
                progressPanel.setBorder(BorderFactory.createTitledBorder("Reservation Timeline"));

                JProgressBar progressBar = new JProgressBar(0, 100);
                progressBar.setValue(progressPercent);
                progressBar.setStringPainted(true);
                progressBar.setString(progressPercent + "%");

                // Custom panel to show start and end times
                JPanel timelinePanel = new JPanel(new BorderLayout());
                DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("MMM d, HH:mm");
                JLabel startLabel = new JLabel(startDateTime.format(timeFormatter));
                JLabel endLabel = new JLabel(endDateTime.format(timeFormatter));
                timelinePanel.add(startLabel, BorderLayout.WEST);
                timelinePanel.add(endLabel, BorderLayout.EAST);

                progressPanel.add(progressBar, BorderLayout.CENTER);
                progressPanel.add(timelinePanel, BorderLayout.SOUTH);

                mainPanel.add(progressPanel);
                mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));

                // Add time details
                JPanel timePanel = new JPanel();
                timePanel.setLayout(new BoxLayout(timePanel, BoxLayout.Y_AXIS));
                timePanel.setBorder(BorderFactory.createTitledBorder("Time Details"));
                timePanel.setAlignmentX(Component.CENTER_ALIGNMENT);

                // Calculate time remaining
                String timeRemaining;
                if (now.isBefore(startDateTime)) {
                    Duration toStart = Duration.between(now, startDateTime);
                    long hours = toStart.toHours();
                    long minutes = toStart.toMinutesPart();
                    timeRemaining = String.format("Starts in: %d hours, %d minutes", hours, minutes);
                } else if (now.isBefore(endDateTime)) {
                    Duration toEnd = Duration.between(now, endDateTime);
                    long hours = toEnd.toHours();
                    long minutes = toEnd.toMinutesPart();
                    timeRemaining = String.format("Time remaining: %d hours, %d minutes", hours, minutes);
                } else {
                    timeRemaining = "Reservation has ended";
                }

                JLabel remainingLabel = new JLabel(timeRemaining);
                remainingLabel.setFont(new Font("Arial", Font.BOLD, 14));
                remainingLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

                timePanel.add(Box.createRigidArea(new Dimension(0, 5)));
                timePanel.add(remainingLabel);
                timePanel.add(Box.createRigidArea(new Dimension(0, 5)));

                mainPanel.add(timePanel);
                mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));

                // Add action buttons
                JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));

                // Only show Complete button if the reservation is not already completed
                JButton completeButton = new JButton("Complete Reservation");
                completeButton.setBackground(new Color(46, 204, 113)); // Green
                completeButton.setForeground(Color.WHITE);
                completeButton.setEnabled(!reservation.getStatus().equalsIgnoreCase("Completed"));

                JButton closeButton = new JButton("Close");
                closeButton.setBackground(new Color(189, 195, 199)); // Light gray

                // Add action listeners
                completeButton.addActionListener(e -> {
                    detailsDialog.dispose();
                    completeReservation(reservationId);
                });

                closeButton.addActionListener(e -> detailsDialog.dispose());

                // Only add the Complete button if the reservation isn't already completed
                if (!reservation.getStatus().equalsIgnoreCase("Completed")) {
                    buttonPanel.add(completeButton);
                }
                buttonPanel.add(closeButton);

                mainPanel.add(buttonPanel);

                // Set up dialog
                detailsDialog.add(new JScrollPane(mainPanel), BorderLayout.CENTER);
                detailsDialog.setSize(500, 600);
                detailsDialog.setLocationRelativeTo(this);
                detailsDialog.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Failed to retrieve reservation details: " + details.get("message"),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error retrieving reservation details: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void completeReservation(int reservationId) {
        try {
            // First confirm with the user
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to complete this reservation?\nReservation ID: " + reservationId,
                    "Confirm Completion",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                // Call the controller to complete the reservation
                Map<String, Object> result = reservationController.completeReservation(
                        reservationId, currentUser.getUserID());

                boolean success = (boolean) result.get("success");
                if (success) {
                    // Show rating dialog
                    showRatingDialog(reservationId);
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Failed to complete reservation: " + result.get("message"),
                            "Completion Failed",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error completing reservation: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showRatingDialog(int reservationId) {
        // Create a dialog for rating
        JDialog ratingDialog = new JDialog(this, "Rate Your Parking Experience", true);
        ratingDialog.setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Add header
        JLabel titleLabel = new JLabel("Rate Your Parking Experience");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Add rating instruction
        JLabel instructionLabel = new JLabel("Please rate your experience (1-5 stars):");
        instructionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(instructionLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Add star rating panel
        JPanel ratingPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        ButtonGroup ratingGroup = new ButtonGroup();

        JPanel starsPanel = new JPanel();
        starsPanel.setLayout(new BoxLayout(starsPanel, BoxLayout.X_AXIS));

        int[] selectedRating = {0}; // Use array to store selected rating

        for (int i = 1; i <= 5; i++) {
            final int rating = i;
            JToggleButton starButton = new JToggleButton("★", false);
            starButton.setFont(new Font("Arial", Font.PLAIN, 24));
            starButton.setFocusPainted(false);

            // Style the buttons to look more like stars
            starButton.setBackground(Color.WHITE);
            starButton.setForeground(Color.GRAY);

            starButton.addActionListener(e -> {
                selectedRating[0] = rating;

                // Update all star buttons based on the selected rating
                Component[] components = starsPanel.getComponents();
                for (int j = 0; j < components.length; j++) {
                    if (components[j] instanceof JToggleButton) {
                        JToggleButton btn = (JToggleButton) components[j];
                        if (j < rating) {
                            btn.setForeground(Color.ORANGE);
                        } else {
                            btn.setForeground(Color.GRAY);
                        }
                    }
                }
            });

            ratingGroup.add(starButton);
            starsPanel.add(starButton);
        }

        ratingPanel.add(starsPanel);
        mainPanel.add(ratingPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Add submit button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton submitButton = new JButton("Submit Rating");
        JButton cancelButton = new JButton("Skip Rating");

        submitButton.addActionListener(e -> {
            if (selectedRating[0] == 0) {
                JOptionPane.showMessageDialog(ratingDialog,
                        "Please select a rating before submitting.",
                        "Rating Required",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                // Submit the rating without comments
                Map<String, Object> result = reviewController.submitReview(
                        currentUser.getUserID(),
                        reservationId,
                        selectedRating[0],
                        ""); // Empty comment

                boolean success = (boolean) result.get("success");
                if (success) {
                    JOptionPane.showMessageDialog(ratingDialog,
                            "Thank you for your feedback!",
                            "Rating Submitted",
                            JOptionPane.INFORMATION_MESSAGE);

                    ratingDialog.dispose();

                    // Reload reservations
                    loadReservations();
                } else {
                    JOptionPane.showMessageDialog(ratingDialog,
                            "Failed to submit rating: " + result.get("message"),
                            "Submission Failed",
                            JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(ratingDialog,
                        "Error submitting rating: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> {
            ratingDialog.dispose();
            // Reload reservations anyway
            loadReservations();
        });

        buttonPanel.add(submitButton);
        buttonPanel.add(cancelButton);
        mainPanel.add(buttonPanel);

        // Set up dialog
        ratingDialog.add(mainPanel, BorderLayout.CENTER);
        ratingDialog.pack();
        ratingDialog.setSize(400, 350); // Smaller size since no comment section
        ratingDialog.setLocationRelativeTo(this);
        ratingDialog.setVisible(true);
    }

    private void updateReservationsTable(JTable table, List<Map<String, Object>> reservations) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0); // Clear table

        if (reservations.isEmpty()) {
            return;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        for (Map<String, Object> reservationData : reservations) {
            Reservation reservation = (Reservation) reservationData.get("reservation");

            LocalDateTime startDateTime = (LocalDateTime) reservationData.get("startDateTime");
            LocalDateTime endDateTime = (LocalDateTime) reservationData.get("endDateTime");

            // Format dates with null checks
            String formattedStartTime = startDateTime != null ? startDateTime.format(formatter) : "N/A";
            String formattedEndTime = endDateTime != null ? endDateTime.format(formatter) : "N/A";

            // Extract just the vehicle ID instead of using the full vehicle object representation
            String vehicleId = reservationData.get("vehicle").toString();

            // Check if the vehicle is returned as a Vehicle object
            if (vehicleId.contains("vehicleID=")) {
                // Extract just the vehicle ID from the string representation
                int startIndex = vehicleId.indexOf("vehicleID='") + 11; // +11 to skip "vehicleID='"
                int endIndex = vehicleId.indexOf("'", startIndex);
                if (endIndex != -1) {
                    vehicleId = vehicleId.substring(startIndex, endIndex);
                }
            }

            Object[] row = {
                    reservation.getReservationID(),
                    vehicleId, // Use the extracted vehicle ID
                    reservationData.get("parkingAddress"),
                    reservationData.get("slotNumber"),
                    formattedStartTime,
                    formattedEndTime,
                    reservation.getStatus()
            };

            model.addRow(row);
        }
    }

    private void viewReservationDetails() {
        JTable currentTable = tabbedPane.getSelectedIndex() == 0 ? activeReservationsTable : pastReservationsTable;

        int selectedRow = currentTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this,
                    "Please select a reservation to view.",
                    "Selection Required",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int reservationId = (int) currentTable.getValueAt(selectedRow, 0);
        showReservationDetailsWithActions(reservationId);
    }

    private void addDetailRow(JPanel panel, String label, String value) {
        JLabel labelComponent = new JLabel(label);
        labelComponent.setFont(new Font("Arial", Font.BOLD, 12));

        JLabel valueComponent = new JLabel(value);

        panel.add(labelComponent);
        panel.add(valueComponent);
    }

    // Main method for testing
    public static void main(String[] args) {
        // Create a mock user for testing
        User mockUser = new User();
        mockUser.setUserID(1);
        mockUser.setUserName("Test User");

        SwingUtilities.invokeLater(() -> {
            new ReservationHistoryView(mockUser);
        });
    }
}