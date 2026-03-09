package GUI.TrangChu;

import BUS.CustomerBUS;
import BUS.ProductBUS;
import BUS.SalesBUS;
import DTO.SaleDTO;
import DTO.CustomerDTO;
import GUI.UIUtils;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;
import java.time.LocalDate;
import java.time.DayOfWeek;
import java.time.temporal.TemporalAdjusters;

/**
 * Dashboard / home panel with summary cards and a small chart.
 */
public class TrangChuPanel extends JPanel {
    
    private JPanel topCards;
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

        // center area with 3 charts
        JPanel centerPanel = new JPanel(new GridLayout(3, 1, 0, 14));
        centerPanel.setBackground(new Color(0xF8F7FF));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(0, 18, 18, 18));

        // ===== Chart 1: Revenue Chart =====
        JPanel revenueCard = UIUtils.createCard();
        JPanel revenueHeader = new JPanel(new BorderLayout());
        revenueHeader.setOpaque(false);
        JLabel revenueTitle = new JLabel("Doanh thu tuần này");
        revenueTitle.setFont(new Font("Playfair Display", Font.BOLD, 18));
        JButton btnRefresh1 = new JButton("🔄 Làm mới");
        btnRefresh1.setFont(new Font("Arial", Font.PLAIN, 11));
        btnRefresh1.setFocusPainted(false);
        btnRefresh1.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRefresh1.addActionListener(e -> refreshChart());
        revenueHeader.add(revenueTitle, BorderLayout.WEST);
        revenueHeader.add(btnRefresh1, BorderLayout.EAST);
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
        customerTitle.setFont(new Font("Playfair Display", Font.BOLD, 18));
        JButton btnRefresh2 = new JButton("🔄 Làm mới");
        btnRefresh2.setFont(new Font("Arial", Font.PLAIN, 11));
        btnRefresh2.setFocusPainted(false);
        btnRefresh2.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRefresh2.addActionListener(e -> refreshCustomerChart());
        customerHeaderPanel.add(customerTitle, BorderLayout.WEST);
        customerHeaderPanel.add(btnRefresh2, BorderLayout.EAST);
        customerHeaderPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        
        JPanel customerChart = createCustomerChart();
        customerCard.add(customerHeaderPanel, BorderLayout.NORTH);
        customerCard.add(customerChart, BorderLayout.CENTER);

        // ===== Chart 3: Orders Chart =====
        JPanel orderChartCard = UIUtils.createCard();
        JPanel orderChartHeaderPanel = new JPanel(new BorderLayout());
        orderChartHeaderPanel.setOpaque(false);
        JLabel orderChartTitle = new JLabel("Đơn hàng (tuần này)");
        orderChartTitle.setFont(new Font("Playfair Display", Font.BOLD, 18));
        JButton btnRefresh3 = new JButton("🔄 Làm mới");
        btnRefresh3.setFont(new Font("Arial", Font.PLAIN, 11));
        btnRefresh3.setFocusPainted(false);
        btnRefresh3.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRefresh3.addActionListener(e -> refreshOrderChart());
        orderChartHeaderPanel.add(orderChartTitle, BorderLayout.WEST);
        orderChartHeaderPanel.add(btnRefresh3, BorderLayout.EAST);
        orderChartHeaderPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        
        JPanel orderChart = createOrderChart();
        orderChartCard.add(orderChartHeaderPanel, BorderLayout.NORTH);
        orderChartCard.add(orderChart, BorderLayout.CENTER);

        centerPanel.add(revenueCard);
        centerPanel.add(customerCard);
        centerPanel.add(orderChartCard);

        add(topCards, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
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

    public static void main(String[] args) {
        JFrame f = new JFrame("Trang chủ");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setSize(900, 600);
        f.setLocationRelativeTo(null);
        f.setContentPane(new TrangChuPanel());
        f.setVisible(true);
    }
}


