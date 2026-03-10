package GUI.TrangChu;

import BUS.CustomerBUS;
import BUS.ProductBUS;
import BUS.SalesBUS;
import DTO.CustomerDTO;
import DTO.SaleDTO;
import GUI.UIUtils;
import java.awt.*;
import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    private String revenuePeriod = "Tuần"; // Default period
    private String customerPeriod = "Tuần"; // Default period for customer chart
    
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
        JLabel revenueTitle = new JLabel("Doanh thu");
        revenueTitle.setFont(new Font("Playfair Display", Font.BOLD, 16));
        
        JPanel revenueControls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        revenueControls.setOpaque(false);
        JComboBox<String> revenuePeriodCombo = new JComboBox<>(new String[]{"Tuần", "Tháng", "Năm"});
        revenuePeriodCombo.setFont(new Font("Arial", Font.PLAIN, 10));
        revenuePeriodCombo.addActionListener(e -> {
            revenuePeriod = (String) revenuePeriodCombo.getSelectedItem();
            loadChartData();
            chartPanel.repaint();
        });
        JButton btnRefresh1 = new JButton("Làm mới");
        btnRefresh1.setFont(new Font("Arial", Font.PLAIN, 10));
        btnRefresh1.setFocusPainted(false);
        btnRefresh1.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRefresh1.addActionListener(e -> {
            loadChartData();
            chartPanel.repaint();
        });
        revenueControls.add(revenuePeriodCombo);
        revenueControls.add(btnRefresh1);
        
        revenueHeader.add(revenueTitle, BorderLayout.WEST);
        revenueHeader.add(revenueControls, BorderLayout.EAST);
        revenueHeader.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        
        chartLabels = new String[]{ "T1", "T2", "T3", "T4", "T5", "T6", "T7", "T8", "T9", "T10" , "T11", "T12"};
        loadChartData();
        JPanel chart = createChart();
        chartPanel = chart;
        
        revenueCard.add(revenueHeader, BorderLayout.NORTH);
        revenueCard.add(chartPanel, BorderLayout.CENTER);

        // ===== Chart 2: Customer Chart=====
        JPanel customerCard = UIUtils.createCard();
        JPanel customerHeaderPanel = new JPanel(new BorderLayout());
        customerHeaderPanel.setOpaque(false);
        JLabel customerTitle = new JLabel("Khách hàng mới");
        customerTitle.setFont(new Font("Playfair Display", Font.BOLD, 16));
        
        JPanel customerControls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        customerControls.setOpaque(false);
        JComboBox<String> customerPeriodCombo = new JComboBox<>(new String[]{"Tuần", "Tháng", "Năm"});
        customerPeriodCombo.setFont(new Font("Arial", Font.PLAIN, 10));
        customerPeriodCombo.addActionListener(e -> {
            customerPeriod = (String) customerPeriodCombo.getSelectedItem();
            loadCustomerChartData();
            customerChartPanel.repaint();
        });
        JButton btnRefresh2 = new JButton("Làm mới");
        btnRefresh2.setFont(new Font("Arial", Font.PLAIN, 10));
        btnRefresh2.setFocusPainted(false);
        btnRefresh2.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRefresh2.addActionListener(e -> {
            loadCustomerChartData();
            customerChartPanel.repaint();
        });
        customerControls.add(customerPeriodCombo);
        customerControls.add(btnRefresh2);
        
        customerHeaderPanel.add(customerTitle, BorderLayout.WEST);
        customerHeaderPanel.add(customerControls, BorderLayout.EAST);
        customerHeaderPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        
        JPanel customerChart = createCustomerChart();
        customerCard.add(customerHeaderPanel, BorderLayout.NORTH);
        customerCard.add(customerChart, BorderLayout.CENTER);

        // ===== Chart 3: Top Customers Table =====
        JPanel orderChartCard = UIUtils.createCard();
        JPanel orderChartHeaderPanel = new JPanel(new BorderLayout());
        orderChartHeaderPanel.setOpaque(false);
        JLabel orderChartTitle = new JLabel("Top 5 khách hàng thân thiết");
        orderChartTitle.setFont(new Font("Playfair Display", Font.BOLD, 16));
        
        JPanel topCustomersControls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        topCustomersControls.setOpaque(false);
        JComboBox<String> topCustomersPeriodCombo = new JComboBox<>(new String[]{"Tuần", "Tháng", "Năm"});
        topCustomersPeriodCombo.setFont(new Font("Arial", Font.PLAIN, 10));
        topCustomersPeriodCombo.addActionListener(e -> {
            customerPeriod = (String) topCustomersPeriodCombo.getSelectedItem();
            refreshTopCustomersTable();
        });
        JButton btnRefresh3 = new JButton("Làm mới");
        btnRefresh3.setFont(new Font("Arial", Font.PLAIN, 10));
        btnRefresh3.setFocusPainted(false);
        btnRefresh3.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRefresh3.addActionListener(e -> refreshTopCustomersTable());
        topCustomersControls.add(topCustomersPeriodCombo);
        topCustomersControls.add(btnRefresh3);
        
        orderChartHeaderPanel.add(orderChartTitle, BorderLayout.WEST);
        orderChartHeaderPanel.add(topCustomersControls, BorderLayout.EAST);
        orderChartHeaderPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        
        JPanel topCustomersTable = createTopCustomersTable();
        orderChartCard.add(orderChartHeaderPanel, BorderLayout.NORTH);
        orderChartCard.add(topCustomersTable, BorderLayout.CENTER);

        // Add 3 charts to left panel
        chartsPanel.add(revenueCard);
        chartsPanel.add(customerCard);
        chartsPanel.add(orderChartCard);

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
    * Load dữ liệu đơn hàng hoàn thành theo period
    */
    private void loadChartData() {
        try {
            SalesBUS salesBUS = new SalesBUS();
            List<SaleDTO> allSales = salesBUS.getAllSales();
            
            LocalDate now = LocalDate.now();
            
            if ("Tuần".equals(revenuePeriod)) {
                // Tuần: từ T2 đến CN
                LocalDate monday = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
                chartLabels = new String[]{ "T2", "T3", "T4", "T5", "T6", "T7", "CN" };
                chartValues = new int[7];
                
                for (int i = 0; i < 7; i++) {
                    LocalDate currentDay = monday.plusDays(i + 1); // T2 is monday +1
                    int dailyCompleted = (int) allSales.stream()
                        .filter(s -> s.getSaleDate() != null)
                        .filter(s -> s.getSaleDate().equals(currentDay))
                        .filter(s -> "COMPLETED".equals(s.getSaleStatus().name()))
                        .count();
                    chartValues[i] = dailyCompleted;
                }
                
            } else if ("Tháng".equals(revenuePeriod)) {
                // Tháng: từ 1 đến số ngày của tháng hiện tại
                int daysInMonth = now.lengthOfMonth();
                chartLabels = new String[daysInMonth];
                chartValues = new int[daysInMonth];
                
                for (int day = 1; day <= daysInMonth; day++) {
                    chartLabels[day - 1] = String.valueOf(day);
                    LocalDate currentDay = LocalDate.of(now.getYear(), now.getMonth(), day);
                    int dailyCompleted = (int) allSales.stream()
                        .filter(s -> s.getSaleDate() != null)
                        .filter(s -> s.getSaleDate().equals(currentDay))
                        .filter(s -> "COMPLETED".equals(s.getSaleStatus().name()))
                        .count();
                    chartValues[day - 1] = dailyCompleted;
                }
                
            } else if ("Năm".equals(revenuePeriod)) {
                // Năm: T1 đến T12
                chartLabels = new String[]{ "T1", "T2", "T3", "T4", "T5", "T6", "T7", "T8", "T9", "T10" , "T11", "T12"};
                chartValues = new int[12];
                
                for (int month = 1; month <= 12; month++) {
                    final int currentMonth = month;
                    int monthlyCompleted = (int) allSales.stream()
                        .filter(s -> s.getSaleDate() != null)
                        .filter(s -> s.getSaleDate().getYear() == now.getYear())
                        .filter(s -> s.getSaleDate().getMonthValue() == currentMonth)
                        .filter(s -> "COMPLETED".equals(s.getSaleStatus().name()))
                        .count();
                    chartValues[month - 1] = monthlyCompleted;
                }
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            // Fallback về dữ liệu mẫu nếu lỗi
            chartValues = new int[]{ 15, 18, 22, 20, 25, 28, 30, 28, 25, 22, 20, 18 };
            chartLabels = new String[]{ "T1", "T2", "T3", "T4", "T5", "T6", "T7", "T8", "T9", "T10" , "T11", "T12"};
        }
    }

    /**
     * Tạo panel đồ thị area theo tháng
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
                
                // Tìm max value
                int maxVal = 0;
                for (int v : chartValues) {
                    if (v > maxVal) maxVal = v;
                }
                if (maxVal == 0) maxVal = 30;

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

                // Tính toán điểm dữ liệu trên biểu đồ
                int[] xCoords = new int[n];
                int[] yCoords = new int[n];
                
                for (int i = 0; i < n; i++) {
                    xCoords[i] = padL + (i * chartW) / (n - 1);
                    yCoords[i] = padT + chartH - (chartValues[i] * chartH / maxVal);
                }
                
                // Vẽ area chart (fill area)
                Polygon area = new Polygon();
                area.addPoint(padL, padT + chartH);
                for (int i = 0; i < n; i++) {
                    area.addPoint(xCoords[i], yCoords[i]);
                }
                area.addPoint(padL + chartW, padT + chartH);
                
                g2.setColor(new Color(123, 104, 174, 100)); // RGB + alpha
                g2.fillPolygon(area);
                
                // Vẽ đường line
                g2.setColor(new Color(0x7B68AE));
                g2.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                for (int i = 0; i < n - 1; i++) {
                    g2.drawLine(xCoords[i], yCoords[i], xCoords[i + 1], yCoords[i + 1]);
                }
                
                // Vẽ điểm dữ liệu
                g2.setColor(new Color(0x7B68AE));
                for (int i = 0; i < n; i++) {
                    g2.fillOval(xCoords[i] - 4, yCoords[i] - 4, 8, 8);
                    
                    // Hiển thị giá trị trên điểm
                    g2.setColor(Color.BLACK);
                    g2.setFont(new Font("Arial", Font.BOLD, 10));
                    String valStr = String.valueOf(chartValues[i]);
                    int strWidth = g2.getFontMetrics().stringWidth(valStr);
                    g2.drawString(valStr, xCoords[i] - strWidth / 2, yCoords[i] - 10);
                    g2.setColor(new Color(0x7B68AE));
                }

                // Vẽ nhãn tháng
                g2.setColor(new Color(0x666666));
                g2.setFont(new Font("Arial", Font.PLAIN, 11));
                for (int i = 0; i < chartLabels.length; i++) {
                    String label = chartLabels[i];
                    int strWidth = g2.getFontMetrics().stringWidth(label);
                    g2.drawString(label, xCoords[i] - strWidth / 2, h - 6);
                }
            }
        };
        chart.setOpaque(false);
        chart.setPreferredSize(new Dimension(0, 260));
        return chart;
    }
    
    /**
     * Format currency for display (with M, K suffix)
     */
    private String formatCurrency(int value) {
        if (value >= 1000000) {
            return (value / 1000000) + "M";
        } else if (value >= 1000) {
            return (value / 1000) + "K";
        }
        return String.valueOf(value);
    }
    
    /**
     * Format currency for display on bars (with 2 decimal places)
     */
    private String formatCurrencyShort(int value) {
        if (value >= 1000000) {
            return String.format("%.1fM", value / 1000000.0);
        } else if (value >= 1000) {
            return String.format("%.0fK", value / 1000.0);
        }
        return String.valueOf(value);
    }


    /**
     * Làm mới đồ thị với dữ liệu mới nhất
     */
    private void refreshChart() {
        loadChartData(); // Load lại dữ liệu
        chartPanel.repaint(); // Vẽ lại đồ thị
    }

    // ===== CUSTOMER CHART =====
    private int[] customerChartValues;
    private JPanel customerChartPanel;
    
    private void loadCustomerChartData() {
        try {
            CustomerBUS customerBUS = new CustomerBUS();
            ArrayList<CustomerDTO> allCustomers = customerBUS.getAllCustomers();
            
            LocalDate now = LocalDate.now();
            
            if ("Tuần".equals(customerPeriod)) {
                // Tuần: từ T2 đến CN
                LocalDate monday = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
                customerChartValues = new int[7];
                
                for (int i = 0; i < 7; i++) {
                    LocalDate currentDay = monday.plusDays(i + 1); // T2 is monday +1
                    int dailyCustomers = (int) allCustomers.stream()
                        .filter(c -> c.getCreatedAt() != null)
                        .filter(c -> c.getCreatedAt().toLocalDate().equals(currentDay))
                        .count();
                    customerChartValues[i] = dailyCustomers;
                }
                
            } else if ("Tháng".equals(customerPeriod)) {
                // Tháng: từ 1 đến số ngày của tháng hiện tại
                int daysInMonth = now.lengthOfMonth();
                customerChartValues = new int[daysInMonth];
                
                for (int day = 1; day <= daysInMonth; day++) {
                    LocalDate currentDay = LocalDate.of(now.getYear(), now.getMonth(), day);
                    int dailyCustomers = (int) allCustomers.stream()
                        .filter(c -> c.getCreatedAt() != null)
                        .filter(c -> c.getCreatedAt().toLocalDate().equals(currentDay))
                        .count();
                    customerChartValues[day - 1] = dailyCustomers;
                }
                
            } else if ("Năm".equals(customerPeriod)) {
                // Năm: T1 đến T12
                customerChartValues = new int[12];
                
                for (int month = 1; month <= 12; month++) {
                    final int currentMonth = month;
                    int monthlyCustomers = (int) allCustomers.stream()
                        .filter(c -> c.getCreatedAt() != null)
                        .filter(c -> c.getCreatedAt().getYear() == now.getYear())
                        .filter(c -> c.getCreatedAt().getMonthValue() == currentMonth)
                        .count();
                    customerChartValues[month - 1] = monthlyCustomers;
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
                
                // Set chartLabels based on period
                String[] labels;
                if ("Tuần".equals(customerPeriod)) {
                    labels = new String[]{ "T2", "T3", "T4", "T5", "T6", "T7", "CN" };
                } else if ("Tháng".equals(customerPeriod)) {
                    LocalDate now = LocalDate.now();
                    int daysInMonth = now.lengthOfMonth();
                    labels = new String[daysInMonth];
                    for (int i = 0; i < daysInMonth; i++) {
                        labels[i] = String.valueOf(i + 1);
                    }
                } else { // Năm
                    labels = new String[]{ "T1", "T2", "T3", "T4", "T5", "T6", "T7", "T8", "T9", "T10", "T11", "T12" };
                }
                
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

                // Vẽ nhãn
                g2.setColor(new Color(0x666666));
                g2.setFont(new Font("Arial", Font.PLAIN, 11));
                for (int i = 0; i < labels.length; i++) {
                    int xi = padL + (i * 2 + 1) * chartW / (n * 2);
                    String label = labels[i];
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
        loadCustomerChartData();
        customerChartPanel.repaint();
    }

    // ===== TOP CUSTOMERS TABLE =====
    private JTable topCustomersTable;
    private DefaultTableModel topCustomersModel;
    
    private void loadTopCustomersData() {
        try {
            SalesBUS salesBUS = new SalesBUS();
            List<SaleDTO> allSales = salesBUS.getAllSales();
            
            LocalDate now = LocalDate.now();
            LocalDate startDate;
            
            if ("Tuần".equals(customerPeriod)) {
                startDate = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            } else if ("Tháng".equals(customerPeriod)) {
                startDate = now.withDayOfMonth(1);
            } else { // Năm
                startDate = now.withDayOfYear(1);
            }
            
            // Filter sales in the period and completed
            List<SaleDTO> periodSales = allSales.stream()
                .filter(s -> s.getSaleDate() != null)
                .filter(s -> !s.getSaleDate().isBefore(startDate))
                .filter(s -> "COMPLETED".equals(s.getSaleStatus().name()))
                .toList();
            
            // Group by customer name and sum total amount
            Map<String, BigDecimal> customerSpending = new HashMap<>();
            for (SaleDTO sale : periodSales) {
                String customerName = sale.getCustomerName() != null ? sale.getCustomerName() : "Khách lẻ";
                customerSpending.put(customerName, 
                    customerSpending.getOrDefault(customerName, BigDecimal.ZERO).add(sale.getTotalAmount()));
            }
            
            // Sort by spending descending and take top 5
            List<Map.Entry<String, BigDecimal>> topCustomers = customerSpending.entrySet().stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .limit(5)
                .toList();
            
            // Clear table
            topCustomersModel.setRowCount(0);
            
            // Add data to table
            int rank = 1;
            for (Map.Entry<String, BigDecimal> entry : topCustomers) {
                String customerName = entry.getKey();
                BigDecimal totalSpent = entry.getValue();
                
                // Get loyalty points for this customer (if exists)
                CustomerBUS customerBUS = new CustomerBUS();
                List<CustomerDTO> customers = customerBUS.getAllCustomers().stream()
                    .filter(c -> customerName.equals(c.getFullName()))
                    .toList();
                int loyaltyPoints = customers.isEmpty() ? 0 : customers.get(0).getLoyaltyPoints();
                
                topCustomersModel.addRow(new Object[]{
                    rank++,
                    customerName,
                    loyaltyPoints,
                    totalSpent
                });
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi tải danh sách khách hàng thân thiết: " + e.getMessage());
        }
    }
    
    private JPanel createTopCustomersTable() {
        String[] cols = { "STT", "Tên khách hàng", "Điểm tích lũy", "Tổng chi tiêu" };
        topCustomersModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        topCustomersTable = new JTable(topCustomersModel);
        topCustomersTable.setFont(new Font("Arial", Font.PLAIN, 12));
        topCustomersTable.setRowHeight(30);
        topCustomersTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        topCustomersTable.getTableHeader().setBackground(new Color(0xAF9FCB));
        topCustomersTable.getTableHeader().setForeground(Color.WHITE);
        topCustomersTable.setShowVerticalLines(false);
        topCustomersTable.setGridColor(new Color(0xEEEEEE));
        
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v,
                    boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                setHorizontalAlignment(SwingConstants.CENTER);
                if (!sel) {
                    setBackground(row % 2 == 0 ? Color.WHITE : new Color(0xF3F0FA));
                }
                setForeground(Color.BLACK);
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
                if (col == 3 && v != null) { // Money column
                    try {
                        BigDecimal amount = (BigDecimal) v;
                        setText(String.format("%,.0f đ", amount));
                    } catch (Exception e) {
                        setText(v.toString());
                    }
                }
                setForeground(Color.BLACK);
                return this;
            }
        };
        
        topCustomersTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer); // STT
        topCustomersTable.getColumnModel().getColumn(1).setCellRenderer(centerRenderer); // Name
        topCustomersTable.getColumnModel().getColumn(2).setCellRenderer(centerRenderer); // Points
        topCustomersTable.getColumnModel().getColumn(3).setCellRenderer(moneyRenderer);  // Total Spent
        
        // Set column widths
        topCustomersTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        topCustomersTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        topCustomersTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        topCustomersTable.getColumnModel().getColumn(3).setPreferredWidth(120);
        
        loadTopCustomersData();
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JScrollPane(topCustomersTable), BorderLayout.CENTER);
        return panel;
    }
    
    private void refreshTopCustomersTable() {
        loadTopCustomersData();
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


