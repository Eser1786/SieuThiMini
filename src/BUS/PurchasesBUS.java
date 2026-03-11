package BUS;

import DAO.PurchasesDAO;
import DTO.PurchasesDTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PurchasesBUS {
    private PurchasesDAO purchasesDAO;

    public PurchasesBUS() {
        purchasesDAO = new PurchasesDAO();
    }

    public List<PurchasesDTO> getAllPurchases() {
        return purchasesDAO.getAllPurchases();
    }

    public PurchasesDTO getPurchaseById(Long purchaseId) {
        if (purchaseId == null || purchaseId <= 0) return null;
        return purchasesDAO.getPurchaseById(purchaseId);
    }

    public long addPurchase(PurchasesDTO purchase) {

    if (purchase == null) return -1;

    
    if (purchase.getPurchaseCode() == null || purchase.getPurchaseCode().isBlank()) {
        purchase.setPurchaseCode(generatePurchaseCode());
    }

    
    if (purchase.getPurchaseDate() == null) {
        purchase.setPurchaseDate(LocalDateTime.now());
    }

    
    if (purchase.getSupplierId() == null || purchase.getSupplierId() <= 0) {
        System.out.println("Nhà cung cấp không hợp lệ.");
        return -1;
    }

    if (purchase.getEmployeeId() == null || purchase.getEmployeeId() <= 0) {
        System.out.println("Nhân viên không hợp lệ.");
        return -1;
    }

    if (purchase.getTotalAmount() == null || purchase.getTotalAmount().signum() <= 0) {
        System.out.println("Tổng tiền phải lớn hơn 0.");
        return -1;
    }

    
    calculateTotals(purchase);

    return purchasesDAO.insert(purchase); 
}
    public boolean updatePurchase(PurchasesDTO purchase) {
        if (purchase == null || purchase.getPurchaseId() == null) return false;

        
        if (purchase.getSupplierId() == null || purchase.getSupplierId() <= 0) {
            System.out.println("Nhà cung cấp không hợp lệ.");
            return false;
        }
        if (purchase.getEmployeeId() == null || purchase.getEmployeeId() <= 0) {
            System.out.println("Nhân viên không hợp lệ.");
            return false;
        }
        if (purchase.getTotalAmount() == null || purchase.getTotalAmount().signum() <= 0) {
            System.out.println("Tổng tiền phải lớn hơn 0.");
            return false;
        }

        
        calculateTotals(purchase);

        return purchasesDAO.updatePurchase(purchase);
    }

    public boolean deletePurchase(Long purchaseId) {
        if (purchaseId == null || purchaseId <= 0) return false;
        return purchasesDAO.deletePurchase(purchaseId);
    }

    private String generatePurchaseCode() {
        
        LocalDateTime now = LocalDateTime.now();
        String datePart = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String prefix = "PM" + datePart + "-";

        
        List<PurchasesDTO> allPurchases = getAllPurchases();
        int maxNumber = 0;
        for (PurchasesDTO pur : allPurchases) {
            if (pur.getPurchaseCode() != null && pur.getPurchaseCode().startsWith(prefix)) {
                try {
                    String numStr = pur.getPurchaseCode().substring(prefix.length());
                    int num = Integer.parseInt(numStr);
                    if (num > maxNumber) maxNumber = num;
                } catch (NumberFormatException e) {
                    
                }
            }
        }
        int nextNumber = maxNumber + 1;
        return prefix + String.format("%03d", nextNumber);
    }

    private void calculateTotals(PurchasesDTO purchase) {
        BigDecimal subtotal = purchase.getSubtotal() != null ? purchase.getSubtotal() : BigDecimal.ZERO;
        BigDecimal discount = purchase.getDiscountAmount() != null ? purchase.getDiscountAmount() : BigDecimal.ZERO;
        BigDecimal tax = purchase.getTaxAmount() != null ? purchase.getTaxAmount() : BigDecimal.ZERO;
        BigDecimal total = subtotal.subtract(discount).add(tax);
        purchase.setTotalAmount(total);
    }
    
}