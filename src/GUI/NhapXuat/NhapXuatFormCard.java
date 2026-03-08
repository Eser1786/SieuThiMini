package GUI.NhapXuat;

import BUS.EmployeeBUS;
import BUS.ProductBUS;
import BUS.PurchaseInvoicesBUS;
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

/** Form tao phieu nhap kho - dung trong JDialog popup. */
class NhapXuatFormCard extends JPanel {

    private static final Color CLR_BG      = new Color(0xFAF9FF);
    private static final Color CLR_SECTION = new Color(0xEDE7F6);
    private static final Color CLR_ACCENT  = new Color(0x5C4A7F);

    // Data
    private List<ProductDTO>  allProducts  = new ArrayList<>();
    private List<EmployeeDTO> allEmployees = new ArrayList<>();
    private List<SupplierDTO> allSuppliers = new ArrayList<>();

    // Form items list
    private final List<FormItem> items = new ArrayList<>();

    // Section 1
    private JLabel            lblDate;
    private JComboBox<String> cbEmployee;
    private JTextField        txtNote;

    // Section 2 (product table)
    private DefaultTableModel tableModel;

    // Section 3 (supplier)
    private JComboBox<String> cbSupplier;
    private JTextField        txtInvoiceRef;

    // Section 4 summary
    private JLabel lblTotalItems;
    private JLabel lblTotalQty;
    private JLabel lblTotalMoney;

    // Inner data holder
    private static class FormItem {
        long       productId;
        String     productCode;
        String     productName;
        long       quantity;
        BigDecimal unitPrice;
        BigDecimal subtotal;
    }

    NhapXuatFormCard(Window dialogOwner) {
        setBackground(CLR_BG);
        setLayout(new BorderLayout(0, 0));

        // Load data
        try { allProducts  = new ProductBUS().getAllProducts();  } catch (Exception ignored) {}
        try { allEmployees = new EmployeeBUS().getAllEmployees(); } catch (Exception ignored) {}
        try { allSuppliers = new SupplierBUS().getAllSuppliers(); } catch (Exception ignored) {}

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(CLR_BG);
        content.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));

        content.add(buildSection1());
        content.add(Box.createVerticalStrut(12));
        content.add(buildSection2());
        content.add(Box.createVerticalStrut(12));
        content.add(buildSection3());
        content.add(Box.createVerticalStrut(12));
        content.add(buildSection4());

        JScrollPane scroll = new JScrollPane(content);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        add(scroll, BorderLayout.CENTER);
        add(buildSection5(), BorderLayout.SOUTH);
    }

    // ----- Section builders -------------------------------------------------

    private JPanel buildSection1() {
        JPanel wrap = sectionWrap("1. Thong tin chung");
        JPanel grid = new JPanel(new GridBagLayout());
        grid.setOpaque(false);
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(5, 8, 5, 8);
        gc.anchor = GridBagConstraints.WEST;
        gc.fill   = GridBagConstraints.HORIZONTAL;

        lblDate = new JLabel(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        lblDate.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        cbEmployee = new JComboBox<>();
        cbEmployee.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cbEmployee.addItem("-- Chon nhan vien --");
        for (EmployeeDTO e : allEmployees) cbEmployee.addItem(e.getFullName());

        txtNote = new JTextField(30);
        txtNote.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        int row = 0;
        addFormRow(grid, gc, row++, "Ngay tao:", lblDate);
        addFormRow(grid, gc, row++, "Nhan vien:", cbEmployee);
        addFormRow(grid, gc, row,   "Ghi chu:",   txtNote);

        wrap.add(grid, BorderLayout.CENTER);
        return wrap;
    }

    private JPanel buildSection2() {
        JPanel wrap = sectionWrap("2. Danh sach san pham");

        JTextField txtSearch = new JTextField(20);
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JButton btnAdd = makeBtn("+ Them", new Color(0x388E3C));
        btnAdd.addActionListener(e -> showProductPicker(txtSearch.getText().trim()));

        JPanel addBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        addBar.setOpaque(false);
        addBar.add(new JLabel("Tim SP:"));
        addBar.add(txtSearch);
        addBar.add(btnAdd);

        String[] cols = {"San pham", "SKU / Ma SP", "So luong", "Gia nhap (d)", "Thanh tien (d)"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return c == 2; }
            @Override public Class<?> getColumnClass(int c) { return c == 2 ? Long.class : String.class; }
        };
        tableModel.addTableModelListener(e -> {
            int r = e.getFirstRow();
            int c = e.getColumn();
            if (c == 2 && r >= 0 && r < items.size()) {
                try {
                    long qty = Long.parseLong(tableModel.getValueAt(r, 2).toString());
                    if (qty <= 0) qty = 1;
                    FormItem fi = items.get(r);
                    fi.quantity = qty;
                    fi.subtotal = fi.unitPrice.multiply(BigDecimal.valueOf(qty));
                    tableModel.setValueAt(qty, r, 2);
                    tableModel.setValueAt(formatMoney(fi.subtotal), r, 4);
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
        int[] w = {200, 110, 80, 130, 130};
        for (int i = 0; i < w.length; i++) table.getColumnModel().getColumn(i).setPreferredWidth(w[i]);

        JButton btnDel = makeBtn("Xoa dong da chon", new Color(0xC62828));
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

    private JPanel buildSection3() {
        JPanel wrap = sectionWrap("3. Nha cung cap");
        JPanel grid = new JPanel(new GridBagLayout());
        grid.setOpaque(false);
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(5, 8, 5, 8);
        gc.anchor = GridBagConstraints.WEST;
        gc.fill   = GridBagConstraints.HORIZONTAL;

        cbSupplier = new JComboBox<>();
        cbSupplier.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cbSupplier.addItem("-- Chon nha cung cap --");
        for (SupplierDTO s : allSuppliers) cbSupplier.addItem(s.getName());

        txtInvoiceRef = new JTextField(20);
        txtInvoiceRef.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        addFormRow(grid, gc, 0, "Nha cung cap:", cbSupplier);
        addFormRow(grid, gc, 1, "So hoa don nhap:", txtInvoiceRef);

        wrap.add(grid, BorderLayout.CENTER);
        return wrap;
    }

    private JPanel buildSection4() {
        JPanel wrap = sectionWrap("4. Tong ket phieu");
        JPanel inner = new JPanel(new GridLayout(3, 2, 10, 5));
        inner.setOpaque(false);
        inner.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));

        lblTotalItems = new JLabel("0");
        lblTotalQty   = new JLabel("0");
        lblTotalMoney = new JLabel("0 d");
        for (JLabel l : new JLabel[]{lblTotalItems, lblTotalQty, lblTotalMoney}) {
            l.setFont(new Font("Segoe UI", Font.BOLD, 15));
            l.setForeground(CLR_ACCENT);
        }

        inner.add(boldLabel("Tong so san pham:")); inner.add(lblTotalItems);
        inner.add(boldLabel("Tong so luong:"));    inner.add(lblTotalQty);
        inner.add(boldLabel("Tong tien:"));         inner.add(lblTotalMoney);

        wrap.add(inner, BorderLayout.CENTER);
        return wrap;
    }

    private JPanel buildSection5() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
        p.setOpaque(false);
        p.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(0xDDD8EE)));

        JButton btnCancel = makeBtn("Huy", new Color(0x78909C));
        JButton btnPrint  = makeBtn("In phieu", new Color(0x1565C0));
        JButton btnSave   = makeBtn("Luu phieu", CLR_ACCENT);

        btnCancel.addActionListener(e -> closeDialog());
        btnPrint.addActionListener(e -> showPrintDialog());
        btnSave.addActionListener(e -> handleSave());

        p.add(btnCancel);
        p.add(btnPrint);
        p.add(btnSave);
        return p;
    }

    // ----- Product picker ---------------------------------------------------

    private void showProductPicker(String keyword) {
        JDialog dlg = new JDialog(SwingUtilities.getWindowAncestor(this),
                "Chon san pham", Dialog.ModalityType.APPLICATION_MODAL);
        dlg.setSize(600, 400);
        dlg.setLocationRelativeTo(this);

        JTextField search = new JTextField(keyword, 20);
        search.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        String[] cols = {"Ma SP", "Ten san pham", "Ton kho", "Gia nhap (d)"};
        DefaultTableModel pm = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable pickTable = new JTable(pm);
        pickTable.setRowHeight(30);
        pickTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        pickTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        pickTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        Runnable fill = () -> {
            pm.setRowCount(0);
            String kw = search.getText().trim().toLowerCase();
            for (ProductDTO p : allProducts) {
                if (!kw.isEmpty() && !p.getName().toLowerCase().contains(kw)
                        && !p.getCode().toLowerCase().contains(kw)) continue;
                pm.addRow(new Object[]{
                        p.getCode(), p.getName(), p.getTotalQuantity(),
                        p.getCostPrice() != null ? formatMoney(p.getCostPrice()) : "0"
                });
            }
        };
        fill.run();
        search.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate (javax.swing.event.DocumentEvent e) { fill.run(); }
            public void removeUpdate (javax.swing.event.DocumentEvent e) { fill.run(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { fill.run(); }
        });

        JButton btnOk = makeBtn("Chon", CLR_ACCENT);
        btnOk.addActionListener(e -> {
            int sel = pickTable.getSelectedRow();
            if (sel < 0) { JOptionPane.showMessageDialog(dlg, "Vui long chon mot san pham."); return; }
            String code = (String) pm.getValueAt(sel, 0);
            allProducts.stream().filter(p -> p.getCode().equals(code)).findFirst().ifPresent(p -> {
                if (items.stream().anyMatch(fi -> fi.productId == p.getId())) {
                    JOptionPane.showMessageDialog(dlg, "San pham da co trong phieu.");
                    return;
                }
                FormItem fi = new FormItem();
                fi.productId   = p.getId();
                fi.productCode = p.getCode();
                fi.productName = p.getName();
                fi.quantity    = 1;
                fi.unitPrice   = p.getCostPrice() != null ? p.getCostPrice() : BigDecimal.ZERO;
                fi.subtotal    = fi.unitPrice;
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
        top.add(new JLabel("Tim kiem:"));
        top.add(search);
        top.add(btnOk);

        dlg.add(top, BorderLayout.NORTH);
        dlg.add(new JScrollPane(pickTable), BorderLayout.CENTER);
        dlg.setVisible(true);
    }

    // ----- Save handler -----------------------------------------------------

    private void handleSave() {
        int empIdx = cbEmployee.getSelectedIndex();
        if (empIdx <= 0 || empIdx > allEmployees.size()) {
            JOptionPane.showMessageDialog(this, "Vui long chon nhan vien thuc hien.",
                    "Loi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (items.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui long them it nhat mot san pham.",
                    "Loi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        EmployeeDTO emp = allEmployees.get(empIdx - 1);
        try {
            List<String> warnings = saveNhapKho(emp);
            String msg = "Luu phieu nhap thanh cong!";
            if (!warnings.isEmpty()) {
                msg += "\n\nSan pham sap het hang:\n- " + String.join("\n- ", warnings);
                JOptionPane.showMessageDialog(this, msg, "Canh bao ton kho", JOptionPane.WARNING_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, msg, "Thanh cong", JOptionPane.INFORMATION_MESSAGE);
            }
            closeDialog();
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Loi xac thuc", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Loi khi luu phieu: " + ex.getMessage(),
                    "Loi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private List<String> saveNhapKho(EmployeeDTO emp) throws Exception {
        int supIdx = cbSupplier.getSelectedIndex();
        if (supIdx <= 0 || supIdx > allSuppliers.size())
            throw new IllegalArgumentException("Vui long chon nha cung cap.");

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
        if (!ok) throw new RuntimeException("Luu phieu nhap that bai.");

        // Check min stock warnings
        List<String> warnings = new ArrayList<>();
        for (ProductDTO fresh : new ProductBUS().getAllProducts()) {
            final long id = fresh.getId();
            if (items.stream().anyMatch(fi -> fi.productId == id)
                    && fresh.getTotalQuantity() < fresh.getMinStockLevel()) {
                warnings.add(fresh.getName());
            }
        }
        return warnings;
    }

    // ----- Print dialog -----------------------------------------------------

    private void showPrintDialog() {
        StringBuilder sb = new StringBuilder();
        sb.append("Phieu: NK...\n");
        sb.append("Loai : Nhap kho\n");
        sb.append("Ngay : ").append(
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))).append("\n");
        int empIdx = cbEmployee.getSelectedIndex();
        sb.append("NV   : ").append(
                empIdx > 0 && empIdx <= allEmployees.size()
                        ? allEmployees.get(empIdx - 1).getFullName() : "--").append("\n");
        int supIdx = cbSupplier.getSelectedIndex();
        sb.append("NCC  : ").append(
                supIdx > 0 && supIdx <= allSuppliers.size()
                        ? allSuppliers.get(supIdx - 1).getName() : "--").append("\n");
        sb.append("\n");
        sb.append(String.format("%-25s %5s %12s %12s%n", "San pham", "SL", "Gia nhap", "Thanh tien"));
        sb.append("-".repeat(58)).append("\n");
        for (FormItem fi : items) {
            String name = fi.productName.length() > 24 ? fi.productName.substring(0, 24) : fi.productName;
            sb.append(String.format("%-25s %5d %12s %12s%n",
                    name, fi.quantity, formatMoney(fi.unitPrice), formatMoney(fi.subtotal)));
        }
        sb.append("-".repeat(58)).append("\n");
        sb.append("Tong so luong: ").append(items.stream().mapToLong(fi -> fi.quantity).sum()).append("\n");
        BigDecimal total = items.stream().map(fi -> fi.subtotal).reduce(BigDecimal.ZERO, BigDecimal::add);
        sb.append("Tong tien    : ").append(formatMoney(total)).append(" d\n");

        JTextArea area = new JTextArea(sb.toString());
        area.setFont(new Font("Monospaced", Font.PLAIN, 13));
        area.setEditable(false);
        area.setBackground(new Color(0x1E1E2E));
        area.setForeground(Color.WHITE);
        area.setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));

        JDialog dlg = new JDialog(SwingUtilities.getWindowAncestor(this),
                "In phieu", Dialog.ModalityType.APPLICATION_MODAL);
        dlg.setSize(560, 420);
        dlg.setLocationRelativeTo(this);
        dlg.add(new JScrollPane(area), BorderLayout.CENTER);

        JButton btnClose = makeBtn("Dong", new Color(0x607D8B));
        btnClose.addActionListener(e -> dlg.dispose());
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btns.add(btnClose);
        dlg.add(btns, BorderLayout.SOUTH);
        dlg.setVisible(true);
    }

    // ----- Helpers ----------------------------------------------------------

    private void closeDialog() {
        Window w = SwingUtilities.getWindowAncestor(this);
        if (w != null) w.dispose();
    }

    private void updateSummary() {
        lblTotalItems.setText(String.valueOf(items.size()));
        lblTotalQty.setText(String.valueOf(items.stream().mapToLong(fi -> fi.quantity).sum()));
        BigDecimal total = items.stream().map(fi -> fi.subtotal).reduce(BigDecimal.ZERO, BigDecimal::add);
        lblTotalMoney.setText(formatMoney(total) + " d");
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