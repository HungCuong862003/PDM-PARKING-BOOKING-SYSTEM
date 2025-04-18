package app;

import ui.events.MenuEvent;
import java.awt.Component;
import javax.swing.JFrame;
import net.miginfocom.swing.MigLayout;
import ui.base.CustomScrollBar;
import ui.config.AppColor;
import ui.config.ComponentConfig;

public class Main extends javax.swing.JFrame {
    public Main() {
        initComponents();
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setBackground(AppColor.MAIN_BG);
        
        CustomScrollBar sb = new CustomScrollBar();
        sb.setForeground(ComponentConfig.SCROLLBAR_FOREGROUND);
        jScrollPane1.setVerticalScrollBar(sb);
        
        body.setLayout(new MigLayout("wrap, fillx, inset 3", "[fill]", "[]0[]"));

        MenuEvent event = new MenuEvent(){
            @Override
            public void selected(int index) {
                switch(index){
                    case 0 -> showForm(carForm); 
                    case 1 -> System.out.println("Reservation");
                    case 2 -> System.out.println("Parking Lots");
                    case 3 -> System.out.println("Card&Payment");
                    case 4 -> System.out.println("Settings");
                    case 5 -> System.out.println("Logout");
                    default -> showForm(carForm);
                }
            }
            
        };
        menu.initMenu(event);
    }
    
    private void showForm(Component component) {
        body.removeAll();
        jScrollPane1.setViewportView(component);
        body.removeAll();
        body.add(jScrollPane1, java.awt.BorderLayout.CENTER);
        body.revalidate();
        body.repaint();
    }


    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        header = new ui.components.Header();
        menu = new ui.components.Menu();
        body = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        carForm = new ui.form.CarForm();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);

        mainPanel.setBackground(new java.awt.Color(21, 21, 21));

        body.setBackground(new java.awt.Color(51, 51, 51));
        body.setOpaque(false);
        body.setLayout(new java.awt.BorderLayout());

        jScrollPane1.setBackground(new java.awt.Color(51, 51, 51));
        jScrollPane1.setBorder(null);
        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane1.setOpaque(false);
        jScrollPane1.setViewportView(carForm);

        body.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(header, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(menu, javax.swing.GroupLayout.PREFERRED_SIZE, 252, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(body, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addComponent(header, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(menu, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(body, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Main().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel body;
    private ui.form.CarForm carForm;
    private ui.components.Header header;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPanel mainPanel;
    private ui.components.Menu menu;
    // End of variables declaration//GEN-END:variables
}
