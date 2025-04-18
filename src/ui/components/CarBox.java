package ui.components;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class CarBox extends javax.swing.JPanel {

    DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyy");
    DateFormat timeFormat = new SimpleDateFormat("HH:mm");

    private Color color1;
    private Color color2;
    int width = getWidth();
    int height = getHeight();
    
    public CarBox() {
        initComponents();
        setOpaque(false);
        color1 = Color.BLACK;
        color2 = new Color(173, 173, 173);
    }

        
    public Color getColor1() {
        return color1;
    }

    public void setColor1(Color color1) {
        this.color1 = color1;
    }

    public Color getColor2() {
        return color2;
    }

    public void setColor2(Color color2) {
        this.color2 = color2;
    }
    
    public void setData(Model_Box data){
        vehicleID.setText(Integer.toString(data.getVehicleID()));
        status.setText(data.getReservationStatus());
        time.setText(dateFormat.format(data.getReservationStartDate()));
//        startTimeValue.setText(timeFormat.format(data.getReservationStartTime()));
//        endDateValue.setText(dateFormat.format(data.getReservationEndDate()));
//        endTimeValue.setText(dateFormat.format(data.getReservationEndTime()));
//        startDateValue.setText(dateFormat.format(data.getReservationEndDate()));
        price.setText(Float.toString(data.getPricePerHour()));
        parkingLocation.setText(data.getParkingLotLocation());
        slotID.setText(Integer.toString(data.getSlotID()));
    }
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel4 = new javax.swing.JLabel();
        icon = new javax.swing.JLabel();
        vehicleID = new javax.swing.JLabel();
        status = new javax.swing.JLabel();
        parkingLocation = new javax.swing.JLabel();
        slotID = new javax.swing.JLabel();
        price = new javax.swing.JLabel();
        time = new javax.swing.JLabel();

        jLabel4.setText("jLabel4");

        icon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/car.png"))); // NOI18N

        vehicleID.setFont(new java.awt.Font("Helvetica", 1, 20)); // NOI18N
        vehicleID.setForeground(new java.awt.Color(204, 204, 204));
        vehicleID.setText("VehicleID");

        status.setFont(new java.awt.Font("Helvetica", 1, 15)); // NOI18N
        status.setForeground(new java.awt.Color(204, 204, 204));
        status.setText("Status");

        parkingLocation.setFont(new java.awt.Font("Helvetica", 1, 15)); // NOI18N
        parkingLocation.setForeground(new java.awt.Color(204, 204, 204));
        parkingLocation.setText("ParkingLocation");

        slotID.setFont(new java.awt.Font("Helvetica", 1, 15)); // NOI18N
        slotID.setForeground(new java.awt.Color(204, 204, 204));
        slotID.setText("SlotID");

        price.setFont(new java.awt.Font("Helvetica", 1, 15)); // NOI18N
        price.setForeground(new java.awt.Color(204, 204, 204));
        price.setText("Price");

        time.setFont(new java.awt.Font("Helvetica", 1, 15)); // NOI18N
        time.setForeground(new java.awt.Color(204, 204, 204));
        time.setText("Time");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(icon)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(vehicleID)
                    .addComponent(status)
                    .addComponent(parkingLocation)
                    .addComponent(slotID)
                    .addComponent(price)
                    .addComponent(time))
                .addContainerGap(199, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap(35, Short.MAX_VALUE)
                        .addComponent(vehicleID)
                        .addGap(18, 18, 18)
                        .addComponent(status)
                        .addGap(18, 18, 18))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(15, 15, 15)
                        .addComponent(icon)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addComponent(parkingLocation)
                .addGap(18, 18, 18)
                .addComponent(slotID)
                .addGap(18, 18, 18)
                .addComponent(price)
                .addGap(18, 18, 18)
                .addComponent(time)
                .addGap(24, 24, 24))
        );
    }// </editor-fold>//GEN-END:initComponents

        @Override
    protected void paintComponent(Graphics grphcs) {
        Graphics2D g2 = (Graphics2D) grphcs;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        GradientPaint g = new GradientPaint(0, 0, color1, 0, getHeight(), color2);
        g2.setPaint(g);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
        g2.setColor(new Color(255, 255, 255, 50));
        g2.fillOval(getWidth() - (getHeight() / 2), 10, getHeight(), getHeight());
        g2.fillOval(getWidth() - (getHeight() / 2) - 20, getHeight() / 2 + 20, getHeight(), getHeight());
        super.paintComponent(grphcs);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel icon;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel parkingLocation;
    private javax.swing.JLabel price;
    private javax.swing.JLabel slotID;
    private javax.swing.JLabel status;
    private javax.swing.JLabel time;
    private javax.swing.JLabel vehicleID;
    // End of variables declaration//GEN-END:variables
}
