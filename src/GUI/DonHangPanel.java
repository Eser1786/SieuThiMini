package GUI;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.print.*;

/**
 * Panel quản lý đơn hàng — layout co giãn theo cửa sổ.
 * Gồm 3 card:
 * - CARD_TABLE : Bảng danh sách đơn hàng
 * - CARD_DETAIL : Chi tiết / xác nhận / hủy đơn
 * - CARD_INVOICE : In hoá đơn
 */
public class DonHangPanel extends JPanel {

    /* ── hằng card ── */
    private static final String CARD_TABLE = "TABLE";
    private static final String CARD_DETAIL = "DETAIL";
    private static final String CARD_INVOICE = "INVOICE";
    private static final String CARD_CREATE = "CREATE"; // card tạo đơn mới

    private final CardLayout innerCard = new CardLayout();

    /* ── dữ liệu mẫu ── */
    private static final Object[][] SAMPLE_ORDERS = {
            { "HD001", "Beheocon", 5, "-10.000đ", "90.000đ", "Chờ xác nhận" },
            { "HD002", "Nguyễn Anh", 3, "-0đ", "150.000đ", "Đã xác nhận" },
            { "HD003", "Lê Hoàng", 7, "-20.000đ", "280.000đ", "Chờ vận chuyển" },
            { "HD004", "Trần Bảo", 2, "-5.000đ", "45.000đ", "Đang giao" },
            { "HD005", "Phạm Thu", 4, "-0đ", "200.000đ", "Đã giao" },
            { "HD006", "Võ Minh", 1, "-0đ", "36.000đ", "Đã hủy" },
    };

    private DefaultTableModel tableModel;
    private DefaultTableModel chitietModel;

    /* ── nhãn chi tiết ── */
    private JLabel lbMaDon, lbNgayDat, lbNgayGiao, lbTrangThai;
    private JLabel lbTenND, lbIdTK, lbSdt, lbDiaChi;
    private JLabel lbTongCong, lbPhiVC, lbMaGiam, lbTongTT, lbHinhThuc;

    /* ── nút footer chi tiết ── */
    private JButton btnXacNhan, btnHuy, btnInHoaDon;

    /* ── vùng chứa phiếu in (để load lại nội dung) ── */
    private JPanel invoiceCenter;

    private int currentRow = -1;

    public DonHangPanel() {
        setLayout(innerCard);
        add(buildTableCard(), CARD_TABLE);
        add(buildDetailCard(), CARD_DETAIL);
        add(buildInvoiceCard(), CARD_INVOICE);
        add(buildCreateCard(), CARD_CREATE);
        innerCard.show(this, CARD_TABLE);
    }

    /*
     * ═══════════════════════════════════════════════════════════
     * 1. BẢNG DANH SÁCH — đồng nhất với KhachHangPanel
     * ═══════════════════════════════════════════════════════════
     */
    private JPanel buildTableCard() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(new Color(0xF8F7FF));

        /* Toolbar — FlowLayout RIGHT giống KhachHangPanel */
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

        // ComboBox lọc
        JComboBox<String> cbLoc = new JComboBox<>(trangThais);
        cbLoc.setPreferredSize(new Dimension(220, 42));
        cbLoc.setFont(new Font("Arial", Font.PLAIN, 22));
        cbLoc.setBackground(new Color(0xD9D9D9));

        // Ô tìm kiếm
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

        // Nút xuất PDF
        JButton btnPDF = ExportUtils.makeExportButton("📄 Xuất PDF", new Color(0x7B52AB));
        btnPDF.addActionListener(e -> ExportUtils.xuatPDF(DonHangPanel.this, tableModel, "Danh sách đơn hàng"));
        top.add(btnPDF);

        // Nút xuất Excel (CSV)
        JButton btnExcel = ExportUtils.makeExportButton("📊 Xuất Excel", new Color(0x2E7D32));
        btnExcel.addActionListener(e -> ExportUtils.xuatCSV(DonHangPanel.this, tableModel, "don_hang"));
        top.add(btnExcel);

        // Nút tạo đơn hàng mới — style giống nút "+ Thêm khách hàng"
        JButton btnTao = new JButton("+ Tạo đơn hàng");
        btnTao.setFocusPainted(false);
        btnTao.setBackground(new Color(0xD9D9D9));
        btnTao.setPreferredSize(new Dimension(200, 42));
        btnTao.setFont(new Font("Arial", Font.BOLD, 18));
        btnTao.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnTao.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btnTao.setBackground(new Color(0xC5B3E6));
            }

            public void mouseExited(java.awt.event.MouseEvent e) {
                btnTao.setBackground(new Color(0xD9D9D9));
            }
        });
        btnTao.addActionListener(e -> innerCard.show(DonHangPanel.this, CARD_CREATE));
        top.add(btnTao);

        /* Bảng */
        String[] cols = { "Mã đơn", "Người mua", "Số lượng SP", "Khuyến mãi", "Tổng số tiền", "Tình trạng",
                "Thao tác" };
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return c == 6;
            }
        };
        for (Object[] row : SAMPLE_ORDERS)
            tableModel.addRow(new Object[] { row[0], row[1], row[2], row[3], row[4], row[5], "" });

        JTable bang = new JTable(tableModel);
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
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

        /* Renderer xen kẽ màu — tông tím nhạt F3F0FA giống KhachHangPanel */
        DefaultTableCellRenderer altR = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable t, Object val, boolean sel, boolean foc, int r, int c) {
                super.getTableCellRendererComponent(t, val, sel, foc, r, c);
                if (!sel) {
                    setBackground(r % 2 == 0 ? Color.WHITE : new Color(0xF3F0FA));
                    setForeground(Color.BLACK);
                    if (c == 3)
                        setForeground(new Color(0xC62828)); // khuyến mãi: đỏ
                    if (c == 4)
                        setForeground(new Color(0x388E3C)); // tổng tiền: xanh lá
                    if (c == 5) {
                        String v = val == null ? "" : val.toString();
                        switch (v) {
                            case "Chờ xác nhận" -> setForeground(new Color(0xE65100));
                            case "Đã xác nhận" -> setForeground(new Color(0x1565C0));
                            case "Chờ vận chuyển" -> setForeground(new Color(0x6A1B9A));
                            case "Đang giao" -> setForeground(new Color(0x00838F));
                            case "Đã giao" -> setForeground(new Color(0x2E7D32));
                            case "Đã hủy" -> setForeground(new Color(0xB71C1C));
                        }
                    }
                }
                setHorizontalAlignment(SwingConstants.CENTER);
                return this;
            }
        };
        for (int i = 0; i < 6; i++)
            bang.getColumnModel().getColumn(i).setCellRenderer(altR);

        /* Renderer & Editor cột Thao tác */
        bang.getColumnModel().getColumn(6).setCellRenderer(
                (t, val, sel, foc, r, c) -> buildActionPanel(t, r, false));

        bang.getColumnModel().getColumn(6).setCellEditor(new DefaultCellEditor(new JCheckBox()) {
            private JPanel panel;

            @Override
            public Component getTableCellEditorComponent(
                    JTable t, Object val, boolean sel, int r, int c) {
                panel = buildActionPanel(t, r, true);
                return panel;
            }

            @Override
            public Object getCellEditorValue() {
                return "";
            }
        });

        /* Lọc & tìm kiếm */
        Runnable applyFilter = () -> {
            String kw = tfTim.getText().trim();
            int idx = cbLoc.getSelectedIndex();
            RowFilter<DefaultTableModel, Integer> fSt = idx == 0 ? null
                    : RowFilter.regexFilter("(?i)^" + trangThais[idx] + "$", 5);
            RowFilter<DefaultTableModel, Integer> fSr = kw.isEmpty() ? null
                    : RowFilter.orFilter(java.util.List.of(
                            RowFilter.regexFilter("(?i)" + kw, 0),
                            RowFilter.regexFilter("(?i)" + kw, 1)));
            if (fSt != null && fSr != null)
                sorter.setRowFilter(RowFilter.andFilter(java.util.List.of(fSt, fSr)));
            else if (fSt != null)
                sorter.setRowFilter(fSt);
            else if (fSr != null)
                sorter.setRowFilter(fSr);
            else
                sorter.setRowFilter(null);
        };
        cbLoc.addActionListener(e -> applyFilter.run());
        btnTim.addActionListener(e -> applyFilter.run());
        tfTim.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                applyFilter.run();
            }

            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                applyFilter.run();
            }

            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                applyFilter.run();
            }
        });

        JScrollPane scroll = new JScrollPane(bang);
        scroll.setBorder(BorderFactory.createEmptyBorder());

        card.add(top, BorderLayout.NORTH);
        card.add(scroll, BorderLayout.CENTER);
        return card;
    }

    /* ── panel nút thao tác tuỳ trạng thái ── */
    private JPanel buildActionPanel(JTable table, int viewRow, boolean withAction) {
        int modelRow = table.convertRowIndexToModel(viewRow);
        String trangThai = tableModel.getValueAt(modelRow, 5).toString();

        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 7));
        p.setBackground(viewRow % 2 == 0 ? Color.WHITE : new Color(0xF3F0FA));
        p.setOpaque(true);

        JButton btnXem = UIUtils.makeActionButton("Xem đơn", new Color(0x6677C8));
        p.add(btnXem);

        switch (trangThai) {
            case "Chờ xác nhận" -> {
                JButton btnXN = UIUtils.makeActionButton("Xác nhận", new Color(0x6677C8));
                JButton btnHuy = UIUtils.makeActionButton("Hủy đơn", new Color(0xB83434));
                p.add(btnXN);
                p.add(btnHuy);
                if (withAction) {
                    btnXN.addActionListener(e -> {
                        stopEdit(table);
                        xacNhanDon(table, modelRow);
                    });
                    btnHuy.addActionListener(e -> {
                        stopEdit(table);
                        huyDon(table, modelRow);
                    });
                }
            }
            case "Đã xác nhận" -> {
                JButton btnIn = UIUtils.makeActionButton("In hoá đơn", new Color(0x6677C8));
                JButton btnHuy = UIUtils.makeActionButton("Hủy đơn", new Color(0xB83434));
                p.add(btnIn);
                p.add(btnHuy);
                if (withAction) {
                    btnIn.addActionListener(e -> {
                        stopEdit(table);
                        currentRow = modelRow;
                        loadInvoice(modelRow);
                        innerCard.show(DonHangPanel.this, CARD_INVOICE);
                    });
                    btnHuy.addActionListener(e -> {
                        stopEdit(table);
                        huyDon(table, modelRow);
                    });
                }
            }
            case "Chờ vận chuyển", "Đang giao", "Đã giao" -> {
                JButton btnTheo = UIUtils.makeActionButton("Theo dõi đơn", new Color(0x00838F));
                p.add(btnTheo);
                if (withAction)
                    btnTheo.addActionListener(e -> {
                        stopEdit(table);
                        JOptionPane.showMessageDialog(DonHangPanel.this,
                                "Mã đơn: " + tableModel.getValueAt(modelRow, 0) + "\nTrạng thái: " + trangThai,
                                "Theo dõi đơn hàng", JOptionPane.INFORMATION_MESSAGE);
                    });
            }
        }

        if (withAction)
            btnXem.addActionListener(e -> {
                stopEdit(table);
                currentRow = modelRow;
                loadDetail(modelRow);
                innerCard.show(DonHangPanel.this, CARD_DETAIL);
            });
        return p;
    }

    private void stopEdit(JTable t) {
        if (t.isEditing())
            t.getCellEditor().stopCellEditing();
    }

    /*
     * ═══════════════════════════════════════════════════════════
     * 2. CHI TIẾT ĐƠN HÀNG
     * ═══════════════════════════════════════════════════════════
     */
    private JPanel buildDetailCard() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(new Color(0xF0EFF8));

        /* Header */
        JPanel header = new JPanel(new BorderLayout(0, 0));
        header.setBackground(new Color(0xF0EFF8));
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0xCCCCCC)),
                BorderFactory.createEmptyBorder(10, 16, 10, 16)));

        JLabel lblTitle = new JLabel("Xem chi tiết đơn hàng");
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
        btnBack.addActionListener(e -> innerCard.show(DonHangPanel.this, CARD_TABLE));

        header.add(lblTitle, BorderLayout.WEST);
        header.add(btnBack, BorderLayout.EAST);
        card.add(header, BorderLayout.NORTH);

        /* Body */
        JPanel body = new JPanel(new GridBagLayout());
        body.setBackground(new Color(0xF0EFF8));
        body.setBorder(BorderFactory.createEmptyBorder(16, 32, 16, 32));
        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.HORIZONTAL;
        g.insets = new Insets(5, 8, 5, 8);

        Font lbFont = new Font("Arial", Font.BOLD, 15);
        Font valFont = new Font("Arial", Font.PLAIN, 15);
        Font secFont = new Font("Arial", Font.BOLD, 16);
        Color secBg = new Color(0xD1C4E9);

        // Nhãn đơn hàng
        lbMaDon = makeVal(valFont);
        lbNgayDat = makeVal(valFont);
        lbNgayGiao = makeVal(valFont);
        lbTrangThai = makeVal(valFont);

        addSection(body, g, 0, "Thông tin đơn hàng", secFont, secBg);
        addRow2(body, g, 1, "Mã đơn hàng:", lbMaDon, lbFont);
        addRow2(body, g, 2, "Ngày đặt hàng:", lbNgayDat, lbFont);
        addRow2(body, g, 3, "Ngày giao dự kiến:", lbNgayGiao, lbFont);
        addRow2(body, g, 4, "Trạng thái hiện tại:", lbTrangThai, lbFont);

        // Nhãn người mua
        lbTenND = makeVal(valFont);
        lbIdTK = makeVal(valFont);
        lbSdt = makeVal(valFont);
        lbDiaChi = makeVal(valFont);

        addSection(body, g, 5, "Thông tin người mua", secFont, secBg);
        addRow2(body, g, 6, "Tên người đặt:", lbTenND, lbFont);
        addRow2(body, g, 7, "ID tài khoản:", lbIdTK, lbFont);
        addRow2(body, g, 8, "Số điện thoại:", lbSdt, lbFont);
        addRow2(body, g, 9, "Địa chỉ:", lbDiaChi, lbFont);

        // Bảng sản phẩm
        addSection(body, g, 10, "Thông tin sản phẩm đã đặt", secFont, secBg);

        String[] spCols = { "STT", "Sản phẩm", "Số lượng", "Đơn giá", "Thành tiền" };
        chitietModel = new DefaultTableModel(spCols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        JTable tblSP = new JTable(chitietModel);
        tblSP.setFont(new Font("Arial", Font.PLAIN, 13));
        tblSP.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        tblSP.getTableHeader().setBackground(new Color(0xD1C4E9));
        tblSP.setRowHeight(28);
        tblSP.setShowVerticalLines(false);
        tblSP.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        JScrollPane spScroll = new JScrollPane(tblSP);
        spScroll.setPreferredSize(new Dimension(0, 145));
        spScroll.setBorder(BorderFactory.createLineBorder(new Color(0xCCCCCC)));

        g.gridx = 0;
        g.gridy = 11;
        g.gridwidth = 2;
        g.weightx = 1.0;
        body.add(spScroll, g);
        g.gridwidth = 1;

        // Tổng kết
        lbTongCong = makeVal(valFont);
        lbPhiVC = makeVal(valFont);
        lbMaGiam = makeVal(valFont);
        lbTongTT = makeVal(valFont);
        lbHinhThuc = makeVal(valFont);

        addRow2(body, g, 12, "Tổng cộng (ước tính):", lbTongCong, lbFont);
        addRow2(body, g, 13, "Phí vận chuyển:", lbPhiVC, lbFont);
        addRow2(body, g, 14, "Mã giảm giá:", lbMaGiam, lbFont);
        addRow2(body, g, 15, "Tổng số tiền cần thanh toán:", lbTongTT, lbFont);
        addRow2(body, g, 16, "Hình thức thanh toán:", lbHinhThuc, lbFont);

        // Padding cuối
        g.gridx = 0;
        g.gridy = 17;
        g.gridwidth = 2;
        g.weighty = 1.0;
        body.add(Box.createVerticalGlue(), g);

        JScrollPane bodyScroll = new JScrollPane(body);
        bodyScroll.setBorder(BorderFactory.createEmptyBorder());
        bodyScroll.getVerticalScrollBar().setUnitIncrement(16);
        card.add(bodyScroll, BorderLayout.CENTER);

        /* Footer */
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 12));
        footer.setBackground(new Color(0xF0EFF8));
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(0xCCCCCC)));

        btnXacNhan = makeFootBtn("Xác nhận đơn hàng", new Color(0x6677C8));
        btnInHoaDon = makeFootBtn("In hoá đơn", new Color(0x6677C8));
        btnHuy = makeFootBtn("Huỷ đơn hàng", new Color(0xB83434));

        btnXacNhan.addActionListener(e -> {
            if (currentRow < 0)
                return;
            tableModel.setValueAt("Đã xác nhận", currentRow, 5);
            JOptionPane.showMessageDialog(this, "Đã xác nhận đơn hàng!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            loadDetail(currentRow);
        });
        btnInHoaDon.addActionListener(e -> {
            if (currentRow < 0)
                return;
            loadInvoice(currentRow);
            innerCard.show(DonHangPanel.this, CARD_INVOICE);
        });
        btnHuy.addActionListener(e -> {
            if (currentRow < 0)
                return;
            int c = JOptionPane.showConfirmDialog(this, "Huỷ đơn?", "Xác nhận", JOptionPane.YES_NO_OPTION);
            if (c == JOptionPane.YES_OPTION) {
                tableModel.setValueAt("Đã hủy", currentRow, 5);
                JOptionPane.showMessageDialog(this, "Đã huỷ đơn hàng.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                loadDetail(currentRow);
            }
        });

        footer.add(btnXacNhan);
        footer.add(btnInHoaDon);
        footer.add(btnHuy);
        card.add(footer, BorderLayout.SOUTH);
        return card;
    }

    /*
     * ═══════════════════════════════════════════════════════════
     * 3. IN HOÁ ĐƠN
     * ═══════════════════════════════════════════════════════════
     */
    private JPanel buildInvoiceCard() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(new Color(0xEEEEEE));

        /* Header */
        JPanel ivHeader = new JPanel(new BorderLayout());
        ivHeader.setBackground(new Color(0xF0EFF8));
        ivHeader.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0xCCCCCC)),
                BorderFactory.createEmptyBorder(10, 16, 10, 16)));

        JLabel ivTitle = new JLabel("In mã vận đơn");
        ivTitle.setFont(new Font("Arial", Font.BOLD, 18));
        ivTitle.setForeground(new Color(0x444444));

        JButton btnBack = new JButton("← Quay lại");
        btnBack.setFont(new Font("Arial", Font.BOLD, 14));
        btnBack.setBackground(new Color(0x9B8EA8));
        btnBack.setForeground(Color.WHITE);
        btnBack.setFocusPainted(false);
        btnBack.setBorderPainted(false);
        btnBack.setPreferredSize(new Dimension(140, 38));
        btnBack.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnBack.addActionListener(e -> innerCard.show(DonHangPanel.this, CARD_DETAIL));

        ivHeader.add(ivTitle, BorderLayout.WEST);
        ivHeader.add(btnBack, BorderLayout.EAST);
        card.add(ivHeader, BorderLayout.NORTH);

        /* Vùng giữa — phiếu in căn giữa bằng GridBagLayout */
        invoiceCenter = new JPanel(new GridBagLayout());
        invoiceCenter.setBackground(new Color(0xEEEEEE));
        JScrollPane ivScroll = new JScrollPane(invoiceCenter);
        ivScroll.setBorder(BorderFactory.createEmptyBorder());
        ivScroll.getViewport().setBackground(new Color(0xEEEEEE));
        card.add(ivScroll, BorderLayout.CENTER);

        /* Footer nút In */
        JPanel ivFooter = new JPanel(new FlowLayout(FlowLayout.RIGHT, 24, 12));
        ivFooter.setBackground(new Color(0xEEEEEE));
        JButton btnPrint = makeFootBtn("In", new Color(0x6677C8));
        btnPrint.setPreferredSize(new Dimension(100, 42));
        btnPrint.addActionListener(e -> {
            if (invoiceCenter.getComponentCount() > 0)
                printPanel((JPanel) invoiceCenter.getComponent(0));
        });
        ivFooter.add(btnPrint);
        card.add(ivFooter, BorderLayout.SOUTH);
        return card;
    }

    /*
     * ═══════════════════════════════════════════════════════════
     * LOAD DỮ LIỆU
     * ═══════════════════════════════════════════════════════════
     */
    private void loadDetail(int modelRow) {
        String maDon = tableModel.getValueAt(modelRow, 0).toString();
        String nguoiMua = tableModel.getValueAt(modelRow, 1).toString();
        String tongTien = tableModel.getValueAt(modelRow, 4).toString();
        String trangThai = tableModel.getValueAt(modelRow, 5).toString();

        lbMaDon.setText(maDon);
        lbNgayDat.setText("05/03/2026 (22:28)");
        lbNgayGiao.setText("06/03/2026 (08:00)");
        lbTrangThai.setText("  " + trangThai + "  ");
        lbTrangThai.setOpaque(true);
        switch (trangThai) {
            case "Chờ xác nhận" -> {
                lbTrangThai.setBackground(new Color(0xFFECB3));
                lbTrangThai.setForeground(new Color(0xBF360C));
            }
            case "Đã xác nhận" -> {
                lbTrangThai.setBackground(new Color(0xC8E6C9));
                lbTrangThai.setForeground(new Color(0x1B5E20));
            }
            case "Chờ vận chuyển" -> {
                lbTrangThai.setBackground(new Color(0xE1BEE7));
                lbTrangThai.setForeground(new Color(0x4A148C));
            }
            case "Đang giao" -> {
                lbTrangThai.setBackground(new Color(0xB2EBF2));
                lbTrangThai.setForeground(new Color(0x006064));
            }
            case "Đã giao" -> {
                lbTrangThai.setBackground(new Color(0xDCEDC8));
                lbTrangThai.setForeground(new Color(0x33691E));
            }
            case "Đã hủy" -> {
                lbTrangThai.setBackground(new Color(0xFFCDD2));
                lbTrangThai.setForeground(new Color(0xB71C1C));
            }
            default -> {
                lbTrangThai.setBackground(new Color(0xEEEEEE));
                lbTrangThai.setForeground(Color.BLACK);
            }
        }

        lbTenND.setText(nguoiMua);
        lbIdTK.setText("TK" + (100000 + modelRow));
        lbSdt.setText("0102282828");
        lbDiaChi.setText("1/22, Đường số 1, Phường 2, Quận 3, TP.HCM");

        chitietModel.setRowCount(0);
        chitietModel.addRow(new Object[] { "1", "Cơm nắm cá hồi mayo", 1, "16.000", "16.000" });
        chitietModel.addRow(new Object[] { "2", "Mì ý sốt kem", 1, "36.000", "36.000" });
        chitietModel.addRow(new Object[] { "3", "Pepsi không calo", 1, "10.000", "10.000" });
        chitietModel.addRow(new Object[] { "4", "Kem si cu la", 2, "18.000", "38.000" });

        lbTongCong.setText("100.000đ");
        lbPhiVC.setText("Miễn phí");
        lbMaGiam.setText("10.000đ");
        lbTongTT.setText(tongTien);
        lbHinhThuc.setText("Thanh toán khi nhận hàng");

        // Hiện/ẩn nút hành động theo trạng thái
        btnXacNhan.setVisible(trangThai.equals("Chờ xác nhận"));
        btnInHoaDon.setVisible(trangThai.equals("Đã xác nhận"));
        btnHuy.setVisible(trangThai.equals("Chờ xác nhận") || trangThai.equals("Đã xác nhận"));
    }

    private void loadInvoice(int modelRow) {
        String maDon = tableModel.getValueAt(modelRow, 0).toString();
        String tongTT = tableModel.getValueAt(modelRow, 4).toString();

        invoiceCenter.removeAll();

        /* Tờ phiếu trắng có shadow */
        JPanel receipt = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 0, 0, 25));
                g2.fillRoundRect(5, 7, getWidth() - 3, getHeight() - 3, 12, 12);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth() - 4, getHeight() - 4, 12, 12);
            }
        };
        receipt.setOpaque(false);
        receipt.setLayout(new BoxLayout(receipt, BoxLayout.Y_AXIS));
        receipt.setBorder(BorderFactory.createEmptyBorder(22, 30, 22, 30));
        receipt.setPreferredSize(new Dimension(370, 570));

        receipt.add(rLine("TH36", new Font("Arial", Font.BOLD, 26)));
        receipt.add(Box.createVerticalStrut(4));
        receipt.add(dotLine());
        receipt.add(rLine("Hoá đơn giao hàng | Mã đơn: " + maDon, new Font("Arial", Font.PLAIN, 12)));
        receipt.add(rLine("Quầy: TH36-01    NV: NGUYỄN THỊ THÉO", new Font("Arial", Font.PLAIN, 12)));
        receipt.add(rLine("05/03/2026  (22:28)", new Font("Arial", Font.PLAIN, 12)));
        receipt.add(dotLine());

        receipt.add(gridRow(true, "Tên", "SL", "Đơn giá", "Thành tiền"));

        String[][] items = {
                { "1.Cơm nắm cá hồi mayo", "1", "16.000", "16.000" },
                { "2.Mì ý sốt kem", "1", "36.000", "36.000" },
                { "3.Pepsi không calo", "1", "10.000", "10.000" },
                { "4.Kem si cu la", "2", "18.000", "38.000" },
        };
        for (String[] it : items)
            receipt.add(gridRow(false, it));
        receipt.add(dotLine());

        sumRow(receipt, "Tổng", "100.000");
        sumRow(receipt, "Chiết khấu", "10.000");
        sumRow(receipt, "VAT(10%)", "3.636");
        sumRow(receipt, "Tổng tiền", tongTT);
        sumRow(receipt, "Tiền khách trả", tongTT);
        sumRow(receipt, "Tiền trả lại cho khách", "0");
        receipt.add(dotLine());

        JLabel bc = new JLabel("▌▌█▌█▌█▌▌▌█▌▌█▌█▌▌▌█▌█▌▌", SwingConstants.CENTER);
        bc.setFont(new Font("Courier New", Font.PLAIN, 18));
        bc.setAlignmentX(Component.CENTER_ALIGNMENT);
        bc.setMaximumSize(new Dimension(400, 30));
        receipt.add(bc);

        JLabel bcNum = new JLabel("VN2845598375038283", SwingConstants.CENTER);
        bcNum.setFont(new Font("Courier New", Font.PLAIN, 10));
        bcNum.setAlignmentX(Component.CENTER_ALIGNMENT);
        bcNum.setMaximumSize(new Dimension(400, 18));
        receipt.add(bcNum);
        receipt.add(dotLine());

        receipt.add(rLine("Hoá đơn chỉ có giá trị xuất trong ngày", new Font("Arial", Font.ITALIC, 10)));
        receipt.add(rLine("Hotline: 09437767345", new Font("Arial", Font.ITALIC, 10)));

        invoiceCenter.add(receipt, new GridBagConstraints());
        invoiceCenter.revalidate();
        invoiceCenter.repaint();
    }

    /*
     * ═══════════════════════════════════════════════════════════
     * HÀNH ĐỘNG
     * ═══════════════════════════════════════════════════════════
     */
    private void xacNhanDon(JTable t, int row) {
        tableModel.setValueAt("Đã xác nhận", row, 5);
        t.repaint();
        JOptionPane.showMessageDialog(this,
                "Đã xác nhận đơn " + tableModel.getValueAt(row, 0),
                "Thông báo", JOptionPane.INFORMATION_MESSAGE);
    }

    private void huyDon(JTable t, int row) {
        int c = JOptionPane.showConfirmDialog(this,
                "Huỷ đơn " + tableModel.getValueAt(row, 0) + "?",
                "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (c == JOptionPane.YES_OPTION) {
            tableModel.setValueAt("Đã hủy", row, 5);
            t.repaint();
        }
    }

    private void printPanel(JPanel panel) {
        PrinterJob job = PrinterJob.getPrinterJob();
        job.setPrintable((g, pf, pi) -> {
            if (pi > 0)
                return Printable.NO_SUCH_PAGE;
            Graphics2D g2 = (Graphics2D) g;
            g2.translate(pf.getImageableX(), pf.getImageableY());
            double s = Math.min(pf.getImageableWidth() / panel.getWidth(),
                    pf.getImageableHeight() / panel.getHeight());
            g2.scale(s, s);
            panel.printAll(g2);
            return Printable.PAGE_EXISTS;
        });
        if (job.printDialog())
            try {
                job.print();
            } catch (PrinterException ex) {
                JOptionPane.showMessageDialog(this, "Lỗi in: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
    }

    /*
     * ═══════════════════════════════════════════════════════════
     * HELPER
     * ═══════════════════════════════════════════════════════════
     */

    private JLabel makeVal(Font f) {
        JLabel l = new JLabel();
        l.setFont(f);
        return l;
    }

    private void addSection(JPanel p, GridBagConstraints g, int row, String text, Font f, Color bg) {
        g.gridx = 0;
        g.gridy = row;
        g.gridwidth = 2;
        g.weightx = 1.0;
        JLabel l = new JLabel(text);
        l.setFont(f);
        l.setOpaque(true);
        l.setBackground(bg);
        l.setBorder(BorderFactory.createEmptyBorder(5, 12, 5, 12));
        p.add(l, g);
        g.gridwidth = 1;
    }

    private void addRow2(JPanel p, GridBagConstraints g, int row, String label, JComponent val, Font lbF) {
        g.gridx = 0;
        g.gridy = row;
        g.weightx = 0.2;
        JLabel l = new JLabel(label);
        l.setFont(lbF);
        p.add(l, g);
        g.gridx = 1;
        g.weightx = 0.8;
        p.add(val, g);
    }

    private JButton makeFootBtn(String text, Color bg) {
        JButton b = new JButton(text);
        b.setFont(new Font("Arial", Font.BOLD, 14));
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setPreferredSize(new Dimension(180, 42));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }

    /* ── helper phiếu in ── */
    private JLabel rLine(String t, Font f) {
        JLabel l = new JLabel(t, SwingConstants.CENTER);
        l.setFont(f);
        l.setAlignmentX(Component.CENTER_ALIGNMENT);
        l.setMaximumSize(new Dimension(400, 22));
        return l;
    }

    private JLabel dotLine() {
        JLabel l = new JLabel("................................................................",
                SwingConstants.CENTER);
        l.setFont(new Font("Courier New", Font.PLAIN, 11));
        l.setAlignmentX(Component.CENTER_ALIGNMENT);
        l.setMaximumSize(new Dimension(400, 18));
        return l;
    }

    private JPanel gridRow(boolean bold, String... cells) {
        JPanel row = new JPanel(new GridLayout(1, cells.length));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(400, 24));
        for (String c : cells) {
            JLabel l = new JLabel(c, SwingConstants.CENTER);
            l.setFont(new Font("Arial", bold ? Font.BOLD : Font.PLAIN, 12));
            row.add(l);
        }
        return row;
    }

    private void sumRow(JPanel receipt, String name, String amount) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(400, 20));
        JLabel ln = new JLabel(name);
        ln.setFont(new Font("Arial", Font.PLAIN, 11));
        JLabel la = new JLabel(amount, SwingConstants.RIGHT);
        la.setFont(new Font("Arial", Font.PLAIN, 11));
        row.add(ln, BorderLayout.WEST);
        row.add(la, BorderLayout.EAST);
        receipt.add(row);
    }

    /*
     * ═══════════════════════════════════════════════════════════
     * 4. CARD TẠO ĐƠN HÀNG MỚI
     * ═══════════════════════════════════════════════════════════
     */
    private JPanel buildCreateCard() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(new Color(0xF0EFF8));

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
        btnBack.addActionListener(e -> innerCard.show(DonHangPanel.this, CARD_TABLE));

        header.add(lblTitle, BorderLayout.WEST);
        header.add(btnBack, BorderLayout.EAST);
        card.add(header, BorderLayout.NORTH);

        /* Body — GridBagLayout */
        JPanel body = new JPanel(new GridBagLayout());
        body.setBackground(new Color(0xF0EFF8));
        body.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.HORIZONTAL;
        g.insets = new Insets(8, 8, 8, 8);

        Font lbFont = new Font("Arial", Font.BOLD, 15);
        Font secFont = new Font("Arial", Font.BOLD, 16);
        Color secBg = new Color(0xD1C4E9);

        // Các trường nhập liệu
        JTextField tfTenND = UIUtils.makeField();
        JTextField tfSdt = UIUtils.makeField();
        JTextField tfDiaChi = UIUtils.makeField();

        String[] httt = { "Thanh toán khi nhận hàng", "Chuyển khoản", "Thẻ tín dụng" };
        JComboBox<String> cbHinhThuc = new JComboBox<>(httt);
        cbHinhThuc.setFont(new Font("Arial", Font.PLAIN, 18));
        cbHinhThuc.setBackground(new Color(0xF8F7FF));

        JTextArea taNotes = new JTextArea(3, 20);
        taNotes.setFont(new Font("Arial", Font.PLAIN, 15));
        taNotes.setLineWrap(true);
        taNotes.setBorder(BorderFactory.createLineBorder(new Color(0xAAAAAA)));

        // ── Thông tin người đặt ──
        addSec(body, g, 0, "Thông tin người đặt hàng", secFont, secBg);
        addRow2(body, g, 1, "Tên người mua:", makeValEdit(tfTenND), lbFont);
        addRow2(body, g, 2, "Số điện thoại:", makeValEdit(tfSdt), lbFont);
        g.gridx = 0;
        g.gridy = 3;
        g.weightx = 0.2;
        JLabel lbDC = new JLabel("Địa chỉ giao hàng:");
        lbDC.setFont(lbFont);
        body.add(lbDC, g);
        g.gridx = 1;
        g.weightx = 0.8;
        body.add(tfDiaChi, g);

        // ── Thông tin thanh toán ──
        addSec(body, g, 4, "Thanh toán", secFont, secBg);
        g.gridx = 0;
        g.gridy = 5;
        g.weightx = 0.2;
        JLabel lbHT = new JLabel("Hình thức thanh toán:");
        lbHT.setFont(lbFont);
        body.add(lbHT, g);
        g.gridx = 1;
        g.weightx = 0.8;
        body.add(cbHinhThuc, g);

        // ── Ghi chú ──
        addSec(body, g, 6, "Ghi chú đơn hàng", secFont, secBg);
        g.gridx = 0;
        g.gridy = 7;
        g.gridwidth = 2;
        g.weightx = 1.0;
        body.add(new JScrollPane(taNotes), g);
        g.gridwidth = 1;

        // ── Sản phẩm (bảng mini thêm SP thủ công) ──
        addSec(body, g, 8, "Sản phẩm (nhập thủ công)", secFont, secBg);

        String[] spCols = { "Tên sản phẩm", "Số lượng", "Đơn giá" };
        DefaultTableModel spModel = new DefaultTableModel(spCols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return true;
            }
        };
        // Thêm 1 dòng trống mặc định
        spModel.addRow(new Object[] { "", "1", "0" });

        JTable tblSP = new JTable(spModel);
        tblSP.setFont(new Font("Arial", Font.PLAIN, 14));
        tblSP.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        tblSP.getTableHeader().setBackground(new Color(0xD1C4E9));
        tblSP.setRowHeight(30);
        tblSP.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        JScrollPane spScroll = new JScrollPane(tblSP);
        spScroll.setPreferredSize(new Dimension(0, 130));

        g.gridx = 0;
        g.gridy = 9;
        g.gridwidth = 2;
        g.weightx = 1.0;
        body.add(spScroll, g);
        g.gridwidth = 1;

        // Nút thêm / xóa dòng SP
        JButton btnThemSP = new JButton("+ Thêm dòng");
        btnThemSP.setFont(new Font("Arial", Font.BOLD, 13));
        btnThemSP.setBackground(new Color(0xD9D9D9));
        btnThemSP.setFocusPainted(false);
        btnThemSP.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnThemSP.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btnThemSP.setBackground(new Color(0xC5B3E6));
            }

            public void mouseExited(java.awt.event.MouseEvent e) {
                btnThemSP.setBackground(new Color(0xD9D9D9));
            }
        });
        btnThemSP.addActionListener(e -> spModel.addRow(new Object[] { "", "1", "0" }));

        JButton btnXoaSP = new JButton("- Xóa dòng");
        btnXoaSP.setFont(new Font("Arial", Font.BOLD, 13));
        btnXoaSP.setBackground(new Color(0xEECCCC));
        btnXoaSP.setFocusPainted(false);
        btnXoaSP.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnXoaSP.addActionListener(e -> {
            int sel = tblSP.getSelectedRow();
            if (sel >= 0)
                spModel.removeRow(sel);
        });

        JPanel spBtnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        spBtnPanel.setOpaque(false);
        spBtnPanel.add(btnThemSP);
        spBtnPanel.add(btnXoaSP);

        g.gridx = 0;
        g.gridy = 10;
        g.gridwidth = 2;
        g.weightx = 1.0;
        body.add(spBtnPanel, g);
        g.gridwidth = 1;

        // Padding cuối
        g.gridx = 0;
        g.gridy = 11;
        g.gridwidth = 2;
        g.weighty = 1.0;
        body.add(Box.createVerticalGlue(), g);

        JScrollPane bodyScroll = new JScrollPane(body);
        bodyScroll.setBorder(BorderFactory.createEmptyBorder());
        bodyScroll.getVerticalScrollBar().setUnitIncrement(16);
        card.add(bodyScroll, BorderLayout.CENTER);

        /* Footer */
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 12));
        footer.setBackground(new Color(0xF0EFF8));
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(0xCCCCCC)));

        JButton btnLuu = makeFootBtn("💾 Lưu đơn hàng", new Color(0x6677C8));
        JButton btnHuy = makeFootBtn("✕ Hủy bỏ", new Color(0xB83434));

        btnHuy.addActionListener(e -> innerCard.show(DonHangPanel.this, CARD_TABLE));

        btnLuu.addActionListener(e -> {
            String ten = tfTenND.getText().trim();
            String sdt = tfSdt.getText().trim();
            if (ten.isEmpty() || sdt.isEmpty()) {
                JOptionPane.showMessageDialog(DonHangPanel.this,
                        "Vui lòng nhập Tên người mua và Số điện thoại!",
                        "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            // Tính tổng từ bảng sản phẩm
            long tongCong = 0;
            for (int r = 0; r < spModel.getRowCount(); r++) {
                try {
                    long sl = Long.parseLong(spModel.getValueAt(r, 1).toString().replaceAll("[^0-9]", ""));
                    long gia = Long.parseLong(spModel.getValueAt(r, 2).toString().replaceAll("[^0-9]", ""));
                    tongCong += sl * gia;
                } catch (NumberFormatException ex) {
                    /* bỏ qua dòng lỗi */ }
            }
            // Sinh mã đơn tự động
            String maDon = "HD" + String.format("%03d", tableModel.getRowCount() + 1);
            tableModel.addRow(new Object[] {
                    maDon, ten,
                    spModel.getRowCount(),
                    "-0đ",
                    String.format("%,.0f", (double) tongCong) + "đ",
                    "Chờ xác nhận", ""
            });
            JOptionPane.showMessageDialog(DonHangPanel.this,
                    "Đã tạo đơn hàng " + maDon + " thành công!",
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);
            // Reset form
            tfTenND.setText("");
            tfSdt.setText("");
            tfDiaChi.setText("");
            taNotes.setText("");
            spModel.setRowCount(0);
            spModel.addRow(new Object[] { "", "1", "0" });
            innerCard.show(DonHangPanel.this, CARD_TABLE);
        });

        footer.add(btnLuu);
        footer.add(btnHuy);
        card.add(footer, BorderLayout.SOUTH);
        return card;
    }

    /** Trả về component bao JTextField để dùng với addRow2 */
    private JComponent makeValEdit(JTextField tf) {
        return tf;
    }

    /** addSection dùng gridwidth=2 */
    private void addSec(JPanel p, GridBagConstraints g, int row, String text, Font f, Color bg) {
        g.gridx = 0;
        g.gridy = row;
        g.gridwidth = 2;
        g.weightx = 1.0;
        JLabel l = new JLabel(text);
        l.setFont(f);
        l.setOpaque(true);
        l.setBackground(bg);
        l.setBorder(BorderFactory.createEmptyBorder(5, 12, 5, 12));
        p.add(l, g);
        g.gridwidth = 1;
    }
}
