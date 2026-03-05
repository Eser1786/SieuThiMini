package GUI.DonHang;

import BUS.EmployeeBUS;
import BUS.ProductBUS;
import DTO.EmployeeDTO;
import DTO.ProductDTO;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

class DonHangCreateCard extends JPanel {

    private final DonHangPanel parent;
    private List<ProductDTO> allProducts = new ArrayList<>();

    // --- ordered items (backing list) ---
    private final List<OrderItem> items = new ArrayList<>();

    // --- live list UI ---
    private JPanel listPanel;
    private JScrollPane listScroll;

    // --- totals ---
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
        buildUI();
    }

    // ── mock data ───────────────────────────────────────────────────────────
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

    // ── helpers ─────────────────────────────────────────────────────────────
    /** Compact styled JTextField */
    private static JTextField cf() {
        JTextField tf = new JTextField();
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0xCCCCCC), 1),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)));
        return tf;
    }

    /** Recalculate Tam tinh + Tong cong labels */
    private void updateTotals() {
        long sub = 0;
        for (OrderItem it : items) sub += it.unitPrice * it.qty;
        long tot = Math.max(0, sub - discAmt);
        lbSubVal.setText(String.format("%,.0f\u0111", (double) sub));
        lbTotVal.setText(String.format("%,.0f\u0111", (double) tot));
    }

    /** Rebuild the item list panel from scratch */
    private void rebuildList() {
        listPanel.removeAll();
        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0; g.weightx = 1.0; g.fill = GridBagConstraints.HORIZONTAL;
        g.insets = new Insets(0, 0, 1, 0);   // 1-px separator gap
        for (int i = 0; i < items.size(); i++) {
            final int idx = i;
            OrderItem it = items.get(i);
            listPanel.add(buildItemRow(it, idx), g);
            g.gridy = i;
        }
        // filler to push rows to the top
        GridBagConstraints filler = new GridBagConstraints();
        filler.gridx = 0; filler.gridy = items.size();
        filler.weightx = 1.0; filler.weighty = 1.0;
        filler.fill = GridBagConstraints.BOTH;
        listPanel.add(new JLabel(), filler);

        listPanel.revalidate();
        listPanel.repaint();
    }

    /** Build one-item row panel */
    private JPanel buildItemRow(OrderItem it, int idx) {
        JPanel row = new JPanel(new GridBagLayout());
        row.setBackground(idx % 2 == 0 ? Color.WHITE : new Color(0xF7F5FF));
        row.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));

        GridBagConstraints g = new GridBagConstraints();
        g.gridy = 0; g.anchor = GridBagConstraints.WEST; g.insets = new Insets(0, 0, 0, 8);

        // name label (expand)
        JLabel lbName = new JLabel(it.name);
        lbName.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        g.gridx = 0; g.weightx = 1.0; g.fill = GridBagConstraints.HORIZONTAL;
        row.add(lbName, g);

        // unit price
        JLabel lbPrice = new JLabel(String.format("%,.0f\u0111", (double) it.unitPrice));
        lbPrice.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lbPrice.setForeground(new Color(0x555555));
        lbPrice.setPreferredSize(new Dimension(90, 24));
        lbPrice.setHorizontalAlignment(SwingConstants.RIGHT);
        g.gridx = 1; g.weightx = 0; g.fill = GridBagConstraints.NONE;
        row.add(lbPrice, g);

        // subtotal label (updates when spinner changes)
        JLabel lbSub = new JLabel(String.format("%,.0f\u0111", (double)(it.unitPrice * it.qty)));
        lbSub.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbSub.setForeground(new Color(0x5B4FCC));
        lbSub.setPreferredSize(new Dimension(100, 24));
        lbSub.setHorizontalAlignment(SwingConstants.RIGHT);

        // qty spinner (validation: min 1, max 9999, integer only)
        SpinnerNumberModel mdl = new SpinnerNumberModel(it.qty, 1, 9999, 1);
        JSpinner spinner = new JSpinner(mdl);
        spinner.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        spinner.setPreferredSize(new Dimension(64, 30));
        ((JSpinner.DefaultEditor) spinner.getEditor()).getTextField().setHorizontalAlignment(JTextField.CENTER);
        spinner.addChangeListener(ev -> {
            it.qty = (Integer) spinner.getValue();
            lbSub.setText(String.format("%,.0f\u0111", (double)(it.unitPrice * it.qty)));
            updateTotals();
        });
        g.gridx = 2; g.insets = new Insets(0, 0, 0, 8);
        row.add(spinner, g);

        g.gridx = 3; g.insets = new Insets(0, 0, 0, 8);
        row.add(lbSub, g);

        // X button
        JButton btnX = new JButton("X");
        btnX.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnX.setBackground(new Color(0xE53935)); btnX.setForeground(Color.WHITE);
        btnX.setFocusPainted(false); btnX.setBorderPainted(false); btnX.setOpaque(true);
        btnX.setPreferredSize(new Dimension(34, 30));
        btnX.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnX.addActionListener(e -> {
            items.remove(idx);
            rebuildList();
            updateTotals();
        });
        g.gridx = 4; g.insets = new Insets(0, 0, 0, 0);
        row.add(btnX, g);

        return row;
    }

    /** Add or increment a product in the items list */
    private void addProductToOrder(String name, long unitPrice) {
        for (OrderItem it : items) {
            if (it.name.equals(name)) {
                if (it.qty < 9999) it.qty++;
                rebuildList();
                updateTotals();
                return;
            }
        }
        items.add(new OrderItem(name, unitPrice, 1));
        rebuildList();
        updateTotals();
    }

    // ── main build ──────────────────────────────────────────────────────────
    private void buildUI() {
        Color pageBg = new Color(0xF0EFF8);
        setBackground(pageBg);

        /* ── Header ── */
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

        /* ── totals labels (created early so rebuildList can use) ── */
        lbSubVal = new JLabel("0\u0111");
        lbTotVal = new JLabel("0\u0111");
        lbSubVal.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lbTotVal.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lbTotVal.setForeground(new Color(0x5B4FCC));

        /* ── Item list panel + scroll ── */
        listPanel = new JPanel(new GridBagLayout());
        listPanel.setBackground(Color.WHITE);
        // header row
        JPanel listHeader = new JPanel(new GridBagLayout());
        listHeader.setBackground(new Color(0xD1C4E9));
        listHeader.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
        GridBagConstraints lhg = new GridBagConstraints();
        lhg.gridy = 0; lhg.anchor = GridBagConstraints.WEST; lhg.insets = new Insets(0, 0, 0, 8);
        String[] hdrTxt = { "T\u00ean s\u1ea3n ph\u1ea9m", "\u0110\u01a1n gi\u00e1", "S\u1ed1 l\u01b0\u1ee3ng", "Th\u00e0nh ti\u1ec1n", "" };
        int[]    hdrW   = { 0, 90, 64, 100, 34 };
        double[] hdrWx  = { 1.0, 0, 0, 0, 0 };
        for (int i = 0; i < hdrTxt.length; i++) {
            JLabel h = new JLabel(hdrTxt[i]);
            h.setFont(new Font("Segoe UI", Font.BOLD, 13));
            h.setForeground(new Color(0x333333));
            if (hdrW[i] > 0) h.setPreferredSize(new Dimension(hdrW[i], 20));
            if (i == 1 || i == 3) h.setHorizontalAlignment(SwingConstants.RIGHT);
            if (i == 2) h.setHorizontalAlignment(SwingConstants.CENTER);
            lhg.gridx = i; lhg.weightx = hdrWx[i];
            lhg.fill = (i == 0 ? GridBagConstraints.HORIZONTAL : GridBagConstraints.NONE);
            listHeader.add(h, lhg);
        }

        // list scroll — min height = 4 rows (each ~52px) + header
        rebuildList();   // start empty
        listScroll = new JScrollPane(listPanel);
        listScroll.setBorder(BorderFactory.createLineBorder(new Color(0xDDDDDD)));
        listScroll.setPreferredSize(new Dimension(0, 4 * 52));
        listScroll.getVerticalScrollBar().setUnitIncrement(16);

        JPanel listBox = new JPanel(new BorderLayout());
        listBox.setBackground(Color.WHITE);
        listBox.setBorder(BorderFactory.createLineBorder(new Color(0xDDDDDD)));
        listBox.add(listHeader, BorderLayout.NORTH);
        listBox.add(listScroll, BorderLayout.CENTER);

        /* ── Search + Browse row ── */
        final String HINT = "Nh\u1eadp t\u00ean s\u1ea3n ph\u1ea9m, Enter \u0111\u1ec3 th\u00eam...";
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
        tfSearch.addActionListener(e -> {
            String q = tfSearch.getText().trim();
            if (q.isEmpty() || q.equals(HINT)) return;
            String ql = q.toLowerCase();
            for (ProductDTO p : allProducts) {
                String hay = (p.getName() == null ? "" : p.getName().toLowerCase()) + p.getCode().toLowerCase();
                if (hay.contains(ql)) {
                    addProductToOrder(p.getName() != null ? p.getName() : "(Kh\u00f4ng t\u00ean)",
                        p.getSellingPrice() != null ? p.getSellingPrice().longValue() : 0L);
                    tfSearch.setText("");
                    tfSearch.setForeground(Color.BLACK);
                    return;
                }
            }
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

        /* ── Notes ── */
        JTextArea taNotes = new JTextArea(3, 20);
        taNotes.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        taNotes.setLineWrap(true); taNotes.setWrapStyleWord(true);
        taNotes.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
        JScrollPane notesScroll = new JScrollPane(taNotes);
        notesScroll.setBorder(BorderFactory.createLineBorder(new Color(0xCCCCCC)));

        /* ── Discount ── */
        JTextField tfMaKM = cf();
        tfMaKM.setPreferredSize(new Dimension(160, 32));
        JLabel lbDiscStatus = new JLabel("");
        lbDiscStatus.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        JButton btnApply = new JButton("\u00c1p d\u1ee5ng");
        btnApply.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnApply.setBackground(new Color(0x5B4FCC)); btnApply.setForeground(Color.WHITE);
        btnApply.setFocusPainted(false); btnApply.setBorderPainted(false); btnApply.setOpaque(true);
        btnApply.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnApply.setPreferredSize(new Dimension(90, 32));
        btnApply.addActionListener(e -> {
            String code = tfMaKM.getText().trim();
            if (code.isEmpty()) {
                lbDiscStatus.setText("Nh\u1eadp m\u00e3 tr\u01b0\u1edbc."); lbDiscStatus.setForeground(Color.RED); return;
            }
            long sub = 0; for (OrderItem it : items) sub += it.unitPrice * it.qty;
            discAmt = sub / 10;
            lbDiscStatus.setText("Gi\u1ea3m 10%"); lbDiscStatus.setForeground(new Color(0x2E7D32));
            updateTotals();
        });

        JPanel discRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        discRow.setBackground(Color.WHITE);
        JLabel lbKMlbl = new JLabel("M\u00e3 khuy\u1ebfn m\u00e3i:");
        lbKMlbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        discRow.add(lbKMlbl); discRow.add(tfMaKM); discRow.add(btnApply); discRow.add(lbDiscStatus);

        /* ── Summary ── */
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

        /* ── Left card assembly ── */
        JPanel leftContent = new JPanel(new GridBagLayout());
        leftContent.setBackground(Color.WHITE);
        leftContent.setBorder(BorderFactory.createEmptyBorder(18, 22, 18, 22));
        GridBagConstraints lc = new GridBagConstraints();
        lc.gridx = 0; lc.weightx = 1.0; lc.fill = GridBagConstraints.HORIZONTAL;

        JLabel leftTitle = new JLabel("Chi ti\u1ebft \u0111\u01a1n h\u00e0ng");
        leftTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        leftTitle.setForeground(new Color(0x222222));
        lc.gridy = 0; lc.insets = new Insets(0, 0, 10, 0); leftContent.add(leftTitle, lc);
        lc.gridy = 1; lc.insets = new Insets(0, 0, 10, 0); leftContent.add(new JSeparator(), lc);
        lc.gridy = 2; lc.insets = new Insets(0, 0, 8, 0);  leftContent.add(searchRow, lc);
        lc.gridy = 3; lc.insets = new Insets(0, 0, 10, 0); leftContent.add(listBox, lc);

        JLabel lbNotesLbl = new JLabel("Ghi ch\u00fa \u0111\u01a1n h\u00e0ng:");
        lbNotesLbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbNotesLbl.setForeground(new Color(0x444444));
        lc.gridy = 4; lc.insets = new Insets(14, 0, 4, 0);  leftContent.add(lbNotesLbl, lc);
        lc.gridy = 5; lc.insets = new Insets(0, 0, 10, 0);   leftContent.add(notesScroll, lc);
        lc.gridy = 6; lc.insets = new Insets(0, 0, 10, 0);   leftContent.add(new JSeparator(), lc);
        lc.gridy = 7; lc.insets = new Insets(0, 0, 6, 0);    leftContent.add(discRow, lc);
        lc.gridy = 8; lc.insets = new Insets(4, 0, 4, 0);    leftContent.add(subRow, lc);
        lc.gridy = 9; lc.insets = new Insets(0, 0, 0, 0);    leftContent.add(totRow, lc);

        JPanel leftCard = new JPanel(new BorderLayout());
        leftCard.setBackground(Color.WHITE);
        leftCard.setBorder(BorderFactory.createLineBorder(new Color(0xDDDDDD), 1));
        leftCard.add(leftContent, BorderLayout.CENTER);

        /* ── Right column ── */
        JTextField tfTenND  = cf();
        JTextField tfSdt    = cf();
        JTextField tfDiaChi = cf();
        JPanel custCard = makeRightCard("Kh\u00e1ch h\u00e0ng");
        addFieldToCard(custCard, "T\u00ean ng\u01b0\u1eddi mua:", tfTenND);
        addFieldToCard(custCard, "S\u1ed1 \u0111i\u1ec7n tho\u1ea1i:", tfSdt);
        addFieldToCard(custCard, "\u0110\u1ecba ch\u1ec9 giao h\u00e0ng:", tfDiaChi);

        JComboBox<String> cbNhanVien = new JComboBox<>();
        cbNhanVien.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cbNhanVien.addItem("-- Ch\u1ecdn nh\u00e2n vi\u00ean --");
        try {
            List<EmployeeDTO> emps = new EmployeeBUS().getAllEmployees();
            if (emps != null && !emps.isEmpty())
                for (EmployeeDTO e : emps) cbNhanVien.addItem(e.getCode() + " - " + e.getFullName());
            else addFallbackEmployees(cbNhanVien);
        } catch (Exception ignored) { addFallbackEmployees(cbNhanVien); }
        JPanel empCard = makeRightCard("Nh\u00e2n vi\u00ean ti\u1ebfp nh\u1eadn");
        addFieldToCard(empCard, "Nh\u00e2n vi\u00ean:", cbNhanVien);

        JComboBox<String> cbHinhThuc = new JComboBox<>(new String[]{
            "Thanh to\u00e1n khi nh\u1eadn h\u00e0ng", "Chuy\u1ec3n kho\u1ea3n", "Th\u1ebb t\u00edn d\u1ee5ng" });
        cbHinhThuc.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JPanel payCard = makeRightCard("H\u00ecnh th\u1ee9c thanh to\u00e1n");
        addFieldToCard(payCard, "Ph\u01b0\u01a1ng th\u1ee9c:", cbHinhThuc);

        JPanel rightCol = new JPanel(new GridBagLayout());
        rightCol.setBackground(pageBg);
        GridBagConstraints rc = new GridBagConstraints();
        rc.gridx = 0; rc.weightx = 1.0; rc.fill = GridBagConstraints.HORIZONTAL;
        rc.anchor = GridBagConstraints.NORTH;
        rc.gridy = 0; rc.insets = new Insets(0, 0, 14, 0); rightCol.add(custCard, rc);
        rc.gridy = 1; rc.insets = new Insets(0, 0, 14, 0); rightCol.add(empCard, rc);
        rc.gridy = 2; rc.insets = new Insets(0, 0, 0, 0);  rightCol.add(payCard, rc);
        rc.gridy = 3; rc.weighty = 1.0; rc.fill = GridBagConstraints.BOTH;
        rightCol.add(new JLabel(), rc);

        /* ── Two-column body ── */
        JPanel twoCol = new JPanel(new GridBagLayout());
        twoCol.setBackground(pageBg);
        twoCol.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints tc = new GridBagConstraints();
        tc.gridy = 0; tc.weighty = 1.0;
        tc.gridx = 0; tc.weightx = 0.62; tc.fill = GridBagConstraints.BOTH;
        tc.insets = new Insets(0, 0, 0, 14); twoCol.add(leftCard, tc);
        tc.gridx = 1; tc.weightx = 0.38; tc.fill = GridBagConstraints.HORIZONTAL;
        tc.anchor = GridBagConstraints.NORTH; tc.weighty = 0;
        tc.insets = new Insets(0, 0, 0, 0); twoCol.add(rightCol, tc);

        JScrollPane bodyScroll = new JScrollPane(twoCol);
        bodyScroll.setBorder(BorderFactory.createEmptyBorder());
        bodyScroll.getVerticalScrollBar().setUnitIncrement(16);
        add(bodyScroll, BorderLayout.CENTER);

        /* ── Footer ── */
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 12));
        footer.setBackground(pageBg);
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(0xCCCCCC)));
        JButton btnLuu = DonHangPanel.makeFootBtn("L\u01b0u \u0111\u01a1n h\u00e0ng", new Color(0x5B4FCC));
        JButton btnHuy = DonHangPanel.makeFootBtn("H\u1ee7y b\u1ecf", new Color(0xB83434));
        btnHuy.addActionListener(e -> parent.showCard(DonHangPanel.CARD_TABLE));
        btnLuu.addActionListener(e -> {
            String ten    = tfTenND.getText().trim();
            String sdt    = tfSdt.getText().trim();
            String diaChi = tfDiaChi.getText().trim();
            java.util.List<String> errors = new java.util.ArrayList<>();
            JComponent firstBad = null;
            if (ten.isEmpty()) {
                errors.add("\u2022 T\u00ean ng\u01b0\u1eddi mua kh\u00f4ng \u0111\u01b0\u1ee3c \u0111\u1ec3 tr\u1ed1ng.");
                firstBad = tfTenND;
            } else if (!ten.matches("[\\p{L} .'-]+")) {
                errors.add("\u2022 T\u00ean ng\u01b0\u1eddi mua kh\u00f4ng h\u1ee3p l\u1ec7.");
                firstBad = tfTenND;
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
            String maDon    = "HD" + String.format("%03d", parent.tableModel.getRowCount() + 1);
            String nhanVien = cbNhanVien.getSelectedItem().toString();
            String maKM     = tfMaKM.getText().trim();

            // Build confirmation message
            StringBuilder sb = new StringBuilder();
            sb.append("\u2764 X\u00e1c nh\u1eadn t\u1ea1o \u0111\u01a1n h\u00e0ng?\n\n");
            sb.append(String.format("M\u00e3 \u0111\u01a1n:       %s\n", maDon));
            sb.append(String.format("Kh\u00e1ch h\u00e0ng:  %s\n", ten));
            sb.append(String.format("SĐT:         %s\n", sdt));
            sb.append(String.format("\u0110\u1ecba ch\u1ec9:     %s\n", diaChi));
            sb.append(String.format("Nh\u00e2n vi\u00ean:   %s\n", nhanVien));
            sb.append(String.format("S\u1ed1 SP:       %d s\u1ea3n ph\u1ea9m (%d m\u00f3n)\n", items.size(), totalQty));
            if (!maKM.isEmpty()) sb.append(String.format("Khuy\u1ebfn m\u00e3i:  %s\n", maKM));
            sb.append(String.format("\nT\u1ed5ng c\u1ed9ng:   %,.0f\u0111", (double) tongCong));

            int confirm = JOptionPane.showConfirmDialog(this, sb.toString(),
                "X\u00e1c nh\u1eadn \u0111\u01a1n h\u00e0ng", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (confirm != JOptionPane.YES_OPTION) return;

            parent.tableModel.addRow(new Object[]{ maDon, ten, totalQty,
                maKM.isEmpty() ? "-" : maKM,
                String.format("%,.0f", (double) tongCong) + "\u0111",
                "Ch\u1edd x\u00e1c nh\u1eadn", "" });
            parent.nhanVienMap.put(maDon, nhanVien);
            JOptionPane.showMessageDialog(this,
                "\u0110\u00e3 t\u1ea1o \u0111\u01a1n h\u00e0ng " + maDon + " th\u00e0nh c\u00f4ng!",
                "Th\u00e0nh c\u00f4ng", JOptionPane.INFORMATION_MESSAGE);
            // reset
            tfTenND.setText(""); tfSdt.setText(""); tfDiaChi.setText("");
            taNotes.setText(""); tfMaKM.setText("");
            cbNhanVien.setSelectedIndex(0);
            items.clear(); discAmt = 0; rebuildList(); updateTotals(); lbDiscStatus.setText("");
            parent.showCard(DonHangPanel.CARD_TABLE);
        });
        footer.add(btnLuu); footer.add(btnHuy);
        add(footer, BorderLayout.SOUTH);
    }

    // ── Browse dialog (3-column JTable) ─────────────────────────────────────
    private void openBrowseDialog() {
        Frame owner = null;
        Window w = SwingUtilities.getWindowAncestor(this);
        if (w instanceof Frame f) owner = f;
        JDialog dlg = new JDialog(owner, "Ch\u1ecdn s\u1ea3n ph\u1ea9m", true);
        dlg.setSize(560, 460); dlg.setLocationRelativeTo(this);

        // search
        JTextField dlgSearch = cf();

        // table model: Tên | Đơn giá | Mã SP
        DefaultTableModel dlgModel = new DefaultTableModel(
                new String[]{ "T\u00ean s\u1ea3n ph\u1ea9m", "\u0110\u01a1n gi\u00e1", "M\u00e3 SP" }, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        // populate
        for (ProductDTO p : allProducts) {
            long price = p.getSellingPrice() != null ? p.getSellingPrice().longValue() : 0L;
            dlgModel.addRow(new Object[]{
                p.getName() != null ? p.getName() : "(Kh\u00f4ng t\u00ean)",
                String.format("%,.0f\u0111", (double) price),
                p.getCode()
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
        dlgTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        dlgTable.getColumnModel().getColumn(0).setPreferredWidth(230);
        dlgTable.getColumnModel().getColumn(1).setPreferredWidth(100);
        dlgTable.getColumnModel().getColumn(2).setPreferredWidth(80);

        // alternating + right-align price col
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
        dlgTable.getColumnModel().getColumn(1).setCellRenderer(priceR);
        dlgTable.getColumnModel().getColumn(2).setCellRenderer(altR);

        // live filter
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
                            p.getName() != null ? p.getName() : "(Kh\u00f4ng t\u00ean)",
                            String.format("%,.0f\u0111", (double) price),
                            p.getCode()
                        });
                    }
                }
            }
        });

        // double-click to add
        dlgTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) pickRow(dlgTable, dlg);
            }
        });

        JButton btnAdd = new JButton("Th\u00eam v\u00e0o \u0111\u01a1n");
        btnAdd.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnAdd.setBackground(new Color(0x5B4FCC)); btnAdd.setForeground(Color.WHITE);
        btnAdd.setFocusPainted(false); btnAdd.setBorderPainted(false); btnAdd.setOpaque(true);
        btnAdd.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAdd.addActionListener(e -> pickRow(dlgTable, dlg));

        JButton btnClose = new JButton("\u0110\u00f3ng");
        btnClose.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnClose.addActionListener(e -> dlg.dispose());

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        btnRow.setBackground(Color.WHITE);
        btnRow.add(btnClose); btnRow.add(btnAdd);

        JPanel dlgPanel = new JPanel(new BorderLayout(0, 10));
        dlgPanel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        dlgPanel.setBackground(Color.WHITE);
        dlgPanel.add(dlgSearch, BorderLayout.NORTH);
        dlgPanel.add(new JScrollPane(dlgTable), BorderLayout.CENTER);
        dlgPanel.add(btnRow, BorderLayout.SOUTH);
        dlg.setContentPane(dlgPanel);
        dlg.setVisible(true);
    }

    /** Add the selected table row to the order and close (or show warning) */
    private void pickRow(JTable dlgTable, JDialog dlg) {
        int row = dlgTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(dlg, "Vui l\u00f2ng ch\u1ecdn m\u1ed9t s\u1ea3n ph\u1ea9m.",
                "Th\u00f4ng b\u00e1o", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        String name = (String) dlgTable.getValueAt(row, 0);
        String code = (String) dlgTable.getValueAt(row, 2);
        long price = 0L;
        for (ProductDTO p : allProducts) {
            if (p.getCode().equals(code)) {
                if (p.getSellingPrice() != null) price = p.getSellingPrice().longValue();
                break;
            }
        }
        addProductToOrder(name, price);
        dlg.dispose();
    }

    // ── right-column card builders ───────────────────────────────────────────
    private JPanel makeRightCard(String title) {
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0xDDDDDD), 1),
            BorderFactory.createEmptyBorder(14, 18, 16, 18)));
        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0; g.gridy = 0; g.weightx = 1.0; g.fill = GridBagConstraints.HORIZONTAL;
        g.insets = new Insets(0, 0, 8, 0);
        JLabel lbl = new JLabel(title);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14)); lbl.setForeground(new Color(0x222222));
        card.add(lbl, g);
        g.gridy = 1; g.insets = new Insets(0, 0, 10, 0); card.add(new JSeparator(), g);
        card.putClientProperty("nr", 2);
        return card;
    }

    private void addFieldToCard(JPanel card, String label, JComponent field) {
        int nr = (Integer) card.getClientProperty("nr");
        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0; g.weightx = 1.0; g.fill = GridBagConstraints.HORIZONTAL;
        g.gridy = nr; g.insets = new Insets(0, 0, 3, 0);
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12)); lbl.setForeground(new Color(0x555555));
        card.add(lbl, g);
        g.gridy = nr + 1; g.insets = new Insets(0, 0, 10, 0);
        field.setPreferredSize(new Dimension(0, 34)); card.add(field, g);
        card.putClientProperty("nr", nr + 2);
    }

    private static void addFallbackEmployees(JComboBox<String> cb) {
        cb.addItem("NV001 - Nguyen Van An");
        cb.addItem("NV002 - Tran Thi Bich");
        cb.addItem("NV003 - Le Van Cuong");
    }

    // ── inner classes ────────────────────────────────────────────────────────
    private static final class OrderItem {
        String name; long unitPrice; int qty;
        OrderItem(String name, long unitPrice, int qty) {
            this.name = name; this.unitPrice = unitPrice; this.qty = qty;
        }
    }
}