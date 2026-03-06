package GUI.NhanVien;

import BUS.EmployeeBUS;
import BUS.RoleBUS;
import DTO.EmployeeDTO;
import DTO.RoleDTO;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.Dialog;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NhanVienPanel extends JPanel {

    private static final Color PAGE_BG   = new Color(0xF8F7FF);
    private static final Color ACCENT    = new Color(0x5C4A7F);
    private static final Color CARD_LEFT = new Color(0xD1C4E9);
    private static final Color TBL_HDR   = new Color(0x3D2F5C);
    private static final Color BTN_GREEN = new Color(0x388E3C);

    private DefaultTableModel tableModel;
    private final EmployeeBUS empBUS = new EmployeeBUS();
    private List<RoleDTO> roles = new ArrayList<>();
    private final Map<Integer, String> roleMap = new HashMap<>();

    // Detail card labels
    private JLabel lbName, lbRole, lbMaNV, lbGioiTinh, lbCMND, lbNgaySinh,
                   lbNgayTG, lbEmail, lbSdt;
    private JPanel imgPlaceholder;

    public NhanVienPanel() {
        setLayout(new BorderLayout());
        setBackground(PAGE_BG);
        loadRoles();
        add(buildHeader(), BorderLayout.NORTH);
        JPanel body = new JPanel(new BorderLayout(0, 14));
        body.setBackground(PAGE_BG);
        body.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));
        body.add(buildDetailCard(), BorderLayout.NORTH);
        body.add(buildListSection(), BorderLayout.CENTER);
        add(body, BorderLayout.CENTER);
    }

    // ── Roles ────────────────────────────────────────────────────────────────
    private void loadRoles() {
        try {
            List<RoleDTO> r = new RoleBUS().getAllRoles();
            if (r != null) roles = r;
        } catch (Exception ignored) {}
        for (RoleDTO r : roles) roleMap.put(r.getId(), r.getName());
    }

    private String roleName(int id) {
        return roleMap.getOrDefault(id, "Nh\u00e2n vi\u00ean");
    }

    // ── Header ───────────────────────────────────────────────────────────────
    private JPanel buildHeader() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 12));
        p.setBackground(PAGE_BG);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0xDDDDDD)),
                BorderFactory.createEmptyBorder(0, 20, 0, 20)));
        JPanel bar = new JPanel();
        bar.setPreferredSize(new Dimension(5, 26));
        bar.setBackground(ACCENT);
        p.add(bar);
        p.add(Box.createHorizontalStrut(12));
        JLabel lbl = new JLabel("QU\u1ea2N L\u00dd NH\u00c2N VI\u00caN");
        lbl.setFont(new Font("Arial", Font.BOLD, 20));
        p.add(lbl);
        return p;
    }

    // ── Detail card ──────────────────────────────────────────────────────────
    private JPanel buildDetailCard() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(PAGE_BG);
        card.setBorder(BorderFactory.createLineBorder(new Color(0xBBBBBB)));

        // Title bar
        JPanel hdr = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 7));
        hdr.setBackground(new Color(0xEDE7F6));
        hdr.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, CARD_LEFT));
        JLabel h = new JLabel("Th\u00f4ng tin chi ti\u1ebft");
        h.setFont(new Font("Arial", Font.BOLD, 13));
        h.setForeground(new Color(0x3D2F5C));
        hdr.add(h);
        card.add(hdr, BorderLayout.NORTH);

        JPanel body = new JPanel(new GridLayout(1, 2));

        // Left – avatar + name + role
        JPanel left = new JPanel();
        left.setBackground(CARD_LEFT);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setBorder(BorderFactory.createEmptyBorder(20, 16, 20, 16));

        imgPlaceholder = new JPanel() {
            private final Color IC = ACCENT;
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int pw = getWidth(), ph = getHeight();
                double sc = Math.min((pw - 8) / 24.0, (ph - 8) / 24.0);
                double ox = (pw - 24 * sc) / 2.0, oy = (ph - 24 * sc) / 2.0;
                g2.translate(ox, oy); g2.scale(sc, sc);
                g2.setColor(IC); g2.setStroke(new BasicStroke(2f));
                // head circle
                g2.fill(new java.awt.geom.Ellipse2D.Double(8, 2, 8, 8));
                // body arc
                java.awt.geom.Path2D.Float body2 = new java.awt.geom.Path2D.Float();
                body2.moveTo(3, 22); body2.curveTo(3, 15, 21, 15, 21, 22);
                g2.fill(body2);
                g2.dispose();
            }
        };
        imgPlaceholder.setBackground(Color.WHITE);
        imgPlaceholder.setBorder(BorderFactory.createLineBorder(new Color(0x9575CD)));
        imgPlaceholder.setPreferredSize(new Dimension(80, 80));
        imgPlaceholder.setMaximumSize(new Dimension(90, 90));
        imgPlaceholder.setAlignmentX(Component.CENTER_ALIGNMENT);
        left.add(imgPlaceholder);
        left.add(Box.createVerticalStrut(10));

        lbName = new JLabel("T\u00caN NH\u00c2N VI\u00caN");
        lbName.setFont(new Font("Arial", Font.BOLD, 15));
        lbName.setForeground(new Color(0x2F2C35));
        lbName.setAlignmentX(Component.CENTER_ALIGNMENT);
        left.add(lbName);
        left.add(Box.createVerticalStrut(6));

        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(0x9575CD));
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sep.setAlignmentX(Component.CENTER_ALIGNMENT);
        left.add(sep);
        left.add(Box.createVerticalStrut(8));

        lbRole = new JLabel("CH\u1ee8C V\u1ee4");
        lbRole.setFont(new Font("Arial", Font.BOLD, 13));
        lbRole.setForeground(new Color(0x5C4A7F));
        lbRole.setAlignmentX(Component.CENTER_ALIGNMENT);
        left.add(lbRole);
        left.add(Box.createVerticalGlue());

        // Right – fields grid
        JPanel right = new JPanel(new GridBagLayout());
        right.setBackground(Color.WHITE);
        right.setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));
        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.HORIZONTAL;
        g.insets = new Insets(5, 4, 5, 12);
        Font bf = new Font("Arial", Font.BOLD, 13);

        lbMaNV     = new JLabel(""); lbGioiTinh = new JLabel("");
        lbCMND     = new JLabel(""); lbNgayTG   = new JLabel("");
        lbNgaySinh = new JLabel(""); lbEmail    = new JLabel("");
        lbSdt      = new JLabel("");

        String[][] rows2 = {
            {"M\u00e3 nh\u00e2n vi\u00ean:", null, "Email:", null},
            {"Gi\u1edbi t\u00ednh:", null, "S\u0110T:", null},
            {"CMND:", null, "Ng\u00e0y tham gia:", null},
            {"Ng\u00e0y sinh:", null, "L\u01b0\u01a1ng:", null}
        };
        JLabel[][] valLabels = {
            {lbMaNV, lbEmail},
            {lbGioiTinh, lbSdt},
            {lbCMND, lbNgayTG},
            {lbNgaySinh, null}
        };
        // salary label
        JLabel lbSalary = new JLabel("");
        valLabels[3][1] = lbSalary;

        for (int row = 0; row < rows2.length; row++) {
            // col A label
            g.gridx = 0; g.gridy = row; g.weightx = 0.25;
            JLabel la = new JLabel(rows2[row][0]); la.setFont(bf);
            right.add(la, g);
            // col A value
            g.gridx = 1; g.weightx = 0.25;
            valLabels[row][0].setFont(new Font("Arial", Font.PLAIN, 13));
            right.add(valLabels[row][0], g);
            // col B label
            g.gridx = 2; g.weightx = 0.25;
            JLabel lb = new JLabel(rows2[row][2]); lb.setFont(bf);
            right.add(lb, g);
            // col B value
            g.gridx = 3; g.weightx = 0.25;
            valLabels[row][1].setFont(new Font("Arial", Font.PLAIN, 13));
            right.add(valLabels[row][1], g);
        }
        // filler row
        g.gridx = 0; g.gridy = rows2.length; g.gridwidth = 4;
        g.weighty = 1.0; g.fill = GridBagConstraints.BOTH;
        right.add(new JLabel(), g);

        body.add(left); body.add(right);
        card.add(body, BorderLayout.CENTER);
        return card;
    }

    // ── List section ─────────────────────────────────────────────────────────
    private JPanel buildListSection() {
        JPanel section = new JPanel(new BorderLayout(0, 8));
        section.setBackground(PAGE_BG);

        // Toolbar
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        toolbar.setBackground(PAGE_BG);

        JLabel lbTitle = new JLabel("Danh s\u00e1ch nh\u00e2n vi\u00ean");
        lbTitle.setFont(new Font("Arial", Font.BOLD | Font.ITALIC, 16));
        lbTitle.setForeground(ACCENT);

        JButton btnThem = makeBtn("+ TH\u00caM NH\u00c2N VI\u00caN", new Color(0x388E3C));
        JButton btnRefresh = makeBtn("Refresh", BTN_GREEN);

        String[] searchFields = {"M\u00e3 NV", "T\u00ean nh\u00e2n vi\u00ean", "Chuy\u00ea v\u1ee5", "S\u0110T"};
        JComboBox<String> cbField = new JComboBox<>(searchFields);
        cbField.setFont(new Font("Arial", Font.PLAIN, 13));
        cbField.setPreferredSize(new Dimension(155, 32));

        JTextField tfSearch = new JTextField(16);
        tfSearch.setFont(new Font("Arial", Font.PLAIN, 13));

        JButton btnSearch = makeBtn("SEARCH", TBL_HDR);

        toolbar.add(lbTitle);
        toolbar.add(Box.createHorizontalStrut(8));
        toolbar.add(btnThem);
        toolbar.add(btnRefresh);
        toolbar.add(Box.createHorizontalStrut(16));
        toolbar.add(cbField);
        toolbar.add(tfSearch);
        toolbar.add(btnSearch);

        // Table
        String[] cols = {"M\u00e3 NV", "T\u00ean nh\u00e2n vi\u00ean", "Ch\u1ee9c v\u1ee5",
                         "S\u0110T", "Email", "Ng\u00e0y tham gia"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        loadEmployees();

        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        JTable table = new JTable(tableModel);
        table.setRowSorter(sorter);
        table.setRowHeight(36);
        table.setFont(new Font("Arial", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        table.getTableHeader().setBackground(TBL_HDR);
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setReorderingAllowed(false);
        table.setShowVerticalLines(false);
        table.setGridColor(new Color(0xEEEEEE));
        table.setSelectionBackground(new Color(0xC5B3E6));
        table.setSelectionForeground(Color.BLACK);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        int[] prefW = {80, 160, 130, 110, 180, 130};
        for (int i = 0; i < prefW.length; i++)
            table.getColumnModel().getColumn(i).setPreferredWidth(prefW[i]);

        DefaultTableCellRenderer altR = new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(
                    JTable t, Object v, boolean sel, boolean foc, int r, int c) {
                super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                if (!sel) setBackground(r % 2 == 0 ? Color.WHITE : new Color(0xF3F0FA));
                return this;
            }
        };
        for (int i = 0; i < cols.length; i++)
            table.getColumnModel().getColumn(i).setCellRenderer(altR);

        // Row click → fill detail
        table.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;
            int row = table.getSelectedRow();
            if (row < 0) return;
            int model = table.convertRowIndexToModel(row);
            fillDetail(model);
        });

        // Search
        Runnable applyFilter = () -> {
            String kw = tfSearch.getText().trim();
            int col = cbField.getSelectedIndex();
            if (kw.isEmpty()) { sorter.setRowFilter(null); return; }
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + kw, col));
        };
        btnSearch.addActionListener(e -> applyFilter.run());
        tfSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { applyFilter.run(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { applyFilter.run(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { applyFilter.run(); }
        });

        btnRefresh.addActionListener(e -> { loadEmployees(); sorter.setRowFilter(null); tfSearch.setText(""); });
        btnThem.addActionListener(e -> showAddDialog());

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(0xCCCCCC)));
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        section.add(toolbar, BorderLayout.NORTH);
        section.add(scroll, BorderLayout.CENTER);
        return section;
    }

    // ── Data loading ─────────────────────────────────────────────────────────
    private void loadEmployees() {
        tableModel.setRowCount(0);
        try {
            List<EmployeeDTO> list = empBUS.getAllEmployees();
            if (list == null) return;
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            for (EmployeeDTO e : list) {
                String ngay = e.getHireDate() != null ? e.getHireDate().format(fmt) : "";
                tableModel.addRow(new Object[]{
                    e.getCode(), e.getFullName(), roleName(e.getRoleId()),
                    e.getPhone(), e.getEmail(), ngay
                });
            }
        } catch (Exception ignored) {
            // seed mock if DB unavailable
            tableModel.addRow(new Object[]{"NV001", "Nguy\u1ec5n V\u0103n A", "Qu\u1ea3n l\u00fd", "0901234567", "nva@mail.com", "01/01/2024"});
            tableModel.addRow(new Object[]{"NV002", "Tr\u1ea7n Th\u1ecb B", "Nh\u00e2n vi\u00ean", "0907654321", "ttb@mail.com", "15/03/2024"});
        }
    }

    private void fillDetail(int modelRow) {
        lbName.setText(tableModel.getValueAt(modelRow, 1).toString());
        lbRole.setText(tableModel.getValueAt(modelRow, 2).toString());
        lbMaNV.setText(tableModel.getValueAt(modelRow, 0).toString());
        lbSdt.setText(tableModel.getValueAt(modelRow, 3).toString());
        lbEmail.setText(tableModel.getValueAt(modelRow, 4).toString());
        lbNgayTG.setText(tableModel.getValueAt(modelRow, 5).toString());
        imgPlaceholder.repaint();
    }

    // ── Add dialog ───────────────────────────────────────────────────────────
    private void showAddDialog() {
        JDialog dlg = new JDialog(SwingUtilities.getWindowAncestor(this),
                "Th\u00eam Nh\u00e2n Vi\u00ean", Dialog.ModalityType.APPLICATION_MODAL);
        dlg.setLayout(new BorderLayout());

        String[] lbls = {
            "M\u00e3 nh\u00e2n vi\u00ean:", "H\u1ecd v\u00e0 t\u00ean:",
            "T\u00ean \u0111\u0103ng nh\u1eadp:", "S\u0110T:", "Email:",
            "Ng\u00e0y tham gia (dd/MM/yyyy):", "L\u01b0\u01a1ng:"
        };
        JTextField[] tfs = new JTextField[lbls.length];
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(16, 20, 8, 20));
        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.HORIZONTAL; g.insets = new Insets(5, 5, 5, 5);
        for (int i = 0; i < lbls.length; i++) {
            g.gridx = 0; g.gridy = i; g.weightx = 0.4;
            JLabel l = new JLabel(lbls[i]); l.setFont(new Font("Arial", Font.BOLD, 13));
            form.add(l, g);
            g.gridx = 1; g.weightx = 0.6;
            tfs[i] = new JTextField(18); tfs[i].setFont(new Font("Arial", Font.PLAIN, 13));
            form.add(tfs[i], g);
        }
        // Chức vụ dropdown
        g.gridx = 0; g.gridy = lbls.length; g.weightx = 0.4;
        JLabel lRole = new JLabel("Ch\u1ee9c v\u1ee5:"); lRole.setFont(new Font("Arial", Font.BOLD, 13));
        form.add(lRole, g);
        String[] roleNames = roles.isEmpty()
            ? new String[]{"Nh\u00e2n vi\u00ean", "Qu\u1ea3n l\u00fd"}
            : roles.stream().map(RoleDTO::getName).toArray(String[]::new);
        JComboBox<String> cbRole = new JComboBox<>(roleNames);
        cbRole.setFont(new Font("Arial", Font.PLAIN, 13));
        g.gridx = 1; g.weightx = 0.6; form.add(cbRole, g);

        dlg.add(form, BorderLayout.CENTER);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
        JButton btnLuu = new JButton("L\u01b0u");
        btnLuu.setBackground(ACCENT); btnLuu.setForeground(Color.WHITE);
        btnLuu.setFocusPainted(false);
        JButton btnHuy = new JButton("H\u1ee7y"); btnHuy.setFocusPainted(false);
        btns.add(btnHuy); btns.add(btnLuu);
        dlg.add(btns, BorderLayout.SOUTH);

        btnLuu.addActionListener(e -> {
            List<String> errs = new ArrayList<>();
            if (tfs[0].getText().trim().isEmpty()) errs.add("- Vui l\u00f2ng nh\u1eadp m\u00e3 nh\u00e2n vi\u00ean.");
            if (tfs[1].getText().trim().isEmpty()) errs.add("- Vui l\u00f2ng nh\u1eadp h\u1ecd v\u00e0 t\u00ean.");
            if (tfs[2].getText().trim().isEmpty()) errs.add("- Vui l\u00f2ng nh\u1eadp t\u00ean \u0111\u0103ng nh\u1eadp.");
            if (!errs.isEmpty()) {
                JOptionPane.showMessageDialog(dlg, String.join("\n", errs),
                        "Thi\u1ebfu th\u00f4ng tin", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String ngay = tfs[5].getText().trim();
            if (!ngay.isEmpty()) {
                try { new java.text.SimpleDateFormat("dd/MM/yyyy").parse(ngay); }
                catch (Exception ex) {
                    JOptionPane.showMessageDialog(dlg,
                        "Ng\u00e0y ph\u1ea3i \u0111\u00fang \u0111\u1ecbnh d\u1ea1ng dd/MM/yyyy.",
                        "Ng\u00e0y kh\u00f4ng h\u1ee3p l\u1ec7", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }
            String roleSel = cbRole.getSelectedItem().toString();
            tableModel.addRow(new Object[]{
                tfs[0].getText().trim(), tfs[1].getText().trim(), roleSel,
                tfs[3].getText().trim(), tfs[4].getText().trim(), ngay
            });
            dlg.dispose();
        });
        btnHuy.addActionListener(e -> dlg.dispose());
        dlg.pack();
        dlg.setMinimumSize(new Dimension(460, dlg.getPreferredSize().height));
        dlg.setLocationRelativeTo(this);
        dlg.setVisible(true);
    }

    // ── Helper ───────────────────────────────────────────────────────────────
    private JButton makeBtn(String text, Color bg) {
        JButton b = new JButton(text);
        b.setFont(new Font("Arial", Font.BOLD, 12));
        b.setBackground(bg); b.setForeground(Color.WHITE);
        b.setFocusPainted(false); b.setBorderPainted(false); b.setOpaque(true);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }
}

