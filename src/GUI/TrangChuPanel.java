package GUI;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * Dashboard / home panel with summary cards and a small chart.
 */
public class TrangChuPanel extends JPanel {
    public TrangChuPanel() {
        setLayout(new BorderLayout(0, 0));
        setBackground(new Color(0xF8F7FF));

        // four summary cards
        JPanel topCards = new JPanel(new GridLayout(1, 4, 30, 0));
        topCards.setBackground(new Color(0xF8F7FF));
        topCards.setBorder(BorderFactory.createEmptyBorder(18, 18, 12, 18));

        Object[][] cardData = {
            { "💳", "Doanh thu:",    "15.000.000 VND", new Color(0xD4F4E2), new Color(0x5CB85C) },
            { "🛒", "Đơn hàng mới:", "25 đơn",         new Color(0xCDE8FF), new Color(0x4A90D9) },
            { "⚠",  "Sản phẩm sắp hết:", "5 mặt hàng", new Color(0xFFF3CD), new Color(0xF0AD4E) },
            { "👤", "Khách hàng mới:", "+10 khách",    new Color(0xF5D0F5), new Color(0xAB47BC) },
        };

        for (Object[] d : cardData) {
            JPanel card = new JPanel(new BorderLayout(10, 0)) {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(getBackground());
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
                    g2.setColor(((Color) d[4]).darker());
                    g2.setStroke(new BasicStroke(2f));
                    g2.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 18, 18);
                    g2.dispose();
                }
            };
            card.setBackground((Color) d[3]);
            card.setOpaque(false);
            card.setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));

            JLabel icon = new JLabel((String) d[0]);
            icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
            icon.setHorizontalAlignment(SwingConstants.CENTER);
            icon.setPreferredSize(new Dimension(42, 42));

            JPanel txtPanel = new JPanel(new GridLayout(2, 1, 0, 2));
            txtPanel.setOpaque(false);

            JLabel lbTitle = new JLabel((String) d[1]);
            lbTitle.setFont(new Font("Arial", Font.BOLD, 15));
            lbTitle.setForeground(new Color(0x444444));

            JLabel lbVal = new JLabel((String) d[2]);
            lbVal.setFont(new Font("Arial", Font.BOLD, 17));
            lbVal.setForeground(new Color(0x222222));

            txtPanel.add(lbTitle);
            txtPanel.add(lbVal);

            card.add(icon,     BorderLayout.WEST);
            card.add(txtPanel, BorderLayout.CENTER);
            topCards.add(card);
        }

        // center area with chart and recent orders
        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 14, 0));
        centerPanel.setBackground(new Color(0xF8F7FF));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(0, 18, 18, 18));

        // chart card
        JPanel chartCard = UIUtils.createCard();
        JLabel chartTitle = new JLabel("Sales Growth (Tạm thời để z đi r mốt có thông số đầy đủ r sửa lại)");
        chartTitle.setFont(new Font("Playfair Display", Font.BOLD, 20));
        chartTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        int[] chartValues = { 10, 22, 38, 32, 55, 52, 88, 100 };
        String[] chartLabels = { "T2", "T3", "T4", "T5", "T6", "T7", "CN" };

        JPanel chart = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth(), h = getHeight();
                int padL = 40, padR = 20, padT = 20, padB = 30;
                int chartW = w - padL - padR;
                int chartH = h - padT - padB;
                int n = chartValues.length;

                g2.setColor(new Color(0xDDDDDD));
                g2.setStroke(new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{4}, 0));
                for (int i = 0; i <= 4; i++) {
                    int y = padT + chartH * i / 4;
                    g2.drawLine(padL, y, padL + chartW, y);
                    g2.setColor(new Color(0x999999));
                    g2.setFont(new Font("Arial", Font.PLAIN, 11));
                    g2.drawString(String.valueOf(100 - 25 * i), 2, y + 4);
                    g2.setColor(new Color(0xDDDDDD));
                }

                int[] xs = new int[n + 2];
                int[] ys = new int[n + 2];
                for (int i = 0; i < n; i++) {
                    xs[i] = padL + i * chartW / (n - 1);
                    ys[i] = padT + chartH - chartValues[i] * chartH / 100;
                }
                xs[n] = padL + chartW; ys[n] = padT + chartH;
                xs[n + 1] = padL;       ys[n + 1] = padT + chartH;

                g2.setColor(new Color(0xB8A9D9));
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.35f));
                g2.fillPolygon(xs, ys, n + 2);
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));

                g2.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.setColor(new Color(0x7B68AE));
                for (int i = 0; i < n - 1; i++) {
                    g2.drawLine(xs[i], ys[i], xs[i + 1], ys[i + 1]);
                }

                g2.setColor(new Color(0x7B68AE));
                for (int i = 0; i < n; i++) {
                    g2.fillOval(xs[i] - 4, ys[i] - 4, 8, 8);
                }

                g2.setColor(new Color(0x666666));
                g2.setFont(new Font("Arial", Font.PLAIN, 11));
                for (int i = 0; i < chartLabels.length; i++) {
                    int xi = padL + i * chartW / (n - 1);
                    g2.drawString(chartLabels[i], xi - 8, h - 6);
                }
            }
        };
        chart.setOpaque(false);
        chart.setPreferredSize(new Dimension(0, 260));

        chartCard.add(chartTitle, BorderLayout.NORTH);
        chartCard.add(chart,      BorderLayout.CENTER);

        // recent orders table
        JPanel orderCard = UIUtils.createCard();
        JLabel orderTitle = new JLabel("Đơn hàng gần đây");
        orderTitle.setFont(new Font("Playfair Display", Font.BOLD, 20));
        orderTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        String[] orderCols = { "Mã đơn", "Khách hàng", "Trạng thái" };
        Object[][] orderRows = {
            { "HD001", "Nguyễn Văn A", "Đang giao" },
            { "HD002", "Nguyễn Văn N", "Đã giao"   },
            { "HD003", "Nguyễn Văn D", "Bị boom"   },
        };

        DefaultTableModel orderModel = new DefaultTableModel(orderRows, orderCols) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable orderTable = new JTable(orderModel);
        orderTable.setFont(new Font("Arial", Font.PLAIN, 16));
        orderTable.setRowHeight(38);
        orderTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 16));
        orderTable.getTableHeader().setBackground(new Color(0xAF9FCB));
        orderTable.getTableHeader().setForeground(Color.WHITE);
        orderTable.setShowVerticalLines(false);
        orderTable.setGridColor(new Color(0xEEEEEE));

        DefaultTableCellRenderer statusRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v,
                    boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                setHorizontalAlignment(SwingConstants.CENTER);
                if (!sel) {
                    setBackground(row % 2 == 0 ? Color.WHITE : new Color(0xF3F0FA));
                }
                if (col == 2) {
                    String val = v == null ? "" : v.toString();
                    switch (val) {
                        case "Đang giao" -> setForeground(new Color(0x1976D2));
                        case "Đã giao"   -> setForeground(new Color(0x388E3C));
                        case "Bị boom"   -> setForeground(new Color(0xC62828));
                        default          -> setForeground(Color.BLACK);
                    }
                } else {
                    setForeground(Color.BLACK);
                }
                return this;
            }
        };
        for (int i = 0; i < 3; i++)
            orderTable.getColumnModel().getColumn(i).setCellRenderer(statusRenderer);

        orderCard.add(orderTitle,              BorderLayout.NORTH);
        orderCard.add(new JScrollPane(orderTable), BorderLayout.CENTER);

        centerPanel.add(chartCard);
        centerPanel.add(orderCard);

        add(topCards,    BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
    }
   
}
