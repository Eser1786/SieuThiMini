package GUI.DonHang;

import javax.swing.*;
import java.awt.*;
import java.awt.print.*;

class DonHangInvoiceCard extends JPanel {

    private final DonHangPanel parent;
    private JPanel invoiceCenter;

    DonHangInvoiceCard(DonHangPanel parent) {
        this.parent = parent;
        setLayout(new BorderLayout());
        buildUI();
    }

    private void buildUI() {
        setBackground(new Color(0xF4F4F4));

        /* Header */
        JPanel ivHeader = new JPanel(new BorderLayout());
        ivHeader.setBackground(new Color(0xF4F4F4));
        ivHeader.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0xDEE2E6)),
                BorderFactory.createEmptyBorder(12, 20, 12, 20)));
        JLabel ivTitle = new JLabel("In mã vận đơn");
        ivTitle.setFont(new Font("Arial", Font.BOLD, 16));
        ivTitle.setForeground(new Color(0x999999));
        JButton btnBack = new JButton("← Quay lại danh sách");
        btnBack.setFont(new Font("Arial", Font.BOLD, 22));
        btnBack.setBackground(new Color(0x9B8EA8));
        btnBack.setForeground(Color.WHITE);
        btnBack.setFocusPainted(false);
        btnBack.setBorderPainted(false);
        btnBack.setPreferredSize(new Dimension(300, 48));
        btnBack.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnBack.addActionListener(e -> parent.showCard(DonHangPanel.CARD_DETAIL));
        ivHeader.add(ivTitle, BorderLayout.WEST);
        ivHeader.add(btnBack, BorderLayout.EAST);
        add(ivHeader, BorderLayout.NORTH);

        invoiceCenter = new JPanel(new GridBagLayout());
        invoiceCenter.setBackground(new Color(0xF4F4F4));
        JScrollPane ivScroll = new JScrollPane(invoiceCenter);
        ivScroll.setBorder(BorderFactory.createEmptyBorder());
        ivScroll.getVerticalScrollBar().setUnitIncrement(16);
        add(ivScroll, BorderLayout.CENTER);

        JPanel ivFooter = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 16));
        ivFooter.setBackground(new Color(0xF4F4F4));
        JButton btnPrint = DonHangPanel.makeFootBtn("In", new Color(0x8C9EFF));
        btnPrint.setPreferredSize(new Dimension(120, 42));
        btnPrint.addActionListener(e -> {
            if (invoiceCenter.getComponentCount() > 0)
                printPanel((JPanel) invoiceCenter.getComponent(0));
        });
        ivFooter.add(btnPrint);
        add(ivFooter, BorderLayout.SOUTH);
    }

    void loadInvoice(int modelRow) {
        String maDon  = parent.tableModel.getValueAt(modelRow, 0).toString();
        String tongTT = parent.tableModel.getValueAt(modelRow, 4).toString();
        invoiceCenter.removeAll();

        JPanel receipt = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 0, 0, 25));
                g2.fillRoundRect(5, 7, getWidth() - 3, getHeight() - 3, 12, 12);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth() - 4, getHeight() - 4, 12, 12);
            }
        };
        receipt.setOpaque(false);
        receipt.setLayout(new BoxLayout(receipt, BoxLayout.Y_AXIS));
        receipt.setBorder(BorderFactory.createEmptyBorder(22, 30, 22, 30));
        receipt.setPreferredSize(new Dimension(430, 660));

        receipt.add(rLine("TH36", new Font("Arial", Font.BOLD, 26)));
        receipt.add(Box.createVerticalStrut(4));
        receipt.add(dotLine());
        receipt.add(rLine("Hoá đơn giao hàng | Mã đơn: " + maDon, new Font("Arial", Font.PLAIN, 12)));
        String nv = parent.nhanVienMap.getOrDefault(maDon, "NGUYỄN THỊ THÉO").toUpperCase();
        receipt.add(rLine("Quầy: TH36-01    NV: " + nv, new Font("Arial", Font.PLAIN, 12)));
        String timestamp = parent.timeMap.getOrDefault(maDon, "05/03/2026 (22:28)");
        receipt.add(rLine(timestamp, new Font("Arial", Font.PLAIN, 12)));
        receipt.add(dotLine());
        receipt.add(gridRow(true, "Tên", "SL", "Đơn giá", "Thành tiền"));

        DonHangPanel.OrderDetailData od = parent.orderDataMap.get(maDon);
        long subTotal = 0;
        if (od != null && !od.items.isEmpty()) {
            int stt = 1;
            for (DonHangPanel.OrderDetailData.Item it : od.items) {
                long line = it.unitPrice * it.qty; subTotal += line;
                receipt.add(gridRow(false,
                    stt + "." + it.name,
                    String.valueOf(it.qty),
                    String.format("%,.0f", (double) it.unitPrice),
                    String.format("%,.0f", (double) line)));
                stt++;
            }
        } else {
            String[][] fallback = {
                { "1.Nước F trái K",  "2", "25.000", "50.000" },
                { "2.Mì ý sốt kem",   "1", "36.000", "36.000" },
                { "3.Pepsi kg calo",  "1", "10.000", "10.000" },
            };
            for (String[] s : fallback) {
                subTotal += Long.parseLong(s[3].replaceAll("[^0-9]", ""));
                receipt.add(gridRow(false, s));
            }
        }
        receipt.add(dotLine());

        long disc = od != null ? od.discAmt : 0;
        long tot  = od != null ? Math.max(0, subTotal - disc)
                               : Long.parseLong(tongTT.replaceAll("[^0-9]", ""));
        long vat  = tot * 10 / 110;

        sumRow(receipt, "Tổng",                   String.format("%,.0f", (double) subTotal));
        if (disc > 0) sumRow(receipt, "Chiết khấu", String.format("%,.0f", (double) disc));
        sumRow(receipt, "VAT(10%)",               String.format("%,.0f", (double) vat));
        sumRow(receipt, "Tổng tiền",              String.format("%,.0fđ", (double) tot));
        sumRow(receipt, "Tiền khách trả",         String.format("%,.0fđ", (double) tot));
        sumRow(receipt, "Tiền trả lại cho khách", "0");
        receipt.add(dotLine());

        JLabel bc = new JLabel("▌▌█▌█▌█▌▌▌█▌▌█▌█▌▌▌█▌█▌▌", SwingConstants.CENTER);
        bc.setFont(new Font("Courier New", Font.PLAIN, 18));
        bc.setAlignmentX(Component.CENTER_ALIGNMENT);
        bc.setMaximumSize(new Dimension(400, 30));
        receipt.add(bc);

        JLabel bcNum = new JLabel("VN2845598375038283", SwingConstants.CENTER);
        bcNum.setFont(new Font("Courier New", Font.PLAIN, 10));
        bcNum.setAlignmentX(Component.CENTER_ALIGNMENT);
        bcNum.setMaximumSize(new Dimension(400, 18));
        receipt.add(bcNum);
        receipt.add(dotLine());

        receipt.add(rLine("Hoá đơn chỉ có giá trị xuất trong ngày", new Font("Arial", Font.ITALIC, 10)));
        receipt.add(rLine("Hotline: 09437767345", new Font("Arial", Font.ITALIC, 10)));

        invoiceCenter.add(receipt, new GridBagConstraints());
        invoiceCenter.revalidate();
        invoiceCenter.repaint();
    }

    private void printPanel(JPanel panel) {
        PrinterJob job = PrinterJob.getPrinterJob();
        job.setPrintable((g, pf, pi) -> {
            if (pi > 0) return Printable.NO_SUCH_PAGE;
            Graphics2D g2 = (Graphics2D) g;
            g2.translate(pf.getImageableX(), pf.getImageableY());
            double s = Math.min(pf.getImageableWidth() / panel.getWidth(),
                    pf.getImageableHeight() / panel.getHeight());
            g2.scale(s, s);
            panel.printAll(g2);
            return Printable.PAGE_EXISTS;
        });
        if (job.printDialog())
            try { job.print(); }
            catch (PrinterException ex) {
                JOptionPane.showMessageDialog(this, "Lỗi in: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
    }

    /* ── receipt helpers ── */
    private JLabel rLine(String t, Font f) {
        JLabel l = new JLabel(t, SwingConstants.CENTER);
        l.setFont(f); l.setAlignmentX(Component.CENTER_ALIGNMENT);
        l.setMaximumSize(new Dimension(480, 22)); return l;
    }

    private JLabel dotLine() {
        JLabel l = new JLabel("................................................................", SwingConstants.CENTER);
        l.setFont(new Font("Courier New", Font.PLAIN, 11));
        l.setAlignmentX(Component.CENTER_ALIGNMENT);
        l.setMaximumSize(new Dimension(480, 18)); return l;
    }

    private JPanel gridRow(boolean bold, String... cells) {
        JPanel row = new JPanel(new GridBagLayout());
        row.setOpaque(false); row.setMaximumSize(new Dimension(480, 24));
        GridBagConstraints gc = new GridBagConstraints();
        gc.gridy = 0; gc.fill = GridBagConstraints.HORIZONTAL;
        int[] weights = { 50, 12, 19, 19 };
        for (int i = 0; i < cells.length; i++) {
            gc.gridx = i;
            gc.weightx = i < weights.length ? weights[i] : 10;
            JLabel l = new JLabel(cells[i], i == 0 ? SwingConstants.LEFT : SwingConstants.CENTER);
            l.setFont(new Font("Arial", bold ? Font.BOLD : Font.PLAIN, 12));
            row.add(l, gc);
        }
        return row;
    }

    private void sumRow(JPanel receipt, String name, String amount) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false); row.setMaximumSize(new Dimension(400, 20));
        JLabel ln = new JLabel(name); ln.setFont(new Font("Arial", Font.PLAIN, 11));
        JLabel la = new JLabel(amount, SwingConstants.RIGHT); la.setFont(new Font("Arial", Font.PLAIN, 11));
        row.add(ln, BorderLayout.WEST); row.add(la, BorderLayout.EAST);
        receipt.add(row);
    }
}
