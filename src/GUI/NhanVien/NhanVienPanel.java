package GUI.NhanVien;

import BUS.EmployeeBUS;
import BUS.RoleBUS;
import DTO.EmployeeDTO;
import DTO.RoleDTO;
import GUI.ExportUtils;
import GUI.UIUtils;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.Dialog;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;

public class NhanVienPanel extends JPanel {

    // Color constants
    private static final Color PAGE_BG   = new Color(0xF8F7FF);
    private static final Color ACCENT    = new Color(0x5C4A7F);
    private static final Color CARD_LEFT = new Color(0xD1C4E9);
    private static final Color TBL_HDR   = new Color(0xAF9FCB);
    private static final Color BTN_IDLE  = new Color(0xD9D9D9);
    private static final Color BTN_HOVER = new Color(0xC5B3E6);

    // Column indices
    private static final int COL_MA = 0, COL_TEN = 1, COL_CHUCVU = 2;
    private static final int COL_SDT = 3, COL_EMAIL = 4, COL_NGAY = 5, COL_PASS = 6;

    // Fields
    private DefaultTableModel tableModel;
    private final EmployeeBUS empBUS = new EmployeeBUS();
    private List<RoleDTO> roles = new ArrayList<>();
    private final Map<Integer, String> roleMap = new HashMap<>();
    private final Set<Integer> revealedRows = new HashSet<>();
    private JTable table;
    private TableRowSorter<DefaultTableModel> sorter;
    private int selectedModelRow = -1;

    // Photo map: maNV -> absolute path
    private final Map<String, String> photoPathMap = new HashMap<>();

    // Detail card
    private JLabel lbName, lbRole, lbMaNV, lbGioiTinh, lbCMND, lbNgaySinh,
                   lbNgayTG, lbEmail, lbSdt, lbSalary;
    private JPanel avatarBox;
    private Image avatarImage = null;
    private JButton btnSuaNV, btnXoaNV;

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
        addHierarchyListener(e -> {
            if ((e.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) != 0 && !isShowing()) {
                revealedRows.clear();
                if (table != null) table.repaint();
            }
        });
    }

    // Roles
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

    // Header
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

    // Detail card
    private JPanel buildDetailCard() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(PAGE_BG);
        card.setBorder(BorderFactory.createLineBorder(new Color(0xBBBBBB)));

        JPanel hdr = new JPanel(new BorderLayout());
        hdr.setBackground(new Color(0xEDE7F6));
        hdr.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, CARD_LEFT),
                BorderFactory.createEmptyBorder(4, 10, 4, 10)));
        JLabel h = new JLabel("Th\u00f4ng tin chi ti\u1ebft");
        h.setFont(new Font("Arial", Font.BOLD, 13));
        h.setForeground(new Color(0x3D2F5C));
        hdr.add(h, BorderLayout.WEST);

        JPanel actionBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        actionBtns.setOpaque(false);
        btnSuaNV = makeAppBtn("S\u1eeda");
        btnXoaNV = new JButton("X\u00f3a");
        btnXoaNV.setFont(new Font("Arial", Font.BOLD, 13));
        btnXoaNV.setBackground(new Color(0xFFCDD2));
        btnXoaNV.setForeground(new Color(0xB71C1C));
        btnXoaNV.setFocusPainted(false);
        btnXoaNV.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnXoaNV.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btnXoaNV.setBackground(new Color(0xEF9A9A)); }
            public void mouseExited(MouseEvent e)  { btnXoaNV.setBackground(new Color(0xFFCDD2)); }
        });
        btnSuaNV.setEnabled(false);
        btnXoaNV.setEnabled(false);
        actionBtns.add(btnSuaNV);
        actionBtns.add(btnXoaNV);
        hdr.add(actionBtns, BorderLayout.EAST);
        card.add(hdr, BorderLayout.NORTH);

        btnSuaNV.addActionListener(e -> { if (selectedModelRow >= 0) showEditDialog(selectedModelRow); });
        btnXoaNV.addActionListener(e -> {
            if (selectedModelRow < 0) return;
            String ten = tableModel.getValueAt(selectedModelRow, COL_TEN).toString();
            int cf = JOptionPane.showConfirmDialog(this,
                    "X\u00f3a nh\u00e2n vi\u00ean \"" + ten + "\"?",
                    "X\u00e1c nh\u1eadn x\u00f3a", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (cf != JOptionPane.YES_OPTION) return;
            String ma = tableModel.getValueAt(selectedModelRow, COL_MA).toString();
            tableModel.removeRow(selectedModelRow);
            photoPathMap.remove(ma);
            selectedModelRow = -1;
            btnSuaNV.setEnabled(false);
            btnXoaNV.setEnabled(false);
            resetDetailLabels();
            avatarImage = null;
            if (avatarBox != null) avatarBox.repaint();
        });

        JPanel body = new JPanel(new GridLayout(1, 2));

        // Left - avatar + name + role
        JPanel left = new JPanel();
        left.setBackground(CARD_LEFT);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setBorder(BorderFactory.createEmptyBorder(20, 16, 20, 16));

        avatarBox = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (avatarImage != null) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                    g2.drawImage(avatarImage, 0, 0, getWidth(), getHeight(), null);
                    g2.dispose();
                } else {
                    drawAvatarPlaceholder(g);
                }
            }
        };
        avatarBox.setBackground(Color.WHITE);
        avatarBox.setBorder(BorderFactory.createLineBorder(new Color(0x9575CD)));
        avatarBox.setPreferredSize(new Dimension(80, 80));
        avatarBox.setMaximumSize(new Dimension(90, 90));
        avatarBox.setAlignmentX(Component.CENTER_ALIGNMENT);
        left.add(avatarBox);
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

        // Right - fields grid
        JPanel right = new JPanel(new GridBagLayout());
        right.setBackground(Color.WHITE);
        right.setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));
        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.insets = new Insets(5, 4, 5, 12);
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
            gc.gridx = 0; gc.gridy = row; gc.weightx = 0.25;
            JLabel la = new JLabel((String) fieldDef[row][0]); la.setFont(bf);
            right.add(la, gc);
            gc.gridx = 1; gc.weightx = 0.25;
            ((JLabel) fieldDef[row][1]).setFont(vf);
            right.add((JLabel) fieldDef[row][1], gc);
            gc.gridx = 2; gc.weightx = 0.25;
            JLabel lb2 = new JLabel((String) fieldDef[row][2]); lb2.setFont(bf);
            right.add(lb2, gc);
            gc.gridx = 3; gc.weightx = 0.25;
            ((JLabel) fieldDef[row][3]).setFont(vf);
            right.add((JLabel) fieldDef[row][3], gc);
        }
        gc.gridx = 0; gc.gridy = fieldDef.length; gc.gridwidth = 4;
        gc.weighty = 1.0; gc.fill = GridBagConstraints.BOTH;
        right.add(new JLabel(), gc);

        body.add(left); body.add(right);
        card.add(body, BorderLayout.CENTER);
        return card;
    }

    private void drawAvatarPlaceholder(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int pw = avatarBox.getWidth(), ph = avatarBox.getHeight();
        double sc = Math.min((pw - 16) / 24.0, (ph - 16) / 24.0);
        double ox = (pw - 24 * sc) / 2.0, oy = (ph - 24 * sc) / 2.0;
        g2.translate(ox, oy);
        g2.scale(sc, sc);
        g2.setColor(ACCENT);
        g2.fill(new Ellipse2D.Double(8, 4, 8, 8));
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

    private void resetDetailLabels() {
        lbName.setText("T\u00caN NH\u00c2N VI\u00caN");
        lbRole.setText("CH\u1ee8C V\u1ee4");
        for (JLabel l : new JLabel[]{lbMaNV, lbGioiTinh, lbCMND, lbNgaySinh, lbNgayTG, lbEmail, lbSdt, lbSalary})
            l.setText("");
    }

    // List section
    private JPanel buildListSection() {
        JPanel section = new JPanel(new BorderLayout(0, 8));
        section.setBackground(PAGE_BG);

        // ── ROW 1: title + action buttons ──────────────────────────────────
        JPanel row1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        row1.setBackground(PAGE_BG);

        JLabel lbTitle = new JLabel("Danh s\u00e1ch nh\u00e2n vi\u00ean");
        lbTitle.setFont(new Font("Arial", Font.BOLD | Font.ITALIC, 16));
        lbTitle.setForeground(ACCENT);

        JButton btnThem    = makeAppBtn("+ TH\u00caM nh\u00e2n vi\u00ean");
        JButton btnRefresh = makeAppBtn("L\u00e0m m\u1edbi");
        btnRefresh.setIcon(UIUtils.iconRefresh(14, new Color(0x388E3C)));
        btnRefresh.setHorizontalTextPosition(SwingConstants.RIGHT);
        btnRefresh.setIconTextGap(4);
        JButton btnPDF     = ExportUtils.makeExportButton("Xu\u1ea5t PDF",   new Color(0x7B52AB));
        JButton btnExcel   = ExportUtils.makeExportButton("Xu\u1ea5t Excel", new Color(0x2E7D32));
        JButton btnImport  = ExportUtils.makeImportButton("Nh\u1eadp CSV");
        Font btnFont = new Font("Arial", Font.BOLD, 13);
        for (JButton b : new JButton[]{btnPDF, btnExcel, btnImport}) {
            b.setFont(btnFont);
        }

        row1.add(lbTitle);
        row1.add(Box.createHorizontalStrut(8));
        row1.add(btnThem);
        row1.add(btnRefresh);
        row1.add(btnPDF);
        row1.add(btnExcel);
        row1.add(btnImport);

        // ── ROW 2: search controls ──────────────────────────────────────────
        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        row2.setBackground(PAGE_BG);

        JLabel lbTimTheo = new JLabel("T\u00ecm theo:");
        lbTimTheo.setFont(new Font("Arial", Font.PLAIN, 13));
        String[] searchFields = {"M\u00e3 NV", "T\u00ean nh\u00e2n vi\u00ean", "Ch\u1ee9c v\u1ee5", "S\u0110T"};
        JComboBox<String> cbField = new JComboBox<>(searchFields);
        UIUtils.styleComboBox(cbField);
        cbField.setPreferredSize(new Dimension(140, 32));

        JTextField tfSearch = new JTextField(16);
        tfSearch.setFont(new Font("Arial", Font.PLAIN, 13));

        JButton btnSearch = new JButton("T\u00ecm");
        btnSearch.setFont(new Font("Arial", Font.BOLD, 13));
        btnSearch.setBackground(new Color(0x3D2F5C));
        btnSearch.setForeground(Color.WHITE);
        btnSearch.setFocusPainted(false);
        btnSearch.setBorderPainted(false);
        btnSearch.setOpaque(true);
        btnSearch.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSearch.setPreferredSize(new Dimension(70, 32));

        row2.add(lbTimTheo);
        row2.add(cbField);
        row2.add(tfSearch);
        row2.add(btnSearch);

        // ── Combined toolbar ────────────────────────────────────────────────
        JPanel toolbar = new JPanel();
        toolbar.setLayout(new BoxLayout(toolbar, BoxLayout.Y_AXIS));
        toolbar.setBackground(PAGE_BG);
        toolbar.add(row1);
        toolbar.add(row2);

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
        table.getColumnModel().getColumn(COL_PASS).setCellRenderer(new PasswordCellRenderer());

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

        table.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;
            int row = table.getSelectedRow();
            if (row < 0) return;
            fillDetail(table.convertRowIndexToModel(row));
        });

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
            revealedRows.clear(); sorter.setRowFilter(null); tfSearch.setText("");
            loadEmployees();
        });
        btnThem.addActionListener(e -> showAddDialog());
        btnPDF.addActionListener(e -> ExportUtils.xuatPDF(this, tableModel, "Danh s\u00e1ch nh\u00e2n vi\u00ean"));
        btnExcel.addActionListener(e -> ExportUtils.xuatCSV(this, tableModel, "nhan_vien"));
        btnImport.addActionListener(e -> {
            List<String[]> rows = ExportUtils.importCSV(this);
            if (rows == null) return;
            for (String[] r : rows) {
                if (r.length < 7) continue;
                tableModel.addRow(new Object[]{r[0],r[1],r[2],r[3],r[4],r[5],r[6]});
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        UIUtils.styleScrollPane(scroll);

        section.add(toolbar, BorderLayout.NORTH);
        section.add(scroll, BorderLayout.CENTER);
        return section;
    }

    // Password cell renderer
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
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int sz = 16;
            int ix = getWidth() - sz - 6;
            int iy = (getHeight() - sz) / 2;
            g2.translate(ix, iy);
            g2.scale(sz / 24.0, sz / 24.0);
            g2.setColor(ACCENT);
            g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            Path2D eye = new Path2D.Float();
            eye.moveTo(2, 12); eye.curveTo(2, 12, 6, 6, 12, 6);
            eye.curveTo(18, 6, 22, 12, 22, 12);
            eye.curveTo(22, 12, 18, 18, 12, 18);
            eye.curveTo(6, 18, 2, 12, 2, 12);
            eye.closePath();
            g2.draw(eye);
            g2.fill(new Ellipse2D.Double(9, 9, 6, 6));
            if (!revealed) {
                g2.setColor(new Color(0xAAAAAA));
                g2.setStroke(new BasicStroke(2.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawLine(4, 20, 20, 4);
            }
            g2.dispose();
        }
    }

    // Data
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
            tableModel.addRow(new Object[]{"NV001", "Nguy\u1ec5n V\u0103n A", "Qu\u1ea3n l\u00fd",  "0901234567", "nva@mail.com",  "01/01/2024", "pass123"});
            tableModel.addRow(new Object[]{"NV002", "Tr\u1ea7n Th\u1ecb B",   "Nh\u00e2n vi\u00ean", "0907654321", "ttb@mail.com",  "15/03/2024", "abc456"});
        }
    }

    private void fillDetail(int modelRow) {
        selectedModelRow = modelRow;
        btnSuaNV.setEnabled(true);
        btnXoaNV.setEnabled(true);
        lbName.setText(tableModel.getValueAt(modelRow, COL_TEN).toString());
        lbRole.setText(tableModel.getValueAt(modelRow, COL_CHUCVU).toString());
        lbMaNV.setText(tableModel.getValueAt(modelRow, COL_MA).toString());
        lbSdt.setText(tableModel.getValueAt(modelRow, COL_SDT).toString());
        lbEmail.setText(tableModel.getValueAt(modelRow, COL_EMAIL).toString());
        lbNgayTG.setText(tableModel.getValueAt(modelRow, COL_NGAY).toString());
        String ma = tableModel.getValueAt(modelRow, COL_MA).toString();
        String path = photoPathMap.get(ma);
        avatarImage = null;
        if (path != null) {
            try { avatarImage = ImageIO.read(new File(path)).getScaledInstance(90, 90, Image.SCALE_SMOOTH); }
            catch (Exception ignored) {}
        }
        if (avatarBox != null) avatarBox.repaint();
    }

    private String generateMaNV() {
        int max = 0;
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String ma = tableModel.getValueAt(i, COL_MA).toString();
            if (ma.matches("(?i)NV\\d+")) {
                try { max = Math.max(max, Integer.parseInt(ma.substring(2))); }
                catch (NumberFormatException ignored) {}
            }
        }
        return String.format("NV%03d", max + 1);
    }

    private void showAddDialog() { showEmployeeDialog(null, -1); }
    private void showEditDialog(int modelRow) { showEmployeeDialog(modelRow, modelRow); }

    private void showEmployeeDialog(Integer prefilledRow, int editRow) {
        boolean isEdit = (prefilledRow != null);
        String title = isEdit ? "S\u1eeda Nh\u00e2n Vi\u00ean" : "Th\u00eam Nh\u00e2n Vi\u00ean";

        JDialog dlg = new JDialog(SwingUtilities.getWindowAncestor(this),
                title, Dialog.ModalityType.APPLICATION_MODAL);
        dlg.setLayout(new BorderLayout());

        // Photo section
        final String[] tmpPhotoPath = {null};
        JPanel photoSection = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 8));
        photoSection.setBackground(new Color(0xF3F0FA));
        photoSection.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0xD1C4E9)));

        JLabel photoPreview = new JLabel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getIcon() == null) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setColor(new Color(0xBBBBBB));
                    g2.setFont(new Font("Arial", Font.PLAIN, 11));
                    String hint = "Ch\u01b0a c\u00f3 \u1ea3nh";
                    FontMetrics fm = g2.getFontMetrics();
                    g2.drawString(hint, (getWidth()-fm.stringWidth(hint))/2, getHeight()/2 + fm.getAscent()/2);
                    g2.dispose();
                }
            }
        };
        photoPreview.setPreferredSize(new Dimension(80, 80));
        photoPreview.setBorder(BorderFactory.createLineBorder(new Color(0xAAAAAA)));
        photoPreview.setBackground(Color.WHITE);
        photoPreview.setOpaque(true);
        photoPreview.setHorizontalAlignment(SwingConstants.CENTER);

        if (isEdit) {
            String ma = tableModel.getValueAt(prefilledRow, COL_MA).toString();
            String path = photoPathMap.get(ma);
            if (path != null) {
                try {
                    BufferedImage img = ImageIO.read(new File(path));
                    if (img != null) photoPreview.setIcon(new ImageIcon(img.getScaledInstance(80, 80, Image.SCALE_SMOOTH)));
                    tmpPhotoPath[0] = path;
                } catch (Exception ignored) {}
            }
        }

        JButton btnChonAnh = makeAppBtn("Ch\u1ecdn \u1ea3nh");
        JLabel lbPhotoHint = new JLabel("<html><font color='gray' size='2'>Vu\u00f4ng, 256\u2013512px<br>(t\u1ef1 \u0111\u1ed9ng scale)</font></html>");

        btnChonAnh.addActionListener(ev -> {
            Window owner = SwingUtilities.getWindowAncestor(dlg);
            java.awt.FileDialog fd = new java.awt.FileDialog(
                (owner instanceof java.awt.Frame) ? (java.awt.Frame) owner : null,
                "Ch\u1ecdn \u1ea3nh nh\u00e2n vi\u00ean", java.awt.FileDialog.LOAD);
            fd.setFilenameFilter((dir, name) -> {
                String n = name.toLowerCase();
                return n.endsWith(".jpg") || n.endsWith(".jpeg") || n.endsWith(".png");
            });
            fd.setVisible(true);
            String chosenDir  = fd.getDirectory();
            String chosenFile = fd.getFile();
            if (chosenDir == null || chosenFile == null) return;
            File f = new File(chosenDir, chosenFile);
            try {
                BufferedImage img = ImageIO.read(f);
                if (img == null) { JOptionPane.showMessageDialog(dlg, "Kh\u00f4ng \u0111\u1ecdc \u0111\u01b0\u1ee3c file \u1ea3nh.", "L\u1ed7i", JOptionPane.ERROR_MESSAGE); return; }
                if (img.getWidth() != img.getHeight())
                    JOptionPane.showMessageDialog(dlg, "\u1ea2nh kh\u00f4ng vu\u00f4ng \u2014 s\u1ebd b\u1ecb c\u1eaft khi hi\u1ec3n th\u1ecb.", "C\u1ea3nh b\u00e1o", JOptionPane.WARNING_MESSAGE);
                tmpPhotoPath[0] = f.getAbsolutePath();
                photoPreview.setIcon(new ImageIcon(img.getScaledInstance(80, 80, Image.SCALE_SMOOTH)));
                photoPreview.repaint();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dlg, "L\u1ed7i khi \u0111\u1ecdc \u1ea3nh: " + ex.getMessage(), "L\u1ed7i", JOptionPane.ERROR_MESSAGE);
            }
        });

        photoSection.add(photoPreview);
        JPanel photoRight = new JPanel(new GridLayout(2, 1, 0, 4));
        photoRight.setOpaque(false);
        photoRight.add(btnChonAnh);
        photoRight.add(lbPhotoHint);
        photoSection.add(photoRight);
        dlg.add(photoSection, BorderLayout.NORTH);

        // Form fields: [0]Ho ten, [1]Username, [2]SDT, [3]Email, [4]Luong
        JTextField[] tfs = new JTextField[5];
        String[] fieldLabels = {
            "H\u1ecd v\u00e0 t\u00ean *:",
            "T\u00ean \u0111\u0103ng nh\u1eadp *:",
            "S\u0110T (10 s\u1ed1):",
            "Email:",
            "L\u01b0\u01a1ng (VN\u0110):"
        };
        for (int i = 0; i < tfs.length; i++) {
            tfs[i] = new JTextField(18);
            tfs[i].setFont(new Font("Arial", Font.PLAIN, 13));
        }
        UIUtils.attachMoneyFormatter(tfs[4]);

        SpinnerDateModel dateModel = new SpinnerDateModel();
        JSpinner spNgay = new JSpinner(dateModel);
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(spNgay, "dd/MM/yyyy");
        spNgay.setEditor(dateEditor);
        spNgay.setFont(new Font("Arial", Font.PLAIN, 13));

        JPasswordField pfPass = new JPasswordField(18);
        pfPass.setFont(new Font("Arial", Font.PLAIN, 13));
        JButton btnShowPass = new JButton(UIUtils.iconEyeOpen(18, ACCENT));
        btnShowPass.setToolTipText("Hi\u1ec7n / \u1ea8n m\u1eadt kh\u1ea9u");
        btnShowPass.setPreferredSize(new Dimension(32, 32));
        btnShowPass.setFocusPainted(false);
        btnShowPass.setBorderPainted(false);
        btnShowPass.setContentAreaFilled(false);
        btnShowPass.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnShowPass.addActionListener(ev -> {
            if (pfPass.getEchoChar() != 0) {
                pfPass.setEchoChar((char) 0);
                btnShowPass.setIcon(UIUtils.iconEyeOff(18, ACCENT));
            } else {
                pfPass.setEchoChar('\u2022');
                btnShowPass.setIcon(UIUtils.iconEyeOpen(18, ACCENT));
            }
        });

        String[] roleNames = roles.isEmpty()
            ? new String[]{"Nh\u00e2n vi\u00ean", "Qu\u1ea3n l\u00fd"}
            : roles.stream().map(RoleDTO::getName).toArray(String[]::new);
        JComboBox<String> cbRole = new JComboBox<>(roleNames);
        UIUtils.styleComboBox(cbRole);

        JLabel[] errLabels = new JLabel[5];
        for (int i = 0; i < errLabels.length; i++) {
            errLabels[i] = new JLabel(" ");
            errLabels[i].setFont(new Font("Arial", Font.ITALIC, 11));
            errLabels[i].setForeground(Color.RED);
        }

        if (isEdit) {
            tfs[0].setText(tableModel.getValueAt(prefilledRow, COL_TEN).toString());
            tfs[2].setText(tableModel.getValueAt(prefilledRow, COL_SDT).toString());
            tfs[3].setText(tableModel.getValueAt(prefilledRow, COL_EMAIL).toString());
            String ngayStr = tableModel.getValueAt(prefilledRow, COL_NGAY).toString();
            if (!ngayStr.isEmpty()) {
                try { dateModel.setValue(new SimpleDateFormat("dd/MM/yyyy").parse(ngayStr)); }
                catch (Exception ignored) {}
            }
            // pre-select role
            String currentRole = tableModel.getValueAt(prefilledRow, COL_CHUCVU).toString();
            for (int i = 0; i < cbRole.getItemCount(); i++) {
                if (cbRole.getItemAt(i).equals(currentRole)) { cbRole.setSelectedIndex(i); break; }
            }
        }

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createEmptyBorder(12, 20, 8, 20));
        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.HORIZONTAL;
        Font labelFont = new Font("Arial", Font.BOLD, 13);
        Font fieldFont = new Font("Arial", Font.PLAIN, 13);

        int row = 0;
        if (isEdit) {
            gc.gridx = 0; gc.gridy = row; gc.weightx = 0.35; gc.insets = new Insets(5,5,2,5);
            JLabel lMa = new JLabel("M\u00e3 nh\u00e2n vi\u00ean:"); lMa.setFont(labelFont);
            form.add(lMa, gc);
            gc.gridx = 1; gc.weightx = 0.65;
            JLabel lMaVal = new JLabel(tableModel.getValueAt(prefilledRow, COL_MA).toString());
            lMaVal.setFont(new Font("Arial", Font.BOLD, 13));
            lMaVal.setForeground(ACCENT);
            form.add(lMaVal, gc);
            row++;
        }

        for (int i = 0; i < fieldLabels.length; i++) {
            gc.gridx = 0; gc.gridy = row; gc.weightx = 0.35; gc.insets = new Insets(5,5,2,5);
            JLabel lbl = new JLabel(fieldLabels[i]); lbl.setFont(labelFont);
            form.add(lbl, gc);
            gc.gridx = 1; gc.weightx = 0.65;
            tfs[i].setFont(fieldFont);
            form.add(tfs[i], gc);
            row++;
            gc.gridx = 1; gc.gridy = row; gc.insets = new Insets(0,5,4,5);
            form.add(errLabels[i], gc);
            row++;
        }

        gc.gridx = 0; gc.gridy = row; gc.weightx = 0.35; gc.insets = new Insets(5,5,2,5);
        JLabel lDate = new JLabel("Ng\u00e0y tham gia:"); lDate.setFont(labelFont);
        form.add(lDate, gc);
        gc.gridx = 1; gc.weightx = 0.65;
        form.add(spNgay, gc);
        row++;

        gc.gridx = 0; gc.gridy = row; gc.weightx = 0.35; gc.insets = new Insets(5,5,2,5);
        JLabel lPass = new JLabel("M\u1eadt kh\u1ea9u *:"); lPass.setFont(labelFont);
        form.add(lPass, gc);
        gc.gridx = 1; gc.weightx = 0.65;
        JPanel passRow = new JPanel(new BorderLayout(4, 0));
        passRow.setOpaque(false);
        passRow.add(pfPass, BorderLayout.CENTER);
        passRow.add(btnShowPass, BorderLayout.EAST);
        form.add(passRow, gc);
        row++;
        gc.gridx = 1; gc.gridy = row; gc.insets = new Insets(0,5,4,5);
        // errLabels[4] is password error
        form.add(errLabels[4], gc);
        row++;

        gc.gridx = 0; gc.gridy = row; gc.weightx = 0.35; gc.insets = new Insets(5,5,5,5);
        JLabel lRole = new JLabel("Ch\u1ee9c v\u1ee5:"); lRole.setFont(labelFont);
        form.add(lRole, gc);
        gc.gridx = 1; gc.weightx = 0.65;
        form.add(cbRole, gc);

        JScrollPane formScroll = new JScrollPane(form);
        formScroll.setBorder(null);
        formScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        dlg.add(formScroll, BorderLayout.CENTER);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
        btns.setBackground(new Color(0xF3F0FA));
        btns.setBorder(BorderFactory.createMatteBorder(1,0,0,0, new Color(0xD1C4E9)));
        JButton btnLuu = new JButton("L\u01b0u");
        btnLuu.setBackground(ACCENT); btnLuu.setForeground(Color.WHITE);
        btnLuu.setFont(new Font("Arial", Font.BOLD, 13));
        btnLuu.setFocusPainted(false); btnLuu.setBorderPainted(false);
        btnLuu.setPreferredSize(new Dimension(80, 32));
        JButton btnHuy = new JButton("H\u1ee7y");
        btnHuy.setFont(new Font("Arial", Font.BOLD, 13));
        btnHuy.setBackground(BTN_IDLE); btnHuy.setForeground(Color.DARK_GRAY);
        btnHuy.setFocusPainted(false); btnHuy.setBorderPainted(false);
        btnHuy.setPreferredSize(new Dimension(80, 32));
        btns.add(btnHuy); btns.add(btnLuu);
        dlg.add(btns, BorderLayout.SOUTH);

        btnLuu.addActionListener(e -> {
            for (JLabel el : errLabels) el.setText(" ");

            String ten   = tfs[0].getText().trim();
            String user  = tfs[1].getText().trim();
            String sdt   = tfs[2].getText().trim();
            String email = tfs[3].getText().trim();
            String pass  = new String(pfPass.getPassword()).trim();
            String ngay  = new SimpleDateFormat("dd/MM/yyyy").format((java.util.Date) spNgay.getValue());

            boolean hasError = false;

            if (ten.isEmpty())  { errLabels[0].setText("Vui l\u00f2ng nh\u1eadp h\u1ecd v\u00e0 t\u00ean"); hasError = true; }
            if (user.isEmpty()) { errLabels[1].setText("Vui l\u00f2ng nh\u1eadp t\u00ean \u0111\u0103ng nh\u1eadp"); hasError = true; }
            if (!sdt.isEmpty() && !sdt.matches("^0\\d{9}$"))
                { errLabels[2].setText("S\u0110T ph\u1ea3i 10 s\u1ed1, b\u1eaft \u0111\u1ea7u 0"); hasError = true; }
            if (!email.isEmpty() && !email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$"))
                { errLabels[3].setText("Email kh\u00f4ng h\u1ee3p l\u1ec7"); hasError = true; }

            if (!isEdit || !pass.isEmpty()) {
                if (pass.isEmpty()) {
                    errLabels[4].setText("Vui l\u00f2ng nh\u1eadp m\u1eadt kh\u1ea9u");
                    hasError = true;
                } else if (pass.length() < 6) {
                    errLabels[4].setText("T\u1ed1i thi\u1ec3u 6 k\u00fd t\u1ef1");
                    hasError = true;
                } else {
                    boolean hasLetter = pass.chars().anyMatch(Character::isLetter);
                    boolean hasDigit  = pass.chars().anyMatch(Character::isDigit);
                    if (!hasLetter || !hasDigit) {
                        errLabels[4].setText("Ph\u1ea3i c\u00f3 c\u1ea3 ch\u1eef v\u00e0 s\u1ed1");
                        hasError = true;
                    }
                }
            }

            if (hasError) { dlg.revalidate(); dlg.repaint(); return; }

            String ma = isEdit ? tableModel.getValueAt(prefilledRow, COL_MA).toString() : generateMaNV();

            if (tmpPhotoPath[0] != null && !tmpPhotoPath[0].isEmpty()) {
                try {
                    File src = new File(tmpPhotoPath[0]);
                    String ext = tmpPhotoPath[0].contains(".") ? tmpPhotoPath[0].substring(tmpPhotoPath[0].lastIndexOf('.')) : ".png";
                    File dest = new File("img/employees/" + ma + ext);
                    dest.getParentFile().mkdirs();
                    java.nio.file.Files.copy(src.toPath(), dest.toPath(),
                            java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                    photoPathMap.put(ma, dest.getAbsolutePath());
                } catch (Exception ex) {
                    System.err.println("Photo copy failed: " + ex.getMessage());
                }
            }

            String finalPass = (pass.isEmpty() && isEdit)
                ? tableModel.getValueAt(prefilledRow, COL_PASS).toString()
                : pass;

            if (isEdit) {
                tableModel.setValueAt(ten,  editRow, COL_TEN);
                tableModel.setValueAt(cbRole.getSelectedItem().toString(), editRow, COL_CHUCVU);
                tableModel.setValueAt(sdt,  editRow, COL_SDT);
                tableModel.setValueAt(email,editRow, COL_EMAIL);
                tableModel.setValueAt(ngay, editRow, COL_NGAY);
                tableModel.setValueAt(finalPass, editRow, COL_PASS);
                fillDetail(editRow);
            } else {
                tableModel.addRow(new Object[]{ma, ten, cbRole.getSelectedItem().toString(), sdt, email, ngay, finalPass});
            }
            dlg.dispose();
        });

        btnHuy.addActionListener(e -> {
            if (isEdit) {
                int cf = JOptionPane.showConfirmDialog(dlg,
                        "H\u1ee7y s\u1eeda? Thay \u0111\u1ed5i ch\u01b0a l\u01b0u s\u1ebd m\u1ea5t.",
                        "X\u00e1c nh\u1eadn", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (cf != JOptionPane.YES_OPTION) return;
            } else {
                boolean dirty = false;
                for (JTextField f : tfs) if (!f.getText().trim().isEmpty()) { dirty = true; break; }
                if (!dirty) dirty = new String(pfPass.getPassword()).trim().length() > 0;
                if (dirty) {
                    int cf = JOptionPane.showConfirmDialog(dlg,
                            "B\u1ea1n c\u00f3 ch\u1eafc mu\u1ed1n h\u1ee7y? Th\u00f4ng tin \u0111\u00e3 nh\u1eadp s\u1ebd m\u1ea5t.",
                            "X\u00e1c nh\u1eadn h\u1ee7y", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                    if (cf != JOptionPane.YES_OPTION) return;
                }
            }
            dlg.dispose();
        });

        dlg.pack();
        dlg.setMinimumSize(new Dimension(500, dlg.getPreferredSize().height));
        dlg.setLocationRelativeTo(this);
        dlg.setVisible(true);
    }

    private JButton makeAppBtn(String text) {
        JButton b = new JButton(text);
        b.setFont(new Font("Arial", Font.BOLD, 13));
        b.setBackground(BTN_IDLE);
        b.setForeground(Color.DARK_GRAY);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setOpaque(true);
        b.setBorder(BorderFactory.createEmptyBorder(9, 14, 9, 14));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { b.setBackground(BTN_HOVER); }
            public void mouseExited(MouseEvent e)  { b.setBackground(BTN_IDLE); }
        });
        return b;
    }
}