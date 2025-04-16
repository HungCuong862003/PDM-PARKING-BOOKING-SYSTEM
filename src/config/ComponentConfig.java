package config;

import java.awt.Color;
import java.awt.Font;
import javax.swing.border.EmptyBorder;

public abstract class ComponentConfig {
    public static final Color BADGE_BUTTON_COLOR = new Color(220, 67, 67); // red
    public static final Color BADGE_BUTTON_FOREGROUND = Color.WHITE;
    public static final EmptyBorder BADGE_BUTTON_BORDER = new EmptyBorder(10, 11, 10, 11);
    public static final int RADIUS = 15;
    
    public static final Color RIPPLE_BUTTON_EFFECT_COLOR = new Color(173, 173, 173); // gray
    public static final Color RIPPLE_BUTTON_BACKGROUND = new Color(43, 44, 75); // blue
    public static final Color RIPPLE_BUTTON_FOREGROUND = new Color(219,217,217); // light gray
    public static final Font RIPPLE_BUTTON_FONT = new Font("Helvetica", Font.BOLD, 17);
    public static final EmptyBorder RIPPLE_BUTTON_BORDER = new EmptyBorder(8, 10, 8, 10);
    public static final int RIPPLE_BUTTON_RADIUS = 10;
    
    public static final Color SCROLLBAR_FOREGROUND = new Color(130, 130, 130, 100);
    
    public static final int ICON_SIZE = 30;
}
