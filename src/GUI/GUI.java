package GUI;

import java.awt.Dimension;
import javax.swing.*;


public class GUI extends JFrame {
    public GUI() {
        this(true);
    }

    public GUI(boolean showLogin) {
        setTitle("Siêu Thị 36");
        
        try {
            java.io.File iconFile = new java.io.File("img/icons/logo (white background).jpg");
            if (!iconFile.exists()) iconFile = new java.io.File("img/icons/Logo.png");
            if (!iconFile.exists()) iconFile = new java.io.File("img/logo.png");
            if (iconFile.exists())
                setIconImage(new javax.swing.ImageIcon(iconFile.getAbsolutePath()).getImage());
        } catch (Exception ignored) {}
        setSize(1000, 700);
        setMinimumSize(new Dimension(800, 600));
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
                
                LoginDialog loginDialog = new LoginDialog(this);
                loginDialog.setVisible(true);

                if (loginDialog.isLoginSuccess()) {
                    
                    MainPanel mainPanel = new MainPanel();
                    add(mainPanel);
                    
                    revalidate();
                    repaint();
                } else {
                    
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
            
            MainPanel mainPanel = new MainPanel();
            add(mainPanel);
            revalidate();
            repaint();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GUI frame = new GUI();
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            frame.setVisible(true);
        });
    }
}


