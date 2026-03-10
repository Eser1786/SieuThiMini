package GUI.SanPham;

import BUS.CategoryBUS;
import DTO.CategoryDTO;
import GUI.UIUtils;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

/**
 * Panel for category management within product management.
 */
public class CategoryPanel extends JPanel {
    private DefaultTableModel model;
    private JTable table;
    private TableRowSorter<DefaultTableModel> sorter;

    public CategoryPanel() {
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

        JLabel title = new JLabel("QUẢN LÝ DANH MỤC");
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

        // Add category button
        JButton addBtn = new JButton("+ Thêm danh mục");
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
        addBtn.addActionListener(e -> showAddCategoryDialog());

        // Apply search filter
        Runnable applyFilter = () -> {
            String keyword = searchField.getText().trim();
            if (keyword.isEmpty()) {
                sorter.setRowFilter(null);
            } else {
                sorter.setRowFilter(RowFilter.regexFilter("(?i)" + keyword, 2)); // Search in name column (now index 2)
            }
        };

        searchBtn.addActionListener(e -> applyFilter.run());
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { applyFilter.run(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { applyFilter.run(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { applyFilter.run(); }
        });

        top.add(new JLabel("Tìm kiếm:"));
        top.add(searchPanel);
        top.add(addBtn);

        // Table setup
        String[] columns = {"STT", "ID", "Tên danh mục", "Mô tả", "Thao tác"};
        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return col == 4; // Only action column is editable
            }
        };

        table = new JTable(model);
        sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);

        table.setRowHeight(40);
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(0xAF9FCB));
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setReorderingAllowed(false);
        table.setShowVerticalLines(false);
        table.setGridColor(new Color(0xEEEEEE));
        table.setIntercellSpacing(new Dimension(0, 1));

        // Hide ID column (now index 1)
        table.getColumnModel().getColumn(1).setMinWidth(0);
        table.getColumnModel().getColumn(1).setMaxWidth(0);
        table.getColumnModel().getColumn(1).setWidth(0);

        // Set column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(50);   // STT
        table.getColumnModel().getColumn(2).setPreferredWidth(200);  // Name
        table.getColumnModel().getColumn(3).setPreferredWidth(300);  // Description
        table.getColumnModel().getColumn(4).setPreferredWidth(120);  // Action

        // Custom renderer for alternating rows
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(0xF3F0FA));
                }
                setHorizontalAlignment(column == 3 ? SwingConstants.CENTER : SwingConstants.LEFT);
                return c;
            }
        };

        for (int i = 1; i < 4; i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }

        // Action column renderer (always visible)
        table.getColumnModel().getColumn(4).setCellRenderer((t, val, sel, foc, r, c) -> {
            JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 8));
            panel.setOpaque(true);
            JButton editBtn = UIUtils.makeActionButton("Sửa", new Color(0x6677C8));
            editBtn.setPreferredSize(new Dimension(80, 28));
            panel.add(editBtn);
            panel.setBackground(r % 2 == 0 ? Color.WHITE : new Color(0xF3F0FA));
            int viewRow = r;
            editBtn.addActionListener(e -> {
                int modelRow = table.convertRowIndexToModel(viewRow);
                editCategory(modelRow);
            });
            return panel;
        });

        // Action column editor (for keyboard navigation)
        table.getColumnModel().getColumn(4).setCellEditor(new DefaultCellEditor(new JCheckBox()) {
            private final JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 8));
            private final JButton editBtn = UIUtils.makeActionButton("Sửa", new Color(0x6677C8));
            private int currentRow = -1;

            {
                editBtn.setPreferredSize(new Dimension(80, 28));
                panel.setOpaque(true);
                panel.add(editBtn);
                editBtn.addActionListener(e -> {
                    fireEditingStopped();
                    int modelRow = table.convertRowIndexToModel(currentRow);
                    editCategory(modelRow);
                });
            }

            @Override
            public Component getTableCellEditorComponent(JTable table, Object value,
                    boolean isSelected, int row, int column) {
                currentRow = row;
                panel.setBackground(row % 2 == 0 ? Color.WHITE : new Color(0xF3F0FA));
                return panel;
            }

            @Override
            public Object getCellEditorValue() {
                return "";
            }
        });
        JScrollPane scrollPane = new JScrollPane(table);
        UIUtils.styleScrollPane(scrollPane);

        // Layout
        JPanel northPanel = new JPanel();
        northPanel.setLayout(new BoxLayout(northPanel, BoxLayout.Y_AXIS));
        northPanel.add(header);
        northPanel.add(top);

        add(northPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // Load data
        loadCategories();
    }

    private void loadCategories() {
        model.setRowCount(0);
        try {
            CategoryBUS categoryBUS = new CategoryBUS();
            List<CategoryDTO> categories = categoryBUS.getAllCategories();

            int idx = 1;
        for (CategoryDTO category : categories) {
                model.addRow(new Object[]{
                    idx++,
                    category.getID(),
                    category.getName(),
                    category.getDescription() != null ? category.getDescription() : "",
                    "" // Action column
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi tải danh sách danh mục: " + e.getMessage());
        }
    }

    private void showAddCategoryDialog() {
        showCategoryDialog(null);
    }

    private void editCategory(int modelRow) {
        int id = (Integer) model.getValueAt(modelRow, 1);
        String name = (String) model.getValueAt(modelRow, 2);
        String description = (String) model.getValueAt(modelRow, 3);

        CategoryDTO category = new CategoryDTO();
        category.setID(id);
        category.setName(name);
        category.setDescription(description);

        showCategoryDialog(category);
    }

    private void showCategoryDialog(CategoryDTO category) {
        Window owner = SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog(owner, category == null ? "Thêm danh mục" : "Sửa danh mục",
                Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setResizable(false);
        dialog.setLayout(new BorderLayout());

        // Header
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 14));
        header.setBackground(new Color(0xAF9FCB));
        JLabel titleLabel = new JLabel(category == null ? "Thêm danh mục mới" : "Sửa danh mục");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        header.add(titleLabel);

        // Form
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(new Color(0xF0EFF8));
        form.setBorder(BorderFactory.createEmptyBorder(18, 28, 18, 28));
        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.HORIZONTAL;
        g.insets = new Insets(7, 6, 7, 6);
        Font labelFont = new Font("Arial", Font.BOLD, 13);
        Dimension fieldSize = new Dimension(250, 32);

        JTextField nameField = UIUtils.makeField();
        nameField.setPreferredSize(fieldSize);
        if (category != null) {
            nameField.setText(category.getName());
        }

        JTextField descField = UIUtils.makeField();
        descField.setPreferredSize(fieldSize);
        if (category != null) {
            descField.setText(category.getDescription());
        }

        g.gridy = 0;
        g.gridx = 0; g.weightx = 0;
        JLabel nameLabel = new JLabel("Tên danh mục:");
        nameLabel.setFont(labelFont);
        form.add(nameLabel, g);

        g.gridx = 1; g.weightx = 1;
        form.add(nameField, g);

        g.gridy = 1;
        g.gridx = 0; g.weightx = 0;
        JLabel descLabel = new JLabel("Mô tả:");
        descLabel.setFont(labelFont);
        form.add(descLabel, g);

        g.gridx = 1; g.weightx = 1;
        form.add(descField, g);

        // Footer
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 12));
        footer.setBackground(new Color(0xF0EFF8));
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(0xCCCCCC)));

        JButton saveBtn = new JButton("Lưu");
        saveBtn.setFont(new Font("Arial", Font.BOLD, 13));
        saveBtn.setBackground(new Color(0x5C4A7F));
        saveBtn.setForeground(Color.WHITE);
        saveBtn.setBorder(BorderFactory.createEmptyBorder(9, 22, 9, 22));
        saveBtn.setOpaque(true);
        saveBtn.setBorderPainted(false);
        saveBtn.setFocusPainted(false);
        saveBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JButton cancelBtn = new JButton("Hủy");
        cancelBtn.setFont(new Font("Arial", Font.BOLD, 13));
        cancelBtn.setBackground(new Color(0x9B8EA8));
        cancelBtn.setForeground(Color.WHITE);
        cancelBtn.setBorder(BorderFactory.createEmptyBorder(9, 22, 9, 22));
        cancelBtn.setOpaque(true);
        cancelBtn.setBorderPainted(false);
        cancelBtn.setFocusPainted(false);
        cancelBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        cancelBtn.addActionListener(e -> dialog.dispose());

        saveBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            String description = descField.getText().trim();

            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Tên danh mục không được để trống.",
                        "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
                nameField.requestFocus();
                return;
            }

            try {
                CategoryBUS categoryBUS = new CategoryBUS();
                CategoryDTO newCategory = new CategoryDTO();
                newCategory.setName(name);
                newCategory.setDescription(description);

                boolean success;
                if (category == null) {
                    // Add new category
                    success = categoryBUS.addCategory(newCategory);
                } else {
                    // For update, we would need an update method in BUS/DAO
                    // For now, just show message that update is not implemented
                    JOptionPane.showMessageDialog(dialog, "Chức năng sửa danh mục chưa được triển khai.",
                            "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }

                if (success) {
                    JOptionPane.showMessageDialog(dialog, "Lưu danh mục thành công!",
                            "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    loadCategories(); // Refresh table
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Lưu danh mục thất bại!",
                            "Lỗi", JOptionPane.ERROR_MESSAGE);
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(dialog, "Lỗi: " + ex.getMessage(),
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        footer.add(saveBtn);
        footer.add(cancelBtn);

        dialog.add(header, BorderLayout.NORTH);
        dialog.add(form, BorderLayout.CENTER);
        dialog.add(footer, BorderLayout.SOUTH);

        dialog.pack();
        dialog.setMinimumSize(new Dimension(400, dialog.getPreferredSize().height));
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
}