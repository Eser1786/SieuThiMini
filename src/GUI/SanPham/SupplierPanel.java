package GUI.SanPham;

import BUS.SupplierBUS;
import DTO.SupplierDTO;
import GUI.ExportUtils;
import GUI.UIUtils;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.AbstractCellEditor;


public class SupplierPanel extends JPanel {
    private DefaultTableModel model;
    private JTable table;
    private TableRowSorter<DefaultTableModel> sorter;
    private SupplierBUS supplierBUS;

    public SupplierPanel() {
        supplierBUS = new SupplierBUS();
        setLayout(new BorderLayout());
        setBackground(new Color(0xF8F7FF));

        
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 12));
        header.setBackground(new Color(0xF8F7FF));
        header.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0xDDDDDD)),
            BorderFactory.createEmptyBorder(0, 20, 0, 20)));

        JPanel bar = new JPanel();
        bar.setPreferredSize(new Dimension(5, 26));
        bar.setBackground(new Color(0x5C4A7F));
        header.add(bar);
        header.add(Box.createHorizontalStrut(12));

        JLabel title = new JLabel("QUẢN LÝ NHÀ CUNG CẤP");
        title.setFont(new Font("Arial", Font.BOLD, 20));
        header.add(title);

        
        JPanel top = new JPanel(new GUI.WrapLayout(FlowLayout.LEFT, 8, 4));
        top.setBackground(new Color(0xF8F7FF));
        top.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0xCCCCCC), 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));

        
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.setPreferredSize(new Dimension(250, 36));
        searchPanel.setBackground(Color.WHITE);
        searchPanel.setBorder(BorderFactory.createLineBorder(new Color(0xBBBBBB), 1));

        JTextField searchField = new JTextField();
        searchField.setFont(new Font("Arial", Font.PLAIN, 13));
        searchField.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 4));
        searchPanel.add(searchField, BorderLayout.CENTER);

        JButton searchBtn = new JButton("Q");
        searchBtn.setBorderPainted(false);
        searchBtn.setContentAreaFilled(false);
        searchBtn.setFocusPainted(false);
        searchBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        searchBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                searchBtn.setContentAreaFilled(true);
                searchBtn.setBackground(new Color(0xC5B3E6));
                searchBtn.setOpaque(true);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                searchBtn.setContentAreaFilled(false);
                searchBtn.setOpaque(false);
            }
        });
        searchPanel.add(searchBtn, BorderLayout.EAST);

        
        JComboBox<String> searchType = new JComboBox<>(new String[]{"Tất cả", "Mã NCC", "Tên NCC", "Địa chỉ", "Người liên lạc", "SĐT", "Email"});
        searchType.setPreferredSize(new Dimension(150, 36));
        UIUtils.styleComboBox(searchType);

        
        JButton addBtn = new JButton("+ Thêm nhà cung cấp");
        addBtn.setFocusPainted(false);
        addBtn.setBackground(new Color(0xD9D9D9));
        addBtn.setFont(new Font("Arial", Font.BOLD, 13));
        addBtn.setBorder(BorderFactory.createEmptyBorder(9, 14, 9, 14));
        addBtn.setOpaque(true);
        addBtn.setBorderPainted(false);
        addBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        addBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                addBtn.setBackground(new Color(0xC5B3E6));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                addBtn.setBackground(new Color(0xD9D9D9));
            }
        });
        addBtn.addActionListener(e -> showAddSupplierDialog());

        JButton btnPDF    = ExportUtils.makeExportButton("Xuất PDF",   new Color(0x7B52AB));
        JButton btnExcel  = ExportUtils.makeExportButton("Xuất Excel", new Color(0x2E7D32));
        JButton btnImport = ExportUtils.makeImportButton("Nhập CSV");
        for (JButton b : new JButton[]{btnPDF, btnExcel, btnImport})
            b.setFont(new Font("Arial", Font.BOLD, 13));
        btnPDF.addActionListener(e -> ExportUtils.xuatPDF(this, model, "Danh sách nhà cung cấp"));
        btnExcel.addActionListener(e -> ExportUtils.xuatCSV(this, model, "nha_cung_cap"));
        btnImport.addActionListener(e -> {
            List<String[]> rows = ExportUtils.importCSV(this);
            if (rows == null) return;
            for (String[] r : rows) {
                if (r.length < 6) continue;
                model.addRow(new Object[]{r[0], r[1], r[2], r[3], r[4], r[5], "Chi tiết"});
            }
        });

        top.add(searchPanel);
        top.add(searchType);
        top.add(addBtn);
        top.add(btnPDF);
        top.add(btnExcel);
        top.add(btnImport);

        
        model = new DefaultTableModel(new Object[]{"Mã NCC", "Tên NCC", "Địa chỉ", "Người liên lạc", "SĐT", "Email", "Thao tác"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 6; 
            }
        };
        table = new JTable(model);
        sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);
        
        sorter.setSortKeys(java.util.Collections.singletonList(new javax.swing.RowSorter.SortKey(1, javax.swing.SortOrder.ASCENDING)));
        table.setRowHeight(50); 
        table.setFont(new Font("Arial", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(0xAF9FCB));
        table.getTableHeader().setForeground(Color.WHITE);
        table.setGridColor(new Color(0xEEEEEE));
        table.setShowVerticalLines(false);
        table.setSelectionBackground(new Color(0xC5B3E6));

        
        DefaultTableCellRenderer altRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value, boolean isSelected,
                    boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, col);
                if (!isSelected) c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(0xF3F0FA));
                setHorizontalAlignment(col == 0 ? SwingConstants.CENTER : SwingConstants.LEFT);
                return c;
            }
        };
        for (int i = 0; i < 6; i++) table.getColumnModel().getColumn(i).setCellRenderer(altRenderer);

        
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer); 

        
        table.getColumnModel().getColumn(6).setCellRenderer(new ActionRenderer());
        table.getColumnModel().getColumn(6).setCellEditor(new ActionEditor());

        JScrollPane scrollPane = new JScrollPane(table);
        UIUtils.styleScrollPane(scrollPane);

        
        Runnable applyFilter = () -> {
            String keyword = searchField.getText().trim();
            String type = (String) searchType.getSelectedItem();
            if (keyword.isEmpty()) {
                sorter.setRowFilter(null);
            } else {
                sorter.setRowFilter(new javax.swing.RowFilter<DefaultTableModel, Integer>() {
                    @Override
                    public boolean include(javax.swing.RowFilter.Entry<? extends DefaultTableModel, ? extends Integer> entry) {
                        String value = "";
                        if ("Tất cả".equals(type)) {
                            for (int i = 0; i < entry.getValueCount() - 1; i++) {
                                value += entry.getStringValue(i) + " ";
                            }
                        } else {
                            switch (type) {
                                case "Mã NCC": value = entry.getStringValue(0); break;
                                case "Tên NCC": value = entry.getStringValue(1); break;
                                case "Địa chỉ": value = entry.getStringValue(2); break;
                                case "Người liên lạc": value = entry.getStringValue(3); break;
                                case "SĐT": value = entry.getStringValue(4); break;
                                case "Email": value = entry.getStringValue(5); break;
                            }
                        }
                        return value.toLowerCase().contains(keyword.toLowerCase());
                    }
                });
            }
        };

        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { applyFilter.run(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { applyFilter.run(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { applyFilter.run(); }
        });
        searchType.addActionListener(e -> applyFilter.run());
        searchBtn.addActionListener(e -> applyFilter.run());

        JPanel northPanel = new JPanel();
        northPanel.setLayout(new BoxLayout(northPanel, BoxLayout.Y_AXIS));
        northPanel.add(header);
        northPanel.add(top);
        add(northPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        loadSuppliers();
    }

    private void loadSuppliers() {
        try {
            List<SupplierDTO> suppliers = supplierBUS.getAllSuppliers();
            model.setRowCount(0);
            for (SupplierDTO s : suppliers) {
                
                model.addRow(new Object[]{s.getCode(), s.getName(), s.getAddress(), s.getContactPerson(), s.getPhone(), s.getEmail(), "Chi tiết"});
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi tải danh sách nhà cung cấp: " + e.getMessage());
        }
    }

    private void showAddSupplierDialog() {
        showSupplierDialog(null);
    }

    private void showEditSupplierDialog(int id) {
        try {
            SupplierDTO supplier = supplierBUS.getSupplierById(id);
            showSupplierDialog(supplier);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi tải nhà cung cấp: " + e.getMessage());
        }
    }

    private void showSupplierDialog(SupplierDTO supplier) {
        Window owner = SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog(owner, supplier == null ? "Thêm nhà cung cấp" : "Sửa nhà cung cấp",
                Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setResizable(false);
        dialog.setLayout(new BorderLayout());

        
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 14));
        header.setBackground(new Color(0xAF9FCB));
        JLabel titleLabel = new JLabel(supplier == null ? "Thêm nhà cung cấp mới" : "Sửa nhà cung cấp");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        header.add(titleLabel);

        
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(new Color(0xF0EFF8));
        form.setBorder(BorderFactory.createEmptyBorder(18, 28, 18, 28));
        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.HORIZONTAL;
        g.insets = new Insets(7, 6, 7, 6);
        Font labelFont = new Font("Arial", Font.BOLD, 13);
        javax.swing.border.Border fldBorder = BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xBBBBBB)),
                BorderFactory.createEmptyBorder(4, 8, 4, 8));
        Dimension fieldSize = new Dimension(260, 36);

        
        JTextField txtCode = new JTextField();
        txtCode.setFont(new Font("Arial", Font.BOLD, 13));
        txtCode.setBorder(fldBorder);
        txtCode.setPreferredSize(new Dimension(150, 36));
        txtCode.setEditable(false);
        txtCode.setBackground(new Color(0xE8E8E8));
        txtCode.setText(supplier != null ? supplier.getCode() : supplierBUS.generateSupplierCode());

        JTextField txtName    = new JTextField(); txtName.setFont(new Font("Arial", Font.PLAIN, 13));    txtName.setBorder(fldBorder);    txtName.setPreferredSize(fieldSize);
        JTextField txtAddress = new JTextField(); txtAddress.setFont(new Font("Arial", Font.PLAIN, 13)); txtAddress.setBorder(fldBorder); txtAddress.setPreferredSize(fieldSize);
        JTextField txtContact = new JTextField(); txtContact.setFont(new Font("Arial", Font.PLAIN, 13)); txtContact.setBorder(fldBorder); txtContact.setPreferredSize(fieldSize);
        JTextField txtPhone   = new JTextField(); txtPhone.setFont(new Font("Arial", Font.PLAIN, 13));   txtPhone.setBorder(fldBorder);   txtPhone.setPreferredSize(fieldSize);
        JTextField txtEmail   = new JTextField(); txtEmail.setFont(new Font("Arial", Font.PLAIN, 13));   txtEmail.setBorder(fldBorder);   txtEmail.setPreferredSize(fieldSize);

        if (supplier != null) {
            txtName.setText(supplier.getName());
            txtAddress.setText(supplier.getAddress());
            txtContact.setText(supplier.getContactPerson());
            txtPhone.setText(supplier.getPhone());
            txtEmail.setText(supplier.getEmail());
        }

        g.gridy = 0; g.gridx = 0; g.weightx = 0;
        JLabel lbCode = new JLabel("Mã NCC:"); lbCode.setFont(labelFont);
        form.add(lbCode, g);
        g.gridx = 1; g.weightx = 1;
        form.add(txtCode, g);

        g.gridy = 1; g.gridx = 0; g.weightx = 0;
        JLabel lbName = new JLabel("Tên nhà cung cấp:"); lbName.setFont(labelFont); form.add(lbName, g);
        g.gridx = 1; g.weightx = 1; form.add(txtName, g);

        g.gridy = 2; g.gridx = 0; g.weightx = 0;
        JLabel lbAddr = new JLabel("Địa chỉ:"); lbAddr.setFont(labelFont); form.add(lbAddr, g);
        g.gridx = 1; g.weightx = 1; form.add(txtAddress, g);

        g.gridy = 3; g.gridx = 0; g.weightx = 0;
        JLabel lbCt = new JLabel("Người liên lạc:"); lbCt.setFont(labelFont); form.add(lbCt, g);
        g.gridx = 1; g.weightx = 1; form.add(txtContact, g);

        g.gridy = 4; g.gridx = 0; g.weightx = 0;
        JLabel lbPhone = new JLabel("SĐT:"); lbPhone.setFont(labelFont); form.add(lbPhone, g);
        g.gridx = 1; g.weightx = 1; form.add(txtPhone, g);

        g.gridy = 5; g.gridx = 0; g.weightx = 0;
        JLabel lbEmail = new JLabel("Email:"); lbEmail.setFont(labelFont); form.add(lbEmail, g);
        g.gridx = 1; g.weightx = 1; form.add(txtEmail, g);

        
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        footer.setBackground(new Color(0xF0EFF8));
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(0xCCCCCC)));

        JButton saveBtn = UIUtils.makeActionButton("Lưu", new Color(0x5C4A7F));
        saveBtn.setPreferredSize(new Dimension(90, 34));
        saveBtn.addActionListener(e -> {
            try {
                if (txtName.getText().trim().isEmpty() ||
                    txtAddress.getText().trim().isEmpty() ||
                    txtContact.getText().trim().isEmpty() ||
                    txtPhone.getText().trim().isEmpty() ||
                    txtEmail.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Vui lòng điền đầy đủ thông tin.");
                    return;
                }
                SupplierDTO s = supplier == null ? new SupplierDTO() : supplier;
                s.setCode(txtCode.getText().trim());
                s.setName(txtName.getText().trim());
                s.setAddress(txtAddress.getText().trim());
                s.setContactPerson(txtContact.getText().trim());
                s.setPhone(txtPhone.getText().trim());
                s.setEmail(txtEmail.getText().trim());
                if (supplier == null) supplierBUS.addSupplier(s);
                else supplierBUS.updateSupplier(s);
                loadSuppliers();
                dialog.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Lỗi: " + ex.getMessage());
            }
        });

        JButton cancelBtn = UIUtils.makeActionButton("Hủy", new Color(0x9E9E9E));
        cancelBtn.setPreferredSize(new Dimension(80, 34));
        cancelBtn.addActionListener(e -> dialog.dispose());

        footer.add(saveBtn);
        footer.add(cancelBtn);

        dialog.add(header, BorderLayout.NORTH);
        dialog.add(form,   BorderLayout.CENTER);
        dialog.add(footer, BorderLayout.SOUTH);
        dialog.pack();
        dialog.setMinimumSize(new Dimension(420, dialog.getHeight()));
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void deleteSupplier(int id) {
        int opt = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa nhà cung cấp này?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (opt == JOptionPane.YES_OPTION) {
            try {
                supplierBUS.deleteSupplier(id);
                loadSuppliers();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Lỗi xóa nhà cung cấp: " + e.getMessage());
            }
        }
    }

    
    private class ActionRenderer extends JPanel implements TableCellRenderer {
        private final JButton editBtn   = UIUtils.makeActionButton("Sửa",  new Color(0x6677C8));
        private final JButton deleteBtn = UIUtils.makeActionButton("Xóa",  new Color(0xC62828));

        public ActionRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 6, 8));
            setOpaque(true);
            add(editBtn);
            add(deleteBtn);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            setBackground(isSelected ? table.getSelectionBackground()
                    : (row % 2 == 0 ? Color.WHITE : new Color(0xF3F0FA)));
            return this;
        }
    }

    private class ActionEditor extends AbstractCellEditor implements TableCellEditor {
        private final JPanel  panel     = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 8));
        private final JButton editBtn   = UIUtils.makeActionButton("Sửa",  new Color(0x6677C8));
        private final JButton deleteBtn = UIUtils.makeActionButton("Xóa",  new Color(0xC62828));
        private int currentRow;

        public ActionEditor() {
            panel.setOpaque(true);
            panel.add(editBtn);
            panel.add(deleteBtn);

            editBtn.addActionListener(e -> {
                String code = (String) model.getValueAt(currentRow, 0);
                int id = supplierBUS.getIdByCode(code);
                showEditSupplierDialog(id);
                fireEditingStopped();
            });

            deleteBtn.addActionListener(e -> {
                String code = (String) model.getValueAt(currentRow, 0);
                int id = supplierBUS.getIdByCode(code);
                deleteSupplier(id);
                fireEditingStopped();
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            currentRow = table.convertRowIndexToModel(row);
            panel.setBackground(isSelected ? table.getSelectionBackground()
                    : (row % 2 == 0 ? Color.WHITE : new Color(0xF3F0FA)));
            return panel;
        }

        @Override
        public Object getCellEditorValue() { return ""; }
    }
}