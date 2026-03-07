package GUI.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;

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

        // Circular avatar placeholder
        JLabel avatar = new JLabel(makeCircleIcon(90, ACCENT));
        avatar.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Camera icon overlay hint
        JLabel changePhoto = new JLabel("Đổi ảnh đại diện");
        changePhoto.setFont(new Font("Arial", Font.PLAIN, 12));
        changePhoto.setForeground(ACCENT2);
        changePhoto.setCursor(new Cursor(Cursor.HAND_CURSOR));
        changePhoto.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel name = new JLabel("Nguyễn Văn A");
        name.setFont(new Font("Arial", Font.BOLD, 22));
        name.setForeground(VALUE_FG);
        name.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel role = makeRoleBadge("Quản lý");
        role.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(avatar);
        card.add(Box.createVerticalStrut(8));
        card.add(changePhoto);
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

        String[][] fields = {
            { "Họ và tên",              "Nguyễn Văn A"       },
            { "Username / ID nhân viên","NV001"               },
            { "Email",                  "nguyenvana@situ.vn"  },
            { "Số điện thoại",          "0912 345 678"        },
            { "Ngày tạo tài khoản",     "01/01/2026"          },
            { "Vai trò",                "Quản lý"             },
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

        JButton btnPasswd = makeActionBtn("Đổi mật khẩu", ACCENT2);
        btnPasswd.addActionListener(e -> showChangePasswordDialog());

        JButton btnLogout = makeActionBtn("Đăng xuất", new Color(0xB83434));
        btnLogout.addActionListener(e -> {
            int ok = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc muốn đăng xuất?",
                "Đăng xuất", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (ok == JOptionPane.YES_OPTION)
                JOptionPane.showMessageDialog(this, "Đã đăng xuất.");
        });

        card.add(btnPasswd);
        card.add(btnLogout);
        return card;
    }

    // ── Change password dialog ────────────────────────────────────────────
    private void showChangePasswordDialog() {
        Window owner = SwingUtilities.getWindowAncestor(this);
        JDialog dlg = new JDialog(owner, "Đổi mật khẩu", Dialog.ModalityType.APPLICATION_MODAL);
        dlg.setSize(400, 280);
        dlg.setLocationRelativeTo(this);
        dlg.setResizable(false);
        dlg.setLayout(new BorderLayout());

        JPanel hdr = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 14));
        hdr.setBackground(ACCENT2);
        JLabel hl = new JLabel("Đổi mật khẩu");
        hl.setFont(new Font("Arial", Font.BOLD, 17));
        hl.setForeground(Color.WHITE);
        hdr.add(hl);
        dlg.add(hdr, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(PAGE_BG);
        form.setBorder(BorderFactory.createEmptyBorder(16, 28, 8, 28));
        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.HORIZONTAL; g.insets = new Insets(6, 4, 6, 4);
        Font lf = new Font("Arial", Font.BOLD, 13);
        Dimension fd = new Dimension(220, 32);

        String[] rowLabels = { "Mật khẩu hiện tại", "Mật khẩu mới", "Xác nhận mật khẩu mới" };
        JPasswordField[] pfs = new JPasswordField[3];
        for (int i = 0; i < rowLabels.length; i++) {
            g.gridx = 0; g.gridy = i; g.weightx = 0;
            JLabel lbl = new JLabel(rowLabels[i]); lbl.setFont(lf); form.add(lbl, g);
            g.gridx = 1; g.weightx = 1;
            pfs[i] = new JPasswordField(); pfs[i].setPreferredSize(fd); form.add(pfs[i], g);
        }
        dlg.add(form, BorderLayout.CENTER);

        JPanel ft = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 10));
        ft.setBackground(PAGE_BG);
        ft.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, DIVIDER));
        JButton cancel = makeActionBtn("Hủy",  new Color(0x9B8EA8));
        JButton save   = makeActionBtn("Lưu",  ACCENT2);
        cancel.addActionListener(e -> dlg.dispose());
        save.addActionListener(e -> {
            JOptionPane.showMessageDialog(dlg, "Đổi mật khẩu thành công!");
            dlg.dispose();
        });
        ft.add(cancel); ft.add(save);
        dlg.add(ft, BorderLayout.SOUTH);
        dlg.setVisible(true);
    }

    // ── Helpers ───────────────────────────────────────────────────────────
    private JPanel makeCard() {
        JPanel p = new JPanel();
        p.setBackground(CARD_BG);
        p.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(DIVIDER, 1),
            BorderFactory.createEmptyBorder(0, 0, 0, 0)));
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
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

    /** Circular icon placeholder with person silhouette initials */
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


