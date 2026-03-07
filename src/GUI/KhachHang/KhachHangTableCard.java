package GUI.KhachHang;

import GUI.ExportUtils;
import GUI.UIUtils;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.Dialog;
import java.util.List;

class KhachHangTableCard extends JPanel {

    private final KhachHangPanel parent;

    KhachHangTableCard(KhachHangPanel parent) {
        super(new BorderLayout());
        this.parent = parent;
        setBackground(new Color(0xF8F7FF));

        JTable bang = new JTable(parent.tableModel);
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(parent.tableModel);
        bang.setRowSorter(sorter);

        // ── TOP TOOLBAR ─────────────────────────────────────────────────────
        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(new Color(0xF8F7FF));
        top.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xCCCCCC), 1),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));

        // Left: filter + search
        JPanel leftRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));
        leftRow.setBackground(new Color(0xF8F7FF));

        String[] boloc = { "Tất cả", "Đồng", "Bạc", "Vàng", "Kim cương", "Hoạt động", "Không hoạt động" };
        JComboBox<String> cbLoc = new JComboBox<>(boloc);
        cbLoc.setPreferredSize(new Dimension(200, 36));
        UIUtils.styleComboBox(cbLoc);

        JPanel timkiem = new JPanel(new BorderLayout());
        timkiem.setPreferredSize(new Dimension(220, 36));
        timkiem.setBackground(Color.WHITE);
        timkiem.setBorder(BorderFactory.createLineBorder(new Color(0xBBBBBB), 1));
        JTextField tim = new JTextField();
        tim.setFont(new Font("Arial", Font.PLAIN, 13));
        tim.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 4));
        timkiem.add(tim, BorderLayout.CENTER);

        JButton nuttim = new JButton("\uD83D\uDD0D");
        nuttim.setBorderPainted(false);
        nuttim.setContentAreaFilled(false);
        nuttim.setFocusPainted(false);
        nuttim.setCursor(new Cursor(Cursor.HAND_CURSOR));
        nuttim.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                nuttim.setContentAreaFilled(true);
                nuttim.setBackground(new Color(0xC5B3E6));
                nuttim.setOpaque(true);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                nuttim.setContentAreaFilled(false);
                nuttim.setOpaque(false);
            }
        });
        timkiem.add(nuttim, BorderLayout.EAST);

        JLabel lbLoc = new JLabel("Lọc:");
        lbLoc.setFont(new Font("Arial", Font.PLAIN, 13));
        JLabel lbTim = new JLabel("Tìm kiếm:");
        lbTim.setFont(new Font("Arial", Font.PLAIN, 13));

        leftRow.add(lbLoc);
        leftRow.add(cbLoc);
        leftRow.add(Box.createHorizontalStrut(6));
        leftRow.add(lbTim);
        leftRow.add(timkiem);

        // Right: Thêm + export buttons
        JPanel rightRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
        rightRow.setBackground(new Color(0xF8F7FF));

        JButton them = new JButton("+ Thêm khách hàng");
        them.setFocusPainted(false);
        them.setBackground(new Color(0xD9D9D9));
        them.setFont(new Font("Arial", Font.BOLD, 13));
        them.setBorder(BorderFactory.createEmptyBorder(9, 14, 9, 14));
        them.setOpaque(true);
        them.setBorderPainted(false);
        them.setCursor(new Cursor(Cursor.HAND_CURSOR));
        them.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) { them.setBackground(new Color(0xC5B3E6)); }
            public void mouseExited(java.awt.event.MouseEvent evt)  { them.setBackground(new Color(0xD9D9D9)); }
        });
        them.addActionListener(e -> showAddDialog());

        Runnable applyFilter = () -> {
            String tuKhoa = tim.getText().trim();
            int idxLoc = cbLoc.getSelectedIndex();
            RowFilter<DefaultTableModel, Integer> filterLoc = null;
            switch (idxLoc) {
                case 1: filterLoc = RowFilter.regexFilter("(?i)^Đồng$", 5); break;
                case 2: filterLoc = RowFilter.regexFilter("(?i)^Bạc$", 5); break;
                case 3: filterLoc = RowFilter.regexFilter("(?i)^Vàng$", 5); break;
                case 4: filterLoc = RowFilter.regexFilter("(?i)^Kim cương$", 5); break;
                case 5: filterLoc = RowFilter.regexFilter("(?i)^Hoạt động$", 6); break;
                case 6: filterLoc = RowFilter.regexFilter("(?i)^Không hoạt động$", 6); break;
                default: filterLoc = null; break;
            }
            RowFilter<DefaultTableModel, Integer> filterTim = null;
            if (!tuKhoa.isEmpty()) {
                filterTim = RowFilter.orFilter(java.util.List.of(
                        RowFilter.regexFilter("(?i)" + tuKhoa, 0),
                        RowFilter.regexFilter("(?i)" + tuKhoa, 1),
                        RowFilter.regexFilter("(?i)" + tuKhoa, 2)));
            }
            if (filterLoc != null && filterTim != null)
                sorter.setRowFilter(RowFilter.andFilter(java.util.List.of(filterLoc, filterTim)));
            else if (filterLoc != null) sorter.setRowFilter(filterLoc);
            else if (filterTim != null) sorter.setRowFilter(filterTim);
            else sorter.setRowFilter(null);
        };
        cbLoc.addActionListener(e -> applyFilter.run());
        nuttim.addActionListener(e -> applyFilter.run());
        tim.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e)  { applyFilter.run(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e)  { applyFilter.run(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { applyFilter.run(); }
        });

        JButton btnPDF = ExportUtils.makeExportButton("Xuất PDF", new Color(0x7B52AB));
        btnPDF.addActionListener(e -> ExportUtils.xuatPDF(parent, parent.tableModel, "Danh sách khách hàng"));

        JButton btnExcel = ExportUtils.makeExportButton("Xuất Excel", new Color(0x2E7D32));
        btnExcel.addActionListener(e -> ExportUtils.xuatCSV(parent, parent.tableModel, "khach_hang"));

        JButton btnImport = ExportUtils.makeImportButton("Nhập CSV");
        btnImport.addActionListener(e -> {
            List<String[]> rows = ExportUtils.importCSV(parent);
            if (rows == null) return;
            for (String[] r : rows) { if (r.length < 8) continue; parent.tableModel.addRow((Object[]) r); }
        });

        rightRow.add(them);
        rightRow.add(btnPDF);
        rightRow.add(btnExcel);
        rightRow.add(btnImport);

        top.add(leftRow, BorderLayout.WEST);
        top.add(rightRow, BorderLayout.EAST);

        // ── TABLE STYLING ────────────────────────────────────────────────────
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

        bang.getColumnModel().getColumn(0).setPreferredWidth(80);
        bang.getColumnModel().getColumn(1).setPreferredWidth(150);
        bang.getColumnModel().getColumn(2).setPreferredWidth(120);
        bang.getColumnModel().getColumn(3).setPreferredWidth(180);
        bang.getColumnModel().getColumn(4).setPreferredWidth(200);
        bang.getColumnModel().getColumn(5).setPreferredWidth(80);
        bang.getColumnModel().getColumn(6).setPreferredWidth(100);
        bang.getColumnModel().getColumn(7).setPreferredWidth(100);

        DefaultTableCellRenderer altRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(0xF3F0FA));
                    setHorizontalAlignment(SwingConstants.CENTER);
                    if (column == 5) {
                        String val = value == null ? "" : value.toString();
                        switch (val) {
                            case "Đồng"      -> setForeground(new Color(0x8B4513));
                            case "Bạc"       -> setForeground(new Color(0x808080));
                            case "Vàng"      -> setForeground(new Color(0xFFD700));
                            case "Kim cương" -> setForeground(new Color(0x00BFFF));
                            default          -> setForeground(Color.BLACK);
                        }
                    } else if (column == 6) {
                        String val = value == null ? "" : value.toString();
                        setForeground(val.equals("Hoạt động") ? new Color(0x388E3C) : new Color(0xC62828));
                    } else {
                        setForeground(Color.BLACK);
                    }
                }
                return c;
            }
        };
        for (int i = 0; i < bang.getColumnCount() - 1; i++)
            bang.getColumnModel().getColumn(i).setCellRenderer(altRenderer);

        bang.getColumnModel().getColumn(7).setCellRenderer(new TableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {
                JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 8));
                p.setBackground(row % 2 == 0 ? Color.WHITE : new Color(0xF3F0FA));
                p.add(UIUtils.makeActionButton("Chi tiết", new Color(0x6677C8)));
                return p;
            }
        });

        bang.getColumnModel().getColumn(7).setCellEditor(new DefaultCellEditor(new JCheckBox()) {
            private final JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 8));
            private final JButton chiTiet = UIUtils.makeActionButton("Chi tiết", new Color(0x6677C8));
            private int currentRow = -1;
            {
                p.setOpaque(true);
                p.add(chiTiet);
                chiTiet.addActionListener(e -> {
                    fireEditingStopped();
                    int modelRow = bang.convertRowIndexToModel(currentRow);
                    showDetailDialog(modelRow, bang);
                });
            }
            @Override
            public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
                currentRow = row;
                p.setBackground(row % 2 == 0 ? Color.WHITE : new Color(0xF3F0FA));
                return p;
            }
            @Override public Object getCellEditorValue() { return ""; }
        });

        JScrollPane bangScroll = new JScrollPane(bang);
        UIUtils.styleScrollPane(bangScroll);

        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(new Color(0xF8F7FF));
        content.add(bangScroll, BorderLayout.CENTER);

        // ── HEADER ──────────────────────────────────────────────────────────
        JPanel khHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 12));
        khHeader.setBackground(new Color(0xF8F7FF));
        khHeader.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0xDDDDDD)),
                BorderFactory.createEmptyBorder(0, 20, 0, 20)));
        JPanel khBar = new JPanel();
        khBar.setPreferredSize(new Dimension(5, 26));
        khBar.setBackground(new Color(0x5C4A7F));
        khHeader.add(khBar);
        khHeader.add(Box.createHorizontalStrut(12));
        JLabel khTitle = new JLabel("QUẢN LÝ KHÁCH HÀNG");
        khTitle.setFont(new Font("Arial", Font.BOLD, 20));
        khHeader.add(khTitle);

        JPanel north = new JPanel();
        north.setLayout(new BoxLayout(north, BoxLayout.Y_AXIS));
        north.add(khHeader);
        north.add(top);

        add(north, BorderLayout.NORTH);
        add(content, BorderLayout.CENTER);
    }

    private void showDetailDialog(int modelRow, JTable bang) {
        Window owner = SwingUtilities.getWindowAncestor(parent);
        JDialog detail = new JDialog(owner, "Chi tiết khách hàng", Dialog.ModalityType.APPLICATION_MODAL);
        detail.setSize(600, 650);
        detail.setLocationRelativeTo(parent);
        detail.setResizable(false);
        detail.getContentPane().setBackground(new Color(0xF0EFF8));
        detail.setLayout(new BorderLayout(0, 0));

        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 14));
        header.setBackground(new Color(0xAF9FCB));
        JLabel lblTitle = new JLabel("Thông tin khách hàng chi tiết");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitle.setForeground(Color.WHITE);
        header.add(lblTitle);
        detail.add(header, BorderLayout.NORTH);

        String maKH     = parent.tableModel.getValueAt(modelRow, 0).toString();
        String ten      = parent.tableModel.getValueAt(modelRow, 1).toString();
        String sdt      = parent.tableModel.getValueAt(modelRow, 2).toString();
        String email    = parent.tableModel.getValueAt(modelRow, 3).toString();
        String diaChi   = parent.tableModel.getValueAt(modelRow, 4).toString();
        String hang     = parent.tableModel.getValueAt(modelRow, 5).toString();
        String trangThai= parent.tableModel.getValueAt(modelRow, 6).toString();
        String diem       = parent.getDiemFromData(maKH);
        String tgDK       = parent.getTgDKFromData(maKH);
        String lanCuoiMua = parent.getLanCuoiMuaFromData(maKH);
        String tongTien   = parent.getTongTienFromData(maKH);

        String[] labels = {
            "Mã KH:", "Tên khách hàng:", "Số điện thoại:", "Email:",
            "Địa chỉ:", "Điểm tích lũy:", "Thời gian đăng kí:",
            "Lần cuối mua hàng:", "Tổng tiền đã mua:", "Hạng:", "Trạng thái:"
        };
        Object[] values = { maKH, ten, sdt, email, diaChi, diem, tgDK, lanCuoiMua, tongTien, hang, trangThai };

        JPanel body = new JPanel(new GridLayout(labels.length, 2, 10, 10));
        body.setBackground(new Color(0xF0EFF8));
        body.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        for (int i = 0; i < labels.length; i++) {
            JLabel lbl = new JLabel(labels[i]);
            lbl.setFont(new Font("Arial", Font.BOLD, 15));
            JLabel val = new JLabel(values[i] == null ? "-" : values[i].toString());
            val.setFont(new Font("Arial", Font.PLAIN, 15));
            if (i == 9) {
                switch (values[i].toString()) {
                    case "Đồng"      -> val.setForeground(new Color(0x8B4513));
                    case "Bạc"       -> val.setForeground(new Color(0x808080));
                    case "Vàng"      -> val.setForeground(new Color(0xFFD700));
                    case "Kim cương" -> val.setForeground(new Color(0x00BFFF));
                }
            } else if (i == 10) {
                val.setForeground(values[i].toString().equals("Hoạt động") ? new Color(0x388E3C) : new Color(0xC62828));
            }
            body.add(lbl);
            body.add(val);
        }
        JScrollPane scrollDetail = new JScrollPane(body);
        scrollDetail.setBorder(null);
        scrollDetail.getVerticalScrollBar().setUnitIncrement(16);
        detail.add(scrollDetail, BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 14));
        footer.setBackground(new Color(0xF0EFF8));
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(0xCCCCCC)));

        JButton btnSuaDlg = new JButton("✏ Sửa");
        styleButton(btnSuaDlg, new Color(0x6677C8), 100, 40);
        btnSuaDlg.addActionListener(e -> {
            detail.dispose();
            parent.editingRow = modelRow;
            parent.tfMaKH.setText(maKH);
            parent.tfTen.setText(ten);
            parent.tfSdt.setText(sdt);
            parent.tfEmail.setText(email);
            parent.tfDiaChi.setText(diaChi);
            parent.tfDiem.setText(diem);
            parent.tfTgDK.setText(tgDK);
            parent.tfLanCuoiMua.setText(lanCuoiMua);
            parent.tfTongTien.setText(tongTien);
            parent.tfHang.setText(hang);
            parent.tfTrangThai.setText(trangThai);
            parent.enableFormFields(true);
            parent.innerCard.show(parent, KhachHangPanel.CARD_THEM);
        });

        JButton btnXoaDlg = new JButton("🗑 Xóa");
        styleButton(btnXoaDlg, new Color(0xB83434), 100, 40);
        btnXoaDlg.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(detail,
                "Bạn có chắc muốn xóa khách hàng \"" + ten + "\"?",
                "Xác nhận xóa", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (confirm == JOptionPane.YES_OPTION) {
                parent.tableModel.removeRow(modelRow);
                detail.dispose();
            }
        });

        JButton btnDong = new JButton("Đóng");
        styleButton(btnDong, new Color(0x9B8EA8), 100, 40);
        btnDong.addActionListener(e -> detail.dispose());

        footer.add(btnSuaDlg);
        footer.add(btnXoaDlg);
        footer.add(btnDong);
        detail.add(footer, BorderLayout.SOUTH);
        detail.setVisible(true);
    }

    private void showAddDialog() {
        Window owner = SwingUtilities.getWindowAncestor(parent);
        JDialog dlg = new JDialog(owner, "Thêm khách hàng", Dialog.ModalityType.APPLICATION_MODAL);
        dlg.setResizable(false);
        dlg.setLayout(new BorderLayout());

        // ── Header ────────────────────────────────────────────────────────
        JPanel hdr = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 14));
        hdr.setBackground(new Color(0xAF9FCB));
        JLabel hdrLbl = new JLabel("Thêm khách hàng mới");
        hdrLbl.setFont(new Font("Arial", Font.BOLD, 18));
        hdrLbl.setForeground(Color.WHITE);
        hdr.add(hdrLbl);
        dlg.add(hdr, BorderLayout.NORTH);

        // ── Form (2-column GridBagLayout) ─────────────────────────────────
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(new Color(0xF0EFF8));
        form.setBorder(BorderFactory.createEmptyBorder(18, 28, 18, 28));
        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.HORIZONTAL;
        g.insets = new Insets(7, 6, 7, 6);
        Font lf = new Font("Arial", Font.BOLD, 13);
        Dimension fd = new Dimension(200, 32);

        JTextField fMaKH   = UIUtils.makeField(); fMaKH.setPreferredSize(fd);
        JTextField fTen    = UIUtils.makeField(); fTen.setPreferredSize(fd);
        JTextField fSdt    = UIUtils.makeField(); fSdt.setPreferredSize(fd);
        JTextField fEmail  = UIUtils.makeField(); fEmail.setPreferredSize(fd);
        JTextField fDiaChi = UIUtils.makeField(); fDiaChi.setPreferredSize(fd);

        JComboBox<String> cbHang = new JComboBox<>(new String[]{"Đồng", "Bạc", "Vàng", "Kim cương"});
        cbHang.setPreferredSize(fd);
        UIUtils.styleComboBox(cbHang);

        JComboBox<String> cbTT = new JComboBox<>(new String[]{"Hoạt động", "Không hoạt động"});
        cbTT.setPreferredSize(fd);
        UIUtils.styleComboBox(cbTT);

        Object[][] rows = {
            { "Mã KH:",         fMaKH,  "Tên khách hàng:", fTen   },
            { "Số điện thoại:", fSdt,   "Email:",           fEmail  },
            { "Hạng:",          cbHang, "Trạng thái:",      cbTT   },
        };
        for (int i = 0; i < rows.length; i++) {
            g.gridy = i;
            g.gridx = 0; g.weightx = 0;
            JLabel l0 = new JLabel((String) rows[i][0]); l0.setFont(lf);
            form.add(l0, g);
            g.gridx = 1; g.weightx = 1;
            form.add((Component) rows[i][1], g);
            g.gridx = 2; g.weightx = 0;
            JLabel l1 = new JLabel((String) rows[i][2]); l1.setFont(lf);
            form.add(l1, g);
            g.gridx = 3; g.weightx = 1;
            form.add((Component) rows[i][3], g);
        }
        // Địa chỉ — full width
        g.gridy = rows.length; g.gridx = 0; g.weightx = 0; g.gridwidth = 1;
        JLabel lbDC = new JLabel("Địa chỉ:"); lbDC.setFont(lf);
        form.add(lbDC, g);
        g.gridx = 1; g.weightx = 1; g.gridwidth = 3;
        form.add(fDiaChi, g);
        g.gridwidth = 1;

        dlg.add(form, BorderLayout.CENTER);

        // ── Footer ────────────────────────────────────────────────────────
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 12));
        footer.setBackground(new Color(0xF0EFF8));
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(0xCCCCCC)));

        JButton btnLuu = new JButton("Lưu");
        btnLuu.setFont(new Font("Arial", Font.BOLD, 13));
        btnLuu.setBackground(new Color(0x5C4A7F));
        btnLuu.setForeground(Color.WHITE);
        btnLuu.setBorder(BorderFactory.createEmptyBorder(9, 22, 9, 22));
        btnLuu.setOpaque(true); btnLuu.setBorderPainted(false); btnLuu.setFocusPainted(false);
        btnLuu.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JButton btnHuy = new JButton("Hủy");
        btnHuy.setFont(new Font("Arial", Font.BOLD, 13));
        btnHuy.setBackground(new Color(0x9B8EA8));
        btnHuy.setForeground(Color.WHITE);
        btnHuy.setBorder(BorderFactory.createEmptyBorder(9, 22, 9, 22));
        btnHuy.setOpaque(true); btnHuy.setBorderPainted(false); btnHuy.setFocusPainted(false);
        btnHuy.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnHuy.addActionListener(e -> dlg.dispose());

        btnLuu.addActionListener(e -> {
            if (fMaKH.getText().trim().isEmpty() || fTen.getText().trim().isEmpty()
                    || fSdt.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(dlg,
                    "Vui lòng nhập Mã KH, Tên và Số điện thoại!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            parent.tableModel.addRow(new Object[]{
                fMaKH.getText().trim(), fTen.getText().trim(), fSdt.getText().trim(),
                fEmail.getText().trim(), fDiaChi.getText().trim(),
                cbHang.getSelectedItem().toString(), cbTT.getSelectedItem().toString(), ""
            });
            dlg.dispose();
        });
        footer.add(btnLuu);
        footer.add(btnHuy);
        dlg.add(footer, BorderLayout.SOUTH);

        dlg.pack();
        dlg.setMinimumSize(new Dimension(640, dlg.getPreferredSize().height));
        dlg.setLocationRelativeTo(parent);
        dlg.setVisible(true);
    }

    private static void styleButton(JButton btn, Color bg, int width, int height) {
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setPreferredSize(new Dimension(width, height));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setFont(new Font("Arial", Font.BOLD, 14));
    }

}
