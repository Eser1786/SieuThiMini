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
        JButton btnBack = new JButton("← Quay lại");
        btnBack.setFont(new Font("Arial", Font.BOLD, 14));
        btnBack.setBackground(new Color(0xFFFFFF));
        btnBack.setForeground(new Color(0x666666));
        btnBack.setFocusPainted(false);
        btnBack.setPreferredSize(new Dimension(130, 36));
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
        receipt.setPreferredSize(new Dimension(370, 570));

        receipt.add(rLine("TH36", new Font("Arial", Font.BOLD, 26)));
        receipt.add(Box.createVerticalStrut(4));
        receipt.add(dotLine());
        receipt.add(rLine("Hoá đơn giao hàng | Mã đơn: " + maDon, new Font("Arial", Font.PLAIN, 12)));
        receipt.add(rLine("Quầy: TH36-01    NV: NGUYỄN THỊ THÉO",  new Font("Arial", Font.PLAIN, 12)));
        receipt.add(rLine("05/03/2026  (22:28)", new Font("Arial", Font.PLAIN, 12)));
        receipt.add(dotLine());
        receipt.add(gridRow(true, "Tên", "SL", "Đơn giá", "Thành tiền"));

        String[][] items = {
                { "1.Cơm nắm cá hồi mayo", "1", "16.000", "16.000" },
                { "2.Mì ý sốt kem",        "1", "36.000", "36.000" },
                { "3.Pepsi không calo",     "1", "10.000", "10.000" },
                { "4.Kem si cu la",         "2", "18.000", "38.000" },
        };
        for (String[] it : items) receipt.add(gridRow(false, it));
        receipt.add(dotLine());

        sumRow(receipt, "Tổng",                   "100.000");
        sumRow(receipt, "Chiết khấu",             "10.000");
        sumRow(receipt, "VAT(10%)",               "3.636");
        sumRow(receipt, "Tổng tiền",              tongTT);
        sumRow(receipt, "Tiền khách trả",         tongTT);
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
        l.setMaximumSize(new Dimension(400, 22)); return l;
    }

    private JLabel dotLine() {
        JLabel l = new JLabel("................................................................", SwingConstants.CENTER);
        l.setFont(new Font("Courier New", Font.PLAIN, 11));
        l.setAlignmentX(Component.CENTER_ALIGNMENT);
        l.setMaximumSize(new Dimension(400, 18)); return l;
    }

    private JPanel gridRow(boolean bold, String... cells) {
        JPanel row = new JPanel(new GridLayout(1, cells.length));
        row.setOpaque(false); row.setMaximumSize(new Dimension(400, 24));
        for (String c : cells) {
            JLabel l = new JLabel(c, SwingConstants.CENTER);
            l.setFont(new Font("Arial", bold ? Font.BOLD : Font.PLAIN, 12));
            row.add(l);
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
