package GUI.KhuyenMai;

import com.toedter.calendar.JDateChooser;
import java.text.SimpleDateFormat;
import javax.swing.*;
import java.awt.*;
import java.awt.Dialog;
import BUS.DiscountBUS;
import GUI.UIUtils;

/** Dialog thêm khuyến mãi — tách từ KhuyenMaiPanel.showAddDiscountDialog */
class KhuyenMaiAddDialog {

    private static final Color ACCENT  = new Color(0x5C4A7F);
    private static final Color TBL_HDR = new Color(0xAF9FCB);

    static void show(Component parent, DiscountBUS discountBUS, Runnable onSuccess) {
        Window owner = SwingUtilities.getWindowAncestor(parent);
        JDialog dlg = new JDialog(owner, "Thêm khuyến mãi", Dialog.ModalityType.APPLICATION_MODAL);
        dlg.setSize(520, 400);
        dlg.setLocationRelativeTo(parent);
        dlg.setResizable(false);
        dlg.setLayout(new BorderLayout());

        JPanel hdr = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 14));
        hdr.setBackground(TBL_HDR);
        JLabel lbl = new JLabel("Thêm khuyến mãi mới");
        lbl.setFont(new Font("Arial", Font.BOLD, 18));
        lbl.setForeground(Color.WHITE);
        hdr.add(lbl);
        dlg.add(hdr, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(new Color(0xF0EFF8));
        form.setBorder(BorderFactory.createEmptyBorder(16, 24, 16, 24));
        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.HORIZONTAL;
        g.insets = new Insets(6, 6, 6, 6);
        Font lf = new Font("Arial", Font.BOLD, 13);
        Dimension fd = new Dimension(200, 32);

        JTextField tfName     = UIUtils.makeField(); tfName.setPreferredSize(fd);
        JTextField tfDesc     = UIUtils.makeField(); tfDesc.setPreferredSize(fd);
        JTextField tfValue    = UIUtils.makeField(); tfValue.setPreferredSize(fd);
        JTextField tfMinOrder = UIUtils.makeField(); tfMinOrder.setPreferredSize(fd);
        JComboBox<String> cbType = new JComboBox<>(new String[]{"PERCENT", "FIXED"});
        cbType.setPreferredSize(fd);
        UIUtils.styleComboBox(cbType);
        JDateChooser dcStart = new JDateChooser(); dcStart.setDateFormatString("dd/MM/yyyy"); dcStart.setPreferredSize(fd);
        JDateChooser dcEnd   = new JDateChooser(); dcEnd.setDateFormatString("dd/MM/yyyy");   dcEnd.setPreferredSize(fd);

        Object[][] rows = {
            { "Tên:", tfName,     "Giá trị:", tfValue },
            { "Mô tả:", tfDesc,   "Loại giảm:", cbType },
            { "Ngày bắt đầu:", dcStart, "Ngày kết thúc:", dcEnd },
            { "Min order:", tfMinOrder, null, null }
        };
        for (int i = 0; i < rows.length; i++) {
            g.gridy = i;
            g.gridx = 0; g.weightx = 0; JLabel l1 = new JLabel((String) rows[i][0]); l1.setFont(lf); form.add(l1, g);
            g.gridx = 1; g.weightx = 1; form.add((java.awt.Component) rows[i][1], g);
            if (rows[i][2] != null) {
                g.gridx = 2; g.weightx = 0; JLabel l2 = new JLabel((String) rows[i][2]); l2.setFont(lf); form.add(l2, g);
                g.gridx = 3; g.weightx = 1; form.add((java.awt.Component) rows[i][3], g);
            }
        }
        dlg.add(form, BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 12));
        footer.setBackground(new Color(0xF0EFF8));
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(0xCCCCCC)));
        JButton btnLuu = new JButton("Lưu");
        JButton btnHuy = new JButton("Hủy");
        styleBtn(btnLuu, ACCENT);
        styleBtn(btnHuy, new Color(0x9B8EA8));
        btnHuy.addActionListener(e -> dlg.dispose());
        btnLuu.addActionListener(e -> {
            try {
                if (dcStart.getDate() == null || dcEnd.getDate() == null) {
                    JOptionPane.showMessageDialog(dlg, "Vui lòng chọn ngày bắt đầu và kết thúc");
                    return;
                }
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String result = discountBUS.addDiscount(
                        tfName.getText().trim(), tfDesc.getText().trim(),
                        tfValue.getText().trim(), cbType.getSelectedItem().toString(),
                        sdf.format(dcStart.getDate()), sdf.format(dcEnd.getDate()),
                        tfMinOrder.getText().trim());
                if ("SUCCESS".equals(result)) {
                    JOptionPane.showMessageDialog(dlg, "Thêm thành công!");
                    if (onSuccess != null) onSuccess.run();
                    dlg.dispose();
                } else {
                    JOptionPane.showMessageDialog(dlg, result);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dlg, "Lỗi hệ thống", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        footer.add(btnLuu); footer.add(btnHuy);
        dlg.add(footer, BorderLayout.SOUTH);
        dlg.setVisible(true);
    }

    private static void styleBtn(JButton btn, Color bg) {
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Arial", Font.BOLD, 13));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
}
