package GUI.SanPham;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableRowSorter;

import BUS.ProductBUS;
import DTO.ProductDTO;
import GUI.ExportUtils;
import GUI.UIUtils;

import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

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
    DefaultTableModel model;

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
        model = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int col) { return col == 8; }
        };
        loadProducts();

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

        // ── TOP PANEL: filters LEFT, buttons RIGHT (single row) ──────────
        JPanel top = new JPanel(new GUI.WrapLayout(FlowLayout.LEFT, 8, 4));
        top.setBackground(new Color(0xF8F7FF));
        top.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0xCCCCCC), 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));

        // Left: filter combo + search
        String[] boloc = { "Tất cả", "Còn hàng", "Hết hàng", "Có khuyến mãi", "Cận date" };
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

        JLabel lbLoc = new JLabel("Tr\u1ea1ng th\u00e1i:");
        lbLoc.setFont(new Font("Arial", Font.PLAIN, 13));
        JLabel lbTim = new JLabel("T\u00ecm ki\u1ebfm:");
        lbTim.setFont(new Font("Arial", Font.PLAIN, 13));

        JPanel pLoc = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0)); pLoc.setOpaque(false); pLoc.add(lbLoc); pLoc.add(cbLoc);
        JPanel pTim = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0)); pTim.setOpaque(false); pTim.add(lbTim); pTim.add(timkiem);

        // Right: Thêm + export/import buttons
        JButton them = new JButton("+ Thêm sản phẩm");
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
        them.addActionListener(e -> SanPhamAddDialog.show(SanPhamPanel.this, model));

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

        JButton btnPDF    = ExportUtils.makeExportButton("Xuất PDF",   new Color(0x7B52AB));
        JButton btnExcel  = ExportUtils.makeExportButton("Xuất Excel", new Color(0x2E7D32));
        JButton btnImport = ExportUtils.makeImportButton("Nhập CSV");
        for (JButton b : new JButton[]{btnPDF, btnExcel, btnImport})
            b.setFont(new Font("Arial", Font.BOLD, 13));
        btnPDF.addActionListener(e -> ExportUtils.xuatPDF(this, model, "Danh sách sản phẩm"));
        btnExcel.addActionListener(e -> ExportUtils.xuatCSV(this, model, "san_pham"));
        btnImport.addActionListener(e -> {
            List<String[]> rows = ExportUtils.importCSV(this);
            if (rows == null) return;
            for (String[] r : rows) {
                if (r.length < 9) continue;
                model.addRow(new Object[]{r[0],r[1],r[2],r[3],r[4],r[5],r[6],r[7],r[8]});
            }
        });

        top.add(pLoc);
        top.add(pTim);
        top.add(them);
        top.add(btnPDF);
        top.add(btnExcel);
        top.add(btnImport);

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
                    SanPhamDetailDialog.showDetail(SanPhamPanel.this, modelRow, model, bang);
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

        JScrollPane bangScroll = new JScrollPane(bang);
        UIUtils.styleScrollPane(bangScroll);
        content.add(bangScroll, BorderLayout.CENTER);

        // Header
        JPanel spHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 12));
        spHeader.setBackground(new Color(0xF8F7FF));
        spHeader.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0xDDDDDD)),
            BorderFactory.createEmptyBorder(0, 20, 0, 20)));
        JPanel spBar = new JPanel();
        spBar.setPreferredSize(new Dimension(5, 26));
        spBar.setBackground(new Color(0x5C4A7F));
        spHeader.add(spBar);
        spHeader.add(Box.createHorizontalStrut(12));
        JLabel spTitle = new JLabel("QUẢN LÝ SẢN PHẨM");
        spTitle.setFont(new Font("Arial", Font.BOLD, 20));
        spHeader.add(spTitle);

        JPanel spNorth = new JPanel();
        spNorth.setLayout(new BoxLayout(spNorth, BoxLayout.Y_AXIS));
        spNorth.add(spHeader);
        spNorth.add(top);
        tableCard.add(spNorth,  BorderLayout.NORTH);
        tableCard.add(content,  BorderLayout.CENTER);

        add(tableCard, CARD_TABLE);
        innerCard.show(this, CARD_TABLE);
    }

    // Dialog methods moved to SanPhamAddDialog and SanPhamDetailDialog

    private void loadProducts() {
        model.setRowCount(0);
        try {
            ArrayList<ProductDTO> list = new ProductBUS().getAllProducts();
            if (list == null) return;
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            for (ProductDTO p : list) {
                String giaBan  = p.getSellingPrice() != null ? String.format("%,.0fđ", p.getSellingPrice()) : "-";
                long qty = p.getTotalQuantity();
                String kho = qty == 0 ? "Hết hàng" : (qty < p.getMinStockLevel() ? "Gần hết" : "Còn hàng");
                String ngayHH  = p.getExpireDate() != null ? p.getExpireDate().format(fmt) : "-";
                String ncc     = p.getSupplier()  != null ? p.getSupplier().getName()  : "-";
                String dm      = p.getCategory()  != null ? p.getCategory().getName()  : "-";
                String giaVon  = p.getCostPrice() != null ? String.format("%,.0fđ", p.getCostPrice()) : "-";
                String ngaySX  = p.getProductionDate() != null ? p.getProductionDate().format(fmt) : "-";
                model.addRow(new Object[]{
                    p.getCode(),
                    p.getImagePath() != null ? p.getImagePath() : "",
                    p.getName(), giaBan, qty, kho, ngayHH, "-", "",
                    p.getDescription() != null ? p.getDescription() : "-",
                    ncc, dm, giaVon,
                    p.getMinStockLevel(),
                    p.getMadeIn() != null ? p.getMadeIn() : "-",
                    ngaySX,
                    p.getPosition() != null ? p.getPosition() : "-",
                    p.getUnit()   != null ? p.getUnit()   : "-",
                    p.getStatus() != null ? p.getStatus() : "-"
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

