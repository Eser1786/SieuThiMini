package GUI.Kho;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import BUS.ProductBUS;
import DTO.ProductDTO;

import java.awt.*;
import java.util.ArrayList;

public class KhoPanel extends JPanel {
    private JComboBox<String> cbStatus;
    private JTextField txtSearch;
    private JComboBox<String> cbSupplier;
    private JTable table;
    private DefaultTableModel model;

    public KhoPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(0xF3F0FF));
        setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        JLabel title = new JLabel("Quản Lý Kho", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(new Color(0x4B3F72));
        add(title, BorderLayout.NORTH);

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 25));
        topPanel.setBackground(new Color(0xF8F7FF));
        topPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0xCCCCCC)));

        txtSearch = new JTextField();
        txtSearch.setPreferredSize(new Dimension(229, 42));
        txtSearch.setFont(new Font("Arial", Font.PLAIN, 22));
        txtSearch.setBackground(new Color(0xD9D9D9));
        txtSearch.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 4));

        cbSupplier = new JComboBox<>();
        cbSupplier.setPreferredSize(new Dimension(220, 42));
        cbSupplier.setFont(new Font("Arial", Font.PLAIN, 22));
        cbSupplier.setBackground(new Color(0xD9D9D9));

        cbStatus = new JComboBox<>();
        cbStatus.setPreferredSize(new Dimension(220, 42));
        cbStatus.setFont(new Font("Arial", Font.PLAIN, 22));
        cbStatus.setBackground(new Color(0xD9D9D9));

        topPanel.add(new JLabel("Tìm kiếm:"));
        topPanel.add(txtSearch);

        topPanel.add(new JLabel("Nhà cung cấp:"));
        topPanel.add(cbSupplier);

        topPanel.add(new JLabel("Trạng thái:"));
        topPanel.add(cbStatus);
        add(topPanel, BorderLayout.NORTH);
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

