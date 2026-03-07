package GUI.KhuyenMai;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import BUS.DiscountBUS;
import DTO.DiscountDTO;
import GUI.ExportUtils;
import GUI.UIUtils;

public class KhuyenMaiPanel extends JPanel {

    private static final Color PAGE_BG  = new Color(0xF8F7FF);
    private static final Color ACCENT   = new Color(0x5C4A7F);
    private static final Color TBL_HDR  = new Color(0xAF9FCB);

    private final DiscountBUS discountBUS = new DiscountBUS();
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter;

    public KhuyenMaiPanel() {
        setLayout(new BorderLayout());
        setBackground(PAGE_BG);

        // Table model
        String[] cols = { "M\u00e3", "T\u00ean khuy\u1ebfn m\u00e3i", "Gi\u00e1 tr\u1ecb", "Lo\u1ea1i gi\u1ea3m",
                "Ng\u00e0y b\u1eaft \u0111\u1ea7u", "Ng\u00e0y k\u1ebft th\u00fac", "Tr\u1ea1ng th\u00e1i", "Thao t\u00e1c" };
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return c == 7; }
        };
        sorter = new TableRowSorter<>(tableModel);

        // ── Header ──────────────────────────────────────────────────────────
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
        JLabel hdrTitle = new JLabel("QU\u1ea2N L\u00dd KHUY\u1ebeN M\u00c3I");
        hdrTitle.setFont(new Font("Arial", Font.BOLD, 20));
        header.add(hdrTitle);

        // ── Toolbar (single row: filters LEFT, buttons RIGHT) ────────────────
        JPanel toolbar = new JPanel(new BorderLayout());
        toolbar.setBackground(PAGE_BG);
        toolbar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xCCCCCC), 1),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));

        // Left: filter combo + search
        JPanel tbLeft = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));
        tbLeft.setBackground(PAGE_BG);

        String[] loaiGiam = { "T\u1ea5t c\u1ea3", "PERCENT", "FIXED" };
        JComboBox<String> cbLoc = new JComboBox<>(loaiGiam);
        cbLoc.setPreferredSize(new Dimension(200, 36));
        UIUtils.styleComboBox(cbLoc);

        JPanel timPanel = new JPanel(new BorderLayout());
        timPanel.setPreferredSize(new Dimension(220, 36));
        timPanel.setBackground(Color.WHITE);
        timPanel.setBorder(BorderFactory.createLineBorder(new Color(0xBBBBBB), 1));
        JTextField tfTim = new JTextField();
        tfTim.setFont(new Font("Arial", Font.PLAIN, 13));
        tfTim.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 4));
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

        JLabel lbLoc = new JLabel("Lo\u1ea1i gi\u1ea3m:");
        lbLoc.setFont(new Font("Arial", Font.PLAIN, 13));
        JLabel lbTim = new JLabel("T\u00ecm ki\u1ebfm:");
        lbTim.setFont(new Font("Arial", Font.PLAIN, 13));

        tbLeft.add(lbLoc);
        tbLeft.add(cbLoc);
        tbLeft.add(Box.createHorizontalStrut(6));
        tbLeft.add(lbTim);
        tbLeft.add(timPanel);

        // Right: Thêm + export buttons
        JPanel tbRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
        tbRight.setBackground(PAGE_BG);

        JButton btnThem = new JButton("+ Th\u00eam khuy\u1ebfn m\u00e3i");
        btnThem.setFont(new Font("Arial", Font.BOLD, 13));
        btnThem.setBackground(new Color(0xD9D9D9));
        btnThem.setBorder(BorderFactory.createEmptyBorder(9, 14, 9, 14));
        btnThem.setOpaque(true);
        btnThem.setBorderPainted(false);
        btnThem.setFocusPainted(false);
        btnThem.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnThem.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) { btnThem.setBackground(new Color(0xC5B3E6)); }
            public void mouseExited(java.awt.event.MouseEvent e)  { btnThem.setBackground(new Color(0xD9D9D9)); }
        });
        btnThem.addActionListener(e -> KhuyenMaiAddDialog.show(this, discountBUS, this::loadDiscountTables));

        JButton btnPDF   = ExportUtils.makeExportButton("Xu\u1ea5t PDF",   new Color(0x7B52AB));
        JButton btnExcel = ExportUtils.makeExportButton("Xu\u1ea5t Excel", new Color(0x2E7D32));
        JButton btnImport = ExportUtils.makeImportButton("Nh\u1eadp CSV");
        btnPDF.addActionListener(e -> ExportUtils.xuatPDF(this, tableModel, "Danh s\u00e1ch khuy\u1ebfn m\u00e3i"));
        btnExcel.addActionListener(e -> ExportUtils.xuatCSV(this, tableModel, "khuyen_mai"));
        btnImport.addActionListener(e -> {
            List<String[]> rows = ExportUtils.importCSV(this);
            if (rows == null) return;
            for (String[] r : rows) { if (r.length < 7) continue; tableModel.addRow((Object[]) r); }
        });

        tbRight.add(btnThem);
        tbRight.add(btnPDF);
        tbRight.add(btnExcel);
        tbRight.add(btnImport);

        toolbar.add(tbLeft, BorderLayout.WEST);
        toolbar.add(tbRight, BorderLayout.EAST);

        // ── North: header + toolbar ───────────────────────────────────────────
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

        bang.getColumnModel().getColumn(0).setPreferredWidth(60);
        bang.getColumnModel().getColumn(1).setPreferredWidth(220);
        bang.getColumnModel().getColumn(2).setPreferredWidth(90);
        bang.getColumnModel().getColumn(3).setPreferredWidth(100);
        bang.getColumnModel().getColumn(4).setPreferredWidth(130);
        bang.getColumnModel().getColumn(5).setPreferredWidth(130);
        bang.getColumnModel().getColumn(6).setPreferredWidth(100);
        bang.getColumnModel().getColumn(7).setPreferredWidth(120);

        DefaultTableCellRenderer altR = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable t, Object val, boolean sel, boolean foc, int r, int c) {
                super.getTableCellRendererComponent(t, val, sel, foc, r, c);
                if (!sel) {
                    setBackground(r % 2 == 0 ? Color.WHITE : new Color(0xF3F0FA));
                    setForeground(Color.BLACK);
                    if (c == 3) { // Lo\u1ea1i gi\u1ea3m
                        String v = val == null ? "" : val.toString();
                        setForeground("PERCENT".equals(v) ? new Color(0x1565C0) : new Color(0x6A1B9A));
                    }
                    if (c == 6) { // Tr\u1ea1ng th\u00e1i
                        String v = val == null ? "" : val.toString();
                        if (v.equalsIgnoreCase("active") || v.equalsIgnoreCase("Ho\u1ea1t \u0111\u1ed9ng"))
                            setForeground(new Color(0x2E7D32));
                        else
                            setForeground(new Color(0xB71C1C));
                    }
                }
                setHorizontalAlignment(SwingConstants.CENTER);
                return this;
            }
        };
        for (int i = 0; i < 7; i++)
            bang.getColumnModel().getColumn(i).setCellRenderer(altR);

        bang.getColumnModel().getColumn(7).setCellRenderer(
                (t, val, sel, foc, r, c) -> buildActionCell(t, r, false));
        bang.getColumnModel().getColumn(7).setCellEditor(new DefaultCellEditor(new JCheckBox()) {
            @Override
            public Component getTableCellEditorComponent(JTable t, Object val, boolean sel, int r, int c) {
                return buildActionCell(t, r, true);
            }
            @Override public Object getCellEditorValue() { return ""; }
        });

        // Filter logic
        Runnable applyFilter = () -> {
            String kw  = tfTim.getText().trim();
            int    idx = cbLoc.getSelectedIndex();
            RowFilter<DefaultTableModel, Integer> fLoc = idx == 0 ? null
                    : RowFilter.regexFilter("(?i)^" + loaiGiam[idx] + "$", 3);
            RowFilter<DefaultTableModel, Integer> fSr = kw.isEmpty() ? null
                    : RowFilter.orFilter(java.util.List.of(
                            RowFilter.regexFilter("(?i)" + kw, 0),
                            RowFilter.regexFilter("(?i)" + kw, 1)));
            if (fLoc != null && fSr != null) sorter.setRowFilter(RowFilter.andFilter(java.util.List.of(fLoc, fSr)));
            else if (fLoc != null) sorter.setRowFilter(fLoc);
            else if (fSr != null) sorter.setRowFilter(fSr);
            else sorter.setRowFilter(null);
        };
        cbLoc.addActionListener(e -> applyFilter.run());
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

    private JPanel buildActionCell(JTable t, int row, boolean isEditor) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 8));
        p.setBackground(row % 2 == 0 ? Color.WHITE : new Color(0xF3F0FA));
        JButton btn = UIUtils.makeActionButton("Chi ti\u1ebft", new Color(0x6677C8));
        if (isEditor) {
            btn.addActionListener(e -> {
                int modelRow = t.convertRowIndexToModel(row);
                KhuyenMaiDetailDialog.show(KhuyenMaiPanel.this, discountBUS, tableModel, modelRow);
                t.getCellEditor().stopCellEditing();
            });
        }
        p.add(btn);
        return p;
    }


    private void loadDiscountTables() {
        ArrayList<DiscountDTO> list = discountBUS.getAllDiscounts();
        tableModel.setRowCount(0);
        for (DiscountDTO d : list) {
            tableModel.addRow(new Object[]{
                d.getId(), d.getName(), d.getValue(),
                d.getDiscountType().name(),
                d.getStartDate(), d.getEndDate(),
                d.getStatus() != null ? d.getStatus().name() : "-", ""
            });
        }
    }
}
