package main.java.com.parkeasy.controller.admin;

import main.java.com.parkeasy.model.Admin;
import main.java.com.parkeasy.model.ParkingSpace;
import main.java.com.parkeasy.model.Reservation;
import main.java.com.parkeasy.model.User;
import main.java.com.parkeasy.service.AdminService;
import main.java.com.parkeasy.service.ReservationService;
import main.java.com.parkeasy.service.UserService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller class for generating various reports for the admin.
 * Handles the interaction between the admin report interface and the service
 * layer.
 */
public class ReportController {

    private final AdminService adminService;
    private final ReservationService reservationService;
    private final UserService userService;
    private Admin currentAdmin;

    /**
     * Constructor for ReportController.
     *
     * @param currentAdmin The currently logged-in admin
     */
    public ReportController(Admin currentAdmin) throws SQLException {
        this.adminService = new AdminService();
        this.reservationService = new ReservationService();
        this.userService = new UserService();
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
     * Generates a daily revenue report for a specific date.
     *
     * @param date The date for which to generate the report (in format YYYY-MM-DD)
     * @return Map with parking space IDs as keys and daily revenue as values
     */
    public Map<String, BigDecimal> generateDailyRevenueReport(String date) {
        List<ParkingSpace> adminSpaces = getAdminParkingSpaces();
        Map<String, BigDecimal> revenueMap = new HashMap<>();

        for (ParkingSpace space : adminSpaces) {
            BigDecimal revenue = adminService.getDailyRevenue(space.getParkingID(), date);
            revenueMap.put(space.getParkingID(), revenue);
        }

        return revenueMap;
    }

    /**
     * Generates a monthly revenue report for a specific month.
     *
     * @param year  The year for which to generate the report
     * @param month The month for which to generate the report (1-12)
     * @return Map with parking space IDs as keys and monthly revenue as values
     */
    public Map<String, BigDecimal> generateMonthlyRevenueReport(int year, int month) {
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
     * Generates an occupancy report for all parking spaces managed by the admin.
     *
     * @return Map with parking space IDs as keys and occupancy rates as values
     */
    public Map<String, Double> generateOccupancyReport() {
        Map<String, Double> allOccupancy = adminService.getOccupancyRates();
        Map<String, Double> adminOccupancy = new HashMap<>();

        // Filter to only include spaces managed by this admin
        List<ParkingSpace> adminSpaces = getAdminParkingSpaces();
        for (ParkingSpace space : adminSpaces) {
            String parkingID = space.getParkingID();
            if (allOccupancy.containsKey(parkingID)) {
                adminOccupancy.put(parkingID, allOccupancy.get(parkingID));
            } else {
                adminOccupancy.put(parkingID, 0.0);
            }
        }

        return adminOccupancy;
    }

    /**
     * Generates a customer activity report, showing the most active customers.
     *
     * @param limit Maximum number of customers to include in the report
     * @return List of maps containing customer data and activity statistics
     */
    public List<Map<String, Object>> generateCustomerActivityReport(int limit) {
        CustomerManagementController customerController;
        try {
            customerController = new CustomerManagementController(currentAdmin);
        } catch (SQLException e) {
            throw new RuntimeException("Error initializing CustomerManagementController", e);
        }
        return customerController.getTopCustomers(limit);
    }

    /**
     * Generates a reservation report for a specific date range.
     *
     * @param startDate Start date in format YYYY-MM-DD
     * @param endDate   End date in format YYYY-MM-DD
     * @return List of reservations in the specified date range
     */
    public List<Reservation> generateReservationReport(String startDate, String endDate) {
        List<ParkingSpace> adminSpaces = getAdminParkingSpaces();
        return reservationService.getReservationsByDateRange(adminSpaces, startDate, endDate);
    }

    /**
     * Exports a daily revenue report to a CSV file.
     *
     * @param date     The date for which to generate the report (in format
     *                 YYYY-MM-DD)
     * @param filePath The path where the CSV file should be saved
     * @return true if export successful, false otherwise
     */
    public boolean exportDailyRevenueReportToCSV(String date, String filePath) {
        try (FileWriter writer = new FileWriter(filePath)) {
            // Write CSV header
            writer.write("Parking ID,Address,Revenue\n");

            // Get revenue data
            Map<String, BigDecimal> revenueData = generateDailyRevenueReport(date);
            List<ParkingSpace> spaces = getAdminParkingSpaces();

            // Write data rows
            BigDecimal totalRevenue = BigDecimal.ZERO;
            for (ParkingSpace space : spaces) {
                String parkingID = space.getParkingID();
                String address = space.getParkingAddress();
                BigDecimal revenue = revenueData.getOrDefault(parkingID, BigDecimal.ZERO);

                writer.write(parkingID + "," +
                        escapeCSV(address) + "," +
                        revenue.toString() + "\n");

                totalRevenue = totalRevenue.add(revenue);
            }

            // Write total row
            writer.write("TOTAL,," + totalRevenue.toString() + "\n");

            return true;
        } catch (IOException e) {
            System.err.println("Error exporting daily revenue report: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Exports a monthly revenue report to a CSV file.
     *
     * @param year     The year for which to generate the report
     * @param month    The month for which to generate the report (1-12)
     * @param filePath The path where the CSV file should be saved
     * @return true if export successful, false otherwise
     */
    public boolean exportMonthlyRevenueReportToCSV(int year, int month, String filePath) {
        try (FileWriter writer = new FileWriter(filePath)) {
            // Write CSV header
            writer.write("Parking ID,Address,Revenue\n");

            // Get revenue data
            Map<String, BigDecimal> revenueData = generateMonthlyRevenueReport(year, month);
            List<ParkingSpace> spaces = getAdminParkingSpaces();

            // Write data rows
            BigDecimal totalRevenue = BigDecimal.ZERO;
            for (ParkingSpace space : spaces) {
                String parkingID = space.getParkingID();
                String address = space.getParkingAddress();
                BigDecimal revenue = revenueData.getOrDefault(parkingID, BigDecimal.ZERO);

                writer.write(parkingID + "," +
                        escapeCSV(address) + "," +
                        revenue.toString() + "\n");

                totalRevenue = totalRevenue.add(revenue);
            }

            // Write total row
            writer.write("TOTAL,," + totalRevenue.toString() + "\n");

            return true;
        } catch (IOException e) {
            System.err.println("Error exporting monthly revenue report: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Exports an occupancy report to a CSV file.
     *
     * @param filePath The path where the CSV file should be saved
     * @return true if export successful, false otherwise
     */
    public boolean exportOccupancyReportToCSV(String filePath) {
        try (FileWriter writer = new FileWriter(filePath)) {
            // Write CSV header
            writer.write("Parking ID,Address,Total Slots,Occupied Slots,Occupancy Rate\n");

            // Get occupancy data
            Map<String, Double> occupancyData = generateOccupancyReport();
            List<ParkingSpace> spaces = getAdminParkingSpaces();

            // Write data rows
            for (ParkingSpace space : spaces) {
                String parkingID = space.getParkingID();
                String address = space.getParkingAddress();
                int totalSlots = space.getNumberOfSlots();
                double occupancyRate = occupancyData.getOrDefault(parkingID, 0.0);
                int occupiedSlots = (int) Math.round(totalSlots * occupancyRate / 100);

                writer.write(parkingID + "," +
                        escapeCSV(address) + "," +
                        totalSlots + "," +
                        occupiedSlots + "," +
                        String.format("%.2f%%", occupancyRate) + "\n");
            }

            return true;
        } catch (IOException e) {
            System.err.println("Error exporting occupancy report: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Exports a reservation report to a CSV file.
     *
     * @param startDate Start date in format YYYY-MM-DD
     * @param endDate   End date in format YYYY-MM-DD
     * @param filePath  The path where the CSV file should be saved
     * @return true if export successful, false otherwise
     */
    public boolean exportReservationReportToCSV(String startDate, String endDate, String filePath) {
        try (FileWriter writer = new FileWriter(filePath)) {
            // Write CSV header
            writer.write(
                    "Reservation ID,Parking Space,Start Date,Start Time,End Date,End Time,Status,Vehicle ID,User Name\n");

            // Get reservation data
            List<Reservation> reservations = generateReservationReport(startDate, endDate);

            // Cache parking spaces for faster lookup
            List<ParkingSpace> spaces = getAdminParkingSpaces();
            Map<String, String> spaceAddresses = new HashMap<>();
            for (ParkingSpace space : spaces) {
                spaceAddresses.put(space.getParkingID(), space.getParkingAddress());
            }

            // Write data rows
            for (Reservation reservation : reservations) {
                int reservationID = reservation.getReservationID();
                String parkingID = reservationService.getParkingIDFromSlot(reservation.getSlotID());
                String parkingAddress = spaceAddresses.getOrDefault(parkingID, parkingID);
                String startDateVal = String.valueOf(reservation.getStartDate());
                String startTime = String.valueOf(reservation.getStartTime());
                String endDateVal = String.valueOf(reservation.getEndDate());
                String endTime = String.valueOf(reservation.getEndTime());
                String status = reservation.getStatus();
                String vehicleID = reservation.getVehicleID();

                // Get user name
                User user = userService.getUserById(reservation.getUserID());
                String userName = (user != null) ? user.getUserName() : "Unknown";

                writer.write(reservationID + "," +
                        escapeCSV(parkingAddress) + "," +
                        startDateVal + "," +
                        startTime + "," +
                        endDateVal + "," +
                        endTime + "," +
                        status + "," +
                        vehicleID + "," +
                        escapeCSV(userName) + "\n");
            }

            return true;
        } catch (IOException e) {
            System.err.println("Error exporting reservation report: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Populates a table model with daily revenue data for display in a JTable.
     *
     * @param tableModel The table model to populate
     * @param date       The date for which to display revenue (in format
     *                   YYYY-MM-DD)
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

        // Get revenue data
        Map<String, BigDecimal> revenueData = generateDailyRevenueReport(date);
        List<ParkingSpace> spaces = getAdminParkingSpaces();

        // Add rows for each parking space
        BigDecimal totalRevenue = BigDecimal.ZERO;
        for (ParkingSpace space : spaces) {
            String parkingID = space.getParkingID();
            String address = space.getParkingAddress();
            BigDecimal revenue = revenueData.getOrDefault(parkingID, BigDecimal.ZERO);

            Object[] row = {
                    parkingID,
                    address,
                    "$" + revenue.toString()
            };
            tableModel.addRow(row);

            totalRevenue = totalRevenue.add(revenue);
        }

        // Add a total row
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
     * @param year       The year for which to display revenue
     * @param month      The month for which to display revenue (1-12)
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

        // Get revenue data
        Map<String, BigDecimal> revenueData = generateMonthlyRevenueReport(year, month);
        List<ParkingSpace> spaces = getAdminParkingSpaces();

        // Add rows for each parking space
        BigDecimal totalRevenue = BigDecimal.ZERO;
        for (ParkingSpace space : spaces) {
            String parkingID = space.getParkingID();
            String address = space.getParkingAddress();
            BigDecimal revenue = revenueData.getOrDefault(parkingID, BigDecimal.ZERO);

            Object[] row = {
                    parkingID,
                    address,
                    "$" + revenue.toString()
            };
            tableModel.addRow(row);

            totalRevenue = totalRevenue.add(revenue);
        }

        // Add a total row
        Object[] totalRow = {
                "TOTAL",
                "",
                "$" + totalRevenue.toString()
        };
        tableModel.addRow(totalRow);
    }

    /**
     * Populates a table model with occupancy data for display in a JTable.
     *
     * @param tableModel The table model to populate
     */
    public void populateOccupancyTable(DefaultTableModel tableModel) {
        // Clear existing rows
        tableModel.setRowCount(0);

        // Add column headers if needed
        if (tableModel.getColumnCount() == 0) {
            tableModel.addColumn("Parking ID");
            tableModel.addColumn("Address");
            tableModel.addColumn("Total Slots");
            tableModel.addColumn("Occupied Slots");
            tableModel.addColumn("Occupancy Rate");
        }

        // Get occupancy data
        Map<String, Double> occupancyData = generateOccupancyReport();
        List<ParkingSpace> spaces = getAdminParkingSpaces();

        // Add rows for each parking space
        int totalSlots = 0;
        int totalOccupied = 0;

        for (ParkingSpace space : spaces) {
            String parkingID = space.getParkingID();
            String address = space.getParkingAddress();
            int slotsCount = space.getNumberOfSlots();
            double occupancyRate = occupancyData.getOrDefault(parkingID, 0.0);
            int occupiedSlots = (int) Math.round(slotsCount * occupancyRate / 100);

            Object[] row = {
                    parkingID,
                    address,
                    slotsCount,
                    occupiedSlots,
                    String.format("%.2f%%", occupancyRate)
            };
            tableModel.addRow(row);

            totalSlots += slotsCount;
            totalOccupied += occupiedSlots;
        }

        // Calculate overall occupancy rate
        double overallRate = totalSlots > 0 ? (double) totalOccupied / totalSlots * 100 : 0.0;

        // Add a total row
        Object[] totalRow = {
                "TOTAL",
                "",
                totalSlots,
                totalOccupied,
                String.format("%.2f%%", overallRate)
        };
        tableModel.addRow(totalRow);
    }

    /**
     * Populates a table model with reservation data for display in a JTable.
     *
     * @param tableModel The table model to populate
     * @param startDate  Start date in format YYYY-MM-DD
     * @param endDate    End date in format YYYY-MM-DD
     */
    public void populateReservationTable(DefaultTableModel tableModel, String startDate, String endDate) {
        // Clear existing rows
        tableModel.setRowCount(0);

        // Add column headers if needed
        if (tableModel.getColumnCount() == 0) {
            tableModel.addColumn("Reservation ID");
            tableModel.addColumn("Parking Space");
            tableModel.addColumn("Start Date/Time");
            tableModel.addColumn("End Date/Time");
            tableModel.addColumn("Status");
            tableModel.addColumn("Vehicle ID");
            tableModel.addColumn("User Name");
        }

        // Get reservation data
        List<Reservation> reservations = generateReservationReport(startDate, endDate);

        // Cache parking spaces for faster lookup
        List<ParkingSpace> spaces = getAdminParkingSpaces();
        Map<String, String> spaceAddresses = new HashMap<>();
        for (ParkingSpace space : spaces) {
            spaceAddresses.put(space.getParkingID(), space.getParkingAddress());
        }

        // Add rows for each reservation
        for (Reservation reservation : reservations) {
            int reservationID = reservation.getReservationID();
            String parkingID = reservationService.getParkingIDFromSlot(reservation.getSlotID());
            String parkingAddress = spaceAddresses.getOrDefault(parkingID, parkingID);
            String startDateTime = reservation.getStartDate() + " " + reservation.getStartTime();
            String endDateTime = reservation.getEndDate() + " " + reservation.getEndTime();
            String status = reservation.getStatus();
            String vehicleID = reservation.getVehicleID();

            // Get user name
            User user = userService.getUserById(reservation.getUserID());
            String userName = (user != null) ? user.getUserName() : "Unknown";

            Object[] row = {
                    reservationID,
                    parkingAddress,
                    startDateTime,
                    endDateTime,
                    status,
                    vehicleID,
                    userName
            };
            tableModel.addRow(row);
        }
    }

    /**
     * Creates a filename with a timestamp for reports.
     *
     * @param prefix    The prefix for the filename
     * @param extension The file extension (e.g., "csv")
     * @return A filename with timestamp
     */
    public String createReportFilename(String prefix, String extension) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
        String timestamp = LocalDateTime.now().format(formatter);
        return prefix + "_" + timestamp + "." + extension;
    }

    /**
     * Escapes special characters in a string for CSV format.
     *
     * @param value The string to escape
     * @return The escaped string
     */
    private String escapeCSV(String value) {
        if (value == null) {
            return "";
        }

        // If the value contains a comma, double quote, or newline, enclose it in double
        // quotes
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            // Replace double quotes with two double quotes
            String escapedValue = value.replace("\"", "\"\"");
            return "\"" + escapedValue + "\"";
        }

        return value;
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