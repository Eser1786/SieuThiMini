package GUI;

import javax.swing.*;
import java.awt.*;

/**
 * The main content panel which contains header, navigation and a card layout
 * showing the different feature panels.
 */
public class MainPanel extends JPanel {
    public static final String TRANG_CHU  = "TRANG_CHU";
    public static final String SAN_PHAM   = "SAN_PHAM";
    public static final String KHACH_HANG = "KHACH_HANG";
    public static final String NHAN_VIEN  = "NHAN_VIEN";
    public static final String DON_HANG   = "DON_HANG";
    public static final String KHO        = "KHO";
    public static final String KHUYEN_MAI = "KHUYEN_MAI";
    public static final String USER       = "USER";

    private CardLayout cardLayout;
    private JPanel mainCards;

    public MainPanel() {
        setLayout(new BorderLayout());

        // Header bar
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(0x2F2C35));
        header.setPreferredSize(new Dimension(1440, 60));

        JPanel headerLeft = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 15));
        headerLeft.setBackground(new Color(0x2F2C35));
        JLabel ava = new JLabel("~Khu vực để avata~");
        JLabel tenShop = new JLabel("Tên Sốp");
        tenShop.setForeground(Color.WHITE);
        headerLeft.add(ava);
        headerLeft.add(tenShop);

        JLabel sdt = new JLabel("SDT liên hệ: 0345435108");
        sdt.setForeground(Color.WHITE);
        sdt.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 20));

        header.add(headerLeft, BorderLayout.WEST);
        header.add(sdt, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // Navigation column
        JPanel nav = new JPanel();
        nav.setLayout(new BoxLayout(nav, BoxLayout.Y_AXIS));
        nav.setPreferredSize(new Dimension(180, 700));
        nav.setBackground(new Color(0xD1C4E9));
        nav.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.BLACK,1),
            BorderFactory.createEmptyBorder(20,10,20,10)
        ));

        JButton btnTrangChu   = createStyledButton("Trang chủ");
        JButton btnSanPham    = createStyledButton("Sản phẩm");
        JButton btnKhachHang  = createStyledButton("Khách hàng");
        JButton btnNhanVien   = createStyledButton("Nhân viên");
        JButton btnDonHang    = createStyledButton("Đơn hàng");
        JButton btnKho        = createStyledButton("Kho");
        JButton btnKhuyenMai  = createStyledButton("Khuyến mãi");
        JButton btnUser       = createStyledButton("👤 Tài khoản");

        nav.add(btnTrangChu);
        nav.add(Box.createVerticalStrut(12));
        nav.add(btnSanPham);
        nav.add(Box.createVerticalStrut(12));
        nav.add(btnKhachHang);
        nav.add(Box.createVerticalStrut(12));
        nav.add(btnNhanVien);
        nav.add(Box.createVerticalStrut(12));
        nav.add(btnDonHang);
        nav.add(Box.createVerticalStrut(12));
        nav.add(btnKho);
        nav.add(Box.createVerticalStrut(12));
        nav.add(btnKhuyenMai);
        nav.add(Box.createVerticalGlue());
        nav.add(btnUser);

        add(nav, BorderLayout.WEST);

        // central card panel
        cardLayout = new CardLayout();
        mainCards = new JPanel(cardLayout);
        mainCards.add(new TrangChuPanel(), TRANG_CHU);
        mainCards.add(new SanPhamPanel(), SAN_PHAM);
        mainCards.add(new KhachHangPanel(), KHACH_HANG);
        mainCards.add(new NhanVienPanel(), NHAN_VIEN);
        mainCards.add(new DonHangPanel(), DON_HANG);
        mainCards.add(new KhoPanel(), KHO);
        mainCards.add(new KhuyenMaiPanel(), KHUYEN_MAI);
        mainCards.add(new UserPanel(), USER);
        add(mainCards, BorderLayout.CENTER);

        // button listeners
        btnTrangChu.addActionListener(e -> cardLayout.show(mainCards, TRANG_CHU));
        btnSanPham.addActionListener(e -> cardLayout.show(mainCards, SAN_PHAM));
        btnKhachHang.addActionListener(e -> cardLayout.show(mainCards, KHACH_HANG));
        btnNhanVien.addActionListener(e -> cardLayout.show(mainCards, NHAN_VIEN));
        btnDonHang.addActionListener(e -> cardLayout.show(mainCards, DON_HANG));
        btnKho.addActionListener(e -> cardLayout.show(mainCards, KHO));
        btnKhuyenMai.addActionListener(e -> cardLayout.show(mainCards, KHUYEN_MAI));
        btnUser.addActionListener(e -> cardLayout.show(mainCards, USER));
    }

    // styling helper taken from original GUI class
    private JButton createStyledButton(String text) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int shadowOffset = 3;
                for (int i = shadowOffset; i > 0; i--) {
                    int alpha = (int)(50.0 * (shadowOffset - i) / shadowOffset);
                    g2.setColor(new Color(0, 0, 0, alpha));
                    g2.fillRoundRect(0, i, getWidth(), getHeight()-i, 40, 40);
                }
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth()-shadowOffset, getHeight()-shadowOffset, 40, 40);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setBackground(new Color(0xF8F7FF));
        btn.setFont(new Font("Playfair Display", Font.BOLD, 20));
        btn.setForeground(Color.BLACK);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setPreferredSize(new Dimension(204, 45));
        btn.setMaximumSize(new Dimension(204, 45));
        btn.setMinimumSize(new Dimension(204, 45));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(0x88729B));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(0xF8F7FF));
            }
        });
        return btn;
    }
}