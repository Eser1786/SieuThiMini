package GUI.NhapXuat;

import BUS.EmployeeBUS;
import BUS.ProductBUS;
import BUS.PurchaseInvoicesBUS;
import BUS.StockExportBUS;
import BUS.SupplierBUS;
import DTO.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Form tạo phiếu Nhập / Xuất kho.
 *
 * Layout:
 *   [Header info]
 *   [Bảng sản phẩm]
 *   [Phần điều kiện: nhập → nhà cung cấp; xuất → lý do xuất]
 *   [Tổng kết]
 *   [Buttons]
 */
class NhapXuatFormCard extends JPanel {

    private static final Color CLR_BG      = new Color(0xFAF9FF);
    private static final Color CLR_SECTION = new Color(0xEDE7F6);
    private static final Color CLR_ACCENT  = new Color(0x5C4A7F);

    private final NhapXuatPanel parent;

    // Data
    private List<ProductDTO> allProducts = new ArrayList<>();
    private List<EmployeeDTO> allEmployees = new ArrayList<>();
    private List<SupplierDTO> allSuppliers = new ArrayList<>();

    // Form items list
    private final List<FormItem> items = new ArrayList<>();

    // ── Section 1 ───────────────────────────────────────────────
    private JTextField txtCode;
    private JComboBox<String> cbLoai;
    private JLabel lblDate;
    private JComboBox<String> cbEmployee;
    private JTextField txtNote;

    // ── Section 2 (table) ───────────────────────────────────────
    private DefaultTableModel tableModel;

    // ── Section 3 conditional ───────────────────────────────────
    private JPanel conditionalPanel;
    private CardLayout conditionalCard;
    private JComboBox<String> cbSupplier;
    private JTextField txtInvoiceRef;
    private JComboBox<String> cbReason;

    // ── Section 4 summary ───────────────────────────────────────
    private JLabel lblTotalItems;
    private JLabel lblTotalQty;
    private JLabel lblTotalMoney;

    // ── Inner data holder ────────────────────────────────────────
    private static class FormItem {
        long productId;
        String productCode;
        String productName;
        long quantity;
        BigDecimal unitPrice;
        BigDecimal subtotal;
    }

    NhapXuatFormCard(NhapXuatPanel parent) {
        this.parent = parent;
        setBackground(CLR_BG);
        setLayout(new BorderLayout(0, 0));

        // Load data
        try { allProducts = new ProductBUS().getAllProducts(); } catch (Exception ignored) {}
        try { allEmployees = new EmployeeBUS().getAllEmployees(); } catch (Exception ignored) {}
        try { allSuppliers = new SupplierBUS().getAllSuppliers(); } catch (Exception ignored) {}

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(CLR_BG);
        content.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));

        content.add(buildHeaderBar());
        content.add(Box.createVerticalStrut(12));
        content.add(buildSection1());
        content.add(Box.createVerticalStrut(12));
        content.add(buildSection2());
        content.add(Box.createVerticalStrut(12));
        content.add(buildSection3());
        content.add(Box.createVerticalStrut(12));
        content.add(buildSection4());
        content.add(Box.createVerticalStrut(12));
        content.add(buildSection5());

        JScrollPane scroll = new JScrollPane(content);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);

        // Initial conditional visibility
        updateConditional();
    }

    // ─────────────────────────────────────────────────────────────
    // Section builders
    // ─────────────────────────────────────────────────────────────

    private JPanel buildHeaderBar() {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        JLabel lbl = new JLabel("Tạo phiếu Nhập / Xuất kho");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lbl.setForeground(CLR_ACCENT);
        JButton btnBack = makeBtn("← Quay lại", new Color(0x78909C));
        btnBack.addActionListener(e -> parent.showTable());
        p.add(lbl, BorderLayout.WEST);
        p.add(btnBack, BorderLayout.EAST);
        return p;
    }

    /** Section 1 — Thông tin chung */
    private JPanel buildSection1() {
        JPanel wrap = sectionWrap("1. Thông tin chung");
        JPanel grid = new JPanel(new GridBagLayout());
        grid.setOpaque(false);
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(5, 8, 5, 8);
        gc.anchor = GridBagConstraints.WEST;
        gc.fill = GridBagConstraints.HORIZONTAL;

        // Mã phiếu (auto — read-only placeholder)
        txtCode = new JTextField("(Tự động sinh sau khi lưu)", 20);
        txtCode.setEditable(false);
        txtCode.setForeground(Color.GRAY);

        // Loại phiếu
        cbLoai = new JComboBox<>(new String[]{"Nhập kho", "Xuất kho"});
        cbLoai.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cbLoai.addActionListener(e -> updateConditional());

        // Ngày tạo
        lblDate = new JLabel(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        lblDate.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        // Nhân viên
        cbEmployee = new JComboBox<>();
        cbEmployee.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cbEmployee.addItem("-- Chọn nhân viên --");
        for (EmployeeDTO e : allEmployees) cbEmployee.addItem(e.getFullName());

        // Ghi chú
        txtNote = new JTextField(30);
        txtNote.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        int row = 0;
        addFormRow(grid, gc, row++, "Mã phiếu:", txtCode);
        addFormRow(grid, gc, row++, "Loại phiếu:", cbLoai);
        addFormRow(grid, gc, row++, "Ngày tạo:", lblDate);
        addFormRow(grid, gc, row++, "Nhân viên:", cbEmployee);
        addFormRow(grid, gc, row,   "Ghi chú:", txtNote);

        wrap.add(grid, BorderLayout.CENTER);
        return wrap;
    }

    /** Section 2 — Danh sách sản phẩm */
    private JPanel buildSection2() {
        JPanel wrap = sectionWrap("2. Danh sách sản phẩm");

        // Search + add button
        JTextField txtSearch = new JTextField(20);
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtSearch.putClientProperty("JTextField.placeholderText", "Tìm sản phẩm...");
        JButton btnAdd = makeBtn("+ Thêm", new Color(0x388E3C));
        btnAdd.addActionListener(e -> showProductPicker(txtSearch.getText().trim()));

        JPanel addBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        addBar.setOpaque(false);
        addBar.add(new JLabel("Tìm SP:"));
        addBar.add(txtSearch);
        addBar.add(btnAdd);

        // Table
        String[] cols = {"Sản phẩm", "SKU / Mã SP", "Số lượng", "Giá nhập (đ)", "Thành tiền (đ)"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return c == 2; } // only qty editable
            @Override public Class<?> getColumnClass(int c) { return c == 2 ? Long.class : String.class; }
        };
        tableModel.addTableModelListener(e -> {
            int row = e.getFirstRow();
            int col = e.getColumn();
            if (col == 2 && row >= 0 && row < items.size()) {
                try {
                    Object val = tableModel.getValueAt(row, 2);
                    long qty = Long.parseLong(val.toString());
                    if (qty <= 0) qty = 1;
                    FormItem fi = items.get(row);
                    fi.quantity = qty;
                    fi.subtotal = fi.unitPrice.multiply(BigDecimal.valueOf(qty));
                    tableModel.setValueAt(qty, row, 2);
                    tableModel.setValueAt(formatMoney(fi.subtotal), row, 4);
                    updateSummary();
                } catch (NumberFormatException ignored) {}
            }
        });

        JTable table = new JTable(tableModel);
        table.setRowHeight(34);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(0xD1C4E9));
        table.setGridColor(new Color(0xE0E0E0));
        table.setSelectionBackground(new Color(0xEDE7F6));
        int[] widths2 = {200, 110, 80, 130, 130};
        for (int i = 0; i < widths2.length; i++)
            table.getColumnModel().getColumn(i).setPreferredWidth(widths2[i]);

        JButton btnDel = makeBtn("Xóa dòng đã chọn", new Color(0xC62828));
        btnDel.addActionListener(e -> {
            int sel = table.getSelectedRow();
            if (sel >= 0 && sel < items.size()) {
                items.remove(sel);
                tableModel.removeRow(sel);
                updateSummary();
            }
        });

        JScrollPane sp = new JScrollPane(table);
        sp.setPreferredSize(new Dimension(700, 180));

        JPanel tableFooter = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
        tableFooter.setOpaque(false);
        tableFooter.add(btnDel);

        wrap.add(addBar, BorderLayout.NORTH);
        JPanel mid = new JPanel(new BorderLayout());
        mid.setOpaque(false);
        mid.add(sp, BorderLayout.CENTER);
        mid.add(tableFooter, BorderLayout.SOUTH);
        wrap.add(mid, BorderLayout.CENTER);
        return wrap;
    }

    /** Section 3 — Thông tin nguồn / đích (conditional) */
    private JPanel buildSection3() {
        JPanel wrap = sectionWrap("3. Thông tin nguồn / đích");

        conditionalCard = new CardLayout();
        conditionalPanel = new JPanel(conditionalCard);
        conditionalPanel.setOpaque(false);

        // ── Nhập kho card ────────────────────────────────────────
        JPanel nhap = new JPanel(new GridBagLayout());
        nhap.setOpaque(false);
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(5, 8, 5, 8);
        gc.anchor = GridBagConstraints.WEST;
        gc.fill = GridBagConstraints.HORIZONTAL;

        cbSupplier = new JComboBox<>();
        cbSupplier.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cbSupplier.addItem("-- Chọn nhà cung cấp --");
        for (SupplierDTO s : allSuppliers) cbSupplier.addItem(s.getName());

        txtInvoiceRef = new JTextField(20);
        txtInvoiceRef.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtInvoiceRef.putClientProperty("JTextField.placeholderText", "Số hóa đơn nhập (tùy chọn)");

        addFormRow(nhap, gc, 0, "Nhà cung cấp:", cbSupplier);
        addFormRow(nhap, gc, 1, "Số hóa đơn nhập:", txtInvoiceRef);

        // ── Xuất kho card ────────────────────────────────────────
        JPanel xuat = new JPanel(new GridBagLayout());
        xuat.setOpaque(false);
        GridBagConstraints gc2 = new GridBagConstraints();
        gc2.insets = new Insets(5, 8, 5, 8);
        gc2.anchor = GridBagConstraints.WEST;
        gc2.fill = GridBagConstraints.HORIZONTAL;

        cbReason = new JComboBox<>(new String[]{"Bán hàng", "Hủy hàng", "Chuyển kho", "Thất thoát"});
        cbReason.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        addFormRow(xuat, gc2, 0, "Lý do xuất:", cbReason);

        conditionalPanel.add(nhap, "NHAP");
        conditionalPanel.add(xuat, "XUAT");

        wrap.add(conditionalPanel, BorderLayout.CENTER);
        return wrap;
    }

    /** Section 4 — Tổng kết */
    private JPanel buildSection4() {
        JPanel wrap = sectionWrap("4. Tổng kết phiếu");
        JPanel inner = new JPanel(new GridLayout(3, 2, 10, 5));
        inner.setOpaque(false);
        inner.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));

        lblTotalItems = new JLabel("0");
        lblTotalQty   = new JLabel("0");
        lblTotalMoney = new JLabel("0 đ");
        for (JLabel l : new JLabel[]{lblTotalItems, lblTotalQty, lblTotalMoney}) {
            l.setFont(new Font("Segoe UI", Font.BOLD, 15));
            l.setForeground(CLR_ACCENT);
        }

        inner.add(boldLabel("Tổng số sản phẩm:")); inner.add(lblTotalItems);
        inner.add(boldLabel("Tổng số lượng:"));    inner.add(lblTotalQty);
        inner.add(boldLabel("Tổng tiền:"));         inner.add(lblTotalMoney);

        wrap.add(inner, BorderLayout.CENTER);
        return wrap;
    }

    /** Section 5 — Buttons */
    private JPanel buildSection5() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        p.setOpaque(false);

        JButton btnCancel = makeBtn("Hủy", new Color(0x78909C));
        JButton btnPrint  = makeBtn("In phiếu", new Color(0x1565C0));
        JButton btnSave   = makeBtn("Lưu phiếu", CLR_ACCENT);

        btnCancel.addActionListener(e -> parent.showTable());
        btnPrint.addActionListener(e -> showPrintDialog());
        btnSave.addActionListener(e -> handleSave());

        p.add(btnCancel);
        p.add(btnPrint);
        p.add(btnSave);
        return p;
    }

    // ─────────────────────────────────────────────────────────────
    // Product picker dialog
    // ─────────────────────────────────────────────────────────────
    private void showProductPicker(String keyword) {
        JDialog dlg = new JDialog(SwingUtilities.getWindowAncestor(this), "Chọn sản phẩm", Dialog.ModalityType.APPLICATION_MODAL);
        dlg.setSize(600, 400);
        dlg.setLocationRelativeTo(this);

        JTextField search = new JTextField(keyword, 20);
        search.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        String[] cols = {"Mã SP", "Tên sản phẩm", "Tồn kho", "Giá nhập (đ)"};
        DefaultTableModel pickModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable pickTable = new JTable(pickModel);
        pickTable.setRowHeight(30);
        pickTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        pickTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        pickTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        Runnable fillTable = () -> {
            pickModel.setRowCount(0);
            String kw = search.getText().trim().toLowerCase();
            for (ProductDTO p : allProducts) {
                if (!kw.isEmpty() && !p.getName().toLowerCase().contains(kw)
                        && !p.getCode().toLowerCase().contains(kw)) continue;
                pickModel.addRow(new Object[]{
                        p.getCode(), p.getName(), p.getTotalQuantity(),
                        p.getCostPrice() != null ? formatMoney(p.getCostPrice()) : "0"
                });
            }
        };
        fillTable.run();
        search.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { fillTable.run(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { fillTable.run(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { fillTable.run(); }
        });

        JButton btnOk = makeBtn("Chọn", CLR_ACCENT);
        btnOk.addActionListener(e -> {
            int sel = pickTable.getSelectedRow();
            if (sel < 0) { JOptionPane.showMessageDialog(dlg, "Vui lòng chọn một sản phẩm."); return; }
            String code = (String) pickModel.getValueAt(sel, 0);
            allProducts.stream().filter(p -> p.getCode().equals(code)).findFirst().ifPresent(p -> {
                // Check duplicate
                boolean dup = items.stream().anyMatch(fi -> fi.productId == p.getId());
                if (dup) { JOptionPane.showMessageDialog(dlg, "Sản phẩm đã có trong phiếu."); return; }
                FormItem fi = new FormItem();
                fi.productId = p.getId();
                fi.productCode = p.getCode();
                fi.productName = p.getName();
                fi.quantity = 1;
                fi.unitPrice = p.getCostPrice() != null ? p.getCostPrice() : BigDecimal.ZERO;
                fi.subtotal = fi.unitPrice;
                items.add(fi);
                tableModel.addRow(new Object[]{fi.productName, fi.productCode, fi.quantity,
                        formatMoney(fi.unitPrice), formatMoney(fi.subtotal)});
                updateSummary();
                dlg.dispose();
            });
        });
        pickTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) btnOk.doClick();
            }
        });

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        top.add(new JLabel("Tìm kiếm:"));
        top.add(search);
        top.add(btnOk);

        dlg.add(top, BorderLayout.NORTH);
        dlg.add(new JScrollPane(pickTable), BorderLayout.CENTER);
        dlg.setVisible(true);
    }

    // ─────────────────────────────────────────────────────────────
    // Save handler
    // ─────────────────────────────────────────────────────────────
    private void handleSave() {
        // Validate employee
        int empIdx = cbEmployee.getSelectedIndex();
        if (empIdx <= 0 || empIdx > allEmployees.size()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên thực hiện.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (items.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng thêm ít nhất một sản phẩm.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        EmployeeDTO emp = allEmployees.get(empIdx - 1);
        boolean isNhap = "Nhập kho".equals(cbLoai.getSelectedItem());

        try {
            List<String> warnings;
            if (isNhap) {
                warnings = saveNhapKho(emp);
            } else {
                warnings = saveXuatKho(emp);
            }

            String msg = isNhap ? "Lưu phiếu nhập thành công!" : "Lưu phiếu xuất thành công!";
            if (!warnings.isEmpty()) {
                msg += "\n\n⚠ Sản phẩm sắp hết hàng:\n• " + String.join("\n• ", warnings);
                JOptionPane.showMessageDialog(this, msg, "Cảnh báo tồn kho", JOptionPane.WARNING_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, msg, "Thành công", JOptionPane.INFORMATION_MESSAGE);
            }
            parent.showTable();

        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi xác thực", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi lưu phiếu: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private List<String> saveNhapKho(EmployeeDTO emp) throws Exception {
        int supIdx = cbSupplier.getSelectedIndex();
        if (supIdx <= 0 || supIdx > allSuppliers.size()) {
            throw new IllegalArgumentException("Vui lòng chọn nhà cung cấp.");
        }
        SupplierDTO sup = allSuppliers.get(supIdx - 1);

        PurchaseInvoicesDTO inv = new PurchaseInvoicesDTO();
        inv.setEmployeeId((long) emp.getId());
        inv.setEmployeeName(emp.getFullName());
        inv.setSupplierId((long) sup.getID());
        inv.setSupplierName(sup.getName());
        inv.setDateIn(LocalDateTime.now());
        inv.setNotes(txtNote.getText().trim());
        inv.setPaymentMethod("DEBT");
        inv.setPaymentStatus("PENDING");
        inv.setStatus("RECEIVED");

        List<PurchaseInvoiceItemsDTO> invItems = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;
        for (FormItem fi : items) {
            PurchaseInvoiceItemsDTO it = new PurchaseInvoiceItemsDTO();
            it.setProductId((long) fi.productId);
            it.setProductCode(fi.productCode);
            it.setProductName(fi.productName);
            it.setQuantity(fi.quantity);
            it.setUnitPrice(fi.unitPrice);
            it.setSubtotal(fi.subtotal);
            invItems.add(it);
            total = total.add(fi.subtotal);
        }
        inv.setItems(invItems);
        inv.setTotalAmount(total);

        boolean ok = new PurchaseInvoicesBUS().addPurchaseInvoice(inv);
        if (!ok) throw new RuntimeException("Lưu phiếu nhập thất bại.");

        // Check min stock after update (re-read products)
        List<String> warnings = new ArrayList<>();
        List<ProductDTO> fresh = new ProductBUS().getAllProducts();
        for (FormItem fi : items) {
            final long pid = fi.productId;
            final long qty = fi.quantity;
            fresh.stream().filter(p -> (long) p.getId() == pid).findFirst().ifPresent(p -> {
                long newQty = p.getTotalQuantity() + qty; // already updated in DB; re-read would reflect it
                // Use DB value directly
                if (p.getTotalQuantity() < p.getMinStockLevel()) {
                    warnings.add(p.getName());
                }
            });
        }
        return warnings;
    }

    private List<String> saveXuatKho(EmployeeDTO emp) {
        String reasonDisplay = (String) cbReason.getSelectedItem();
        String reason = switch (reasonDisplay) {
            case "Bán hàng"   -> "SALE";
            case "Hủy hàng"   -> "CANCEL";
            case "Chuyển kho" -> "TRANSFER";
            default           -> "LOSS";
        };

        StockExportDTO dto = new StockExportDTO();
        dto.setEmployeeId((long) emp.getId());
        dto.setEmployeeName(emp.getFullName());
        dto.setReason(reason);
        dto.setNotes(txtNote.getText().trim());

        List<StockExportItemDTO> expItems = new ArrayList<>();
        for (FormItem fi : items) {
            StockExportItemDTO it = new StockExportItemDTO();
            it.setProductId(fi.productId);
            it.setProductCode(fi.productCode);
            it.setProductName(fi.productName);
            it.setQuantity(fi.quantity);
            it.setUnitPrice(fi.unitPrice);
            expItems.add(it);
        }
        dto.setItems(expItems);

        return new StockExportBUS().addExport(dto);
    }

    // ─────────────────────────────────────────────────────────────
    // Print dialog
    // ─────────────────────────────────────────────────────────────
    private void showPrintDialog() {
        boolean isNhap = "Nhập kho".equals(cbLoai.getSelectedItem());
        StringBuilder sb = new StringBuilder();
        sb.append("Phiếu: ").append(isNhap ? "NK..." : "XK...").append("\n");
        sb.append("Loại : ").append(isNhap ? "Nhập kho" : "Xuất kho").append("\n");
        sb.append("Ngày : ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))).append("\n");
        int empIdx = cbEmployee.getSelectedIndex();
        sb.append("NV   : ").append(empIdx > 0 && empIdx <= allEmployees.size() ? allEmployees.get(empIdx - 1).getFullName() : "--").append("\n");
        if (isNhap) {
            int supIdx = cbSupplier.getSelectedIndex();
            sb.append("NCC  : ").append(supIdx > 0 && supIdx <= allSuppliers.size() ? allSuppliers.get(supIdx - 1).getName() : "--").append("\n");
        } else {
            sb.append("Lý do: ").append(cbReason.getSelectedItem()).append("\n");
        }
        sb.append("\n");
        sb.append(String.format("%-25s %5s %12s %12s%n", "Sản phẩm", "SL", "Giá nhập", "Thành tiền"));
        sb.append("-".repeat(58)).append("\n");
        for (FormItem fi : items) {
            sb.append(String.format("%-25s %5d %12s %12s%n",
                    fi.productName.length() > 24 ? fi.productName.substring(0, 24) : fi.productName,
                    fi.quantity,
                    formatMoney(fi.unitPrice),
                    formatMoney(fi.subtotal)));
        }
        sb.append("-".repeat(58)).append("\n");
        sb.append("Tổng số lượng: ").append(items.stream().mapToLong(fi -> fi.quantity).sum()).append("\n");
        if (isNhap) {
            BigDecimal total = items.stream().map(fi -> fi.subtotal).reduce(BigDecimal.ZERO, BigDecimal::add);
            sb.append("Tổng tiền    : ").append(formatMoney(total)).append(" đ\n");
        }

        JTextArea area = new JTextArea(sb.toString());
        area.setFont(new Font("Monospaced", Font.PLAIN, 13));
        area.setEditable(false);
        area.setBackground(new Color(0x1E1E2E));
        area.setForeground(Color.WHITE);
        area.setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));

        JDialog dlg = new JDialog(SwingUtilities.getWindowAncestor(this), "In phiếu", Dialog.ModalityType.APPLICATION_MODAL);
        dlg.setSize(560, 420);
        dlg.setLocationRelativeTo(this);
        dlg.add(new JScrollPane(area), BorderLayout.CENTER);

        JButton btnClose = makeBtn("Đóng", new Color(0x607D8B));
        btnClose.addActionListener(e -> dlg.dispose());
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btns.add(btnClose);
        dlg.add(btns, BorderLayout.SOUTH);
        dlg.setVisible(true);
    }

    // ─────────────────────────────────────────────────────────────
    // Helpers
    // ─────────────────────────────────────────────────────────────
    private void updateConditional() {
        if (conditionalCard == null) return;
        boolean isNhap = "Nhập kho".equals(cbLoai.getSelectedItem());
        conditionalCard.show(conditionalPanel, isNhap ? "NHAP" : "XUAT");
    }

    private void updateSummary() {
        long totalItems = items.size();
        long totalQty   = items.stream().mapToLong(fi -> fi.quantity).sum();
        BigDecimal totalMoney = items.stream().map(fi -> fi.subtotal).reduce(BigDecimal.ZERO, BigDecimal::add);
        lblTotalItems.setText(String.valueOf(totalItems));
        lblTotalQty.setText(String.valueOf(totalQty));
        lblTotalMoney.setText(formatMoney(totalMoney) + " đ");
    }

    private JPanel sectionWrap(String title) {
        JPanel p = new JPanel(new BorderLayout(0, 6));
        p.setBackground(CLR_SECTION);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xCE93D8), 1, true),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)));
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel lbl = boldLabel(title);
        lbl.setForeground(CLR_ACCENT);
        lbl.setBorder(BorderFactory.createEmptyBorder(0, 0, 4, 0));
        p.add(lbl, BorderLayout.NORTH);
        return p;
    }

    private void addFormRow(JPanel grid, GridBagConstraints gc, int row, String label, JComponent comp) {
        gc.gridx = 0; gc.gridy = row; gc.weightx = 0;
        grid.add(boldLabel(label), gc);
        gc.gridx = 1; gc.weightx = 1;
        grid.add(comp, gc);
    }

    private JLabel boldLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 14));
        return l;
    }

    private JButton makeBtn(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(7, 16, 7, 16));
        return btn;
    }

    private String formatMoney(BigDecimal val) {
        if (val == null) return "0";
        return String.format("%,.0f", val);
    }
}
