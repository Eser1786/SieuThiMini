package GUI.NhapKho;

import BUS.PurchaseInvoicesBUS;
import DTO.PurchaseInvoicesDTO;
import DTO.enums.PurchaseInvoicesEnum.PurchaseInvoicesStatus;
import GUI.UIUtils;
import GUI.WrapLayout;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;


class NhapKhoTableCard extends JPanel {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final Color HDR_BG  = new Color(0xAF9FCB);
    private static final Color ROW_ALT = new Color(0xF3F0FA);

    private final NhapKhoPanel parent;
    private final DefaultTableModel model;    private final java.util.List<PurchaseInvoicesDTO> invoiceList = new java.util.ArrayList<>();    private JTextField txtSearch;
    private JComboBox<String> cbStatus;
    private TableRowSorter<DefaultTableModel> sorter;

    NhapKhoTableCard(NhapKhoPanel parent) {
        this.parent = parent;
        setLayout(new BorderLayout(0, 0));
        setBackground(new Color(0xF8F7FF));

        
        String[] cols = {"Mã phiếu", "Ngày nhập", "Nhà cung cấp", "Nhân viên", "Tổng tiền (đ)", "TT Thanh toán", "Trạng thái", "Thao tác"};
        model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        
        JTable table = new JTable(model);
        sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);
        table.setRowHeight(52);
        table.setFont(new Font("Arial", Font.PLAIN, 16));
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(new Color(0xEDE7F6));
        table.setSelectionForeground(Color.BLACK);

        
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 16));
        table.getTableHeader().setBackground(HDR_BG);
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setPreferredSize(new Dimension(0, 44));

        
        DefaultTableCellRenderer altRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val, boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                if (!sel) setBackground(row % 2 == 0 ? Color.WHITE : ROW_ALT);
                setFont(new Font("Arial", Font.PLAIN, 15));
                setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                return this;
            }
        };
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val, boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                if (!sel) setBackground(row % 2 == 0 ? Color.WHITE : ROW_ALT);
                setFont(new Font("Arial", Font.PLAIN, 15));
                setHorizontalAlignment(SwingConstants.CENTER);
                return this;
            }
        };
        DefaultTableCellRenderer payRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val, boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                if (!sel) setBackground(row % 2 == 0 ? Color.WHITE : ROW_ALT);
                setHorizontalAlignment(SwingConstants.CENTER);
                setFont(new Font("Arial", Font.BOLD, 14));
                String v = val == null ? "" : val.toString();
                if (!sel) {
                    if ("Đã thanh toán".equals(v)) setForeground(new Color(0x2E7D32));
                    else                           setForeground(new Color(0xE65100));
                }
                return this;
            }
        };
        DefaultTableCellRenderer statusRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val, boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                if (!sel) setBackground(row % 2 == 0 ? Color.WHITE : ROW_ALT);
                setHorizontalAlignment(SwingConstants.CENTER);
                setFont(new Font("Arial", Font.BOLD, 14));
                String v = val == null ? "" : val.toString();
                if (!sel) switch (v) {
                    case "Đã nhập"      -> setForeground(new Color(0x2E7D32));
                    case "Đã hủy"       -> setForeground(new Color(0xC62828));
                    case "Chờ xác nhận" -> setForeground(new Color(0xE65100));
                    default             -> setForeground(new Color(0x37474F));
                }
                return this;
            }
        };

        for (int i = 0; i < cols.length; i++) {
            if (i == 1 || i == 3) table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
            else if (i == 5)      table.getColumnModel().getColumn(i).setCellRenderer(payRenderer);
            else if (i == 6)      table.getColumnModel().getColumn(i).setCellRenderer(statusRenderer);
            else                  table.getColumnModel().getColumn(i).setCellRenderer(altRenderer);
        }

        
        table.getTableHeader().setReorderingAllowed(false);

        int[] widths = {120, 130, 180, 150, 130, 130, 110, 90};
        for (int i = 0; i < widths.length; i++)
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        
        table.getColumnModel().getColumn(7).setCellRenderer(
            new javax.swing.table.TableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable t, Object val, boolean sel, boolean foc, int row, int col) {
                    JButton btn = new JButton("Chi tiết");
                    btn.setFont(new Font("Arial", Font.BOLD, 12));
                    btn.setBackground(new Color(0x9B8EA8));
                    btn.setForeground(Color.WHITE);
                    btn.setFocusPainted(false);
                    btn.setBorderPainted(false);
                    btn.setOpaque(true);
                    return btn;
                }
            });

        
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int viewRow = table.rowAtPoint(e.getPoint());
                int col     = table.columnAtPoint(e.getPoint());
                if (col == 7 && viewRow >= 0) {
                    int modelRow = table.convertRowIndexToModel(viewRow);
                    if (modelRow < invoiceList.size()) {
                        PurchaseInvoicesDTO inv = invoiceList.get(modelRow);
                        parent.openDetailPopup(inv, SwingUtilities.getWindowAncestor(NhapKhoTableCard.this));
                    }
                }
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder());

        
        JPanel top = new JPanel(new WrapLayout(FlowLayout.LEFT, 8, 4));
        top.setBackground(new Color(0xF8F7FF));
        top.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xCCCCCC), 1),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));

        cbStatus = new JComboBox<>(new String[]{"Tất cả trạng thái", "Chờ xác nhận", "Đã nhập", "Đã hủy", "Chưa thanh toán", "Đã thanh toán"});
        cbStatus.setPreferredSize(new Dimension(200, 36));
        UIUtils.styleComboBox(cbStatus);
        cbStatus.addActionListener(e -> applyFilter());

        JPanel timPanel = new JPanel(new BorderLayout());
        timPanel.setPreferredSize(new Dimension(220, 36));
        timPanel.setBackground(Color.WHITE);
        timPanel.setBorder(BorderFactory.createLineBorder(new Color(0xBBBBBB), 1));

        txtSearch = new JTextField();
        txtSearch.setFont(new Font("Arial", Font.PLAIN, 13));
        txtSearch.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 4));
        txtSearch.setToolTipText("Tìm kiếm mã phiếu, nhà cung cấp, nhân viên...");
        txtSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate (javax.swing.event.DocumentEvent e) { applyFilter(); }
            public void removeUpdate (javax.swing.event.DocumentEvent e) { applyFilter(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { applyFilter(); }
        });

        JButton btnTim = new JButton("Q");
        btnTim.setBorderPainted(false);
        btnTim.setContentAreaFilled(false);
        btnTim.setFocusPainted(false);
        btnTim.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnTim.addActionListener(e -> applyFilter());
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
        timPanel.add(txtSearch, BorderLayout.CENTER);
        timPanel.add(btnTim, BorderLayout.EAST);

        JLabel lbLoc = new JLabel("Trạng thái:");
        lbLoc.setFont(new Font("Arial", Font.PLAIN, 13));
        JLabel lbTim = new JLabel("Tìm kiếm:");
        lbTim.setFont(new Font("Arial", Font.PLAIN, 13));
        JPanel pLoc = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        pLoc.setOpaque(false); pLoc.add(lbLoc); pLoc.add(cbStatus);
        JPanel pTim = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        pTim.setOpaque(false); pTim.add(lbTim); pTim.add(timPanel);

        JButton btnRefresh = new JButton("Làm mới");
        btnRefresh.setFocusPainted(false);
        btnRefresh.setBackground(new Color(0xD9D9D9));
        btnRefresh.setFont(new Font("Arial", Font.BOLD, 13));
        btnRefresh.setBorder(BorderFactory.createEmptyBorder(9, 14, 9, 14));
        btnRefresh.setOpaque(true);
        btnRefresh.setBorderPainted(false);
        btnRefresh.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRefresh.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) { btnRefresh.setBackground(new Color(0xC5B3E6)); }
            public void mouseExited(java.awt.event.MouseEvent e)  { btnRefresh.setBackground(new Color(0xD9D9D9)); }
        });
        btnRefresh.addActionListener(e -> refresh());

        JButton btnNew = new JButton("+ Tạo phiếu nhập");
        btnNew.setFocusPainted(false);
        btnNew.setBackground(new Color(0xD9D9D9));
        btnNew.setFont(new Font("Arial", Font.BOLD, 13));
        btnNew.setBorder(BorderFactory.createEmptyBorder(9, 14, 9, 14));
        btnNew.setOpaque(true);
        btnNew.setBorderPainted(false);
        btnNew.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnNew.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) { btnNew.setBackground(new Color(0xC5B3E6)); }
            public void mouseExited(java.awt.event.MouseEvent e)  { btnNew.setBackground(new Color(0xD9D9D9)); }
        });
        btnNew.addActionListener(e -> parent.openCreatePopup(SwingUtilities.getWindowAncestor(this)));

        JButton btnExportPDF   = GUI.ExportUtils.makeExportButton("Xuất PDF",   new Color(0x7B52AB));
        btnExportPDF.addActionListener(e -> GUI.ExportUtils.xuatPDF(this, model, "Danh sach phieu nhap kho"));

        JButton btnExportExcel = GUI.ExportUtils.makeExportButton("Xuất Excel", new Color(0x2E7D32));
        btnExportExcel.addActionListener(e -> GUI.ExportUtils.xuatCSV(this, model, "Danh sach phieu nhap kho"));

        top.add(pLoc);
        top.add(pTim);
        top.add(btnRefresh);
        top.add(btnExportPDF);
        top.add(btnExportExcel);
        top.add(btnNew);

        
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 12));
        header.setBackground(new Color(0xF8F7FF));
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0xDDDDDD)),
                BorderFactory.createEmptyBorder(0, 20, 0, 20)));
        JPanel bar = new JPanel();
        bar.setPreferredSize(new Dimension(5, 26));
        bar.setBackground(new Color(0x5C4A7F));
        JLabel titleLbl = new JLabel("QUẢN LÝ NHẬP KHO");
        titleLbl.setFont(new Font("Arial", Font.BOLD, 20));
        header.add(bar);
        header.add(Box.createHorizontalStrut(12));
        header.add(titleLbl);

        JPanel north = new JPanel();
        north.setLayout(new BoxLayout(north, BoxLayout.Y_AXIS));
        north.add(header);
        north.add(top);

        add(north, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);

        refresh();
    }

    
    
    
    void refresh() {
        model.setRowCount(0);
        invoiceList.clear();
        try {
            List<PurchaseInvoicesDTO> invoices = new PurchaseInvoicesBUS().getAllPurchaseInvoices();
            if (invoices == null) return;
            for (PurchaseInvoicesDTO inv : invoices) {
                invoiceList.add(inv);
                String date  = inv.getDateIn() != null ? inv.getDateIn().format(FMT) : "";
                String total = inv.getTotalAmount() != null
                        ? String.format("%,.0fđ", inv.getTotalAmount()) : "0đ";
                String pay   = mapPayment(inv.getPaymentStatus());
                String stat  = mapStatus(inv.getStatus());
                model.addRow(new Object[]{
                        inv.getInvoiceCode(),
                        date,
                        inv.getSupplierName()  != null ? inv.getSupplierName()  : "",
                        inv.getEmployeeName()  != null ? inv.getEmployeeName()  : "",
                        total, pay, stat, ""
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        applyFilter();
    }

    private void applyFilter() {
        String kw = txtSearch == null ? "" : txtSearch.getText().trim().toLowerCase();
        String st = cbStatus == null ? "Tất cả trạng thái" : (String) cbStatus.getSelectedItem();

        List<RowFilter<Object, Object>> filters = new java.util.ArrayList<>();
        if (!kw.isEmpty())
            filters.add(RowFilter.regexFilter("(?i)" + java.util.regex.Pattern.quote(kw)));
        if (st != null && !"Tất cả trạng thái".equals(st))
            filters.add(RowFilter.regexFilter("(?i)" + java.util.regex.Pattern.quote(st)));
        sorter.setRowFilter(filters.isEmpty() ? null : RowFilter.andFilter(filters));
    }

    private static String mapPayment(String s) {
        if (s == null) return "Chưa thanh toán";
        return switch (s) {
            case "PAID"    -> "Đã thanh toán";
            case "PARTIAL" -> "Thanh toán một phần";
            default        -> "Chưa thanh toán";
        };
    }

    private static String mapStatus(PurchaseInvoicesStatus s) {
        if (s == null) return "Chờ xác nhận";
        return switch (s) {
            case PENDING   -> "Chờ xác nhận";
            case RECEIVED  -> "Đã nhập";
            case CANCELLED -> "Đã hủy";
            default        -> "Chờ xác nhận";
        };
    }
}
