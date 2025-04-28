package main.java.com.parkeasy.controller.admin;

import main.java.com.parkeasy.model.Admin;
import main.java.com.parkeasy.model.ParkingSpace;
import main.java.com.parkeasy.service.AdminService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller class for managing revenue information and statistics.
 * Handles the interaction between the admin revenue interface and the service layer.
 */
public class RevenueController {

    private final AdminService adminService;
    private Admin currentAdmin;

    /**
     * Constructor for RevenueController.
     *
     * @param currentAdmin The currently logged-in admin
     */
    public RevenueController(Admin currentAdmin) throws SQLException {
        this.adminService = new AdminService();
        this.currentAdmin = currentAdmin;
    }

    /**
     * Gets all parking spaces managed by the current admin.
     *
     * @return List of parking spaces managed by the current admin
     */
    public List<ParkingSpace> getAdminParkingSpaces() {
        return adminService.getParkingSpacesByAdmin(currentAdmin.getAdminID());
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
     * Gets daily revenue for all parking spaces managed by the current admin.
     *
     * @param date The date for which to calculate revenue (in format YYYY-MM-DD)
     * @return Map with parking space IDs as keys and daily revenue as values
     */
    public Map<String, BigDecimal> getAllDailyRevenue(String date) {
        List<ParkingSpace> adminSpaces = getAdminParkingSpaces();
        Map<String, BigDecimal> revenueMap = new HashMap<>();

        for (ParkingSpace space : adminSpaces) {
            BigDecimal revenue = adminService.getDailyRevenue(space.getParkingID(), date);
            revenueMap.put(space.getParkingID(), revenue);
        }

        return revenueMap;
    }

    /**
     * Gets monthly revenue for all parking spaces managed by the current admin.
     *
     * @param year The year for which to calculate revenue
     * @param month The month for which to calculate revenue (1-12)
     * @return Map with parking space IDs as keys and monthly revenue as values
     */
    public Map<String, BigDecimal> getMonthlyRevenue(int year, int month) {
        Map<String, BigDecimal> allRevenue = adminService.getMonthlyRevenue(year, month);
        Map<String, BigDecimal> adminRevenue = new HashMap<>();

        // Filter to only include spaces managed by this admin
        List<ParkingSpace> adminSpaces = getAdminParkingSpaces();
        for (ParkingSpace space : adminSpaces) {
            String parkingID = space.getParkingID();
            if (allRevenue.containsKey(parkingID)) {
                adminRevenue.put(parkingID, allRevenue.get(parkingID));
            } else {
                adminRevenue.put(parkingID, BigDecimal.ZERO);
            }
        }

        return adminRevenue;
    }

    /**
     * Gets total revenue statistics for all time for parking spaces managed by the admin.
     *
     * @return Map with parking space IDs as keys and total revenue as values
     */
    public Map<String, BigDecimal> getTotalRevenue() {
        Map<String, BigDecimal> allRevenue = adminService.getTotalRevenue();
        Map<String, BigDecimal> adminRevenue = new HashMap<>();

        // Filter to only include spaces managed by this admin
        List<ParkingSpace> adminSpaces = getAdminParkingSpaces();
        for (ParkingSpace space : adminSpaces) {
            String parkingID = space.getParkingID();
            if (allRevenue.containsKey(parkingID)) {
                adminRevenue.put(parkingID, allRevenue.get(parkingID));
            } else {
                adminRevenue.put(parkingID, BigDecimal.ZERO);
            }
        }

        return adminRevenue;
    }

    /**
     * Gets the total revenue across all parking spaces managed by the admin for a specific day.
     *
     * @param date The date for which to calculate total revenue (in format YYYY-MM-DD)
     * @return The total revenue across all spaces for the specified date
     */
    public BigDecimal getTotalDailyRevenue(String date) {
        Map<String, BigDecimal> dailyRevenue = getAllDailyRevenue(date);

        BigDecimal total = BigDecimal.ZERO;
        for (BigDecimal revenue : dailyRevenue.values()) {
            total = total.add(revenue);
        }

        return total;
    }

    /**
     * Gets the total revenue across all parking spaces managed by the admin for a specific month.
     *
     * @param year The year for which to calculate revenue
     * @param month The month for which to calculate revenue (1-12)
     * @return The total revenue across all spaces for the specified month
     */
    public BigDecimal getTotalMonthlyRevenue(int year, int month) {
        Map<String, BigDecimal> monthlyRevenue = getMonthlyRevenue(year, month);

        BigDecimal total = BigDecimal.ZERO;
        for (BigDecimal revenue : monthlyRevenue.values()) {
            total = total.add(revenue);
        }

        return total;
    }

    /**
     * Gets the total all-time revenue across all parking spaces managed by the admin.
     *
     * @return The total all-time revenue across all spaces
     */
    public BigDecimal getGrandTotalRevenue() {
        Map<String, BigDecimal> totalRevenue = getTotalRevenue();

        BigDecimal total = BigDecimal.ZERO;
        for (BigDecimal revenue : totalRevenue.values()) {
            total = total.add(revenue);
        }

        return total;
    }

    /**
     * Gets daily revenue for the past week for a specific parking space.
     *
     * @param parkingID The ID of the parking space
     * @return Map with dates as keys and daily revenue as values
     */
    public Map<String, BigDecimal> getWeeklyRevenue(String parkingID) {
        // Check if the admin owns this parking space
        ParkingSpace space = adminService.getParkingSpaceById(parkingID);
        if (space == null || space.getAdminID() != currentAdmin.getAdminID()) {
            return new HashMap<>();
        }

        Map<String, BigDecimal> weeklyRevenue = new HashMap<>();
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // Get revenue for the past 7 days
        for (int i = 6; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            String dateStr = date.format(formatter);
            BigDecimal revenue = adminService.getDailyRevenue(parkingID, dateStr);
            weeklyRevenue.put(dateStr, revenue);
        }

        return weeklyRevenue;
    }

    /**
     * Populates a table model with daily revenue data for display in a JTable.
     *
     * @param tableModel The table model to populate
     * @param date The date for which to display revenue (in format YYYY-MM-DD)
     */
    public void populateDailyRevenueTable(DefaultTableModel tableModel, String date) {
        // Clear existing rows
        tableModel.setRowCount(0);

        // Add column headers if needed
        if (tableModel.getColumnCount() == 0) {
            tableModel.addColumn("Parking ID");
            tableModel.addColumn("Address");
            tableModel.addColumn("Revenue");
        }

        // Get all spaces managed by this admin
        List<ParkingSpace> adminSpaces = getAdminParkingSpaces();
        Map<String, BigDecimal> revenueMap = getAllDailyRevenue(date);

        // Add rows for each parking space
        for (ParkingSpace space : adminSpaces) {
            String parkingID = space.getParkingID();
            BigDecimal revenue = revenueMap.getOrDefault(parkingID, BigDecimal.ZERO);

            Object[] row = {
                    parkingID,
                    space.getParkingAddress(),
                    "$" + revenue.toString()
            };
            tableModel.addRow(row);
        }

        // Add a total row
        BigDecimal totalRevenue = getTotalDailyRevenue(date);
        Object[] totalRow = {
                "TOTAL",
                "",
                "$" + totalRevenue.toString()
        };
        tableModel.addRow(totalRow);
    }

    /**
     * Populates a table model with monthly revenue data for display in a JTable.
     *
     * @param tableModel The table model to populate
     * @param year The year for which to display revenue
     * @param month The month for which to display revenue (1-12)
     */
    public void populateMonthlyRevenueTable(DefaultTableModel tableModel, int year, int month) {
        // Clear existing rows
        tableModel.setRowCount(0);

        // Add column headers if needed
        if (tableModel.getColumnCount() == 0) {
            tableModel.addColumn("Parking ID");
            tableModel.addColumn("Address");
            tableModel.addColumn("Revenue");
        }

        // Get all spaces managed by this admin
        List<ParkingSpace> adminSpaces = getAdminParkingSpaces();
        Map<String, BigDecimal> revenueMap = getMonthlyRevenue(year, month);

        // Add rows for each parking space
        for (ParkingSpace space : adminSpaces) {
            String parkingID = space.getParkingID();
            BigDecimal revenue = revenueMap.getOrDefault(parkingID, BigDecimal.ZERO);

            Object[] row = {
                    parkingID,
                    space.getParkingAddress(),
                    "$" + revenue.toString()
            };
            tableModel.addRow(row);
        }

        // Add a total row
        BigDecimal totalRevenue = getTotalMonthlyRevenue(year, month);
        Object[] totalRow = {
                "TOTAL",
                "",
                "$" + totalRevenue.toString()
        };
        tableModel.addRow(totalRow);
    }

    /**
     * Creates data for a revenue chart for the past week.
     *
     * @param parkingID The ID of the parking space
     * @return Two arrays: dates (x-axis) and revenue values (y-axis)
     */
    public Object[] createWeeklyRevenueChartData(String parkingID) {
        Map<String, BigDecimal> weeklyRevenue = getWeeklyRevenue(parkingID);

        // Sort dates chronologically
        List<LocalDate> sortedDates = weeklyRevenue.keySet().stream()
                .map(dateStr -> LocalDate.parse(dateStr))
                .sorted()
                .toList();

        // Create arrays for chart data
        String[] dates = new String[sortedDates.size()];
        double[] revenues = new double[sortedDates.size()];

        DateTimeFormatter displayFormatter = DateTimeFormatter.ofPattern("MM/dd");

        for (int i = 0; i < sortedDates.size(); i++) {
            LocalDate date = sortedDates.get(i);
            String dateStr = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            dates[i] = date.format(displayFormatter);
            revenues[i] = weeklyRevenue.get(dateStr).doubleValue();
        }

        return new Object[] { dates, revenues };
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