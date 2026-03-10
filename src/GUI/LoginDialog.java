package GUI;

import BUS.EmployeeBUS;
import BUS.UserSession;
import DTO.EmployeeDTO;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

/**
 * Dialog đăng nhập nhỏ
 */
public class LoginDialog extends JDialog {
    // Bộ màu từ MainPanel
    private static final Color BG_COLOR = new Color(0xD1C4E9); // sidebar background
    private static final Color BTN_NORMAL = new Color(0xF8F7FF);
    private static final Color BTN_HOVER = new Color(0x88729B);
    private static final Color BTN_LOGIN_LIGHT = new Color(0x8B7FA8); // light purple for login button
    private static final Color BTN_LOGIN_DARK = new Color(0x5C4A7F);  // dark purple for login button hover
    private static final Color HEADER_BG = new Color(0x2F2C35);

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JCheckBox chkShowPassword;
    private JLabel lblMessage;

    private boolean loginSuccess = false;

    public LoginDialog(Frame parent) {
        super(parent, "Đăng Nhập Hệ Thống Siêu Thị", true);
        setSize(550, 380);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        // when user closes dialog with X before logging in, exit application
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                if (!loginSuccess) {
                    System.exit(0);
                }
            }
        });
        setResizable(false);

        initUI();
    }

    /** Load và scale logo từ img/logo.png, trả null nếu không tìm thấy */
    private ImageIcon loadLogo(int w, int h) {
        try {
            java.io.File f = new java.io.File("img/icons/Logo.png");
            if (!f.exists()) f = new java.io.File("img/logo.png");
            if (!f.exists()) f = new java.io.File("img/logo.jpg");
            if (!f.exists()) return null;
            Image img = new ImageIcon(f.getAbsolutePath()).getImage()
                            .getScaledInstance(w, h, Image.SCALE_SMOOTH);
            return new ImageIcon(img);
        } catch (Exception e) { return null; }
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(new Color(0xF8F7FF));

        // ── Header (logo + tên shop) ──────────────────────────────────────────
        JPanel header = new JPanel(new GridBagLayout());
        header.setBackground(HEADER_BG);
        header.setPreferredSize(new Dimension(0, 100));
        GridBagConstraints hgbc = new GridBagConstraints();
        hgbc.gridx = 0; hgbc.gridy = 0; hgbc.anchor = GridBagConstraints.CENTER;

        ImageIcon logoIcon = loadLogo(60, 60);
        if (logoIcon != null) {
            JLabel logoLbl = new JLabel(logoIcon);
            header.add(logoLbl, hgbc);
        }

        hgbc.gridy = 1; hgbc.insets = new Insets(4, 0, 0, 0);
        JLabel lblTitle = new JLabel("SIÊU THỊ 36");
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 20));
        header.add(lblTitle, hgbc);

        hgbc.gridy = 2; hgbc.insets = new Insets(2, 0, 4, 0);
        JLabel lblSub = new JLabel("Tiện Lợi & Sống Khỏe");
        lblSub.setForeground(new Color(0xCCBBEE));
        lblSub.setFont(new Font("Arial", Font.ITALIC, 12));
        header.add(lblSub, hgbc);

        add(header, BorderLayout.NORTH);

        // ── Center panel ─────────────────────────────────────────────────────
        JPanel center = new JPanel(new GridBagLayout());
        center.setBackground(BG_COLOR);
        center.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(12, 8, 12, 8);
        Font labelFont = new Font("Arial", Font.BOLD, 14);
        Font fieldFont = new Font("Arial", Font.PLAIN, 14);

        // Username row
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        JLabel lbUser = new JLabel("Tên đăng nhập:");
        lbUser.setFont(labelFont);
        center.add(lbUser, gbc);

        gbc.gridx = 1; gbc.gridwidth = 2; gbc.weightx = 1.0;
        txtUsername = new JTextField();
        txtUsername.setFont(fieldFont);
        txtUsername.setPreferredSize(new Dimension(220, 36));
        txtUsername.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0xAA99CC), 1),
            BorderFactory.createEmptyBorder(4, 8, 4, 8)));
        center.add(txtUsername, gbc);

        // Password row
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1; gbc.weightx = 0;
        JLabel lbPass = new JLabel("Mật khẩu:");
        lbPass.setFont(labelFont);
        center.add(lbPass, gbc);

        gbc.gridx = 1; gbc.weightx = 1.0;
        txtPassword = new JPasswordField();
        txtPassword.setFont(fieldFont);
        txtPassword.setPreferredSize(new Dimension(180, 36));
        txtPassword.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0xAA99CC), 1),
            BorderFactory.createEmptyBorder(4, 8, 4, 8)));
        center.add(txtPassword, gbc);

        gbc.gridx = 2; gbc.weightx = 0;
        chkShowPassword = new JCheckBox("👁");
        chkShowPassword.setOpaque(false);
        chkShowPassword.setToolTipText("Hiển thị mật khẩu");
        chkShowPassword.addActionListener(e -> {
            txtPassword.setEchoChar(chkShowPassword.isSelected() ? '\0' : '*');
        });
        center.add(chkShowPassword, gbc);

        // Login button
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 3; gbc.weightx = 1.0;
        gbc.insets = new Insets(16, 8, 12, 8);
        btnLogin = new JButton("Đăng Nhập") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnLogin.setBackground(BTN_LOGIN_LIGHT);
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFont(new Font("Arial", Font.BOLD, 15));
        btnLogin.setFocusPainted(false);
        btnLogin.setContentAreaFilled(false);
        btnLogin.setOpaque(false);
        btnLogin.setBorderPainted(false);
        btnLogin.setPreferredSize(new Dimension(0, 42));
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogin.addActionListener(new LoginAction());
        center.add(btnLogin, gbc);

        // Message label
        gbc.gridy = 3; gbc.insets = new Insets(8, 8, 8, 8);
        lblMessage = new JLabel(" ");
        lblMessage.setFont(new Font("Arial", Font.PLAIN, 13));
        lblMessage.setForeground(Color.RED);
        lblMessage.setHorizontalAlignment(SwingConstants.CENTER);
        center.add(lblMessage, gbc);

        add(center, BorderLayout.CENTER);

        // Hover
        btnLogin.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnLogin.setBackground(BTN_LOGIN_DARK);
            }
            @Override public void mouseExited(java.awt.event.MouseEvent evt) {
                btnLogin.setBackground(BTN_LOGIN_LIGHT);
            }
        });

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