package GUI;

import javax.swing.*;
import java.awt.Dimension;

/**
 * Top-level frame. Delegates everything to MainPanel.
 */
public class GUI extends JFrame {
    public GUI() {
        setTitle("Siu Thị 36");
        setSize(1440, 1024);
        setMinimumSize(new Dimension(1000, 700));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        try {
            add(new MainPanel());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Không thể kết nối đến cơ sở dữ liệu MySQL (Cổng 3307)!\n" +
                            "Vui lòng bật XAMPP MySQL hoặc Docker Container trước khi chạy App.\n\n" +
                            "Chi tiết lỗi: " + e.getMessage(),
                    "Lỗi Kết Nối CSDL", JOptionPane.ERROR_MESSAGE);
            // Vẫn add màn hình rỗng hoặc có thể thoát luôn.
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GUI frame = new GUI();
            frame.setVisible(true);
        });
    }
}
