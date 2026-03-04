package GUI;

import javax.swing.*;
import java.awt.*;

/**
 * Placeholder panel for customer management.  For now it only displays a title.
 * Logic can later be moved here from the original GUI class.
 */
public class KhachHangPanel extends JPanel {
    public KhachHangPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(0xF8F7FF));
        JLabel title = new JLabel("Quản lý Khách hàng", SwingConstants.CENTER);
        title.setFont(new Font("Playfair Display", Font.BOLD, 32));
        add(title, BorderLayout.CENTER);
    }
}