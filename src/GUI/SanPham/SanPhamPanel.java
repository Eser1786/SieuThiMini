package GUI.SanPham;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableRowSorter;

import GUI.UIUtils;

import java.awt.*;

/**
 * Panel for product management. Contains a card layout switching between
 * the table and the add/edit form.
 *
 * Visible columns (indices):
 *   0=Mã SP, 1=Ảnh, 2=Tên sản phẩm, 3=Giá bán, 4=Số lượng,
 *   5=Kho, 6=Ngày hết hạn, 7=Khuyến mãi, 8=Thao tác
 * Hidden columns (indices):
 *   9=Mô tả, 10=Nhà cung cấp, 11=Danh mục, 12=Giá vốn,
 *   13=Tồn kho tối thiểu, 14=Xuất xứ, 15=Ngày sản xuất,
 *   16=Vị trí, 17=Đơn vị, 18=Trạng thái
 */
public class SanPhamPanel extends JPanel {
    private CardLayout innerCard;

    public static final String CARD_TABLE = "TABLE";
    public static final String CARD_THEM  = "THEM";

    public SanPhamPanel() {
        innerCard = new CardLayout();
        setLayout(innerCard);

        // ---- All columns (visible + hidden) ----
        String[] columns = {
            "Mã SP", "Ảnh", "Tên sản phẩm", "Giá bán",
            "Số lượng", "Kho", "Ngày hết hạn", "Khuyến mãi", "Thao tác",
            // hidden:
            "Mô tả", "Nhà cung cấp", "Danh mục", "Giá vốn",
            "Tồn kho tối thiểu", "Xuất xứ", "Ngày sản xuất",
            "Vị trí", "Đơn vị", "Trạng thái"
        };
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int col) { return col == 8; }
        };
        // Sample data — hidden cols default "-"
        model.addRow(new Object[]{
            "MD01", "", "Nước F trái K", "25.000đ", 7, "Còn hàng", "26/10/2026", "-2.000đ", "",
            "Nước giải khát hương trái cây", "Công ty ABC", "Đồ uống",
            "20.000đ", 10, "Việt Nam", "01/01/2026", "Kệ A1", "Chai", "Đang bán"
        });
        model.addRow(new Object[]{
            "MD02", "", "Thịt mèo cháy", "17.000đ", 7, "Còn hàng", "10/11/2026", "-7.000đ", "",
            "Thịt mèo vị cháy", "Công ty XYZ", "Thực phẩm",
            "12.000đ", 5, "Thái Lan", "15/03/2026", "Kệ B2", "Gói", "Đang bán"
        });

        // ---- Table card ----
        JPanel tableCard = new JPanel(new BorderLayout());

        JTable bang = new JTable(model);
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        bang.setRowSorter(sorter);

        // Hide columns 9–18
        for (int i = 9; i <= 18; i++) {
            bang.getColumnModel().getColumn(i).setMinWidth(0);
            bang.getColumnModel().getColumn(i).setMaxWidth(0);
            bang.getColumnModel().getColumn(i).setWidth(0);
            bang.getColumnModel().getColumn(i).setPreferredWidth(0);
        }

        JPanel top = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 10));
        top.setPreferredSize(new Dimension(1174, 94));
        top.setBackground(new Color(0xF8F7FF));
        top.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.BLACK, 1),
            BorderFactory.createEmptyBorder(20, 10, 20, 10)
        ));

        String[] boloc = { "Tất cả", "Còn hàng", "Hết hàng", "Có khuyến mãi", "Cận date" };
        JComboBox<String> cbLoc = new JComboBox<>(boloc);
        cbLoc.setPreferredSize(new Dimension(254, 42));
        cbLoc.setFont(new Font("Arial", Font.PLAIN, 24));
        cbLoc.setBackground(new Color(0xD9D9D9));

        JPanel timkiem = new JPanel(new BorderLayout());
        timkiem.setPreferredSize(new Dimension(229, 42));
        timkiem.setBackground(new Color(0xD9D9D9));
        JTextField tim = new JTextField();
        tim.setFont(new Font("Arial", Font.PLAIN, 24));
        timkiem.add(tim, BorderLayout.CENTER);

        JButton nuttim = new JButton("🔍");
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

        JButton them = new JButton("+ Thêm sản phẩm");
        them.setFocusPainted(false);
        them.setBackground(new Color(0xD9D9D9));
        them.setPreferredSize(new Dimension(254, 42));
        them.setFont(new Font("Arial", Font.BOLD, 24));
        them.setCursor(new Cursor(Cursor.HAND_CURSOR));
        them.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) { them.setBackground(new Color(0xC5B3E6)); }
            public void mouseExited(java.awt.event.MouseEvent evt)  { them.setBackground(new Color(0xD9D9D9)); }
        });
        them.addActionListener(e -> showThemPopup(model));

        Runnable applyFilter = () -> {
            String tuKhoa = tim.getText().trim();
            int idxLoc = cbLoc.getSelectedIndex();

            RowFilter<DefaultTableModel, Integer> filterLoc = switch (idxLoc) {
                case 1 -> RowFilter.regexFilter("(?i)Còn hàng", 5);
                case 2 -> RowFilter.regexFilter("(?i)Hết hàng", 5);
                case 3 -> RowFilter.regexFilter("^-\\d", 7);
                case 4 -> new RowFilter<>() {
                    @Override
                    public boolean include(Entry<? extends DefaultTableModel, ? extends Integer> entry) {
                        String dateStr = entry.getStringValue(6).trim();
                        try {
                            java.time.LocalDate date = java.time.LocalDate.parse(
                                dateStr, java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                            java.time.LocalDate today = java.time.LocalDate.now();
                            return !date.isBefore(today) && date.isBefore(today.plusDays(7));
                        } catch (Exception ex) { return false; }
                    }
                };
                default -> null;
            };

            RowFilter<DefaultTableModel, Integer> filterTim = tuKhoa.isEmpty()
                ? null : RowFilter.regexFilter("(?i)" + tuKhoa, 2);

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

        top.add(cbLoc);
        top.add(timkiem);
        top.add(them);

        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(new Color(0xF8F7FF));

        bang.setRowHeight(52);
        bang.setFont(new Font("Arial", Font.PLAIN, 16));
        bang.getTableHeader().setFont(new Font("Arial", Font.BOLD, 16));
        bang.getTableHeader().setPreferredSize(new Dimension(1166, 52));
        bang.getTableHeader().setBackground(new Color(0xAF9FCB));
        bang.getTableHeader().setForeground(Color.WHITE);
        bang.getTableHeader().setReorderingAllowed(false);
        bang.setShowVerticalLines(false);
        bang.setGridColor(new Color(0xEEEEEE));
        bang.setIntercellSpacing(new Dimension(0, 1));
        bang.getColumnModel().getColumn(0).setPreferredWidth(70);
        bang.getColumnModel().getColumn(1).setPreferredWidth(60);
        bang.getColumnModel().getColumn(2).setPreferredWidth(180);
        bang.getColumnModel().getColumn(4).setPreferredWidth(70);
        bang.getColumnModel().getColumn(8).setPreferredWidth(120);

        DefaultTableCellRenderer altRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(0xF3F0FA));
                }
                setHorizontalAlignment(SwingConstants.CENTER);
                if (column == 5 && !isSelected) {
                    String val = value == null ? "" : value.toString();
                    switch (val) {
                        case "Còn hàng" -> setForeground(new Color(0x388E3C));
                        case "Hết hàng" -> setForeground(new Color(0xC62828));
                        default         -> setForeground(Color.BLACK);
                    }
                } else {
                    setForeground(Color.BLACK);
                }
                return c;
            }
        };
        for (int i = 0; i < 9; i++)
            bang.getColumnModel().getColumn(i).setCellRenderer(altRenderer);

        bang.getColumnModel().getColumn(8).setCellRenderer(new TableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {
                JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 8));
                p.setBackground(row % 2 == 0 ? Color.WHITE : new Color(0xF3F0FA));
                JButton xem = UIUtils.makeActionButton("Xem chi tiết", new Color(0x6677C8));
                xem.setPreferredSize(new Dimension(100, 32));
                p.add(xem);
                return p;
            }
        });

        bang.getColumnModel().getColumn(8).setCellEditor(new DefaultCellEditor(new JCheckBox()) {
            private final JPanel p    = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 8));
            private final JButton xem = UIUtils.makeActionButton("Xem chi tiết", new Color(0x6677C8));
            private int currentRow = -1;
            {
                xem.setPreferredSize(new Dimension(100, 32));
                p.setOpaque(true);
                p.add(xem);
                xem.addActionListener(e -> {
                    fireEditingStopped();
                    int modelRow = bang.convertRowIndexToModel(currentRow);
                    showDetailDialog(modelRow, model, bang);
                });
            }
            @Override
            public Component getTableCellEditorComponent(
                    JTable table, Object value, boolean isSelected, int row, int column) {
                currentRow = row;
                p.setBackground(row % 2 == 0 ? Color.WHITE : new Color(0xF3F0FA));
                return p;
            }
            @Override public Object getCellEditorValue() { return ""; }
        });

        content.add(new JScrollPane(bang), BorderLayout.CENTER);
        tableCard.add(top,     BorderLayout.NORTH);
        tableCard.add(content, BorderLayout.CENTER);

        add(tableCard, CARD_TABLE);
        innerCard.show(this, CARD_TABLE);
    }

    // ---- helpers ----
    private JTextField makePopupField() { return UIUtils.makeField(); }

    private void styleBtn(JButton b, Color bg, int w, int h) {
        b.setFont(new Font("Arial", Font.BOLD, 15));
        b.setBackground(bg); b.setForeground(Color.WHITE);
        b.setFocusPainted(false); b.setBorderPainted(false);
        b.setPreferredSize(new Dimension(w, h));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    /**
     * Popup thêm sản phẩm — form đầy đủ tất cả trường DTO, null layout y chang code gốc.
     */
    private void showThemPopup(DefaultTableModel model) {
        Window owner = SwingUtilities.getWindowAncestor(this);
        JDialog popup = new JDialog(owner, "Thêm sản phẩm", Dialog.ModalityType.APPLICATION_MODAL);
        popup.setSize(860, 640);
        popup.setLocationRelativeTo(this);
        popup.setResizable(false);
        popup.getContentPane().setBackground(new Color(0xF0EFF8));
        popup.setLayout(new BorderLayout());

        // Header
        JPanel formHeader = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 18));
        formHeader.setBackground(new Color(0xF0EFF8));
        formHeader.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0xCCCCCC)));
        JButton btnQuayLai = new JButton("← Quay lại danh sách");
        btnQuayLai.setFont(new Font("Arial", Font.BOLD, 22));
        btnQuayLai.setBackground(new Color(0x9B8EA8));
        btnQuayLai.setForeground(Color.WHITE);
        btnQuayLai.setFocusPainted(false); btnQuayLai.setBorderPainted(false);
        btnQuayLai.setPreferredSize(new Dimension(300, 48));
        btnQuayLai.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnQuayLai.addActionListener(e -> popup.dispose());
        formHeader.add(btnQuayLai);
        popup.add(formHeader, BorderLayout.NORTH);

        // Form (null layout)
        JPanel form = new JPanel(null);
        form.setBackground(new Color(0xF0EFF8));
        Font lf = new Font("Arial", Font.BOLD, 20);
        int lx = 60, fx = 250, fw = 380, fh = 42, gap = 70;

        JTextField fMa      = makePopupField(); JTextField fTen     = makePopupField();
        JTextField fMoTa    = makePopupField(); JTextField fNCC     = makePopupField();
        JTextField fDM      = makePopupField(); JTextField fGiaVon  = makePopupField();
        JTextField fGiaBan  = makePopupField(); JTextField fSL      = makePopupField();
        JTextField fTonMin  = makePopupField(); JTextField fXX      = makePopupField();
        JTextField fNgaySX  = makePopupField(); JTextField fNgayHH  = makePopupField();
        JTextField fViTri   = makePopupField(); JTextField fDonVi   = makePopupField();
        JTextField fTT      = makePopupField(); JTextField fKM      = makePopupField();

        String[] lblTexts = {
            "Mã SP", "Tên sản phẩm", "Mô tả", "Nhà cung cấp", "Danh mục",
            "Giá vốn", "Giá bán", "Số lượng", "Tồn kho tối thiểu",
            "Xuất xứ", "Ngày sản xuất", "Ngày hết hạn",
            "Vị trí", "Đơn vị", "Trạng thái", "Khuyến mãi"
        };
        JTextField[] flds = {
            fMa, fTen, fMoTa, fNCC, fDM,
            fGiaVon, fGiaBan, fSL, fTonMin,
            fXX, fNgaySX, fNgayHH,
            fViTri, fDonVi, fTT, fKM
        };
        int y = 20;
        for (int i = 0; i < lblTexts.length; i++) {
            JLabel lb = new JLabel(lblTexts[i]); lb.setFont(lf); lb.setBounds(lx, y, 220, 30);
            flds[i].setBounds(fx, y, fw, fh);
            form.add(lb); form.add(flds[i]);
            y += gap;
        }
        JLabel lbAnh = new JLabel("Ảnh"); lbAnh.setFont(lf); lbAnh.setBounds(lx, y, 220, 30);
        JButton btnAnh = new JButton("+ Thêm ảnh");
        btnAnh.setFont(new Font("Arial", Font.PLAIN, 18));
        btnAnh.setBackground(new Color(0xE0DDE8)); btnAnh.setForeground(new Color(0x666666));
        btnAnh.setFocusPainted(false); btnAnh.setBounds(fx, y, 200, 70);
        btnAnh.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAnh.setBorder(BorderFactory.createLineBorder(new Color(0xAAAAAA), 1, true));
        form.add(lbAnh); form.add(btnAnh);
        y += 90;

        form.setPreferredSize(new Dimension(800, y));
        JScrollPane scroll = new JScrollPane(form);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        popup.add(scroll, BorderLayout.CENTER);

        // Footer
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 30, 16));
        footer.setBackground(new Color(0xF0EFF8));
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(0xCCCCCC)));
        JButton btnLuu = new JButton("LƯU");
        btnLuu.setFont(new Font("Arial", Font.BOLD, 24));
        btnLuu.setBackground(new Color(0xB83434)); btnLuu.setForeground(Color.WHITE);
        btnLuu.setFocusPainted(false); btnLuu.setBorderPainted(false);
        btnLuu.setPreferredSize(new Dimension(160, 52));
        btnLuu.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLuu.addActionListener(e -> {
            int sl = 0;
            try { sl = Integer.parseInt(fSL.getText().trim()); } catch (NumberFormatException ignore) {}
            String kho = sl > 0 ? "Còn hàng" : "Hết hàng";
            model.addRow(new Object[]{
                fMa.getText(), "", fTen.getText(), fGiaBan.getText(),
                sl, kho, fNgayHH.getText(), fKM.getText().isEmpty() ? "-" : fKM.getText(), "",
                fMoTa.getText(), fNCC.getText(), fDM.getText(), fGiaVon.getText(),
                fTonMin.getText(), fXX.getText(), fNgaySX.getText(),
                fViTri.getText(), fDonVi.getText(), fTT.getText()
            });
            popup.dispose();
        });
        footer.add(btnLuu);
        popup.add(footer, BorderLayout.SOUTH);
        popup.setVisible(true);
    }

    /**
     * Popup xem chi tiết — hiển thị đầy đủ tất cả trường kể cả cột ẩn.
     * Có nút Sửa (mở showEditPopup) và Xóa.
     */
    private void showDetailDialog(int modelRow, DefaultTableModel model, JTable bang) {
        Window owner = SwingUtilities.getWindowAncestor(this);
        JDialog detail = new JDialog(owner, "Chi tiết sản phẩm", Dialog.ModalityType.APPLICATION_MODAL);
        detail.setSize(520, 640);
        detail.setLocationRelativeTo(this);
        detail.setResizable(false);
        detail.getContentPane().setBackground(new Color(0xF0EFF8));
        detail.setLayout(new BorderLayout(0, 0));

        // Header
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 14));
        header.setBackground(new Color(0xAF9FCB));
        JLabel lblTitle = new JLabel("Thông tin sản phẩm");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 20)); lblTitle.setForeground(Color.WHITE);
        header.add(lblTitle);
        detail.add(header, BorderLayout.NORTH);

        // Body — hiển thị tất cả 15 trường (visible + hidden)
        String[] lbls = {
            "Mã SP", "Tên sản phẩm", "Mô tả", "Nhà cung cấp", "Danh mục",
            "Giá vốn", "Giá bán", "Số lượng", "Tồn kho tối thiểu",
            "Xuất xứ", "Ngày sản xuất", "Ngày hết hạn",
            "Vị trí", "Đơn vị", "Trạng thái"
        };
        // col indices in model: 0=Mã,2=Tên,9=MoTa,10=NCC,11=DM,12=GiaVon,3=GiaBan,4=SL,13=TonMin,14=XX,15=NgaySX,6=NgayHH,16=ViTri,17=DonVi,18=TT
        int[] colIdx = { 0, 2, 9, 10, 11, 12, 3, 4, 13, 14, 15, 6, 16, 17, 18 };

        JPanel body = new JPanel(new GridLayout(lbls.length, 2, 12, 10));
        body.setBackground(new Color(0xF0EFF8));
        body.setBorder(BorderFactory.createEmptyBorder(16, 36, 16, 36));
        for (int i = 0; i < lbls.length; i++) {
            JLabel lbl = new JLabel(lbls[i] + ":");
            lbl.setFont(new Font("Arial", Font.BOLD, 15));
            Object v = model.getValueAt(modelRow, colIdx[i]);
            JLabel val = new JLabel(v == null ? "-" : v.toString());
            val.setFont(new Font("Arial", Font.PLAIN, 15));
            body.add(lbl); body.add(val);
        }
        JScrollPane scrollDetail = new JScrollPane(body);
        scrollDetail.setBorder(null);
        scrollDetail.getVerticalScrollBar().setUnitIncrement(16);
        detail.add(scrollDetail, BorderLayout.CENTER);

        // Footer
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 14));
        footer.setBackground(new Color(0xF0EFF8));
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(0xCCCCCC)));

        JButton btnSua = new JButton("✏ Sửa");
        styleBtn(btnSua, new Color(0x6677C8), 110, 40);
        btnSua.addActionListener(e -> { detail.dispose(); showEditPopup(modelRow, model, bang); });

        JButton btnXoa = new JButton("🗑 Xóa");
        styleBtn(btnXoa, new Color(0xB83434), 110, 40);
        btnXoa.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(detail,
                "Bạn có chắc muốn xóa sản phẩm \"" + model.getValueAt(modelRow, 2) + "\"?",
                "Xác nhận xóa", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (confirm == JOptionPane.YES_OPTION) { model.removeRow(modelRow); detail.dispose(); }
        });

        footer.add(btnSua); footer.add(btnXoa);
        detail.add(footer, BorderLayout.SOUTH);
        detail.setVisible(true);
    }

    /**
     * Popup sửa — có đầy đủ tất cả trường DTO, lưu lại vào model.
     */
    private void showEditPopup(int modelRow, DefaultTableModel model, JTable bang) {
        Window owner = SwingUtilities.getWindowAncestor(this);
        JDialog popup = new JDialog(owner, "Sửa sản phẩm", Dialog.ModalityType.APPLICATION_MODAL);
        popup.setSize(580, 640);
        popup.setLocationRelativeTo(this);
        popup.setResizable(false);
        popup.getContentPane().setBackground(new Color(0xF0EFF8));
        popup.setLayout(new BorderLayout(0, 0));

        // Header
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 14));
        header.setBackground(new Color(0x6677C8));
        JLabel lblTitle = new JLabel("Chỉnh sửa thông tin");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 20)); lblTitle.setForeground(Color.WHITE);
        header.add(lblTitle);
        popup.add(header, BorderLayout.NORTH);

        // Fields — same order as detail
        String[] lbls = {
            "Mã SP", "Tên sản phẩm", "Mô tả", "Nhà cung cấp", "Danh mục",
            "Giá vốn", "Giá bán", "Số lượng", "Tồn kho tối thiểu",
            "Xuất xứ", "Ngày sản xuất", "Ngày hết hạn",
            "Vị trí", "Đơn vị", "Trạng thái", "Khuyến mãi"
        };
        int[] colIdx = { 0, 2, 9, 10, 11, 12, 3, 4, 13, 14, 15, 6, 16, 17, 18, 7 };

        JTextField[] flds = new JTextField[lbls.length];
        for (int i = 0; i < lbls.length; i++) {
            flds[i] = UIUtils.makeField();
            Object v = model.getValueAt(modelRow, colIdx[i]);
            flds[i].setText(v == null ? "" : v.toString());
        }

        JPanel formBody = new JPanel();
        formBody.setLayout(new BoxLayout(formBody, BoxLayout.Y_AXIS));
        formBody.setBackground(new Color(0xF0EFF8));
        formBody.setBorder(BorderFactory.createEmptyBorder(10, 36, 10, 36));
        for (int i = 0; i < lbls.length; i++) {
            JLabel lbl = new JLabel(lbls[i]);
            lbl.setFont(new Font("Arial", Font.BOLD, 15));
            lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
            flds[i].setAlignmentX(Component.LEFT_ALIGNMENT);
            flds[i].setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
            formBody.add(lbl);
            formBody.add(flds[i]);
            formBody.add(Box.createVerticalStrut(8));
        }
        JScrollPane scrollEdit = new JScrollPane(formBody);
        scrollEdit.setBorder(null);
        scrollEdit.getVerticalScrollBar().setUnitIncrement(16);
        popup.add(scrollEdit, BorderLayout.CENTER);

        // Footer
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 14));
        footer.setBackground(new Color(0xF0EFF8));
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(0xCCCCCC)));

        JButton btnHuy = new JButton("Hủy");
        styleBtn(btnHuy, new Color(0x9B8EA8), 100, 40);
        btnHuy.addActionListener(e -> popup.dispose());

        JButton btnLuu = new JButton("Lưu");
        styleBtn(btnLuu, new Color(0xB83434), 100, 40);
        btnLuu.addActionListener(e -> {
            for (int i = 0; i < colIdx.length; i++)
                model.setValueAt(flds[i].getText(), modelRow, colIdx[i]);
            // cập nhật kho tự động theo số lượng
            try {
                int sl = Integer.parseInt(flds[7].getText().trim());
                model.setValueAt(sl > 0 ? "Còn hàng" : "Hết hàng", modelRow, 5);
            } catch (NumberFormatException ignore) {}
            bang.repaint();
            popup.dispose();
        });

        footer.add(btnHuy); footer.add(btnLuu);
        popup.add(footer, BorderLayout.SOUTH);
        popup.setVisible(true);
    }
}
