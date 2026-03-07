package GUI.NhapXuat;

import BUS.PurchaseInvoicesBUS;
import BUS.StockExportBUS;
import DTO.PurchaseInvoicesDTO;
import DTO.StockExportDTO;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Bảng lịch sử tất cả phiếu Nhập + Xuất kho, sorted theo ngày mới nhất.
 */
class NhapXuatTableCard extends JPanel {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final NhapXuatPanel parent;
    private final DefaultTableModel model;
    private JTextField txtSearch;
    private JComboBox<String> cbLoai;

    // Raw merged rows for filtering
    private final List<Object[]> allRows = new ArrayList<>();

    NhapXuatTableCard(NhapXuatPanel parent) {
        this.parent = parent;
        setLayout(new BorderLayout(0, 0));
        setBackground(Color.WHITE);

        // ── Top bar ─────────────────────────────────────────────
        JPanel topBar = new JPanel(new BorderLayout(10, 0));
        topBar.setBackground(new Color(0xF3EFF8));
        topBar.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));

        JLabel title = new JLabel("Nhập Xuất Kho");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(new Color(0x3D3057));

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        filterPanel.setOpaque(false);

        txtSearch = new JTextField(18);
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtSearch.putClientProperty("JTextField.placeholderText", "Tìm mã phiếu / nhân viên...");

        cbLoai = new JComboBox<>(new String[]{"Tất cả", "Nhập kho", "Xuất kho"});
        cbLoai.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JButton btnNew = makeBtn("+ Tạo phiếu mới", new Color(0x5C4A7F));
        btnNew.addActionListener(e -> parent.showForm());

        JButton btnRefresh = makeBtn("↻ Làm mới", new Color(0x607D8B));
        btnRefresh.addActionListener(e -> refresh());

        filterPanel.add(new JLabel("Loại:"));
        filterPanel.add(cbLoai);
        filterPanel.add(txtSearch);
        filterPanel.add(btnRefresh);
        filterPanel.add(btnNew);

        topBar.add(title, BorderLayout.WEST);
        topBar.add(filterPanel, BorderLayout.EAST);
        add(topBar, BorderLayout.NORTH);

        // ── Table ────────────────────────────────────────────────
        String[] cols = {"Mã phiếu", "Loại", "Ngày tạo", "Nhân viên", "Tổng tiền (đ)", "Trạng thái"};
        model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        JTable table = new JTable(model);
        table.setRowHeight(36);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(0xD1C4E9));
        table.setSelectionBackground(new Color(0xEDE7F6));
        table.setGridColor(new Color(0xE0E0E0));
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 1));

        // Center-align all except Mã phiếu and Nhân viên
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 1; i < cols.length; i++) table.getColumnModel().getColumn(i).setCellRenderer(center);

        // Colour-code Loại column
        table.getColumnModel().getColumn(1).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val, boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                setHorizontalAlignment(CENTER);
                if ("Nhập kho".equals(val)) {
                    setForeground(new Color(0x2E7D32)); setFont(getFont().deriveFont(Font.BOLD));
                } else {
                    setForeground(new Color(0xC62828)); setFont(getFont().deriveFont(Font.BOLD));
                }
                if (sel) setForeground(Color.WHITE);
                return this;
            }
        });

        // Column widths
        int[] widths = {130, 90, 140, 150, 130, 110};
        for (int i = 0; i < widths.length; i++)
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        add(new JScrollPane(table), BorderLayout.CENTER);

        // ── Listeners ────────────────────────────────────────────
        txtSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { applyFilter(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { applyFilter(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { applyFilter(); }
        });
        cbLoai.addActionListener(e -> applyFilter());

        refresh();
    }

    // ─────────────────────────────────────────────────────────────
    // Data helpers
    // ─────────────────────────────────────────────────────────────

    void refresh() {
        allRows.clear();

        // Load phiếu nhập
        try {
            List<PurchaseInvoicesDTO> invoices = new PurchaseInvoicesBUS().getAllPurchaseInvoices();
            if (invoices != null) {
                for (PurchaseInvoicesDTO inv : invoices) {
                    String date = inv.getDateIn() != null ? inv.getDateIn().format(FMT) : "";
                    String total = inv.getTotalAmount() != null
                            ? String.format("%,.0f", inv.getTotalAmount()) : "0";
                    allRows.add(new Object[]{
                            inv.getInvoiceCode(), "Nhập kho", date,
                            inv.getEmployeeName() != null ? inv.getEmployeeName() : "",
                            total,
                            inv.getStatus() != null ? inv.getStatus() : "RECEIVED"
                    });
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Load phiếu xuất
        try {
            List<StockExportDTO> exports = new StockExportBUS().getAllExports();
            if (exports != null) {
                for (StockExportDTO exp : exports) {
                    String date = exp.getExportDate() != null ? exp.getExportDate().format(FMT) : "";
                    String total = exp.getTotalAmount() != null
                            ? String.format("%,.0f", exp.getTotalAmount()) : "0";
                    allRows.add(new Object[]{
                            exp.getExportCode(), "Xuất kho", date,
                            exp.getEmployeeName() != null ? exp.getEmployeeName() : "",
                            total,
                            exp.getStatus() != null ? exp.getStatus() : "DONE"
                    });
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        applyFilter();
    }

    private void applyFilter() {
        String kw = txtSearch.getText().trim().toLowerCase();
        String loai = (String) cbLoai.getSelectedItem();

        model.setRowCount(0);
        for (Object[] row : allRows) {
            String type = (String) row[1];
            if (!"Tất cả".equals(loai) && !loai.equals(type)) continue;
            if (!kw.isEmpty()) {
                boolean match = false;
                for (Object cell : row) {
                    if (cell != null && cell.toString().toLowerCase().contains(kw)) { match = true; break; }
                }
                if (!match) continue;
            }
            model.addRow(row);
        }
    }

    // ─────────────────────────────────────────────────────────────
    // Button factory
    // ─────────────────────────────────────────────────────────────
    private JButton makeBtn(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(6, 14, 6, 14));
        return btn;
    }
}
