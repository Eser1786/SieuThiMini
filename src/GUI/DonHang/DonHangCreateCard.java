package GUI.DonHang;

import GUI.UIUtils;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;

class DonHangCreateCard extends JPanel {

    private final DonHangPanel parent;

    DonHangCreateCard(DonHangPanel parent) {
        this.parent = parent;
        setLayout(new BorderLayout());
        buildUI();
    }

    private void buildUI() {
        setBackground(new Color(0xF0EFF8));

        /* Header */
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(0xF0EFF8));
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0xCCCCCC)),
                BorderFactory.createEmptyBorder(10, 16, 10, 16)));
        JLabel lblTitle = new JLabel("+ Tạo đơn hàng mới");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitle.setForeground(new Color(0x444444));
        JButton btnBack = new JButton("← Quay lại");
        btnBack.setFont(new Font("Arial", Font.BOLD, 14));
        btnBack.setBackground(new Color(0x9B8EA8));
        btnBack.setForeground(Color.WHITE);
        btnBack.setFocusPainted(false);
        btnBack.setBorderPainted(false);
        btnBack.setPreferredSize(new Dimension(160, 38));
        btnBack.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnBack.addActionListener(e -> parent.showCard(DonHangPanel.CARD_TABLE));
        header.add(lblTitle, BorderLayout.WEST);
        header.add(btnBack, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        /* Body wrapper */
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(new Color(0xF0EFF8));

        JPanel body = new JPanel(new GridBagLayout());
        body.setBackground(Color.WHITE);
        body.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xDDDDDD), 1),
                BorderFactory.createEmptyBorder(30, 50, 30, 50)));

        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.HORIZONTAL;
        g.insets = new Insets(8, 8, 8, 8);

        g.gridx = 0; g.gridy = 0; g.gridwidth = 2;
        body.add(Box.createHorizontalStrut(750), g);
        g.gridwidth = 1;

        Font lbFont  = new Font("Arial", Font.BOLD, 15);
        Font secFont = new Font("Arial", Font.BOLD, 16);
        Color secBg  = new Color(0xF3F0FA);

        JTextField tfTenND  = UIUtils.makeField();
        JTextField tfSdt    = UIUtils.makeField();
        JTextField tfDiaChi = UIUtils.makeField();

        String[] httt = { "Thanh toán khi nhận hàng", "Chuyển khoản", "Thẻ tín dụng" };
        JComboBox<String> cbHinhThuc = new JComboBox<>(httt);
        cbHinhThuc.setFont(new Font("Arial", Font.PLAIN, 18));
        cbHinhThuc.setBackground(new Color(0xF8F7FF));

        JTextArea taNotes = new JTextArea(3, 20);
        taNotes.setFont(new Font("Arial", Font.PLAIN, 15));
        taNotes.setLineWrap(true);
        taNotes.setBorder(BorderFactory.createLineBorder(new Color(0xAAAAAA)));

        addSec(body, g, 1, "Thông tin người đặt hàng", secFont, secBg);
        addRow2(body, g, 2, "Tên người mua:", tfTenND, lbFont);
        addRow2(body, g, 3, "Số điện thoại:", tfSdt,   lbFont);
        g.gridx = 0; g.gridy = 4; g.weightx = 0.2;
        JLabel lbDC = new JLabel("Địa chỉ giao hàng:"); lbDC.setFont(lbFont); body.add(lbDC, g);
        g.gridx = 1; g.weightx = 0.8; body.add(tfDiaChi, g);

        addSec(body, g, 5, "Thanh toán", secFont, secBg);
        g.gridx = 0; g.gridy = 6; g.weightx = 0.2;
        JLabel lbHT = new JLabel("Hình thức thanh toán:"); lbHT.setFont(lbFont); body.add(lbHT, g);
        g.gridx = 1; g.weightx = 0.8; body.add(cbHinhThuc, g);

        addSec(body, g, 7, "Ghi chú đơn hàng", secFont, secBg);
        g.gridx = 0; g.gridy = 8; g.gridwidth = 2; g.weightx = 1.0;
        body.add(new JScrollPane(taNotes), g); g.gridwidth = 1;

        addSec(body, g, 9, "Sản phẩm (nhập thủ công)", secFont, secBg);

        String[] spCols = { "Tên sản phẩm", "Số lượng", "Đơn giá" };
        DefaultTableModel spModel = new DefaultTableModel(spCols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return true; }
        };
        spModel.addRow(new Object[]{ "", "1", "0" });

        JTable tblSP = new JTable(spModel);
        tblSP.setFont(new Font("Arial", Font.PLAIN, 14));
        tblSP.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        tblSP.getTableHeader().setBackground(new Color(0xD1C4E9));
        tblSP.setRowHeight(30);
        tblSP.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        JScrollPane spScroll = new JScrollPane(tblSP);
        spScroll.setPreferredSize(new Dimension(0, 130));

        g.gridx = 0; g.gridy = 10; g.gridwidth = 2; g.weightx = 1.0;
        body.add(spScroll, g); g.gridwidth = 1;

        JButton btnThemSP = new JButton("+ Thêm dòng");
        btnThemSP.setFont(new Font("Arial", Font.BOLD, 13));
        btnThemSP.setBackground(new Color(0xD9D9D9));
        btnThemSP.setFocusPainted(false);
        btnThemSP.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnThemSP.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) { btnThemSP.setBackground(new Color(0xC5B3E6)); }
            public void mouseExited(java.awt.event.MouseEvent e)  { btnThemSP.setBackground(new Color(0xD9D9D9)); }
        });
        btnThemSP.addActionListener(e -> spModel.addRow(new Object[]{ "", "1", "0" }));

        JButton btnXoaSP = new JButton("- Xóa dòng");
        btnXoaSP.setFont(new Font("Arial", Font.BOLD, 13));
        btnXoaSP.setBackground(new Color(0xEECCCC));
        btnXoaSP.setFocusPainted(false);
        btnXoaSP.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnXoaSP.addActionListener(e -> {
            int sel = tblSP.getSelectedRow();
            if (sel >= 0) spModel.removeRow(sel);
        });

        JPanel spBtnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        spBtnPanel.setOpaque(false);
        spBtnPanel.add(btnThemSP); spBtnPanel.add(btnXoaSP);
        g.gridx = 0; g.gridy = 11; g.gridwidth = 2; g.weightx = 1.0;
        body.add(spBtnPanel, g); g.gridwidth = 1;

        g.gridx = 0; g.gridy = 12; g.gridwidth = 2; g.weighty = 1.0;
        body.add(Box.createVerticalGlue(), g);

        GridBagConstraints wgc = new GridBagConstraints();
        wgc.gridx = 0; wgc.gridy = 0;
        wgc.anchor = GridBagConstraints.NORTH;
        wgc.weightx = 1.0; wgc.weighty = 1.0;
        wgc.insets = new Insets(30, 0, 40, 0);
        wrapper.add(body, wgc);

        JScrollPane bodyScroll = new JScrollPane(wrapper);
        bodyScroll.setBorder(BorderFactory.createEmptyBorder());
        bodyScroll.getVerticalScrollBar().setUnitIncrement(16);
        add(bodyScroll, BorderLayout.CENTER);

        /* Footer */
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 12));
        footer.setBackground(new Color(0xF0EFF8));
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(0xCCCCCC)));

        JButton btnLuu = DonHangPanel.makeFootBtn("Lưu đơn hàng", new Color(0x6677C8));
        JButton btnHuy = DonHangPanel.makeFootBtn("Hủy bỏ",        new Color(0xB83434));

        btnHuy.addActionListener(e -> parent.showCard(DonHangPanel.CARD_TABLE));
        btnLuu.addActionListener(e -> {
            String ten = tfTenND.getText().trim();
            String sdt = tfSdt.getText().trim();
            if (ten.isEmpty() || sdt.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Vui lòng nhập Tên người mua và Số điện thoại!",
                        "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            long tongCong = 0;
            for (int r = 0; r < spModel.getRowCount(); r++) {
                try {
                    long sl  = Long.parseLong(spModel.getValueAt(r, 1).toString().replaceAll("[^0-9]", ""));
                    long gia = Long.parseLong(spModel.getValueAt(r, 2).toString().replaceAll("[^0-9]", ""));
                    tongCong += sl * gia;
                } catch (NumberFormatException ex) { /* bỏ qua */ }
            }
            String maDon = "HD" + String.format("%03d", parent.tableModel.getRowCount() + 1);
            parent.tableModel.addRow(new Object[]{
                    maDon, ten, spModel.getRowCount(), "-0đ",
                    String.format("%,.0f", (double) tongCong) + "đ", "Chờ xác nhận", ""
            });
            JOptionPane.showMessageDialog(this,
                    "Đã tạo đơn hàng " + maDon + " thành công!",
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);
            tfTenND.setText(""); tfSdt.setText(""); tfDiaChi.setText(""); taNotes.setText("");
            spModel.setRowCount(0);
            spModel.addRow(new Object[]{ "", "1", "0" });
            parent.showCard(DonHangPanel.CARD_TABLE);
        });

        footer.add(btnLuu); footer.add(btnHuy);
        add(footer, BorderLayout.SOUTH);
    }

    private void addRow2(JPanel p, GridBagConstraints g, int row, String label, JTextField tf, Font lbF) {
        g.gridx = 0; g.gridy = row; g.weightx = 0.2;
        JLabel l = new JLabel(label); l.setFont(lbF); p.add(l, g);
        g.gridx = 1; g.weightx = 0.8; p.add(tf, g);
    }

    private void addSec(JPanel p, GridBagConstraints g, int row, String text, Font f, Color bg) {
        g.gridx = 0; g.gridy = row; g.gridwidth = 2; g.weightx = 1.0;
        JLabel l = new JLabel(text); l.setFont(f); l.setOpaque(true); l.setBackground(bg);
        l.setBorder(BorderFactory.createEmptyBorder(5, 12, 5, 12));
        p.add(l, g); g.gridwidth = 1;
    }
}
