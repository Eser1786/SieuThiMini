package GUI.DonHang;

import BUS.CustomerBUS;
import BUS.DiscountBUS;
import BUS.EmployeeBUS;
import BUS.ProductBUS;
import BUS.SalesBUS;
import BUS.UserSession;
import DAO.DBConnection;
import DAO.ProductDAO;
import DAO.SalesInvoiceDAO;
import DAO.SalesInvoiceDiscountDAO;
import DAO.SalesInvoiceItemDAO;
import DTO.CustomerDTO;
import DTO.DiscountDTO;
import DTO.EmployeeDTO;
import DTO.ProductDTO;
import DTO.RoleDTO;
import DTO.SaleDTO;
import DTO.SalesInvoiceDTO;
import DTO.SalesInvoiceDiscountDTO;
import DTO.SalesInvoiceItemDTO;
import DTO.enums.SaleEnum.SalePaymentMethod;
import DTO.enums.SaleEnum.SaleStatus;
import GUI.MainPanel;

import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import javax.swing.text.*;


class DonHangCreateCard extends JPanel {

    private final DonHangPanel parent;
    private List<ProductDTO> allProducts = new ArrayList<>();
    private List<CustomerDTO> allCustomers = new ArrayList<>();

    
    private final List<OrderItem> items = new ArrayList<>();
    private String itemFilter = "";

    
    private JPanel listPanel;
    private JScrollPane listScroll;

    
    private JLabel lbSubVal;
    private JLabel lbTotVal;
    private long discAmt = 0L;

    DonHangCreateCard(DonHangPanel parent) {
        this.parent = parent;
        setLayout(new BorderLayout());
        try {
            List<ProductDTO> loaded = new ProductBUS().getAllProducts();
            if (loaded != null && !loaded.isEmpty()) allProducts = loaded;
            else allProducts = getMockProducts();
        } catch (Exception ignored) { allProducts = getMockProducts(); }
        try {
            java.util.ArrayList<CustomerDTO> loadedC = new CustomerBUS().getAllCustomers();
            if (loadedC != null) allCustomers = loadedC;
        } catch (Exception ignored) {}
        buildUI();
    }

    
    private List<ProductDTO> getMockProducts() {
        List<ProductDTO> list = new ArrayList<>();
        String[][] data = {
            { "SP001", "Nuoc F trai K",        "25000" },
            { "SP002", "Thit meo chay",         "17000" },
            { "SP003", "Com nam ca hoi mayo",   "16000" },
            { "SP004", "Mi y sot kem",          "36000" },
            { "SP005", "Pepsi khong calo",      "10000" },
            { "SP006", "Kem si cu la",          "18000" },
            { "SP007", "Banh mi pate",          "12000" },
            { "SP008", "Tra sua tran chau",     "30000" },
        };
        for (String[] d : data) {
            ProductDTO p = new ProductDTO();
            p.setCode(d[0]); p.setName(d[1]);
            p.setSellingPrice(new BigDecimal(d[2]));
            list.add(p);
        }
        return list;
    }

    
    private static JTextField cf() {
        JTextField tf = new JTextField();
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0xCCCCCC), 1),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)));
        return tf;
    }

    private void updateTotals() {
        long sub = 0;
        for (OrderItem it : items) sub += it.unitPrice * it.qty;
        long tot = Math.max(0, sub - discAmt);
        lbSubVal.setText(String.format("%,.0f\u0111", (double) sub));
        lbTotVal.setText(String.format("%,.0f\u0111", (double) tot));
    }

    
    private void rebuildList() {
        listPanel.removeAll();
        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0; g.weightx = 1.0; g.fill = GridBagConstraints.HORIZONTAL;
        g.insets = new Insets(0, 0, 1, 0);
        String fl = itemFilter.trim().toLowerCase();
        int vis = 0;
        for (int i = 0; i < items.size(); i++) {
            OrderItem it = items.get(i);
            boolean match = fl.isEmpty()
                || it.name.toLowerCase().contains(fl)
                || it.code.toLowerCase().contains(fl);
            if (!match) continue;
            g.gridy = vis;
            listPanel.add(buildItemRow(it, i, vis), g);
            vis++;
        }
        GridBagConstraints filler = new GridBagConstraints();
        filler.gridx = 0; filler.gridy = vis;
        filler.weightx = 1.0; filler.weighty = 1.0;
        filler.fill = GridBagConstraints.BOTH;
        listPanel.add(new JLabel(), filler);
        listPanel.revalidate();
        listPanel.repaint();
    }

    
    private JPanel buildItemRow(OrderItem it, int idx, int vis) {
        JPanel row = new JPanel(new GridBagLayout());
        row.setBackground(vis % 2 == 0 ? Color.WHITE : new Color(0xF7F5FF));
        row.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
        GridBagConstraints g = new GridBagConstraints();
        g.gridy = 0; g.anchor = GridBagConstraints.WEST; g.insets = new Insets(0, 0, 0, 8);

        
        JLabel lbCode = new JLabel(it.code);
        lbCode.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lbCode.setForeground(new Color(0x666666));
        lbCode.setPreferredSize(new Dimension(70, 24));
        g.gridx = 0; g.weightx = 0; g.fill = GridBagConstraints.NONE;
        row.add(lbCode, g);

        
        JLabel lbName = new JLabel(it.name);
        lbName.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lbName.setPreferredSize(new Dimension(160, 24));
        g.gridx = 1; g.weightx = 0; g.fill = GridBagConstraints.NONE;
        row.add(lbName, g);

        
        JLabel lbPrice = new JLabel(String.format("%,.0f\u0111", (double) it.unitPrice));
        lbPrice.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lbPrice.setForeground(new Color(0x555555));
        lbPrice.setPreferredSize(new Dimension(90, 24));
        lbPrice.setHorizontalAlignment(SwingConstants.RIGHT);
        g.gridx = 2; g.weightx = 0; g.fill = GridBagConstraints.NONE;
        row.add(lbPrice, g);

        
        JLabel lbSub = new JLabel(String.format("%,.0f\u0111", (double)(it.unitPrice * it.qty)));
        lbSub.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbSub.setForeground(new Color(0x5B4FCC));
        lbSub.setPreferredSize(new Dimension(75, 24));
        lbSub.setHorizontalAlignment(SwingConstants.RIGHT);

        
        SpinnerNumberModel mdl = new SpinnerNumberModel(it.qty, 1, 9999, 1);
        JSpinner spinner = new JSpinner(mdl);
        spinner.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        spinner.setPreferredSize(new Dimension(64, 30));
        JFormattedTextField spinTF = ((JSpinner.DefaultEditor) spinner.getEditor()).getTextField();
        spinTF.setHorizontalAlignment(JTextField.CENTER);
        ((AbstractDocument) spinTF.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int off, String s, AttributeSet a)
                    throws BadLocationException {
                if (s != null && s.matches("\\d+")) super.insertString(fb, off, s, a);
            }
            @Override
            public void replace(FilterBypass fb, int off, int len, String s, AttributeSet a)
                    throws BadLocationException {
                if (s != null && s.matches("\\d*")) super.replace(fb, off, len, s, a);
            }
        });
        spinner.addChangeListener(ev -> {
            it.qty = (Integer) spinner.getValue();
            lbSub.setText(String.format("%,.0f\u0111", (double)(it.unitPrice * it.qty)));
            updateTotals();
        });
        g.gridx = 3; g.insets = new Insets(0, 0, 0, 8);
        row.add(spinner, g);

        g.gridx = 4; g.insets = new Insets(0, 0, 0, 8);
        row.add(lbSub, g);

        
        JButton btnX = new JButton("X");
        btnX.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnX.setBackground(new Color(0xE53935)); btnX.setForeground(Color.WHITE);
        btnX.setFocusPainted(false); btnX.setBorderPainted(false); btnX.setOpaque(true);
        btnX.setPreferredSize(new Dimension(34, 30));
        btnX.setMargin(new Insets(0, 0, 0, 0));
        btnX.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnX.addActionListener(e -> {
            int cf = JOptionPane.showConfirmDialog(
                DonHangCreateCard.this,
                "B\u1ea1n c\u00f3 ch\u1eafc mu\u1ed1n x\u00f3a \u201c" + it.name + "\u201d kh\u1ecfi \u0111\u01a1n?",
                "X\u00e1c nh\u1eadn x\u00f3a",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (cf != JOptionPane.YES_OPTION) return;
            items.remove(idx);
            rebuildList();
            updateTotals();
        });
        g.gridx = 5; g.insets = new Insets(0, 0, 0, 0);
        row.add(btnX, g);

        
        g.gridx = 6; g.weightx = 1.0; g.fill = GridBagConstraints.HORIZONTAL;
        row.add(new JLabel(), g);

        return row;
    }

    
   private void addProductToOrder(long productId, String code, String name, long unitPrice) {

    for (OrderItem it : items) {
        if (it.productId == productId) {
            if (it.qty < 9999) it.qty++;
            rebuildList();
            updateTotals();
            return;
        }
    }

    items.add(new OrderItem(productId, code, name, unitPrice, 1));
    rebuildList();
    updateTotals();
}

    
    private void buildUI() {
        Color pageBg = new Color(0xF0EFF8);
        setBackground(pageBg);

        
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(pageBg);
        header.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0xCCCCCC)),
            BorderFactory.createEmptyBorder(12, 20, 12, 20)));
        JLabel lblTitle = new JLabel("+ T\u1ea1o \u0111\u01a1n h\u00e0ng m\u1edbi");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(new Color(0x333333));
        JButton btnBack = new JButton("\u2190 Quay l\u1ea1i");
        btnBack.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnBack.setBackground(new Color(0x9B8EA8)); btnBack.setForeground(Color.WHITE);
        btnBack.setFocusPainted(false); btnBack.setBorderPainted(false); btnBack.setOpaque(true);
        btnBack.setPreferredSize(new Dimension(160, 36));
        btnBack.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnBack.addActionListener(e -> parent.showCard(DonHangPanel.CARD_TABLE));
        header.add(lblTitle, BorderLayout.WEST);
        header.add(btnBack, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        
        lbSubVal = new JLabel("0\u0111");
        lbTotVal = new JLabel("0\u0111");
        lbSubVal.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lbTotVal.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lbTotVal.setForeground(new Color(0x5B4FCC));

        
        listPanel = new JPanel(new GridBagLayout());
        listPanel.setBackground(Color.WHITE);

        JPanel listHeader = new JPanel(new GridBagLayout());
        listHeader.setBackground(new Color(0xD1C4E9));
        listHeader.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
        GridBagConstraints lhg = new GridBagConstraints();
        lhg.gridy = 0; lhg.anchor = GridBagConstraints.WEST; lhg.insets = new Insets(0, 0, 0, 8);
        String[] hdrTxt = { "M\u00e3 SP", "T\u00ean s\u1ea3n ph\u1ea9m", "\u0110\u01a1n gi\u00e1",
                            "S\u1ed1 l\u01b0\u1ee3ng", "Th\u00e0nh ti\u1ec1n", "", "" };
        int[]    hdrW   = { 70, 160, 90, 64, 75, 34, 0 };
        double[] hdrWx  = { 0, 0, 0, 0, 0, 0, 1.0 };
        for (int i = 0; i < hdrTxt.length; i++) {
            JLabel h = new JLabel(hdrTxt[i]);
            h.setFont(new Font("Segoe UI", Font.BOLD, 13));
            h.setForeground(new Color(0x333333));
            if (hdrW[i] > 0) h.setPreferredSize(new Dimension(hdrW[i], 20));
            if (i == 2 || i == 4) h.setHorizontalAlignment(SwingConstants.RIGHT);
            if (i == 3) h.setHorizontalAlignment(SwingConstants.CENTER);
            lhg.gridx = i; lhg.weightx = hdrWx[i];
            lhg.fill = (i == 6 ? GridBagConstraints.HORIZONTAL : GridBagConstraints.NONE);
            listHeader.add(h, lhg);
        }

        rebuildList();
        listScroll = new JScrollPane(listPanel);
        listScroll.setBorder(BorderFactory.createLineBorder(new Color(0xDDDDDD)));
        listScroll.setPreferredSize(new Dimension(0, 4 * 52));
        listScroll.getVerticalScrollBar().setUnitIncrement(16);

        JPanel listBox = new JPanel(new BorderLayout());
        listBox.setBackground(Color.WHITE);
        listBox.setBorder(BorderFactory.createLineBorder(new Color(0xDDDDDD)));
        listBox.add(listHeader, BorderLayout.NORTH);
        listBox.add(listScroll, BorderLayout.CENTER);

        
        final String HINT = "T\u00ecm ki\u1ebfm s\u1ea3n ph\u1ea9m \u0111\u00e3 th\u00eam v\u00e0o \u0111\u01a1n...";
        JTextField tfSearch = cf();
        tfSearch.setText(HINT);
        tfSearch.setForeground(Color.GRAY);
        tfSearch.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) {
                if (tfSearch.getText().equals(HINT)) { tfSearch.setText(""); tfSearch.setForeground(Color.BLACK); }
            }
            @Override public void focusLost(FocusEvent e) {
                if (tfSearch.getText().trim().isEmpty()) { tfSearch.setText(HINT); tfSearch.setForeground(Color.GRAY); }
            }
        });
        tfSearch.getDocument().addDocumentListener(new DocumentListener() {
            void upd() {
                String t = tfSearch.getText();
                itemFilter = t.equals(HINT) ? "" : t;
                rebuildList();
            }
            @Override public void insertUpdate(DocumentEvent e) { upd(); }
            @Override public void removeUpdate(DocumentEvent e) { upd(); }
            @Override public void changedUpdate(DocumentEvent e) { upd(); }
        });

        JButton btnBrowse = new JButton("Duy\u1ec7t s\u1ea3n ph\u1ea9m");
        btnBrowse.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnBrowse.setBackground(new Color(0x5B4FCC)); btnBrowse.setForeground(Color.WHITE);
        btnBrowse.setFocusPainted(false); btnBrowse.setBorderPainted(false); btnBrowse.setOpaque(true);
        btnBrowse.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnBrowse.setPreferredSize(new Dimension(170, 36));
        btnBrowse.addActionListener(e -> openBrowseDialog());

        JPanel searchRow = new JPanel(new BorderLayout(8, 0));
        searchRow.setBackground(Color.WHITE);
        searchRow.add(tfSearch, BorderLayout.CENTER);
        searchRow.add(btnBrowse, BorderLayout.EAST);

        
        JTextArea taNotes = new JTextArea(3, 20);
        taNotes.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        taNotes.setLineWrap(true); taNotes.setWrapStyleWord(true);
        taNotes.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
        JScrollPane notesScroll = new JScrollPane(taNotes);
        notesScroll.setBorder(BorderFactory.createLineBorder(new Color(0xCCCCCC)));

        
        java.util.ArrayList<DiscountDTO> activeDiscounts = new java.util.ArrayList<>();
        JComboBox<String> cbMaKM = new JComboBox<>();
        cbMaKM.addItem("(Không áp dụng)");
        try {
            java.util.ArrayList<DiscountDTO> allD = new DiscountBUS().getAllDiscounts();
            if (allD != null) {
                for (DiscountDTO d : allD) {
                    if (d.getStatus() != null && "ACTIVE".equals(d.getStatus().name())) {
                        activeDiscounts.add(d);
                        cbMaKM.addItem(d.getDiscountCode() + " - " + d.getName());
                    }
                }
            }
        } catch (Exception ignored) {}
        cbMaKM.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cbMaKM.setPreferredSize(new Dimension(260, 32));
        JLabel lbDiscStatus = new JLabel("  ");
        lbDiscStatus.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        cbMaKM.addActionListener(e -> {
            int idx = cbMaKM.getSelectedIndex();
            if (idx <= 0 || idx - 1 >= activeDiscounts.size()) {
                discAmt = 0; lbDiscStatus.setText("  "); updateTotals(); return;
            }
            DiscountDTO d = activeDiscounts.get(idx - 1);
            long sub = 0; for (OrderItem it : items) sub += (long)(it.unitPrice * it.qty);
            if (d.getMinOrderAmount() != null && sub < d.getMinOrderAmount().longValue()) {
                discAmt = 0;
                lbDiscStatus.setText("⚠ Đơn tối thiểu: " + String.format("%,.0fđ", d.getMinOrderAmount().doubleValue()));
                lbDiscStatus.setForeground(new Color(0xE65100));
                updateTotals(); return;
            }
            if ("PERCENT".equals(d.getDiscountType().name())) {
                discAmt = (long)(sub * d.getValue().doubleValue() / 100.0);
                lbDiscStatus.setText("Giảm " + d.getValue().stripTrailingZeros().toPlainString()
                        + "% → -" + String.format("%,.0fđ", (double) discAmt));
            } else {
                discAmt = Math.min(d.getValue().longValue(), sub);
                lbDiscStatus.setText("Giảm cố định -" + String.format("%,.0fđ", (double) discAmt));
            }
            lbDiscStatus.setForeground(new Color(0x2E7D32));
            updateTotals();
        });

        JPanel discRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        discRow.setBackground(Color.WHITE);
        JLabel lbKMlbl = new JLabel("Mã khuyến mãi:");
        lbKMlbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        discRow.add(lbKMlbl); discRow.add(cbMaKM);
        JPanel discInfoRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 2));
        discInfoRow.setBackground(Color.WHITE);
        discInfoRow.add(lbDiscStatus);

        
        JPanel subRow = new JPanel(new BorderLayout());
        subRow.setBackground(Color.WHITE);
        subRow.setBorder(BorderFactory.createEmptyBorder(4, 0, 2, 0));
        JLabel lbSubLbl = new JLabel("T\u1ea1m t\u00ednh:");
        lbSubLbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lbSubVal.setHorizontalAlignment(SwingConstants.RIGHT);
        subRow.add(lbSubLbl, BorderLayout.WEST); subRow.add(lbSubVal, BorderLayout.EAST);

        JPanel totRow = new JPanel(new BorderLayout());
        totRow.setBackground(Color.WHITE);
        totRow.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(0xDDDDDD)),
            BorderFactory.createEmptyBorder(8, 0, 0, 0)));
        JLabel lbTotLbl = new JLabel("T\u1ed5ng c\u1ed9ng:");
        lbTotLbl.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lbTotVal.setHorizontalAlignment(SwingConstants.RIGHT);
        totRow.add(lbTotLbl, BorderLayout.WEST); totRow.add(lbTotVal, BorderLayout.EAST);

        
        JPanel leftContent = new JPanel(new GridBagLayout());
        leftContent.setBackground(Color.WHITE);
        leftContent.setBorder(BorderFactory.createEmptyBorder(18, 22, 18, 22));
        GridBagConstraints lc = new GridBagConstraints();
        lc.gridx = 0; lc.weightx = 1.0; lc.fill = GridBagConstraints.HORIZONTAL;

        JLabel leftTitle = new JLabel("Chi ti\u1ebft \u0111\u01a1n h\u00e0ng");
        leftTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        leftTitle.setForeground(new Color(0x222222));
        lc.gridy = 0; lc.insets = new Insets(0,0,10,0); leftContent.add(leftTitle, lc);
        lc.gridy = 1; lc.insets = new Insets(0,0,10,0); leftContent.add(new JSeparator(), lc);
        lc.gridy = 2; lc.insets = new Insets(0,0,8,0);  leftContent.add(searchRow, lc);
        lc.gridy = 3; lc.insets = new Insets(0,0,10,0); leftContent.add(listBox, lc);
        JLabel lbNotesLbl = new JLabel("Ghi ch\u00fa \u0111\u01a1n h\u00e0ng:");
        lbNotesLbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbNotesLbl.setForeground(new Color(0x444444));
        lc.gridy = 4; lc.insets = new Insets(14,0,4,0); leftContent.add(lbNotesLbl, lc);
        lc.gridy = 5; lc.insets = new Insets(0,0,10,0); leftContent.add(notesScroll, lc);
        lc.gridy = 6; lc.insets = new Insets(0,0,10,0); leftContent.add(new JSeparator(), lc);
        lc.gridy = 7; lc.insets = new Insets(0,0,2,0);  leftContent.add(discRow, lc);
        lc.gridy = 8; lc.insets = new Insets(0,0,6,0);  leftContent.add(discInfoRow, lc);
        lc.gridy = 9; lc.insets = new Insets(4,0,4,0);  leftContent.add(subRow, lc);
        lc.gridy = 10; lc.insets = new Insets(0,0,0,0); leftContent.add(totRow, lc);

        JPanel leftCard = new JPanel(new BorderLayout());
        leftCard.setBackground(Color.WHITE);
        leftCard.setBorder(BorderFactory.createLineBorder(new Color(0xDDDDDD), 1));
        leftCard.add(leftContent, BorderLayout.CENTER);

        
        JTextField tfTenND  = cf();
        JTextField tfSdt    = cf();
        JTextField tfDiaChi = cf();

        
        JComboBox<String> cbKhachHang = new JComboBox<>();
        cbKhachHang.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cbKhachHang.addItem("-- Ch\u1ecdn kh\u00e1ch h\u00e0ng --");
        for (CustomerDTO c : allCustomers) {
            String label = (c.getCode() != null ? c.getCode() : "") + " - "
                         + (c.getFullName() != null ? c.getFullName() : "")
                         + (c.getPhone() != null ? " (" + c.getPhone() + ")" : "");
            cbKhachHang.addItem(label);
        }
        cbKhachHang.addActionListener(e -> {
            int idx = cbKhachHang.getSelectedIndex();
            if (idx <= 0 || idx - 1 >= allCustomers.size()) return;
            CustomerDTO sel = allCustomers.get(idx - 1);
            tfTenND.setText(sel.getFullName() != null ? sel.getFullName() : "");
            tfSdt.setText(sel.getPhone() != null ? sel.getPhone() : "");
            tfDiaChi.setText(sel.getAddress() != null ? sel.getAddress() : "");
        });

        JButton btnTaoKH = new JButton("+ T\u1ea1o kh\u00e1ch m\u1edbi");
        btnTaoKH.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnTaoKH.setBackground(new Color(0x2E7D32)); btnTaoKH.setForeground(Color.WHITE);
        btnTaoKH.setFocusPainted(false); btnTaoKH.setBorderPainted(false); btnTaoKH.setOpaque(true);
        btnTaoKH.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnTaoKH.setVisible(false);
        btnTaoKH.addActionListener(e -> {

    Component c = DonHangCreateCard.this;

    while (c != null && !(c instanceof MainPanel)) {
        c = c.getParent();
    }

    if (c instanceof MainPanel mainPanel) {
        mainPanel.navigateToKhachHangAndAdd();
    } else {
        System.out.println("Không tìm thấy MainPanel");
    }

});
        JPanel custCard = makeRightCard("Kh\u00e1ch h\u00e0ng");
        addFieldToCard(custCard, "Ch\u1ecdn kh\u00e1ch:", cbKhachHang);
        {
            int nr = (Integer) custCard.getClientProperty("nr");
            GridBagConstraints gk = new GridBagConstraints();
            gk.gridx = 0; gk.gridy = nr; gk.weightx = 1.0;
            gk.fill = GridBagConstraints.NONE; gk.anchor = GridBagConstraints.EAST;
            gk.insets = new Insets(0, 0, 10, 0);
            custCard.add(btnTaoKH, gk);
            custCard.putClientProperty("nr", nr + 1);
        }
        addFieldToCard(custCard, "T\u00ean ng\u01b0\u1eddi mua:", tfTenND);
        addFieldToCard(custCard, "S\u1ed1 \u0111i\u1ec7n tho\u1ea1i:", tfSdt);
        addFieldToCard(custCard, "\u0110\u1ecba ch\u1ec9 giao h\u00e0ng:", tfDiaChi);

        JComboBox<String> cbNhanVien = new JComboBox<>();
cbNhanVien.setFont(new Font("Segoe UI", Font.PLAIN, 14));

EmployeeDTO currentUser = UserSession.getCurrentUser();
String roleName = "";

try {
    if (currentUser != null) {
        RoleDTO role = new EmployeeBUS().getRole((long) currentUser.getId());
        if (role != null) roleName = role.getName();
    }
} catch (Exception e) {
    e.printStackTrace();
}

try {
    if ("ADMIN".equals(roleName)) {
        // ADMIN → hiển thị tất cả nhân viên
        cbNhanVien.addItem("-- Chọn nhân viên --");
        List<EmployeeDTO> emps = new EmployeeBUS().getAllEmployees();
        if (emps != null)
            for (EmployeeDTO e : emps)
                cbNhanVien.addItem(e.getCode() + " - " + e.getFullName());
    } 
    else {
        // User thường → chỉ hiện chính mình
        if (currentUser != null) {
            cbNhanVien.addItem(currentUser.getCode() + " - " + currentUser.getFullName());
            cbNhanVien.setEditable(false);
        }
    }
} catch (Exception ignored) {}

JPanel empCard = makeRightCard("Nhân viên tiếp nhận");
addFieldToCard(empCard, "Nhân viên:", cbNhanVien);


        JComboBox<String> cbHinhThuc = new JComboBox<>(new String[]{
            "Thanh to\u00e1n khi nh\u1eadn h\u00e0ng", "Chuy\u1ec3n kho\u1ea3n", "Th\u1ebb t\u00edn d\u1ee5ng" });
        cbHinhThuc.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JLabel lbTime = new JLabel(LocalDateTime.now()
            .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
        lbTime.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lbTime.setForeground(new Color(0x444444));
        JPanel payCard = makeRightCard("Thanh to\u00e1n & Tr\u1ea1ng th\u00e1i");
        addFieldToCard(payCard, "Ph\u01b0\u01a1ng th\u1ee9c:", cbHinhThuc);
        addFieldToCard(payCard, "Th\u1eddi gian t\u1ea1o:", lbTime);

        JPanel rightCol = new JPanel(new GridBagLayout());
        rightCol.setBackground(pageBg);
        GridBagConstraints rc = new GridBagConstraints();
        rc.gridx = 0; rc.weightx = 1.0; rc.fill = GridBagConstraints.HORIZONTAL;
        rc.anchor = GridBagConstraints.NORTH;
        rc.gridy = 0; rc.insets = new Insets(0,0,14,0); rightCol.add(custCard, rc);
        rc.gridy = 1; rc.insets = new Insets(0,0,14,0); rightCol.add(empCard, rc);
        rc.gridy = 2; rc.insets = new Insets(0,0,0,0);  rightCol.add(payCard, rc);
        rc.gridy = 3; rc.weighty = 1.0; rc.fill = GridBagConstraints.BOTH;
        rightCol.add(new JLabel(), rc);

        
        JPanel twoCol = new JPanel(new GridBagLayout());
        twoCol.setBackground(pageBg);
        twoCol.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints tc = new GridBagConstraints();
        tc.gridy = 0; tc.weighty = 1.0;
        tc.gridx = 0; tc.weightx = 0.62; tc.fill = GridBagConstraints.BOTH;
        tc.insets = new Insets(0,0,0,14); twoCol.add(leftCard, tc);
        tc.gridx = 1; tc.weightx = 0.38; tc.fill = GridBagConstraints.HORIZONTAL;
        tc.anchor = GridBagConstraints.NORTH; tc.weighty = 0;
        tc.insets = new Insets(0,0,0,0); twoCol.add(rightCol, tc);

        JScrollPane bodyScroll = new JScrollPane(twoCol);
        bodyScroll.setBorder(BorderFactory.createEmptyBorder());
        bodyScroll.getVerticalScrollBar().setUnitIncrement(16);
        add(bodyScroll, BorderLayout.CENTER);

        
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 12));
        footer.setBackground(pageBg);
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(0xCCCCCC)));
        JButton btnLuu = DonHangPanel.makeFootBtn("L\u01b0u \u0111\u01a1n h\u00e0ng", new Color(0x5B4FCC));
        JButton btnHuy = DonHangPanel.makeFootBtn("H\u1ee7y b\u1ecf", new Color(0xB83434));

        
        btnHuy.addActionListener(e -> {
            boolean dirty = !items.isEmpty()
                || !tfTenND.getText().trim().isEmpty()
                || !tfSdt.getText().trim().isEmpty()
                || !tfDiaChi.getText().trim().isEmpty();
            if (dirty) {
                int cf = JOptionPane.showConfirmDialog(this,
                    "B\u1ea1n c\u00f3 ch\u1eafc mu\u1ed1n h\u1ee7y? Th\u00f4ng tin \u0111\u00e3 nh\u1eadp s\u1ebd m\u1ea5t.",
                    "X\u00e1c nh\u1eadn h\u1ee7y",
                    JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (cf != JOptionPane.YES_OPTION) return;
            }
            parent.showCard(DonHangPanel.CARD_TABLE);
        });

        btnLuu.addActionListener(e -> {
            String ten    = tfTenND.getText().trim();
            String sdt    = tfSdt.getText().trim();
            String diaChi = tfDiaChi.getText().trim();
            java.util.List<String> errors = new java.util.ArrayList<>();
            JComponent firstBad = null;
            if (ten.isEmpty()) {
                errors.add("\u2022 T\u00ean ng\u01b0\u1eddi mua kh\u00f4ng \u0111\u01b0\u1ee3c \u0111\u1ec3 tr\u1ed1ng."); firstBad = tfTenND;
            } else if (!ten.matches("[\\p{L} .'-]+")) {
                errors.add("\u2022 T\u00ean ng\u01b0\u1eddi mua kh\u00f4ng h\u1ee3p l\u1ec7."); firstBad = tfTenND;
            }
            if (!sdt.matches("0[0-9]{9}")) {
                errors.add("\u2022 S\u0110T ph\u1ea3i g\u1ed3m 10 ch\u1eef s\u1ed1, b\u1eaft \u0111\u1ea7u b\u1eb1ng 0.");
                if (firstBad == null) firstBad = tfSdt;
            }
            if (diaChi.isEmpty()) {
                errors.add("\u2022 \u0110\u1ecba ch\u1ec9 giao h\u00e0ng kh\u00f4ng \u0111\u01b0\u1ee3c \u0111\u1ec3 tr\u1ed1ng.");
                if (firstBad == null) firstBad = tfDiaChi;
            }
            if (cbNhanVien.getSelectedIndex() == 0)
                errors.add("\u2022 Vui l\u00f2ng ch\u1ecdn nh\u00e2n vi\u00ean ti\u1ebfp nh\u1eadn.");
            if (items.isEmpty())
                errors.add("\u2022 Vui l\u00f2ng th\u00eam \u00edt nh\u1ea5t m\u1ed9t s\u1ea3n ph\u1ea9m.");
            if (!errors.isEmpty()) {
                JOptionPane.showMessageDialog(this, String.join("\n", errors),
                    "L\u1ed7i nh\u1eadp li\u1ec7u", JOptionPane.WARNING_MESSAGE);
                if (firstBad != null) firstBad.requestFocus(); return;
            }
            long tongCong = 0;
            for (OrderItem it : items) tongCong += it.unitPrice * it.qty;
            tongCong = Math.max(0, tongCong - discAmt);
            int totalQty = 0; for (OrderItem it : items) totalQty += it.qty;
            
            
            SalesBUS salesBUS = new SalesBUS();
            String maDon = salesBUS.generateSaleCode();
            
            String nhanVien = cbNhanVien.getSelectedItem().toString();
            
            
            SaleDTO sale = new SaleDTO();
            sale.setSaleCode(maDon);
            sale.setSaleDate(java.time.LocalDate.now());
            
            
            CustomerDTO customer = findOrCreateCustomer(ten, sdt, diaChi);
            if (customer != null) {
                sale.setCustomerID(customer.getId());
                sale.setCustomerName(customer.getFullName());
            }
            
            
            EmployeeDTO employee = findEmployeeByName(nhanVien);
            if (employee != null) {
                sale.setEmployeeID(employee.getId());
                sale.setEmployeeName(employee.getFullName());
            }
            
            sale.setSubTotal(BigDecimal.valueOf(tongCong + discAmt));
            sale.setDiscountAmount(BigDecimal.valueOf(discAmt));
            sale.setTotalAmount(BigDecimal.valueOf(tongCong));
            sale.setTotalQuantity(totalQty);
            sale.setSaleStatus(SaleStatus.PENDING);
            sale.setPaymentMethod(SalePaymentMethod.CASH); 
            
            
            StringBuilder itemsNote = new StringBuilder();
            for (OrderItem it : items) {
                itemsNote.append(it.code).append("|").append(it.name).append("|").append(it.unitPrice).append("|").append(it.qty).append(";");
            }
            String userNote = taNotes.getText().trim();
            if (!userNote.isEmpty()) {
                itemsNote.append("NOTE:").append(userNote);
            }
            sale.setNote(itemsNote.toString());
            
            
            boolean saved = salesBUS.addSale(sale);
            if(saved){

    
    SaleDTO savedSale = salesBUS.getSaleByCode(maDon);
    Long saleId = savedSale.getSaleID();
    
    SalesInvoiceDAO invoiceDAO = new SalesInvoiceDAO();

SalesInvoiceDTO invoice = new SalesInvoiceDTO();

invoice.setInvoiceCode(invoiceDAO.generateInvoiceCode());
invoice.setSaleId(saleId);
invoice.setSubtotal(BigDecimal.valueOf(tongCong + discAmt));
invoice.setDiscountAmount(BigDecimal.valueOf(discAmt));
invoice.setTaxAmount(BigDecimal.ZERO);
invoice.setTotalAmount(BigDecimal.valueOf(tongCong));
invoice.setStatus("PENDING");

Long invoiceId = invoiceDAO.addSalesInvoice(invoice); 
if(cbMaKM.getSelectedIndex() > 0){

    DiscountDTO d = activeDiscounts.get(cbMaKM.getSelectedIndex() - 1);

    SalesInvoiceDiscountDAO disDAO = new SalesInvoiceDiscountDAO();

    SalesInvoiceDiscountDTO sid = new SalesInvoiceDiscountDTO();

    sid.setInvoiceId(invoiceId);
    sid.setDiscountId((long) d.getId());

    disDAO.insert(sid);
}          
    SalesInvoiceItemDAO itemDAO = new SalesInvoiceItemDAO();
    

    for(OrderItem it : items){

    SalesInvoiceItemDTO item = new SalesInvoiceItemDTO();
    ProductDAO productDAO = new ProductDAO();
    item.setInvoiceId(invoiceId);
    item.setProductId(it.productId);
    item.setQuantity(it.qty);
    item.setUnitPrice(BigDecimal.valueOf(it.unitPrice));
    item.setSubtotal(BigDecimal.valueOf(it.unitPrice * it.qty));

    itemDAO.insert(item);
     productDAO.decreaseStock(it.productId, it.qty);
}

    
    if (customer != null) {
        customer.setLastPurchaseAt(java.time.LocalDateTime.now());
        try {
            CustomerBUS customerBUS = new CustomerBUS();
            customerBUS.updateCustomer(customer);
        } catch (Exception ex) {
            System.err.println("Lỗi cập nhật last_purchase cho khách hàng: " + ex.getMessage());
        }
    }
}
            if (!saved) {
                JOptionPane.showMessageDialog(this, "Lỗi khi lưu đơn hàng!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            
            JOptionPane.showMessageDialog(this, "Đã tạo đơn hàng " + maDon + " thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            
            
            parent.refreshTable();
            parent.showCard(DonHangPanel.CARD_TABLE);
            items.clear(); discAmt = 0; rebuildList(); updateTotals(); cbMaKM.setSelectedIndex(0);
            parent.showCard(DonHangPanel.CARD_TABLE);
        });
        footer.add(btnLuu); footer.add(btnHuy);
        add(footer, BorderLayout.SOUTH);
    }

    
    private void openBrowseDialog() {
        Frame owner = null;
        Window w = SwingUtilities.getWindowAncestor(this);
        if (w instanceof Frame f) owner = f;
        JDialog dlg = new JDialog(owner, "Ch\u1ecdn s\u1ea3n ph\u1ea9m", true);
        dlg.setSize(580, 500); dlg.setLocationRelativeTo(this);

        JTextField dlgSearch = cf();

        DefaultTableModel dlgModel = new DefaultTableModel(
                new String[]{ "M\u00e3 SP", "T\u00ean s\u1ea3n ph\u1ea9m", "\u0110\u01a1n gi\u00e1" }, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        for (ProductDTO p : allProducts) {
            long price = p.getSellingPrice() != null ? p.getSellingPrice().longValue() : 0L;
            dlgModel.addRow(new Object[]{
                p.getCode(),
                p.getName() != null ? p.getName() : "(Kh\u00f4ng t\u00ean)",
                String.format("%,.0f\u0111", (double) price)
            });
        }

        JTable dlgTable = new JTable(dlgModel);
        dlgTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        dlgTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        dlgTable.getTableHeader().setBackground(new Color(0xD1C4E9));
        dlgTable.getTableHeader().setForeground(new Color(0x222222));
        dlgTable.getTableHeader().setReorderingAllowed(false);
        dlgTable.setRowHeight(38);
        dlgTable.setShowVerticalLines(false);
        dlgTable.setGridColor(new Color(0xEEEEEE));
        dlgTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        dlgTable.getColumnModel().getColumn(0).setPreferredWidth(80);
        dlgTable.getColumnModel().getColumn(1).setPreferredWidth(240);
        dlgTable.getColumnModel().getColumn(2).setPreferredWidth(120);

        DefaultTableCellRenderer priceR = new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object v,
                    boolean sel, boolean foc, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                if (!sel) c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(0xF7F5FF));
                return c;
            }
        };
        priceR.setHorizontalAlignment(SwingConstants.RIGHT);
        DefaultTableCellRenderer altR = new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object v,
                    boolean sel, boolean foc, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                if (!sel) c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(0xF7F5FF));
                return c;
            }
        };
        dlgTable.getColumnModel().getColumn(0).setCellRenderer(altR);
        dlgTable.getColumnModel().getColumn(1).setCellRenderer(altR);
        dlgTable.getColumnModel().getColumn(2).setCellRenderer(priceR);

        dlgSearch.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { filter(); }
            @Override public void removeUpdate(DocumentEvent e) { filter(); }
            @Override public void changedUpdate(DocumentEvent e) { filter(); }
            void filter() {
                String q = dlgSearch.getText().trim().toLowerCase();
                dlgModel.setRowCount(0);
                for (ProductDTO p : allProducts) {
                    String hay = (p.getName() == null ? "" : p.getName().toLowerCase())
                               + p.getCode().toLowerCase();
                    if (q.isEmpty() || hay.contains(q)) {
                        long price = p.getSellingPrice() != null ? p.getSellingPrice().longValue() : 0L;
                        dlgModel.addRow(new Object[]{
                            p.getCode(),
                            p.getName() != null ? p.getName() : "(Kh\u00f4ng t\u00ean)",
                            String.format("%,.0f\u0111", (double) price)
                        });
                    }
                }
            }
        });

        JLabel lbStatus = new JLabel(" ");
        lbStatus.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lbStatus.setForeground(new Color(0x2E7D32));

        
        dlgTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) pickRows(dlgTable, lbStatus);
            }
        });

        JButton btnAdd = new JButton("Th\u00eam v\u00e0o \u0111\u01a1n");
        btnAdd.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnAdd.setBackground(new Color(0x5B4FCC)); btnAdd.setForeground(Color.WHITE);
        btnAdd.setFocusPainted(false); btnAdd.setBorderPainted(false); btnAdd.setOpaque(true);
        btnAdd.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAdd.addActionListener(e -> pickRows(dlgTable, lbStatus));

        JButton btnClose = new JButton("\u0110\u00f3ng");
        btnClose.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnClose.addActionListener(e -> dlg.dispose());

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        btnRow.setBackground(Color.WHITE);
        btnRow.add(btnClose); btnRow.add(btnAdd);

        JLabel hint = new JLabel("Gi\u1eef Ctrl ho\u1eb7c Shift \u0111\u1ec3 ch\u1ecdn nhi\u1ec1u s\u1ea3n ph\u1ea9m");
        hint.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        hint.setForeground(new Color(0x888888));

        JPanel south = new JPanel(new BorderLayout(0, 4));
        south.setBackground(Color.WHITE);
        south.add(hint, BorderLayout.NORTH);
        JPanel statusBtnRow = new JPanel(new BorderLayout());
        statusBtnRow.setBackground(Color.WHITE);
        statusBtnRow.add(lbStatus, BorderLayout.WEST);
        statusBtnRow.add(btnRow, BorderLayout.EAST);
        south.add(statusBtnRow, BorderLayout.SOUTH);

        JPanel dlgPanel = new JPanel(new BorderLayout(0, 10));
        dlgPanel.setBorder(BorderFactory.createEmptyBorder(16, 16, 14, 16));
        dlgPanel.setBackground(Color.WHITE);
        dlgPanel.add(dlgSearch, BorderLayout.NORTH);
        dlgPanel.add(new JScrollPane(dlgTable), BorderLayout.CENTER);
        dlgPanel.add(south, BorderLayout.SOUTH);
        dlg.setContentPane(dlgPanel);
        dlg.setVisible(true);
    }

    
   private void pickRows(JTable dlgTable, JLabel lbStatus) {
    int[] rows = dlgTable.getSelectedRows();

    if (rows.length == 0) {
        JOptionPane.showMessageDialog(null,
            "Vui lòng chọn ít nhất một sản phẩm.",
            "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        return;
    }

    for (int r : rows) {

        String code = dlgTable.getValueAt(r, 0).toString();

        for (ProductDTO p : allProducts) {

            if (p.getCode().equals(code)) {

                long price = p.getSellingPrice() != null
                        ? p.getSellingPrice().longValue()
                        : 0;

                addProductToOrder(
                        p.getId(),   
                        p.getCode(),
                        p.getName(),
                        price
                );

                break;
            }
        }
    }

    lbStatus.setText(rows.length + " sản phẩm đã được thêm vào đơn ✔");
}

    
    private JPanel makeRightCard(String title) {
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0xDDDDDD), 1),
            BorderFactory.createEmptyBorder(14, 18, 16, 18)));
        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0; g.gridy = 0; g.weightx = 1.0; g.fill = GridBagConstraints.HORIZONTAL;
        g.insets = new Insets(0,0,8,0);
        JLabel lbl = new JLabel(title);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14)); lbl.setForeground(new Color(0x222222));
        card.add(lbl, g);
        g.gridy = 1; g.insets = new Insets(0,0,10,0); card.add(new JSeparator(), g);
        card.putClientProperty("nr", 2);
        return card;
    }

    private void addFieldToCard(JPanel card, String label, JComponent field) {
        int nr = (Integer) card.getClientProperty("nr");
        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0; g.weightx = 1.0; g.fill = GridBagConstraints.HORIZONTAL;
        g.gridy = nr; g.insets = new Insets(0,0,3,0);
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12)); lbl.setForeground(new Color(0x555555));
        card.add(lbl, g);
        g.gridy = nr + 1; g.insets = new Insets(0,0,10,0);
        field.setPreferredSize(new Dimension(0, 34)); card.add(field, g);
        card.putClientProperty("nr", nr + 2);
    }

    private static void addFallbackEmployees(JComboBox<String> cb) {
        cb.addItem("NV001 - Nguyen Van An");
        cb.addItem("NV002 - Tran Thi Bich");
        cb.addItem("NV003 - Le Van Cuong");
    }

    
    private CustomerDTO findOrCreateCustomer(String name, String phone, String address) {
        
        for (CustomerDTO c : allCustomers) {
            if (phone.equals(c.getPhone())) {
                return c;
            }
        }
        
        
        CustomerDTO newCustomer = new CustomerDTO();
        newCustomer.setFullName(name);
        newCustomer.setPhone(phone);
        newCustomer.setAddress(address);
        
        
        String code = generateCustomerCode();
        newCustomer.setCode(code);
        
        CustomerBUS customerBUS = new CustomerBUS();
        boolean added = customerBUS.AddCustomer(newCustomer);
        if (added) {
            
            allCustomers = customerBUS.getAllCustomers();
            
            for (CustomerDTO c : allCustomers) {
                if (code.equals(c.getCode())) {
                    return c;
                }
            }
        }
        
        return null; 
    }
    
    private EmployeeDTO findEmployeeByName(String name) {
        EmployeeBUS employeeBUS = new EmployeeBUS();
        java.util.ArrayList<EmployeeDTO> employees = employeeBUS.getAllEmployees();
        for (EmployeeDTO e : employees) {
            String displayName = e.getCode() + " - " + e.getFullName();
            if (name.equals(displayName)) {
                return e;
            }
        }
        
        if (!employees.isEmpty()) {
            return employees.get(0);
        }
        return null;
    }

    
    private static final class OrderItem {

    long productId;   
    String code;
    String name;
    long unitPrice;
    int qty;

    OrderItem(long productId, String code, String name, long unitPrice, int qty) {
        this.productId = productId;
        this.code = code;
        this.name = name;
        this.unitPrice = unitPrice;
        this.qty = qty;
    }
}
    
    private String generateCustomerCode() {
        String sql = "SELECT MAX(customer_code) FROM customers WHERE customer_code LIKE 'KH%'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                String maxCode = rs.getString(1);
                if (maxCode == null) {
                    return "KH001";
                }
                int num = Integer.parseInt(maxCode.substring(2)) + 1;
                return "KH" + String.format("%03d", num);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "KH001";
    }
}