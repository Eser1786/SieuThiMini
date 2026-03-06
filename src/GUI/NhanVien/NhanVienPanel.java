package GUI.NhanVien;

import BUS.EmployeeBUS;
import BUS.RoleBUS;
import DTO.EmployeeDTO;
import DTO.RoleDTO;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.Dialog;
import java.awt.event.*;
import java.awt.geom.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.regex.Pattern;

public class NhanVienPanel extends JPanel {

    private static final Color PAGE_BG   = new Color(0xF8F7FF);
    private static final Color ACCENT    = new Color(0x5C4A7F);
    private static final Color CARD_LEFT = new Color(0xD1C4E9);
    private static final Color TBL_HDR   = new Color(0xAF9FCB); // match KhachHang
    private static final Color BTN_IDLE  = new Color(0xD9D9D9);
    private static final Color BTN_HOVER = new Color(0xC5B3E6);

    private static final int COL_MA = 0, COL_TEN = 1, COL_CHUCVU = 2;
    private static final int COL_SDT = 3, COL_EMAIL = 4, COL_NGAY = 5, COL_PASS = 6;

    private DefaultTableModel tableModel;
    private final EmployeeBUS empBUS = new EmployeeBUS();
    private List<RoleDTO> roles = new ArrayList<>();
    private final Map<Integer, String> roleMap = new HashMap<>();
    private final Set<Integer> revealedRows = new HashSet<>();
    private JTable table;
    private TableRowSorter<DefaultTableModel> sorter;

    // Detail card labels
    private JLabel lbName, lbRole, lbMaNV, lbGioiTinh, lbCMND, lbNgaySinh,
                   lbNgayTG, lbEmail, lbSdt, lbSalary;

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
        // Hide passwords when the user switches away from this tab
        addHierarchyListener(e -> {
            if ((e.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) != 0 && !isShowing()) {
                revealedRows.clear();
                if (table != null) table.repaint();
            }
        });
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

        // userplaceholder.svg: head cx=12 cy=8 r=4, body M4 20c0-4 3.5-7 8-7s8 3 8 7v1H4v-1z
        JPanel imgBox = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int pw = getWidth(), ph = getHeight();
                double sc = Math.min((pw - 16) / 24.0, (ph - 16) / 24.0);
                double ox = (pw - 24 * sc) / 2.0, oy = (ph - 24 * sc) / 2.0;
                g2.translate(ox, oy);
                g2.scale(sc, sc);
                g2.setColor(ACCENT);
                // head: circle cx=12 cy=8 r=4
                g2.fill(new Ellipse2D.Double(8, 4, 8, 8));
                // body: M4 20c0-4 3.5-7 8-7s8 3 8 7v1H4v-1z
                Path2D bodyPath = new Path2D.Float();
                bodyPath.moveTo(4, 20);
                bodyPath.curveTo(4, 16, 7.5, 13, 12, 13);
                bodyPath.curveTo(16.5, 13, 20, 16, 20, 20);
                bodyPath.lineTo(20, 21);
                bodyPath.lineTo(4, 21);
                bodyPath.lineTo(4, 20);
                bodyPath.closePath();
                g2.fill(bodyPath);
                g2.dispose();
            }
        };
        imgBox.setBackground(Color.WHITE);
        imgBox.setBorder(BorderFactory.createLineBorder(new Color(0x9575CD)));
        imgBox.setPreferredSize(new Dimension(80, 80));
        imgBox.setMaximumSize(new Dimension(90, 90));
        imgBox.setAlignmentX(Component.CENTER_ALIGNMENT);
        left.add(imgBox);
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

        lbMaNV = new JLabel(""); lbGioiTinh = new JLabel("");
        lbCMND = new JLabel(""); lbNgayTG   = new JLabel("");
        lbNgaySinh = new JLabel(""); lbEmail  = new JLabel("");
        lbSdt  = new JLabel(""); lbSalary   = new JLabel("");

        Font vf = new Font("Arial", Font.PLAIN, 13);
        Object[][] fieldDef = {
            {"M\u00e3 nh\u00e2n vi\u00ean:", lbMaNV,     "Email:",             lbEmail},
            {"Gi\u1edbi t\u00ednh:",        lbGioiTinh, "S\u0110T:",           lbSdt},
            {"CMND:",                       lbCMND,     "Ng\u00e0y tham gia:", lbNgayTG},
            {"Ng\u00e0y sinh:",             lbNgaySinh, "L\u01b0\u01a1ng:",    lbSalary}
        };
        for (int row = 0; row < fieldDef.length; row++) {
            g.gridx = 0; g.gridy = row; g.weightx = 0.25;
            JLabel la = new JLabel((String) fieldDef[row][0]); la.setFont(bf);
            right.add(la, g);
            g.gridx = 1; g.weightx = 0.25;
            ((JLabel) fieldDef[row][1]).setFont(vf);
            right.add((JLabel) fieldDef[row][1], g);
            g.gridx = 2; g.weightx = 0.25;
            JLabel lb2 = new JLabel((String) fieldDef[row][2]); lb2.setFont(bf);
            right.add(lb2, g);
            g.gridx = 3; g.weightx = 0.25;
            ((JLabel) fieldDef[row][3]).setFont(vf);
            right.add((JLabel) fieldDef[row][3], g);
        }
        // filler
        g.gridx = 0; g.gridy = fieldDef.length; g.gridwidth = 4;
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

        // App-style buttons – gray bg, lavender on hover (matching KhachHang)
        JButton btnThem    = makeAppBtn("+ TH\u00caM NH\u00c2N VI\u00caN");
        JButton btnRefresh = makeAppBtn("\u21bb Refresh");

        // Dropdown chooses which column to search; label explains the control
        JLabel lbTimTheo = new JLabel("T\u00ecm theo:");
        lbTimTheo.setFont(new Font("Arial", Font.PLAIN, 13));
        String[] searchFields = {"M\u00e3 NV", "T\u00ean nh\u00e2n vi\u00ean", "Ch\u1ee9c v\u1ee5", "S\u0110T"};
        JComboBox<String> cbField = new JComboBox<>(searchFields);
        cbField.setFont(new Font("Arial", Font.PLAIN, 13));
        cbField.setPreferredSize(new Dimension(140, 32));

        JTextField tfSearch = new JTextField(16);
        tfSearch.setFont(new Font("Arial", Font.PLAIN, 13));

        JButton btnSearch = new JButton("SEARCH");
        btnSearch.setFont(new Font("Arial", Font.BOLD, 12));
        btnSearch.setBackground(new Color(0x3D2F5C));
        btnSearch.setForeground(Color.WHITE);
        btnSearch.setFocusPainted(false);
        btnSearch.setBorderPainted(false);
        btnSearch.setOpaque(true);
        btnSearch.setCursor(new Cursor(Cursor.HAND_CURSOR));

        toolbar.add(lbTitle);
        toolbar.add(Box.createHorizontalStrut(8));
        toolbar.add(btnThem);
        toolbar.add(btnRefresh);
        toolbar.add(Box.createHorizontalStrut(16));
        toolbar.add(lbTimTheo);
        toolbar.add(cbField);
        toolbar.add(tfSearch);
        toolbar.add(btnSearch);

        // Table - 7 columns including masked password

        String[] cols = {"M\u00e3 NV", "T\u00ean nh\u00e2n vi\u00ean", "Ch\u1ee9c v\u1ee5",

                         "S\u0110T", "Email", "Ng\u00e0y tham gia", "M\u1eadt kh\u1ea9u"};

        tableModel = new DefaultTableModel(cols, 0) {

            @Override public boolean isCellEditable(int r, int c) { return false; }

        };

        loadEmployees();



        sorter = new TableRowSorter<>(tableModel);

        table = new JTable(tableModel);

        table.setRowSorter(sorter);

        table.setRowHeight(36);

        table.setFont(new Font("Arial", Font.PLAIN, 13));

        // KhachHang-style header: font 16 bold, height 52, color 0xAF9FCB

        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 16));

        table.getTableHeader().setPreferredSize(new Dimension(0, 52));

        table.getTableHeader().setBackground(TBL_HDR);

        table.getTableHeader().setForeground(Color.WHITE);

        table.getTableHeader().setReorderingAllowed(false);

        table.setShowVerticalLines(false);

        table.setGridColor(new Color(0xEEEEEE));

        table.setSelectionBackground(new Color(0xC5B3E6));

        table.setSelectionForeground(Color.BLACK);

        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);



        int[] prefW = {75, 145, 110, 100, 155, 115, 120};

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

        for (int i = 0; i < COL_PASS; i++)

            table.getColumnModel().getColumn(i).setCellRenderer(altR);



        // Password column: masked circles + eye-icon toggle

        table.getColumnModel().getColumn(COL_PASS).setCellRenderer(new PasswordCellRenderer());



        // Click rightmost 28px of password cell to reveal/hide

        table.addMouseListener(new MouseAdapter() {

            @Override public void mouseClicked(MouseEvent e) {

                int col = table.columnAtPoint(e.getPoint());

                int viewRow = table.rowAtPoint(e.getPoint());

                if (col != COL_PASS || viewRow < 0) return;

                Rectangle cellRect = table.getCellRect(viewRow, col, false);

                if (e.getX() >= cellRect.x + cellRect.width - 28) {

                    int modelRow = table.convertRowIndexToModel(viewRow);

                    if (revealedRows.contains(modelRow)) revealedRows.remove(modelRow);

                    else revealedRows.add(modelRow);

                    table.repaint();

                }

            }

        });



        // Row click -> fill detail card

        table.getSelectionModel().addListSelectionListener(e -> {

            if (e.getValueIsAdjusting()) return;

            int row = table.getSelectedRow();

            if (row < 0) return;

            fillDetail(table.convertRowIndexToModel(row));

        });



        // Search - use Pattern.quote so user input is treated as literal

        Runnable applyFilter = () -> {

            String kw = tfSearch.getText().trim();

            int col = cbField.getSelectedIndex();

            if (kw.isEmpty()) { sorter.setRowFilter(null); return; }

            try { sorter.setRowFilter(RowFilter.regexFilter("(?i)" + Pattern.quote(kw), col)); }

            catch (Exception ignored) {}

        };

        btnSearch.addActionListener(e -> applyFilter.run());

        tfSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {

            public void insertUpdate(javax.swing.event.DocumentEvent e) { applyFilter.run(); }

            public void removeUpdate(javax.swing.event.DocumentEvent e) { applyFilter.run(); }

            public void changedUpdate(javax.swing.event.DocumentEvent e) { applyFilter.run(); }

        });



        btnRefresh.addActionListener(e -> {

            loadEmployees(); revealedRows.clear(); sorter.setRowFilter(null); tfSearch.setText("");

        });

        btnThem.addActionListener(e -> showAddDialog());

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(0xCCCCCC)));
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        section.add(toolbar, BorderLayout.NORTH);
        section.add(scroll, BorderLayout.CENTER);
        return section;
    }

    // ── Password cell renderer ──────────────────────────────────────────────
    private class PasswordCellRenderer extends DefaultTableCellRenderer {
        private boolean revealed = false;

        @Override public Component getTableCellRendererComponent(
                JTable t, Object v, boolean sel, boolean foc, int viewRow, int c) {
            super.getTableCellRendererComponent(t, v, sel, foc, viewRow, c);
            if (!sel) setBackground(viewRow % 2 == 0 ? Color.WHITE : new Color(0xF3F0FA));
            int modelRow = t.convertRowIndexToModel(viewRow);
            revealed = revealedRows.contains(modelRow);
            setText(revealed ? (v == null ? "" : v.toString()) : "\u25cf\u25cf\u25cf\u25cf\u25cf\u25cf");
            setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 30));
            return this;
        }

        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            // Draw eye icon (show.svg) on right side; slash overlay when hidden
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int sz = 16;
            int ix = getWidth() - sz - 6;
            int iy = (getHeight() - sz) / 2;
            g2.translate(ix, iy);
            g2.scale(sz / 24.0, sz / 24.0);
            g2.setColor(ACCENT);
            g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            // eye outline: M2 12s4-6 10-6 10 6 10 6-4 6-10 6S2 12 2 12z
            Path2D eye = new Path2D.Float();
            eye.moveTo(2, 12);
            eye.curveTo(2, 12, 6, 6, 12, 6);
            eye.curveTo(18, 6, 22, 12, 22, 12);
            eye.curveTo(22, 12, 18, 18, 12, 18);
            eye.curveTo(6, 18, 2, 12, 2, 12);
            eye.closePath();
            g2.draw(eye);
            // pupil: circle cx=12 cy=12 r=3
            g2.fill(new Ellipse2D.Double(9, 9, 6, 6));
            if (!revealed) {
                // slash to indicate hidden
                g2.setColor(new Color(0xAAAAAA));
                g2.setStroke(new BasicStroke(2.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawLine(4, 20, 20, 4);
            }
            g2.dispose();
        }
    }

    // ── Data loading ─────────────────────────────────────────────────────────
    private void loadEmployees() {
        tableModel.setRowCount(0);
        revealedRows.clear();
        try {
            List<EmployeeDTO> list = empBUS.getAllEmployees();
            if (list == null) return;
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            for (EmployeeDTO e : list) {
                String ngay = e.getHireDate() != null ? e.getHireDate().format(fmt) : "";
                tableModel.addRow(new Object[]{
                    e.getCode(), e.getFullName(), roleName(e.getRoleId()),
                    e.getPhone(), e.getEmail(), ngay,
                    e.getPasswordHash() != null ? e.getPasswordHash() : ""
                });
            }
        } catch (Exception ignored) {
            // Mock data when DB is unavailable
            tableModel.addRow(new Object[]{"NV001", "Nguy\u1ec5n V\u0103n A", "Qu\u1ea3n l\u00fd", "0901234567", "nva@mail.com", "01/01/2024", "pass123"});
            tableModel.addRow(new Object[]{"NV002", "Tr\u1ea7n Th\u1ecb B", "Nh\u00e2n vi\u00ean", "0907654321", "ttb@mail.com", "15/03/2024", "abc456"});
        }
    }

    private void fillDetail(int modelRow) {
        lbName.setText(tableModel.getValueAt(modelRow, COL_TEN).toString());
        lbRole.setText(tableModel.getValueAt(modelRow, COL_CHUCVU).toString());
        lbMaNV.setText(tableModel.getValueAt(modelRow, COL_MA).toString());
        lbSdt.setText(tableModel.getValueAt(modelRow, COL_SDT).toString());
        lbEmail.setText(tableModel.getValueAt(modelRow, COL_EMAIL).toString());
        lbNgayTG.setText(tableModel.getValueAt(modelRow, COL_NGAY).toString());
    }

    // -- Add dialog ----------------------------------------------------------

    private void showAddDialog() {

        JDialog dlg = new JDialog(SwingUtilities.getWindowAncestor(this),

                "Th\u00eam Nh\u00e2n Vi\u00ean", Dialog.ModalityType.APPLICATION_MODAL);

        dlg.setLayout(new BorderLayout());



        // [0]Ma NV [1]Ho ten [2]Username [3]SDT [4]Email [5]Ngay [6]Luong

        // Password handled by separate JPasswordField

        String[] fldLabels = {

            "M\u00e3 nh\u00e2n vi\u00ean *:", "H\u1ecd v\u00e0 t\u00ean *:",

            "T\u00ean \u0111\u0103ng nh\u1eadp *:",

            "S\u0110T (10 s\u1ed1):", "Email:",

            "Ng\u00e0y tham gia (dd/MM/yyyy):", "L\u01b0\u01a1ng (VN\u0110):"

        };

        JTextField[] tfs = new JTextField[fldLabels.length];

        JPasswordField pfPass = new JPasswordField(18);

        pfPass.setFont(new Font("Arial", Font.PLAIN, 13));



        JPanel form = new JPanel(new GridBagLayout());

        form.setBorder(BorderFactory.createEmptyBorder(16, 20, 8, 20));

        GridBagConstraints g = new GridBagConstraints();

        g.fill = GridBagConstraints.HORIZONTAL; g.insets = new Insets(5, 5, 5, 5);

        for (int i = 0; i < fldLabels.length; i++) {

            g.gridx = 0; g.gridy = i; g.weightx = 0.4;

            JLabel l = new JLabel(fldLabels[i]); l.setFont(new Font("Arial", Font.BOLD, 13));

            form.add(l, g);

            g.gridx = 1; g.weightx = 0.6;

            tfs[i] = new JTextField(18); tfs[i].setFont(new Font("Arial", Font.PLAIN, 13));

            form.add(tfs[i], g);

        }

        // Password row with show/hide toggle

        g.gridx = 0; g.gridy = fldLabels.length; g.weightx = 0.4;

        JLabel lPass = new JLabel("M\u1eadt kh\u1ea9u *:"); lPass.setFont(new Font("Arial", Font.BOLD, 13));

        form.add(lPass, g);

        JPanel passRow = new JPanel(new BorderLayout(4, 0));

        passRow.setOpaque(false);

        passRow.add(pfPass, BorderLayout.CENTER);

        JButton btnShowPass = new JButton("\uD83D\uDC41");

        btnShowPass.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 12));

        btnShowPass.setPreferredSize(new Dimension(36, 28));

        btnShowPass.setFocusPainted(false);

        btnShowPass.setToolTipText("Hi\u1ec7n/\u1ea8n m\u1eadt kh\u1ea9u");

        btnShowPass.addActionListener(ev -> {

            if (pfPass.getEchoChar() != 0) { pfPass.setEchoChar((char) 0); btnShowPass.setText("\uD83D\uDE48"); }

            else { pfPass.setEchoChar('\u2022'); btnShowPass.setText("\uD83D\uDC41"); }

        });

        passRow.add(btnShowPass, BorderLayout.EAST);

        g.gridx = 1; g.weightx = 0.6; form.add(passRow, g);

        // Ch\u1ee9c v\u1ee5 dropdown

        g.gridx = 0; g.gridy = fldLabels.length + 1; g.weightx = 0.4;

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

        btnLuu.setBackground(ACCENT); btnLuu.setForeground(Color.WHITE); btnLuu.setFocusPainted(false);

        JButton btnHuy = new JButton("H\u1ee7y"); btnHuy.setFocusPainted(false);

        btns.add(btnHuy); btns.add(btnLuu);

        dlg.add(btns, BorderLayout.SOUTH);



        btnLuu.addActionListener(e -> {

            String ma    = tfs[0].getText().trim();

            String ten   = tfs[1].getText().trim();

            String user  = tfs[2].getText().trim();

            String sdt   = tfs[3].getText().trim();

            String email = tfs[4].getText().trim();

            String ngay  = tfs[5].getText().trim();

            String luong = tfs[6].getText().trim();

            String pass  = new String(pfPass.getPassword()).trim();



            List<String> errs = new ArrayList<>();

            if (ma.isEmpty())   errs.add("- Vui l\u00f2ng nh\u1eadp m\u00e3 nh\u00e2n vi\u00ean.");

            if (ten.isEmpty())  errs.add("- Vui l\u00f2ng nh\u1eadp h\u1ecd v\u00e0 t\u00ean.");

            if (user.isEmpty()) errs.add("- Vui l\u00f2ng nh\u1eadp t\u00ean \u0111\u0103ng nh\u1eadp.");

            if (pass.isEmpty()) errs.add("- Vui l\u00f2ng nh\u1eadp m\u1eadt kh\u1ea9u.");

            else if (pass.length() < 6) errs.add("- M\u1eadt kh\u1ea9u t\u1ed1i thi\u1ec3u 6 k\u00fd t\u1ef1.");

            if (!sdt.isEmpty()   && !sdt.matches("^0\\d{9}$"))

                errs.add("- S\u0110T ph\u1ea3i l\u00e0 10 ch\u1eef s\u1ed1, b\u1eaft \u0111\u1ea7u b\u1eb1ng 0.");

            if (!email.isEmpty() && !email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$"))

                errs.add("- Email kh\u00f4ng \u0111\u00fang \u0111\u1ecbnh d\u1ea1ng.");

            if (!ngay.isEmpty()) {

                try { new java.text.SimpleDateFormat("dd/MM/yyyy").parse(ngay); }

                catch (Exception ex) { errs.add("- Ng\u00e0y ph\u1ea3i \u0111\u00fang \u0111\u1ecbnh d\u1ea1ng dd/MM/yyyy."); }

            }

            if (!luong.isEmpty()) {

                try { Long.parseLong(luong.replaceAll("[,.]", "")); }

                catch (NumberFormatException ex) { errs.add("- L\u01b0\u01a1ng ph\u1ea3i l\u00e0 s\u1ed1 nguy\u00ean."); }

            }

            // Duplicate ma NV check

            final String maFinal = ma;

            for (int i = 0; i < tableModel.getRowCount(); i++) {

                if (maFinal.equalsIgnoreCase(tableModel.getValueAt(i, COL_MA).toString())) {

                    errs.add("- M\u00e3 nh\u00e2n vi\u00ean '" + maFinal + "' \u0111\u00e3 t\u1ed3n t\u1ea1i.");

                    break;

                }

            }

            if (!errs.isEmpty()) {

                JOptionPane.showMessageDialog(dlg, String.join("\n", errs),

                        "L\u1ed7i nh\u1eadp li\u1ec7u", JOptionPane.WARNING_MESSAGE);

                return;

            }

            tableModel.addRow(new Object[]{

                ma, ten, cbRole.getSelectedItem().toString(), sdt, email, ngay, pass

            });

            dlg.dispose();

        });

        btnHuy.addActionListener(e -> {
            boolean dirty = false;
            for (JTextField f : tfs) {
                if (!f.getText().trim().isEmpty()) { dirty = true; break; }
            }
            if (!dirty) dirty = new String(pfPass.getPassword()).trim().length() > 0;
            if (dirty) {
                int cf = JOptionPane.showConfirmDialog(dlg,
                        "Bạn có chắc muốn hủy? Thông tin đã nhập sẽ mất.",
                        "Xác nhận hủy", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (cf != JOptionPane.YES_OPTION) return;
            }
            dlg.dispose();
        });

        dlg.pack();

        dlg.setMinimumSize(new Dimension(490, dlg.getPreferredSize().height));

        dlg.setLocationRelativeTo(this);

        dlg.setVisible(true);

    }

    // ── App-style button – gray bg / lavender hover (matching KhachHang) ────
    private JButton makeAppBtn(String text) {
        JButton b = new JButton(text);
        b.setFont(new Font("Arial", Font.BOLD, 14));
        b.setBackground(BTN_IDLE);
        b.setForeground(Color.DARK_GRAY);
        b.setFocusPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { b.setBackground(BTN_HOVER); }
            public void mouseExited(MouseEvent e)  { b.setBackground(BTN_IDLE); }
        });
        return b;
    }
}

