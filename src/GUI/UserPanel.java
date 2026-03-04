package GUI;

import javax.swing.*;
import java.awt.*;

public class UserPanel extends JPanel {
    public UserPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(0xF8F7FF));
        JLabel title = new JLabel("Người dùng", SwingConstants.CENTER);
        title.setFont(new Font("Playfair Display", Font.BOLD, 32));
        add(title, BorderLayout.CENTER);
    }
}