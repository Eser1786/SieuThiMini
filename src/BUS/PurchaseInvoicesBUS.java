package BUS;

import DAO.PurchaseInvoicesDAO;
import DTO.PurchaseInvoicesDTO;
import DTO.PurchaseInvoiceItemsDTO;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PurchaseInvoicesBUS {
    private PurchaseInvoicesDAO purchaseInvoicesDAO;

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

        // Generate invoice code if not provided
        if (invoice.getInvoiceCode() == null || invoice.getInvoiceCode().isBlank()) {
            invoice.setInvoiceCode(generateInvoiceCode());
        }

        // Set date_in if not provided
        if (invoice.getDateIn() == null) {
            invoice.setDateIn(LocalDateTime.now());
        }

        // Validation
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

        // Calculate totals if not set
        calculateTotals(invoice);

        return purchaseInvoicesDAO.addPurchaseInvoice(invoice);
    }

    public boolean updatePurchaseInvoice(PurchaseInvoicesDTO invoice) {
        if (invoice == null || invoice.getInvoiceId() == null) return false;

        // Validation
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

        // Calculate totals
        calculateTotals(invoice);

        return purchaseInvoicesDAO.updatePurchaseInvoice(invoice);
    }

    public boolean deletePurchaseInvoice(Long invoiceId) throws SQLException {
        if (invoiceId == null || invoiceId <= 0) return false;
        return purchaseInvoicesDAO.deletePurchaseInvoice(invoiceId);
    }

    private String generateInvoiceCode() {
        // Format: PN + YYYYMMDD + - + 3-digit number
        LocalDateTime now = LocalDateTime.now();
        String datePart = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String prefix = "PN" + datePart + "-";

        // Find the next available number
        List<PurchaseInvoicesDTO> allInvoices = getAllPurchaseInvoices();
        int maxNumber = 0;
        for (PurchaseInvoicesDTO inv : allInvoices) {
            if (inv.getInvoiceCode() != null && inv.getInvoiceCode().startsWith(prefix)) {
                try {
                    String numStr = inv.getInvoiceCode().substring(prefix.length());
                    int num = Integer.parseInt(numStr);
                    if (num > maxNumber) maxNumber = num;
                } catch (NumberFormatException e) {
                    // Ignore
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
}