package GUI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import BUS.ProductBUS;
import DTO.ProductDTO;

import java.awt.*;
import java.util.ArrayList;

public class KhoPanel extends JPanel {
    private DefaultTableModel model;
    public KhoPanel() {
    setLayout(new BorderLayout());
    setBackground(new Color(0xF8F7FF));

    JLabel title = new JLabel("Quản Lý Kho", SwingConstants.CENTER);
    title.setFont(new Font("Playfair Display", Font.BOLD, 32));
    add(title, BorderLayout.NORTH);

    String[] headers = {"Hình ảnh","STT", "Mã SP", "Tên SP", "SL", "Nhà cung cấp","Trạng thái"};

    model = new DefaultTableModel(headers, 0);
    JTable table = new JTable(model);
    table.setRowHeight(60);

    add(new JScrollPane(table), BorderLayout.CENTER);

    loadData(); // 👈 gọi load data
}
   private void loadData() {
        ProductBUS bus = new ProductBUS();
        ArrayList<ProductDTO> list = bus.getAllProducts();

        model.setRowCount(0); // clear bảng

        int stt = 1;
        for (ProductDTO p : list) {

            model.addRow(new Object[]{
                    "",
                    stt++,
                    p.getCode(),
                    p.getName(),
                    0,
                    p.getSupplier().getName(),
                    0,
                
            });
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