package GUI.TrangChu;

import BUS.CustomerBUS;
import BUS.ProductBUS;
import BUS.SalesBUS;
import DTO.SaleDTO;
import GUI.UIUtils;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

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
    
    public TrangChuPanel() {
        setLayout(new BorderLayout(0, 0));
        setBackground(new Color(0xF8F7FF));

        // four summary cards
        topCards = new JPanel(new GridLayout(1, 4, 30, 0));
        topCards.setBackground(new Color(0xF8F7FF));
        topCards.setBorder(BorderFactory.createEmptyBorder(18, 18, 12, 18));
        
        loadDashboardData();
        
        loadDashboardData();

        // center area with chart and recent orders
        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 14, 0));
        centerPanel.setBackground(new Color(0xF8F7FF));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(0, 18, 18, 18));

        // chart card
        JPanel chartCard = UIUtils.createCard();
        // Tạo header cho chart với nút refresh
        JPanel chartHeader = new JPanel(new BorderLayout());
        chartHeader.setOpaque(false);

        JLabel chartTitle = new JLabel("Doanh thu tuần này");
        chartTitle.setFont(new Font("Playfair Display", Font.BOLD, 20));

        JButton btnRefresh = new JButton("🔄 Làm mới");
        btnRefresh.setFont(new Font("Arial", Font.PLAIN, 12));
        btnRefresh.setFocusPainted(false);
        btnRefresh.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRefresh.addActionListener(e -> refreshChart());

        chartHeader.add(chartTitle, BorderLayout.WEST);
        chartHeader.add(btnRefresh, BorderLayout.EAST);
        chartHeader.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        // Lấy dữ liệu doanh thu tuần từ database
        chartLabels = new String[]{ "T2", "T3", "T4", "T5", "T6", "T7", "CN" };
        loadChartData(); // Load dữ liệu đồ thị

        JPanel chart = createChart(); // Tạo đồ thị
        chartPanel = chart; // Lưu lại để refresh sau

        chartCard.add(chartHeader, BorderLayout.NORTH);
        chartCard.add(chartPanel, BorderLayout.CENTER);

        // recent orders table
        orderCard = UIUtils.createCard();
        JPanel orderHeader = new JPanel(new BorderLayout());
        orderHeader.setOpaque(false);
        
        JLabel orderTitle = new JLabel("Đơn hàng gần đây");
        orderTitle.setFont(new Font("Playfair Display", Font.BOLD, 20));
        
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
        orderTable.setFont(new Font("Arial", Font.PLAIN, 14));
        orderTable.setRowHeight(35);
        orderTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
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

        centerPanel.add(chartCard);
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

    /**
    * Load dữ liệu doanh thu tuần và chuyển sang phần trăm cho đồ thị
    */
    private void loadChartData() {
        try {
            SalesBUS salesBUS = new SalesBUS();
            
            // Lấy doanh thu 7 ngày trong tuần
            double[] weekRevenue = salesBUS.getWeeklyRevenue();
            
            // Tìm giá trị max
            double maxRevenue = salesBUS.getMaxRevenue(weekRevenue);
            
            // Chuyển sang phần trăm (0-100) để vẽ đồ thị
            chartValues = new int[7];
            if (maxRevenue > 0) {
                for (int i = 0; i < 7; i++) {
                    chartValues[i] = (int) (weekRevenue[i] * 100 / maxRevenue);
                }
            } else {
                // Nếu không có doanh thu, set về 0
                for (int i = 0; i < 7; i++) {
                    chartValues[i] = 0;
                }
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            // Fallback về dữ liệu mẫu nếu lỗi
            chartValues = new int[]{ 10, 22, 38, 32, 55, 52, 88, 100 };
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
     * Làm mới đồ thị với dữ liệu mới nhất
     */
    private void refreshChart() {
        loadChartData(); // Load lại dữ liệu
        chartPanel.repaint(); // Vẽ lại đồ thị
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


