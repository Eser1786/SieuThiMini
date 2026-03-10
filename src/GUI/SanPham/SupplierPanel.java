package GUI.SanPham;

import BUS.SupplierBUS;
import DTO.SupplierDTO;
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

/**
 * Panel for supplier management within product management.
 */
public class SupplierPanel extends JPanel {
    private DefaultTableModel model;
    private JTable table;
    private TableRowSorter<DefaultTableModel> sorter;
    private SupplierBUS supplierBUS;

    public SupplierPanel() {
        supplierBUS = new SupplierBUS();
        setLayout(new BorderLayout());
        setBackground(new Color(0xF8F7FF));

        // Header
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

        // Top panel with filters and buttons
        JPanel top = new JPanel(new GUI.WrapLayout(FlowLayout.LEFT, 8, 4));
        top.setBackground(new Color(0xF8F7FF));
        top.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0xCCCCCC), 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));

        // Search field
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.setPreferredSize(new Dimension(250, 36));
        searchPanel.setBackground(Color.WHITE);
        searchPanel.setBorder(BorderFactory.createLineBorder(new Color(0xBBBBBB), 1));

        JTextField searchField = new JTextField();
        searchField.setFont(new Font("Arial", Font.PLAIN, 13));
        searchField.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 4));
        searchPanel.add(searchField, BorderLayout.CENTER);

        JButton searchBtn = new JButton("🔍");
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

        // ComboBox for search type
        JComboBox<String> searchType = new JComboBox<>(new String[]{"Tất cả", "Mã NCC", "Tên NCC", "Địa chỉ", "Người liên lạc", "SĐT", "Email"});
        searchType.setFont(new Font("Arial", Font.PLAIN, 13));
        searchType.setPreferredSize(new Dimension(120, 36));

        // Add supplier button
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

        top.add(searchPanel);
        top.add(searchType);
        top.add(addBtn);

        // Table
        model = new DefaultTableModel(new Object[]{"Mã NCC", "Tên NCC", "Địa chỉ", "Người liên lạc", "SĐT", "Email", "Thao tác"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 6; // Only action column editable
            }
        };
        table = new JTable(model);
        sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);
        // default sort by supplier name (column index 1)
        sorter.setSortKeys(java.util.Collections.singletonList(new javax.swing.RowSorter.SortKey(1, javax.swing.SortOrder.ASCENDING)));
        table.setRowHeight(50); // make rows taller
        table.setFont(new Font("Arial", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(0xE8E6F5));
        table.setGridColor(new Color(0xE8E6F5));
        table.setSelectionBackground(new Color(0xC5B3E6));

        // Center align for some columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer); // Mã NCC

        // Action column renderer and editor
        table.getColumnModel().getColumn(6).setCellRenderer(new ActionRenderer());
        table.getColumnModel().getColumn(6).setCellEditor(new ActionEditor());

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(0xCCCCCC), 1));

        // Apply search filter (unchanged)
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

        add(header, BorderLayout.NORTH);
        add(top, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER); // let table fill remaining space

        loadSuppliers();
    }

    private void loadSuppliers() {
        try {
            List<SupplierDTO> suppliers = supplierBUS.getAllSuppliers();
            model.setRowCount(0);
            for (SupplierDTO s : suppliers) {
                // display supplier code instead of internal ID
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
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), supplier == null ? "Thêm nhà cung cấp" : "Sửa nhà cung cấp", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        JTextField txtCode = new JTextField(15);
        txtCode.setEditable(false);
        JTextField txtName = new JTextField(20);
        JTextField txtAddress = new JTextField(20);
        JTextField txtContact = new JTextField(20);
        JTextField txtPhone = new JTextField(20);
        JTextField txtEmail = new JTextField(20);

        if (supplier != null) {
            txtCode.setText(supplier.getCode());
            txtName.setText(supplier.getName());
            txtAddress.setText(supplier.getAddress());
            txtContact.setText(supplier.getContactPerson());
            txtPhone.setText(supplier.getPhone());
            txtEmail.setText(supplier.getEmail());
        } else {
            // generate code for new supplier
            txtCode.setText(supplierBUS.generateSupplierCode());
        }

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Mã NCC:"), gbc);
        gbc.gridx = 1;
        panel.add(txtCode, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Tên nhà cung cấp:"), gbc);
        gbc.gridx = 1;
        panel.add(txtName, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Địa chỉ:"), gbc);
        gbc.gridx = 1;
        panel.add(txtAddress, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Người liên lạc:"), gbc);
        gbc.gridx = 1;
        panel.add(txtContact, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(new JLabel("SĐT:"), gbc);
        gbc.gridx = 1;
        panel.add(txtPhone, gbc);

        gbc.gridx = 0; gbc.gridy = 5;
        panel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        panel.add(txtEmail, gbc);

        JButton saveBtn = new JButton("Lưu");
        saveBtn.addActionListener(e -> {
            try {
                // validate
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

                if (supplier == null) {
                    supplierBUS.addSupplier(s);
                } else {
                    supplierBUS.updateSupplier(s);
                }
                loadSuppliers();
                dialog.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Lỗi: " + ex.getMessage());
            }
        });

        JButton cancelBtn = new JButton("Hủy");
        cancelBtn.addActionListener(e -> dialog.dispose());

        JPanel btnPanel = new JPanel();
        btnPanel.add(saveBtn);
        if (supplier != null) {
            JButton deleteBtn = new JButton("Xóa");
            deleteBtn.addActionListener(e -> {
                int opt = JOptionPane.showConfirmDialog(dialog, "Bạn có chắc muốn xóa nhà cung cấp này?", "Xác nhận", JOptionPane.YES_NO_OPTION);
                if (opt == JOptionPane.YES_OPTION) {
                    supplierBUS.deleteSupplier(supplier.getID());
                    loadSuppliers();
                    dialog.dispose();
                }
            });
            btnPanel.add(deleteBtn);
        }
        btnPanel.add(cancelBtn);

        dialog.add(panel, BorderLayout.CENTER);
        dialog.add(btnPanel, BorderLayout.SOUTH);
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

    // Action renderer and editor for table
    private class ActionRenderer extends JPanel implements TableCellRenderer {
        private JButton editBtn = new JButton("Sửa");
        private JButton deleteBtn = new JButton("Xóa");

        public ActionRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));
            editBtn.setFont(new Font("Arial", Font.PLAIN, 12));
            deleteBtn.setFont(new Font("Arial", Font.PLAIN, 12));
            add(editBtn);
            add(deleteBtn);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            return this;
        }
    }

    private class ActionEditor extends AbstractCellEditor implements TableCellEditor {
        private JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        private JButton editBtn = new JButton("Sửa");
        private JButton deleteBtn = new JButton("Xóa");
        private int currentRow;

        public ActionEditor() {
            editBtn.setFont(new Font("Arial", Font.PLAIN, 12));
            deleteBtn.setFont(new Font("Arial", Font.PLAIN, 12));
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
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            currentRow = table.convertRowIndexToModel(row);
            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            return "";
        }
    }
}