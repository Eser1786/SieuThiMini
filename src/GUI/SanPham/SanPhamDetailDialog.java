package GUI.SanPham;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.Dialog;
import GUI.UIUtils;

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

        JButton btnSua = new JButton("✏ Sửa");
        styleBtn(btnSua, new Color(0x6677C8), 110, 40);
        btnSua.addActionListener(e -> { detail.dispose(); showEdit(parent, modelRow, model, bang); });

        JButton btnXoa = new JButton("🗑 Xóa");
        styleBtn(btnXoa, new Color(0xB83434), 110, 40);
        btnXoa.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(detail,
                "Bạn có chắc muốn xóa sản phẩm \"" + model.getValueAt(modelRow, 2) + "\"?",
                "Xác nhận xóa", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (confirm == JOptionPane.YES_OPTION) { model.removeRow(modelRow); detail.dispose(); }
        });

        footer.add(btnSua); footer.add(btnXoa);
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

        JTextField[] flds = new JTextField[lbls.length];
        for (int i = 0; i < lbls.length; i++) {
            flds[i] = UIUtils.makeField();
            Object v = model.getValueAt(modelRow, colIdx[i]);
            flds[i].setText(v == null ? "" : v.toString());
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
            flds[i].setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
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
            for (int i = 0; i < colIdx.length; i++)
                model.setValueAt(flds[i].getText(), modelRow, colIdx[i]);
            try {
                int sl = Integer.parseInt(flds[7].getText().trim());
                model.setValueAt(sl > 0 ? "Còn hàng" : "Hết hàng", modelRow, 5);
            } catch (NumberFormatException ignore) {}
            bang.repaint();
            popup.dispose();
        });

        footer.add(btnHuy); footer.add(btnLuu);
        popup.add(footer, BorderLayout.SOUTH);
        popup.setVisible(true);
    }

    private static void styleBtn(JButton b, Color bg, int w, int h) {
        b.setFont(new Font("Arial", Font.BOLD, 15));
        b.setBackground(bg); b.setForeground(Color.WHITE);
        b.setFocusPainted(false); b.setBorderPainted(false);
        b.setPreferredSize(new Dimension(w, h));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
}
