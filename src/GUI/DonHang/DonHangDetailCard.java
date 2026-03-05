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
    private JLabel lbTongCong, lbVAT, lbPhiVC, lbMaGiam, lbHinhThuc;
    private DefaultTableModel chitietModel;

    /* ── footer buttons ── */
    private JButton btnSua, btnLuu, btnXoa, btnInHoaDon, btnHuyDon, btnThanhToan, btnXacNhan, btnHoanTac;
    /* ── edit snapshot (for undo) ── */
    private String origTenND, origSdt, origDiaChi, origTongTT, origTrangThai;
    private JLabel lbNhanVien;

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
        JButton btnBack = new JButton("← Quay lại danh sách");
        btnBack.setFont(new Font("Arial", Font.BOLD, 22));
        btnBack.setBackground(new Color(0x9B8EA8));
        btnBack.setForeground(Color.WHITE);
        btnBack.setFocusPainted(false);
        btnBack.setBorderPainted(false);
        btnBack.setPreferredSize(new Dimension(300, 48));
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
        body.setPreferredSize(new Dimension(750, 1020));

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
        lbNhanVien = makeVal(valFont);
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
        tblSP.setShowHorizontalLines(true);
        tblSP.setGridColor(new Color(0xEEEEEE));
        tblSP.setFillsViewportHeight(true);
        tblSP.setPreferredScrollableViewportSize(new Dimension(580, 150));
        tblSP.getColumnModel().getColumn(0).setPreferredWidth(45);
        tblSP.getColumnModel().getColumn(0).setMaxWidth(55);
        tblSP.getColumnModel().getColumn(1).setPreferredWidth(260);
        tblSP.getColumnModel().getColumn(2).setPreferredWidth(75);
        tblSP.getColumnModel().getColumn(3).setPreferredWidth(115);
        tblSP.getColumnModel().getColumn(4).setPreferredWidth(115);
        JScrollPane spScroll = new JScrollPane(tblSP);
        spScroll.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, Color.BLACK));

        g.gridx = 0; g.gridy = 12; g.gridwidth = 2; g.weightx = 1.0;
        body.add(spScroll, g);
        g.gridwidth = 1;

        lbTongCong = makeVal(valFont);
        lbVAT      = makeVal(valFont);
        lbPhiVC    = makeVal(valFont);
        lbMaGiam   = makeVal(valFont);
        tfTongTT   = UIUtils.makeField();
        lbHinhThuc = makeVal(valFont);

        addRow2(body, g, 13, "Tổng cộng (ước tính):",       lbTongCong, valFont);
        addRow2(body, g, 14, "VAT (10%):",                   lbVAT,      valFont);
        addRow2(body, g, 15, "Phí vận chuyển:",              lbPhiVC,    valFont);
        addRow2(body, g, 16, "Khuyến mãi:",                  lbMaGiam,   valFont);
        addRow2(body, g, 17, "Tổng số tiền cần thanh toán:", tfTongTT,   valFont);
        addRow2(body, g, 18, "Hình thức thanh toán:",        lbHinhThuc, valFont);

        g.gridx = 0; g.gridy = 19; g.gridwidth = 2; g.weighty = 1.0;
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

        btnSua       = DonHangPanel.makeFootBtn("Sửa",        new Color(0x6677C8));
        btnLuu       = DonHangPanel.makeFootBtn("Lưu",        new Color(0x4CAF50));
        btnXoa       = DonHangPanel.makeFootBtn("Xóa",        new Color(0xE53935));
        btnInHoaDon  = DonHangPanel.makeFootBtn("In hoá đơn", new Color(0x8C9EFF));
        btnHuyDon    = DonHangPanel.makeFootBtn("Hủy đơn",   new Color(0xB83434));
        btnThanhToan = DonHangPanel.makeFootBtn("Thanh toán", new Color(0x2E7D32));
        btnXacNhan   = DonHangPanel.makeFootBtn("Xác nhận",  new Color(0x1565C0));

        btnSua.addActionListener(e -> setDetailEditable(true));
        btnLuu.addActionListener(e -> saveDetailChanges());
        btnXoa.addActionListener(e -> deleteCurrentOrder());
        btnInHoaDon.addActionListener(e -> {
            if (parent.currentRow < 0) return;
            parent.showInvoice(parent.currentRow);
        });
        btnHuyDon.addActionListener(e -> {
            if (parent.currentRow < 0) return;
            int c = JOptionPane.showConfirmDialog(this,
                    "Huỷ đơn " + parent.tableModel.getValueAt(parent.currentRow, 0) + "?",
                    "Xác nhận", JOptionPane.YES_NO_OPTION);
            if (c == JOptionPane.YES_OPTION) {
                parent.tableModel.setValueAt("Đã hủy", parent.currentRow, 5);
                JOptionPane.showMessageDialog(this, "Đơn hàng đã được huỷ.",
                        "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                loadDetail(parent.currentRow);
            }
        });
        btnThanhToan.addActionListener(e -> {
            if (parent.currentRow < 0) return;
            String maDon    = parent.tableModel.getValueAt(parent.currentRow, 0).toString();
            String tongTien = parent.tableModel.getValueAt(parent.currentRow, 4).toString();
            JOptionPane.showMessageDialog(this,
                    "Xác nhận thanh toán đơn hàng " + maDon + "\nSố tiền: " + tongTien,
                    "Thanh toán", JOptionPane.INFORMATION_MESSAGE);
        });

        btnXacNhan.addActionListener(e -> {
            if (parent.currentRow < 0) return;
            int c = JOptionPane.showConfirmDialog(this,
                    "Xác nhận đơn hàng " + parent.tableModel.getValueAt(parent.currentRow, 0) + "?",
                    "Xác nhận đơn hàng", JOptionPane.YES_NO_OPTION);
            if (c != JOptionPane.YES_OPTION) return;
            parent.tableModel.setValueAt("Đã xác nhận", parent.currentRow, 5);
            JOptionPane.showMessageDialog(this, "Đã xác nhận đơn hàng.",
                    "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            loadDetail(parent.currentRow);
        });
        btnHoanTac = DonHangPanel.makeFootBtn("Hoàn tác", new Color(0xFF7043));
        btnHoanTac.addActionListener(e -> {
            int c = JOptionPane.showConfirmDialog(this,
                    "Bỏ các thay đổi và khôi phục dữ liệu gốc?",
                    "Hoàn tác", JOptionPane.YES_NO_OPTION);
            if (c == JOptionPane.YES_OPTION) {
                cbTrangThai.setSelectedItem(origTrangThai);
                tfTenND.setText(origTenND);
                tfSdt.setText(origSdt);
                tfDiaChi.setText(origDiaChi);
                tfTongTT.setText(origTongTT);
                setDetailEditable(false);
            }
        });
        btnLuu.setVisible(false);
        btnHuyDon.setVisible(false);
        btnThanhToan.setVisible(false);
        btnXacNhan.setVisible(false);
        btnHoanTac.setVisible(false);
        footer.add(btnThanhToan);
        footer.add(btnXacNhan);
        footer.add(btnSua);
        footer.add(btnLuu);
        footer.add(btnHoanTac);
        footer.add(btnInHoaDon);
        footer.add(btnHuyDon);
        footer.add(btnXoa);
        setDetailEditable(false);
        add(footer, BorderLayout.SOUTH);
    }

    void loadDetail(int modelRow) {
        String maDon     = parent.tableModel.getValueAt(modelRow, 0).toString();
        String nguoiMua  = parent.tableModel.getValueAt(modelRow, 1).toString();
        String tongTien  = parent.tableModel.getValueAt(modelRow, 4).toString();
        String trangThai = parent.tableModel.getValueAt(modelRow, 5).toString();
        String maKMTbl   = parent.tableModel.getValueAt(modelRow, 3).toString();

        lbMaDon.setText(maDon);
        cbTrangThai.setSelectedItem(trangThai);
        if (lbNhanVien != null)
            lbNhanVien.setText(parent.nhanVienMap.getOrDefault(maDon, "Nguyễn Thị Thẹo"));

        DonHangPanel.OrderDetailData od = parent.orderDataMap.get(maDon);
        if (od != null) {
            // Real order created from the create form
            lbNgayDat.setText(od.time);
            lbNgayGiao.setText("Dự kiến giao sau 1-3 ngày");
            tfTenND.setText(od.ten);
            tfSdt.setText(od.phone);
            tfDiaChi.setText(od.diaChi);
            lbHinhThuc.setText(od.payMethod);
            String discDisplay = od.maKM.isEmpty()
                ? (od.discAmt > 0 ? "Giảm " + String.format("%,.0fđ", (double) od.discAmt) : "-")
                : od.maKM + (od.discAmt > 0 ? " (giảm " + String.format("%,.0fđ", (double) od.discAmt) + ")" : "");
            lbMaGiam.setText(discDisplay);
            lbIdTK.setText("-");
            chitietModel.setRowCount(0);
            long sub = 0; int stt = 1;
            for (DonHangPanel.OrderDetailData.Item it : od.items) {
                long line = it.unitPrice * it.qty; sub += line;
                chitietModel.addRow(new Object[]{ String.valueOf(stt++),
                    it.code + " - " + it.name, it.qty,
                    String.format("%,.0fđ", (double) it.unitPrice),
                    String.format("%,.0fđ", (double) line) });
            }
            long tot = Math.max(0, sub - od.discAmt);
            lbTongCong.setText(String.format("%,.0fđ", (double) sub));
            lbVAT.setText(String.format("%,.0fđ", (double) (tot * 10L / 110)));
            lbPhiVC.setText("Miễn phí");
            tfTongTT.setText(String.format("%,.0fđ", (double) tot));
        } else {
            // Fallback for pre-existing sample orders
            lbNgayDat.setText("05/03/2026 (22:28)");
            lbNgayGiao.setText("06/03/2026 (08:00)");
            tfTenND.setText(nguoiMua);
            lbIdTK.setText("TK" + (100000 + modelRow));
            tfSdt.setText("0902345678");
            tfDiaChi.setText("123 Nguyễn Trãi, P.2, Q.5, TP.HCM");
            lbHinhThuc.setText("Thanh toán khi nhận hàng");
            lbMaGiam.setText(maKMTbl.equals("-") ? "-" : maKMTbl);
            chitietModel.setRowCount(0);
            chitietModel.addRow(new Object[]{ "1", "SP001 - Nước F trái K",   2, "25.000đ", "50.000đ" });
            chitietModel.addRow(new Object[]{ "2", "SP004 - Mì ý sốt kem",    1, "36.000đ", "36.000đ" });
            chitietModel.addRow(new Object[]{ "3", "SP005 - Pepsi không calo", 1, "10.000đ", "10.000đ" });
            lbTongCong.setText("96.000đ");
            lbVAT.setText(String.format("%,.0fđ", (double) (Long.parseLong(tongTien.replaceAll("[^0-9]", "")) * 10L / 110)));
            lbPhiVC.setText("Miễn phí");
            tfTongTT.setText(tongTien);
        }
        setDetailEditable(false);
    }

    void setDetailEditable(boolean editable) {
        if (editable) {
            origTrangThai = cbTrangThai.getSelectedItem() != null ? cbTrangThai.getSelectedItem().toString() : "";
            origTenND  = tfTenND.getText();
            origSdt    = tfSdt.getText();
            origDiaChi = tfDiaChi.getText();
            origTongTT = tfTongTT.getText();
        }
        if (cbTrangThai != null) cbTrangThai.setEnabled(editable);
        tfTenND.setEditable(editable);
        tfSdt.setEditable(editable);
        tfDiaChi.setEditable(editable);
        tfTongTT.setEditable(editable);
        if (btnSua      != null) btnSua.setVisible(!editable);
        if (btnLuu      != null) btnLuu.setVisible(editable);
        if (btnHoanTac  != null) btnHoanTac.setVisible(editable);
        if (btnXoa      != null) btnXoa.setVisible(!editable);
        if (btnInHoaDon != null) btnInHoaDon.setVisible(!editable);
        if (editable) {
            if (btnHuyDon    != null) btnHuyDon.setVisible(false);
            if (btnThanhToan != null) btnThanhToan.setVisible(false);
            if (btnXacNhan   != null) btnXacNhan.setVisible(false);
        } else if (parent.currentRow >= 0) {
            String tt = cbTrangThai.getSelectedItem() != null ? cbTrangThai.getSelectedItem().toString() : "";
            if (btnHuyDon    != null) btnHuyDon.setVisible(tt.equals("Chờ xác nhận") || tt.equals("Đã xác nhận"));
            if (btnThanhToan != null) btnThanhToan.setVisible(!tt.equals("Đã hủy"));
            if (btnXacNhan   != null) btnXacNhan.setVisible(tt.equals("Chờ xác nhận"));
        }
    }

    private void saveDetailChanges() {
        if (parent.currentRow < 0) return;

        String ten      = tfTenND.getText().trim();
        String sdt      = tfSdt.getText().trim();
        String diaChi   = tfDiaChi.getText().trim();
        String tongTien = tfTongTT.getText().trim();
        String trangThai = cbTrangThai.getSelectedItem() != null
                ? cbTrangThai.getSelectedItem().toString() : "";

        java.util.List<String> errors = new java.util.ArrayList<>();
        JComponent firstBad = null;

        if (ten.isEmpty()) {
            errors.add("• Tên người đặt không được để trống.");
            firstBad = tfTenND;
        } else if (!ten.matches("[\\p{L} .'-]+")) {
            errors.add("• Tên người đặt không hợp lệ (không chứa số hoặc ký tự đặc biệt).");
            firstBad = tfTenND;
        }
        if (!sdt.matches("0[0-9]{9}")) {
            errors.add("• Số điện thoại phải gồm 10 chữ số và bắt đầu bằng 0.");
            if (firstBad == null) firstBad = tfSdt;
        }
        if (diaChi.isEmpty()) {
            errors.add("• Địa chỉ không được để trống.");
            if (firstBad == null) firstBad = tfDiaChi;
        }
        String soTien = tongTien.replaceAll("[đĐ.,\\s]", "");
        try {
            long amount = Long.parseLong(soTien);
            if (amount <= 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            errors.add("• Tổng số tiền phải là số dương hợp lệ (ví dụ: 90.000đ hoặc 90000).");
            if (firstBad == null) firstBad = tfTongTT;
        }
        if (!errors.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    String.join("\n", errors),
                    "Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE);
            if (firstBad != null) firstBad.requestFocus();
            return;
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
