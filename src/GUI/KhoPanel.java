package GUI;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import BUS.ProductBUS;
import DTO.ProductDTO;

import java.awt.*;
import java.util.ArrayList;

public class KhoPanel extends JPanel {
    private JTextField txtSearch;
    private JComboBox<String> cbSupplier;
    private JTable table;
    private DefaultTableModel model;
    public KhoPanel() {
    setLayout(new BorderLayout(15,15));
    setBackground(new Color(0xF3F0FF));
    setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

    JLabel title = new JLabel("Quản Lý Kho", SwingConstants.CENTER);
    title.setFont(new Font("Segoe UI", Font.BOLD, 28));
    title.setForeground(new Color(0x4B3F72));
    add(title, BorderLayout.NORTH);
            setLayout(new BorderLayout());

JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
topPanel.setBackground(new Color(0xF8F7FF));

txtSearch = new JTextField(15);
cbSupplier = new JComboBox<>();

topPanel.add(new JLabel("Tìm kiếm:"));
topPanel.add(txtSearch);

topPanel.add(new JLabel("Nhà cung cấp:"));
topPanel.add(cbSupplier);

add(topPanel, BorderLayout.NORTH);
    String[] headers = {"Hình ảnh","STT","Mã SP","Tên SP","SL","Nhà cung cấp","Trạng thái"};

    model = new DefaultTableModel(headers, 0);
    JTable table = new JTable(model);
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
            } 
            else {
                // 🔹 Giữ zebra row giống các cột khác
                if (row % 2 == 0) {
                    setBackground(new Color(245, 245, 250));
                } else {
                    setBackground(new Color(230, 230, 240));
                }

                // 🔹 Đổi màu chữ theo trạng thái
                if (status.equals("Hết hàng")) {
                    setForeground(Color.RED);
                } 
                else if (status.equals("Gần hết")) {
                    setForeground(new Color(255,140,0));
                } 
                else {
                    setForeground(new Color(0,128,0));
                }
            }

            setHorizontalAlignment(SwingConstants.CENTER);
            return this;
        }
        
    }
);
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
    table.getTableHeader().setBackground(new Color(0x6C5CE7));
    table.getTableHeader().setForeground(Color.WHITE);
    table.getTableHeader().setPreferredSize(new Dimension(100, 40));

    // ====== ZEBRA ROW COLOR ======
    table.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {

            Component c = super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);

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

    loadData();
    loadSuppliers();
}
  private void loadData() {
    ProductBUS bus = new ProductBUS();
    ArrayList<ProductDTO> list = bus.getAllProducts();

    model.setRowCount(0);

    int stt = 1;
    for (ProductDTO p : list) {

        long quantity = p.getTotalQuantity();
        long minStock = p.getMinStockLevel();

        String status;

        if (quantity == 0) {
            status = "Hết hàng";
        } else if (quantity < minStock) {
            status = "Gần hết";
        } else {
            status = "Còn hàng";
        }

        model.addRow(new Object[]{
                "",
                stt++,
                p.getCode(),
                p.getName(),
                quantity,
                p.getSupplier() != null ? p.getSupplier().getName() : "",
                status
        });
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
    public static void main(String[] args) {
        JFrame f = new JFrame("Quản Lý Kho");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setSize(900, 600);
        f.setLocationRelativeTo(null);
        f.setContentPane(new KhoPanel());
        f.setVisible(true);
    }
}