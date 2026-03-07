package GUI.Kho;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import BUS.ProductBUS;
import DTO.ProductDTO;
import GUI.ExportUtils;
import GUI.UIUtils;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * KhoTableCard — tách từ KhoPanel, chứa toàn bộ nội dung hiển thị kho.
 */
class KhoTableCard extends JPanel {
    private JComboBox<String> cbStatus;
    private JTextField txtSearch;
    private JComboBox<String> cbSupplier;
    private JTable table;
    private DefaultTableModel model;

    KhoTableCard() {
        setLayout(new BorderLayout());
        setBackground(new Color(0xF8F7FF));

        // ── HEADER ──────────────────────────────────────────────────────────
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 12));
        header.setBackground(new Color(0xF8F7FF));
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0xDDDDDD)),
                BorderFactory.createEmptyBorder(0, 20, 0, 20)));
        JPanel bar = new JPanel();
        bar.setPreferredSize(new Dimension(5, 26));
        bar.setBackground(new Color(0x5C4A7F));
        header.add(bar);
        header.add(Box.createHorizontalStrut(12));
        JLabel hdrTitle = new JLabel("QUẢN LÝ KHO");
        hdrTitle.setFont(new Font("Arial", Font.BOLD, 20));
        header.add(hdrTitle);

        // ── TOP TOOLBAR (single row: filters LEFT, buttons RIGHT) ───────────
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(0xF8F7FF));
        topPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0xCCCCCC)),
                BorderFactory.createEmptyBorder(8, 16, 8, 16)));

        // Left: search + filters
        JPanel khoRow1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));
        khoRow1.setBackground(new Color(0xF8F7FF));

        JLabel lbSearch = new JLabel("Tìm kiếm:");
        lbSearch.setFont(new Font("Arial", Font.PLAIN, 13));
        txtSearch = new JTextField();
        txtSearch.setPreferredSize(new Dimension(200, 36));
        txtSearch.setFont(new Font("Arial", Font.PLAIN, 13));
        txtSearch.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xBBBBBB), 1),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)));

        JLabel lbNCC = new JLabel("Nhà cung cấp:");
        lbNCC.setFont(new Font("Arial", Font.PLAIN, 13));
        cbSupplier = new JComboBox<>();
        cbSupplier.setPreferredSize(new Dimension(160, 36));
        UIUtils.styleComboBox(cbSupplier);

        JLabel lbStatus = new JLabel("Trạng thái:");
        lbStatus.setFont(new Font("Arial", Font.PLAIN, 13));
        cbStatus = new JComboBox<>();
        cbStatus.setPreferredSize(new Dimension(140, 36));
        UIUtils.styleComboBox(cbStatus);

        khoRow1.add(lbSearch); khoRow1.add(txtSearch);
        khoRow1.add(Box.createHorizontalStrut(6));
        khoRow1.add(lbNCC); khoRow1.add(cbSupplier);
        khoRow1.add(Box.createHorizontalStrut(6));
        khoRow1.add(lbStatus); khoRow1.add(cbStatus);

        // Right: export buttons
        JPanel khoRow2 = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
        khoRow2.setBackground(new Color(0xF8F7FF));

        JButton btnPDF    = ExportUtils.makeExportButton("Xuất PDF",   new Color(0x7B52AB));
        JButton btnExcel  = ExportUtils.makeExportButton("Xuất Excel", new Color(0x2E7D32));
        JButton btnImport = ExportUtils.makeImportButton("Nhập CSV");
        btnPDF.addActionListener(e -> ExportUtils.xuatPDF(this, model, "Danh sách kho"));
        btnExcel.addActionListener(e -> ExportUtils.xuatCSV(this, model, "kho"));
        btnImport.addActionListener(e -> {
            List<String[]> rows = ExportUtils.importCSV(this);
            if (rows == null) return;
            for (String[] r : rows) { if (r.length < 7) continue; model.addRow((Object[])r); }
        });
        khoRow2.add(btnPDF); khoRow2.add(btnExcel); khoRow2.add(btnImport);

        topPanel.add(khoRow1, BorderLayout.WEST);
        topPanel.add(khoRow2, BorderLayout.EAST);

        // Stack header + toolbar in NORTH
        JPanel northArea = new JPanel();
        northArea.setLayout(new BoxLayout(northArea, BoxLayout.Y_AXIS));
        northArea.add(header);
        northArea.add(topPanel);
        add(northArea, BorderLayout.NORTH);

        String[] headers = { "Hình ảnh", "STT", "Mã SP", "Tên SP", "SL", "Nhà cung cấp", "Trạng thái" };
        model = new DefaultTableModel(headers, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0) return ImageIcon.class;
                return Object.class;
            }
        };
        table = new JTable(model);
        table.setRowHeight(55);

        // renderer for image column
        table.getColumnModel().getColumn(0).setCellRenderer(
                new DefaultTableCellRenderer() {
                    @Override
                    public Component getTableCellRendererComponent(
                            JTable table, Object value, boolean isSelected,
                            boolean hasFocus, int row, int column) {
                        JLabel label = (JLabel) super.getTableCellRendererComponent(
                                table, value, isSelected, hasFocus, row, column);
                        if (value instanceof ImageIcon) {
                            label.setIcon((ImageIcon) value);
                            label.setText("");
                        }
                        label.setHorizontalAlignment(SwingConstants.CENTER);
                        return label;
                    }
                });

        table.getColumnModel().getColumn(6).setCellRenderer(
                new DefaultTableCellRenderer() {
                    @Override
                    public Component getTableCellRendererComponent(
                            JTable table, Object value, boolean isSelected,
                            boolean hasFocus, int row, int column) {
                        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                        String status = value.toString();
                        if (isSelected) {
                            setBackground(table.getSelectionBackground());
                            setForeground(table.getSelectionForeground());
                        } else {
                            setBackground(row % 2 == 0 ? new Color(245, 245, 250) : new Color(230, 230, 240));
                            if (status.equals("Hết hàng")) setForeground(Color.RED);
                            else if (status.equals("Gần hết")) setForeground(new Color(255, 140, 0));
                            else setForeground(new Color(0, 128, 0));
                        }
                        setHorizontalAlignment(SwingConstants.CENTER);
                        return this;
                    }
                });

        // Table style
        table.setRowHeight(55);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(new Color(0xDCD6F7));
        table.setSelectionForeground(Color.BLACK);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(0xAF9FCB));
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setPreferredSize(new Dimension(100, 40));

        // Zebra row renderer
        table.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {
                JLabel c = (JLabel) super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);
                if (value instanceof ImageIcon) { c.setIcon((ImageIcon) value); c.setText(""); }
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? new Color(0xF8F7FF) : new Color(0xECE9F9));
                }
                setHorizontalAlignment(SwingConstants.CENTER);
                return c;
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(new Color(0xF3F0FF));
        UIUtils.styleScrollPane(scroll);
        add(scroll, BorderLayout.CENTER);

        loadStatusFilter();
        loadSuppliers();
        loadData();

        txtSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e)  { loadData(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e)  { loadData(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { loadData(); }
        });
        cbSupplier.addActionListener(e -> loadData());
        cbStatus.addActionListener(e -> loadData());
    }

    private void loadData() {
        ProductBUS bus = new ProductBUS();
        ArrayList<ProductDTO> list = bus.getAllProducts();
        String keyword = txtSearch.getText().trim().toLowerCase();
        String selectedSupplier = cbSupplier.getSelectedItem().toString();
        String selectedStatus = (cbStatus.getSelectedItem() != null) ? cbStatus.getSelectedItem().toString() : "Tất cả";
        model.setRowCount(0);
        int stt = 1;
        for (ProductDTO p : list) {
            String productName = p.getName().toLowerCase();
            String supplierName = p.getSupplier().getName();
            if (!productName.contains(keyword)) continue;
            if (!selectedSupplier.equals("Tất cả") && !supplierName.equals(selectedSupplier)) continue;
            long quantity = p.getTotalQuantity();
            long minStock = p.getMinStockLevel();
            String status = (quantity == 0) ? "Hết hàng" : (quantity < minStock) ? "Gần hết" : "Còn hàng";
            if (!selectedStatus.equals("Tất cả") && !status.equals(selectedStatus)) continue;
            model.addRow(new Object[]{
                    loadProductIcon(p.getImagePath()), stt++, p.getCode(), p.getName(), quantity, supplierName, status
            });
        }
    }

    private ImageIcon loadProductIcon(String path) {
        if (path == null || path.isEmpty()) return null;
        String normalized = path.replace('\\', '/');
        java.io.File file = new java.io.File(normalized);
        try {
            ImageIcon icon;
            if (file.exists()) {
                icon = new ImageIcon(file.getAbsolutePath());
            } else {
                java.net.URL url = getClass().getResource("/" + normalized);
                if (url != null) icon = new ImageIcon(url); else return null;
            }
            Image img = icon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
            return new ImageIcon(img);
        } catch (Exception e) { return null; }
    }

    private void loadSuppliers() {
        ProductBUS bus = new ProductBUS();
        ArrayList<ProductDTO> list = bus.getAllProducts();
        cbSupplier.removeAllItems();
        cbSupplier.addItem("Tất cả");
        for (ProductDTO p : list) {
            String supplierName = p.getSupplier().getName();
            boolean exists = false;
            for (int i = 0; i < cbSupplier.getItemCount(); i++) {
                if (cbSupplier.getItemAt(i).equals(supplierName)) { exists = true; break; }
            }
            if (!exists) cbSupplier.addItem(supplierName);
        }
    }

    private void loadStatusFilter() {
        cbStatus.removeAllItems();
        cbStatus.addItem("Tất cả");
        cbStatus.addItem("Còn hàng");
        cbStatus.addItem("Gần hết");
        cbStatus.addItem("Hết hàng");
        cbStatus.setSelectedIndex(0);
    }
}
