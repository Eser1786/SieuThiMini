package BUS;

import DAO.SalesInvoiceDAO;
import DTO.SalesInvoiceDTO;
import DTO.SalesInvoiceItemDTO;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class SalesInvoiceBUS {
    private SalesInvoiceDAO salesInvoiceDAO;

    public SalesInvoiceBUS() {
        salesInvoiceDAO = new SalesInvoiceDAO();
    }

    public List<SalesInvoiceDTO> getAllSalesInvoices() {
        return salesInvoiceDAO.getAllSalesInvoices();
    }

    public SalesInvoiceDTO getSalesInvoiceById(Long invoiceId) {
        if (invoiceId == null || invoiceId <= 0) return null;
        return salesInvoiceDAO.getSalesInvoiceById(invoiceId);
    }

    public boolean addSalesInvoice(SalesInvoiceDTO invoice) {
        if (invoice == null) return false;

        // Generate invoice code if not provided
        if (invoice.getInvoiceCode() == null || invoice.getInvoiceCode().isBlank()) {
            invoice.setInvoiceCode(generateInvoiceCode());
        }

        // Validation
        if (invoice.getCustomerId() == null || invoice.getCustomerId() <= 0) {
            System.out.println("Khách hàng không hợp lệ.");
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

        return salesInvoiceDAO.addSalesInvoice(invoice);
    }

    public boolean updateSalesInvoice(SalesInvoiceDTO invoice) {
        if (invoice == null || invoice.getInvoiceId() == null) return false;

        // Validation
        if (invoice.getCustomerId() == null || invoice.getCustomerId() <= 0) {
            System.out.println("Khách hàng không hợp lệ.");
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

        return salesInvoiceDAO.updateSalesInvoice(invoice);
    }

    public boolean deleteSalesInvoice(Long invoiceId) throws SQLException {
        if (invoiceId == null || invoiceId <= 0) return false;
        return salesInvoiceDAO.deleteSalesInvoice(invoiceId);
    }

    private String generateInvoiceCode() {
        // Format: HD + YYYYMMDD + - + 3-digit number
        LocalDateTime now = LocalDateTime.now();
        String datePart = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String prefix = "HD" + datePart + "-";

        // Find the next available number
        List<SalesInvoiceDTO> allInvoices = getAllSalesInvoices();
        int maxNumber = 0;
        for (SalesInvoiceDTO inv : allInvoices) {
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

    private void calculateTotals(SalesInvoiceDTO invoice) {
        BigDecimal subtotal = BigDecimal.ZERO;
        for (SalesInvoiceItemDTO item : invoice.getItems()) {
            if (item.getSubtotal() != null) {
                subtotal = subtotal.add(item.getSubtotal());
            } else if (item.getUnitPrice() != null && item.getQuantity() > 0) {
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