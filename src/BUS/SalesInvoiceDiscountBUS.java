package BUS;

import DAO.SalesInvoiceDiscountDAO;
import DTO.SalesInvoiceDiscountDTO;

public class SalesInvoiceDiscountBUS {

    private SalesInvoiceDiscountDAO dao = new SalesInvoiceDiscountDAO();

    public boolean addDiscountToInvoice(Long discountId, Long invoiceId) {

        SalesInvoiceDiscountDTO dto = new SalesInvoiceDiscountDTO();
        dto.setDiscountId(discountId);
        dto.setInvoiceId(invoiceId);

        return dao.insert(dto);
    }
}