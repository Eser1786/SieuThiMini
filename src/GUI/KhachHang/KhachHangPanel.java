package GUI.KhachHang;

import GUI.ExportUtils;
import GUI.UIUtils;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.List;

/**
 * Panel for customer management. Contains a card layout switching between
 * the table and the add/edit form.
 */
public class KhachHangPanel extends JPanel {
    private CardLayout innerCard;
    private int editingRow = -1;
    private JTextField tfMaKH, tfTen, tfSdt, tfEmail, tfDiaChi, tfDiem, tfTgDK, tfLanCuoiMua, tfTongTien, tfHang,
            tfTrangThai;
    private JButton btnLuu, btnSua, btnXoa, btnHoanTac;

    public static final String CARD_TABLE = "TABLE";
    public static final String CARD_THEM = "THEM";

    public KhachHangPanel() {
        innerCard = new CardLayout();
        setLayout(innerCard);

        tfMaKH = UIUtils.makeField();
        tfTen = UIUtils.makeField();
        tfSdt = UIUtils.makeField();
        tfEmail = UIUtils.makeField();
        tfDiaChi = UIUtils.makeField();
        tfDiem = UIUtils.makeField();
        tfTgDK = UIUtils.makeField();
        tfLanCuoiMua = UIUtils.makeField();
        tfTongTien = UIUtils.makeField();
        tfHang = UIUtils.makeField();
        tfTrangThai = UIUtils.makeField();

        // ĐÃ SỬA: Chỉ giữ lại các cột hiển thị trên table, bỏ "Thời gian đăng kí", "Lần cuối mua", "Tổng tiền", "Điểm"
        String[] columns = {
                "Mã KH", "Tên", "Số điện thoại", "Email",
                "Địa chỉ", "Hạng", "Trạng thái", "Thao tác"
        };

        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 7; // Chỉ cột cuối (Thao tác) được phép chỉnh sửa
            }
        };

        // ĐÃ SỬA: Cập nhật dữ liệu mẫu chỉ với các cột hiển thị trên table
        model.addRow(new Object[] {
                "KH001", "Lê Đỗ Thái Anh", "098754321", "anhdo@gmail.com",
                "213LDTA", "Bạc", "Hoạt động", ""
        });
        model.addRow(new Object[] {
                "KH002", "Lý Nguyễn", "0915987654", "nguyenly@gmail.com",
                "456 Nguyễn Trãii", "Vàng", "Hoạt động", ""
        });
        model.addRow(new Object[] {
                "KH003", "Nguyễn Hoàng Sang", "0933777888", "sangnguyen@gmail.com",
                "789KTX", "Đồng", "Không hoạt động", ""
        });
        model.addRow(new Object[] {
                "KH004", "Trân dơ hầy", "0977111222", "tranbado@gmail.com",
                "Bụi chúi", "Kim cương", "Hoạt động", ""
        });

        JPanel tableCard = new JPanel(new BorderLayout());

        JTable bang = new JTable(model);
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        bang.setRowSorter(sorter); // Cho phép sắp xếp khi click vào header

        // ── TOP TOOLBAR (2 rows, auto-height) ────────────────────────────────
        JPanel top = new JPanel();
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        top.setBackground(new Color(0xF8F7FF));
        top.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xCCCCCC), 1),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));

        // Row 1: filter combo + search field
        JPanel khRow1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));
        khRow1.setBackground(new Color(0xF8F7FF));

        String[] boloc = { "T\u1ea5t c\u1ea3", "\u0110\u1ed3ng", "B\u1ea1c", "V\u00e0ng", "Kim c\u01b0\u01a1ng", "Ho\u1ea1t \u0111\u1ed9ng", "Kh\u00f4ng ho\u1ea1t \u0111\u1ed9ng" };
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

        khRow1.add(cbLoc);
        khRow1.add(timkiem);

        // Row 2: Thêm button + export buttons
        JPanel khRow2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        khRow2.setBackground(new Color(0xF8F7FF));

        JButton them = new JButton("+ Th\u00eam kh\u00e1ch h\u00e0ng");
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
        them.addActionListener(e -> {
            editingRow = -1;
            clearForm();
            enableFormFields(true);
            btnLuu.setVisible(true);
            btnSua.setVisible(false);
            btnXoa.setVisible(false);
            btnHoanTac.setVisible(false);
            innerCard.show(this, CARD_THEM);
        });

        Runnable applyFilter = () -> {
            String tuKhoa = tim.getText().trim();
            int idxLoc = cbLoc.getSelectedIndex();

            // Tạo filter theo loại lọc - ĐÃ SỬA index cột
            RowFilter<DefaultTableModel, Integer> filterLoc = null;

            switch (idxLoc) {
                case 1: // Đồng
                    filterLoc = RowFilter.regexFilter("(?i)^\u0110\u1ed3ng$", 5); // Cột Hạng (index 5)
                    break;
                case 2: // Bạc
                    filterLoc = RowFilter.regexFilter("(?i)^B\u1ea1c$", 5);
                    break;
                case 3: // Vàng
                    filterLoc = RowFilter.regexFilter("(?i)^V\u00e0ng$", 5);
                    break;
                case 4: // Kim cương
                    filterLoc = RowFilter.regexFilter("(?i)^Kim c\u01b0\u01a1ng$", 5);
                    break;
                case 5: // Hoạt động
                    filterLoc = RowFilter.regexFilter("(?i)^Ho\u1ea1t \u0111\u1ed9ng$", 6); // Cột Trạng thái (index 6)
                    break;
                case 6: // Không hoạt động
                    filterLoc = RowFilter.regexFilter("(?i)^Kh\u00f4ng ho\u1ea1t \u0111\u1ed9ng$", 6);
                    break;
                default: // Tất cả
                    filterLoc = null;
                    break;
            }

            RowFilter<DefaultTableModel, Integer> filterTim = null;
            if (!tuKhoa.isEmpty()) {
                filterTim = RowFilter.orFilter(
                        java.util.List.of(
                                RowFilter.regexFilter("(?i)" + tuKhoa, 0),
                                RowFilter.regexFilter("(?i)" + tuKhoa, 1),
                                RowFilter.regexFilter("(?i)" + tuKhoa, 2)
                ));
            }

            if (filterLoc != null && filterTim != null)
                sorter.setRowFilter(RowFilter.andFilter(java.util.List.of(filterLoc, filterTim)));
            else if (filterLoc != null)
                sorter.setRowFilter(filterLoc);
            else if (filterTim != null)
                sorter.setRowFilter(filterTim);
            else
                sorter.setRowFilter(null);
        };

        cbLoc.addActionListener(e -> applyFilter.run());
        nuttim.addActionListener(e -> applyFilter.run());
        tim.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e)  { applyFilter.run(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e)  { applyFilter.run(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { applyFilter.run(); }
        });

        // Export buttons
        JButton btnPDF = ExportUtils.makeExportButton("Xu\u1ea5t PDF", new Color(0x7B52AB));
        btnPDF.addActionListener(e -> ExportUtils.xuatPDF(KhachHangPanel.this, model, "Danh s\u00e1ch kh\u00e1ch h\u00e0ng"));

        JButton btnExcel = ExportUtils.makeExportButton("Xu\u1ea5t Excel", new Color(0x2E7D32));
        btnExcel.addActionListener(e -> ExportUtils.xuatCSV(KhachHangPanel.this, model, "khach_hang"));

        JButton btnImport = ExportUtils.makeImportButton("Nh\u1eadp CSV");
        btnImport.addActionListener(e -> {
            List<String[]> rows = ExportUtils.importCSV(KhachHangPanel.this);
            if (rows == null) return;
            for (String[] r : rows) { if (r.length < 8) continue; model.addRow((Object[])r); }
        });

        khRow2.add(them);
        khRow2.add(btnPDF);
        khRow2.add(btnExcel);
        khRow2.add(btnImport);

        top.add(khRow1);
        top.add(khRow2);

        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(new Color(0xF8F7FF));

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

        // ĐÃ SỬA: Cập nhật độ rộng cột cho table mới
        bang.getColumnModel().getColumn(0).setPreferredWidth(80); // Mã KH
        bang.getColumnModel().getColumn(1).setPreferredWidth(150); // Tên
        bang.getColumnModel().getColumn(2).setPreferredWidth(120); // SĐT
        bang.getColumnModel().getColumn(3).setPreferredWidth(180); // Email
        bang.getColumnModel().getColumn(4).setPreferredWidth(200); // Địa chỉ
        bang.getColumnModel().getColumn(5).setPreferredWidth(80); // Hạng
        bang.getColumnModel().getColumn(6).setPreferredWidth(100); // Trạng thái
        bang.getColumnModel().getColumn(7).setPreferredWidth(100); // Thao tác

        DefaultTableCellRenderer altRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    // Màu xen kẽ: trắng và tím nhạt
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(0xF3F0FA));
                }
                setHorizontalAlignment(SwingConstants.CENTER);

                if (!isSelected) {
                    if (column == 5) { // Cột Hạng (index 5)
                        String val = value == null ? "" : value.toString();
                        switch (val) {
                            case "Đồng" -> setForeground(new Color(0x8B4513)); // Màu nâu
                            case "Bạc" -> setForeground(new Color(0x808080)); // Màu xám
                            case "Vàng" -> setForeground(new Color(0xFFD700)); // Màu vàng
                            case "Kim cương" -> setForeground(new Color(0x00BFFF)); // Màu xanh dương
                            default -> setForeground(Color.BLACK);
                        }
                    } else if (column == 6) { // Cột Trạng thái (index 6)
                        String val = value == null ? "" : value.toString();
                        if (val.equals("Hoạt động")) {
                            setForeground(new Color(0x388E3C)); // Màu xanh lá
                        } else {
                            setForeground(new Color(0xC62828)); // Màu đỏ
                        }
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
                JButton chiTiet = UIUtils.makeActionButton("Chi tiết", new Color(0x6677C8));
                p.add(chiTiet);
                return p;
            }
        });

        bang.getColumnModel().getColumn(7).setCellEditor(new DefaultCellEditor(new JCheckBox()) {
            private final JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 8));
            private final JButton chiTiet = UIUtils.makeActionButton("Chi tiết", new Color(0x6677C8));
            private int currentRow = -1; // Lưu dòng hiện tại đang được click

            {
                p.setOpaque(true);
                p.add(chiTiet);

                // Xử lý khi click nút Chi tiết
                chiTiet.addActionListener(e -> {
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

            @Override
            public Object getCellEditorValue() {
                return "";
            }
        });

        JScrollPane bangScroll = new JScrollPane(bang);
        UIUtils.styleScrollPane(bangScroll);
        content.add(bangScroll, BorderLayout.CENTER);
        tableCard.add(top, BorderLayout.NORTH);
        tableCard.add(content, BorderLayout.CENTER);

        JPanel themCard = new JPanel(new BorderLayout());
        themCard.setBackground(new Color(0xF0EFF8));

        JPanel formHeader = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 18));
        formHeader.setBackground(new Color(0xF0EFF8));
        formHeader.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0xCCCCCC)));

        JButton btnQuayLai = new JButton("← Quay lại danh sách");
        btnQuayLai.setFont(new Font("Arial", Font.BOLD, 22));
        btnQuayLai.setBackground(new Color(0x9B8EA8));
        btnQuayLai.setForeground(Color.WHITE);
        btnQuayLai.setFocusPainted(false);
        btnQuayLai.setBorderPainted(false);
        btnQuayLai.setPreferredSize(new Dimension(300, 48));
        btnQuayLai.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnQuayLai.addActionListener(e -> {
            clearForm();
            enableFormFields(true);
            editingRow = -1;
            innerCard.show(this, CARD_TABLE);
        });
        formHeader.add(btnQuayLai);
        themCard.add(formHeader, BorderLayout.NORTH);

        JPanel formBody = new JPanel(new GridBagLayout());
        formBody.setBackground(new Color(0xF0EFF8));
        formBody.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8);

        Font labelFont = new Font("Arial", Font.BOLD, 18);

        // Hàng 1: Mã KH và Tên
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel lbMaKH = new JLabel("Mã KH:");
        lbMaKH.setFont(labelFont);
        formBody.add(lbMaKH, gbc);

        gbc.gridx = 1;
        formBody.add(tfMaKH, gbc);

        gbc.gridx = 2;
        JLabel lbTen = new JLabel("Tên khách hàng:");
        lbTen.setFont(labelFont);
        formBody.add(lbTen, gbc);

        gbc.gridx = 3;
        formBody.add(tfTen, gbc);

        // Hàng 2: SĐT và Email
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel lbSdt = new JLabel("Số điện thoại:");
        lbSdt.setFont(labelFont);
        formBody.add(lbSdt, gbc);

        gbc.gridx = 1;
        formBody.add(tfSdt, gbc);

        gbc.gridx = 2;
        JLabel lbEmail = new JLabel("Email:");
        lbEmail.setFont(labelFont);
        formBody.add(lbEmail, gbc);

        gbc.gridx = 3;
        formBody.add(tfEmail, gbc);

        // Hàng 3: Địa chỉ
        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel lbDiaChi = new JLabel("Địa chỉ:");
        lbDiaChi.setFont(labelFont);
        formBody.add(lbDiaChi, gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 3;
        formBody.add(tfDiaChi, gbc);
        gbc.gridwidth = 1;

        // Hàng 4: Điểm và Thời gian ĐK
        gbc.gridx = 0;
        gbc.gridy = 3;
        JLabel lbDiem = new JLabel("Điểm tích lũy:");
        lbDiem.setFont(labelFont);
        formBody.add(lbDiem, gbc);

        gbc.gridx = 1;
        formBody.add(tfDiem, gbc);

        gbc.gridx = 2;
        JLabel lbTgDK = new JLabel("Thời gian ĐK:");
        lbTgDK.setFont(labelFont);
        formBody.add(lbTgDK, gbc);

        gbc.gridx = 3;
        formBody.add(tfTgDK, gbc);

        // Hàng 5: Lần cuối mua và Tổng tiền
        gbc.gridx = 0;
        gbc.gridy = 4;
        JLabel lbLanCuoi = new JLabel("Lần cuối mua:");
        lbLanCuoi.setFont(labelFont);
        formBody.add(lbLanCuoi, gbc);

        gbc.gridx = 1;
        formBody.add(tfLanCuoiMua, gbc);

        gbc.gridx = 2;
        JLabel lbTongTien = new JLabel("Tổng tiền đã mua:");
        lbTongTien.setFont(labelFont);
        formBody.add(lbTongTien, gbc);

        gbc.gridx = 3;
        formBody.add(tfTongTien, gbc);

        // Hàng 6: Hạng và Trạng thái
        gbc.gridx = 0;
        gbc.gridy = 5;
        JLabel lbHang = new JLabel("Hạng:");
        lbHang.setFont(labelFont);
        formBody.add(lbHang, gbc);

        gbc.gridx = 1;
        formBody.add(tfHang, gbc);

        gbc.gridx = 2;
        JLabel lbTrangThai = new JLabel("Trạng thái:");
        lbTrangThai.setFont(labelFont);
        formBody.add(lbTrangThai, gbc);

        gbc.gridx = 3;
        formBody.add(tfTrangThai, gbc);

        themCard.add(formBody, BorderLayout.CENTER);

        JPanel btnWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 16));
        btnWrapper.setBackground(new Color(0xF0EFF8));
        btnWrapper.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(0xCCCCCC)));

        btnLuu = new JButton("LƯU");
        btnLuu.setFont(new Font("Arial", Font.BOLD, 24));
        btnLuu.setBackground(new Color(0xB83434));
        btnLuu.setForeground(Color.WHITE);
        btnLuu.setFocusPainted(false);
        btnLuu.setBorderPainted(false);
        btnLuu.setPreferredSize(new Dimension(160, 52));
        btnLuu.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLuu.addActionListener(e -> {
            if (editingRow == -1) {
                saveNewCustomer(model);
            } else {
                updateCustomer(model);
                innerCard.show(this, CARD_TABLE);
            }
        });

        btnSua = new JButton("SỬA");
        btnSua.setFont(new Font("Arial", Font.BOLD, 24));
        btnSua.setBackground(new Color(0x6677C8));
        btnSua.setForeground(Color.WHITE);
        btnSua.setFocusPainted(false);
        btnSua.setBorderPainted(false);
        btnSua.setPreferredSize(new Dimension(160, 52));
        btnSua.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSua.addActionListener(e -> {
            if (editingRow >= 0) {
                enableFormFields(true);
                btnSua.setVisible(false);
                btnLuu.setVisible(true);
                btnXoa.setVisible(true);
                btnHoanTac.setVisible(true);
            }
        });

        btnXoa = new JButton("XÓA");
        btnXoa.setFont(new Font("Arial", Font.BOLD, 24));
        btnXoa.setBackground(new Color(0xB83434));
        btnXoa.setForeground(Color.WHITE);
        btnXoa.setFocusPainted(false);
        btnXoa.setBorderPainted(false);
        btnXoa.setPreferredSize(new Dimension(160, 52));
        btnXoa.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnXoa.addActionListener(e -> {
            if (editingRow >= 0) {
                int confirm = JOptionPane.showConfirmDialog(this, 
                    "Bạn có chắc muốn xóa khách hàng này?", 
                    "Xác nhận xóa", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (confirm == JOptionPane.YES_OPTION) {
                    model.removeRow(editingRow);
                    clearForm();
                    enableFormFields(true);
                    editingRow = -1;
                    innerCard.show(this, CARD_TABLE);
                }
            }
        });

        btnHoanTac = new JButton("HOÀN TÁC");
        btnHoanTac.setFont(new Font("Arial", Font.BOLD, 24));
        btnHoanTac.setBackground(new Color(0xFF7043));
        btnHoanTac.setForeground(Color.WHITE);
        btnHoanTac.setFocusPainted(false);
        btnHoanTac.setBorderPainted(false);
        btnHoanTac.setPreferredSize(new Dimension(180, 52));
        btnHoanTac.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnHoanTac.setVisible(false);
        btnHoanTac.addActionListener(e -> {
            // Load lại dữ liệu gốc từ model
            loadFormData(model, editingRow);
            enableFormFields(false);
            btnSua.setVisible(true);
            btnLuu.setVisible(false);
            btnXoa.setVisible(false);
            btnHoanTac.setVisible(false);
        });

        btnWrapper.add(btnLuu);
        btnWrapper.add(btnSua);
        btnWrapper.add(btnXoa);
        btnWrapper.add(btnHoanTac);
        themCard.add(btnWrapper, BorderLayout.SOUTH);

        btnSua.setVisible(false);
        btnXoa.setVisible(false);
        btnHoanTac.setVisible(false);

        add(tableCard, CARD_TABLE);
        add(themCard, CARD_THEM);
        innerCard.show(this, CARD_TABLE);
    }

    /**
     * Hiển thị dialog chi tiết khách hàng với ĐẦY ĐỦ thông tin
     */
    private void showDetailDialog(int modelRow, DefaultTableModel model, JTable bang) {
        Window owner = SwingUtilities.getWindowAncestor(this);
        JDialog detail = new JDialog(owner, "Chi tiết khách hàng", Dialog.ModalityType.APPLICATION_MODAL);
        detail.setSize(600, 650);
        detail.setLocationRelativeTo(this);
        detail.setResizable(false);
        detail.getContentPane().setBackground(new Color(0xF0EFF8));
        detail.setLayout(new BorderLayout(0, 0));

        // Header
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 14));
        header.setBackground(new Color(0xAF9FCB));
        JLabel lblTitle = new JLabel("Thông tin khách hàng chi tiết");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitle.setForeground(Color.WHITE);
        header.add(lblTitle);
        detail.add(header, BorderLayout.NORTH);

        // Lấy dữ liệu từ model và các field (vì model chỉ có 8 cột, cần lấy thêm từ data gốc)
        // Trong thực tế, bạn nên lưu trữ đầy đủ dữ liệu ở đâu đó, nhưng tạm thời lấy từ form fields
        String maKH = model.getValueAt(modelRow, 0).toString();
        String ten = model.getValueAt(modelRow, 1).toString();
        String sdt = model.getValueAt(modelRow, 2).toString();
        String email = model.getValueAt(modelRow, 3).toString();
        String diaChi = model.getValueAt(modelRow, 4).toString();
        String hang = model.getValueAt(modelRow, 5).toString();
        String trangThai = model.getValueAt(modelRow, 6).toString();
        
        // Các trường bị ẩn trên table - lấy từ data gốc (ở đây dùng giá trị mẫu)
        // Trong thực tế, bạn cần lưu trữ đầy đủ dữ liệu hoặc query lại từ database
        String diem = getDiemFromData(maKH);
        String tgDK = getTgDKFromData(maKH);
        String lanCuoiMua = getLanCuoiMuaFromData(maKH);
        String tongTien = getTongTienFromData(maKH);

        String[] labels = {
            "Mã KH:", "Tên khách hàng:", "Số điện thoại:", "Email:",
            "Địa chỉ:", "Điểm tích lũy:", "Thời gian đăng kí:",
            "Lần cuối mua hàng:", "Tổng tiền đã mua:", "Hạng:", "Trạng thái:"
        };
        
        Object[] values = {
            maKH, ten, sdt, email, diaChi, diem, tgDK, lanCuoiMua, tongTien, hang, trangThai
        };

        JPanel body = new JPanel(new GridLayout(labels.length, 2, 10, 10));
        body.setBackground(new Color(0xF0EFF8));
        body.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        
        for (int i = 0; i < labels.length; i++) {
            JLabel lbl = new JLabel(labels[i]);
            lbl.setFont(new Font("Arial", Font.BOLD, 15));
            
            JLabel val = new JLabel(values[i] == null ? "-" : values[i].toString());
            val.setFont(new Font("Arial", Font.PLAIN, 15));
            
            // Tô màu cho Hạng và Trạng thái
            if (i == 9) { // Hạng
                String hangVal = values[i] == null ? "" : values[i].toString();
                switch (hangVal) {
                    case "Đồng" -> val.setForeground(new Color(0x8B4513));
                    case "Bạc" -> val.setForeground(new Color(0x808080));
                    case "Vàng" -> val.setForeground(new Color(0xFFD700));
                    case "Kim cương" -> val.setForeground(new Color(0x00BFFF));
                }
            } else if (i == 10) { // Trạng thái
                String tt = values[i] == null ? "" : values[i].toString();
                if (tt.equals("Hoạt động")) {
                    val.setForeground(new Color(0x388E3C));
                } else {
                    val.setForeground(new Color(0xC62828));
                }
            }
            
            body.add(lbl);
            body.add(val);
        }
        
        JScrollPane scrollDetail = new JScrollPane(body);
        scrollDetail.setBorder(null);
        scrollDetail.getVerticalScrollBar().setUnitIncrement(16);
        detail.add(scrollDetail, BorderLayout.CENTER);

        // Footer với các nút chức năng
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 14));
        footer.setBackground(new Color(0xF0EFF8));
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(0xCCCCCC)));

        JButton btnSua = new JButton("✏ Sửa");
        styleButton(btnSua, new Color(0x6677C8), 100, 40);
        btnSua.addActionListener(e -> {
            detail.dispose();
            editingRow = modelRow;
            // Load đầy đủ dữ liệu lên form
            tfMaKH.setText(maKH);
            tfTen.setText(ten);
            tfSdt.setText(sdt);
            tfEmail.setText(email);
            tfDiaChi.setText(diaChi);
            tfDiem.setText(diem);
            tfTgDK.setText(tgDK);
            tfLanCuoiMua.setText(lanCuoiMua);
            tfTongTien.setText(tongTien);
            tfHang.setText(hang);
            tfTrangThai.setText(trangThai);
            enableFormFields(true);
            innerCard.show(this, CARD_THEM);
        });

        JButton btnXoa = new JButton("🗑 Xóa");
        styleButton(btnXoa, new Color(0xB83434), 100, 40);
        btnXoa.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(detail,
                "Bạn có chắc muốn xóa khách hàng \"" + ten + "\"?",
                "Xác nhận xóa", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (confirm == JOptionPane.YES_OPTION) {
                model.removeRow(modelRow);
                detail.dispose();
            }
        });

        JButton btnDong = new JButton("Đóng");
        styleButton(btnDong, new Color(0x9B8EA8), 100, 40);
        btnDong.addActionListener(e -> detail.dispose());

        footer.add(btnSua);
        footer.add(btnXoa);
        footer.add(btnDong);
        detail.add(footer, BorderLayout.SOUTH);
        
        detail.setVisible(true);
    }

    // Các phương thức tạm thời để lấy dữ liệu bị ẩn - trong thực tế bạn sẽ lấy từ database
    private String getDiemFromData(String maKH) {
        // Trong thực tế, bạn sẽ query database
        switch(maKH) {
            case "KH001": return "36";
            case "KH002": return "580";
            case "KH003": return "120";
            case "KH004": return "950";
            default: return "0";
        }
    }

    private String getTgDKFromData(String maKH) {
        switch(maKH) {
            case "KH001": return "01/01/2026";
            case "KH002": return "15/03/2023";
            case "KH003": return "20/06/2023";
            case "KH004": return "10/10/2023";
            default: return "01/01/2024";
        }
    }

    private String getLanCuoiMuaFromData(String maKH) {
        switch(maKH) {
            case "KH001": return "15/03/2026";
            case "KH002": return "20/03/2024";
            case "KH003": return "10/02/2024";
            case "KH004": return "11/03/2024";
            default: return "01/01/2024";
        }
    }

    private String getTongTienFromData(String maKH) {
        switch(maKH) {
            case "KH001": return "3.600.000đ";
            case "KH002": return "12.070.000đ";
            case "KH003": return "1.012.000đ";
            case "KH004": return "27.000.000đ";
            default: return "0đ";
        }
    }

    /**
     * Style cho các nút trong dialog
     */
    private void styleButton(JButton btn, Color bg, int width, int height) {
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setPreferredSize(new Dimension(width, height));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setFont(new Font("Arial", Font.BOLD, 14));
    }

    /**
     * Xóa trắng tất cả các trường trong form
     */
    private void clearForm() {
        tfMaKH.setText("");
        tfTen.setText("");
        tfSdt.setText("");
        tfEmail.setText("");
        tfDiaChi.setText("");
        tfDiem.setText("");
        tfTgDK.setText("");
        tfLanCuoiMua.setText("");
        tfTongTien.setText("");
        tfHang.setText("");
        tfTrangThai.setText("");
    }

    /**
     * Bật/tắt chế độ chỉnh sửa cho các trường
     */
    private void enableFormFields(boolean enable) {
        tfMaKH.setEditable(enable);
        tfTen.setEditable(enable);
        tfSdt.setEditable(enable);
        tfEmail.setEditable(enable);
        tfDiaChi.setEditable(enable);
        tfDiem.setEditable(enable);
        tfTgDK.setEditable(enable);
        tfLanCuoiMua.setEditable(enable);
        tfTongTien.setEditable(enable);
        tfHang.setEditable(enable);
        tfTrangThai.setEditable(enable);
    }

    /**
     * Load dữ liệu từ model lên form (chỉ các cột hiển thị)
     */
    private void loadFormData(DefaultTableModel model, int row) {
        tfMaKH.setText(model.getValueAt(row, 0).toString());
        tfTen.setText(model.getValueAt(row, 1).toString());
        tfSdt.setText(model.getValueAt(row, 2).toString());
        tfEmail.setText(model.getValueAt(row, 3).toString());
        tfDiaChi.setText(model.getValueAt(row, 4).toString());
        tfHang.setText(model.getValueAt(row, 5).toString());
        tfTrangThai.setText(model.getValueAt(row, 6).toString());
        
        // Các trường bị ẩn - lấy từ data gốc
        String maKH = model.getValueAt(row, 0).toString();
        tfDiem.setText(getDiemFromData(maKH));
        tfTgDK.setText(getTgDKFromData(maKH));
        tfLanCuoiMua.setText(getLanCuoiMuaFromData(maKH));
        tfTongTien.setText(getTongTienFromData(maKH));
    }

    /**
     * Lưu khách hàng mới
     */
    private void saveNewCustomer(DefaultTableModel model) {
        // Kiểm tra dữ liệu bắt buộc
        if (tfMaKH.getText().trim().isEmpty() || tfTen.getText().trim().isEmpty() || 
            tfSdt.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Vui lòng nhập Mã KH, Tên và Số điện thoại!", 
                "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Thêm dòng mới vào table (chỉ các cột hiển thị)
        model.addRow(new Object[] {
            tfMaKH.getText(), tfTen.getText(), tfSdt.getText(), tfEmail.getText(),
            tfDiaChi.getText(), tfHang.getText(), tfTrangThai.getText(), ""
        });
        
        // Reset form và quay lại bảng
        clearForm();
        innerCard.show(this, CARD_TABLE);
    }

    /**
     * Cập nhật thông tin khách hàng
     */
    private void updateCustomer(DefaultTableModel model) {
        if (editingRow >= 0 && editingRow < model.getRowCount()) {
            model.setValueAt(tfMaKH.getText(), editingRow, 0);
            model.setValueAt(tfTen.getText(), editingRow, 1);
            model.setValueAt(tfSdt.getText(), editingRow, 2);
            model.setValueAt(tfEmail.getText(), editingRow, 3);
            model.setValueAt(tfDiaChi.getText(), editingRow, 4);
            model.setValueAt(tfHang.getText(), editingRow, 5);
            model.setValueAt(tfTrangThai.getText(), editingRow, 6);
            
            // Reset form và quay lại bảng
            clearForm();
            enableFormFields(true);
            editingRow = -1;
        }
    }

    /** Public navigation – used by DonHangCreateCard to jump straight to the add form */
    public void showCard(String card) {
        innerCard.show(this, card);
    }
}