package BUS;

import DAO.SaleDAO;
import DTO.SaleDTO;
import DTO.enums.SaleEnum.SaleStatus;

import java.util.ArrayList;

public class SalesBUS {
    private SaleDAO saleDAO;

    public SalesBUS() {
        saleDAO = new SaleDAO();
    }

    public ArrayList<SaleDTO> getAllSales() {
        return saleDAO.getAllSales();
    }

    public SaleDTO getSaleById(int saleId) {
        if (saleId <= 0) return null;
        return saleDAO.getSaleById(saleId);
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
}