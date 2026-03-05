package GUI.test_SellerPanel;

import javax.swing.*;
import java.awt.*;

public class SidebarPanel extends JPanel {

    public SidebarPanel(){

        setPreferredSize(new Dimension(120,0));
        setBackground(new Color(0xAF9FCB));
        setLayout(new BorderLayout());

        JLabel title = new JLabel("QUẢN LÝ\nBÁN HÀNG",SwingConstants.CENTER);
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI",Font.BOLD,14));

        JButton logout = new JButton("ĐĂNG XUẤT");

        add(title,BorderLayout.NORTH);
        add(logout,BorderLayout.SOUTH);
    }
}