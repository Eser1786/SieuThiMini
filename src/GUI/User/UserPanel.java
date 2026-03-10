package GUI.User;

import BUS.EmployeeBUS;
import BUS.UserSession;
import DTO.EmployeeDTO;
import DTO.RoleDTO;
import GUI.GUI;
import GUI.LoginDialog;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.time.format.DateTimeFormatter;
import javax.imageio.ImageIO;
import javax.swing.*;

public class UserPanel extends JPanel {

    private static final Color PAGE_BG   = new Color(0xF8F7FF);
    private static final Color CARD_BG   = Color.WHITE;
    private static final Color ACCENT    = new Color(0xAF9FCB);
    private static final Color ACCENT2   = new Color(0x6677C8);
    private static final Color LABEL_FG  = new Color(0x555555);
    private static final Color VALUE_FG  = new Color(0x222222);
    private static final Color DIVIDER   = new Color(0xE8E6F5);
    private static final Font  LABEL_F   = new Font("Arial", Font.BOLD, 13);
    private static final Font  VALUE_F   = new Font("Arial", Font.PLAIN, 14);

    public UserPanel() {
        setLayout(new BorderLayout());
        setBackground(PAGE_BG);

        // ── Header bar ────────────────────────────────────────────────────
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 14));
        header.setBackground(new Color(0x2C2C3A));
        JLabel hTitle = new JLabel("| TÀI KHOẢN");
        hTitle.setFont(new Font("Arial", Font.BOLD, 17));
        hTitle.setForeground(Color.WHITE);
        header.add(hTitle);
        add(header, BorderLayout.NORTH);

        // ── Scrollable centre ─────────────────────────────────────────────
        JPanel centre = new JPanel();
        centre.setLayout(new BoxLayout(centre, BoxLayout.Y_AXIS));
        centre.setBackground(PAGE_BG);
        centre.setBorder(BorderFactory.createEmptyBorder(28, 16, 28, 16));

        // Avatar + name card
        JPanel avatarCard = buildAvatarCard();
        avatarCard.setAlignmentX(Component.CENTER_ALIGNMENT);
        centre.add(avatarCard);
        centre.add(Box.createVerticalStrut(20));

        // Info card
        JPanel infoCard = buildInfoCard();
        infoCard.setAlignmentX(Component.CENTER_ALIGNMENT);
        centre.add(infoCard);
        centre.add(Box.createVerticalStrut(20));

        // Action buttons card
        JPanel actionsCard = buildActionsCard();
        actionsCard.setAlignmentX(Component.CENTER_ALIGNMENT);
        centre.add(actionsCard);
        centre.add(Box.createVerticalStrut(12));

        JScrollPane scroll = new JScrollPane(centre);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.setBackground(PAGE_BG);
        scroll.getViewport().setBackground(PAGE_BG);
        add(scroll, BorderLayout.CENTER);
    }

    // ── Avatar + display name ─────────────────────────────────────────────
    private JPanel buildAvatarCard() {
        JPanel card = makeCard();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(24, 32, 20, 32));

        EmployeeDTO user = UserSession.getCurrentUser();

        // Try to load real employee photo: DB path first, then file-system scan
        JLabel avatar;
        if (user != null) {
            String dbPath = user.getPhotoPath();
            BufferedImage photo = null;
            if (dbPath != null && !dbPath.isEmpty()) {
                try { photo = ImageIO.read(new File(dbPath)); } catch (Exception ignored) {}
            }
            if (photo == null) photo = loadEmployeePhoto(user.getCode());
            if (photo != null) {
                BufferedImage circled = makeCircularImage(photo, 90);
                avatar = new JLabel(new ImageIcon(circled));
            } else {
                avatar = new JLabel(makeCircleIcon(90, ACCENT));
            }
        } else {
            avatar = new JLabel(makeCircleIcon(90, ACCENT));
        }
        avatar.setAlignmentX(Component.CENTER_ALIGNMENT);

        String displayName = user != null ? user.getFullName() : "Chưa đăng nhập";
        JLabel name = new JLabel(displayName);
        name.setFont(new Font("Arial", Font.BOLD, 22));
        name.setForeground(VALUE_FG);
        name.setAlignmentX(Component.CENTER_ALIGNMENT);

        String roleName = "Không xác định";
        if (user != null) {
            EmployeeBUS empBUS = new EmployeeBUS();
            RoleDTO role = empBUS.getRole((long) user.getId());
            if (role != null) roleName = role.getName();
        }
        JLabel role = makeRoleBadge(roleName);
        role.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(avatar);
        card.add(Box.createVerticalStrut(10));
        card.add(name);
        card.add(Box.createVerticalStrut(6));
        card.add(role);
        return card;
    }

    // ── Info fields ───────────────────────────────────────────────────────
    private JPanel buildInfoCard() {
        JPanel card = makeCard();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(20, 32, 20, 32));

        addSectionTitle(card, "Thông tin cá nhân");
        card.add(Box.createVerticalStrut(10));

        EmployeeDTO user = UserSession.getCurrentUser();
        String fullName = user != null ? user.getFullName() : "N/A";
        String username = user != null ? user.getUsername() : "N/A";
        String email = user != null ? user.getEmail() : "N/A";
        String phone = user != null ? user.getPhone() : "N/A";
        String hireDate = "N/A";
        if (user != null && user.getHireDate() != null) {
            try {
                hireDate = user.getHireDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            } catch (Exception ignored) {
                hireDate = user.getHireDate().toString();
            }
        }
        String roleName = "N/A";
        if (user != null) {
            EmployeeBUS empBUS = new EmployeeBUS();
            RoleDTO role = empBUS.getRole((long) user.getId());
            if (role != null) {
                roleName = role.getName();
            }
        }

        String[][] fields = {
            { "Họ và tên",              fullName       },
            { "Username / ID nhân viên", username       },
            { "Email",                  email           },
            { "Số điện thoại",          phone           },
            { "Ngày tạo tài khoản",     hireDate        },
            { "Vai trò",                roleName        },
        };

        for (int i = 0; i < fields.length; i++) {
            card.add(buildRow(fields[i][0], fields[i][1]));
            if (i < fields.length - 1) card.add(makeDivider());
        }
        return card;
    }

    // ── Action buttons ────────────────────────────────────────────────────
    private JPanel buildActionsCard() {
        JPanel card = makeCard();
        card.setLayout(new FlowLayout(FlowLayout.CENTER, 16, 16));

        JButton btnLogout = makeActionBtn("Đăng xuất", new Color(0xB83434));
        btnLogout.addActionListener(e -> {
            int ok = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc muốn đăng xuất?",
                "Đăng xuất", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (ok == JOptionPane.YES_OPTION) {
                // Thực hiện logout
                UserSession.logout();
                // Đóng frame hiện tại
                JFrame mainFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
                mainFrame.dispose();
                // Hiển thị login dialog
                LoginDialog loginDialog = new LoginDialog(null);
                loginDialog.setVisible(true);
                if (loginDialog.isLoginSuccess()) {
                    // Tạo frame mới với MainPanel
                    JFrame newFrame = new GUI(false);
                    newFrame.setVisible(true);
                }
            }
        });

        card.add(btnLogout);
        return card;
    }

    // ── Helpers ───────────────────────────────────────────────────────────
    private JPanel makeCard() {
        JPanel p = new JPanel();
        p.setBackground(CARD_BG);
        p.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(DIVIDER, 1),
            BorderFactory.createEmptyBorder(0, 0, 0, 0)));
        p.setMaximumSize(new Dimension(640, Integer.MAX_VALUE));
        return p;
    }

    private JPanel buildRow(String label, String value) {
        JPanel row = new JPanel(new BorderLayout(12, 0));
        row.setOpaque(false);
        row.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));

        JLabel lbl = new JLabel(label);
        lbl.setFont(LABEL_F);
        lbl.setForeground(LABEL_FG);
        lbl.setPreferredSize(new Dimension(190, 20));

        JLabel val = new JLabel(value);
        val.setFont(VALUE_F);
        val.setForeground(VALUE_FG);

        row.add(lbl, BorderLayout.WEST);
        row.add(val, BorderLayout.CENTER);
        return row;
    }

    private JSeparator makeDivider() {
        JSeparator sep = new JSeparator();
        sep.setForeground(DIVIDER);
        sep.setBackground(DIVIDER);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        return sep;
    }

    private void addSectionTitle(JPanel parent, String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Arial", Font.BOLD, 15));
        lbl.setForeground(ACCENT);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        parent.add(lbl);
    }

    private JLabel makeRoleBadge(String role) {
        JLabel badge = new JLabel(role) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0xEDE8F7));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                super.paintComponent(g);
                g2.dispose();
            }
            @Override public Dimension getPreferredSize() {
                Dimension d = super.getPreferredSize();
                return new Dimension(d.width, d.height + 2);
            }
        };
        badge.setFont(new Font("Arial", Font.BOLD, 12));
        badge.setForeground(ACCENT2);
        badge.setOpaque(false);
        badge.setBorder(BorderFactory.createEmptyBorder(5, 14, 5, 14));
        return badge;
    }

    private JButton makeActionBtn(String text, Color bg) {
        JButton b = new JButton(text);
        b.setFont(new Font("Arial", Font.BOLD, 14));
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setOpaque(true);
        b.setBorder(BorderFactory.createEmptyBorder(10, 28, 10, 28));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { b.setBackground(bg.darker()); }
            public void mouseExited(MouseEvent e)  { b.setBackground(bg); }
        });
        return b;
    }

    /** Try to load employee photo from img/employees/<code>.<ext> */
    private BufferedImage loadEmployeePhoto(String employeeCode) {
        if (employeeCode == null || employeeCode.isEmpty()) return null;
        String[] exts = {".jpg", ".jpeg", ".png"};
        for (String ext : exts) {
            File f = new File("img/employees/" + employeeCode + ext);
            if (f.exists()) {
                try { return ImageIO.read(f); } catch (Exception ignored) {}
            }
        }
        return null;
    }

    /** Crop a BufferedImage into a circular shape at given size */
    private BufferedImage makeCircularImage(BufferedImage src, int size) {
        BufferedImage out = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = out.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setClip(new Ellipse2D.Float(0, 0, size, size));
        g2.drawImage(src, 0, 0, size, size, null);
        g2.dispose();
        return out;
    }

    /** Circular icon placeholder with person silhouette */
    private ImageIcon makeCircleIcon(int size, Color bg) {
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setClip(new Ellipse2D.Float(0, 0, size, size));
        g2.setColor(bg);
        g2.fillOval(0, 0, size, size);
        // head
        g2.setColor(new Color(255, 255, 255, 200));
        int hw = size / 3, hh = size / 3;
        g2.fillOval((size - hw) / 2, size / 8, hw, hh);
        // body
        int bw = (int)(size * 0.55), bh = (int)(size * 0.4);
        g2.fillOval((size - bw) / 2, (int)(size * 0.52), bw, bh);
        g2.dispose();
        return new ImageIcon(img);
    }
}


