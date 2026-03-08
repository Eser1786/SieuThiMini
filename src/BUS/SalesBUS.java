package BUS;

import DAO.SaleDAO;
import DAO.DBConnection;
import DTO.SaleDTO;
import DTO.enums.SaleEnum.SaleStatus;

import java.util.ArrayList;
import java.sql.*;

public class SalesBUS {
    private SaleDAO saleDAO;

    public SalesBUS() {
        saleDAO = new SaleDAO();
    }

    public ArrayList<SaleDTO> getAllSales() {
        return saleDAO.getAllSales();
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
        return saleDAO.updateStatus(saleCode, SaleStatus.COMPLETED);
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
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return "DH001";
    }
}