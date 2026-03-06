package GUI.DonHang;

import BUS.SalesBUS;
import DTO.SaleDTO;
import DTO.enums.SaleEnum.SaleStatus;
import GUI.ExportUtils;
import GUI.UIUtils;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

class DonHangTableCard extends JPanel {

    private final SalesBUS salesBUS = new SalesBUS();
    private final DonHangPanel parent;

    DonHangTableCard(DonHangPanel parent) {
        this.parent = parent;
        setLayout(new BorderLayout());
        setBackground(new Color(0xF8F7FF));
        build();
    }

    private void build() {
        /* ── Khởi tạo tableModel trên parent ── */
        String[] cols = { "Mã đơn", "Người mua", "Số lượng SP", "Giảm giá",
                "Tổng số tiền", "Tình trạng", "Thao tác" };
        parent.tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return c == 6; }
        };
        loadSalesFromDatabase();

        /* Toolbar */
        String[] trangThais = {
                "Tất cả", "Chờ xác nhận", "Đã xác nhận",
                "Chờ vận chuyển", "Đang giao", "Đã giao", "Đã hủy"
        };
        JPanel top = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 25));
        top.setBackground(new Color(0xF8F7FF));
        top.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLACK, 1),
                BorderFactory.createEmptyBorder(0, 10, 0, 10)));
        top.setPreferredSize(new Dimension(0, 94));

        JComboBox<String> cbLoc = new JComboBox<>(trangThais);
        cbLoc.setPreferredSize(new Dimension(220, 42));
        cbLoc.setFont(new Font("Arial", Font.PLAIN, 22));
        cbLoc.setBackground(new Color(0xD9D9D9));

        JPanel timPanel = new JPanel(new BorderLayout());
        timPanel.setPreferredSize(new Dimension(229, 42));
        timPanel.setBackground(new Color(0xD9D9D9));

        JTextField tfTim = new JTextField();
        tfTim.setFont(new Font("Arial", Font.PLAIN, 22));
        tfTim.setBackground(new Color(0xD9D9D9));
        tfTim.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 4));

        JButton btnTim = new JButton("🔍");
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

        top.add(cbLoc);
        top.add(timPanel);

        JButton btnTao = new JButton("+ Tạo đơn hàng");
        btnTao.setFocusPainted(false);
        btnTao.setBackground(new Color(0xD9D9D9));
        btnTao.setPreferredSize(new Dimension(200, 42));
        btnTao.setFont(new Font("Arial", Font.BOLD, 18));
        btnTao.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnTao.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) { btnTao.setBackground(new Color(0xC5B3E6)); }
            public void mouseExited(java.awt.event.MouseEvent e)  { btnTao.setBackground(new Color(0xD9D9D9)); }
        });
        btnTao.addActionListener(e -> parent.openCreatePopup(SwingUtilities.getWindowAncestor(this)));
        top.add(btnTao);

        JButton btnPDF = ExportUtils.makeExportButton("Xuất PDF", new Color(0x7B52AB));
        btnPDF.addActionListener(e -> ExportUtils.xuatPDF(this, parent.tableModel, "Danh sách đơn hàng"));
        top.add(btnPDF);

        JButton btnExcel = ExportUtils.makeExportButton("Xuất Excel", new Color(0x2E7D32));
        btnExcel.addActionListener(e -> ExportUtils.xuatCSV(this, parent.tableModel, "don_hang"));
        top.add(btnExcel);

        /* Bảng */
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
        int[] prefWidths = { 70, 120, 80, 90, 110, 130, 200 };
        int[] minWidths  = { 55,  80, 60,  70,  85, 100,  80 };
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
                    if (c == 5) {
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
        for (int i = 0; i < 6; i++)
            bang.getColumnModel().getColumn(i).setCellRenderer(altR);

        bang.getColumnModel().getColumn(6).setCellRenderer(
                (t, val, sel, foc, r, c) -> buildActionPanel(t, r, false));
        bang.getColumnModel().getColumn(6).setCellEditor(new DefaultCellEditor(new JCheckBox()) {
            private JPanel panel;
            @Override
            public Component getTableCellEditorComponent(JTable t, Object val, boolean sel, int r, int c) {
                panel = buildActionPanel(t, r, true);
                return panel;
            }
            @Override public Object getCellEditorValue() { return ""; }
        });

        /* Lọc & tìm kiếm */
        Runnable applyFilter = () -> {
            String kw  = tfTim.getText().trim();
            int    idx = cbLoc.getSelectedIndex();
            RowFilter<DefaultTableModel, Integer> fSt = idx == 0 ? null
                    : RowFilter.regexFilter("(?i)^" + trangThais[idx] + "$", 5);
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

        JScrollPane scroll = new JScrollPane(bang);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        add(top, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
    }

    private void loadSalesFromDatabase() {
    parent.tableModel.setRowCount(0);

    try {

        List<SaleDTO> sales = salesBUS.getAllSales();

        if (sales == null || sales.isEmpty()) {
            return;
        }

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

            parent.tableModel.addRow(new Object[]{
                    maDon,
                    nguoiMua,
                    soLuong,
                    giamGia,
                    tongTien,
                    trangThai,
                    ""
            });
        }

    } catch (Exception e) {
        e.printStackTrace();
    }
}
    private String mapStatus(SaleStatus status) {
        if (status == null) return "";
        return switch (status) {
            case COMPLETED -> "Đã giao";
            case CANCELLED -> "Đã hủy";
        };
    }

    private JPanel buildActionPanel(JTable table, int viewRow, boolean withAction) {
        int    modelRow  = table.convertRowIndexToModel(viewRow);
        String trangThai = parent.tableModel.getValueAt(modelRow, 5).toString();
        Color  rowBg     = viewRow % 2 == 0 ? Color.WHITE : new Color(0xF3F0FA);

        // Inner panel holds the buttons with FlowLayout (wraps if needed)
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

        // Outer panel centers inner both vertically and horizontally
        JPanel outer = new JPanel(new GridBagLayout());
        outer.setBackground(rowBg);
        outer.setOpaque(true);
        outer.add(inner); // GridBagLayout default: center
        return outer;
    }

    private void stopEdit(JTable t) {
        if (t.isEditing()) t.getCellEditor().stopCellEditing();
    }

   private void xacNhanDon(JTable t, int row) {

    String saleCode = parent.tableModel.getValueAt(row, 0).toString();

    boolean ok = salesBUS.confirmSale(saleCode);

    if(ok){
        parent.tableModel.setValueAt("Đã xác nhận", row, 5);
        t.repaint();
    }
}

    private void huyDon(JTable t, int row) {
        int c = JOptionPane.showConfirmDialog(this,
                "Huỷ đơn " + parent.tableModel.getValueAt(row, 0) + "?",
                "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (c == JOptionPane.YES_OPTION) {
            parent.tableModel.setValueAt("Đã hủy", row, 5);
            t.repaint();
        }
    }
}
