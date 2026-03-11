package GUI.DonHang;

import BUS.SalesBUS;
import BUS.SalesInvoiceBUS;
import DTO.SaleDTO;
import DTO.SalesInvoiceDTO;
import DTO.SalesInvoiceItemDTO;
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
        String maDon = parent.tableModel.getValueAt(modelRow, 0).toString();
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

        // --- fetch DB data ---
        SalesBUS salesBUS = new SalesBUS();
        SalesInvoiceBUS salesInvoiceBUS = new SalesInvoiceBUS();
        SaleDTO sale = salesBUS.getSaleByCode(maDon);

        // header
        receipt.add(rLine("TH36", new Font("Arial", Font.BOLD, 26)));
        receipt.add(Box.createVerticalStrut(4));
        receipt.add(dotLine());
        receipt.add(rLine("Ho\u00e1 \u0111\u01a1n giao h\u00e0ng | M\u00e3 \u0111\u01a1n: " + maDon, new Font("Arial", Font.PLAIN, 12)));
        String nv = (sale != null && sale.getEmployeeName() != null)
            ? sale.getEmployeeName().toUpperCase()
            : parent.nhanVienMap.getOrDefault(maDon, "NGUY\u1ec4N TH\u1eca TH\u00c9O").toUpperCase();
        receipt.add(rLine("Qu\u1ea7y: TH36-01    NV: " + nv, new Font("Arial", Font.PLAIN, 12)));
        String timestamp = (sale != null && sale.getSaleDate() != null)
            ? sale.getSaleDate().toString()
            : parent.timeMap.getOrDefault(maDon, "");
        receipt.add(rLine(timestamp, new Font("Arial", Font.PLAIN, 12)));
        receipt.add(dotLine());
        receipt.add(gridRow(true, "T\u00ean", "SL", "\u0110\u01a1n gi\u00e1", "Th\u00e0nh ti\u1ec1n"));

        // items + totals
        long subTotal = 0;
        long vatAmt   = 0;
        long finalTot = 0;
        long disc     = 0;

        if (sale != null) {
            SalesInvoiceDTO inv = salesInvoiceBUS.getSalesInvoiceBySaleId((long) sale.getSaleID());
            if (inv != null && inv.getItems() != null && !inv.getItems().isEmpty()) {
                int stt = 1;
                for (SalesInvoiceItemDTO item : inv.getItems()) {
                    long line = item.getSubtotal() != null ? item.getSubtotal().longValue()
                              : (item.getUnitPrice() != null ? item.getUnitPrice().longValue() * item.getQuantity() : 0);
                    subTotal += line;
                    receipt.add(gridRow(false,
                        stt + "." + item.getProductName(),
                        String.valueOf(item.getQuantity()),
                        item.getUnitPrice() != null ? String.format("%,.0f", item.getUnitPrice().doubleValue()) : "-",
                        String.format("%,.0f", (double) line)));
                    stt++;
                }
                finalTot = sale.getTotalAmount() != null ? sale.getTotalAmount().longValue() : subTotal;
                vatAmt   = inv.getTaxAmount() != null ? inv.getTaxAmount().longValue() : finalTot * 10L / 110;
                disc     = sale.getDiscountAmount() != null ? sale.getDiscountAmount().longValue() : 0;
            } else {
                // no sales_invoice — parse from note field
                String note = sale.getNote();
                if (note != null && !note.isEmpty()) {
                    int stt = 1;
                    for (String part : note.split(";")) {
                        if (part.startsWith("NOTE:")) continue;
                        String[] p = part.split("\\|");
                        if (p.length >= 4) {
                            try {
                                String iName = p[1];
                                long iPrice  = Long.parseLong(p[2]);
                                int  iQty    = Integer.parseInt(p[3]);
                                if (iQty <= 0) continue;
                                long iLine   = iPrice * iQty;
                                subTotal += iLine;
                                receipt.add(gridRow(false,
                                    stt + "." + iName,
                                    String.valueOf(iQty),
                                    String.format("%,.0f", (double) iPrice),
                                    String.format("%,.0f", (double) iLine)));
                                stt++;
                            } catch (NumberFormatException ignored) {}
                        }
                    }
                    disc     = sale.getDiscountAmount() != null ? sale.getDiscountAmount().longValue() : 0;
                    finalTot = sale.getTotalAmount() != null ? sale.getTotalAmount().longValue()
                                                             : Math.max(0, subTotal - disc);
                    long netSub = subTotal - disc;
                    vatAmt = finalTot > netSub ? finalTot - netSub : 0;
                } else {
                    DonHangPanel.OrderDetailData od = parent.orderDataMap.get(maDon);
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
                        disc     = od.discAmt;
                        finalTot = Math.max(0, subTotal - disc);
                    } else {
                        finalTot = sale.getTotalAmount() != null ? sale.getTotalAmount().longValue() : 0;
                        subTotal = finalTot;
                    }
                    vatAmt = 0;
                }
            }
        } else {
            // sale not in DB — fallback to in-memory map
            DonHangPanel.OrderDetailData od = parent.orderDataMap.get(maDon);
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
                disc     = od.discAmt;
                finalTot = Math.max(0, subTotal - disc);
                vatAmt   = finalTot * 10L / 110;
            } else {
                String tongTT = parent.tableModel.getValueAt(modelRow, 4).toString();
                finalTot = Long.parseLong(tongTT.replaceAll("[^0-9]", ""));
                subTotal = finalTot;
                vatAmt   = finalTot * 10L / 110;
            }
        }

        receipt.add(dotLine());
        sumRow(receipt, "T\u1ed5ng",                   String.format("%,.0f", (double) subTotal));
        if (disc > 0) sumRow(receipt, "Chi\u1ebft kh\u1ea5u", String.format("%,.0f", (double) disc));
        sumRow(receipt, "VAT(10%)",               String.format("%,.0f", (double) vatAmt));
        sumRow(receipt, "T\u1ed5ng ti\u1ec1n",      String.format("%,.0f\u0111", (double) finalTot));
        sumRow(receipt, "Ti\u1ec1n kh\u00e1ch tr\u1ea3",  String.format("%,.0f\u0111", (double) finalTot));
        sumRow(receipt, "Ti\u1ec1n tr\u1ea3 l\u1ea1i cho kh\u00e1ch", "0");
        receipt.add(dotLine());

        JLabel bc = new JLabel("\u258c\u258c\u2588\u258c\u2588\u258c\u2588\u258c\u258c\u258c\u2588\u258c\u258c\u2588\u258c\u2588\u258c\u258c\u258c\u2588\u258c\u2588\u258c\u258c", SwingConstants.CENTER);
        bc.setFont(new Font("Courier New", Font.PLAIN, 18));
        bc.setAlignmentX(Component.CENTER_ALIGNMENT);
        bc.setMaximumSize(new Dimension(400, 30));
        receipt.add(bc);

        JLabel bcNum = new JLabel("VN" + maDon.replaceAll("[^0-9A-Za-z]", "") + "283", SwingConstants.CENTER);
        bcNum.setFont(new Font("Courier New", Font.PLAIN, 10));
        bcNum.setAlignmentX(Component.CENTER_ALIGNMENT);
        bcNum.setMaximumSize(new Dimension(400, 18));
        receipt.add(bcNum);
        receipt.add(dotLine());

        receipt.add(rLine("Ho\u00e1 \u0111\u01a1n ch\u1ec9 c\u00f3 gi\u00e1 tr\u1ecb xu\u1ea5t trong ng\u00e0y", new Font("Arial", Font.ITALIC, 10)));
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
            int align = i == 0 ? SwingConstants.LEFT
                      : i == 1 ? SwingConstants.CENTER
                      : SwingConstants.RIGHT;
            JLabel l = new JLabel(cells[i], align);
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
