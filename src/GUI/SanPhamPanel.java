package GUI;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableRowSorter;
import java.awt.*;

/**
 * Panel for product management. Contains a card layout switching between
 * the table and the add/edit form.
 */
public class SanPhamPanel extends JPanel {
    private CardLayout innerCard;
    private int editingRow = -1;
    private JTextField tfTen, tfMa, tfSL, tfGia, tfDate, tfKM;

    public static final String CARD_TABLE = "TABLE";
    public static final String CARD_THEM  = "THEM";

    public SanPhamPanel() {
        innerCard = new CardLayout();
        setLayout(innerCard);

        // shared fields
        tfTen  = UIUtils.makeField();
        tfMa   = UIUtils.makeField();
        tfSL   = UIUtils.makeField();
        tfGia  = UIUtils.makeField();
        tfDate = UIUtils.makeField();
        tfKM   = UIUtils.makeField();

        String[] columns = {
            "Mã SP", "Ảnh", "Tên sản phẩm", "Giá",
            "Số lượng", "Kho", "Date", "Khuyến mãi", "Thao tác"
        };
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return column == 8; }
        };
        model.addRow(new Object[]{ "MD01", "", "Nước F trái K", "25.000đ", 7, "Còn hàng", "26/10/2026", "-2.000đ", "" });
        model.addRow(new Object[]{ "MD02", "", "Thịt mèo cháy", "17.000đ", 7, "Còn hàng", "10/11/2026", "-7.000đ", "" });

        // card with table
        JPanel tableCard = new JPanel(new BorderLayout());

        JTable bang = new JTable(model);
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        bang.setRowSorter(sorter);

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
        them.addActionListener(e -> innerCard.show(this, CARD_THEM));

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
                                dateStr,
                                java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")
                            );
                            java.time.LocalDate today = java.time.LocalDate.now();
                            return !date.isBefore(today) && date.isBefore(today.plusDays(7));
                        } catch (Exception ex) {
                            return false;
                        }
                    }
                };
                default -> null;
            };

            RowFilter<DefaultTableModel, Integer> filterTim = tuKhoa.isEmpty()
                ? null
                : RowFilter.regexFilter("(?i)" + tuKhoa, 2);

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
        bang.getColumnModel().getColumn(8).setPreferredWidth(100);

        DefaultTableCellRenderer altRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);
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
        for (int i = 0; i < bang.getColumnCount() - 1; i++)
            bang.getColumnModel().getColumn(i).setCellRenderer(altRenderer);

        bang.getColumnModel().getColumn(8).setCellRenderer(new TableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {
                JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 8));
                p.setBackground(row % 2 == 0 ? Color.WHITE : new Color(0xF3F0FA));
                JButton sua = UIUtils.makeActionButton("Sửa", new Color(0x6677C8));
                JButton xoa = UIUtils.makeActionButton("Xóa", new Color(0xB83434));
                p.add(sua); p.add(xoa);
                return p;
            }
        });

        bang.getColumnModel().getColumn(8).setCellEditor(new DefaultCellEditor(new JCheckBox()) {
            private final JPanel p    = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 8));
            private final JButton sua = UIUtils.makeActionButton("Sửa", new Color(0x6677C8));
            private final JButton xoa = UIUtils.makeActionButton("Xóa", new Color(0xB83434));
            private int currentRow = -1;
            {
                p.setOpaque(true);
                p.add(sua); p.add(xoa);
                sua.addActionListener(e -> {
                    fireEditingStopped();
                    int modelRow = bang.convertRowIndexToModel(currentRow);
                    editingRow = modelRow;
                    tfMa.setText(model.getValueAt(modelRow, 0).toString());
                    tfTen.setText(model.getValueAt(modelRow, 2).toString());
                    tfGia.setText(model.getValueAt(modelRow, 3).toString());
                    tfSL.setText(model.getValueAt(modelRow, 4).toString());
                    tfDate.setText(model.getValueAt(modelRow, 6).toString());
                    String km = model.getValueAt(modelRow, 7).toString();
                    tfKM.setText(km.equals("-") ? "" : km);
                    innerCard.show(SanPhamPanel.this, CARD_THEM);
                });
                xoa.addActionListener(e -> {
                    fireEditingStopped();
                    if (currentRow >= 0 && currentRow < model.getRowCount())
                        model.removeRow(currentRow);
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

        // form card (add/edit)
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
            tfTen.setText(""); tfMa.setText(""); tfSL.setText("");
            tfGia.setText(""); tfDate.setText(""); tfKM.setText("");
            editingRow = -1;
            innerCard.show(this, CARD_TABLE);
        });
        formHeader.add(btnQuayLai);
        themCard.add(formHeader, BorderLayout.NORTH);

        JPanel formBody = new JPanel();
        formBody.setBackground(new Color(0xF0EFF8));
        formBody.setBorder(BorderFactory.createEmptyBorder(20, 60, 20, 60));
        formBody.setLayout(new GridLayout(6, 2, 20, 20));
        formBody.add(new JLabel("Mã SP")); formBody.add(tfMa);
        formBody.add(new JLabel("Tên")); formBody.add(tfTen);
        formBody.add(new JLabel("Giá")); formBody.add(tfGia);
        formBody.add(new JLabel("Số lượng")); formBody.add(tfSL);
        formBody.add(new JLabel("Ngày")); formBody.add(tfDate);
        formBody.add(new JLabel("Khuyến mãi")); formBody.add(tfKM);
        themCard.add(formBody, BorderLayout.CENTER);

        JButton btnLuu = new JButton("LƯU");
        btnLuu.setFont(new Font("Arial", Font.BOLD, 24));
        btnLuu.setBackground(new Color(0xB83434));
        btnLuu.setForeground(Color.WHITE);
        btnLuu.setFocusPainted(false);
        btnLuu.setBorderPainted(false);
        btnLuu.setPreferredSize(new Dimension(160, 52));
        btnLuu.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLuu.addActionListener(e -> {
            if (editingRow >= 0 && editingRow < model.getRowCount()) {
                model.setValueAt(tfMa.getText(), editingRow, 0);
                model.setValueAt(tfTen.getText(), editingRow, 2);
                model.setValueAt(tfGia.getText(), editingRow, 3);
                model.setValueAt(tfSL.getText(), editingRow, 4);
                model.setValueAt(tfDate.getText(), editingRow, 6);
                model.setValueAt(tfKM.getText().isEmpty() ? "-" : tfKM.getText(), editingRow, 7);
            } else {
                model.addRow(new Object[] {
                    tfMa.getText(), "", tfTen.getText(), tfGia.getText(),
                    tfSL.getText(), "Còn hàng", tfDate.getText(),
                    tfKM.getText().isEmpty() ? "-" : tfKM.getText(), ""
                });
            }
            tfTen.setText(""); tfMa.setText(""); tfSL.setText("");
            tfGia.setText(""); tfDate.setText(""); tfKM.setText("");
            editingRow = -1;
            innerCard.show(this, CARD_TABLE);
        });
        JPanel btnWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnWrapper.setBackground(new Color(0xF0EFF8));
        btnWrapper.add(btnLuu);
        themCard.add(btnWrapper, BorderLayout.SOUTH);

        add(tableCard, CARD_TABLE);
        add(themCard, CARD_THEM);
        innerCard.show(this, CARD_TABLE);
    }
}
