package form;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import config.ComponentConfig;

public class CarForm extends javax.swing.JPanel {

    public CarForm() {
        initComponents();
        setOpaque(false);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        carLayeredPanel = new javax.swing.JLayeredPane();
        carBox3 = new components.CarBox();
        carBox2 = new components.CarBox();
        carBox1 = new components.CarBox();
        carBox4 = new components.CarBox();
        carBox5 = new components.CarBox();
        carBox6 = new components.CarBox();
        carBox7 = new components.CarBox();
        carBox8 = new components.CarBox();

        setBackground(new java.awt.Color(51, 51, 51));
        setOpaque(false);

        carLayeredPanel.setBackground(new java.awt.Color(51, 51, 51));
        carLayeredPanel.setLayout(new java.awt.GridLayout(4, 2, 30, 30));
        carLayeredPanel.add(carBox3);
        carLayeredPanel.add(carBox2);
        carLayeredPanel.add(carBox1);
        carLayeredPanel.add(carBox4);
        carLayeredPanel.add(carBox5);
        carLayeredPanel.add(carBox6);
        carLayeredPanel.add(carBox7);
        carLayeredPanel.add(carBox8);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(70, 70, 70)
                .addComponent(carLayeredPanel)
                .addGap(70, 70, 70))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(70, 70, 70)
                .addComponent(carLayeredPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 956, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(78, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

        @Override
    public void paint(Graphics grphcs) {
        Graphics2D g2 = (Graphics2D) grphcs.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
        g2.dispose();
        super.paint(grphcs);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private components.CarBox carBox1;
    private components.CarBox carBox2;
    private components.CarBox carBox3;
    private components.CarBox carBox4;
    private components.CarBox carBox5;
    private components.CarBox carBox6;
    private components.CarBox carBox7;
    private components.CarBox carBox8;
    private javax.swing.JLayeredPane carLayeredPanel;
    // End of variables declaration//GEN-END:variables
}
