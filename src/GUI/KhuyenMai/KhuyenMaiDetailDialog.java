package GUI.KhuyenMai;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.Dialog;
import BUS.DiscountBUS;
import DTO.DiscountDTO;

/** Dialog chi tiết khuyến mãi — tách từ KhuyenMaiPanel.showDetailDialog */
class KhuyenMaiDetailDialog {

    private static final Color TBL_HDR = new Color(0xAF9FCB);

    static void show(Component parent, DiscountBUS discountBUS, DefaultTableModel tableModel, int modelRow) {
        Object idObj = tableModel.getValueAt(modelRow, 0);
        if (idObj == null) return;
        int id;
        try { id = Integer.parseInt(idObj.toString()); } catch (Exception ex) { return; }

        DiscountDTO d = discountBUS.getDiscountById(id);
        if (d == null) return;

        Window owner = SwingUtilities.getWindowAncestor(parent);
        JDialog dlg = new JDialog(owner, "Chi tiết khuyến mãi", Dialog.ModalityType.APPLICATION_MODAL);
        dlg.setSize(500, 420);
        dlg.setLocationRelativeTo(parent);
        dlg.setResizable(false);
        dlg.setLayout(new BorderLayout());

        JPanel hdr = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 14));
        hdr.setBackground(TBL_HDR);
        JLabel lbl = new JLabel("Thông tin khuyến mãi");
        lbl.setFont(new Font("Arial", Font.BOLD, 18));
        lbl.setForeground(Color.WHITE);
        hdr.add(lbl);
        dlg.add(hdr, BorderLayout.NORTH);

        String[] labels = { "Mã:", "Tên:", "Mô tả:", "Giá trị giảm:", "Loại giảm:",
                             "Ngày bắt đầu:", "Ngày kết thúc:", "Min order:", "Trạng thái:" };
        Object[] values = { d.getId(), d.getName(), d.getDescription(), d.getValue(),
                             d.getDiscountType().name(), d.getStartDate(), d.getEndDate(),
                             d.getMinOrderAmount(), d.getStatus() != null ? d.getStatus().name() : "-" };
        JPanel body = new JPanel(new GridLayout(labels.length, 2, 10, 10));
        body.setBackground(new Color(0xF0EFF8));
        body.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        for (int i = 0; i < labels.length; i++) {
            JLabel l = new JLabel(labels[i]); l.setFont(new Font("Arial", Font.BOLD, 14));
            JLabel v = new JLabel(values[i] == null ? "-" : values[i].toString()); v.setFont(new Font("Arial", Font.PLAIN, 14));
            body.add(l); body.add(v);
        }
        dlg.add(new JScrollPane(body) {{ setBorder(null); }}, BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 12));
        footer.setBackground(new Color(0xF0EFF8));
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(0xCCCCCC)));
        JButton btnDong = new JButton("Đóng");
        btnDong.setBackground(new Color(0x9B8EA8)); btnDong.setForeground(Color.WHITE);
        btnDong.setFont(new Font("Arial", Font.BOLD, 13));
        btnDong.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        btnDong.setOpaque(true); btnDong.setBorderPainted(false); btnDong.setFocusPainted(false);
        btnDong.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnDong.addActionListener(e -> dlg.dispose());
        footer.add(btnDong);
        dlg.add(footer, BorderLayout.SOUTH);
        dlg.setVisible(true);
    }
}
