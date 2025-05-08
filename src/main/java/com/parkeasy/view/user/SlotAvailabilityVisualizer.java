// New class: SlotAvailabilityVisualizer.java
package main.java.com.parkeasy.view.user;

import main.java.com.parkeasy.model.ParkingSlot;
import main.java.com.parkeasy.controller.admin.ParkingManagementController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Objects;

/**
 * Visual representation of parking slots for a parking space
 * Allows admins to quickly see occupancy status and manage slots
 */
public class SlotAvailabilityVisualizer extends JPanel {
    private final String parkingId;
    private final ParkingManagementController controller;
    private final JFrame parentFrame;
    
    private List<ParkingSlot> slots;
    private Map<String, Rectangle> slotRectangles = new HashMap<>();
    private String selectedSlot = null;
    
    // Colors
    private final Color AVAILABLE_COLOR = new Color(120, 200, 120);
    private final Color OCCUPIED_COLOR = new Color(220, 100, 100);
    private final Color SELECTED_COLOR = new Color(100, 150, 220);
    private final Color HOVER_COLOR = new Color(180, 180, 220);
    
    private String hoverSlot = null;
    
    public SlotAvailabilityVisualizer(String parkingId, JFrame parentFrame) {
        this.parkingId = parkingId;
        this.parentFrame = parentFrame;
        this.controller = new ParkingManagementController();
        
        setPreferredSize(new Dimension(600, 400));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(Color.WHITE);
        
        // Load slots
        loadSlots();
        
        // Add mouse listeners for interaction
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleMouseClick(e.getX(), e.getY());
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                hoverSlot = null;
                repaint();
            }
        });
        
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                updateHoverSlot(e.getX(), e.getY());
            }
        });
    }
    
    private void loadSlots() {
        try {
            slots = controller.getParkingSlotsByParkingId(parkingId);
            calculateSlotPositions();
            repaint();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(parentFrame, 
                "Error loading parking slots: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void calculateSlotPositions() {
        if (slots == null || slots.isEmpty()) {
            return;
        }
        
        slotRectangles.clear();
        
        // Calculate grid dimensions
        int slotCount = slots.size();
        int cols = (int) Math.ceil(Math.sqrt(slotCount));
        int rows = (int) Math.ceil((float) slotCount / cols);
        
        // Calculate slot size
        int padding = 20;
        int availableWidth = getWidth() - (2 * padding);
        int availableHeight = getHeight() - (2 * padding);
        
        int slotWidth = availableWidth / cols;
        int slotHeight = availableHeight / rows;
        
        // Minimum slot size
        slotWidth = Math.max(slotWidth, 60);
        slotHeight = Math.max(slotHeight, 60);
        
        // Create rectangles for each slot
        int index = 0;
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                if (index < slotCount) {
                    ParkingSlot slot = slots.get(index);
                    int x = padding + (col * slotWidth);
                    int y = padding + (row * slotHeight);
                    
                    slotRectangles.put(slot.getSlotNumber(), 
                        new Rectangle(x, y, slotWidth - 10, slotHeight - 10));
                    
                    index++;
                }
            }
        }
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Recalculate positions if needed
        if (slotRectangles.isEmpty() && slots != null && !slots.isEmpty()) {
            calculateSlotPositions();
        }
        
        // Draw slots
        for (ParkingSlot slot : slots) {
            Rectangle rect = slotRectangles.get(slot.getSlotNumber());
            if (rect != null) {
                // Determine color
                Color slotColor;
                if (slot.getSlotNumber().equals(selectedSlot)) {
                    slotColor = SELECTED_COLOR;
                } else if (slot.getSlotNumber().equals(hoverSlot)) {
                    slotColor = HOVER_COLOR;
                } else {
                    slotColor = slot.getAvailability() ? AVAILABLE_COLOR : OCCUPIED_COLOR;
                }
                
                // Draw rounded rectangle
                g2d.setColor(slotColor);
                g2d.fillRoundRect(rect.x, rect.y, rect.width, rect.height, 15, 15);
                
                // Draw border
                g2d.setColor(Color.DARK_GRAY);
                g2d.setStroke(new BasicStroke(1.5f));
                g2d.drawRoundRect(rect.x, rect.y, rect.width, rect.height, 15, 15);
                
                // Draw slot number
                g2d.setColor(Color.BLACK);
                g2d.setFont(new Font("Arial", Font.BOLD, 12));
                FontMetrics fm = g2d.getFontMetrics();
                String text = slot.getSlotNumber();
                int textWidth = fm.stringWidth(text);
                g2d.drawString(text, 
                    rect.x + (rect.width - textWidth) / 2,
                    rect.y + rect.height / 2 - 5);

// Draw status text
                g2d.setFont(new Font("Arial", Font.PLAIN, 10));
                String status = slot.getAvailability() ? "Available" : "Occupied";
                textWidth = fm.stringWidth(status);
                g2d.drawString(status,
                        rect.x + (rect.width - textWidth) / 2,  // X coordinate
                        rect.y + rect.height / 2 + 15);        // Y coordinate
            }
        }
    }
    
    private void updateHoverSlot(int x, int y) {
        String oldHoverSlot = hoverSlot;
        hoverSlot = null;
        
        for (Map.Entry<String, Rectangle> entry : slotRectangles.entrySet()) {
            if (entry.getValue().contains(x, y)) {
                hoverSlot = entry.getKey();
                break;
            }
        }
        
        if (!Objects.equals(oldHoverSlot, hoverSlot)) {
            repaint();
        }
    }
    
    private void handleMouseClick(int x, int y) {
        for (Map.Entry<String, Rectangle> entry : slotRectangles.entrySet()) {
            if (entry.getValue().contains(x, y)) {
                selectedSlot = entry.getKey();
                showSlotActionMenu(selectedSlot, x, y);
                repaint();
                break;
            }
        }
    }
    
    private void showSlotActionMenu(String slotNumber, int x, int y) {
        JPopupMenu popupMenu = new JPopupMenu();
        
        // Find the slot
        ParkingSlot selectedParkingSlot = slots.stream()
            .filter(s -> s.getSlotNumber().equals(slotNumber))
            .findFirst()
            .orElse(null);
        
        if (selectedParkingSlot == null) {
            return;
        }
        
        boolean isAvailable = selectedParkingSlot.getAvailability();
        
        // Add menu items
        JMenuItem viewDetailsItem = new JMenuItem("View Details");
        JMenuItem toggleStatusItem = new JMenuItem(isAvailable ? "Mark as Occupied" : "Mark as Available");
        JMenuItem viewReservationsItem = new JMenuItem("View Reservations");
        
        viewDetailsItem.addActionListener(e -> showSlotDetails(slotNumber));
        toggleStatusItem.addActionListener(e -> toggleSlotStatus(slotNumber, isAvailable));
        viewReservationsItem.addActionListener(e -> showSlotReservations(slotNumber));
        
        popupMenu.add(viewDetailsItem);
        popupMenu.add(toggleStatusItem);
        popupMenu.add(viewReservationsItem);
        
        popupMenu.show(this, x, y);
    }
    
    private void showSlotDetails(String slotNumber) {
        try {
            // Get selected slot
            ParkingSlot slot = slots.stream()
                .filter(s -> s.getSlotNumber().equals(slotNumber))
                .findFirst()
                .orElse(null);
            
            if (slot == null) {
                return;
            }
            
            // Get active reservation info if slot is occupied
            Map<String, Object> reservationInfo = null;
            if (!slot.getAvailability()) {
                reservationInfo = controller.getActiveReservationInfoForSlot(slotNumber);
            }
            
            // Show details dialog
            JDialog detailsDialog = new JDialog(parentFrame, "Slot Details", true);
            detailsDialog.setLayout(new BorderLayout());
            
            JPanel detailsPanel = new JPanel();
            detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
            detailsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            
            // Add slot details
            JLabel titleLabel = new JLabel("Slot " + slotNumber + " Details");
            titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
            titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            
            JPanel infoPanel = new JPanel(new GridLayout(0, 2, 10, 5));
            infoPanel.setBorder(BorderFactory.createTitledBorder("Basic Information"));
            
            addDetailRow(infoPanel, "Slot Number:", slotNumber);
            addDetailRow(infoPanel, "Status:", slot.getAvailability() ? "Available" : "Occupied");
            addDetailRow(infoPanel, "Parking ID:", slot.getParkingID());
            
            detailsPanel.add(titleLabel);
            detailsPanel.add(Box.createRigidArea(new Dimension(0, 15)));
            detailsPanel.add(infoPanel);
            
            // Add reservation details if applicable
            if (reservationInfo != null && !reservationInfo.isEmpty()) {
                JPanel reservationPanel = new JPanel(new GridLayout(0, 2, 10, 5));
                reservationPanel.setBorder(BorderFactory.createTitledBorder("Current Reservation"));
                
                for (Map.Entry<String, Object> entry : reservationInfo.entrySet()) {
                    if (entry.getValue() != null) {
                        addDetailRow(reservationPanel, entry.getKey() + ":", entry.getValue().toString());
                    }
                }
                
                detailsPanel.add(Box.createRigidArea(new Dimension(0, 15)));
                detailsPanel.add(reservationPanel);
            }
            
            // Add close button
            JButton closeButton = new JButton("Close");
            closeButton.addActionListener(e -> detailsDialog.dispose());
            
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            buttonPanel.add(closeButton);
            
            detailsPanel.add(Box.createRigidArea(new Dimension(0, 15)));
            detailsPanel.add(buttonPanel);
            
            // Set up dialog
            detailsDialog.add(new JScrollPane(detailsPanel), BorderLayout.CENTER);
            detailsDialog.setSize(400, 500);
            detailsDialog.setLocationRelativeTo(parentFrame);
            detailsDialog.setVisible(true);
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(parentFrame, 
                "Error showing slot details: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void toggleSlotStatus(String slotNumber, boolean currentStatus) {
        try {
            boolean success = controller.updateSlotStatus(slotNumber, !currentStatus);
            
            if (success) {
                // Update local slot list
                for (ParkingSlot slot : slots) {
                    if (slot.getSlotNumber().equals(slotNumber)) {
                        slot.setAvailability(!currentStatus);
                        break;
                    }
                }
                
                repaint();
                
                JOptionPane.showMessageDialog(parentFrame,
                    "Slot status updated successfully.",
                    "Status Updated",
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(parentFrame,
                    "Failed to update slot status.",
                    "Update Failed",
                    JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(parentFrame,
                "Error updating slot status: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void showSlotReservations(String slotNumber) {
        try {
            // Get reservations for this slot
            List<Map<String, Object>> reservations = controller.getAllReservationsForSlot(slotNumber);
            
            if (reservations == null || reservations.isEmpty()) {
                JOptionPane.showMessageDialog(parentFrame,
                    "No reservations found for this slot.",
                    "No Reservations",
                    JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            // Create and show dialog
            JDialog reservationsDialog = new JDialog(parentFrame, "Slot Reservations", true);
            reservationsDialog.setLayout(new BorderLayout());
            
            // Create table for reservations
            String[] columnNames = {"ID", "Start Date", "End Date", "Status", "Vehicle"};
            Object[][] data = new Object[reservations.size()][columnNames.length];
            
            for (int i = 0; i < reservations.size(); i++) {
                Map<String, Object> reservation = reservations.get(i);
                data[i][0] = reservation.get("reservationID");
                data[i][1] = formatDate(reservation.get("startDate")) + " " + 
                             formatTime(reservation.get("startTime"));
                data[i][2] = formatDate(reservation.get("endDate")) + " " + 
                             formatTime(reservation.get("endTime"));
                data[i][3] = reservation.get("status");
                data[i][4] = reservation.get("vehicleID");
            }
            
            JTable reservationsTable = new JTable(data, columnNames);
            reservationsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            reservationsTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
            
            // Add table to a scroll pane
            JScrollPane scrollPane = new JScrollPane(reservationsTable);
            
            // Add title
            JLabel titleLabel = new JLabel("Reservations for Slot " + slotNumber);
            titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
            titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            
            // Add close button
            JButton closeButton = new JButton("Close");
            closeButton.addActionListener(e -> reservationsDialog.dispose());
            
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
            buttonPanel.add(closeButton);
            
            // Add components to dialog
            reservationsDialog.add(titleLabel, BorderLayout.NORTH);
            reservationsDialog.add(scrollPane, BorderLayout.CENTER);
            reservationsDialog.add(buttonPanel, BorderLayout.SOUTH);
            
            // Set up dialog
            reservationsDialog.setSize(600, 400);
            reservationsDialog.setLocationRelativeTo(parentFrame);
            reservationsDialog.setVisible(true);
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(parentFrame,
                "Error retrieving reservations: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // Helper methods
    private void addDetailRow(JPanel panel, String label, String value) {
        JLabel labelComponent = new JLabel(label);
        labelComponent.setFont(new Font("Arial", Font.BOLD, 12));
        
        JLabel valueComponent = new JLabel(value);
        valueComponent.setFont(new Font("Arial", Font.PLAIN, 12));
        
        panel.add(labelComponent);
        panel.add(valueComponent);
    }
    
    private String formatDate(Object date) {
        if (date == null) return "N/A";
        return date.toString();
    }
    
    private String formatTime(Object time) {
        if (time == null) return "N/A";
        return time.toString();
    }
    
    // Public method to refresh data
    public void refresh() {
        loadSlots();
    }
}