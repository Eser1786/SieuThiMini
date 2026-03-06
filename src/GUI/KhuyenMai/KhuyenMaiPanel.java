package GUI.KhuyenMai;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;

public class KhuyenMaiPanel extends JPanel {

    private static final Color PAGE_BG   = new Color(0xF8F7FF);
    private static final Color RIGHT_BG  = new Color(0x5C4A7F);
    private static final Color CARD_DARK = new Color(0x2F2C35);
    private static final Color TBL_HDR   = new Color(0x3D2F5C);

    private DefaultTableModel voucherModel;
    private DefaultTableModel discountModel;

    public KhuyenMaiPanel() {
        setLayout(new BorderLayout());
        setBackground(PAGE_BG);
        add(buildPageHeader(), BorderLayout.NORTH);

        JPanel body = new JPanel(new GridBagLayout());
        body.setBackground(PAGE_BG);
        body.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));

        GridBagConstraints gc = new GridBagConstraints();
        gc.gridy = 0; gc.weighty = 1.0; gc.fill = GridBagConstraints.BOTH;

        JScrollPane leftScroll = new JScrollPane(buildLeftColumn());
        leftScroll.setBorder(null);
        leftScroll.getVerticalScrollBar().setUnitIncrement(16);
        gc.gridx = 0; gc.weightx = 0.58; gc.insets = new Insets(0, 0, 0, 14);
        body.add(leftScroll, gc);

        JPanel rightCol = buildRightColumn();
        gc.gridx = 1; gc.weightx = 0.42; gc.insets = new Insets(0, 0, 0, 0);
        body.add(rightCol, gc);

        add(body, BorderLayout.CENTER);
    }

    // ── Page header ──────────────────────────────────────────────────────────
    private JPanel buildPageHeader() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 12));
        p.setBackground(PAGE_BG);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0xDDDDDD)),
                BorderFactory.createEmptyBorder(0, 20, 0, 20)));
        JPanel bar = new JPanel();
        bar.setPreferredSize(new Dimension(5, 26));
        bar.setBackground(RIGHT_BG);
        p.add(bar);
        p.add(Box.createHorizontalStrut(12));
        JLabel lbl = new JLabel("QUẢN LÝ KHUYẾN MÃI");
        lbl.setFont(new Font("Arial", Font.BOLD, 20));
        p.add(lbl);
        return p;
    }

    // ── Left column (2 detail cards stacked) ─────────────────────────────────
    private JPanel buildLeftColumn() {
        JPanel col = new JPanel();
        col.setLayout(new BoxLayout(col, BoxLayout.Y_AXIS));
        col.setBackground(PAGE_BG);
        col.add(buildDetailCard(
                "S\u1ED1 voucher", "M\u00C3 VOUCHER", "M\u00f4 t\u1EA3",
                new String[]{"Ng\u00e0y b\u1eaft \u0111\u1ea7u :", "Gi\u00e1 tr\u1ecb t\u1ed1i thi\u1ec3u :",
                        "% Gi\u1ea3m :", "S\u1ed1 l\u01b0\u1ee3t s\u1eed d\u1ee5ng:",
                        "Ng\u00e0y k\u1ebft th\u00fac :", "Khuy\u1ebfn m\u00e3i t\u1ed1i \u0111a :"}));
        col.add(Box.createVerticalStrut(20));
        col.add(buildDetailCard(
                "S\u1ed1 khuy\u1ebfn m\u00e3i", "T\u00caN S\u1ea2N PH\u1ea8M", null,
                new String[]{"Gi\u00e1 g\u1ed1c :", "Gi\u00e1 khuy\u1ebfn m\u00e3i:",
                        "% Gi\u1ea3m :", "Ng\u00e0y b\u1eaft \u0111\u1ea7u :",
                        "Ng\u00e0y k\u1ebft th\u00fac :"}));
        col.add(Box.createVerticalStrut(20));
        return col;
    }

    /**
     * Dark-left / white-right detail card.
     * descLabel != null  → show textarea (voucher)
     * descLabel == null  → show image placeholder (product discount)
     */
    private JPanel buildDetailCard(String idLabel, String nameLabel,
                                   String descLabel, String[] fields) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(PAGE_BG);
        card.setBorder(BorderFactory.createLineBorder(new Color(0xBBBBBB)));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Title bar
        JPanel hdr = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 7));
        hdr.setBackground(new Color(0xEDE7F6));
        hdr.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0xD1C4E9)));
        JLabel lbHdr = new JLabel("Thông tin chi tiết");
        lbHdr.setFont(new Font("Arial", Font.BOLD, 13));
        lbHdr.setForeground(new Color(0x3D2F5C));
        hdr.add(lbHdr);
        card.add(hdr, BorderLayout.NORTH);

        JPanel body = new JPanel(new GridLayout(1, 2));

        // Dark left panel
        JPanel left = new JPanel();
        left.setBackground(CARD_DARK);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setBorder(BorderFactory.createEmptyBorder(20, 16, 20, 16));

        JLabel lbId = new JLabel(idLabel);
        lbId.setFont(new Font("Arial", Font.PLAIN, 11));
        lbId.setForeground(new Color(0xD1C4E9));
        lbId.setAlignmentX(Component.CENTER_ALIGNMENT);
        left.add(lbId);
        left.add(Box.createVerticalStrut(8));

        JLabel lbName = new JLabel(nameLabel);
        lbName.setFont(new Font("Arial", Font.BOLD, 15));
        lbName.setForeground(Color.WHITE);
        lbName.setAlignmentX(Component.CENTER_ALIGNMENT);
        left.add(lbName);
        left.add(Box.createVerticalStrut(6));

        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(0x88729B));
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        left.add(sep);
        left.add(Box.createVerticalStrut(12));

        if (descLabel != null) {
            JLabel lbDL = new JLabel(descLabel);
            lbDL.setFont(new Font("Arial", Font.PLAIN, 11));
            lbDL.setForeground(new Color(0xD1C4E9));
            lbDL.setAlignmentX(Component.LEFT_ALIGNMENT);
            left.add(lbDL);
            left.add(Box.createVerticalStrut(4));
            JTextArea ta = new JTextArea();
            ta.setFont(new Font("Arial", Font.PLAIN, 12));
            ta.setLineWrap(true); ta.setWrapStyleWord(true);
            ta.setBackground(new Color(0x3D2F5C));
            ta.setForeground(Color.WHITE);
            JScrollPane tas = new JScrollPane(ta);
            tas.setBorder(BorderFactory.createLineBorder(new Color(0x88729B)));
            tas.setAlignmentX(Component.LEFT_ALIGNMENT);
            tas.setPreferredSize(new Dimension(0, 85));
            tas.setMaximumSize(new Dimension(Integer.MAX_VALUE, 85));
            left.add(tas);
        } else {
            JPanel imgBox = new JPanel(new BorderLayout());
            imgBox.setBackground(new Color(0x3D2F5C));
            imgBox.setBorder(BorderFactory.createLineBorder(new Color(0x88729B)));
            imgBox.setPreferredSize(new Dimension(110, 100));
            imgBox.setMaximumSize(new Dimension(120, 100));
            imgBox.setAlignmentX(Component.CENTER_ALIGNMENT);
            JLabel imgLbl = new JLabel("\uD83D\uDCF7", SwingConstants.CENTER);
            imgLbl.setFont(new Font("Arial", Font.PLAIN, 36));
            imgBox.add(imgLbl, BorderLayout.CENTER);
            left.add(imgBox);
        }
        left.add(Box.createVerticalGlue());

        // White right panel
        JPanel right = new JPanel(new GridBagLayout());
        right.setBackground(Color.WHITE);
        right.setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));
        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.HORIZONTAL;
        g.insets = new Insets(5, 4, 5, 4);
        Font lf = new Font("Arial", Font.BOLD, 13);
        for (int i = 0; i < fields.length; i++) {
            g.gridx = 0; g.gridy = i; g.weightx = 0.45;
            JLabel l = new JLabel(fields[i]); l.setFont(lf);
            right.add(l, g);
            g.gridx = 1; g.weightx = 0.55;
            right.add(new JLabel(""), g);
        }
        g.gridx = 0; g.gridy = fields.length;
        g.gridwidth = 2; g.weighty = 1.0; g.fill = GridBagConstraints.BOTH;
        right.add(new JLabel(), g);

        body.add(left); body.add(right);
        card.add(body, BorderLayout.CENTER);
        return card;
    }

    // ── Right column ─────────────────────────────────────────────────────────
    private JPanel buildRightColumn() {
        JPanel col = new JPanel();
        col.setLayout(new BoxLayout(col, BoxLayout.Y_AXIS));
        col.setBackground(RIGHT_BG);
        col.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        voucherModel = new DefaultTableModel(
                new String[]{"S\u1ed1 voucher", "M\u00e3 voucher", "% Gi\u1ea3m", "S\u1ed1 l\u01b0\u1ee3ng"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        col.add(buildListSection("Danh s\u00e1ch voucher", "+TH\u00caM VOUCHER", voucherModel, this::showAddVoucherDialog));
        col.add(Box.createVerticalStrut(24));

        discountModel = new DefaultTableModel(
                new String[]{"M\u00e3 Gi\u1ea3m", "M\u00e3 SP", "% Gi\u1ea3m"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        col.add(buildListSection("Danh s\u00e1ch gi\u1ea3m gi\u00e1 s\u1ea3n ph\u1ea9m",
                "+TH\u00caM KHUY\u1EECN M\u00C3I", discountModel, this::showAddDiscountDialog));
        return col;
    }

    private JPanel buildListSection(String title, String addLabel, DefaultTableModel model, Runnable onAdd) {
        JPanel section = new JPanel(new BorderLayout(0, 8));
        section.setBackground(RIGHT_BG);
        section.setAlignmentX(Component.LEFT_ALIGNMENT);
        section.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

        JPanel top = new JPanel();
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        top.setBackground(RIGHT_BG);

        JLabel lbTitle = new JLabel(title);
        lbTitle.setFont(new Font("Arial", Font.BOLD | Font.ITALIC, 17));
        lbTitle.setForeground(Color.WHITE);
        lbTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        top.add(lbTitle);
        top.add(Box.createVerticalStrut(10));

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        btnRow.setBackground(RIGHT_BG);
        btnRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnRow.add(listBtn("Refresh", new Color(0x2E7D32)));
        JButton btnAdd = listBtn(addLabel, CARD_DARK);
        btnAdd.addActionListener(e -> onAdd.run());
        btnRow.add(btnAdd);
        top.add(btnRow);
        top.add(Box.createVerticalStrut(8));

        JPanel searchRow = new JPanel(new BorderLayout(6, 0));
        searchRow.setBackground(RIGHT_BG);
        searchRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        JTextField tfSearch = new JTextField();
        tfSearch.setFont(new Font("Arial", Font.PLAIN, 13));
        JButton btnSearch = listBtn("SEARCH", TBL_HDR);
        btnSearch.setPreferredSize(new Dimension(86, 30));
        searchRow.add(tfSearch, BorderLayout.CENTER);
        searchRow.add(btnSearch, BorderLayout.EAST);
        top.add(searchRow);
        top.add(Box.createVerticalStrut(8));

        section.add(top, BorderLayout.NORTH);

        // Table
        JTable table = new JTable(model);
        table.setFont(new Font("Arial", Font.PLAIN, 13));
        table.setRowHeight(30);
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(true);
        table.setGridColor(new Color(0xD1C4E9));
        table.setFillsViewportHeight(true);
        table.setPreferredScrollableViewportSize(new Dimension(300, 180));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        table.getTableHeader().setBackground(TBL_HDR);
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setReorderingAllowed(false);
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable t, Object v, boolean sel, boolean foc, int r, int c) {
                Component comp = super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                comp.setBackground(sel ? new Color(0xC5B3E6)
                        : (r % 2 == 0 ? Color.WHITE : new Color(0xE8E0F0)));
                return comp;
            }
        });

        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createLineBorder(TBL_HDR));
        section.add(sp, BorderLayout.CENTER);
        return section;
    }

    private JButton listBtn(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.BOLD, 12));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void showAddVoucherDialog() {
        JDialog dlg = new JDialog(SwingUtilities.getWindowAncestor(this),
                "Th\u00eam Voucher", Dialog.ModalityType.APPLICATION_MODAL);
        dlg.setLayout(new BorderLayout());
        String[] labels = {
            "M\u00e3 voucher:", "M\u00f4 t\u1ea3:", "Ng\u00e0y b\u1eaft \u0111\u1ea7u (dd/MM/yyyy):",
            "Gi\u00e1 tr\u1ecb t\u1ed1i thi\u1ec3u (\u0111):", "% Gi\u1ea3m:", "S\u1ed1 l\u01b0\u1ee3t s\u1eed d\u1ee5ng:",
            "Ng\u00e0y k\u1ebft th\u00fac (dd/MM/yyyy):", "Khuy\u1ebfn m\u00e3i t\u1ed1i \u0111a (\u0111):"
        };
        JTextField[] tfs = new JTextField[labels.length];
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(16, 20, 8, 20));
        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.HORIZONTAL; g.insets = new Insets(5, 5, 5, 5);
        for (int i = 0; i < labels.length; i++) {
            g.gridx = 0; g.gridy = i; g.weightx = 0.5;
            JLabel l = new JLabel(labels[i]);
            l.setFont(new Font("Arial", Font.BOLD, 13));
            form.add(l, g);
            g.gridx = 1; g.weightx = 0.5;
            tfs[i] = new JTextField(18);
            tfs[i].setFont(new Font("Arial", Font.PLAIN, 13));
            form.add(tfs[i], g);
        }
        dlg.add(form, BorderLayout.CENTER);
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
        JButton btnLuu = new JButton("L\u01b0u");
        btnLuu.setBackground(new Color(0x5C4A7F));
        btnLuu.setForeground(Color.WHITE);
        btnLuu.setFocusPainted(false);
        JButton btnHuy = new JButton("H\u1ee7y");
        btnHuy.setFocusPainted(false);
        btns.add(btnHuy); btns.add(btnLuu);
        dlg.add(btns, BorderLayout.SOUTH);
        btnLuu.addActionListener(e -> {
            String ma = tfs[0].getText().trim();
            if (ma.isEmpty()) {
                JOptionPane.showMessageDialog(dlg, "Vui l\u00f2ng nh\u1eadp m\u00e3 voucher.",
                        "Thi\u1ebfu th\u00f4ng tin", JOptionPane.WARNING_MESSAGE);
                return;
            }
            voucherModel.addRow(new Object[]{
                voucherModel.getRowCount() + 1, ma,
                tfs[4].getText().trim(), tfs[5].getText().trim()
            });
            dlg.dispose();
        });
        btnHuy.addActionListener(e -> dlg.dispose());
        dlg.pack();
        dlg.setMinimumSize(new Dimension(440, dlg.getPreferredSize().height));
        dlg.setLocationRelativeTo(this);
        dlg.setVisible(true);
    }

    private void showAddDiscountDialog() {
        JDialog dlg = new JDialog(SwingUtilities.getWindowAncestor(this),
                "Th\u00eam Khuy\u1ebfn M\u00e3i S\u1ea3n Ph\u1ea9m", Dialog.ModalityType.APPLICATION_MODAL);
        dlg.setLayout(new BorderLayout());
        String[] labels = {
            "M\u00e3 gi\u1ea3m:", "M\u00e3 s\u1ea3n ph\u1ea9m:", "% Gi\u1ea3m:",
            "Ng\u00e0y b\u1eaft \u0111\u1ea7u (dd/MM/yyyy):", "Ng\u00e0y k\u1ebft th\u00fac (dd/MM/yyyy):"
        };
        JTextField[] tfs = new JTextField[labels.length];
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(16, 20, 8, 20));
        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.HORIZONTAL; g.insets = new Insets(5, 5, 5, 5);
        for (int i = 0; i < labels.length; i++) {
            g.gridx = 0; g.gridy = i; g.weightx = 0.5;
            JLabel l = new JLabel(labels[i]);
            l.setFont(new Font("Arial", Font.BOLD, 13));
            form.add(l, g);
            g.gridx = 1; g.weightx = 0.5;
            tfs[i] = new JTextField(18);
            tfs[i].setFont(new Font("Arial", Font.PLAIN, 13));
            form.add(tfs[i], g);
        }
        dlg.add(form, BorderLayout.CENTER);
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
        JButton btnLuu = new JButton("L\u01b0u");
        btnLuu.setBackground(new Color(0x5C4A7F));
        btnLuu.setForeground(Color.WHITE);
        btnLuu.setFocusPainted(false);
        JButton btnHuy = new JButton("H\u1ee7y");
        btnHuy.setFocusPainted(false);
        btns.add(btnHuy); btns.add(btnLuu);
        dlg.add(btns, BorderLayout.SOUTH);
        btnLuu.addActionListener(e -> {
            String maSP = tfs[1].getText().trim();
            if (maSP.isEmpty()) {
                JOptionPane.showMessageDialog(dlg, "Vui l\u00f2ng nh\u1eadp m\u00e3 s\u1ea3n ph\u1ea9m.",
                        "Thi\u1ebfu th\u00f4ng tin", JOptionPane.WARNING_MESSAGE);
                return;
            }
            discountModel.addRow(new Object[]{
                tfs[0].getText().trim(), maSP, tfs[2].getText().trim()
            });
            dlg.dispose();
        });
        btnHuy.addActionListener(e -> dlg.dispose());
        dlg.pack();
        dlg.setMinimumSize(new Dimension(420, dlg.getPreferredSize().height));
        dlg.setLocationRelativeTo(this);
        dlg.setVisible(true);
    }
}


