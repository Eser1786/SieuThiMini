package GUI.KhachHang;

import GUI.ExportUtils;
import GUI.UIUtils;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableRowSorter;
import java.awt.*;

/**
 * Panel for customer management. Contains a card layout switching between
 * the table and the add/edit form.
 */
public class KhachHangPanel extends JPanel {
    private CardLayout innerCard;
    private int editingRow = -1;
    private JTextField tfMaKH, tfTen, tfSdt, tfEmail, tfDiaChi, tfDiem, tfTgDK, tfLanCuoiMua, tfTongTien, tfHang,
            tfTrangThai;

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

        String[] columns = {
                "Mã KH", "Tên", "Số điện thoại", "Email",
                "Địa chỉ", "Điểm", "Thời gian đăng kí",
                "Lần cuối mua", "Tổng tiền", "Hạng", "Trạng thái", "Thao tác"
        };

        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 11; // Chỉ cột cuối (Thao tác) được phép chỉnh sửa
            }
        };

        // thim đại mấy ngừ
        model.addRow(new Object[] {
                "KH001", "Lê Đỗ Thái Anh", "098754321", "anhdo@gmail.com",
                "213LDTA", "36", "01/01/2026",
                "15/03/2026", "3.600.000đ", "Bạc", "Hoạt động", ""
        });
        model.addRow(new Object[] {
                "KH002", "Lý Nguyễn", "0915987654", "nguyenly@gmail.com",
                "456 Nguyễn Trãii", "580", "15/03/2023",
                "20/03/2024", "12.070.000đ", "Vàng", "Hoạt động", ""
        });
        model.addRow(new Object[] {
                "KH003", "Nguyễn Hoàng Sang", "0933777888", "sangnguyen@gmail.com",
                "789KTX", "120", "20/06/2023",
                "10/02/2024", "1.012.000đ", "Đồng", "Không hoạt động", ""
        });
        model.addRow(new Object[] {
                "KH004", "Trân dơ hầy", "0977111222", "tranbado@gmail.com",
                "Bụi chúi", "950", "10/10/2023",
                "11/03/2024", "27.000.000đ", "Kim cương", "Hoạt động", ""
        });

        JPanel tableCard = new JPanel(new BorderLayout());

        JTable bang = new JTable(model);
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        bang.setRowSorter(sorter); // Cho phép sắp xếp khi click vào header

        JPanel top = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 25));
        top.setPreferredSize(new Dimension(0, 94));
        top.setBackground(new Color(0xF8F7FF));
        top.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLACK, 1),
                BorderFactory.createEmptyBorder(0, 10, 0, 10)));

        String[] boloc = { "Tất cả", "Đồng", "Bạc", "Vàng", "Kim cương", "Hoạt động", "Không hoạt động" };
        JComboBox<String> cbLoc = new JComboBox<>(boloc);
        cbLoc.setPreferredSize(new Dimension(220, 42));
        cbLoc.setFont(new Font("Arial", Font.PLAIN, 22));
        cbLoc.setBackground(new Color(0xD9D9D9));

        JPanel timkiem = new JPanel(new BorderLayout());
        timkiem.setPreferredSize(new Dimension(229, 42));
        timkiem.setBackground(new Color(0xD9D9D9));
        JTextField tim = new JTextField();
        tim.setFont(new Font("Arial", Font.PLAIN, 22));
        tim.setBackground(new Color(0xD9D9D9));
        tim.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 4));
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

        JButton them = new JButton("+ Thêm khách hàng");
        them.setFocusPainted(false);
        them.setBackground(new Color(0xD9D9D9));
        them.setPreferredSize(new Dimension(220, 42));
        them.setFont(new Font("Arial", Font.BOLD, 18));
        them.setCursor(new Cursor(Cursor.HAND_CURSOR));
        them.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                them.setBackground(new Color(0xC5B3E6));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                them.setBackground(new Color(0xD9D9D9));
            }
        });
        them.addActionListener(e -> {
            editingRow = -1;
            clearForm();
            enableFormFields(true);
            innerCard.show(this, CARD_THEM);
        });

        Runnable applyFilter = () -> {
            String tuKhoa = tim.getText().trim();
            int idxLoc = cbLoc.getSelectedIndex();

            // Tạo filter theo loại lọc
            RowFilter<DefaultTableModel, Integer> filterLoc = null;

            switch (idxLoc) {
                case 1: // Đồng
                    filterLoc = RowFilter.regexFilter("(?i)^Đồng$", 9);
                    break;
                case 2: // Bạc
                    filterLoc = RowFilter.regexFilter("(?i)^Bạc$", 9);
                    break;
                case 3: // Vàng
                    filterLoc = RowFilter.regexFilter("(?i)^Vàng$", 9);
                    break;
                case 4: // Kim cương
                    filterLoc = RowFilter.regexFilter("(?i)^Kim cương$", 9);
                    break;
                case 5: // Hoạt động
                    filterLoc = RowFilter.regexFilter("(?i)^Hoạt động$", 10);
                    break;
                case 6: // Không hoạt động
                    filterLoc = RowFilter.regexFilter("(?i)^Không hoạt động$", 10);
                    break;
                default: // Tất cả
                    filterLoc = null;
                    break;
            }

            RowFilter<DefaultTableModel, Integer> filterTim = null;
            if (!tuKhoa.isEmpty()) {
                filterTim = RowFilter.orFilter(
                        java.util.List.of(
                                RowFilter.regexFilter("(?i)" + tuKhoa, 0), // Tìm theo mã KH
                                RowFilter.regexFilter("(?i)" + tuKhoa, 1), // Tìm theo tên
                                RowFilter.regexFilter("(?i)" + tuKhoa, 2) // Tìm theo SĐT
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

        top.add(cbLoc);
        top.add(timkiem);
        top.add(them);

        // Nút xuất PDF
        JButton btnPDF = ExportUtils.makeExportButton("📄 Xuất PDF", new Color(0x7B52AB));
        btnPDF.addActionListener(e -> ExportUtils.xuatPDF(KhachHangPanel.this, model, "Danh sách khách hàng"));
        top.add(btnPDF);

        // Nút xuất Excel (CSV)
        JButton btnExcel = ExportUtils.makeExportButton("📊 Xuất Excel", new Color(0x2E7D32));
        btnExcel.addActionListener(e -> ExportUtils.xuatCSV(KhachHangPanel.this, model, "khach_hang"));
        top.add(btnExcel);

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

        bang.getColumnModel().getColumn(0).setPreferredWidth(80); // Mã KH
        bang.getColumnModel().getColumn(1).setPreferredWidth(150); // Tên
        bang.getColumnModel().getColumn(2).setPreferredWidth(120); // SĐT
        bang.getColumnModel().getColumn(3).setPreferredWidth(180); // Email
        bang.getColumnModel().getColumn(4).setPreferredWidth(200); // Địa chỉ
        bang.getColumnModel().getColumn(5).setPreferredWidth(60); // Điểm
        bang.getColumnModel().getColumn(6).setPreferredWidth(100); // Tg đăng kí
        bang.getColumnModel().getColumn(7).setPreferredWidth(100); // Lần cuối mua
        bang.getColumnModel().getColumn(8).setPreferredWidth(100); // Tổng tiền
        bang.getColumnModel().getColumn(9).setPreferredWidth(80); // Hạng
        bang.getColumnModel().getColumn(10).setPreferredWidth(100); // Trạng thái
        bang.getColumnModel().getColumn(11).setPreferredWidth(100); // Thao tác

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
                    if (column == 9) { // Cột Hạng
                        String val = value == null ? "" : value.toString();
                        switch (val) {
                            case "Đồng" -> setForeground(new Color(0x8B4513)); // Màu nâu
                            case "Bạc" -> setForeground(new Color(0x808080)); // Màu xám
                            case "Vàng" -> setForeground(new Color(0xFFD700)); // Màu vàng
                            case "Kim cương" -> setForeground(new Color(0x00BFFF)); // Màu xanh dương
                            default -> setForeground(Color.BLACK);
                        }
                    } else if (column == 10) { // Cột Trạng thái
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

        bang.getColumnModel().getColumn(11).setCellRenderer(new TableCellRenderer() {
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

        bang.getColumnModel().getColumn(11).setCellEditor(new DefaultCellEditor(new JCheckBox()) {
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
                    editingRow = modelRow;

                    loadFormData(model, modelRow);

                    enableFormFields(false);

                    innerCard.show(KhachHangPanel.this, CARD_THEM);
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

        content.add(new JScrollPane(bang), BorderLayout.CENTER);
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

        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel lbDiaChi = new JLabel("Địa chỉ:");
        lbDiaChi.setFont(labelFont);
        formBody.add(lbDiaChi, gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 3;
        formBody.add(tfDiaChi, gbc);
        gbc.gridwidth = 1;

        gbc.gridx = 0;
        gbc.gridy = 3;
        JLabel lbDiem = new JLabel("Điểm tích lũy:");
        lbDiem.setFont(labelFont);
        formBody.add(lbDiem, gbc);

        gbc.gridx = 1;
        formBody.add(tfDiem, gbc);

        gbc.gridx = 2;
        JLabel lbHang = new JLabel("Hạng:");
        lbHang.setFont(labelFont);
        formBody.add(lbHang, gbc);

        gbc.gridx = 3;
        formBody.add(tfHang, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        JLabel lbTgDK = new JLabel("Thời gian ĐK:");
        lbTgDK.setFont(labelFont);
        formBody.add(lbTgDK, gbc);

        gbc.gridx = 1;
        formBody.add(tfTgDK, gbc);

        gbc.gridx = 2;
        JLabel lbLanCuoi = new JLabel("Lần cuối mua:");
        lbLanCuoi.setFont(labelFont);
        formBody.add(lbLanCuoi, gbc);

        gbc.gridx = 3;
        formBody.add(tfLanCuoiMua, gbc);

        // Hàng 6: Tổng tiền và Trạng thái
        gbc.gridx = 0;
        gbc.gridy = 5;
        JLabel lbTongTien = new JLabel("Tổng tiền đã mua:");
        lbTongTien.setFont(labelFont);
        formBody.add(lbTongTien, gbc);

        gbc.gridx = 1;
        formBody.add(tfTongTien, gbc);

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

        JButton btnLuu = new JButton("LƯU");
        btnLuu.setFont(new Font("Arial", Font.BOLD, 24));
        btnLuu.setBackground(new Color(0xB83434));
        btnLuu.setForeground(Color.WHITE);
        btnLuu.setFocusPainted(false);
        btnLuu.setBorderPainted(false);
        btnLuu.setPreferredSize(new Dimension(160, 52));
        btnLuu.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JButton btnSua = new JButton("SỬA");
        btnSua.setFont(new Font("Arial", Font.BOLD, 24));
        btnSua.setBackground(new Color(0x6677C8));
        btnSua.setForeground(Color.WHITE);
        btnSua.setFocusPainted(false);
        btnSua.setBorderPainted(false);
        btnSua.setPreferredSize(new Dimension(160, 52));
        btnSua.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JButton btnXoa = new JButton("XÓA");
        btnXoa.setFont(new Font("Arial", Font.BOLD, 24));
        btnXoa.setBackground(new Color(0xB83434));
        btnXoa.setForeground(Color.WHITE);
        btnXoa.setFocusPainted(false);
        btnXoa.setBorderPainted(false);
        btnXoa.setPreferredSize(new Dimension(160, 52));
        btnXoa.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnLuu.addActionListener(e -> {
            if (editingRow == -1) {
                // Chế độ thêm mới
                saveNewCustomer(model);
            } else {
                // Chế độ sửa (đã bật editable)
                updateCustomer(model);
            }
        });

        btnSua.addActionListener(e -> {
            if (editingRow >= 0) {
                // Chuyển sang chế độ sửa
                enableFormFields(true);
                btnSua.setVisible(false);
                btnLuu.setVisible(true);
                btnXoa.setVisible(true);
            }
        });

        btnXoa.addActionListener(e -> {
            if (editingRow >= 0) {
                int confirm = JOptionPane.showConfirmDialog(this,
                        "Bạn có chắc muốn xóa khách hàng này?",
                        "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    model.removeRow(editingRow);
                    clearForm();
                    enableFormFields(true);
                    editingRow = -1;
                    innerCard.show(this, CARD_TABLE);
                }
            }
        });

        btnWrapper.add(btnLuu);
        btnWrapper.add(btnSua);
        btnWrapper.add(btnXoa);
        themCard.add(btnWrapper, BorderLayout.SOUTH);

        add(tableCard, CARD_TABLE);
        add(themCard, CARD_THEM);
        innerCard.show(this, CARD_TABLE); // Hiển thị card bảng ban đầu
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
     * Load dữ liệu từ model lên form
     */
    private void loadFormData(DefaultTableModel model, int row) {
        tfMaKH.setText(model.getValueAt(row, 0).toString());
        tfTen.setText(model.getValueAt(row, 1).toString());
        tfSdt.setText(model.getValueAt(row, 2).toString());
        tfEmail.setText(model.getValueAt(row, 3).toString());
        tfDiaChi.setText(model.getValueAt(row, 4).toString());
        tfDiem.setText(model.getValueAt(row, 5).toString());
        tfTgDK.setText(model.getValueAt(row, 6).toString());
        tfLanCuoiMua.setText(model.getValueAt(row, 7).toString());
        tfTongTien.setText(model.getValueAt(row, 8).toString());
        tfHang.setText(model.getValueAt(row, 9).toString());
        tfTrangThai.setText(model.getValueAt(row, 10).toString());
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

        // Thêm dòng mới
        model.addRow(new Object[] {
                tfMaKH.getText(), tfTen.getText(), tfSdt.getText(), tfEmail.getText(),
                tfDiaChi.getText(), tfDiem.getText(), tfTgDK.getText(),
                tfLanCuoiMua.getText(), tfTongTien.getText(), tfHang.getText(),
                tfTrangThai.getText(), ""
        });

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
            model.setValueAt(tfDiem.getText(), editingRow, 5);
            model.setValueAt(tfTgDK.getText(), editingRow, 6);
            model.setValueAt(tfLanCuoiMua.getText(), editingRow, 7);
            model.setValueAt(tfTongTien.getText(), editingRow, 8);
            model.setValueAt(tfHang.getText(), editingRow, 9);
            model.setValueAt(tfTrangThai.getText(), editingRow, 10);

            clearForm();
            enableFormFields(true);
            editingRow = -1;
            innerCard.show(this, CARD_TABLE);
        }
    }
}

