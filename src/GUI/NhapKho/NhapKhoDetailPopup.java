package GUI.NhapKho;

import BUS.PurchaseInvoicesBUS;
import DTO.PurchaseInvoiceItemsDTO;
import DTO.PurchaseInvoicesDTO;
import DTO.enums.PurchaseInvoicesEnum.PurchaseInvoicesStatus;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;

/** Popup xem chi ti\u1ebft phi\u1ebfu nh\u1eadp kho. */
class NhapKhoDetailPopup extends JDialog {

    private static final DateTimeFormatter FMT       = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final Color             CLR_ACCENT = new Color(0x5C4A7F);
    private static final Color             CLR_HDR    = new Color(0xAF9FCB);
    private static final Color             CLR_WHITE  = Color.WHITE;
    private static final Color             CLR_PAGE   = new Color(0xF0EFF8);

    private final PurchaseInvoicesDTO invoice;
    private final NhapKhoPanel       parentPanel;

    NhapKhoDetailPopup(Window owner, PurchaseInvoicesDTO invoice, NhapKhoPanel parentPanel) {
        super(owner, "Chi ti\u1ebft phi\u1ebfu nh\u1eadp kho", ModalityType.APPLICATION_MODAL);
        this.invoice     = invoice;
        this.parentPanel = parentPanel;
        setSize(920, 660);
        setMinimumSize(new Dimension(700, 500));
        setLocationRelativeTo(owner);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setContentPane(buildContent());
    }

    private JPanel buildContent() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(CLR_PAGE);

        // ── Header ────────────────────────────────────────────────────────────
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(CLR_PAGE);
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0xCCCCCC)),
                BorderFactory.createEmptyBorder(14, 20, 14, 20)));

        JLabel titleLbl = new JLabel("Phi\u1ebfu nh\u1eadp kho: " +
                (invoice.getInvoiceCode() != null ? invoice.getInvoiceCode() : ""));
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLbl.setForeground(CLR_ACCENT);
        header.add(titleLbl, BorderLayout.WEST);

        // Status badge
        boolean isPending = invoice.getStatus() == null || "PENDING".equals(invoice.getStatus());
        JLabel statusLbl = new JLabel(mapStatus(invoice.getStatus()));
        statusLbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        statusLbl.setOpaque(true);
        statusLbl.setBorder(BorderFactory.createEmptyBorder(4, 14, 4, 14));
        statusLbl.setBackground(isPending ? new Color(0xFFF3E0) : new Color(0xE8F5E9));
        statusLbl.setForeground(isPending ? new Color(0xE65100) : new Color(0x2E7D32));
        header.add(statusLbl, BorderLayout.EAST);

        root.add(header, BorderLayout.NORTH);

        // ── Body ──────────────────────────────────────────────────────────────
        JPanel body = new JPanel(new BorderLayout(0, 12));
        body.setBackground(CLR_PAGE);
        body.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));

        // Info grid (4 columns: key-value pairs)
        JPanel infoPanel = new JPanel(new GridLayout(0, 4, 16, 8));
        infoPanel.setBackground(CLR_WHITE);
        infoPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xDDDDDD)),
                BorderFactory.createEmptyBorder(12, 16, 12, 16)));
        addInfo(infoPanel, "M\u00e3 phi\u1ebfu:",        nvl(invoice.getInvoiceCode()));
        addInfo(infoPanel, "Ng\u00e0y nh\u1eadp:",       invoice.getDateIn() != null ? invoice.getDateIn().format(FMT) : "");
        addInfo(infoPanel, "Nh\u00e0 cung c\u1ea5p:",    nvl(invoice.getSupplierName()));
        addInfo(infoPanel, "Nh\u00e2n vi\u00ean:",       nvl(invoice.getEmployeeName()));
        addInfo(infoPanel, "PT thanh to\u00e1n:",        nvl(invoice.getPaymentMethod()));
        addInfo(infoPanel, "TT thanh to\u00e1n:",        mapPayment(invoice.getPaymentStatus()));
        addInfo(infoPanel, "Ghi ch\u00fa:",              nvl(invoice.getNotes()));
        addInfo(infoPanel, "Tr\u1ea1ng th\u00e1i:",      mapStatus(invoice.getStatus()));
        body.add(infoPanel, BorderLayout.NORTH);

        // Items table
        String[] cols = {"M\u00e3 SP", "T\u00ean s\u1ea3n ph\u1ea9m", "S\u1ed1 l\u01b0\u1ee3ng", "\u0110\u01a1n gi\u00e1 (\u0111)", "Th\u00e0nh ti\u1ec1n (\u0111)"};
        DefaultTableModel tm = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        if (invoice.getItems() != null) {
            for (PurchaseInvoiceItemsDTO item : invoice.getItems()) {
                tm.addRow(new Object[]{
                        nvl(item.getProductCode()),
                        nvl(item.getProductName()),
                        item.getQuantity(),
                        item.getUnitPrice() != null ? String.format("%,.0f", item.getUnitPrice()) : "0",
                        item.getSubtotal()  != null ? String.format("%,.0f", item.getSubtotal())  : "0"
                });
            }
        }
        JTable tbl = new JTable(tm);
        tbl.setRowHeight(36);
        tbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tbl.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tbl.getTableHeader().setBackground(CLR_HDR);
        tbl.getTableHeader().setForeground(CLR_WHITE);
        tbl.getTableHeader().setPreferredSize(new Dimension(0, 38));
        tbl.setShowHorizontalLines(true);
        tbl.setShowVerticalLines(false);
        tbl.setIntercellSpacing(new Dimension(0, 0));

        JLabel lblTotal = new JLabel("T\u1ed5ng ti\u1ec1n: " +
                (invoice.getTotalAmount() != null ? String.format("%,.0f\u0111", invoice.getTotalAmount()) : "0\u0111"));
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTotal.setForeground(CLR_ACCENT);
        lblTotal.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        lblTotal.setHorizontalAlignment(SwingConstants.RIGHT);

        JPanel tableCard = new JPanel(new BorderLayout());
        tableCard.setBackground(CLR_WHITE);
        tableCard.setBorder(BorderFactory.createLineBorder(new Color(0xDDDDDD)));
        tableCard.add(new JScrollPane(tbl), BorderLayout.CENTER);
        tableCard.add(lblTotal, BorderLayout.SOUTH);
        body.add(tableCard, BorderLayout.CENTER);

        root.add(body, BorderLayout.CENTER);

        // ── Footer ────────────────────────────────────────────────────────────
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 12));
        footer.setBackground(CLR_PAGE);
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(0xCCCCCC)));

        JButton btnClose = makeBtn("\u0110\u00f3ng", new Color(0x607D8B));
        btnClose.addActionListener(e -> dispose());

        // Show Sua + Xac nhan when invoice is PENDING (or null/unknown = treat as PENDING)
        boolean canEdit = invoice.getStatus() == null 
        || invoice.getStatus() == PurchaseInvoicesStatus.PENDING;

        JButton btnEdit = makeBtn("S\u1eeda phi\u1ebfu", new Color(0xD9D9D9));
        btnEdit.setForeground(new Color(0x333333));
        btnEdit.addActionListener(e -> {
            Window owner = NhapKhoDetailPopup.this.getOwner();
            dispose();
            parentPanel.openEditPopup(invoice, owner);
        });
        btnEdit.setVisible(canEdit);

        JButton btnConfirm = makeBtn("X\u00e1c nh\u1eadn nh\u1eadp kho", new Color(0x388E3C));
        btnConfirm.addActionListener(e -> handleConfirm());
        btnConfirm.setVisible(canEdit);
        JButton btnCancel = makeBtn("Hủy phiếu", new Color(0xE53935));
                btnCancel.addActionListener(e -> handleCancel());
                btnCancel.setVisible(canEdit);
                JButton btnPayment = makeBtn("Xác nhận thanh toán", new Color(0x1976D2));
btnPayment.addActionListener(e -> handleConfirmPayment());
btnPayment.setVisible(
        invoice.getStatus() == PurchaseInvoicesStatus.PENDING
        && !"PAID".equals(invoice.getPaymentStatus())
);
        footer.add(btnEdit);
        footer.add(btnConfirm);
        footer.add(btnPayment);
        footer.add(btnCancel);
        footer.add(btnClose);

        root.add(footer, BorderLayout.SOUTH);
        return root;
    }

    private void handleConfirm() {
        int choice = JOptionPane.showConfirmDialog(this,
                "X\u00e1c nh\u1eadn nh\u1eadp kho?\nS\u1ed1 l\u01b0\u1ee3ng s\u1ea3n ph\u1ea9m s\u1ebd \u0111\u01b0\u1ee3c c\u1ed9ng v\u00e0o t\u1ed3n kho.",
                "X\u00e1c nh\u1eadn", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (choice != JOptionPane.YES_OPTION) return;
        try {
            boolean ok = new PurchaseInvoicesBUS().confirmPurchaseInvoice(invoice.getInvoiceId());
            if (ok) {
                JOptionPane.showMessageDialog(this,
                        "\u0110\u00e3 x\u00e1c nh\u1eadn nh\u1eadp kho th\u00e0nh c\u00f4ng! T\u1ed3n kho \u0111\u00e3 \u0111\u01b0\u1ee3c c\u1eadp nh\u1eadt.",
                        "Th\u00e0nh c\u00f4ng", JOptionPane.INFORMATION_MESSAGE);
                dispose();
                parentPanel.showTable();
            } else {
                JOptionPane.showMessageDialog(this, "X\u00e1c nh\u1eadn th\u1ea5t b\u1ea1i.",
                        "L\u1ed7i", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "L\u1ed7i: " + ex.getMessage(),
                    "L\u1ed7i", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void handleCancel() {

    int choice = JOptionPane.showConfirmDialog(
            this,
            "Bạn có chắc muốn hủy phiếu nhập này?",
            "Xác nhận hủy",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
    );

    if (choice != JOptionPane.YES_OPTION) return;

    try {

        boolean ok = new PurchaseInvoicesBUS()
                .cancelPurchaseInvoice(invoice.getInvoiceId());

        if (ok) {

            JOptionPane.showMessageDialog(
                    this,
                    "Đã hủy phiếu nhập thành công!",
                    "Thành công",
                    JOptionPane.INFORMATION_MESSAGE
            );

            dispose();
            parentPanel.showTable();

        } else {

            JOptionPane.showMessageDialog(
                    this,
                    "Hủy phiếu thất bại.",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE
            );
        }

    } catch (Exception ex) {

        ex.printStackTrace();

        JOptionPane.showMessageDialog(
                this,
                "Lỗi: " + ex.getMessage(),
                "Lỗi",
                JOptionPane.ERROR_MESSAGE
        );
    }
}
    // ── Helpers ───────────────────────────────────────────────────────────────

    private static void addInfo(JPanel p, String key, String value) {
        JLabel kLbl = new JLabel(key);
        kLbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        kLbl.setForeground(new Color(0x555555));
        JLabel vLbl = new JLabel(value);
        vLbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        p.add(kLbl);
        p.add(vLbl);
    }

    private static String nvl(String s) { return s != null ? s : ""; }

    private static String mapStatus(PurchaseInvoicesStatus s) {
        if (s == null) return "Ch\u1edd x\u00e1c nh\u1eadn";
        return switch (s) {
            case PENDING   -> "Ch\u1edd x\u00e1c nh\u1eadn";
            case RECEIVED  -> "\u0110\u00e3 nh\u1eadp";
            case CANCELLED -> "\u0110\u00e3 h\u1ee7y";
            default        -> "Ch\u1edd x\u00e1c nh\u1eadn";
        };
    }

    private static String mapPayment(String s) {
        if (s == null) return "Ch\u01b0a thanh to\u00e1n";
        return switch (s) {
            case "PAID"    -> "\u0110\u00e3 thanh to\u00e1n";
            case "PARTIAL" -> "Thanh to\u00e1n m\u1ed9t ph\u1ea7n";
            default        -> "Ch\u01b0a thanh to\u00e1n";
        };
    }

    private static JButton makeBtn(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        return btn;
    }
    private void handleConfirmPayment() {

    int choice = JOptionPane.showConfirmDialog(
            this,
            "Xác nhận đã thanh toán phiếu nhập này?",
            "Xác nhận thanh toán",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
    );

    if (choice != JOptionPane.YES_OPTION) return;

    try {

        boolean ok = new PurchaseInvoicesBUS()
                .confirmPayment(invoice.getInvoiceId());

        if (ok) {

            JOptionPane.showMessageDialog(
                    this,
                    "Đã xác nhận thanh toán!",
                    "Thành công",
                    JOptionPane.INFORMATION_MESSAGE
            );

            dispose();
            parentPanel.showTable();

        } else {

            JOptionPane.showMessageDialog(
                    this,
                    "Xác nhận thanh toán thất bại.",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE
            );
        }

    } catch (Exception ex) {

        ex.printStackTrace();

        JOptionPane.showMessageDialog(
                this,
                "Lỗi: " + ex.getMessage(),
                "Lỗi",
                JOptionPane.ERROR_MESSAGE
        );
    }
}
}
