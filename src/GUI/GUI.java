package GUI;

import java.awt.Dimension;
import javax.swing.*;

/**
 * Top-level frame. Delegates everything to MainPanel.
 */
public class GUI extends JFrame {
    public GUI() {
        this(true);
    }

    public GUI(boolean showLogin) {
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

        if (showLogin) {
            try {
                // Hiển thị dialog đăng nhập
                LoginDialog loginDialog = new LoginDialog(this);
                loginDialog.setVisible(true);

                if (loginDialog.isLoginSuccess()) {
                    // Đăng nhập thành công, hiển thị MainPanel
                    MainPanel mainPanel = new MainPanel();
                    add(mainPanel);
                    // đảm bảo layout/hiển thị được cập nhật
                    revalidate();
                    repaint();
                } else {
                    // Đăng nhập thất bại, thoát app
                    System.exit(0);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                        "Không thể kết nối đến cơ sở dữ liệu MySQL (Cổng 3307)!\n" +
                                "Vui lòng bật XAMPP MySQL hoặc Docker Container trước khi chạy App.\n\n" +
                                "Chi tiết lỗi: " + e.getMessage(),
                        "Lỗi Kết Nối CSDL", JOptionPane.ERROR_MESSAGE);
                System.exit(0);
            }
        } else {
            // Đăng nhập đã thành công, hiển thị MainPanel trực tiếp
            MainPanel mainPanel = new MainPanel();
            add(mainPanel);
            revalidate();
            repaint();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GUI frame = new GUI();
            frame.setVisible(true);
        });
    }
}
