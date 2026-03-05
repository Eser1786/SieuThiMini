package GUI.DonHang;

import GUI.UIUtils;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;

class DonHangDetailCard extends JPanel {

    private final DonHangPanel parent;

    /* ── form fields ── */
    private JLabel lbMaDon, lbNgayDat, lbNgayGiao, lbIdTK;
    private JComboBox<String> cbTrangThai;
    private JTextField tfTenND, tfSdt, tfDiaChi, tfTongTT;
    private JLabel lbTongCong, lbPhiVC, lbMaGiam, lbHinhThuc;
    private DefaultTableModel chitietModel;

    /* ── footer buttons ── */
    private JButton btnSua, btnLuu, btnXoa, btnInHoaDon;

    DonHangDetailCard(DonHangPanel parent) {
        this.parent = parent;
        setLayout(new BorderLayout());
        buildUI();
    }

    private void buildUI() {
        setBackground(new Color(0xF4F4F4));

        /* Header */
        JPanel header = new JPanel(new BorderLayout(0, 0));
        header.setBackground(new Color(0xF4F4F4));
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0xDEE2E6)),
                BorderFactory.createEmptyBorder(12, 20, 12, 20)));
        JLabel lblTitle = new JLabel("Xem chi tiết đơn hàng");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 16));
        lblTitle.setForeground(new Color(0x999999));
        JButton btnBack = new JButton("← Quay lại");
        btnBack.setFont(new Font("Arial", Font.BOLD, 14));
        btnBack.setBackground(new Color(0xFFFFFF));
        btnBack.setForeground(new Color(0x666666));
        btnBack.setFocusPainted(false);
        btnBack.setPreferredSize(new Dimension(130, 36));
        btnBack.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnBack.addActionListener(e -> parent.showCard(DonHangPanel.CARD_TABLE));
        header.add(lblTitle, BorderLayout.WEST);
        header.add(btnBack, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        /* Wrapper */
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(new Color(0xF4F4F4));

        /* Body */
        JPanel body = new JPanel(new GridBagLayout());
        body.setBackground(Color.WHITE);
        body.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xDDDDDD)),
                BorderFactory.createEmptyBorder(20, 40, 30, 40)));
        body.setMinimumSize(new Dimension(650, 0));
        body.setPreferredSize(new Dimension(750, 850));

        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.HORIZONTAL;
        g.insets = new Insets(6, 4, 6, 4);

        Font valFont = new Font("Arial", Font.PLAIN, 14);
        Font secFont = new Font("Arial", Font.BOLD, 16);
        Color secBg  = new Color(0xF5F5F5);

        lbMaDon    = makeVal(valFont);
        lbNgayDat  = makeVal(valFont);
        lbNgayGiao = makeVal(valFont);
        cbTrangThai = new JComboBox<>(new String[]{
                "Chờ xác nhận", "Đã xác nhận", "Chờ vận chuyển",
                "Đang giao", "Đã giao", "Đã hủy"
        });
        cbTrangThai.setFont(valFont);
        cbTrangThai.setEnabled(false);
        JLabel lbNhanVien = makeVal(valFont);
        lbNhanVien.setText("Nguyễn Thị Thẹo");

        addSection(body, g, 0, "Thông tin đơn hàng", secFont, secBg);
        addRow2(body, g, 1, "Mã đơn hàng:",        lbMaDon,     valFont);
        addRow2(body, g, 2, "Ngày đặt hàng:",       lbNgayDat,   valFont);
        addRow2(body, g, 3, "Ngày giao dự kiến:",   lbNgayGiao,  valFont);
        addRow2(body, g, 4, "Trạng thái hiện tại:", cbTrangThai, valFont);
        addRow2(body, g, 5, "Nhân viên tiếp nhận:", lbNhanVien,  valFont);

        tfTenND  = UIUtils.makeField();
        lbIdTK   = makeVal(valFont);
        tfSdt    = UIUtils.makeField();
        tfDiaChi = UIUtils.makeField();

        addSection(body, g, 6,  "Thông tin người mua", secFont, secBg);
        addRow2(body, g, 7,  "Tên người đặt:", tfTenND,  valFont);
        addRow2(body, g, 8,  "ID tài khoản:",  lbIdTK,   valFont);
        addRow2(body, g, 9,  "Số điện thoại:", tfSdt,    valFont);
        addRow2(body, g, 10, "Địa chỉ:",       tfDiaChi, valFont);

        addSection(body, g, 11, "Thông tin sản phẩm đã đặt", secFont, secBg);

        String[] spCols = { "STT", "Sản phẩm", "Số lượng", "Đơn giá", "Thành tiền" };
        chitietModel = new DefaultTableModel(spCols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tblSP = new JTable(chitietModel);
        tblSP.setFont(new Font("Arial", Font.PLAIN, 13));
        tblSP.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        tblSP.getTableHeader().setBackground(Color.WHITE);
        tblSP.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.BLACK));
        tblSP.setRowHeight(30);
        tblSP.setShowVerticalLines(false);
        tblSP.setGridColor(Color.WHITE);
        JScrollPane spScroll = new JScrollPane(tblSP);
        spScroll.setPreferredSize(new Dimension(0, 160));
        spScroll.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, Color.BLACK));

        g.gridx = 0; g.gridy = 12; g.gridwidth = 2; g.weightx = 1.0;
        body.add(spScroll, g);
        g.gridwidth = 1;

        lbTongCong = makeVal(valFont);
        lbPhiVC    = makeVal(valFont);
        lbMaGiam   = makeVal(valFont);
        tfTongTT   = UIUtils.makeField();
        lbHinhThuc = makeVal(valFont);

        addRow2(body, g, 13, "Tổng cộng (ước tính):",       lbTongCong, valFont);
        addRow2(body, g, 14, "Phí vận chuyển:",              lbPhiVC,    valFont);
        addRow2(body, g, 15, "Mã giảm giá:",                 lbMaGiam,   valFont);
        addRow2(body, g, 16, "Tổng số tiền cần thanh toán:", tfTongTT,   valFont);
        addRow2(body, g, 17, "Hình thức thanh toán:",        lbHinhThuc, valFont);

        g.gridx = 0; g.gridy = 18; g.gridwidth = 2; g.weighty = 1.0;
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
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 16));
        footer.setBackground(new Color(0xF4F4F4));

        btnSua      = DonHangPanel.makeFootBtn("Sửa",        new Color(0x6677C8));
        btnLuu      = DonHangPanel.makeFootBtn("Lưu",        new Color(0x4CAF50));
        btnXoa      = DonHangPanel.makeFootBtn("Xóa",        new Color(0xE53935));
        btnInHoaDon = DonHangPanel.makeFootBtn("In hoá đơn", new Color(0x8C9EFF));

        btnSua.addActionListener(e -> setDetailEditable(true));
        btnLuu.addActionListener(e -> saveDetailChanges());
        btnXoa.addActionListener(e -> deleteCurrentOrder());
        btnInHoaDon.addActionListener(e -> {
            if (parent.currentRow < 0) return;
            parent.showInvoice(parent.currentRow);
        });

        btnLuu.setVisible(false);
        footer.add(btnSua);
        footer.add(btnLuu);
        footer.add(btnInHoaDon);
        footer.add(btnXoa);
        setDetailEditable(false);
        add(footer, BorderLayout.SOUTH);
    }

    void loadDetail(int modelRow) {
        String maDon     = parent.tableModel.getValueAt(modelRow, 0).toString();
        String nguoiMua  = parent.tableModel.getValueAt(modelRow, 1).toString();
        String tongTien  = parent.tableModel.getValueAt(modelRow, 4).toString();
        String trangThai = parent.tableModel.getValueAt(modelRow, 5).toString();

        lbMaDon.setText(maDon);
        lbNgayDat.setText("05/03/2026 (22:28)");
        lbNgayGiao.setText("06/03/2026 (08:00)");
        cbTrangThai.setSelectedItem(trangThai);
        tfTenND.setText(nguoiMua);
        lbIdTK.setText("TK" + (100000 + modelRow));
        tfSdt.setText("0102282828");
        tfDiaChi.setText("1/22, Đường số 1, Phường 2, Quận 3, TP.HCM");

        chitietModel.setRowCount(0);
        chitietModel.addRow(new Object[]{ "1", "Cơm nắm cá hồi mayo", 1, "16.000", "16.000" });
        chitietModel.addRow(new Object[]{ "2", "Mì ý sốt kem",        1, "36.000", "36.000" });
        chitietModel.addRow(new Object[]{ "3", "Pepsi không calo",     1, "10.000", "10.000" });
        chitietModel.addRow(new Object[]{ "4", "Kem si cu la",         2, "18.000", "38.000" });

        lbTongCong.setText("100.000đ");
        lbPhiVC.setText("Miễn phí");
        lbMaGiam.setText("10.000đ");
        tfTongTT.setText(tongTien);
        lbHinhThuc.setText("Thanh toán khi nhận hàng");
        setDetailEditable(false);
    }

    void setDetailEditable(boolean editable) {
        if (cbTrangThai != null) cbTrangThai.setEnabled(editable);
        tfTenND.setEditable(editable);
        tfSdt.setEditable(editable);
        tfDiaChi.setEditable(editable);
        tfTongTT.setEditable(editable);
        if (btnSua != null) btnSua.setVisible(!editable);
        if (btnLuu != null) btnLuu.setVisible(editable);
    }

    private void saveDetailChanges() {
        if (parent.currentRow < 0) return;

        String ten      = tfTenND.getText().trim();
        String sdt      = tfSdt.getText().trim();
        String diaChi   = tfDiaChi.getText().trim();
        String tongTien = tfTongTT.getText().trim();
        String trangThai = cbTrangThai.getSelectedItem() != null
                ? cbTrangThai.getSelectedItem().toString() : "";

        if (ten.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tên người đặt không được để trống.",
                    "Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE);
            tfTenND.requestFocus(); return;
        }
        if (!ten.matches("[\\p{L} .'-]+")) {
            JOptionPane.showMessageDialog(this,
                    "Tên người đặt không hợp lệ (không chứa số hoặc ký tự đặc biệt).",
                    "Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE);
            tfTenND.requestFocus(); return;
        }
        if (!sdt.matches("0[0-9]{9}")) {
            JOptionPane.showMessageDialog(this,
                    "Số điện thoại phải gồm 10 chữ số và bắt đầu bằng 0.",
                    "Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE);
            tfSdt.requestFocus(); return;
        }
        if (diaChi.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Địa chỉ không được để trống.",
                    "Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE);
            tfDiaChi.requestFocus(); return;
        }
        String soTien = tongTien.replaceAll("[đĐ.,\\s]", "");
        try {
            long amount = Long.parseLong(soTien);
            if (amount <= 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Tổng số tiền phải là số dương hợp lệ (ví dụ: 90.000đ hoặc 90000).",
                    "Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE);
            tfTongTT.requestFocus(); return;
        }

        parent.tableModel.setValueAt(ten,      parent.currentRow, 1);
        parent.tableModel.setValueAt(tongTien, parent.currentRow, 4);
        parent.tableModel.setValueAt(trangThai, parent.currentRow, 5);
        setDetailEditable(false);
        JOptionPane.showMessageDialog(this, "Đã lưu thay đổi đơn hàng.",
                "Thông báo", JOptionPane.INFORMATION_MESSAGE);
    }

    private void deleteCurrentOrder() {
        if (parent.currentRow < 0) return;
        int c = JOptionPane.showConfirmDialog(this,
                "Xóa đơn " + parent.tableModel.getValueAt(parent.currentRow, 0) + "?",
                "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (c == JOptionPane.YES_OPTION) {
            parent.tableModel.removeRow(parent.currentRow);
            parent.currentRow = -1;
            parent.showCard(DonHangPanel.CARD_TABLE);
        }
    }

    /* ── helpers ── */
    private JLabel makeVal(Font f) {
        JLabel l = new JLabel(); l.setFont(f); return l;
    }

    private void addSection(JPanel p, GridBagConstraints g, int row, String text, Font f, Color bg) {
        g.gridx = 0; g.gridy = row; g.gridwidth = 2; g.weightx = 1.0;
        JLabel l = new JLabel(text); l.setFont(f); l.setOpaque(true); l.setBackground(bg);
        l.setBorder(BorderFactory.createEmptyBorder(5, 12, 5, 12));
        p.add(l, g); g.gridwidth = 1;
    }

    private void addRow2(JPanel p, GridBagConstraints g, int row, String label, JComponent val, Font lbF) {
        g.gridx = 0; g.gridy = row; g.weightx = 0.2;
        JLabel l = new JLabel(label); l.setFont(lbF); p.add(l, g);
        g.gridx = 1; g.weightx = 0.8; p.add(val, g);
    }
}
