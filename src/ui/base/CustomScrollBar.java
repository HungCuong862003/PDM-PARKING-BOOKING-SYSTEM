package ui.base;

import java.awt.Color;
import java.awt.Dimension;
import javax.swing.JScrollBar;

public class CustomScrollBar extends JScrollBar {

    public CustomScrollBar() {
        setUI(new CustomScrollBarUI());
        setPreferredSize(new Dimension(5, 5));
        setForeground(new Color(0, 0, 0));
        setUnitIncrement(20);
        setOpaque(false);
    }
}