package BUS;

import DAO.DBConnection;
import DAO.ProductDAO;
import DAO.SaleDAO;
import DAO.SalesInvoiceItemDAO;
import DTO.SaleDTO;
import DTO.SalesInvoiceItemDTO;
import DTO.enums.SaleEnum.SaleStatus;
import java.sql.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

public class SalesBUS {
    private SaleDAO saleDAO;
    private SalesInvoiceItemDAO saleItemDAO = new SalesInvoiceItemDAO();
    private ProductDAO productDAO = new ProductDAO();
    public SalesBUS() {
        saleDAO = new SaleDAO();
    }

    public ArrayList<SaleDTO> getAllSales() {
        try {
            return saleDAO.getAllSales();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public ArrayList<SaleDTO> getSalesByDateRange(LocalDate from, LocalDate to) {
        return saleDAO.getAllSalesByDateRange(from, to);
    }

    public ArrayList<SaleDTO> getSalesByStatus(SaleStatus status) {
        return saleDAO.getSalesByStatus(status);
    }

    public SaleDTO getSaleById(int saleId) {
        if (saleId <= 0) return null;
        return saleDAO.getSaleById(saleId);
    }

    public SaleDTO getSaleByCode(String saleCode) {
        if (saleCode == null || saleCode.isBlank()) return null;
        return saleDAO.getSaleByCode(saleCode);
    }

    public boolean addSale(SaleDTO sale) {
        if (sale == null) return false;
        if (sale.getSaleCode() == null || sale.getSaleCode().isBlank()) {
            System.out.println("Mã đơn hàng không được để trống.");
            return false;
        }
        if (sale.getCustomerID() <= 0) {
            System.out.println("Khách hàng không hợp lệ.");
            return false;
        }
        if (sale.getEmployeeID() <= 0) {
            System.out.println("Nhân viên không hợp lệ.");
            return false;
        }
        if (sale.getTotalAmount() == null || sale.getTotalAmount().signum() <= 0) {
            System.out.println("Tổng tiền phải lớn hơn 0.");
            return false;
        }
        return saleDAO.addSale(sale);
    }
    
public boolean confirmSale(String saleCode){

    try{

        // 1. cập nhật trạng thái đơn
        saleDAO.updateStatus(saleCode, SaleStatus.CONFIRMED);

        // 2. lấy danh sách sản phẩm trong đơn
        List<SalesInvoiceItemDTO> items = saleItemDAO.getBySaleCode(saleCode);

System.out.println("SaleCode: " + saleCode);
System.out.println("Items size: " + items.size());

        // 3. trừ kho
        for(SalesInvoiceItemDTO item : items){

            productDAO.decreaseStock(
                    item.getProductId(),
                    item.getQuantity()
            );
        }

        return true;

    }catch(Exception e){
        e.printStackTrace();
        return false;
    }
}
    public boolean cancelSale(String saleCode){
        return saleDAO.updateStatus(saleCode, SaleStatus.CANCELLED);
    }

    public boolean deleteSale(String saleCode) {
        if (saleCode == null || saleCode.isBlank()) {
            System.out.println("Mã đơn hàng không được để trống.");
            return false;
        }
        return saleDAO.deleteSale(saleCode);
    }

    public String generateSaleCode() {
        // Tìm số lớn nhất hiện tại
        String sql = "SELECT MAX(CAST(SUBSTRING(sale_code, 3) AS UNSIGNED)) as max_num FROM sales WHERE sale_code LIKE 'DH%'";
        
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            if (rs.next()) {
                int maxNum = rs.getInt("max_num");
                if (rs.wasNull()) {
                    return "DH001";
                }
                return String.format("DH%03d", maxNum + 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return "DH001";
    }


    /**
     * Lấy doanh thu tuần hiện tại (Thứ 2 - Chủ nhật)
     * Trả về mảng 7 phần tử: [T2, T3, T4, T5, T6, T7, CN]
     */
    public double[] getWeeklyRevenue() {
        double[] weekRevenue = new double[7];
        
        try {
            ArrayList<SaleDTO> allSales = getAllSales();
            LocalDate today = LocalDate.now();
            
            // Tìm ngày đầu tuần (Thứ 2)
            LocalDate monday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            
            // Tính doanh thu từng ngày trong tuần
            for (int i = 0; i < 7; i++) {
                LocalDate currentDay = monday.plusDays(i);
                
                double dailyRevenue = allSales.stream()
                    .filter(sale -> sale.getSaleDate() != null)
                    .filter(sale -> sale.getSaleStatus() == SaleStatus.COMPLETED)
                    .filter(sale -> sale.getSaleDate().equals(currentDay))
                    .mapToDouble(sale -> sale.getTotalAmount().doubleValue())
                    .sum();
                
                weekRevenue[i] = dailyRevenue;
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return weekRevenue;
    }

    /**
     * Tìm giá trị max trong mảng doanh thu
     */
    public double getMaxRevenue(double[] revenues) {
        double max = 0;
        for (double rev : revenues) {
            if (rev > max) max = rev;
        }
        return max;
    }
}