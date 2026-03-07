package GUI.KhuyenMai;

import com.toedter.calendar.JDateChooser;
import java.text.SimpleDateFormat;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.Dialog;
import java.util.ArrayList;
import java.util.List;
import BUS.DiscountBUS;
import DTO.DiscountDTO;
import GUI.ExportUtils;
import GUI.UIUtils;

public class KhuyenMaiPanel extends JPanel {

    private static final Color PAGE_BG = new Color(0xF8F7FF);
    private static final Color ACCENT  = new Color(0x5C4A7F);
    private static final Color TBL_HDR = new Color(0xAF9FCB);

    private final DiscountBUS discountBUS = new DiscountBUS();
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter;

    public KhuyenMaiPanel() {
        setLayout(new BorderLayout());
        setBackground(PAGE_BG);

        // ── Model ─────────────────────────────────────────────────────────────
        String[] cols = { "Mã", "Tên khuyến mãi", "Giá trị", "Loại giảm",
                "Ngày bắt đầu", "Ngày kết thúc", "Trạng thái", "Thao tác" };
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return c == 7; }
        };
        sorter = new TableRowSorter<>(tableModel);

        // ── Header ────────────────────────────────────────────────────────────
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 12));
        header.setBackground(PAGE_BG);
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0xDDDDDD)),
                BorderFactory.createEmptyBorder(0, 20, 0, 20)));
        JPanel bar = new JPanel();
        bar.setPreferredSize(new Dimension(5, 26));
        bar.setBackground(ACCENT);
        header.add(bar);
        header.add(Box.createHorizontalStrut(12));
        JLabel hdrTitle = new JLabel("QUẢN LÝ KHUYẾN MÃI");
        hdrTitle.setFont(new Font("Arial", Font.BOLD, 20));
        header.add(hdrTitle);

        // ── Toolbar ───────────────────────────────────────────────────────────
        JPanel toolbar = new JPanel(new BorderLayout());
        toolbar.setBackground(PAGE_BG);
        toolbar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xCCCCCC), 1),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));

        JPanel tbLeft = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));
        tbLeft.setBackground(PAGE_BG);

        String[] loaiGiam = { "Tất cả", "PERCENT", "FIXED" };
        JComboBox<String> cbLoc = new JComboBox<>(loaiGiam);
        cbLoc.setPreferredSize(new Dimension(160, 38));
        UIUtils.styleComboBox(cbLoc);

        String[] trangThaiList = { "Tất cả", "ACTIVE", "EXPIRED", "INACTIVE" };
        JComboBox<String> cbTrangThai = new JComboBox<>(trangThaiList);
        cbTrangThai.setPreferredSize(new Dimension(150, 38));
        UIUtils.styleComboBox(cbTrangThai);

        JPanel timPanel = new JPanel(new BorderLayout());
        timPanel.setPreferredSize(new Dimension(220, 38));
        timPanel.setBackground(Color.WHITE);
        timPanel.setBorder(BorderFactory.createLineBorder(new Color(0xBBBBBB), 1));
        JTextField tfTim = new JTextField();
        tfTim.setFont(new Font("Arial", Font.PLAIN, 13));
        tfTim.setBorder(BorderFactory.createEmptyBorder(2, 8, 2, 4));
        JButton btnTim = new JButton("\uD83D\uDD0D");
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

        JLabel lbLoc = new JLabel("Loại giảm:");  lbLoc.setFont(new Font("Arial", Font.PLAIN, 13));
        JLabel lbTT  = new JLabel("Trạng thái:"); lbTT.setFont(new Font("Arial", Font.PLAIN, 13));
        JLabel lbTim = new JLabel("Tìm kiếm:");   lbTim.setFont(new Font("Arial", Font.PLAIN, 13));

        tbLeft.add(lbLoc); tbLeft.add(cbLoc);
        tbLeft.add(Box.createHorizontalStrut(4));
        tbLeft.add(lbTT);  tbLeft.add(cbTrangThai);
        tbLeft.add(Box.createHorizontalStrut(6));
        tbLeft.add(lbTim); tbLeft.add(timPanel);

        JPanel tbRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
        tbRight.setBackground(PAGE_BG);

        JButton btnThem = new JButton("+ Thêm khuyến mãi");
        btnThem.setFont(new Font("Arial", Font.BOLD, 13));
        btnThem.setBackground(new Color(0xD9D9D9));
        btnThem.setBorder(BorderFactory.createEmptyBorder(9, 14, 9, 14));
        btnThem.setOpaque(true); btnThem.setBorderPainted(false); btnThem.setFocusPainted(false);
        btnThem.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnThem.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) { btnThem.setBackground(new Color(0xC5B3E6)); }
            public void mouseExited(java.awt.event.MouseEvent e)  { btnThem.setBackground(new Color(0xD9D9D9)); }
        });
        btnThem.addActionListener(e -> showAddDialog());

        JButton btnPDF    = ExportUtils.makeExportButton("Xuất PDF",   new Color(0x7B52AB));
        JButton btnExcel  = ExportUtils.makeExportButton("Xuất Excel", new Color(0x2E7D32));
        JButton btnImport = ExportUtils.makeImportButton("Nhập CSV");
        btnPDF.addActionListener(e -> ExportUtils.xuatPDF(this, tableModel, "Danh sách khuyến mãi"));
        btnExcel.addActionListener(e -> ExportUtils.xuatCSV(this, tableModel, "khuyen_mai"));
        btnImport.addActionListener(e -> {
            List<String[]> rows = ExportUtils.importCSV(this);
            if (rows == null) return;
            for (String[] r : rows) { if (r.length < 7) continue; tableModel.addRow((Object[]) r); }
        });

        tbRight.add(btnThem); tbRight.add(btnPDF); tbRight.add(btnExcel); tbRight.add(btnImport);
        toolbar.add(tbLeft, BorderLayout.WEST);
        toolbar.add(tbRight, BorderLayout.EAST);

        JPanel north = new JPanel();
        north.setLayout(new BoxLayout(north, BoxLayout.Y_AXIS));
        north.add(header);
        north.add(toolbar);
        add(north, BorderLayout.NORTH);

        // ── Table ─────────────────────────────────────────────────────────────
        JTable bang = new JTable(tableModel);
        bang.setRowSorter(sorter);
        bang.setRowHeight(52);
        bang.setFont(new Font("Arial", Font.PLAIN, 14));
        bang.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        bang.getTableHeader().setPreferredSize(new Dimension(0, 52));
        bang.getTableHeader().setBackground(TBL_HDR);
        bang.getTableHeader().setForeground(Color.WHITE);
        bang.getTableHeader().setReorderingAllowed(false);
        bang.setShowVerticalLines(false);
        bang.setGridColor(new Color(0xEEEEEE));
        bang.setIntercellSpacing(new Dimension(0, 1));
        bang.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        bang.getColumnModel().getColumn(0).setPreferredWidth(70);
        bang.getColumnModel().getColumn(1).setPreferredWidth(220);
        bang.getColumnModel().getColumn(2).setPreferredWidth(90);
        bang.getColumnModel().getColumn(3).setPreferredWidth(100);
        bang.getColumnModel().getColumn(4).setPreferredWidth(120);
        bang.getColumnModel().getColumn(5).setPreferredWidth(120);
        bang.getColumnModel().getColumn(6).setPreferredWidth(100);
        bang.getColumnModel().getColumn(7).setPreferredWidth(110);

        DefaultTableCellRenderer altR = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable t, Object val, boolean sel, boolean foc, int r, int c) {
                super.getTableCellRendererComponent(t, val, sel, foc, r, c);
                if (!sel) {
                    setBackground(r % 2 == 0 ? Color.WHITE : new Color(0xF3F0FA));
                    setForeground(Color.BLACK);
                    if (c == 3) {
                        String v = val == null ? "" : val.toString();
                        setForeground("PERCENT".equals(v) ? new Color(0x1565C0) : new Color(0x6A1B9A));
                    }
                    if (c == 6) {
                        String v = val == null ? "" : val.toString();
                        if (v.equalsIgnoreCase("ACTIVE"))        setForeground(new Color(0x2E7D32));
                        else if (v.equalsIgnoreCase("EXPIRED"))  setForeground(new Color(0xB71C1C));
                        else                                      setForeground(new Color(0x888888));
                    }
                }
                setHorizontalAlignment(SwingConstants.CENTER);
                return this;
            }
        };
        for (int i = 0; i < 7; i++)
            bang.getColumnModel().getColumn(i).setCellRenderer(altR);

        bang.getColumnModel().getColumn(7).setCellRenderer(
                (t, val, sel, foc, r, c) -> buildActionCell(bang, r, false));
        bang.getColumnModel().getColumn(7).setCellEditor(new DefaultCellEditor(new JCheckBox()) {
            @Override
            public Component getTableCellEditorComponent(JTable t, Object val, boolean sel, int r, int c) {
                return buildActionCell(t, r, true);
            }
            @Override public Object getCellEditorValue() { return ""; }
        });

        bang.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int row = bang.rowAtPoint(e.getPoint());
                int col = bang.columnAtPoint(e.getPoint());
                if (col == 7 && row >= 0)
                    showDetailDialog(bang.convertRowIndexToModel(row));
            }
        });

        Runnable applyFilter = () -> {
            String kw      = tfTim.getText().trim();
            int    idxLoc  = cbLoc.getSelectedIndex();
            int    idxTT   = cbTrangThai.getSelectedIndex();

            RowFilter<DefaultTableModel, Integer> fLoc = idxLoc == 0 ? null
                    : RowFilter.regexFilter("(?i)^" + loaiGiam[idxLoc] + "$", 3);
            RowFilter<DefaultTableModel, Integer> fTT = idxTT == 0 ? null
                    : RowFilter.regexFilter("(?i)^" + trangThaiList[idxTT] + "$", 6);
            RowFilter<DefaultTableModel, Integer> fSr = kw.isEmpty() ? null
                    : RowFilter.orFilter(List.of(
                            RowFilter.regexFilter("(?i)" + kw, 0),
                            RowFilter.regexFilter("(?i)" + kw, 1)));

            List<RowFilter<DefaultTableModel, Integer>> active = new java.util.ArrayList<>();
            if (fLoc != null) active.add(fLoc);
            if (fTT  != null) active.add(fTT);
            if (fSr  != null) active.add(fSr);

            if (active.isEmpty()) sorter.setRowFilter(null);
            else if (active.size() == 1) sorter.setRowFilter(active.get(0));
            else sorter.setRowFilter(RowFilter.andFilter(active));
        };
        cbLoc.addActionListener(e -> applyFilter.run());
        cbTrangThai.addActionListener(e -> applyFilter.run());
        btnTim.addActionListener(e -> applyFilter.run());
        tfTim.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e)  { applyFilter.run(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e)  { applyFilter.run(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { applyFilter.run(); }
        });

        JScrollPane scroll = new JScrollPane(bang);
        UIUtils.styleScrollPane(scroll);
        add(scroll, BorderLayout.CENTER);

        loadDiscountTables();
    }

    // ── Action cell ───────────────────────────────────────────────────────────
    private JPanel buildActionCell(JTable t, int row, boolean isEditor) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 8));
        p.setBackground(row % 2 == 0 ? Color.WHITE : new Color(0xF3F0FA));
        JButton btn = UIUtils.makeActionButton("Chi tiết", new Color(0x6677C8));
        if (isEditor) {
            btn.addActionListener(e -> {
                int modelRow = t.convertRowIndexToModel(row);
                showDetailDialog(modelRow);
                t.getCellEditor().stopCellEditing();
            });
        }
        p.add(btn);
        return p;
    }

    // ── Load data ─────────────────────────────────────────────────────────────
    void loadDiscountTables() {
        ArrayList<DiscountDTO> list = discountBUS.getAllDiscounts();
        tableModel.setRowCount(0);
        if (list == null) return;
        for (DiscountDTO d : list) {
            tableModel.addRow(new Object[]{
                d.getId(), d.getName(), d.getValue(),
                d.getDiscountType().name(),
                d.getStartDate(), d.getEndDate(),
                d.getStatus() != null ? d.getStatus().name() : "-", ""
            });
        }
    }

    // ── Detail dialog ─────────────────────────────────────────────────────────
    private void showDetailDialog(int modelRow) {
        if (modelRow < 0 || modelRow >= tableModel.getRowCount()) return;

        String ma        = cell(modelRow, 0);
        String ten       = cell(modelRow, 1);
        String giaTriStr = cell(modelRow, 2);
        String loaiGiam  = cell(modelRow, 3);
        String ngayBD    = cell(modelRow, 4);
        String ngayKT    = cell(modelRow, 5);
        String trangThai = cell(modelRow, 6);

        DiscountDTO d = null;
        try { int id = Integer.parseInt(ma); d = discountBUS.getDiscountById(id); } catch (Exception ignored) {}

        String moTa     = d != null && d.getDescription() != null ? d.getDescription() : "-";
        String minOrder = d != null ? String.valueOf(d.getMinOrderAmount()) : "-";
        final DiscountDTO dto = d;

        Window owner = SwingUtilities.getWindowAncestor(this);
        JDialog dlg = new JDialog(owner, "Chi tiết khuyến mãi", Dialog.ModalityType.APPLICATION_MODAL);
        dlg.setResizable(false);
        dlg.setLayout(new BorderLayout());

        JPanel hdr = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 14));
        hdr.setBackground(TBL_HDR);
        JLabel hdrLbl = new JLabel("Thông tin khuyến mãi");
        hdrLbl.setFont(new Font("Arial", Font.BOLD, 18));
        hdrLbl.setForeground(Color.WHITE);
        hdr.add(hdrLbl);
        dlg.add(hdr, BorderLayout.NORTH);

        String[] labels = { "Mã:", "Tên:", "Mô tả:", "Giá trị giảm:", "Loại giảm:",
                             "Ngày bắt đầu:", "Ngày kết thúc:", "Min order:", "Trạng thái:" };
        Object[] values = { ma, ten, moTa, giaTriStr, loaiGiam, ngayBD, ngayKT, minOrder, trangThai };
        JPanel body = new JPanel(new GridLayout(labels.length, 2, 10, 10));
        body.setBackground(new Color(0xF0EFF8));
        body.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        for (int i = 0; i < labels.length; i++) {
            JLabel l = new JLabel(labels[i]); l.setFont(new Font("Arial", Font.BOLD, 14));
            JLabel v = new JLabel(values[i] == null ? "-" : values[i].toString()); v.setFont(new Font("Arial", Font.PLAIN, 14));
            body.add(l); body.add(v);
        }
        dlg.add(body, BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 12));
        footer.setBackground(new Color(0xF0EFF8));
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(0xCCCCCC)));

        if (dto != null) {
            JButton btnSua = makeDialogBtn("Sửa", ACCENT);
            JButton btnXoa = makeDialogBtn("Xóa", new Color(0xC62828));
            btnSua.addActionListener(e -> { dlg.dispose(); showEditDialog(dto); });
            btnXoa.addActionListener(e -> {
                int confirm = JOptionPane.showConfirmDialog(dlg, "Xóa khuyến mãi này?", "Xác nhận", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    String result = discountBUS.deleteDiscount(dto.getId());
                    if ("SUCCESS".equals(result)) {
                        JOptionPane.showMessageDialog(dlg, "Đã xóa khuyến mãi.");
                        loadDiscountTables(); dlg.dispose();
                    } else {
                        JOptionPane.showMessageDialog(dlg, result, "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                }
            });
            footer.add(btnSua); footer.add(btnXoa);
        }
        JButton btnDong = makeDialogBtn("Đóng", new Color(0x9B8EA8));
        btnDong.addActionListener(e -> dlg.dispose());
        footer.add(btnDong);
        dlg.add(footer, BorderLayout.SOUTH);

        dlg.pack();
        dlg.setMinimumSize(new Dimension(460, dlg.getPreferredSize().height));
        dlg.setLocationRelativeTo(this);
        dlg.setVisible(true);
    }

    // ── Add dialog ────────────────────────────────────────────────────────────
    private void showAddDialog() {
        JDialog dlg = makeFormDialog("Thêm khuyến mãi mới", "Thêm khuyến mãi");
        JTextField fName     = formField(); JTextField fDesc  = formField();
        JTextField fValue    = formField(); JTextField fMinOrder = formField();
        JComboBox<String> cbType   = styledCombo(new String[]{"PERCENT", "FIXED"});
        JComboBox<String> cbStatus = styledCombo(new String[]{"ACTIVE", "INACTIVE", "EXPIRED"});
        JDateChooser dcStart = dateChooser(); JDateChooser dcEnd = dateChooser();

        JPanel form = buildFormGrid(new Object[][]{
            {"Tên khuyến mãi *:", fName,     "Loại giảm:",       cbType},
            {"Giá trị giảm *:",   fValue,    "Trạng thái:",      cbStatus},
            {"Min order (VNĐ):",  fMinOrder, "Ngày bắt đầu *:",  dcStart},
            {"Mô tả:",            fDesc,     "Ngày kết thúc *:", dcEnd},
        });
        dlg.add(form, BorderLayout.CENTER);

        JPanel footer = makeFooter();
        JButton btnLuu = makeDialogBtn("Lưu", ACCENT);
        JButton btnHuy = makeDialogBtn("Hủy", new Color(0x9B8EA8));
        footer.add(btnLuu); footer.add(btnHuy);
        dlg.add(footer, BorderLayout.SOUTH);
        btnHuy.addActionListener(e -> dlg.dispose());
        btnLuu.addActionListener(e -> {
            try {
                if (dcStart.getDate() == null || dcEnd.getDate() == null) {
                    JOptionPane.showMessageDialog(dlg, "Vui lòng chọn ngày bắt đầu và ngày kết thúc!"); return;
                }
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String result = discountBUS.addDiscount(
                        fName.getText().trim(), fDesc.getText().trim(),
                        fValue.getText().trim(), cbType.getSelectedItem().toString(),
                        cbStatus.getSelectedItem().toString(),
                        sdf.format(dcStart.getDate()), sdf.format(dcEnd.getDate()),
                        fMinOrder.getText().trim());
                if ("SUCCESS".equals(result)) {
                    JOptionPane.showMessageDialog(dlg, "Thêm khuyến mãi thành công!");
                    loadDiscountTables(); dlg.dispose();
                } else {
                    JOptionPane.showMessageDialog(dlg, result, "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dlg, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });
        dlg.pack();
        dlg.setMinimumSize(new Dimension(680, dlg.getPreferredSize().height));
        dlg.setLocationRelativeTo(this);
        dlg.setVisible(true);
    }

    // ── Edit dialog ───────────────────────────────────────────────────────────
    private void showEditDialog(DiscountDTO d) {
        JDialog dlg = makeFormDialog("Sửa thông tin khuyến mãi", "Sửa khuyến mãi");
        JTextField fName     = formField(); fName.setText(d.getName());
        JTextField fDesc     = formField(); fDesc.setText(d.getDescription() != null ? d.getDescription() : "");
        JTextField fValue    = formField(); fValue.setText(String.valueOf(d.getValue()));
        JTextField fMinOrder = formField(); fMinOrder.setText(String.valueOf(d.getMinOrderAmount()));
        JComboBox<String> cbType   = styledCombo(new String[]{"PERCENT", "FIXED"});
        JComboBox<String> cbStatus = styledCombo(new String[]{"ACTIVE", "INACTIVE", "EXPIRED"});
        cbType.setSelectedItem(d.getDiscountType().name());
        if (d.getStatus() != null) cbStatus.setSelectedItem(d.getStatus().name());
        JDateChooser dcStart = dateChooser(); JDateChooser dcEnd = dateChooser();
        if (d.getStartDate() != null) dcStart.setDate(java.sql.Date.valueOf(d.getStartDate()));
        if (d.getEndDate()   != null) dcEnd.setDate(java.sql.Date.valueOf(d.getEndDate()));

        JPanel form = buildFormGrid(new Object[][]{
            {"Tên khuyến mãi *:", fName,     "Loại giảm:",       cbType},
            {"Giá trị giảm *:",   fValue,    "Trạng thái:",      cbStatus},
            {"Min order (VNĐ):",  fMinOrder, "Ngày bắt đầu *:",  dcStart},
            {"Mô tả:",            fDesc,     "Ngày kết thúc *:", dcEnd},
        });
        dlg.add(form, BorderLayout.CENTER);

        JPanel footer = makeFooter();
        JButton btnLuu = makeDialogBtn("Cập nhật", ACCENT);
        JButton btnHuy = makeDialogBtn("Hủy", new Color(0x9B8EA8));
        footer.add(btnLuu); footer.add(btnHuy);
        dlg.add(footer, BorderLayout.SOUTH);
        btnHuy.addActionListener(e -> dlg.dispose());
        btnLuu.addActionListener(e -> {
            try {
                if (dcStart.getDate() == null || dcEnd.getDate() == null) {
                    JOptionPane.showMessageDialog(dlg, "Vui lòng chọn ngày bắt đầu và ngày kết thúc!"); return;
                }
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                boolean ok = discountBUS.updateDiscount(
                        d.getId(), fName.getText().trim(), fDesc.getText().trim(),
                        Double.parseDouble(fValue.getText().trim()),
                        cbType.getSelectedItem().toString(),
                        sdf.format(dcStart.getDate()), sdf.format(dcEnd.getDate()),
                        Double.parseDouble(fMinOrder.getText().trim()),
                        cbStatus.getSelectedItem().toString());
                if (ok) { JOptionPane.showMessageDialog(dlg, "Cập nhật thành công!"); loadDiscountTables(); dlg.dispose(); }
                else    { JOptionPane.showMessageDialog(dlg, "Cập nhật thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE); }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dlg, "Giá trị giảm và Min order phải là số!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dlg, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });
        dlg.pack();
        dlg.setMinimumSize(new Dimension(680, dlg.getPreferredSize().height));
        dlg.setLocationRelativeTo(this);
        dlg.setVisible(true);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────
    private String cell(int row, int col) {
        Object v = tableModel.getValueAt(row, col);
        return v == null ? "-" : v.toString();
    }

    private JDialog makeFormDialog(String headerText, String windowTitle) {
        Window owner = SwingUtilities.getWindowAncestor(this);
        JDialog dlg = new JDialog(owner, windowTitle, Dialog.ModalityType.APPLICATION_MODAL);
        dlg.setResizable(false);
        dlg.setLayout(new BorderLayout());
        JPanel hdr = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 14));
        hdr.setBackground(TBL_HDR);
        JLabel hdrLbl = new JLabel(headerText);
        hdrLbl.setFont(new Font("Arial", Font.BOLD, 18));
        hdrLbl.setForeground(Color.WHITE);
        hdr.add(hdrLbl);
        dlg.add(hdr, BorderLayout.NORTH);
        return dlg;
    }

    private JPanel buildFormGrid(Object[][] rows) {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(new Color(0xF0EFF8));
        form.setBorder(BorderFactory.createEmptyBorder(18, 28, 14, 28));
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(6, 6, 6, 6);
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.anchor = GridBagConstraints.WEST;
        for (int r = 0; r < rows.length; r++) {
            for (int c = 0; c < 4; c++) {
                gc.gridx = c; gc.gridy = r;
                if (c % 2 == 0) {
                    gc.weightx = 0;
                    JLabel lb = new JLabel(rows[r][c].toString());
                    lb.setFont(new Font("Arial", Font.PLAIN, 13));
                    form.add(lb, gc);
                } else {
                    gc.weightx = 1;
                    form.add((Component) rows[r][c], gc);
                }
            }
        }
        return form;
    }

    private JPanel makeFooter() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        footer.setBackground(new Color(0xF0EFF8));
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(0xD1C4E9)));
        return footer;
    }

    private JTextField formField() {
        JTextField tf = new JTextField();
        tf.setFont(new Font("Arial", Font.PLAIN, 13));
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xBBBBBB)),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)));
        tf.setPreferredSize(new Dimension(200, 36));
        return tf;
    }

    private JComboBox<String> styledCombo(String[] items) {
        JComboBox<String> cb = new JComboBox<>(items);
        UIUtils.styleComboBox(cb);
        cb.setPreferredSize(new Dimension(200, 36));
        return cb;
    }

    private JDateChooser dateChooser() {
        JDateChooser dc = new JDateChooser();
        dc.setDateFormatString("dd/MM/yyyy");
        dc.setPreferredSize(new Dimension(200, 36));
        return dc;
    }

    private JButton makeDialogBtn(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Arial", Font.BOLD, 13));
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 22, 8, 22));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }
}

