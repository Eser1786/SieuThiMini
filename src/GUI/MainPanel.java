package GUI;

import GUI.DonHang.DonHangPanel;
import GUI.KhachHang.KhachHangPanel;
import GUI.Kho.KhoPanel;
import GUI.KhuyenMai.KhuyenMaiPanel;
import GUI.NhanVien.NhanVienPanel;
import GUI.SanPham.SanPhamPanel;
import GUI.TrangChu.TrangChuPanel;
import GUI.User.UserPanel;
import javax.swing.*;
import java.awt.*;

/**
 * Panel chính chứa header, sidebar nav và CardLayout để hiển thị các panel con.
 */
public class MainPanel extends JPanel {
    public static final String TRANG_CHU = "TRANG_CHU";
    public static final String SAN_PHAM = "SAN_PHAM";
    public static final String KHACH_HANG = "KHACH_HANG";
    public static final String NHAN_VIEN = "NHAN_VIEN";
    public static final String DON_HANG = "DON_HANG";
    public static final String KHO = "KHO";
    public static final String KHUYEN_MAI = "KHUYEN_MAI";
    public static final String USER = "USER";

    // Màu nút sidebar
    private static final Color CLR_NORMAL = new Color(0xF8F7FF);
    private static final Color CLR_HOVER = new Color(0x88729B);
    private static final Color CLR_ACTIVE = new Color(0x5C4A7F); // highlight tab đang mở

    private CardLayout cardLayout;
    private JPanel mainCards;

    // Theo dõi nút đang active để bỏ highlight khi chuyển tab
    private JButton activeBtn = null;

    public MainPanel() {
        setLayout(new BorderLayout());

        // ── Header ──────────────────────────────────
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(0x2F2C35));
        header.setPreferredSize(new Dimension(0, 60));

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

        // ── Sidebar nav ─────────────────────────────
        JPanel nav = new JPanel();
        nav.setLayout(new BoxLayout(nav, BoxLayout.Y_AXIS));
        nav.setPreferredSize(new Dimension(180, 0));
        nav.setBackground(new Color(0xD1C4E9));
        nav.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLACK, 1),
                BorderFactory.createEmptyBorder(20, 10, 20, 10)));

        JButton btnTrangChu = createNavButton("Trang chủ");
        JButton btnSanPham = createNavButton("Sản phẩm");
        JButton btnKhachHang = createNavButton("Khách hàng");
        JButton btnNhanVien = createNavButton("Nhân viên");
        JButton btnDonHang = createNavButton("Đơn hàng");
        JButton btnKho = createNavButton("Kho");
        JButton btnKhuyenMai = createNavButton("Khuyến mãi");
        JButton btnUser = createNavButton("Tai khoan"); // tránh emoji

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

        // ── Card Panel ──────────────────────────────
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

        // ── Listeners với highlight sidebar ─────────
        btnTrangChu.addActionListener(e -> navigate(mainCards, TRANG_CHU, btnTrangChu));
        btnSanPham.addActionListener(e -> navigate(mainCards, SAN_PHAM, btnSanPham));
        btnKhachHang.addActionListener(e -> navigate(mainCards, KHACH_HANG, btnKhachHang));
        btnNhanVien.addActionListener(e -> navigate(mainCards, NHAN_VIEN, btnNhanVien));
        btnDonHang.addActionListener(e -> navigate(mainCards, DON_HANG, btnDonHang));
        btnKho.addActionListener(e -> navigate(mainCards, KHO, btnKho));
        btnKhuyenMai.addActionListener(e -> navigate(mainCards, KHUYEN_MAI, btnKhuyenMai));
        btnUser.addActionListener(e -> navigate(mainCards, USER, btnUser));

        // Highlight Trang chủ mặc định khi mở app
        setActive(btnTrangChu);
    }

    /** Chuyển card và highlight nút sidebar tương ứng */
    private void navigate(JPanel cards, String card, JButton btn) {
        cardLayout.show(cards, card);
        setActive(btn);
    }

    /** Đặt nút active — bỏ highlight nút cũ, set màu CLR_ACTIVE cho nút mới */
    private void setActive(JButton btn) {
        if (activeBtn != null) {
            activeBtn.setBackground(CLR_NORMAL);
            activeBtn.setForeground(Color.BLACK);
        }
        activeBtn = btn;
        btn.setBackground(CLR_ACTIVE);
        btn.setForeground(Color.WHITE);
    }

    /** Tạo nút sidebar với bo góc, hover effect */
    private JButton createNavButton(String text) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // shadow nhẹ
                int shadow = 3;
                for (int i = shadow; i > 0; i--) {
                    int alpha = (int) (50.0 * (shadow - i) / shadow);
                    g2.setColor(new Color(0, 0, 0, alpha));
                    g2.fillRoundRect(0, i, getWidth(), getHeight() - i, 40, 40);
                }
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth() - shadow, getHeight() - shadow, 40, 40);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setBackground(CLR_NORMAL);
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
            public void mouseEntered(java.awt.event.MouseEvent e) {
                // Chỉ hover nếu không phải nút đang active
                if (btn != activeBtn)
                    btn.setBackground(CLR_HOVER);
            }

            public void mouseExited(java.awt.event.MouseEvent e) {
                if (btn != activeBtn)
                    btn.setBackground(CLR_NORMAL);
            }
        });
        return btn;
    }
}
