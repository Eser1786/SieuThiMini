package GUI;

import javax.swing.*;
import java.awt.*;

public class DonHangPanel extends JPanel {
    public DonHangPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(0xF8F7FF));
        JLabel title = new JLabel("Quản Lý Đơn Hàng", SwingConstants.CENTER);
        title.setFont(new Font("Playfair Display", Font.BOLD, 32));
        add(title, BorderLayout.CENTER);
    }
}