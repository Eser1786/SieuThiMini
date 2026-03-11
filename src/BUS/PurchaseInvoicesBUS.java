package BUS;

import DAO.DBConnection;
import DAO.PurchaseInvoicesDAO;
import DAO.PurchasesDAO;
import DTO.PurchaseInvoicesDTO;
import DTO.PurchaseInvoiceItemsDTO;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import DTO.enums.PurchaseInvoicesEnum.PurchaseInvoicesStatus;
public class PurchaseInvoicesBUS {
    private PurchasesDAO purchasesDAO = new PurchasesDAO();
    private PurchaseInvoicesDAO purchaseInvoicesDAO;
    private final ProductBUS productBUS = new ProductBUS();

    public PurchaseInvoicesBUS() {
        purchaseInvoicesDAO = new PurchaseInvoicesDAO();
    }

    public List<PurchaseInvoicesDTO> getAllPurchaseInvoices() {
        return purchaseInvoicesDAO.getAllPurchaseInvoices();
    }

    public PurchaseInvoicesDTO getPurchaseInvoiceById(Long invoiceId) {
        if (invoiceId == null || invoiceId <= 0) return null;
        return purchaseInvoicesDAO.getPurchaseInvoiceById(invoiceId);
    }

    public boolean addPurchaseInvoice(PurchaseInvoicesDTO invoice) {
        if (invoice == null) return false;

        
        if (invoice.getInvoiceCode() == null || invoice.getInvoiceCode().isBlank()) {
            invoice.setInvoiceCode(generateInvoiceCode());
        }

        
        if (invoice.getDateIn() == null) {
            invoice.setDateIn(LocalDateTime.now());
        }

        
        if (invoice.getSupplierId() == null || invoice.getSupplierId() <= 0) {
            System.out.println("Nhà cung cấp không hợp lệ.");
            return false;
        }
        if (invoice.getEmployeeId() == null || invoice.getEmployeeId() <= 0) {
            System.out.println("Nhân viên không hợp lệ.");
            return false;
        }
        if (invoice.getTotalAmount() == null || invoice.getTotalAmount().signum() <= 0) {
            System.out.println("Tổng tiền phải lớn hơn 0.");
            return false;
        }
        if (invoice.getItems() == null || invoice.getItems().isEmpty()) {
            System.out.println("Hóa đơn phải có ít nhất một sản phẩm.");
            return false;
        }

        
        calculateTotals(invoice);

        return purchaseInvoicesDAO.addPurchaseInvoice(invoice);
        
    }

    
    public boolean confirmPurchaseInvoice(Long invoiceId) {

    PurchaseInvoicesDTO inv = getPurchaseInvoiceById(invoiceId);

    if(inv == null) return false;

    if(!"PAID".equals(inv.getPaymentStatus())){
        throw new RuntimeException("Phải thanh toán trước khi xác nhận nhập kho!");
    }

    if(inv.getStatus() != PurchaseInvoicesStatus.PENDING)
        return false;

    boolean ok = purchaseInvoicesDAO.confirmInvoice(invoiceId);

    if(ok && inv.getItems()!=null){
        for(PurchaseInvoiceItemsDTO item : inv.getItems()){
            productBUS.updateStock(item.getProductId(), item.getQuantity());
        }
    }

    return ok;
}
    public boolean updatePurchaseInvoice(PurchaseInvoicesDTO invoice) {
        if (invoice == null || invoice.getInvoiceId() == null) return false;

        
        if (invoice.getSupplierId() == null || invoice.getSupplierId() <= 0) {
            System.out.println("Nhà cung cấp không hợp lệ.");
            return false;
        }
        if (invoice.getEmployeeId() == null || invoice.getEmployeeId() <= 0) {
            System.out.println("Nhân viên không hợp lệ.");
            return false;
        }
        if (invoice.getTotalAmount() == null || invoice.getTotalAmount().signum() <= 0) {
            System.out.println("Tổng tiền phải lớn hơn 0.");
            return false;
        }
        if (invoice.getItems() == null || invoice.getItems().isEmpty()) {
            System.out.println("Hóa đơn phải có ít nhất một sản phẩm.");
            return false;
        }

        
        calculateTotals(invoice);

        return purchaseInvoicesDAO.updatePurchaseInvoice(invoice);
    }

    public boolean deletePurchaseInvoice(Long invoiceId) throws SQLException {
        if (invoiceId == null || invoiceId <= 0) return false;
        return purchaseInvoicesDAO.deletePurchaseInvoice(invoiceId);
    }

    private String generateInvoiceCode() {
        
        LocalDateTime now = LocalDateTime.now();
        String datePart = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String prefix = "PN" + datePart + "-";

        
        List<PurchaseInvoicesDTO> allInvoices = getAllPurchaseInvoices();
        int maxNumber = 0;
        for (PurchaseInvoicesDTO inv : allInvoices) {
            if (inv.getInvoiceCode() != null && inv.getInvoiceCode().startsWith(prefix)) {
                try {
                    String numStr = inv.getInvoiceCode().substring(prefix.length());
                    int num = Integer.parseInt(numStr);
                    if (num > maxNumber) maxNumber = num;
                } catch (NumberFormatException e) {
                    
                }
            }
        }
        int nextNumber = maxNumber + 1;
        return prefix + String.format("%03d", nextNumber);
    }

    private void calculateTotals(PurchaseInvoicesDTO invoice) {
        BigDecimal subtotal = BigDecimal.ZERO;
        for (PurchaseInvoiceItemsDTO item : invoice.getItems()) {
            if (item.getSubtotal() != null) {
                subtotal = subtotal.add(item.getSubtotal());
            } else if (item.getUnitPrice() != null && item.getQuantity() != null && item.getQuantity() > 0) {
                BigDecimal itemSubtotal = item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
                item.setSubtotal(itemSubtotal);
                subtotal = subtotal.add(itemSubtotal);
            }
        }
        invoice.setSubtotal(subtotal);

        BigDecimal discount = invoice.getDiscountAmount() != null ? invoice.getDiscountAmount() : BigDecimal.ZERO;
        BigDecimal tax = invoice.getTaxAmount() != null ? invoice.getTaxAmount() : BigDecimal.ZERO;
        BigDecimal total = subtotal.subtract(discount).add(tax);
        invoice.setTotalAmount(total);
    }
    public boolean cancelPurchaseInvoice(long invoiceId) throws Exception {
    return new PurchaseInvoicesDAO().cancelPurchaseInvoice(invoiceId);
}

public boolean confirmPayment(Long invoiceId){

    PurchaseInvoicesDTO inv = purchaseInvoicesDAO.getPurchaseInvoiceById(invoiceId);

    if(inv == null) return false;

    return purchaseInvoicesDAO.updatePaymentStatus(invoiceId,"PAID");
}
}
