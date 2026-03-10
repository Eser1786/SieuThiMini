package GUI;

import BUS.EmployeeBUS;
import BUS.UserSession;
import DTO.EmployeeDTO;
import DTO.RoleDTO;
import GUI.DonHang.DonHangPanel;
import GUI.KhachHang.KhachHangPanel;
import GUI.Kho.KhoPanel;
import GUI.KhuyenMai.KhuyenMaiPanel;
import GUI.NhapKho.NhapKhoPanel;
import GUI.NhanVien.NhanVienPanel;
import GUI.SanPham.SanPhamPanel;
import GUI.TrangChu.TrangChuPanel;
import GUI.User.UserPanel;
import java.awt.*;
import javax.swing.*;

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
    public static final String NHAP_XUAT = "NHAP_XUAT";
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
    private JButton btnKhachHang;
    private KhachHangPanel khachHangPanel;
    private JButton btnSanPham;
    private JButton btnNhanVien;
    private JButton btnDonHang;
    private JButton btnNhapXuat;
    private JButton btnKhuyenMai;

    public MainPanel() {
        setLayout(new BorderLayout());

        // ── Header ──────────────────────────────────
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(0x2F2C35));
        header.setPreferredSize(new Dimension(0, 60));

        JPanel headerLeft = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        headerLeft.setBackground(new Color(0x2F2C35));

        // Logo
        try {
            java.io.File logoFile = new java.io.File("img/icons/logo transparent.png");
            if (!logoFile.exists()) logoFile = new java.io.File("img/icons/Logo.png");
            if (!logoFile.exists()) logoFile = new java.io.File("img/logo.png");
            if (!logoFile.exists()) logoFile = new java.io.File("img/logo.jpg");
            if (logoFile.exists()) {
                Image logoImg = new ImageIcon(logoFile.getAbsolutePath()).getImage()
                        .getScaledInstance(44, 44, Image.SCALE_SMOOTH);
                headerLeft.add(new JLabel(new ImageIcon(logoImg)));
            }
        } catch (Exception ignored) {}

        JPanel shopNamePanel = new JPanel(new GridLayout(2, 1, 0, 0));
        shopNamePanel.setOpaque(false);
        JLabel tenShop = new JLabel("SIÊU THỊ 36");
        tenShop.setForeground(Color.WHITE);
        tenShop.setFont(new Font("Arial", Font.BOLD, 16));
        JLabel slogan = new JLabel("Tiện Lợi & Sống Khỏe");
        slogan.setForeground(new Color(0xCCBBEE));
        slogan.setFont(new Font("Arial", Font.ITALIC, 11));
        shopNamePanel.add(tenShop);
        shopNamePanel.add(slogan);
        headerLeft.add(shopNamePanel);

        JLabel sdt = new JLabel("SDT liên hệ: 0345435108");
        sdt.setForeground(Color.WHITE);
        sdt.setFont(new Font("Arial", Font.PLAIN, 13));
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
        nav.add(btnTrangChu);
        nav.add(Box.createVerticalStrut(12));

        // Lấy role của user hiện tại
        EmployeeDTO user = UserSession.getCurrentUser();
        String roleName = "ADMIN"; // default
        if (user != null) {
            try {
                EmployeeBUS empBUS = new EmployeeBUS();
                RoleDTO role = empBUS.getRole((long) user.getId());
                if (role != null) {
                    roleName = role.getName();
                }
            } catch (Exception e) {
                // Nếu không lấy được role, dùng default
                e.printStackTrace();
            }
        }

        // thêm sidebar vào layout
        add(nav, BorderLayout.WEST);


        // Prepare card container
        cardLayout = new CardLayout();
        mainCards = new JPanel(cardLayout);

        // Add panels và nút dựa trên role
        // ADMIN: tất cả
        // MANAGER: TrangChu, SanPham, NhanVien
        // CASHIER: TrangChu, DonHang
        // WAREHOUSE: TrangChu, Kho, NhapKho
        // SUPPORT: TrangChu, KhachHang
        mainCards.add(new TrangChuPanel(), TRANG_CHU); // Trang chủ luôn có

        boolean isAdmin     = "ADMIN".equals(roleName);
        boolean isManager   = "MANAGER".equals(roleName);
        boolean isCashier   = "CASHIER".equals(roleName);
        boolean isWarehouse = "WAREHOUSE".equals(roleName);
        boolean isSupport   = "SUPPORT".equals(roleName);

        // Sản phẩm: ADMIN, MANAGER
        if (isAdmin || isManager) {
            mainCards.add(new SanPhamPanel(), SAN_PHAM);
            btnSanPham = createNavButton("Sản phẩm");
            btnSanPham.addActionListener(e -> navigate(mainCards, SAN_PHAM, btnSanPham));
            nav.add(btnSanPham);
            nav.add(Box.createVerticalStrut(12));
        }

        // Khách hàng: ADMIN, SUPPORT
        if (isAdmin || isSupport) {
            khachHangPanel = new KhachHangPanel();
            mainCards.add(khachHangPanel, KHACH_HANG);
            btnKhachHang = createNavButton("Khách hàng");
            btnKhachHang.addActionListener(e -> navigate(mainCards, KHACH_HANG, btnKhachHang));
            nav.add(btnKhachHang);
            nav.add(Box.createVerticalStrut(12));
        }

        // Nhân viên: ADMIN, MANAGER
        if (isAdmin || isManager) {
            mainCards.add(new NhanVienPanel(), NHAN_VIEN);
            btnNhanVien = createNavButton("Nhân viên");
            btnNhanVien.addActionListener(e -> navigate(mainCards, NHAN_VIEN, btnNhanVien));
            nav.add(btnNhanVien);
            nav.add(Box.createVerticalStrut(12));
        }

        // Đơn hàng: ADMIN, CASHIER
        if (isAdmin || isCashier) {
            mainCards.add(new DonHangPanel(), DON_HANG);
            btnDonHang = createNavButton("Đơn hàng");
            btnDonHang.addActionListener(e -> navigate(mainCards, DON_HANG, btnDonHang));
            nav.add(btnDonHang);
            nav.add(Box.createVerticalStrut(12));
        }

        // Kho + Nhập kho: ADMIN, WAREHOUSE
        if (isAdmin || isWarehouse) {
            mainCards.add(new KhoPanel(), KHO);
            JButton btnKho = createNavButton("Kho");
            btnKho.addActionListener(e -> navigate(mainCards, KHO, btnKho));
            nav.add(btnKho);
            nav.add(Box.createVerticalStrut(12));

            mainCards.add(new NhapKhoPanel(), NHAP_XUAT);
            btnNhapXuat = createNavButton("Nhập kho");
            btnNhapXuat.addActionListener(e -> navigate(mainCards, NHAP_XUAT, btnNhapXuat));
            nav.add(btnNhapXuat);
            nav.add(Box.createVerticalStrut(12));
        }

        // Khuyến mãi: ADMIN
        if (isAdmin) {
            mainCards.add(new KhuyenMaiPanel(), KHUYEN_MAI);
            btnKhuyenMai = createNavButton("Khuyến mãi");
            btnKhuyenMai.addActionListener(e -> navigate(mainCards, KHUYEN_MAI, btnKhuyenMai));
            nav.add(btnKhuyenMai);
            nav.add(Box.createVerticalStrut(12));
        }

        // User panel luôn có
        mainCards.add(new UserPanel(), USER);
        JButton btnUser = createNavButton("👤 Tài khoản");
        btnUser.addActionListener(e -> navigate(mainCards, USER, btnUser));
        nav.add(Box.createVerticalGlue());
        nav.add(btnUser);
        add(mainCards, BorderLayout.CENTER);

        // ── Listeners với highlight sidebar ─────────
        btnTrangChu.addActionListener(e -> navigate(mainCards, TRANG_CHU, btnTrangChu));

        // Highlight Trang chủ mặc định khi mở app
        setActive(btnTrangChu);
    }

    /** Chuyển sang tab Khách hàng và mở popup thêm khách hàng */
    public void navigateToKhachHangAndAdd() {
        if (btnKhachHang == null || khachHangPanel == null) return;
        navigate(mainCards, KHACH_HANG, btnKhachHang);
        // Delay nhỏ để CardLayout render xong rồi mới mở popup
        SwingUtilities.invokeLater(() -> khachHangPanel.triggerAddCustomer());
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

    /** Navigate to KhachHang tab and open the "Thêm" (create-new) card */
    public void showKhachHangCreate() {
        cardLayout.show(mainCards, KHACH_HANG);
        setActive(btnKhachHang);
        for (java.awt.Component c : mainCards.getComponents()) {
            if (c instanceof KhachHangPanel kh) {
                kh.showCard(KhachHangPanel.CARD_THEM);
                break;
            }
        }
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Siêu Thị Mini - Quản Lý");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1200, 800);
            frame.setLocationRelativeTo(null);
            frame.setContentPane(new MainPanel());
            frame.setVisible(true);
        });
    }
}
