package GUI.DonHang;

import BUS.SalesBUS;
import DTO.SaleDTO;
import DTO.enums.SaleEnum.SaleStatus;
import GUI.ExportUtils;
import GUI.UIUtils;
import com.toedter.calendar.JDateChooser;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

class DonHangTableCard extends JPanel {

    private final SalesBUS salesBUS = new SalesBUS();
    private final DonHangPanel parent;
    private JDateChooser dcFrom;
    private JDateChooser dcTo;

    DonHangTableCard(DonHangPanel parent) {
        this.parent = parent;
        setLayout(new BorderLayout());
        setBackground(new Color(0xF8F7FF));
        build();
    }

    private void build() {
        
        String[] cols = { "Mã đơn", "Người mua", "Số lượng SP", "Giảm giá",
                "Tổng số tiền", "Phương thức", "Tình trạng", "Thao tác" };
        parent.tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return c == 7; }
        };
        loadSalesFromDatabase();

        
        String[] trangThais = {
                "Tất cả", "Chờ xác nhận", "Đã xác nhận",
                "Chờ vận chuyển", "Đang giao", "Đã giao", "Đã hủy"
        };

        
        JPanel top = new JPanel(new GUI.WrapLayout(FlowLayout.LEFT, 8, 4));
        top.setBackground(new Color(0xF8F7FF));
        top.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xCCCCCC), 1),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));

        
        JComboBox<String> cbLoc = new JComboBox<>(trangThais);
        cbLoc.setPreferredSize(new Dimension(200, 36));
        UIUtils.styleComboBox(cbLoc);

        JPanel timPanel = new JPanel(new BorderLayout());
        timPanel.setPreferredSize(new Dimension(220, 36));
        timPanel.setBackground(Color.WHITE);
        timPanel.setBorder(BorderFactory.createLineBorder(new Color(0xBBBBBB), 1));

        JTextField tfTim = new JTextField();
        tfTim.setFont(new Font("Arial", Font.PLAIN, 13));
        tfTim.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 4));

        JButton btnTim = new JButton("Q");
        btnTim.setBorderPainted(false);
        btnTim.setContentAreaFilled(false);
        btnTim.setFocusPainted(false);
        btnTim.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnTim.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btnTim.setContentAreaFilled(true);
                btnTim.setBackground(new Color(0xC5B3E6));
                btnTim.setOpaque(true);
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                btnTim.setContentAreaFilled(false);
                btnTim.setOpaque(false);
            }
        });
        timPanel.add(tfTim, BorderLayout.CENTER);
        timPanel.add(btnTim, BorderLayout.EAST);

        JLabel lbLoc = new JLabel("Tr\u1ea1ng th\u00e1i:");
        lbLoc.setFont(new Font("Arial", Font.PLAIN, 13));
        JLabel lbTim = new JLabel("T\u00ecm ki\u1ebfm:");
        lbTim.setFont(new Font("Arial", Font.PLAIN, 13));

        JPanel pLoc = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0)); pLoc.setOpaque(false); pLoc.add(lbLoc); pLoc.add(cbLoc);
        JPanel pTim = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0)); pTim.setOpaque(false); pTim.add(lbTim); pTim.add(timPanel);

        
        JButton btnTao = new JButton("+ T\u1ea1o \u0111\u01a1n h\u00e0ng");
        btnTao.setFocusPainted(false);
        btnTao.setBackground(new Color(0xD9D9D9));
        btnTao.setFont(new Font("Arial", Font.BOLD, 13));
        btnTao.setBorder(BorderFactory.createEmptyBorder(9, 14, 9, 14));
        btnTao.setOpaque(true);
        btnTao.setBorderPainted(false);
        btnTao.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnTao.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) { btnTao.setBackground(new Color(0xC5B3E6)); }
            public void mouseExited(java.awt.event.MouseEvent e)  { btnTao.setBackground(new Color(0xD9D9D9)); }
        });
        btnTao.addActionListener(e -> parent.openCreatePopup(SwingUtilities.getWindowAncestor(this)));

        JButton btnPDF = ExportUtils.makeExportButton("In PDF", new Color(0x7B52AB));
        btnPDF.addActionListener(e -> ExportUtils.xuatPDF(this, parent.tableModel, "Danh s\u00e1ch \u0111\u01a1n h\u00e0ng"));

        JButton btnExcel = ExportUtils.makeExportButton("In Excel", new Color(0x2E7D32));
        btnExcel.addActionListener(e -> ExportUtils.xuatCSV(this, parent.tableModel, "don_hang"));

        top.add(pLoc);
        top.add(pTim);

        
        Runnable[] filterRef = {null};
        dcFrom = new JDateChooser();
        dcFrom.setPreferredSize(new Dimension(130, 36));
        dcFrom.setDateFormatString("dd/MM/yyyy");
        dcTo = new JDateChooser();
        dcTo.setPreferredSize(new Dimension(130, 36));
        dcTo.setDateFormatString("dd/MM/yyyy");

        JLabel lbTuNgay = new JLabel("Từ ngày:");
        lbTuNgay.setFont(new Font("Arial", Font.PLAIN, 13));
        JLabel lbDenNgay = new JLabel("Đến ngày:");
        lbDenNgay.setFont(new Font("Arial", Font.PLAIN, 13));

        JPanel pFrom = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        pFrom.setOpaque(false); pFrom.add(lbTuNgay); pFrom.add(dcFrom);
        JPanel pTo = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        pTo.setOpaque(false); pTo.add(lbDenNgay); pTo.add(dcTo);

        JButton btnLocNgay = new JButton("Lọc ngày");
        btnLocNgay.setFocusPainted(false);
        btnLocNgay.setBackground(new Color(0xD9D9D9));
        btnLocNgay.setFont(new Font("Arial", Font.BOLD, 13));
        btnLocNgay.setBorder(BorderFactory.createEmptyBorder(9, 14, 9, 14));
        btnLocNgay.setOpaque(true);
        btnLocNgay.setBorderPainted(false);
        btnLocNgay.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLocNgay.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) { btnLocNgay.setBackground(new Color(0xC5B3E6)); }
            public void mouseExited(java.awt.event.MouseEvent e)  { btnLocNgay.setBackground(new Color(0xD9D9D9)); }
        });
        btnLocNgay.addActionListener(e -> {
            if (dcFrom.getDate() == null || dcTo.getDate() == null) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn cả ngày bắt đầu và ngày kết thúc.",
                        "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
                return;
            }
            LocalDate from = dcFrom.getDate().toInstant()
                    .atZone(java.time.ZoneId.systemDefault()).toLocalDate();
            LocalDate to = dcTo.getDate().toInstant()
                    .atZone(java.time.ZoneId.systemDefault()).toLocalDate();
            if (to.isBefore(from)) {
                JOptionPane.showMessageDialog(this, "Ngày kết thúc phải sau hoặc bằng ngày bắt đầu.",
                        "Lỗi ngày", JOptionPane.WARNING_MESSAGE);
                return;
            }
            filterByDateRange(from, to);
            if (filterRef[0] != null) filterRef[0].run();
        });

        JButton btnResetNgay = new JButton("Reset");
        btnResetNgay.setFocusPainted(false);
        btnResetNgay.setBackground(new Color(0xD9D9D9));
        btnResetNgay.setFont(new Font("Arial", Font.BOLD, 13));
        btnResetNgay.setBorder(BorderFactory.createEmptyBorder(9, 10, 9, 10));
        btnResetNgay.setOpaque(true);
        btnResetNgay.setBorderPainted(false);
        btnResetNgay.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnResetNgay.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) { btnResetNgay.setBackground(new Color(0xEF9A9A)); }
            public void mouseExited(java.awt.event.MouseEvent e)  { btnResetNgay.setBackground(new Color(0xD9D9D9)); }
        });
        btnResetNgay.addActionListener(e -> { dcFrom.setDate(null); dcTo.setDate(null); cbLoc.setSelectedIndex(0); tfTim.setText(""); loadSalesFromDatabase(); });

        top.add(pFrom);
        top.add(pTo);
        top.add(btnLocNgay);
        top.add(btnResetNgay);
        top.add(btnTao);
        top.add(btnPDF);
        top.add(btnExcel);

        
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(parent.tableModel);
        JTable bang = new JTable(parent.tableModel);
        bang.setRowSorter(sorter);
        bang.setRowHeight(52);
        bang.setFont(new Font("Arial", Font.PLAIN, 16));
        bang.getTableHeader().setFont(new Font("Arial", Font.BOLD, 16));
        bang.getTableHeader().setPreferredSize(new Dimension(0, 52));
        bang.getTableHeader().setBackground(new Color(0xAF9FCB));
        bang.getTableHeader().setForeground(Color.WHITE);
        bang.getTableHeader().setReorderingAllowed(false);
        bang.setShowVerticalLines(false);
        bang.setGridColor(new Color(0xEEEEEE));
        bang.setIntercellSpacing(new Dimension(0, 1));
        bang.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        bang.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        int[] prefWidths = { 70, 120, 80, 90, 110, 90, 130, 200 };
        int[] minWidths  = { 55,  80, 60,  70,  85, 70, 100,  80 };
        for (int i = 0; i < prefWidths.length; i++) {
            bang.getColumnModel().getColumn(i).setPreferredWidth(prefWidths[i]);
            bang.getColumnModel().getColumn(i).setMinWidth(minWidths[i]);
        }

        DefaultTableCellRenderer altR = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable t, Object val, boolean sel, boolean foc, int r, int c) {
                super.getTableCellRendererComponent(t, val, sel, foc, r, c);
                if (!sel) {
                    setBackground(r % 2 == 0 ? Color.WHITE : new Color(0xF3F0FA));
                    setForeground(Color.BLACK);
                    if (c == 3) setForeground(new Color(0xC62828));
                    if (c == 4) setForeground(new Color(0x388E3C));
                    if (c == 6) {
                        String v = val == null ? "" : val.toString();
                        switch (v) {
                            case "Chờ xác nhận"   -> setForeground(new Color(0xE65100));
                            case "Đã xác nhận"    -> setForeground(new Color(0x1565C0));
                            case "Chờ vận chuyển" -> setForeground(new Color(0x6A1B9A));
                            case "Đang giao"      -> setForeground(new Color(0x00838F));
                            case "Đã giao"        -> setForeground(new Color(0x2E7D32));
                            case "Đã hủy"         -> setForeground(new Color(0xB71C1C));
                        }
                    }
                }
                setHorizontalAlignment(SwingConstants.CENTER);
                return this;
            }
        };
        for (int i = 0; i < 7; i++)
            bang.getColumnModel().getColumn(i).setCellRenderer(altR);

        bang.getColumnModel().getColumn(7).setCellRenderer(
                (t, val, sel, foc, r, c) -> buildActionPanel(t, r, false));
        bang.getColumnModel().getColumn(7).setCellEditor(new DefaultCellEditor(new JCheckBox()) {
            private JPanel panel;
            @Override
            public Component getTableCellEditorComponent(JTable t, Object val, boolean sel, int r, int c) {
                panel = buildActionPanel(t, r, true);
                return panel;
            }
            @Override public Object getCellEditorValue() { return ""; }
        });

        
        Runnable applyFilter = () -> {
            String kw  = tfTim.getText().trim();
            int    idx = cbLoc.getSelectedIndex();
            RowFilter<DefaultTableModel, Integer> fSt = idx == 0 ? null
                    : RowFilter.regexFilter("(?i)^" + trangThais[idx] + "$", 6);
            RowFilter<DefaultTableModel, Integer> fSr = kw.isEmpty() ? null
                    : RowFilter.orFilter(java.util.List.of(
                            RowFilter.regexFilter("(?i)" + kw, 0),
                            RowFilter.regexFilter("(?i)" + kw, 1)));
            if (fSt != null && fSr != null) sorter.setRowFilter(RowFilter.andFilter(java.util.List.of(fSt, fSr)));
            else if (fSt != null) sorter.setRowFilter(fSt);
            else if (fSr != null) sorter.setRowFilter(fSr);
            else sorter.setRowFilter(null);
        };
        cbLoc.addActionListener(e -> applyFilter.run());
        btnTim.addActionListener(e -> applyFilter.run());
        tfTim.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e)  { applyFilter.run(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e)  { applyFilter.run(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { applyFilter.run(); }
        });
        filterRef[0] = applyFilter;

        JScrollPane scroll = new JScrollPane(bang);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        
        JPanel dhHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 12));
        dhHeader.setBackground(new Color(0xF8F7FF));
        dhHeader.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0xDDDDDD)),
                BorderFactory.createEmptyBorder(0, 20, 0, 20)));
        JPanel dhBar = new JPanel();
        dhBar.setPreferredSize(new Dimension(5, 26));
        dhBar.setBackground(new Color(0x5C4A7F));
        dhHeader.add(dhBar);
        dhHeader.add(Box.createHorizontalStrut(12));
        JLabel dhTitle = new JLabel("QUẢN LÝ ĐƠN HÀNG");
        dhTitle.setFont(new Font("Arial", Font.BOLD, 20));
        dhHeader.add(dhTitle);

        JPanel dhNorth = new JPanel();
        dhNorth.setLayout(new BoxLayout(dhNorth, BoxLayout.Y_AXIS));
        dhNorth.add(dhHeader);
        dhNorth.add(top);
        add(dhNorth, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
    }

    public void loadSalesFromDatabase() {
    parent.tableModel.setRowCount(0);

    try {

        List<SaleDTO> sales = salesBUS.getAllSales();

        if (sales == null || sales.isEmpty()) return;

        for (SaleDTO s : sales) {

            String maDon = s.getSaleCode();

            String nguoiMua = s.getCustomerName() != null
                    ? s.getCustomerName()
                    : "";

            int soLuong = s.getTotalQuantity();

            String giamGia = "-";
            if (s.getDiscountAmount() != null &&
                s.getDiscountAmount().signum() != 0) {

                giamGia = "-" + String.format("%,.0fđ", s.getDiscountAmount());
            }

            String tongTien = "-";
            if (s.getTotalAmount() != null) {
                tongTien = String.format("%,.0fđ", s.getTotalAmount());
            }

            String trangThai = mapStatus(s.getSaleStatus());
            String phuongThuc = s.getSalePaymentMethod() != null ? s.getSalePaymentMethod().getValue() : "-";

            parent.tableModel.addRow(new Object[]{
                    maDon,
                    nguoiMua,
                    soLuong,
                    giamGia,
                    tongTien,
                    phuongThuc,
                    trangThai,
                    ""
            });
        }

    } catch (Exception e) {
        e.printStackTrace();
    }
}
    private void filterByDateRange(LocalDate from, LocalDate to) {
        parent.tableModel.setRowCount(0);
        try {
            List<SaleDTO> sales = salesBUS.getSalesByDateRange(from, to);
            if (sales == null) return;
            for (SaleDTO s : sales) {
                String maDon     = s.getSaleCode();
                String nguoiMua  = s.getCustomerName() != null ? s.getCustomerName() : "";
                int    soLuong   = s.getTotalQuantity();
                String giamGia   = "-";
                if (s.getDiscountAmount() != null && s.getDiscountAmount().signum() != 0)
                    giamGia = "-" + String.format("%,.0fđ", s.getDiscountAmount());
                String tongTien  = "-";
                if (s.getTotalAmount() != null)
                    tongTien = String.format("%,.0fđ", s.getTotalAmount());
                String trangThai  = mapStatus(s.getSaleStatus());
                String phuongThuc = s.getSalePaymentMethod() != null ? s.getSalePaymentMethod().getValue() : "-";
                parent.tableModel.addRow(new Object[]{ maDon, nguoiMua, soLuong, giamGia, tongTien, phuongThuc, trangThai, "" });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String mapStatus(SaleStatus status) {
        if (status == null) return "";
        return switch (status) {
            case PENDING -> "Chờ xác nhận";
            case CONFIRMED -> "Đã xác nhận";
            case SHIPPING -> "Chờ vận chuyển";
            case DELIVERING -> "Đang giao";
            case COMPLETED -> "Đã giao";
            case CANCELLED -> "Đã hủy";
        };
    }

    private JPanel buildActionPanel(JTable table, int viewRow, boolean withAction) {
        int    modelRow  = table.convertRowIndexToModel(viewRow);
        String trangThai = parent.tableModel.getValueAt(modelRow, 6).toString();
        Color  rowBg     = viewRow % 2 == 0 ? Color.WHITE : new Color(0xF3F0FA);

        
        JPanel inner = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 4));
        inner.setOpaque(false);

        JButton btnXem = UIUtils.makeActionButton("Chi tiết", new Color(0x6677C8));
        inner.add(btnXem);

        if (trangThai.equals("Chờ xác nhận")) {
            JButton btnXN = UIUtils.makeActionButton("Xác nhận", new Color(0x6677C8));
            inner.add(btnXN);
            if (withAction)
                btnXN.addActionListener(e -> {
                    stopEdit(table);
                    String maDon = parent.tableModel.getValueAt(modelRow, 0).toString();
                    int c = JOptionPane.showConfirmDialog(this,
                            "Xác nhận đơn hàng " + maDon + "?",
                            "Xác nhận", JOptionPane.YES_NO_OPTION);
                    if (c == JOptionPane.YES_OPTION) xacNhanDon(table, modelRow);
                });
        }

        if (!trangThai.equals("Đã hủy")) {
            JButton btnTT = UIUtils.makeActionButton("Thanh toán", new Color(0x2E7D32));
            inner.add(btnTT);
            if (withAction) {
                String maDon    = parent.tableModel.getValueAt(modelRow, 0).toString();
                String tongTien = parent.tableModel.getValueAt(modelRow, 4).toString();
                btnTT.addActionListener(e -> {
                    stopEdit(table);
                    JOptionPane.showMessageDialog(this,
                            "Xác nhận thanh toán đơn hàng " + maDon + "\nSố tiền: " + tongTien,
                            "Thanh toán", JOptionPane.INFORMATION_MESSAGE);
                });
            }
        }

        

        if (withAction)
            btnXem.addActionListener(e -> { stopEdit(table); parent.showDetail(modelRow); });

        
        JPanel outer = new JPanel(new GridBagLayout());
        outer.setBackground(rowBg);
        outer.setOpaque(true);
        outer.add(inner); 
        return outer;
    }

    private void stopEdit(JTable t) {
        if (t.isEditing()) t.getCellEditor().stopCellEditing();
    }

   private void xacNhanDon(JTable t, int row) {

    String saleCode = parent.tableModel.getValueAt(row, 0).toString();

    boolean ok = salesBUS.confirmSale(saleCode);

    if(ok){

        JOptionPane.showMessageDialog(this,
                "Xác nhận đơn hàng thành công!\nKho đã được cập nhật.",
                "Thành công",
                JOptionPane.INFORMATION_MESSAGE);

        loadSalesFromDatabase(); 
    }
}

    private void huyDon(JTable t, int row) {
        String saleCode = parent.tableModel.getValueAt(row, 0).toString();
        int c = JOptionPane.showConfirmDialog(this,
                "Huỷ đơn " + saleCode + "?",
                "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (c == JOptionPane.YES_OPTION) {
            boolean ok = salesBUS.cancelSale(saleCode);
            if (ok) {
                parent.tableModel.setValueAt("Đã hủy", row, 6);
                t.repaint();
            }
        }
    }

    

    private void filterByStatus(String statusText) {
        parent.tableModel.setRowCount(0);

        if ("Tất cả".equals(statusText)) {
            loadSalesFromDatabase();
            return;
        }

        SaleStatus status = null;
        switch (statusText) {
            case "Chờ xác nhận" -> status = SaleStatus.PENDING;
            case "Đã xác nhận" -> status = SaleStatus.CONFIRMED;
            case "Chờ vận chuyển" -> status = SaleStatus.SHIPPING;
            case "Đang giao" -> status = SaleStatus.DELIVERING;
            case "Đã giao" -> status = SaleStatus.COMPLETED;
            case "Đã hủy" -> status = SaleStatus.CANCELLED;
        }

        if (status != null) {
            try {
                List<SaleDTO> sales = salesBUS.getSalesByStatus(status);
                if (sales != null) {
                    for (SaleDTO s : sales) {
                        String maDon = s.getSaleCode();
                        String nguoiMua = s.getCustomerName() != null ? s.getCustomerName() : "";
                        int soLuong = s.getTotalQuantity();
                        String giamGia = "-";
                        if (s.getDiscountAmount() != null && s.getDiscountAmount().signum() != 0) {
                            giamGia = "-" + String.format("%,.0fđ", s.getDiscountAmount());
                        }
                        String tongTien = "-";
                        if (s.getTotalAmount() != null) {
                            tongTien = String.format("%,.0fđ", s.getTotalAmount());
                        }
                        String trangThai = mapStatus(s.getSaleStatus());
                        String phuongThuc = s.getSalePaymentMethod() != null ? s.getSalePaymentMethod().getValue() : "-";
                        parent.tableModel.addRow(new Object[]{maDon, nguoiMua, soLuong, giamGia, tongTien, phuongThuc, trangThai, ""});
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
