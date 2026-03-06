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

public class KhoPanel extends JPanel {
    private JComboBox<String> cbStatus;
    private JTextField txtSearch;
    private JComboBox<String> cbSupplier;
    private JTable table;
    private DefaultTableModel model;

    public KhoPanel() {
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
        JLabel hdrTitle = new JLabel("QU\u1ea2N L\u00dd KHO");
        hdrTitle.setFont(new Font("Arial", Font.BOLD, 20));
        header.add(hdrTitle);

        // ── TOP TOOLBAR (2 rows) ─────────────────────────────────────────────
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBackground(new Color(0xF8F7FF));
        topPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0xCCCCCC)),
                BorderFactory.createEmptyBorder(8, 16, 8, 16)));

        // Row 1: search + filters
        JPanel khoRow1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));
        khoRow1.setBackground(new Color(0xF8F7FF));

        JLabel lbSearch = new JLabel("T\u00ecm ki\u1ebfm:");
        lbSearch.setFont(new Font("Arial", Font.PLAIN, 13));
        txtSearch = new JTextField();
        txtSearch.setPreferredSize(new Dimension(200, 36));
        txtSearch.setFont(new Font("Arial", Font.PLAIN, 13));
        txtSearch.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xBBBBBB), 1),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)));

        JLabel lbNCC = new JLabel("Nh\u00e0 cung c\u1ea5p:");
        lbNCC.setFont(new Font("Arial", Font.PLAIN, 13));
        cbSupplier = new JComboBox<>();
        cbSupplier.setPreferredSize(new Dimension(160, 36));
        UIUtils.styleComboBox(cbSupplier);

        JLabel lbStatus = new JLabel("Tr\u1ea1ng th\u00e1i:");
        lbStatus.setFont(new Font("Arial", Font.PLAIN, 13));
        cbStatus = new JComboBox<>();
        cbStatus.setPreferredSize(new Dimension(140, 36));
        UIUtils.styleComboBox(cbStatus);

        khoRow1.add(lbSearch); khoRow1.add(txtSearch);
        khoRow1.add(Box.createHorizontalStrut(6));
        khoRow1.add(lbNCC); khoRow1.add(cbSupplier);
        khoRow1.add(Box.createHorizontalStrut(6));
        khoRow1.add(lbStatus); khoRow1.add(cbStatus);

        // Row 2: export buttons
        JPanel khoRow2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        khoRow2.setBackground(new Color(0xF8F7FF));

        JButton btnPDF    = ExportUtils.makeExportButton("Xu\u1ea5t PDF",   new Color(0x7B52AB));
        JButton btnExcel  = ExportUtils.makeExportButton("Xu\u1ea5t Excel", new Color(0x2E7D32));
        JButton btnImport = ExportUtils.makeImportButton("Nh\u1eadp CSV");
        btnPDF.addActionListener(e -> ExportUtils.xuatPDF(this, model, "Danh s\u00e1ch kho"));
        btnExcel.addActionListener(e -> ExportUtils.xuatCSV(this, model, "kho"));
        btnImport.addActionListener(e -> {
            List<String[]> rows = ExportUtils.importCSV(this);
            if (rows == null) return;
            for (String[] r : rows) { if (r.length < 7) continue; model.addRow((Object[])r); }
        });
        khoRow2.add(btnPDF); khoRow2.add(btnExcel); khoRow2.add(btnImport);

        topPanel.add(khoRow1);
        topPanel.add(khoRow2);

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
                if (columnIndex == 0)
                    return ImageIcon.class;
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

                        super.getTableCellRendererComponent(
                                table, value, isSelected, hasFocus, row, column);

                        String status = value.toString();

                        // 🔹 Giữ màu dòng khi được chọn
                        if (isSelected) {
                            setBackground(table.getSelectionBackground());
                            setForeground(table.getSelectionForeground());
                        } else {
                            // 🔹 Giữ zebra row giống các cột khác
                            if (row % 2 == 0) {
                                setBackground(new Color(245, 245, 250));
                            } else {
                                setBackground(new Color(230, 230, 240));
                            }

                            // 🔹 Đổi màu chữ theo trạng thái
                            if (status.equals("Hết hàng")) {
                                setForeground(Color.RED);
                            } else if (status.equals("Gần hết")) {
                                setForeground(new Color(255, 140, 0));
                            } else {
                                setForeground(new Color(0, 128, 0));
                            }
                        }

                        setHorizontalAlignment(SwingConstants.CENTER);
                        return this;
                    }

                });
        // ====== TABLE STYLE ======
        table.setRowHeight(55);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(new Color(0xDCD6F7));
        table.setSelectionForeground(Color.BLACK);

        // ====== HEADER STYLE ======
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(0xAF9FCB));
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setPreferredSize(new Dimension(100, 40));

        // ====== ZEBRA ROW COLOR ======
        table.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {

                JLabel c = (JLabel) super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);

                if (value instanceof ImageIcon) {
                    c.setIcon((ImageIcon) value);
                    c.setText("");
                }

                if (!isSelected) {
                    if (row % 2 == 0) {
                        c.setBackground(new Color(0xF8F7FF));
                    } else {
                        c.setBackground(new Color(0xECE9F9));
                    }
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
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                loadData();
            }

            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                loadData();
            }

            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                loadData();
            }

        });

        // Khi đổi nhà cung cấp
        cbSupplier.addActionListener(e -> loadData());
        cbStatus.addActionListener(e -> loadData());
    }

    private void loadData() {
        ProductBUS bus = new ProductBUS();
        ArrayList<ProductDTO> list = bus.getAllProducts();

        String keyword = txtSearch.getText().trim().toLowerCase();
        String selectedSupplier = cbSupplier.getSelectedItem().toString();
        String selectedStatus = "Tất cả";

        if (cbStatus.getSelectedItem() != null) {
            selectedStatus = cbStatus.getSelectedItem().toString();
        }
        model.setRowCount(0);
        int stt = 1;

        for (ProductDTO p : list) {

            String productName = p.getName().toLowerCase();
            String supplierName = p.getSupplier().getName();

            // 🔎 lọc theo tên
            if (!productName.contains(keyword))
                continue;

            // 📦 lọc theo nhà cung cấp
            if (!selectedSupplier.equals("Tất cả")
                    && !supplierName.equals(selectedSupplier))
                continue;

            // 👉 tính trạng thái
            long quantity = p.getTotalQuantity();
            long minStock = p.getMinStockLevel();

            String status;
            if (quantity == 0)
                status = "Hết hàng";
            else if (quantity < minStock)
                status = "Gần hết";
            else
                status = "Còn hàng";

            // 🔥 Lọc theo trạng thái
            if (!selectedStatus.equals("Tất cả")
                    && !status.equals(selectedStatus))
                continue;

            model.addRow(new Object[] {
                    loadProductIcon(p.getImagePath()),
                    stt++,
                    p.getCode(),
                    p.getName(),
                    quantity,
                    supplierName,
                    status
            });
        }
    }

    private ImageIcon loadProductIcon(String path) {
        if (path == null || path.isEmpty())
            return null;

        String normalized = path.replace('\\', '/');
        java.io.File file = new java.io.File(normalized);
        System.out.println("[debug] path='" + path + "' normalized='" + normalized + "' exists=" + file.exists());

        try {
            ImageIcon icon;
            if (file.exists()) {
                icon = new ImageIcon(file.getAbsolutePath());
            } else {
                java.net.URL url = getClass().getResource("/" + normalized);
                System.out.println("[debug] resource url=" + url);
                if (url != null) {
                    icon = new ImageIcon(url);
                } else {
                    return null;
                }
            }
            Image img = icon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
            return new ImageIcon(img);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
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
                if (cbSupplier.getItemAt(i).equals(supplierName)) {
                    exists = true;
                    break;
                }
            }

            if (!exists) {
                cbSupplier.addItem(supplierName);
            }
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

    public static void main(String[] args) {
        JFrame f = new JFrame("Quản Lý Kho");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setSize(900, 600);
        f.setLocationRelativeTo(null);
        f.setContentPane(new KhoPanel());
        f.setVisible(true);
    }
}

