package GUI;

import javax.swing.*;
import java.awt.*;

public class NhanVienPanel extends JPanel {
    public NhanVienPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(0xF8F7FF));
        JLabel title = new JLabel("Quản lý Nhân viên", SwingConstants.CENTER);
        title.setFont(new Font("Playfair Display", Font.BOLD, 32));
        add(title, BorderLayout.CENTER);
    }
}