package GUI.NhapKho;
import BUS.PurchasesBUS;
import BUS.EmployeeBUS;
import BUS.ProductBUS;
import BUS.PurchaseInvoicesBUS;
import BUS.SupplierBUS;
import DTO.*;
import DTO.enums.PurchaseInvoicesEnum.PurchaseInvoicesStatus;
import BUS.PurchasesBUS;
import DAO.PurchasesDAO;
import com.toedter.calendar.JDateChooser;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


class NhapKhoFormCard extends JPanel {
    private boolean isLoadingData = false;
    private static final Color CLR_PAGE   = new Color(0xF0EFF8);
    private static final Color CLR_WHITE  = Color.WHITE;
    private static final Color CLR_ACCENT = new Color(0x5C4A7F);
    private static final Color CLR_HDR    = new Color(0xD1C4E9);

    
    private List<ProductDTO>     allProducts  = new ArrayList<>();
    private List<EmployeeDTO>    allEmployees = new ArrayList<>();
    private List<SupplierDTO>    allSuppliers = new ArrayList<>();
    private final List<FormItem> items        = new ArrayList<>();
    private final PurchaseInvoicesDTO editInvoice; 

    
    private JDateChooser        dateChooser;
    private JComboBox<String>   cbEmployee;
    private final JTextArea     txtNote       = new JTextArea(3, 18);
    private JComboBox<String>   cbSupplier;
    private final JTextField    txtInvoiceRef = new JTextField();

    private final JLabel lblTotalItems = new JLabel("0");
    private final JLabel lblTotalQty   = new JLabel("0");
    private final JLabel lblTotalMoney = new JLabel("0 \u0111");
    private final JLabel lblLeftTotal  = new JLabel("0 \u0111");

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

    NhapKhoFormCard(Window dialogOwner) {
        this(dialogOwner, null);
    }

    NhapKhoFormCard(Window dialogOwner, PurchaseInvoicesDTO existing) {
        this.editInvoice = existing;
        setBackground(CLR_PAGE);
        setLayout(new BorderLayout());

        try { allProducts  = new ProductBUS().getAllProducts();  } catch (Exception ignored) {}
        try { allEmployees = new EmployeeBUS().getAllEmployees(); } catch (Exception ignored) {}
        try { allSuppliers = new SupplierBUS().getAllSuppliers(); } catch (Exception ignored) {}

        
        if (existing != null && existing.getItems() != null) {
            for (PurchaseInvoiceItemsDTO it : existing.getItems()) {
                FormItem fi  = new FormItem();
                fi.productId   = it.getProductId()  != null ? it.getProductId()  : 0L;
                fi.productCode = it.getProductCode() != null ? it.getProductCode() : "";
                fi.productName = it.getProductName() != null ? it.getProductName() : "";
                fi.quantity    = it.getQuantity()    != null ? it.getQuantity()    : 1L;
                fi.unitPrice   = it.getUnitPrice()   != null ? it.getUnitPrice()   : BigDecimal.ZERO;
                fi.subtotal    = it.getSubtotal()    != null ? it.getSubtotal()    : BigDecimal.ZERO;
                items.add(fi);
            }
        }

        add(buildHeader(), BorderLayout.NORTH);
        add(buildBody(),   BorderLayout.CENTER);
        add(buildFooter(), BorderLayout.SOUTH);

        
        if (existing == null) {
            String datePart = new java.text.SimpleDateFormat("yyyyMMdd").format(new Date());
            String randPart = String.format("%04d", (int)(Math.random() * 10000));
            txtInvoiceRef.setText("NK-" + datePart + "-" + randPart);
        }
        txtInvoiceRef.setEditable(false);
        txtInvoiceRef.setBackground(new Color(0xE8E6F0));
        txtInvoiceRef.setForeground(new Color(0x888888));
        
        if (existing != null) {
            isLoadingData = true;
            for (int i = 0; i < allEmployees.size(); i++) {
                if ((long) allEmployees.get(i).getId() == existing.getEmployeeId()) {
                    cbEmployee.setSelectedIndex(i + 1); break;
                }
            }
            

            for (int i = 0; i < allSuppliers.size(); i++) {
                if ((long) allSuppliers.get(i).getID() == existing.getSupplierId()) {
                    cbSupplier.setSelectedIndex(i + 1); break;
                }
            }
            isLoadingData = false;
            if (existing.getNotes() != null) txtNote.setText(existing.getNotes());
            if (existing.getDateIn() != null) {
                Date existingDate = Date.from(existing.getDateIn().atZone(ZoneId.systemDefault()).toInstant());
                Date today = todayMidnight();
                dateChooser.setDate(existingDate.before(today) ? today : existingDate);
            }
        }
    }

    

    private JPanel buildHeader() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(CLR_PAGE);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0xCCCCCC)),
                BorderFactory.createEmptyBorder(12, 20, 12, 20)));
        String title = editInvoice == null
                ? "+ T\u1ea1o phi\u1ebfu nh\u1eadp kho"
                : "\u270f S\u1eeda phi\u1ebfu nh\u1eadp kho";
        JLabel lbl = new JLabel(title);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lbl.setForeground(new Color(0x333333));
        p.add(lbl, BorderLayout.WEST);
        return p;
    }

    

    private JScrollPane buildBody() {
        JPanel twoCol = new JPanel(new GridBagLayout());
        twoCol.setBackground(CLR_PAGE);
        twoCol.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints tc = new GridBagConstraints();
        tc.gridy = 0; tc.weighty = 1.0;

        tc.gridx = 0; tc.weightx = 0.65; tc.fill = GridBagConstraints.BOTH;
        tc.insets = new Insets(0, 0, 0, 14);
        twoCol.add(buildLeftCard(), tc);

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
        listPanel = new JPanel(new GridBagLayout());
        listPanel.setBackground(CLR_WHITE);

        JPanel tableHeader = new JPanel(new GridBagLayout());
        tableHeader.setBackground(CLR_HDR);
        tableHeader.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
        GridBagConstraints lhg = new GridBagConstraints();
        lhg.gridy = 0; lhg.anchor = GridBagConstraints.WEST; lhg.insets = new Insets(0, 0, 0, 6);
        String[] hdrTxt = {"T\u00ean s\u1ea3n ph\u1ea9m", "SKU", "S\u1ed1 l\u01b0\u1ee3ng", "Gi\u00e1 nh\u1eadp (\u0111)", "Th\u00e0nh ti\u1ec1n (\u0111)", ""};
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

        JTextField tfSearch = new JTextField();
        tfSearch.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tfSearch.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xCCCCCC), 1),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)));

        JButton btnBrowse = makeBtn("+ Duy\u1ec7t s\u1ea3n ph\u1ea9m", CLR_ACCENT);
        btnBrowse.setPreferredSize(new Dimension(200, 36));
        btnBrowse.addActionListener(e -> openProductPicker());

        JPanel searchRow = new JPanel(new BorderLayout(8, 0));
        searchRow.setBackground(CLR_WHITE);
        searchRow.add(tfSearch, BorderLayout.CENTER);
        searchRow.add(btnBrowse, BorderLayout.EAST);

        JLabel lbTotLbl = new JLabel("T\u1ed5ng ti\u1ec1n:");
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

        JPanel leftContent = new JPanel(new GridBagLayout());
        leftContent.setBackground(CLR_WHITE);
        leftContent.setBorder(BorderFactory.createEmptyBorder(18, 22, 18, 22));
        GridBagConstraints lc = new GridBagConstraints();
        lc.gridx = 0; lc.weightx = 1.0; lc.fill = GridBagConstraints.HORIZONTAL;

        JLabel leftTitle = new JLabel("Chi ti\u1ebft s\u1ea3n ph\u1ea9m nh\u1eadp");
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
            listPanel.add(buildItemRow(items.get(i), i), g);
        }
        GridBagConstraints filler = new GridBagConstraints();
        filler.gridx = 0; filler.gridy = items.size();
        filler.weightx = 1.0; filler.weighty = 1.0; filler.fill = GridBagConstraints.BOTH;
        listPanel.add(new JLabel(), filler);
        listPanel.revalidate();
        listPanel.repaint();
        updateSummary();
    }

    private JPanel buildItemRow(FormItem fi, int vis) {
        JPanel row = new JPanel(new GridBagLayout());
        row.setBackground(vis % 2 == 0 ? CLR_WHITE : new Color(0xF7F5FF));
        row.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
        GridBagConstraints g = new GridBagConstraints();
        g.gridy = 0; g.anchor = GridBagConstraints.WEST; g.insets = new Insets(0, 0, 0, 8);

        JLabel lbName = new JLabel(fi.productName);
        lbName.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        g.gridx = 0; g.weightx = 1.0; g.fill = GridBagConstraints.HORIZONTAL;
        row.add(lbName, g);

        JLabel lbCode = new JLabel(fi.productCode);
        lbCode.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lbCode.setForeground(new Color(0x666666));
        lbCode.setPreferredSize(new Dimension(100, 24));
        g.gridx = 1; g.weightx = 0; g.fill = GridBagConstraints.NONE;
        row.add(lbCode, g);

        JLabel lbPrice = new JLabel(formatMoney(fi.unitPrice));
        lbPrice.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lbPrice.setForeground(new Color(0x555555));
        lbPrice.setPreferredSize(new Dimension(120, 24));
        lbPrice.setHorizontalAlignment(SwingConstants.RIGHT);

        JLabel lbSub = new JLabel(formatMoney(fi.subtotal));
        lbSub.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbSub.setForeground(CLR_ACCENT);
        lbSub.setPreferredSize(new Dimension(120, 24));
        lbSub.setHorizontalAlignment(SwingConstants.RIGHT);

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
        g.gridx = 2; row.add(spinner, g);
        g.gridx = 3; row.add(lbPrice, g);
        g.gridx = 4; row.add(lbSub, g);

        JButton btnX = new JButton("X");
        btnX.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnX.setBackground(new Color(0xE53935)); btnX.setForeground(CLR_WHITE);
        btnX.setFocusPainted(false); btnX.setBorderPainted(false); btnX.setOpaque(true);
        btnX.setPreferredSize(new Dimension(34, 30));
        btnX.setMargin(new Insets(0, 0, 0, 0));
        btnX.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnX.addActionListener(e -> { items.remove(fi); rebuildList(); });
        g.gridx = 5; g.insets = new Insets(0, 0, 0, 0);
        row.add(btnX, g);

        return row;
    }

    

    private JPanel buildRightCol() {
        
        dateChooser = new JDateChooser();
        dateChooser.setDateFormatString("dd/MM/yyyy");
        dateChooser.setDate(new Date());
        dateChooser.setMinSelectableDate(todayMidnight());
        dateChooser.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        dateChooser.setPreferredSize(new Dimension(0, 34));

        
        JPanel infoCard = makeRightCard("Th\u00f4ng tin chung");

        cbEmployee = new JComboBox<>();
        cbEmployee.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cbEmployee.addItem("-- Ch\u1ecdn nh\u00e2n vi\u00ean --");
        for (EmployeeDTO e : allEmployees) cbEmployee.addItem(e.getFullName());

        txtNote.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtNote.setLineWrap(true); txtNote.setWrapStyleWord(true);
        JScrollPane noteSP = new JScrollPane(txtNote);
        noteSP.setBorder(BorderFactory.createLineBorder(new Color(0xCCCCCC)));
        noteSP.setPreferredSize(new Dimension(0, 72));

        addFieldToCard(infoCard, "Ng\u00e0y nh\u1eadp:", dateChooser);
        addFieldToCard(infoCard, "Nh\u00e2n vi\u00ean:", cbEmployee);
        addFieldToCard(infoCard, "Ghi ch\u00fa:", noteSP);

        
        JPanel supplierCard = makeRightCard("Nh\u00e0 cung c\u1ea5p");
        cbSupplier = new JComboBox<>();
        cbSupplier.addActionListener(e -> {
    if (!isLoadingData) {
        onSupplierChanged();
    }
});
        cbSupplier.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cbSupplier.addItem("-- Ch\u1ecdn nh\u00e0 cung c\u1ea5p --");
        for (SupplierDTO s : allSuppliers) cbSupplier.addItem(s.getName());

        txtInvoiceRef.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtInvoiceRef.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xCCCCCC), 1),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)));
        addFieldToCard(supplierCard, "Nh\u00e0 cung c\u1ea5p:", cbSupplier);
        addFieldToCard(supplierCard, "S\u1ed1 H\u0110 nh\u1eadp:", txtInvoiceRef);

        
        JPanel summaryCard = makeRightCard("T\u1ed5ng k\u1ebft phi\u1ebfu");
        for (JLabel l : new JLabel[]{lblTotalItems, lblTotalQty, lblTotalMoney}) {
            l.setFont(new Font("Segoe UI", Font.BOLD, 14));
            l.setForeground(CLR_ACCENT);
        }
        addFieldToCard(summaryCard, "S\u1ed1 lo\u1ea1i SP:", lblTotalItems);
        addFieldToCard(summaryCard, "T\u1ed5ng s\u1ed1 l\u01b0\u1ee3ng:", lblTotalQty);
        addFieldToCard(summaryCard, "T\u1ed5ng ti\u1ec1n:", lblTotalMoney);

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

    

    private JPanel buildFooter() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 12));
        p.setBackground(CLR_PAGE);
        p.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(0xCCCCCC)));
        JButton btnHuy = makeBtn("H\u1ee7y", new Color(0xB83434));
        JButton btnLuu = makeBtn("L\u01b0u phi\u1ebfu", CLR_ACCENT);
        btnHuy.setPreferredSize(new Dimension(110, 36));
        btnLuu.setPreferredSize(new Dimension(130, 36));
        btnHuy.addActionListener(e -> closeDialog());
        btnLuu.addActionListener(e -> handleSave());
        p.add(btnHuy);
        p.add(btnLuu);
        return p;
    }

    

    private void openProductPicker() {
        
           
    if (cbSupplier.getSelectedIndex() <= 0) {
        JOptionPane.showMessageDialog(
            this,
            "Vui lòng chọn nhà cung cấp trước khi thêm sản phẩm.",
            "Thiếu thông tin",
            JOptionPane.WARNING_MESSAGE
        );
        return;
    }
    int supIdx = cbSupplier.getSelectedIndex();
SupplierDTO selectedSupplier = allSuppliers.get(supIdx - 1);
long supplierId = selectedSupplier.getID();
        JDialog dlg = new JDialog(SwingUtilities.getWindowAncestor(this),
                "Ch\u1ecdn s\u1ea3n ph\u1ea9m", Dialog.ModalityType.APPLICATION_MODAL);
        dlg.setSize(640, 500);
        dlg.setLocationRelativeTo(this);

        JTextField search = new JTextField(20);
        search.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        search.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xCCCCCC)),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)));

        String[] cols = {"M\u00e3 SP", "T\u00ean s\u1ea3n ph\u1ea9m", "T\u1ed3n kho", "Gi\u00e1 nh\u1eadp (\u0111)"};
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
                if (p.getSupplier().getID() != supplierId) continue;
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

        JButton btnAdd = makeBtn("Th\u00eam v\u00e0o phi\u1ebfu", CLR_ACCENT);
        btnAdd.setPreferredSize(new Dimension(170, 36));
        btnAdd.addActionListener(e -> {
            int[] rows = pickTable.getSelectedRows();
            if (rows.length == 0) {
                JOptionPane.showMessageDialog(dlg, "Vui l\u00f2ng ch\u1ecdn \u00edt nh\u1ea5t m\u1ed9t s\u1ea3n ph\u1ea9m.");
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

        JLabel hint = new JLabel("Gi\u1eef Ctrl ho\u1eb7c Shift \u0111\u1ec3 ch\u1ecdn nhi\u1ec1u s\u1ea3n ph\u1ea9m");
        hint.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        hint.setForeground(Color.GRAY);

        JButton btnClose = makeBtn("\u0110\u00f3ng", new Color(0x607D8B));
        btnClose.addActionListener(e -> dlg.dispose());

        JPanel topRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 10));
        topRow.add(new JLabel("T\u00ecm ki\u1ebfm:")); topRow.add(search);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        btnRow.add(hint); btnRow.add(btnClose); btnRow.add(btnAdd);

        dlg.add(topRow, BorderLayout.NORTH);
        dlg.add(new JScrollPane(pickTable), BorderLayout.CENTER);
        dlg.add(btnRow, BorderLayout.SOUTH);
        dlg.setVisible(true);
    }

    

    private void handleSave() {
        int empIdx = cbEmployee.getSelectedIndex();
        if (empIdx <= 0 || empIdx > allEmployees.size()) {
            JOptionPane.showMessageDialog(this, "Vui l\u00f2ng ch\u1ecdn nh\u00e2n vi\u00ean th\u1ef1c hi\u1ec7n.",
                    "L\u1ed7i", JOptionPane.ERROR_MESSAGE); return;
        }
        if (items.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui l\u00f2ng th\u00eam \u00edt nh\u1ea5t m\u1ed9t s\u1ea3n ph\u1ea9m.",
                    "L\u1ed7i", JOptionPane.ERROR_MESSAGE); return;
        }
        EmployeeDTO emp = allEmployees.get(empIdx - 1);
        try {
            doSave(emp);
            JOptionPane.showMessageDialog(this,
                    editInvoice == null ? "T\u1ea1o phi\u1ebfu nh\u1eadp th\u00e0nh c\u00f4ng!" : "C\u1eadp nh\u1eadt phi\u1ebfu th\u00e0nh c\u00f4ng!",
                    "Th\u00e0nh c\u00f4ng", JOptionPane.INFORMATION_MESSAGE);
            closeDialog();
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "L\u1ed7i x\u00e1c th\u1ef1c", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "L\u1ed7i khi l\u01b0u phi\u1ebfu: " + ex.getMessage(),
                    "L\u1ed7i", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void doSave(EmployeeDTO emp) throws Exception {
        int supIdx = cbSupplier.getSelectedIndex();
        if (supIdx <= 0 || supIdx > allSuppliers.size())
            throw new IllegalArgumentException("Vui l\u00f2ng ch\u1ecdn nh\u00e0 cung c\u1ea5p.");

        SupplierDTO sup = allSuppliers.get(supIdx - 1);

        Date selectedDate = dateChooser.getDate() != null ? dateChooser.getDate() : new Date();
        LocalDateTime dateIn = selectedDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

        List<PurchaseInvoiceItemsDTO> invItems = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;
        for (FormItem fi : items) {
            PurchaseInvoiceItemsDTO it = new PurchaseInvoiceItemsDTO();
            it.setProductId(fi.productId);
            it.setProductCode(fi.productCode);
            it.setProductName(fi.productName);
            it.setQuantity(fi.quantity);
            it.setUnitPrice(fi.unitPrice);
            it.setSubtotal(fi.subtotal);
            invItems.add(it);
            total = total.add(fi.subtotal);
        }
        PurchasesBUS purchaseBUS = new PurchasesBUS();

        PurchasesDTO purchase = new PurchasesDTO();
        purchase.setSupplierId((long) sup.getID());
        purchase.setEmployeeId((long) emp.getId());
        purchase.setPurchaseDate(dateIn);
        purchase.setStatus("PENDING");
        purchase.setTotalAmount(total);
        purchase.setSubtotal(total);
        purchase.setDiscountAmount(BigDecimal.ZERO);
        purchase.setTaxAmount(BigDecimal.ZERO);
        purchase.setTotalAmount(total);
long purchaseId = purchaseBUS.addPurchase(purchase);
if (purchaseId <= 0) {
    throw new RuntimeException("Tạo purchase thất bại");
}
        PurchaseInvoicesBUS bus = new PurchaseInvoicesBUS();
        boolean ok;

        if (editInvoice == null) {
            
            PurchaseInvoicesDTO inv = new PurchaseInvoicesDTO();
            inv.setPurchaseId(purchaseId);   
            inv.setEmployeeId((long) emp.getId());
            inv.setEmployeeName(emp.getFullName());
            inv.setSupplierId((long) sup.getID());
            inv.setSupplierName(sup.getName());
            inv.setDateIn(dateIn);
            inv.setNotes(txtNote.getText().trim());
            inv.setPaymentMethod("DEBT");
            inv.setPaymentStatus("PENDING");
            inv.setStatus(PurchaseInvoicesStatus.PENDING);
            inv.setItems(invItems);
            inv.setTotalAmount(total);
            inv.setDiscountAmount(BigDecimal.ZERO);
            inv.setTaxAmount(BigDecimal.ZERO);
            inv.setSubtotal(total);
            ok = bus.addPurchaseInvoice(inv);
            
        } else {
            
            editInvoice.setEmployeeId((long) emp.getId());
            editInvoice.setEmployeeName(emp.getFullName());
            editInvoice.setSupplierId((long) sup.getID());
            editInvoice.setSupplierName(sup.getName());
            editInvoice.setDateIn(dateIn);
            editInvoice.setNotes(txtNote.getText().trim());
            editInvoice.setItems(invItems);
            editInvoice.setTotalAmount(total);
            editInvoice.setSubtotal(total);
            ok = bus.updatePurchaseInvoice(editInvoice);
        }
        if (!ok) throw new RuntimeException("L\u01b0u phi\u1ebfu nh\u1eadp th\u1ea5t b\u1ea1i.");
    }

    

    private void closeDialog() {
        Window w = SwingUtilities.getWindowAncestor(this);
        if (w != null) w.dispose();
    }

    private static Date todayMidnight() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    private void updateSummary() {
        lblTotalItems.setText(String.valueOf(items.size()));
        lblTotalQty.setText(String.valueOf(items.stream().mapToLong(fi -> fi.quantity).sum()));
        BigDecimal total = items.stream().map(fi -> fi.subtotal).reduce(BigDecimal.ZERO, BigDecimal::add);
        String totalStr = formatMoney(total) + " \u0111";
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
    private void onSupplierChanged() {

    if (isLoadingData) return;   

    if (items.isEmpty()) return; 

    int confirm = JOptionPane.showConfirmDialog(
        this,
        "Đổi nhà cung cấp sẽ xoá sản phẩm đã chọn. Tiếp tục?",
        "Xác nhận",
        JOptionPane.YES_NO_OPTION
    );

    if (confirm != JOptionPane.YES_OPTION) {
        return;
    }

    items.clear();
    rebuildList();
}
}