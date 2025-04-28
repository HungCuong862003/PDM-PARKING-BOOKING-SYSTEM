package main.java.com.parkeasy.controller.admin;

import main.java.com.parkeasy.model.Admin;
import main.java.com.parkeasy.model.ParkingSpace;
import main.java.com.parkeasy.model.ParkingSlot;
import main.java.com.parkeasy.service.AdminService;
import main.java.com.parkeasy.util.DatabaseConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Controller class for managing parking spaces and slots.
 * Handles the interaction between the admin user interface and the service layer.
 */
public class ParkingManagementController {

    private final AdminService adminService;
    private Admin currentAdmin;

    /**
     * Constructor for ParkingManagementController.
     *
     * @param currentAdmin The currently logged-in admin
     */
    public ParkingManagementController(Admin currentAdmin) throws SQLException {
        this.adminService = new AdminService();
        this.currentAdmin = currentAdmin;
    }

    /**
     * Gets all parking spaces for display in the admin interface.
     *
     * @return List of all parking spaces
     */
    public List<ParkingSpace> getAllParkingSpaces() {
        return adminService.getAllParkingSpaces();
    }

    /**
     * Gets parking spaces managed by the current admin.
     *
     * @return List of parking spaces managed by the current admin
     */
    public List<ParkingSpace> getAdminParkingSpaces() {
        return adminService.getParkingSpacesByAdmin(currentAdmin.getAdminID());
    }

    /**
     * Creates a new parking space.
     *
     * @param parkingID Unique identifier for the parking space
     * @param parkingAddress Address or location of the parking space
     * @param costOfParking Cost per hour/day for parking
     * @param numberOfSlots Total number of parking slots
     * @param maxDuration Maximum parking duration in hours
     * @param description Description of the parking space
     * @return true if creation successful, false otherwise
     */
    public boolean createParkingSpace(String parkingID, String parkingAddress,
                                      float costOfParking, int numberOfSlots, int maxDuration, String description) {

        // Validate inputs
        if (parkingID == null || parkingID.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Parking ID cannot be empty", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (parkingAddress == null || parkingAddress.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Address cannot be empty", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (costOfParking < 0) {
            JOptionPane.showMessageDialog(null, "Cost cannot be negative", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (numberOfSlots <= 0) {
            JOptionPane.showMessageDialog(null, "Number of slots must be positive", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (maxDuration <= 0) {
            JOptionPane.showMessageDialog(null, "Maximum duration must be positive", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Create parking space object
        ParkingSpace parkingSpace = new ParkingSpace(
                parkingID,
                parkingAddress,
                costOfParking,
                numberOfSlots,
                maxDuration,
                description,
                currentAdmin.getAdminID()
        );

        // Create the parking space
        boolean spaceCreated = adminService.createParkingSpace(parkingSpace);

        // If space created successfully, create the parking slots
        if (spaceCreated) {
            return adminService.createParkingSlots(parkingID, numberOfSlots);
        }

        return false;
    }

    /**
     * Updates an existing parking space.
     *
     * @param parkingSpace The parking space with updated information
     * @return true if update successful, false otherwise
     */
    public boolean updateParkingSpace(ParkingSpace parkingSpace) {
        // Validate that the admin owns this parking space
        if (parkingSpace.getAdminID() != currentAdmin.getAdminID()) {
            JOptionPane.showMessageDialog(null, "You can only update your own parking spaces",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return adminService.updateParkingSpace(parkingSpace);
    }

    /**
     * Deletes a parking space.
     *
     * @param parkingID The ID of the parking space to delete
     * @return true if deletion successful, false otherwise
     */
    public boolean deleteParkingSpace(String parkingID) {
        // Check if the admin owns this parking space
        ParkingSpace space = adminService.getParkingSpaceById(parkingID);
        if (space == null) {
            JOptionPane.showMessageDialog(null, "Parking space not found", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (space.getAdminID() != currentAdmin.getAdminID()) {
            JOptionPane.showMessageDialog(null, "You can only delete your own parking spaces",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Confirm deletion
        int confirm = JOptionPane.showConfirmDialog(null,
                "Are you sure you want to delete this parking space? This action cannot be undone.",
                "Confirm Deletion", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            return adminService.deleteParkingSpace(parkingID);
        }

        return false;
    }

    /**
     * Updates the pricing for a parking space.
     *
     * @param parkingID The ID of the parking space
     * @param newPrice The new price per hour/day
     * @return true if update successful, false otherwise
     */
    public boolean updateParkingPrice(String parkingID, BigDecimal newPrice) {
        // Check if the admin owns this parking space
        ParkingSpace space = adminService.getParkingSpaceById(parkingID);
        if (space == null) {
            JOptionPane.showMessageDialog(null, "Parking space not found", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (space.getAdminID() != currentAdmin.getAdminID()) {
            JOptionPane.showMessageDialog(null, "You can only update your own parking spaces",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Validate price
        if (newPrice.compareTo(BigDecimal.ZERO) < 0) {
            JOptionPane.showMessageDialog(null, "Price cannot be negative", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return adminService.updateParkingPrice(parkingID, newPrice);
    }

    /**
     * Gets all parking slots for a specific parking space.
     *
     * @param parkingID The ID of the parking space
     * @return List of parking slots in the specified parking space
     */
    public List<ParkingSlot> getParkingSlots(String parkingID) {
        return adminService.getParkingSlotsByParkingID(parkingID);
    }

    /**
     * Updates the availability of a parking slot.
     *
     * @param slotID The ID of the parking slot
     * @param availability The new availability status
     * @return true if update successful, false otherwise
     */
    public boolean updateSlotAvailability(int slotID, boolean availability) {
        // Check if the slot belongs to a parking space owned by the admin
        ParkingSlot slot = adminService.getParkingSlotById(slotID);
        if (slot == null) {
            JOptionPane.showMessageDialog(null, "Parking slot not found", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        ParkingSpace space = adminService.getParkingSpaceById(slot.getParkingID());
        if (space == null || space.getAdminID() != currentAdmin.getAdminID()) {
            JOptionPane.showMessageDialog(null, "You can only update slots in your own parking spaces",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return adminService.updateSlotAvailability(slotID, availability);
    }

    /**
     * Gets occupancy statistics for all parking spaces owned by the admin.
     *
     * @return Map with parking space IDs as keys and occupancy rates as values
     */
    public Map<String, Double> getOccupancyRates() {
        return adminService.getOccupancyRates();
    }

    /**
     * Gets daily revenue for a specific parking space.
     *
     * @param parkingID The ID of the parking space
     * @param date The date for which to calculate revenue (in format YYYY-MM-DD)
     * @return The total revenue for the specified date
     */
    public BigDecimal getDailyRevenue(String parkingID, String date) {
        // Check if the admin owns this parking space
        ParkingSpace space = adminService.getParkingSpaceById(parkingID);
        if (space == null || space.getAdminID() != currentAdmin.getAdminID()) {
            return BigDecimal.ZERO;
        }

        return adminService.getDailyRevenue(parkingID, date);
    }

    /**
     * Gets monthly revenue statistics for all parking spaces owned by the admin.
     *
     * @param year The year for which to calculate revenue
     * @param month The month for which to calculate revenue (1-12)
     * @return Map with parking space IDs as keys and monthly revenue as values
     */
    public Map<String, BigDecimal> getMonthlyRevenue(int year, int month) {
        return adminService.getMonthlyRevenue(year, month);
    }

    /**
     * Populates a table model with parking space data for display in a JTable.
     *
     * @param tableModel The table model to populate
     * @param parkingSpaces The list of parking spaces
     */
    public void populateParkingSpacesTable(DefaultTableModel tableModel, List<ParkingSpace> parkingSpaces) {
        // Clear existing rows
        tableModel.setRowCount(0);

        // Add column headers if needed
        if (tableModel.getColumnCount() == 0) {
            tableModel.addColumn("ID");
            tableModel.addColumn("Address");
            tableModel.addColumn("Cost");
            tableModel.addColumn("Slots");
            tableModel.addColumn("Max Duration");
            tableModel.addColumn("Description");
        }

        // Add rows for each parking space
        for (ParkingSpace space : parkingSpaces) {
            Object[] row = {
                    space.getParkingID(),
                    space.getParkingAddress(),
                    space.getCostOfParking(),
                    space.getNumberOfSlots(),
                    space.getMaxDuration() + " hrs",
                    space.getDescription()
            };
            tableModel.addRow(row);
        }
    }

    /**
     * Populates a table model with parking slot data for display in a JTable.
     *
     * @param tableModel The table model to populate
     * @param parkingSlots The list of parking slots
     */
    public void populateParkingSlotsTable(DefaultTableModel tableModel, List<ParkingSlot> parkingSlots) {
        // Clear existing rows
        tableModel.setRowCount(0);

        // Add column headers if needed
        if (tableModel.getColumnCount() == 0) {
            tableModel.addColumn("Slot ID");
            tableModel.addColumn("Slot Number");
            tableModel.addColumn("Status");
        }

        // Add rows for each parking slot
        for (ParkingSlot slot : parkingSlots) {
            Object[] row = {
                    slot.getSlotID(),
                    slot.getSlotNumber(),
                    slot.isAvailability() ? "Available" : "Occupied"
            };
            tableModel.addRow(row);
        }
    }

    /**
     * Populates a form with parking space data for editing.
     *
     * @param parkingID The ID of the parking space to edit
     * @param addressField The text field for the address
     * @param costField The text field for the cost
     * @param slotsField The text field for the number of slots
     * @param durationField The text field for the maximum duration
     * @param descriptionArea The text area for the description
     * @return true if parking space was found and form populated, false otherwise
     */
    public boolean populateEditForm(String parkingID, JTextField addressField, JTextField costField,
                                    JTextField slotsField, JTextField durationField, JTextArea descriptionArea) {

        ParkingSpace space = adminService.getParkingSpaceById(parkingID);
        if (space == null) {
            return false;
        }

        // Check if the admin owns this parking space
        if (space.getAdminID() != currentAdmin.getAdminID()) {
            JOptionPane.showMessageDialog(null, "You can only edit your own parking spaces",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Populate form fields
        addressField.setText(space.getParkingAddress());
        costField.setText(space.getCostOfParking().toString());
        slotsField.setText(String.valueOf(space.getNumberOfSlots()));
        durationField.setText(String.valueOf(space.getMaxDuration()));
        descriptionArea.setText(space.getDescription());

        return true;
    }

    /**
     * Sets the current admin user.
     *
     * @param admin The new admin user
     */
    public void setCurrentAdmin(Admin admin) {
        this.currentAdmin = admin;
    }
}