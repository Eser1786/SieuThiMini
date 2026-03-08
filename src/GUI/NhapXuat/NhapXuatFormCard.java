package GUI.NhapXuat;

import BUS.EmployeeBUS;
import BUS.ProductBUS;
import BUS.PurchaseInvoicesBUS;
import BUS.SupplierBUS;
import DTO.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/** Form tao phieu nhap kho - dung trong JDialog popup, layout 2 cot giong DonHangCreateCard. */
class NhapXuatFormCard extends JPanel {

    private static final Color CLR_PAGE   = new Color(0xF0EFF8);
    private static final Color CLR_WHITE  = Color.WHITE;
    private static final Color CLR_ACCENT = new Color(0x5C4A7F);
    private static final Color CLR_HDR    = new Color(0xD1C4E9);

    // Data
    private List<ProductDTO>  allProducts  = new ArrayList<>();
    private List<EmployeeDTO> allEmployees = new ArrayList<>();
    private List<SupplierDTO> allSuppliers = new ArrayList<>();

    // Form items (backing the dynamic list panel)
    private final List<FormItem> items = new ArrayList<>();

    // UI refs - initialised at field level so build methods can use them safely
    private final JLabel            lblDate       = new JLabel();
    private       JComboBox<String> cbEmployee;
    private final JTextArea         txtNote       = new JTextArea(3, 18);
    private       JComboBox<String> cbSupplier;
    private final JTextField        txtInvoiceRef = new JTextField();

    // Summary labels (right card)
    private final JLabel lblTotalItems = new JLabel("0");
    private final JLabel lblTotalQty   = new JLabel("0");
    private final JLabel lblTotalMoney = new JLabel("0 d");

    // Total label shown in left card footer
    private final JLabel lblLeftTotal  = new JLabel("0 d");

    // List UI
    private JPanel      listPanel;
    private JScrollPane listScroll;

    private static class FormItem {
        long       productId;
        String     productCode;
        String     productName;
        long       quantity;
        BigDecimal unitPrice;
        BigDecimal subtotal;
    }

    NhapXuatFormCard(Window dialogOwner) {
        setBackground(CLR_PAGE);
        setLayout(new BorderLayout());

        try { allProducts  = new ProductBUS().getAllProducts();  } catch (Exception ignored) {}
        try { allEmployees = new EmployeeBUS().getAllEmployees(); } catch (Exception ignored) {}
        try { allSuppliers = new SupplierBUS().getAllSuppliers(); } catch (Exception ignored) {}

        add(buildHeader(), BorderLayout.NORTH);
        add(buildBody(),   BorderLayout.CENTER);
        add(buildFooter(), BorderLayout.SOUTH);
    }

    // ── Header ──────────────────────────────────────────────────────────────

    private JPanel buildHeader() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(CLR_PAGE);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0xCCCCCC)),
                BorderFactory.createEmptyBorder(12, 20, 12, 20)));
        JLabel lbl = new JLabel("+ Tao phieu nhap kho");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lbl.setForeground(new Color(0x333333));
        p.add(lbl, BorderLayout.WEST);
        return p;
    }

    // ── Body (2-column layout) ───────────────────────────────────────────────

    private JScrollPane buildBody() {
        JPanel twoCol = new JPanel(new GridBagLayout());
        twoCol.setBackground(CLR_PAGE);
        twoCol.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints tc = new GridBagConstraints();
        tc.gridy = 0; tc.weighty = 1.0;

        // Left card (65%)
        tc.gridx = 0; tc.weightx = 0.65; tc.fill = GridBagConstraints.BOTH;
        tc.insets = new Insets(0, 0, 0, 14);
        twoCol.add(buildLeftCard(), tc);

        // Right column (35%)
        tc.gridx = 1; tc.weightx = 0.35; tc.fill = GridBagConstraints.HORIZONTAL;
        tc.anchor = GridBagConstraints.NORTH; tc.weighty = 0;
        tc.insets = new Insets(0, 0, 0, 0);
        twoCol.add(buildRightCol(), tc);

        JScrollPane sp = new JScrollPane(twoCol);
        sp.setBorder(BorderFactory.createEmptyBorder());
        sp.getVerticalScrollBar().setUnitIncrement(16);
        return sp;
    }

    private JPanel buildLeftCard() {
        // List panel - rows added by rebuildList()
        listPanel = new JPanel(new GridBagLayout());
        listPanel.setBackground(CLR_WHITE);

        // Table header bar
        JPanel tableHeader = new JPanel(new GridBagLayout());
        tableHeader.setBackground(CLR_HDR);
        tableHeader.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
        GridBagConstraints lhg = new GridBagConstraints();
        lhg.gridy = 0; lhg.anchor = GridBagConstraints.WEST; lhg.insets = new Insets(0, 0, 0, 6);
        String[] hdrTxt = {"Ten san pham", "SKU", "So luong", "Gia nhap (d)", "Thanh tien (d)", ""};
        int[]    hdrW   = {0, 100, 80, 120, 120, 36};
        double[] hdrWx  = {1.0, 0, 0, 0, 0, 0};
        for (int i = 0; i < hdrTxt.length; i++) {
            JLabel h = new JLabel(hdrTxt[i]);
            h.setFont(new Font("Segoe UI", Font.BOLD, 13));
            h.setForeground(new Color(0x333333));
            if (hdrW[i] > 0) h.setPreferredSize(new Dimension(hdrW[i], 20));
            if (i == 2 || i == 3 || i == 4) h.setHorizontalAlignment(SwingConstants.RIGHT);
            lhg.gridx = i; lhg.weightx = hdrWx[i];
            lhg.fill = (i == 0 ? GridBagConstraints.HORIZONTAL : GridBagConstraints.NONE);
            tableHeader.add(h, lhg);
        }

        rebuildList();

        listScroll = new JScrollPane(listPanel);
        listScroll.setBorder(BorderFactory.createLineBorder(new Color(0xDDDDDD)));
        listScroll.setPreferredSize(new Dimension(0, 5 * 52));
        listScroll.getVerticalScrollBar().setUnitIncrement(16);

        JPanel listBox = new JPanel(new BorderLayout());
        listBox.setBackground(CLR_WHITE);
        listBox.setBorder(BorderFactory.createLineBorder(new Color(0xDDDDDD)));
        listBox.add(tableHeader, BorderLayout.NORTH);
        listBox.add(listScroll, BorderLayout.CENTER);

        // Search field + Browse button row
        JTextField tfSearch = new JTextField();
        tfSearch.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tfSearch.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xCCCCCC), 1),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)));

        JButton btnBrowse = makeBtn("+ Duyet san pham", CLR_ACCENT);
        btnBrowse.setPreferredSize(new Dimension(200, 36));
        btnBrowse.addActionListener(e -> openProductPicker());

        JPanel searchRow = new JPanel(new BorderLayout(8, 0));
        searchRow.setBackground(CLR_WHITE);
        searchRow.add(tfSearch, BorderLayout.CENTER);
        searchRow.add(btnBrowse, BorderLayout.EAST);

        // Total row at bottom of left card
        JLabel lbTotLbl = new JLabel("Tong tien:");
        lbTotLbl.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblLeftTotal.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblLeftTotal.setForeground(CLR_ACCENT);
        lblLeftTotal.setHorizontalAlignment(SwingConstants.RIGHT);
        JPanel totRow = new JPanel(new BorderLayout());
        totRow.setBackground(CLR_WHITE);
        totRow.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(0xDDDDDD)),
                BorderFactory.createEmptyBorder(10, 0, 4, 0)));
        totRow.add(lbTotLbl, BorderLayout.WEST);
        totRow.add(lblLeftTotal, BorderLayout.EAST);

        // Left card content (GridBagLayout)
        JPanel leftContent = new JPanel(new GridBagLayout());
        leftContent.setBackground(CLR_WHITE);
        leftContent.setBorder(BorderFactory.createEmptyBorder(18, 22, 18, 22));
        GridBagConstraints lc = new GridBagConstraints();
        lc.gridx = 0; lc.weightx = 1.0; lc.fill = GridBagConstraints.HORIZONTAL;

        JLabel leftTitle = new JLabel("Chi tiet san pham nhap");
        leftTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        leftTitle.setForeground(new Color(0x222222));

        lc.gridy = 0; lc.insets = new Insets(0, 0, 10, 0); lc.weighty = 0;
        leftContent.add(leftTitle, lc);
        lc.gridy = 1; lc.insets = new Insets(0, 0, 8, 0);
        leftContent.add(new JSeparator(), lc);
        lc.gridy = 2; lc.insets = new Insets(0, 0, 10, 0);
        leftContent.add(searchRow, lc);
        lc.gridy = 3; lc.insets = new Insets(0, 0, 10, 0); lc.weighty = 1.0;
        lc.fill = GridBagConstraints.BOTH;
        leftContent.add(listBox, lc);
        lc.gridy = 4; lc.insets = new Insets(0, 0, 0, 0); lc.weighty = 0;
        lc.fill = GridBagConstraints.HORIZONTAL;
        leftContent.add(totRow, lc);

        JPanel leftCard = new JPanel(new BorderLayout());
        leftCard.setBackground(CLR_WHITE);
        leftCard.setBorder(BorderFactory.createLineBorder(new Color(0xDDDDDD)));
        leftCard.add(leftContent, BorderLayout.CENTER);
        return leftCard;
    }

    private void rebuildList() {
        listPanel.removeAll();
        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0; g.weightx = 1.0; g.fill = GridBagConstraints.HORIZONTAL;
        g.insets = new Insets(0, 0, 1, 0);
        for (int i = 0; i < items.size(); i++) {
            g.gridy = i;
            listPanel.add(buildItemRow(items.get(i), i, i), g);
        }
        // Filler
        GridBagConstraints filler = new GridBagConstraints();
        filler.gridx = 0; filler.gridy = items.size();
        filler.weightx = 1.0; filler.weighty = 1.0; filler.fill = GridBagConstraints.BOTH;
        listPanel.add(new JLabel(), filler);
        listPanel.revalidate();
        listPanel.repaint();
        updateSummary();
    }

    private JPanel buildItemRow(FormItem fi, int idx, int vis) {
        JPanel row = new JPanel(new GridBagLayout());
        row.setBackground(vis % 2 == 0 ? CLR_WHITE : new Color(0xF7F5FF));
        row.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
        GridBagConstraints g = new GridBagConstraints();
        g.gridy = 0; g.anchor = GridBagConstraints.WEST; g.insets = new Insets(0, 0, 0, 8);

        // col 0: Ten SP (expands)
        JLabel lbName = new JLabel(fi.productName);
        lbName.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        g.gridx = 0; g.weightx = 1.0; g.fill = GridBagConstraints.HORIZONTAL;
        row.add(lbName, g);

        // col 1: SKU
        JLabel lbCode = new JLabel(fi.productCode);
        lbCode.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lbCode.setForeground(new Color(0x666666));
        lbCode.setPreferredSize(new Dimension(100, 24));
        g.gridx = 1; g.weightx = 0; g.fill = GridBagConstraints.NONE;
        row.add(lbCode, g);

        // col 3: Don gia (pre-declared before spinner so changeListener can update it)
        JLabel lbPrice = new JLabel(formatMoney(fi.unitPrice));
        lbPrice.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lbPrice.setForeground(new Color(0x555555));
        lbPrice.setPreferredSize(new Dimension(120, 24));
        lbPrice.setHorizontalAlignment(SwingConstants.RIGHT);

        // col 4: Thanh tien (pre-declared before spinner)
        JLabel lbSub = new JLabel(formatMoney(fi.subtotal));
        lbSub.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbSub.setForeground(CLR_ACCENT);
        lbSub.setPreferredSize(new Dimension(120, 24));
        lbSub.setHorizontalAlignment(SwingConstants.RIGHT);

        // col 2: Spinner so luong
        SpinnerNumberModel mdl = new SpinnerNumberModel((int) Math.min(fi.quantity, 999999L), 1, 999999, 1);
        JSpinner spinner = new JSpinner(mdl);
        spinner.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        spinner.setPreferredSize(new Dimension(80, 30));
        ((JSpinner.DefaultEditor) spinner.getEditor()).getTextField()
                .setHorizontalAlignment(JTextField.CENTER);
        spinner.addChangeListener(ev -> {
            fi.quantity = ((Number) spinner.getValue()).longValue();
            fi.subtotal = fi.unitPrice.multiply(BigDecimal.valueOf(fi.quantity));
            lbSub.setText(formatMoney(fi.subtotal));
            updateSummary();
        });
        g.gridx = 2; g.insets = new Insets(0, 0, 0, 8);
        row.add(spinner, g);
        g.gridx = 3; row.add(lbPrice, g);
        g.gridx = 4; row.add(lbSub, g);

        // col 5: Remove (X) button
        JButton btnX = new JButton("X");
        btnX.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnX.setBackground(new Color(0xE53935)); btnX.setForeground(CLR_WHITE);
        btnX.setFocusPainted(false); btnX.setBorderPainted(false); btnX.setOpaque(true);
        btnX.setPreferredSize(new Dimension(34, 30));
        btnX.setMargin(new Insets(0, 0, 0, 0));
        btnX.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnX.addActionListener(e -> {
            items.remove(idx);
            rebuildList();
        });
        g.gridx = 5; g.insets = new Insets(0, 0, 0, 0);
        row.add(btnX, g);

        return row;
    }

    // ── Right column ─────────────────────────────────────────────────────────

    private JPanel buildRightCol() {
        // Card 1: Thong tin chung
        JPanel infoCard = makeRightCard("Thong tin chung");
        lblDate.setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        lblDate.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        cbEmployee = new JComboBox<>();
        cbEmployee.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cbEmployee.addItem("-- Chon nhan vien --");
        for (EmployeeDTO e : allEmployees) cbEmployee.addItem(e.getFullName());

        txtNote.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtNote.setLineWrap(true); txtNote.setWrapStyleWord(true);
        JScrollPane noteSP = new JScrollPane(txtNote);
        noteSP.setBorder(BorderFactory.createLineBorder(new Color(0xCCCCCC)));
        noteSP.setPreferredSize(new Dimension(0, 72));

        addFieldToCard(infoCard, "Ngay tao:", lblDate);
        addFieldToCard(infoCard, "Nhan vien:", cbEmployee);
        addFieldToCard(infoCard, "Ghi chu:", noteSP);

        // Card 2: Nha cung cap
        JPanel supplierCard = makeRightCard("Nha cung cap");
        cbSupplier = new JComboBox<>();
        cbSupplier.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cbSupplier.addItem("-- Chon nha cung cap --");
        for (SupplierDTO s : allSuppliers) cbSupplier.addItem(s.getName());

        txtInvoiceRef.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtInvoiceRef.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xCCCCCC), 1),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)));
        addFieldToCard(supplierCard, "Nha cung cap:", cbSupplier);
        addFieldToCard(supplierCard, "So hoa don nhap:", txtInvoiceRef);

        // Card 3: Tong ket
        JPanel summaryCard = makeRightCard("Tong ket phieu");
        for (JLabel l : new JLabel[]{lblTotalItems, lblTotalQty, lblTotalMoney}) {
            l.setFont(new Font("Segoe UI", Font.BOLD, 14));
            l.setForeground(CLR_ACCENT);
        }
        addFieldToCard(summaryCard, "So loai SP:", lblTotalItems);
        addFieldToCard(summaryCard, "Tong so luong:", lblTotalQty);
        addFieldToCard(summaryCard, "Tong tien:", lblTotalMoney);

        JPanel rightCol = new JPanel(new GridBagLayout());
        rightCol.setBackground(CLR_PAGE);
        GridBagConstraints rc = new GridBagConstraints();
        rc.gridx = 0; rc.weightx = 1.0; rc.fill = GridBagConstraints.HORIZONTAL;
        rc.anchor = GridBagConstraints.NORTH;
        rc.gridy = 0; rc.insets = new Insets(0, 0, 14, 0); rightCol.add(infoCard, rc);
        rc.gridy = 1; rc.insets = new Insets(0, 0, 14, 0); rightCol.add(supplierCard, rc);
        rc.gridy = 2; rc.insets = new Insets(0, 0, 0, 0);  rightCol.add(summaryCard, rc);
        rc.gridy = 3; rc.weighty = 1.0; rc.fill = GridBagConstraints.BOTH;
        rightCol.add(new JLabel(), rc);
        return rightCol;
    }

    private JPanel makeRightCard(String title) {
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(CLR_WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xDDDDDD), 1),
                BorderFactory.createEmptyBorder(14, 16, 14, 16)));
        card.putClientProperty("nr", 0);

        JLabel lbl = new JLabel(title);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbl.setForeground(CLR_ACCENT);
        lbl.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0x9B8EA8)),
                BorderFactory.createEmptyBorder(0, 0, 6, 0)));

        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0; g.gridy = 0; g.gridwidth = 2; g.weightx = 1.0;
        g.fill = GridBagConstraints.HORIZONTAL; g.insets = new Insets(0, 0, 10, 0);
        card.add(lbl, g);
        card.putClientProperty("nr", 1);
        return card;
    }

    private void addFieldToCard(JPanel card, String labelText, JComponent comp) {
        int nr = (Integer) card.getClientProperty("nr");
        GridBagConstraints g = new GridBagConstraints();
        g.gridy = nr;
        g.gridx = 0; g.anchor = GridBagConstraints.NORTHWEST;
        g.insets = new Insets(0, 0, 10, 8);
        JLabel lbl = new JLabel(labelText);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        card.add(lbl, g);
        g.gridx = 1; g.weightx = 1.0; g.fill = GridBagConstraints.HORIZONTAL;
        g.insets = new Insets(0, 0, 10, 0);
        card.add(comp, g);
        card.putClientProperty("nr", nr + 1);
    }

    // ── Footer ───────────────────────────────────────────────────────────────

    private JPanel buildFooter() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 12));
        p.setBackground(CLR_PAGE);
        p.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(0xCCCCCC)));
        JButton btnHuy = makeBtn("Huy",       new Color(0xB83434));
        JButton btnLuu = makeBtn("Luu phieu", CLR_ACCENT);
        btnHuy.setPreferredSize(new Dimension(110, 36));
        btnLuu.setPreferredSize(new Dimension(130, 36));
        btnHuy.addActionListener(e -> closeDialog());
        btnLuu.addActionListener(e -> handleSave());
        p.add(btnHuy);
        p.add(btnLuu);
        return p;
    }

    // ── Product picker (multi-select) ─────────────────────────────────────────

    private void openProductPicker() {
        JDialog dlg = new JDialog(SwingUtilities.getWindowAncestor(this),
                "Chon san pham", Dialog.ModalityType.APPLICATION_MODAL);
        dlg.setSize(640, 500);
        dlg.setLocationRelativeTo(this);

        JTextField search = new JTextField(20);
        search.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        search.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xCCCCCC)),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)));

        String[] cols = {"Ma SP", "Ten san pham", "Ton kho", "Gia nhap (d)"};
        DefaultTableModel pm = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable pickTable = new JTable(pm);
        pickTable.setRowHeight(34);
        pickTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        pickTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        pickTable.getTableHeader().setBackground(CLR_HDR);
        pickTable.getTableHeader().setReorderingAllowed(false);
        pickTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        Runnable fill = () -> {
            pm.setRowCount(0);
            String kw = search.getText().trim().toLowerCase();
            for (ProductDTO p : allProducts) {
                if (!kw.isEmpty() && !p.getName().toLowerCase().contains(kw)
                        && !p.getCode().toLowerCase().contains(kw)) continue;
                pm.addRow(new Object[]{
                        p.getCode(), p.getName(),
                        p.getTotalQuantity(),
                        p.getCostPrice() != null ? formatMoney(p.getCostPrice()) : "0"
                });
            }
        };
        fill.run();
        search.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate (javax.swing.event.DocumentEvent e) { fill.run(); }
            public void removeUpdate (javax.swing.event.DocumentEvent e) { fill.run(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { fill.run(); }
        });

        JButton btnAdd = makeBtn("Them vao phieu", CLR_ACCENT);
        btnAdd.setPreferredSize(new Dimension(170, 36));
        btnAdd.addActionListener(e -> {
            int[] rows = pickTable.getSelectedRows();
            if (rows.length == 0) {
                JOptionPane.showMessageDialog(dlg, "Vui long chon it nhat mot san pham.");
                return;
            }
            for (int row : rows) {
                String code = (String) pm.getValueAt(row, 0);
                for (ProductDTO p : allProducts) {
                    if (!p.getCode().equals(code)) continue;
                    if (items.stream().anyMatch(fi -> fi.productId == p.getId())) break;
                    FormItem fi = new FormItem();
                    fi.productId   = p.getId();
                    fi.productCode = p.getCode();
                    fi.productName = p.getName();
                    fi.quantity    = 1;
                    fi.unitPrice   = p.getCostPrice() != null ? p.getCostPrice() : BigDecimal.ZERO;
                    fi.subtotal    = fi.unitPrice;
                    items.add(fi);
                    break;
                }
            }
            rebuildList();
            dlg.dispose();
        });

        pickTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) btnAdd.doClick();
            }
        });

        JLabel hint = new JLabel("Giu Ctrl hoac Shift de chon nhieu san pham");
        hint.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        hint.setForeground(Color.GRAY);

        JButton btnClose = makeBtn("Dong", new Color(0x607D8B));
        btnClose.addActionListener(e -> dlg.dispose());

        JPanel topRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 10));
        topRow.add(new JLabel("Tim kiem:")); topRow.add(search);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        btnRow.add(hint); btnRow.add(btnClose); btnRow.add(btnAdd);

        dlg.add(topRow, BorderLayout.NORTH);
        dlg.add(new JScrollPane(pickTable), BorderLayout.CENTER);
        dlg.add(btnRow, BorderLayout.SOUTH);
        dlg.setVisible(true);
    }

    // ── Save handler ─────────────────────────────────────────────────────────

    private void handleSave() {
        int empIdx = cbEmployee.getSelectedIndex();
        if (empIdx <= 0 || empIdx > allEmployees.size()) {
            JOptionPane.showMessageDialog(this, "Vui long chon nhan vien thuc hien.",
                    "Loi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (items.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui long them it nhat mot san pham.",
                    "Loi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        EmployeeDTO emp = allEmployees.get(empIdx - 1);
        try {
            List<String> warnings = saveNhapKho(emp);
            String msg = "Luu phieu nhap thanh cong!";
            if (!warnings.isEmpty()) {
                msg += "\n\nSan pham sap het hang:\n- " + String.join("\n- ", warnings);
                JOptionPane.showMessageDialog(this, msg, "Canh bao ton kho", JOptionPane.WARNING_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, msg, "Thanh cong", JOptionPane.INFORMATION_MESSAGE);
            }
            closeDialog();
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Loi xac thuc", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Loi khi luu phieu: " + ex.getMessage(),
                    "Loi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private List<String> saveNhapKho(EmployeeDTO emp) throws Exception {
        int supIdx = cbSupplier.getSelectedIndex();
        if (supIdx <= 0 || supIdx > allSuppliers.size())
            throw new IllegalArgumentException("Vui long chon nha cung cap.");

        SupplierDTO sup = allSuppliers.get(supIdx - 1);

        PurchaseInvoicesDTO inv = new PurchaseInvoicesDTO();
        inv.setEmployeeId((long) emp.getId());
        inv.setEmployeeName(emp.getFullName());
        inv.setSupplierId((long) sup.getID());
        inv.setSupplierName(sup.getName());
        inv.setDateIn(LocalDateTime.now());
        inv.setNotes(txtNote.getText().trim());
        inv.setPaymentMethod("DEBT");
        inv.setPaymentStatus("PENDING");
        inv.setStatus("RECEIVED");

        List<PurchaseInvoiceItemsDTO> invItems = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;
        for (FormItem fi : items) {
            PurchaseInvoiceItemsDTO it = new PurchaseInvoiceItemsDTO();
            it.setProductId((long) fi.productId);
            it.setProductCode(fi.productCode);
            it.setProductName(fi.productName);
            it.setQuantity(fi.quantity);
            it.setUnitPrice(fi.unitPrice);
            it.setSubtotal(fi.subtotal);
            invItems.add(it);
            total = total.add(fi.subtotal);
        }
        inv.setItems(invItems);
        inv.setTotalAmount(total);

        boolean ok = new PurchaseInvoicesBUS().addPurchaseInvoice(inv);
        if (!ok) throw new RuntimeException("Luu phieu nhap that bai.");

        List<String> warnings = new ArrayList<>();
        for (ProductDTO fresh : new ProductBUS().getAllProducts()) {
            final long id = fresh.getId();
            if (items.stream().anyMatch(fi -> fi.productId == id)
                    && fresh.getTotalQuantity() < fresh.getMinStockLevel()) {
                warnings.add(fresh.getName());
            }
        }
        return warnings;
    }

    // ── Helpers ─────────────────────────────────────────────────────────────

    private void closeDialog() {
        Window w = SwingUtilities.getWindowAncestor(this);
        if (w != null) w.dispose();
    }

    private void updateSummary() {
        lblTotalItems.setText(String.valueOf(items.size()));
        lblTotalQty.setText(String.valueOf(items.stream().mapToLong(fi -> fi.quantity).sum()));
        BigDecimal total = items.stream().map(fi -> fi.subtotal).reduce(BigDecimal.ZERO, BigDecimal::add);
        String totalStr = formatMoney(total) + " d";
        lblTotalMoney.setText(totalStr);
        lblLeftTotal.setText(totalStr);
    }

    private JButton makeBtn(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBackground(bg);
        btn.setForeground(CLR_WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(7, 16, 7, 16));
        return btn;
    }

    private String formatMoney(BigDecimal val) {
        if (val == null) return "0";
        return String.format("%,.0f", val);
    }
}