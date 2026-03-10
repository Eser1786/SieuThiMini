package GUI.TrangChu;

import BUS.CustomerBUS;
import BUS.ProductBUS;
import BUS.SalesBUS;
import DTO.CustomerDTO;
import DTO.SaleDTO;
import DTO.enums.CustomerEnum.CustomerType;
import GUI.UIUtils;
import java.awt.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/**
 * Dashboard / home panel with summary cards and a small chart.
 */
public class TrangChuPanel extends JPanel {
    
    private JPanel topCards;
    private JPanel orderCard;
    private JTable orderTable;
    private DefaultTableModel orderModel;
    private JComboBox<String> statusFilter;
    private JComboBox<String> paymentFilter;
    
    private int[] chartValues;
    private String[] chartLabels;
    private JPanel chartPanel;
    private JComboBox<String> cbChartPeriod;
    private JLabel revenueTitle;
    
    public TrangChuPanel() {
        setLayout(new BorderLayout(0, 0));
        setBackground(new Color(0xF8F7FF));

        // four summary cards
        topCards = new JPanel(new GridLayout(1, 4, 30, 0));
        topCards.setBackground(new Color(0xF8F7FF));
        topCards.setBorder(BorderFactory.createEmptyBorder(18, 18, 12, 18));
        
        try {
            loadDashboardData();
        } catch (Exception e) {
            // Nếu không load được data, hiển thị mặc định
            e.printStackTrace();
            // Có thể thêm thông báo lỗi ở đây
        }

        // center area with 3 charts on left and orders table on right
        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 14, 0));
        centerPanel.setBackground(new Color(0xF8F7FF));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(0, 18, 18, 18));

        // ===== LEFT PANEL: 3 Charts =====
        JPanel chartsPanel = new JPanel(new GridLayout(3, 1, 0, 14));
        chartsPanel.setBackground(new Color(0xF8F7FF));

        // ===== Chart 1: Revenue Chart =====
        JPanel revenueCard = UIUtils.createCard();
        JPanel revenueHeader = new JPanel(new BorderLayout());
        revenueHeader.setOpaque(false);
        revenueTitle = new JLabel("Doanh thu tuần này");
        revenueTitle.setFont(new Font("Playfair Display", Font.BOLD, 16));
        cbChartPeriod = new JComboBox<>(new String[]{"Tuần", "Tháng", "Năm"});
        cbChartPeriod.setFont(new Font("Arial", Font.PLAIN, 11));
        cbChartPeriod.setPreferredSize(new Dimension(80, 24));
        cbChartPeriod.addActionListener(e -> refreshChart());
        JButton btnRefresh1 = new JButton("Làm mới");
        btnRefresh1.setFont(new Font("Arial", Font.PLAIN, 10));
        btnRefresh1.setFocusPainted(false);
        btnRefresh1.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRefresh1.addActionListener(e -> refreshChart());
        JPanel revenueRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 0));
        revenueRight.setOpaque(false);
        revenueRight.add(cbChartPeriod);
        revenueRight.add(btnRefresh1);
        revenueHeader.add(revenueTitle, BorderLayout.WEST);
        revenueHeader.add(revenueRight, BorderLayout.EAST);
        revenueHeader.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        
        chartLabels = new String[]{ "T2", "T3", "T4", "T5", "T6", "T7", "CN" };
        loadChartData();
        JPanel chart = createChart();
        chartPanel = chart;
        
        revenueCard.add(revenueHeader, BorderLayout.NORTH);
        revenueCard.add(chartPanel, BorderLayout.CENTER);

        // ===== Chart 2: Customer Chart=====
        JPanel customerCard = UIUtils.createCard();
        JPanel customerHeaderPanel = new JPanel(new BorderLayout());
        customerHeaderPanel.setOpaque(false);
        JLabel customerTitle = new JLabel("Khách hàng mới (tuần này)");
        customerTitle.setFont(new Font("Playfair Display", Font.BOLD, 16));
        JButton btnRefresh2 = new JButton("Làm mới");
        btnRefresh2.setFont(new Font("Arial", Font.PLAIN, 10));
        btnRefresh2.setFocusPainted(false);
        btnRefresh2.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRefresh2.addActionListener(e -> refreshCustomerChart());
        customerHeaderPanel.add(customerTitle, BorderLayout.WEST);
        customerHeaderPanel.add(btnRefresh2, BorderLayout.EAST);
        customerHeaderPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        
        JPanel customerChart = createCustomerChart();
        customerCard.add(customerHeaderPanel, BorderLayout.NORTH);
        customerCard.add(customerChart, BorderLayout.CENTER);

        // ===== Chart 3: Loyalty Points Chart =====
        JPanel loyaltyCard = UIUtils.createCard();
        JPanel loyaltyChartHeaderPanel = new JPanel(new BorderLayout());
        loyaltyChartHeaderPanel.setOpaque(false);
        JLabel loyaltyChartTitle = new JLabel("Top 5 Khách Hàng (Loyalty Points)");
        loyaltyChartTitle.setFont(new Font("Playfair Display", Font.BOLD, 16));
        JButton btnRefresh3 = new JButton("Làm mới");
        btnRefresh3.setFont(new Font("Arial", Font.PLAIN, 10));
        btnRefresh3.setFocusPainted(false);
        btnRefresh3.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRefresh3.addActionListener(e -> refreshLoyaltyPointsChart());
        loyaltyChartHeaderPanel.add(loyaltyChartTitle, BorderLayout.WEST);
        loyaltyChartHeaderPanel.add(btnRefresh3, BorderLayout.EAST);
        loyaltyChartHeaderPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        
        JPanel loyaltyChart = createLoyaltyPointsChart();
        loyaltyCard.add(loyaltyChartHeaderPanel, BorderLayout.NORTH);
        loyaltyCard.add(loyaltyChart, BorderLayout.CENTER);

        // Add 3 charts to left panel
        chartsPanel.add(revenueCard);
        chartsPanel.add(customerCard);
        chartsPanel.add(loyaltyCard);

        // ===== RIGHT PANEL: Recent Orders Table =====
        orderCard = UIUtils.createCard();
        JPanel orderHeader = new JPanel(new BorderLayout());
        orderHeader.setOpaque(false);
        
        JLabel orderTitle = new JLabel("Đơn hàng gần đây");
        orderTitle.setFont(new Font("Playfair Display", Font.BOLD, 16));
        
        // Filter controls
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        filterPanel.setOpaque(false);
        
        filterPanel.add(new JLabel("Trạng thái:"));
        statusFilter = new JComboBox<>(new String[]{"Tất cả", "PENDING", "CONFIRMED", "SHIPPING", "DELIVERING", "COMPLETED", "CANCELLED"});
        statusFilter.addActionListener(e -> loadRecentOrders());
        filterPanel.add(statusFilter);
        
        filterPanel.add(new JLabel("Thanh toán:"));
        paymentFilter = new JComboBox<>(new String[]{"Tất cả", "CASH", "CARD", "TRANSFER"});
        paymentFilter.addActionListener(e -> loadRecentOrders());
        filterPanel.add(paymentFilter);
        
        orderHeader.add(orderTitle, BorderLayout.WEST);
        orderHeader.add(filterPanel, BorderLayout.EAST);
        
        orderHeader.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        String[] orderCols = { "Mã đơn", "Khách hàng", "Trạng thái", "Thanh toán", "Tổng tiền" };
        orderModel = new DefaultTableModel(orderCols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        orderTable = new JTable(orderModel);
        orderTable.setFont(new Font("Arial", Font.PLAIN, 12));
        orderTable.setRowHeight(30);
        orderTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
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
                if (col == 2) { // Status column
                    String val = v == null ? "" : v.toString();
                    switch (val) {
                        case "PENDING" -> { setForeground(new Color(0xFF9800)); setText("Chờ xác nhận"); }
                        case "CONFIRMED" -> { setForeground(new Color(0x2196F3)); setText("Đã xác nhận"); }
                        case "SHIPPING" -> { setForeground(new Color(0x9C27B0)); setText("Đang giao"); }
                        case "DELIVERING" -> { setForeground(new Color(0xFF5722)); setText("Đang vận chuyển"); }
                        case "COMPLETED" -> { setForeground(new Color(0x4CAF50)); setText("Hoàn thành"); }
                        case "CANCELLED" -> { setForeground(new Color(0xF44336)); setText("Đã hủy"); }
                        default -> setForeground(Color.BLACK);
                    }
                } else if (col == 3) { // Payment column
                    String val = v == null ? "" : v.toString();
                    switch (val) {
                        case "CASH" -> setText("Tiền mặt");
                        case "CARD" -> setText("Thẻ");
                        case "TRANSFER" -> setText("Chuyển khoản");
                        default -> setText(val);
                    }
                    setForeground(Color.BLACK);
                } else {
                    setForeground(Color.BLACK);
                }
                return this;
            }
        };
        
        DefaultTableCellRenderer moneyRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v,
                    boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                setHorizontalAlignment(SwingConstants.RIGHT);
                if (!sel) {
                    setBackground(row % 2 == 0 ? Color.WHITE : new Color(0xF3F0FA));
                }
                if (col == 4 && v != null) { // Money column
                    try {
                        double amount = Double.parseDouble(v.toString());
                        setText(String.format("%,.0f đ", amount));
                    } catch (NumberFormatException e) {
                        setText(v.toString());
                    }
                }
                setForeground(Color.BLACK);
                return this;
            }
        };

        orderTable.getColumnModel().getColumn(0).setCellRenderer(statusRenderer); // Code
        orderTable.getColumnModel().getColumn(1).setCellRenderer(statusRenderer); // Customer
        orderTable.getColumnModel().getColumn(2).setCellRenderer(statusRenderer); // Status
        orderTable.getColumnModel().getColumn(3).setCellRenderer(statusRenderer); // Payment
        orderTable.getColumnModel().getColumn(4).setCellRenderer(moneyRenderer);  // Amount

        orderCard.add(orderHeader, BorderLayout.NORTH);
        orderCard.add(new JScrollPane(orderTable), BorderLayout.CENTER);

        // Add left and right panels to center
        centerPanel.add(chartsPanel);
        centerPanel.add(orderCard);

        add(topCards, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        
        // Load initial data
        loadRecentOrders();
    }
    
    private void loadDashboardData() {
        try {
            // Get data from database
            SalesBUS salesBUS = new SalesBUS();
            CustomerBUS customerBUS = new CustomerBUS();
            ProductBUS productBUS = new ProductBUS();
            
            // Total sales (doanh thu - chỉ đếm đơn đã hoàn thành)
            List<SaleDTO> allSales = salesBUS.getAllSales();
            int totalSales = (int) allSales.stream()
                .filter(s -> "COMPLETED".equals(s.getSaleStatus().name()))
                .count();
            
            // Pending orders (đơn hàng đang chờ)
            int pendingOrders = (int) allSales.stream()
                .filter(s -> "PENDING".equals(s.getSaleStatus().name()))
                .count();
            
            // Low stock products
            List<DTO.ProductDTO> allProducts = productBUS.getAllProducts();
            int lowStockCount = (int) allProducts.stream()
                .filter(p -> p.getTotalQuantity() <= p.getMinStockLevel())
                .count();
            
            // Total customers
            int totalCustomers = customerBUS.getAllCustomers().size();
            
            // Create card data
            Object[][] cardData = {
                    { "💰", "Doanh thu:", totalSales + " đơn", new Color(0xD4F4E2), new Color(0x5CB85C) },
                    { "🕒", "Đơn hàng đang chờ:", pendingOrders + " đơn", new Color(0xFFF3CD), new Color(0xF0AD4E) },
                    { "⚠", "Sản phẩm sắp hết:", (lowStockCount == 0 ? "Không có" : lowStockCount + " mặt hàng"), 
                      lowStockCount == 0 ? new Color(0xD4F4E2) : new Color(0xFFF3CD), 
                      lowStockCount == 0 ? new Color(0x5CB85C) : new Color(0xF0AD4E) },
                    { "👥", "Tổng khách hàng:", totalCustomers + " khách", new Color(0xF5D0F5), new Color(0xAB47BC) },
            };

            // Clear existing cards
            topCards.removeAll();
            
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

                card.add(icon, BorderLayout.WEST);
                card.add(txtPanel, BorderLayout.CENTER);
                topCards.add(card);
            }
            
            topCards.revalidate();
            topCards.repaint();
            
        } catch (Exception e) {
            e.printStackTrace();
            // Fallback to default data
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu dashboard: " + e.getMessage());
        }
    }
    
    /**
    * Load dữ liệu doanh thu theo kỳ được chọn và chuyển sang phần trăm cho đồ thị
    */
    private void loadChartData() {
        try {
            SalesBUS salesBUS = new SalesBUS();
            String period = cbChartPeriod != null ? (String) cbChartPeriod.getSelectedItem() : "Tuần";
            double[] raw;
            if ("Tháng".equals(period)) {
                raw = salesBUS.getMonthlyRevenue();
                chartLabels = new String[]{"Tuần 1", "Tuần 2", "Tuần 3", "Tuần 4"};
                if (revenueTitle != null) revenueTitle.setText("Doanh thu tháng này");
            } else if ("Năm".equals(period)) {
                raw = salesBUS.getYearlyRevenue();
                chartLabels = new String[]{"T1","T2","T3","T4","T5","T6","T7","T8","T9","T10","T11","T12"};
                if (revenueTitle != null) revenueTitle.setText("Doanh thu năm nay");
            } else {
                raw = salesBUS.getWeeklyRevenue();
                chartLabels = new String[]{"T2", "T3", "T4", "T5", "T6", "T7", "CN"};
                if (revenueTitle != null) revenueTitle.setText("Doanh thu tuần này");
            }
            double maxRevenue = salesBUS.getMaxRevenue(raw);
            chartValues = new int[raw.length];
            if (maxRevenue > 0) {
                for (int i = 0; i < raw.length; i++)
                    chartValues[i] = (int) (raw[i] * 100 / maxRevenue);
            }
        } catch (Exception e) {
            e.printStackTrace();
            chartValues = new int[]{ 10, 22, 38, 32, 55, 52, 88 };
        }
    }

    /**
     * Tạo panel đồ thị với dữ liệu hiện tại
     */
    private JPanel createChart() {
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

                // Vẽ lưới ngang
                g2.setColor(new Color(0xDDDDDD));
                g2.setStroke(
                        new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] { 4 }, 0));
                for (int i = 0; i <= 4; i++) {
                    int y = padT + chartH * i / 4;
                    g2.drawLine(padL, y, padL + chartW, y);
                    g2.setColor(new Color(0x999999));
                    g2.setFont(new Font("Arial", Font.PLAIN, 11));
                    g2.drawString(String.valueOf(100 - 25 * i) + "%", 2, y + 4);
                    g2.setColor(new Color(0xDDDDDD));
                }

                // Tạo các điểm cho đồ thị
                int[] xs = new int[n + 2];
                int[] ys = new int[n + 2];
                for (int i = 0; i < n; i++) {
                    xs[i] = padL + i * chartW / (n - 1);
                    ys[i] = padT + chartH - chartValues[i] * chartH / 100;
                }
                xs[n] = padL + chartW;
                ys[n] = padT + chartH;
                xs[n + 1] = padL;
                ys[n + 1] = padT + chartH;

                // Fill màu phía dưới đường
                g2.setColor(new Color(0xB8A9D9));
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.35f));
                g2.fillPolygon(xs, ys, n + 2);
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));

                // Vẽ đường nối
                g2.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.setColor(new Color(0x7B68AE));
                for (int i = 0; i < n - 1; i++) {
                    g2.drawLine(xs[i], ys[i], xs[i + 1], ys[i + 1]);
                }

                // Vẽ các điểm tròn
                g2.setColor(new Color(0x7B68AE));
                for (int i = 0; i < n; i++) {
                    g2.fillOval(xs[i] - 4, ys[i] - 4, 8, 8);
                }

                // Vẽ nhãn ngày
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
        return chart;
    }


    /**
     * Làm mới đồ thị với dữ liệu mới nhất theo kỳ đã chọn
     */
    private void refreshChart() {
        loadChartData();
        chartPanel.repaint();
    }

    // ===== CUSTOMER CHART =====
    private int[] customerChartValues;
    private JPanel customerChartPanel;
    
    private void loadCustomerChartData() {
        try {
            CustomerBUS customerBUS = new CustomerBUS();
            ArrayList<CustomerDTO> allCustomers = customerBUS.getAllCustomers();
            
            LocalDate today = LocalDate.now();
            LocalDate monday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            
            // Tính số khách hàng mới từng ngày trong tuần
            customerChartValues = new int[7];
            int maxCustomers = 0;
            
            for (int i = 0; i < 7; i++) {
                LocalDate currentDay = monday.plusDays(i);
                
                int dailyCustomers = (int) allCustomers.stream()
                    .filter(c -> c.getCreatedAt() != null)
                    .filter(c -> c.getCreatedAt().toLocalDate().equals(currentDay))
                    .count();
                
                customerChartValues[i] = dailyCustomers;
                if (dailyCustomers > maxCustomers) {
                    maxCustomers = dailyCustomers;
                }
            }
            
            // Nếu không có khách hàng mới, set về 0
            if (maxCustomers == 0) {
                for (int i = 0; i < 7; i++) {
                    customerChartValues[i] = 0;
                }
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            customerChartValues = new int[7];
        }
    }
    
    private JPanel createCustomerChart() {
        loadCustomerChartData();
        
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
                int n = customerChartValues.length;
                
                // Tìm max value
                int maxVal = 0;
                for (int v : customerChartValues) {
                    if (v > maxVal) maxVal = v;
                }
                if (maxVal == 0) maxVal = 10; // Default max nếu không có data

                // Vẽ lưới ngang
                g2.setColor(new Color(0xDDDDDD));
                g2.setStroke(new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] { 4 }, 0));
                for (int i = 0; i <= 4; i++) {
                    int y = padT + chartH * i / 4;
                    g2.drawLine(padL, y, padL + chartW, y);
                    g2.setColor(new Color(0x999999));
                    g2.setFont(new Font("Arial", Font.PLAIN, 11));
                    int gridVal = maxVal - (maxVal * i / 4);
                    g2.drawString(String.valueOf(gridVal), 2, y + 4);
                    g2.setColor(new Color(0xDDDDDD));
                }

                // Vẽ cột biểu đồ
                int barWidth = chartW / (n * 2);
                g2.setColor(new Color(0x66BB6A));
                for (int i = 0; i < n; i++) {
                    int x = padL + (i * 2 + 1) * chartW / (n * 2);
                    int barHeight = maxVal > 0 ? customerChartValues[i] * chartH / maxVal : 0;
                    int y = padT + chartH - barHeight;
                    
                    g2.fillRoundRect(x - barWidth / 2, y, barWidth, barHeight, 4, 4);
                    
                    // Hiển thị giá trị trên cột
                    g2.setColor(Color.BLACK);
                    g2.setFont(new Font("Arial", Font.BOLD, 10));
                    String valStr = String.valueOf(customerChartValues[i]);
                    int strWidth = g2.getFontMetrics().stringWidth(valStr);
                    g2.drawString(valStr, x - strWidth / 2, y - 5);
                    g2.setColor(new Color(0x66BB6A));
                }

                // Vẽ nhãn ngày
                g2.setColor(new Color(0x666666));
                g2.setFont(new Font("Arial", Font.PLAIN, 11));
                for (int i = 0; i < chartLabels.length; i++) {
                    int xi = padL + (i * 2 + 1) * chartW / (n * 2);
                    String label = chartLabels[i];
                    int strWidth = g2.getFontMetrics().stringWidth(label);
                    g2.drawString(label, xi - strWidth / 2, h - 6);
                }
            }
        };
        chart.setOpaque(false);
        chart.setPreferredSize(new Dimension(0, 200));
        customerChartPanel = chart;
        return chart;
    }
    
    private void refreshCustomerChart() {
        customerChartPanel.repaint();
    }

    // ===== LOYALTY POINTS CHART =====
    private ArrayList<CustomerDTO> topLoyaltyCustomers;
    private JPanel loyaltyChartPanel;
    
    private void loadLoyaltyPointsData() {
        try {
            CustomerBUS customerBUS = new CustomerBUS();
            ArrayList<CustomerDTO> allCustomers = customerBUS.getAllCustomers();
            
            // Sort by loyalty points and take top 5
            topLoyaltyCustomers = allCustomers.stream()
                .sorted((a, b) -> Integer.compare(b.getLoyaltyPoints(), a.getLoyaltyPoints()))
                .limit(5)
                .collect(ArrayList::new, List::add, List::addAll);
            
            if (topLoyaltyCustomers == null) {
                topLoyaltyCustomers = new ArrayList<>();
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            topLoyaltyCustomers = new ArrayList<>();
        }
    }
    
    private JPanel createLoyaltyPointsChart() {
        loadLoyaltyPointsData();
        
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
                int n = topLoyaltyCustomers.size();
                
                if (n == 0) {
                    g2.setColor(new Color(0x999999));
                    g2.setFont(new Font("Arial", Font.PLAIN, 14));
                    g2.drawString("Không có dữ liệu khách hàng", padL + 50, padT + chartH / 2);
                    return;
                }
                
                // Tìm max loyalty points
                int maxVal = topLoyaltyCustomers.stream()
                    .mapToInt(CustomerDTO::getLoyaltyPoints)
                    .max()
                    .orElse(100);
                if (maxVal == 0) maxVal = 100;

                // Vẽ lưới ngang
                g2.setColor(new Color(0xDDDDDD));
                g2.setStroke(new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] { 4 }, 0));
                for (int i = 0; i <= 4; i++) {
                    int y = padT + chartH * i / 4;
                    g2.drawLine(padL, y, padL + chartW, y);
                    g2.setColor(new Color(0x999999));
                    g2.setFont(new Font("Arial", Font.PLAIN, 10));
                    int gridVal = maxVal - (maxVal * i / 4);
                    g2.drawString(String.valueOf(gridVal), 2, y + 4);
                    g2.setColor(new Color(0xDDDDDD));
                }

                // Vẽ cột biểu đồ với tên và màu theo rank
                int barWidth = chartW / (n * 2);
                for (int i = 0; i < n; i++) {
                    CustomerDTO customer = topLoyaltyCustomers.get(i);
                    
                    // Chọn màu dựa trên customer type (rank)
                    Color barColor = getColorForRank(customer.getType());
                    g2.setColor(barColor);
                    
                    int x = padL + (i * 2 + 1) * chartW / (n * 2);
                    int barHeight = customer.getLoyaltyPoints() * chartH / maxVal;
                    int y = padT + chartH - barHeight;
                    
                    g2.fillRoundRect(x - barWidth / 2, y, barWidth, barHeight, 4, 4);
                    
                    // Hiển thị giá trị trên cột
                    g2.setColor(Color.BLACK);
                    g2.setFont(new Font("Arial", Font.BOLD, 9));
                    String valStr = String.valueOf(customer.getLoyaltyPoints());
                    int strWidth = g2.getFontMetrics().stringWidth(valStr);
                    g2.drawString(valStr, x - strWidth / 2, y - 5);
                    
                    // Vẽ tên khách hàng với màu theo rank
                    g2.setColor(barColor);
                    g2.setFont(new Font("Arial", Font.BOLD, 10));
                    String name = customer.getFullName();
                    if (name.length() > 8) {
                        name = name.substring(0, 8) + "...";
                    }
                    int nameWidth = g2.getFontMetrics().stringWidth(name);
                    g2.drawString(name, x - nameWidth / 2, h - 8);
                }
            }
            
            private Color getColorForRank(CustomerType type) {
                if (type == null) return new Color(0xB8860B); // default bronze
                switch (type) {
                    case DIAMOND -> { return new Color(0x00CED1); } // cyan/turquoise
                    case GOLD -> { return new Color(0xFFD700); } // gold
                    case SILVER -> { return new Color(0xC0C0C0); } // silver
                    default -> { return new Color(0xB8860B); } // bronze
                }
            }
        };
        chart.setOpaque(false);
        chart.setPreferredSize(new Dimension(0, 200));
        loyaltyChartPanel = chart;
        return chart;
    }
    
    private void refreshLoyaltyPointsChart() {
        loyaltyChartPanel.repaint();
    }

    // ===== ORDER CHART =====
    private int[] orderChartValues;
    private JPanel orderChartPanel;
    
    private void loadOrderChartData() {
        try {
            SalesBUS salesBUS = new SalesBUS();
            ArrayList<SaleDTO> allSales = salesBUS.getAllSales();
            
            LocalDate today = LocalDate.now();
            LocalDate monday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            
            // Tính số đơn hàng từng ngày trong tuần
            orderChartValues = new int[7];
            int maxOrders = 0;
            
            for (int i = 0; i < 7; i++) {
                LocalDate currentDay = monday.plusDays(i);
                
                int dailyOrders = (int) allSales.stream()
                    .filter(s -> s.getSaleDate() != null)
                    .filter(s -> s.getSaleDate().equals(currentDay))
                    .count();
                
                orderChartValues[i] = dailyOrders;
                if (dailyOrders > maxOrders) {
                    maxOrders = dailyOrders;
                }
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            orderChartValues = new int[7];
        }
    }
    
    private JPanel createOrderChart() {
        loadOrderChartData();
        
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
                int n = orderChartValues.length;
                
                // Tìm max value
                int maxVal = 0;
                for (int v : orderChartValues) {
                    if (v > maxVal) maxVal = v;
                }
                if (maxVal == 0) maxVal = 10; // Default max nếu không có data

                // Vẽ lưới ngang
                g2.setColor(new Color(0xDDDDDD));
                g2.setStroke(new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] { 4 }, 0));
                for (int i = 0; i <= 4; i++) {
                    int y = padT + chartH * i / 4;
                    g2.drawLine(padL, y, padL + chartW, y);
                    g2.setColor(new Color(0x999999));
                    g2.setFont(new Font("Arial", Font.PLAIN, 11));
                    int gridVal = maxVal - (maxVal * i / 4);
                    g2.drawString(String.valueOf(gridVal), 2, y + 4);
                    g2.setColor(new Color(0xDDDDDD));
                }

                // Vẽ cột biểu đồ
                int barWidth = chartW / (n * 2);
                g2.setColor(new Color(0x42A5F5));
                for (int i = 0; i < n; i++) {
                    int x = padL + (i * 2 + 1) * chartW / (n * 2);
                    int barHeight = maxVal > 0 ? orderChartValues[i] * chartH / maxVal : 0;
                    int y = padT + chartH - barHeight;
                    
                    g2.fillRoundRect(x - barWidth / 2, y, barWidth, barHeight, 4, 4);
                    
                    // Hiển thị giá trị trên cột
                    g2.setColor(Color.BLACK);
                    g2.setFont(new Font("Arial", Font.BOLD, 10));
                    String valStr = String.valueOf(orderChartValues[i]);
                    int strWidth = g2.getFontMetrics().stringWidth(valStr);
                    g2.drawString(valStr, x - strWidth / 2, y - 5);
                    g2.setColor(new Color(0x42A5F5));
                }

                // Vẽ nhãn ngày
                g2.setColor(new Color(0x666666));
                g2.setFont(new Font("Arial", Font.PLAIN, 11));
                for (int i = 0; i < chartLabels.length; i++) {
                    int xi = padL + (i * 2 + 1) * chartW / (n * 2);
                    String label = chartLabels[i];
                    int strWidth = g2.getFontMetrics().stringWidth(label);
                    g2.drawString(label, xi - strWidth / 2, h - 6);
                }
            }
        };
        chart.setOpaque(false);
        chart.setPreferredSize(new Dimension(0, 200));
        orderChartPanel = chart;
        return chart;
    }
    
    private void refreshOrderChart() {
        orderChartPanel.repaint();
    }

    private void loadRecentOrders() {
        try {
            SalesBUS salesBUS = new SalesBUS();
            List<SaleDTO> sales = salesBUS.getAllSales();
            
            // Apply filters
            String statusFilterValue = (String) statusFilter.getSelectedItem();
            String paymentFilterValue = (String) paymentFilter.getSelectedItem();
            
            if (!"Tất cả".equals(statusFilterValue)) {
                sales = sales.stream()
                    .filter(s -> statusFilterValue.equals(s.getSaleStatus().name()))
                    .toList();
            }
            
            if (!"Tất cả".equals(paymentFilterValue)) {
                sales = sales.stream()
                    .filter(s -> paymentFilterValue.equals(s.getSalePaymentMethod().name()))
                    .toList();
            }
            
            // Sort by date (newest first) and limit to 10
            sales = sales.stream()
                .filter(s -> s.getSaleDate() != null) // Filter out null dates
                .sorted((a, b) -> b.getSaleDate().compareTo(a.getSaleDate()))
                .limit(10)
                .toList();
            
            // Clear table
            orderModel.setRowCount(0);
            
            // Add data to table
            for (SaleDTO sale : sales) {
                String customerName = sale.getCustomerName() != null ? sale.getCustomerName() : "Khách lẻ";
                orderModel.addRow(new Object[]{
                    sale.getSaleCode(),
                    customerName,
                    sale.getSaleStatus().name(),
                    sale.getSalePaymentMethod().name(),
                    sale.getTotalAmount()
                });
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi tải danh sách đơn hàng: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        JFrame f = new JFrame("Trang chủ");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setSize(900, 600);
        f.setLocationRelativeTo(null);
        f.setContentPane(new TrangChuPanel());
        f.setVisible(true);
    }
}


