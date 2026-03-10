package GUI;

import BUS.EmployeeBUS;
import BUS.UserSession;
import DTO.EmployeeDTO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Dialog đăng nhập nhỏ
 */
public class LoginDialog extends JDialog {
    // Bộ màu từ MainPanel
    private static final Color BG_COLOR = new Color(0xD1C4E9); // sidebar background
    private static final Color BTN_NORMAL = new Color(0xF8F7FF);
    private static final Color BTN_HOVER = new Color(0x88729B);
    private static final Color HEADER_BG = new Color(0x2F2C35);

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JCheckBox chkShowPassword;
    private JLabel lblMessage;

    private boolean loginSuccess = false;

    public LoginDialog(Frame parent) {
        super(parent, "Đăng Nhập Hệ Thống Siêu Thị", true);
        setSize(400, 300);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        setResizable(false);

        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());

        // Header
        JPanel header = new JPanel(new FlowLayout(FlowLayout.CENTER));
        header.setBackground(HEADER_BG);
        header.setPreferredSize(new Dimension(0, 60));
        JLabel lblTitle = new JLabel("Đăng Nhập");
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        header.add(lblTitle);
        add(header, BorderLayout.NORTH);

        // Center panel
        JPanel center = new JPanel(new GridBagLayout());
        center.setBackground(BG_COLOR);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        gbc.gridx = 0; gbc.gridy = 0;
        center.add(new JLabel("Tên đăng nhập:"), gbc);

        gbc.gridx = 1;
        txtUsername = new JTextField(15);
        center.add(txtUsername, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        center.add(new JLabel("Mật khẩu:"), gbc);

        gbc.gridx = 1;
        txtPassword = new JPasswordField(15);
        center.add(txtPassword, gbc);

        gbc.gridx = 2; gbc.gridy = 1;
        chkShowPassword = new JCheckBox("👁");
        chkShowPassword.setToolTipText("Hiển thị mật khẩu");
        chkShowPassword.addActionListener(e -> {
            if (chkShowPassword.isSelected()) {
                txtPassword.setEchoChar('\0');
            } else {
                txtPassword.setEchoChar('*');
            }
        });
        center.add(chkShowPassword, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 3;
        btnLogin = new JButton("Đăng Nhập");
        btnLogin.setBackground(BTN_NORMAL);
        btnLogin.setForeground(Color.BLACK);
        btnLogin.setFocusPainted(false);
        btnLogin.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        btnLogin.addActionListener(new LoginAction());
        center.add(btnLogin, gbc);

        gbc.gridy = 3; gbc.gridwidth = 3;
        lblMessage = new JLabel("");
        lblMessage.setForeground(Color.RED);
        center.add(lblMessage, gbc);

        add(center, BorderLayout.CENTER);

        // Hover effect cho button
        btnLogin.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnLogin.setBackground(BTN_HOVER);
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnLogin.setBackground(BTN_NORMAL);
            }
        });

        // Enter key để login
        getRootPane().setDefaultButton(btnLogin);
    }

    private class LoginAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String username = txtUsername.getText().trim();
            String password = new String(txtPassword.getPassword());

            if (username.isEmpty() || password.isEmpty()) {
                lblMessage.setText("Vui lòng nhập tên đăng nhập và mật khẩu!");
                return;
            }

            try {
                EmployeeBUS empBUS = new EmployeeBUS();
                EmployeeDTO user = empBUS.login(username, password);

                if (user != null) {
                    UserSession.setCurrentUser(user);
                    loginSuccess = true;
                    dispose(); // Đóng dialog
                } else {
                    lblMessage.setText("Tên đăng nhập hoặc mật khẩu không đúng!");
                    lblMessage.setForeground(Color.RED);
                }
            } catch (Exception ex) {
                lblMessage.setText("Không thể kết nối đến cơ sở dữ liệu!\nChi tiết: " + ex.getMessage());
                lblMessage.setForeground(Color.RED);
            }
        }
    }

    public boolean isLoginSuccess() {
        return loginSuccess;
    }
}