package BUS;

import DAO.DBConnection;
import DAO.StockExportDAO;
import DTO.StockExportDTO;
import DTO.StockExportItemDTO;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class StockExportBUS {
    private final StockExportDAO dao = new StockExportDAO();
    private final ProductBUS productBUS = new ProductBUS();

    public List<StockExportDTO> getAllExports() {
        return dao.getAllExports();
    }

    public StockExportDTO getExportById(Long exportId) {
        if (exportId == null || exportId <= 0) return null;
        return dao.getExportById(exportId);
    }

    /**
     * Lưu phiếu xuất kho.
     * Sau khi lưu DB thành công: trừ tồn kho từng item.
     * Trả về danh sách tên sản phẩm có tồn < min_stock_level (dùng để hiển thị cảnh báo).
     * Ném IllegalArgumentException nếu validate thất bại.
     */
    public List<String> addExport(StockExportDTO dto) {
        if (dto == null) throw new IllegalArgumentException("Phiếu xuất không được null.");
        if (dto.getItems() == null || dto.getItems().isEmpty())
            throw new IllegalArgumentException("Phiếu xuất phải có ít nhất một sản phẩm.");
        if (dto.getEmployeeId() == null || dto.getEmployeeId() <= 0)
            throw new IllegalArgumentException("Nhân viên thực hiện không hợp lệ.");
        for (StockExportItemDTO item : dto.getItems()) {
            if (item.getQuantity() == null || item.getQuantity() <= 0)
                throw new IllegalArgumentException("Số lượng xuất phải lớn hơn 0.");
        }

        // Auto-generate code if not set
        if (dto.getExportCode() == null || dto.getExportCode().isBlank()) {
            dto.setExportCode(generateExportCode());
        }
        if (dto.getExportDate() == null) dto.setExportDate(LocalDateTime.now());
        if (dto.getStatus() == null) dto.setStatus("DONE");

        // Calculate total_amount = sum(qty * unitPrice)
        BigDecimal total = BigDecimal.ZERO;
        for (StockExportItemDTO item : dto.getItems()) {
            if (item.getUnitPrice() != null && item.getQuantity() != null) {
                total = total.add(item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
            }
        }
        dto.setTotalAmount(total);

        long exportId = dao.addExport(dto);
        if (exportId <= 0) throw new RuntimeException("Lưu phiếu xuất thất bại.");

        // Update stock & collect warnings
        List<String> warnings = new ArrayList<>();
        for (StockExportItemDTO item : dto.getItems()) {
            productBUS.updateStock(item.getProductId(), -item.getQuantity());
        }
        // Re-read products to check min stock
        var products = productBUS.getAllProducts();
        for (StockExportItemDTO item : dto.getItems()) {
            products.stream()
                .filter(p -> (long) p.getId() == item.getProductId())
                .findFirst()
                .ifPresent(p -> {
                    long newQty = p.getTotalQuantity() - item.getQuantity();
                    if (newQty < p.getMinStockLevel()) {
                        warnings.add(p.getName() + " (còn ~" + newQty + " " + (p.getUnit() != null ? p.getUnit() : "") + ")");
                    }
                });
        }
        return warnings;
    }

    public boolean deleteExport(Long exportId) {
        if (exportId == null || exportId <= 0) return false;
        return dao.deleteExport(exportId);
    }

    // ─────────────────────────────────────────────────────────────
    // Auto-code generation: XK{yyyyMMdd}-{001}
    // ─────────────────────────────────────────────────────────────
    private String generateExportCode() {
        String datePart = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String prefix = "XK" + datePart + "-";
        String sql = "SELECT export_code FROM stock_exports WHERE export_code LIKE ?";
        int max = 0;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, prefix + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String code = rs.getString(1);
                try {
                    int num = Integer.parseInt(code.substring(prefix.length()));
                    if (num > max) max = num;
                } catch (NumberFormatException ignored) {}
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return prefix + String.format("%03d", max + 1);
    }
}
