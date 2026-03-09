package GUI.SanPham;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.Dialog;
import GUI.UIUtils;
import BUS.CategoryBUS;
import BUS.SupplierBUS;
import DTO.CategoryDTO;
import DTO.SupplierDTO;
import java.util.ArrayList;

/** Dialog chi tiết + sửa San Pham — tách từ SanPhamPanel */
class SanPhamDetailDialog {

    static void showDetail(Component parent, int modelRow, DefaultTableModel model, JTable bang) {
        Window owner = SwingUtilities.getWindowAncestor(parent);
        JDialog detail = new JDialog(owner, "Chi tiết sản phẩm", Dialog.ModalityType.APPLICATION_MODAL);
        detail.setSize(520, 640);
        detail.setLocationRelativeTo(parent);
        detail.setResizable(false);
        detail.getContentPane().setBackground(new Color(0xF0EFF8));
        detail.setLayout(new BorderLayout(0, 0));

        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 14));
        header.setBackground(new Color(0xAF9FCB));
        JLabel lblTitle = new JLabel("Thông tin sản phẩm");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 20)); lblTitle.setForeground(Color.WHITE);
        header.add(lblTitle);
        detail.add(header, BorderLayout.NORTH);

        String[] lbls = {
            "Mã SP", "Tên sản phẩm", "Mô tả", "Nhà cung cấp", "Danh mục",
            "Giá vốn", "Giá bán", "Số lượng", "Tồn kho tối thiểu",
            "Xuất xứ", "Ngày sản xuất", "Ngày hết hạn",
            "Vị trí", "Đơn vị", "Trạng thái"
        };
        int[] colIdx = { 0, 2, 9, 10, 11, 12, 3, 4, 13, 14, 15, 6, 16, 17, 18 };

        JPanel body = new JPanel(new GridLayout(lbls.length, 2, 12, 10));
        body.setBackground(new Color(0xF0EFF8));
        body.setBorder(BorderFactory.createEmptyBorder(16, 36, 16, 36));
        for (int i = 0; i < lbls.length; i++) {
            JLabel lbl = new JLabel(lbls[i] + ":");
            lbl.setFont(new Font("Arial", Font.BOLD, 15));
            Object v = model.getValueAt(modelRow, colIdx[i]);
            JLabel val = new JLabel(v == null ? "-" : v.toString());
            val.setFont(new Font("Arial", Font.PLAIN, 15));
            body.add(lbl); body.add(val);
        }
        JScrollPane scrollDetail = new JScrollPane(body);
        scrollDetail.setBorder(null);
        scrollDetail.getVerticalScrollBar().setUnitIncrement(16);
        detail.add(scrollDetail, BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 14));
        footer.setBackground(new Color(0xF0EFF8));
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(0xCCCCCC)));

        JButton btnSua = new JButton("Sửa");
        styleBtn(btnSua, new Color(0x6677C8), 110, 40);
        btnSua.addActionListener(e -> { detail.dispose(); showEdit(parent, modelRow, model, bang); });

        JButton btnXoa = new JButton("Xóa");
        styleBtn(btnXoa, new Color(0xB83434), 110, 40);
        btnXoa.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(detail,
                "Bạn có chắc muốn xóa sản phẩm \"" + model.getValueAt(modelRow, 2) + "\"?",
                "Xác nhận xóa", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (confirm == JOptionPane.YES_OPTION) { model.removeRow(modelRow); detail.dispose(); }
        });

        JButton btnDong = new JButton("Đóng");
        styleBtn(btnDong, new Color(0x9B8EA8), 110, 40);
        btnDong.addActionListener(e -> detail.dispose());

        footer.add(btnSua); footer.add(btnXoa); footer.add(btnDong);
        detail.add(footer, BorderLayout.SOUTH);
        detail.setVisible(true);
    }

    static void showEdit(Component parent, int modelRow, DefaultTableModel model, JTable bang) {
        Window owner = SwingUtilities.getWindowAncestor(parent);
        JDialog popup = new JDialog(owner, "Sửa sản phẩm", Dialog.ModalityType.APPLICATION_MODAL);
        popup.setSize(580, 640);
        popup.setLocationRelativeTo(parent);
        popup.setResizable(false);
        popup.getContentPane().setBackground(new Color(0xF0EFF8));
        popup.setLayout(new BorderLayout(0, 0));

        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 14));
        header.setBackground(new Color(0x6677C8));
        JLabel lblTitle = new JLabel("Chỉnh sửa thông tin");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 20)); lblTitle.setForeground(Color.WHITE);
        header.add(lblTitle);
        popup.add(header, BorderLayout.NORTH);

        String[] lbls = {
            "Mã SP", "Tên sản phẩm", "Mô tả", "Nhà cung cấp", "Danh mục",
            "Giá vốn", "Giá bán", "Số lượng", "Tồn kho tối thiểu",
            "Xuất xứ", "Ngày sản xuất", "Ngày hết hạn",
            "Vị trí", "Đơn vị", "Trạng thái", "Khuyến mãi"
        };
        int[] colIdx = { 0, 2, 9, 10, 11, 12, 3, 4, 13, 14, 15, 6, 16, 17, 18, 7 };

        JComponent[] flds = new JComponent[lbls.length];
        for (int i = 0; i < lbls.length; i++) {
            Object v = model.getValueAt(modelRow, colIdx[i]);
            String text = v == null ? "" : v.toString();
            
            if (i == 0) { // Mã SP - read-only
                JTextField tf = UIUtils.makeField();
                tf.setText(text);
                tf.setEditable(false);
                tf.setBackground(new Color(0xE8E8E8));
                flds[i] = tf;
            } else if (i == 3) { // Nhà cung cấp - combo box
                JComboBox<String> cb = new JComboBox<>();
                cb.setFont(new Font("Arial", Font.PLAIN, 14));
                cb.setBackground(Color.WHITE);
                try {
                    ArrayList<SupplierDTO> suppliers = new SupplierBUS().getAllSuppliers();
                    for (SupplierDTO s : suppliers) {
                        cb.addItem(s.getName());
                    }
                    cb.setSelectedItem(text);
                } catch (Exception e) {
                    cb.addItem(text);
                }
                flds[i] = cb;
            } else if (i == 4) { // Danh mục - combo box
                JComboBox<String> cb = new JComboBox<>();
                cb.setFont(new Font("Arial", Font.PLAIN, 14));
                cb.setBackground(Color.WHITE);
                try {
                    ArrayList<CategoryDTO> categories = new CategoryBUS().getAllCategories();
                    for (CategoryDTO c : categories) {
                        cb.addItem(c.getName());
                    }
                    cb.setSelectedItem(text);
                } catch (Exception e) {
                    cb.addItem(text);
                }
                flds[i] = cb;
            } else if (i == 7) { // Số lượng - read-only
                JTextField tf = UIUtils.makeField();
                tf.setText(text);
                tf.setEditable(false);
                tf.setBackground(new Color(0xE8E8E8));
                flds[i] = tf;
            } else if (i == 14) { // Trạng thái - combo box
                JComboBox<String> cb = new JComboBox<>();
                cb.setFont(new Font("Arial", Font.PLAIN, 14));
                cb.setBackground(Color.WHITE);
                cb.addItem("Còn hàng");
                cb.addItem("Hết hàng");
                cb.addItem("Ngừng kinh doanh");
                cb.setSelectedItem(text);
                flds[i] = cb;
            } else if (i == 5 || i == 6) { // Giá vốn và giá bán - với validation
                JTextField tf = UIUtils.makeField();
                tf.setText(text);
                final int fieldIndex = i;
                tf.addFocusListener(new java.awt.event.FocusAdapter() {
                    public void focusLost(java.awt.event.FocusEvent evt) {
                        validatePrices(flds, fieldIndex);
                    }
                });
                flds[i] = tf;
            } else { // Các field khác - text field
                JTextField tf = UIUtils.makeField();
                tf.setText(text);
                flds[i] = tf;
            }
        }

        JPanel formBody = new JPanel();
        formBody.setLayout(new BoxLayout(formBody, BoxLayout.Y_AXIS));
        formBody.setBackground(new Color(0xF0EFF8));
        formBody.setBorder(BorderFactory.createEmptyBorder(10, 36, 10, 36));
        for (int i = 0; i < lbls.length; i++) {
            JLabel lbl = new JLabel(lbls[i]);
            lbl.setFont(new Font("Arial", Font.BOLD, 15));
            lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
            flds[i].setAlignmentX(Component.LEFT_ALIGNMENT);
            if (flds[i] instanceof JTextField) {
                flds[i].setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
            } else if (flds[i] instanceof JComboBox) {
                flds[i].setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
            }
            formBody.add(lbl);
            formBody.add(flds[i]);
            formBody.add(Box.createVerticalStrut(8));
        }
        JScrollPane scrollEdit = new JScrollPane(formBody);
        scrollEdit.setBorder(null);
        scrollEdit.getVerticalScrollBar().setUnitIncrement(16);
        popup.add(scrollEdit, BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 14));
        footer.setBackground(new Color(0xF0EFF8));
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(0xCCCCCC)));

        JButton btnHuy = new JButton("Hủy");
        styleBtn(btnHuy, new Color(0x9B8EA8), 100, 40);
        btnHuy.addActionListener(e -> {
            int cf = JOptionPane.showConfirmDialog(popup,
                    "Bạn có chắc muốn hủy? Thay đổi chưa lưu sẽ bị mất.",
                    "Xác nhận hủy", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (cf == JOptionPane.YES_OPTION) popup.dispose();
        });

        JButton btnLuu = new JButton("Lưu");
        styleBtn(btnLuu, new Color(0xB83434), 100, 40);
        btnLuu.addActionListener(e -> {
            // Validate prices before saving
            if (!validatePrices(flds, -1)) {
                return; // Don't save if validation fails
            }
            
            for (int i = 0; i < colIdx.length; i++) {
                String value;
                if (flds[i] instanceof JTextField) {
                    value = ((JTextField) flds[i]).getText();
                } else if (flds[i] instanceof JComboBox) {
                    value = ((JComboBox<?>) flds[i]).getSelectedItem().toString();
                } else {
                    value = "";
                }
                model.setValueAt(value, modelRow, colIdx[i]);
            }
            try {
                String slText = "";
                if (flds[7] instanceof JTextField) {
                    slText = ((JTextField) flds[7]).getText().trim();
                }
                int sl = Integer.parseInt(slText);
                model.setValueAt(sl > 0 ? "Còn hàng" : "Hết hàng", modelRow, 5);
            } catch (NumberFormatException ignore) {}
            bang.repaint();
            popup.dispose();
        });

        footer.add(btnHuy); footer.add(btnLuu);
        popup.add(footer, BorderLayout.SOUTH);
        popup.setVisible(true);
    }

    private static boolean validatePrices(JComponent[] flds, int currentFieldIndex) {
        try {
            String costPriceText = "";
            String sellingPriceText = "";
            
            if (flds[5] instanceof JTextField) {
                costPriceText = ((JTextField) flds[5]).getText().trim();
            }
            if (flds[6] instanceof JTextField) {
                sellingPriceText = ((JTextField) flds[6]).getText().trim();
            }
            
            if (!costPriceText.isEmpty() && !sellingPriceText.isEmpty()) {
                double costPrice = Double.parseDouble(costPriceText);
                double sellingPrice = Double.parseDouble(sellingPriceText);
                
                if (sellingPrice <= costPrice) {
                    JOptionPane.showMessageDialog(null,
                        "Giá bán phải lớn hơn giá vốn!",
                        "Lỗi nhập liệu",
                        JOptionPane.ERROR_MESSAGE);
                    
                    // Focus back to the field that was just edited
                    if (currentFieldIndex == 5) {
                        ((JTextField) flds[5]).requestFocus();
                    } else if (currentFieldIndex == 6) {
                        ((JTextField) flds[6]).requestFocus();
                    }
                    
                    return false;
                }
            }
        } catch (NumberFormatException e) {
            // If parsing fails, let it go - will be handled elsewhere
        }
        return true;
    }

    private static void styleBtn(JButton b, Color bg, int w, int h) {
        b.setFont(new Font("Arial", Font.BOLD, 15));
        b.setBackground(bg); b.setForeground(Color.WHITE);
        b.setFocusPainted(false); b.setBorderPainted(false);
        b.setPreferredSize(new Dimension(w, h));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
}
