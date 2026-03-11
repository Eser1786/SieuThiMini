
package GUI.NhanVien;

import BUS.EmployeeBUS;
import BUS.RoleBUS;
import BUS.UserSession;
import DTO.EmployeeDTO;
import DTO.RoleDTO;
import GUI.ExportUtils;
import GUI.UIUtils;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.io.File;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.*;

public class NhanVienPanel extends JPanel {

    
    private static final Color PAGE_BG   = new Color(0xF8F7FF);
    private static final Color ACCENT    = new Color(0x5C4A7F);
    private static final Color CARD_LEFT = new Color(0xD1C4E9);
    private static final Color TBL_HDR   = new Color(0xAF9FCB);
    private static final Color BTN_IDLE  = new Color(0xD9D9D9);
    private static final Color BTN_HOVER = new Color(0xC5B3E6);

    
    static final int COL_MA = 0, COL_TEN = 1, COL_CHUCVU = 2;
    static final int COL_SDT = 3, COL_EMAIL = 4, COL_NGAY = 5, COL_PASS = 6;
    static final int COL_ID = 7; 

    
    DefaultTableModel tableModel;
    private final EmployeeBUS empBUS = new EmployeeBUS();
    List<RoleDTO> roles = new ArrayList<>();
    private final Map<Integer, String> roleMap = new HashMap<>();
    private final Set<Integer> revealedRows = new HashSet<>();
    private JTable table;
    private TableRowSorter<DefaultTableModel> sorter;
    private int selectedModelRow = -1;

    
    final Map<String, String> photoPathMap = new HashMap<>();

    
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

    
    private void loadRoles() {
        try {
            List<RoleDTO> r = new RoleBUS().getAllRoles();
            System.out.println("Loaded roles: " + (r != null ? r.size() : "null"));
            if (r != null) roles = r;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        for (RoleDTO r : roles) roleMap.put(r.getId(), r.getName());
        System.out.println("Role map: " + roleMap);
    }

    private String roleName(int id) {
        String name = roleMap.getOrDefault(id, "Nhân viên");
        switch (name.toUpperCase()) {
            case "ADMIN": return "Admin";
            case "MANAGER": return "Quản lý nhân viên, sản phẩm";
            case "CASHIER": return "Thu ngân";
            case "WAREHOUSE": return "Nhập kho";
            case "SUPPORT": return "Chăm sóc khách hàng";
            default: return name;
        }
    }

    
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
            int cf1 = JOptionPane.showConfirmDialog(this,
                    "Xóa nhân viên \"" + ten + "\"?",
                    "Xác nhận xóa", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (cf1 != JOptionPane.YES_OPTION) return;
            int cf2 = JOptionPane.showConfirmDialog(this,
                    "Xác nhận lần cuối: Nhân viên sẽ bị ẩn vĩnh viễn. Tiếp tục?",
                    "Xác nhận lần 2", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (cf2 != JOptionPane.YES_OPTION) return;
            int empId = (Integer) tableModel.getValueAt(selectedModelRow, COL_ID);
            String ma = tableModel.getValueAt(selectedModelRow, COL_MA).toString();
            empBUS.softDeleteEmployee(empId);
            photoPathMap.remove(ma);
            selectedModelRow = -1;
            btnSuaNV.setEnabled(false);
            btnXoaNV.setEnabled(false);
            resetDetailLabels();
            avatarImage = null;
            if (avatarBox != null) avatarBox.repaint();
            loadEmployees();
        });

        JPanel body = new JPanel(new GridLayout(1, 2));

        
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

        
        JPanel right = new JPanel(new GridBagLayout());
        right.setBackground(Color.WHITE);
        right.setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));
        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.insets = new Insets(5, 4, 5, 12);
        Font bf = new Font("Arial", Font.BOLD, 13);

        lbMaNV = new JLabel(""); lbNgayTG   = new JLabel("");
        lbEmail  = new JLabel(""); lbSdt  = new JLabel("");
        lbSalary   = new JLabel("");

        Font vf = new Font("Arial", Font.PLAIN, 13);
        Object[][] fieldDef = {
            {"Mã nhân viên:", lbMaNV,     "Email:",             lbEmail},
            {"SĐT:",        lbSdt, "Ngày tham gia:", lbNgayTG},
            {"Lương:",             lbSalary, "",    new JLabel("")}
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
        lbName.setText("TÊN NHÂN VIÊN");
        lbRole.setText("CHỨC VỤ");
        for (JLabel l : new JLabel[]{lbMaNV, lbNgayTG, lbEmail, lbSdt, lbSalary})
            l.setText("");
    }

    
    private JPanel buildListSection() {
        JPanel section = new JPanel(new BorderLayout(0, 8));
        section.setBackground(PAGE_BG);

        
        JPanel toolbar = new JPanel(new GUI.WrapLayout(FlowLayout.LEFT, 8, 4));
        toolbar.setBackground(PAGE_BG);
        toolbar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xCCCCCC), 1),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));

        String[] roleFilters = {"Tất cả", "Quản lý", "Nhân viên"};
        JComboBox<String> cbRoleFilter = new JComboBox<>(roleFilters);
        cbRoleFilter.setPreferredSize(new Dimension(170, 38));
        UIUtils.styleComboBox(cbRoleFilter);

        JPanel timPanel = new JPanel(new BorderLayout());
        timPanel.setPreferredSize(new Dimension(240, 38));
        timPanel.setBackground(Color.WHITE);
        timPanel.setBorder(BorderFactory.createLineBorder(new Color(0xBBBBBB), 1));
        JTextField tfSearch = new JTextField();
        tfSearch.setFont(new Font("Arial", Font.PLAIN, 13));
        tfSearch.setBorder(BorderFactory.createEmptyBorder(2, 8, 2, 4));
        JButton btnSearch = new JButton("Q");
        btnSearch.setBorderPainted(false);
        btnSearch.setContentAreaFilled(false);
        btnSearch.setFocusPainted(false);
        btnSearch.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSearch.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btnSearch.setContentAreaFilled(true);
                btnSearch.setBackground(BTN_HOVER);
                btnSearch.setOpaque(true);
            }
            public void mouseExited(MouseEvent e) {
                btnSearch.setContentAreaFilled(false);
                btnSearch.setOpaque(false);
            }
        });
        timPanel.add(tfSearch, BorderLayout.CENTER);
        timPanel.add(btnSearch, BorderLayout.EAST);

        JLabel lbRoleFilter = new JLabel("Chức vụ:");
        lbRoleFilter.setFont(new Font("Arial", Font.PLAIN, 13));
        JLabel lbSearch = new JLabel("Tìm kiếm:");
        lbSearch.setFont(new Font("Arial", Font.PLAIN, 13));

        JPanel pRole   = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0)); pRole.setOpaque(false); pRole.add(lbRoleFilter); pRole.add(cbRoleFilter);
        JPanel pSearch = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0)); pSearch.setOpaque(false); pSearch.add(lbSearch); pSearch.add(timPanel);

        JButton btnThem    = makeAppBtn("+ TH\u00caM nh\u00e2n vi\u00ean");
        JButton btnRefresh = makeAppBtn("L\u00e0m m\u1edbi");
        btnRefresh.setIcon(UIUtils.iconRefresh(14, new Color(0x388E3C)));
        btnRefresh.setHorizontalTextPosition(SwingConstants.RIGHT);
        btnRefresh.setIconTextGap(4);
        JButton btnPDF     = ExportUtils.makeExportButton("Xuất PDF",   new Color(0x7B52AB));
        JButton btnExcel   = ExportUtils.makeExportButton("Xuất Excel", new Color(0x2E7D32));
        JButton btnImport  = ExportUtils.makeImportButton("Nh\u1eadp CSV");
        Font btnFont = new Font("Arial", Font.BOLD, 13);
        for (JButton b : new JButton[]{btnPDF, btnExcel, btnImport}) {
            b.setFont(btnFont);
        }

        toolbar.add(pRole);
        toolbar.add(pSearch);
        toolbar.add(btnThem);
        toolbar.add(btnRefresh);
        toolbar.add(btnPDF);
        toolbar.add(btnExcel);
        toolbar.add(btnImport);

        String[] cols = {"M\u00e3 NV", "T\u00ean nh\u00e2n vi\u00ean", "Ch\u1ee9c v\u1ee5",
                         "S\u0110T", "Email", "Ng\u00e0y tham gia", "M\u1eadt kh\u1ea9u", "employee_id"};
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
        
        table.getColumnModel().getColumn(COL_ID).setMinWidth(0);
        table.getColumnModel().getColumn(COL_ID).setMaxWidth(0);
        table.getColumnModel().getColumn(COL_ID).setWidth(0);
        table.getColumnModel().getColumn(COL_ID).setPreferredWidth(0);

        DefaultTableCellRenderer altR = new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(
                    JTable t, Object v, boolean sel, boolean foc, int r, int c) {
                super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                if (!sel) {
                    int modelRow = t.convertRowIndexToModel(r);
                    Object ma = tableModel.getValueAt(modelRow, COL_MA);
                    EmployeeDTO me = UserSession.getCurrentUser();
                    if (me != null && ma != null && ma.toString().equals(me.getCode())) {
                        setBackground(new Color(0xC8E6C9)); 
                    } else {
                        setBackground(r % 2 == 0 ? Color.WHITE : new Color(0xF3F0FA));
                    }
                }
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
            int roleFilterIdx = cbRoleFilter.getSelectedIndex();

            RowFilter<DefaultTableModel, Integer> rfRole = switch (roleFilterIdx) {
                case 1 -> RowFilter.regexFilter("(?i)^Quản lý$", COL_CHUCVU);
                case 2 -> RowFilter.regexFilter("(?i)^Nhân viên$", COL_CHUCVU);
                default -> null;
            };
            RowFilter<DefaultTableModel, Integer> rfSearch = kw.isEmpty()
                    ? null
                    : RowFilter.orFilter(java.util.List.of(
                            RowFilter.regexFilter("(?i)" + Pattern.quote(kw), COL_MA),
                            RowFilter.regexFilter("(?i)" + Pattern.quote(kw), COL_TEN),
                            RowFilter.regexFilter("(?i)" + Pattern.quote(kw), COL_SDT),
                            RowFilter.regexFilter("(?i)" + Pattern.quote(kw), COL_EMAIL)));

            if (rfRole != null && rfSearch != null) {
                sorter.setRowFilter(RowFilter.andFilter(java.util.List.of(rfRole, rfSearch)));
            } else if (rfRole != null) {
                sorter.setRowFilter(rfRole);
            } else if (rfSearch != null) {
                sorter.setRowFilter(rfSearch);
            } else {
                sorter.setRowFilter(null);
            }
        };
        cbRoleFilter.addActionListener(e -> applyFilter.run());
        btnSearch.addActionListener(e -> applyFilter.run());
        tfSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { applyFilter.run(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { applyFilter.run(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { applyFilter.run(); }
        });

        btnRefresh.addActionListener(e -> {
            revealedRows.clear(); sorter.setRowFilter(null); tfSearch.setText("");
            cbRoleFilter.setSelectedIndex(0);
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

    
    private void loadEmployees() {
        tableModel.setRowCount(0);
        revealedRows.clear();
        try {
            List<EmployeeDTO> list = empBUS.getAllEmployees();
            System.out.println("Loaded employees: " + (list != null ? list.size() : "null"));
            if (list == null || list.isEmpty()) return;

            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            for (EmployeeDTO e : list) {
                String ngay = e.getHireDate() != null ? e.getHireDate().format(fmt) : "";
                tableModel.addRow(new Object[]{
                    e.getCode(), e.getFullName(), roleName(e.getRoleId()),
                    e.getPhone(), e.getEmail(), ngay,
                    e.getPasswordHash() != null ? e.getPasswordHash() : "",
                    e.getId()
                });
                if (e.getPhotoPath() != null && !e.getPhotoPath().isEmpty()) {
                    photoPathMap.put(e.getCode(), e.getPhotoPath());
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    void fillDetail(int modelRow) {
        selectedModelRow = modelRow;
        btnSuaNV.setEnabled(true);
        btnXoaNV.setEnabled(true);
        lbName.setText(tableModel.getValueAt(modelRow, COL_TEN).toString());
        lbRole.setText(tableModel.getValueAt(modelRow, COL_CHUCVU).toString());
        lbMaNV.setText(tableModel.getValueAt(modelRow, COL_MA).toString());
        lbSdt.setText(tableModel.getValueAt(modelRow, COL_SDT).toString());
        lbEmail.setText(tableModel.getValueAt(modelRow, COL_EMAIL).toString());
        lbNgayTG.setText(tableModel.getValueAt(modelRow, COL_NGAY).toString());
        
        
        int empId = (Integer) tableModel.getValueAt(modelRow, COL_ID);
        try {
            EmployeeDTO emp = empBUS.getEmployeeById(empId);
            if (emp != null && emp.getSalary() != null) {
                lbSalary.setText(String.format("%,.0f đ", emp.getSalary().doubleValue()));
            } else {
                lbSalary.setText("Chưa cập nhật");
            }
        } catch (Exception e) {
            System.err.println("Lỗi lấy lương nhân viên: " + e.getMessage());
            lbSalary.setText("Lỗi");
        }
        
        String ma = tableModel.getValueAt(modelRow, COL_MA).toString();
        String path = photoPathMap.get(ma);
        avatarImage = null;
        if (path != null) {
            try { avatarImage = ImageIO.read(new File(path)).getScaledInstance(90, 90, Image.SCALE_SMOOTH); }
            catch (Exception ignored) {}
        }
        
        if (avatarImage == null) {
            for (String ext : new String[]{".jpg", ".jpeg", ".png"}) {
                File f = new File("img/employees/" + ma + ext);
                if (f.exists()) {
                    try { avatarImage = ImageIO.read(f).getScaledInstance(90, 90, Image.SCALE_SMOOTH); break; }
                    catch (Exception ignored) {}
                }
            }
        }
        if (avatarBox != null) avatarBox.repaint();
    }

    String generateMaNV() {
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

    private void showAddDialog() { NhanVienEmployeeDialog.show(this, null, -1); }
    private void showEditDialog(int modelRow) { NhanVienEmployeeDialog.show(this, modelRow, modelRow); }


    JButton makeAppBtn(String text) {
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
