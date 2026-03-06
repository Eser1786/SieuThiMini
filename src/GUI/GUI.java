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
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                int opt = JOptionPane.showConfirmDialog(
                        GUI.this,
                        "Bạn có muốn thoát ứng dụng không?",
                        "Xác nhận thoát",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE);
                if (opt == JOptionPane.YES_OPTION) System.exit(0);
            }
        });
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
