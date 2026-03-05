package GUI.KhuyenMai;

import javax.swing.*;
import java.awt.*;

public class KhuyenMaiPanel extends JPanel {
    public KhuyenMaiPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(0xF8F7FF));
        JLabel title = new JLabel("Quản Lý Khuyến Mãi", SwingConstants.CENTER);
        title.setFont(new Font("Playfair Display", Font.BOLD, 32));
        add(title, BorderLayout.CENTER);
    }
}

