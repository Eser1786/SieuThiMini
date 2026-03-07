package DAO;

import DTO.StockExportDTO;
import DTO.StockExportItemDTO;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StockExportDAO {

    // ─────────────────────────────────────────────────────────────
    // READ
    // ─────────────────────────────────────────────────────────────

    public List<StockExportDTO> getAllExports() {
        List<StockExportDTO> list = new ArrayList<>();
        String sql = """
                SELECT se.*, e.full_name AS employee_name
                FROM stock_exports se
                LEFT JOIN employees e ON se.employee_id = e.employee_id
                ORDER BY se.export_date DESC
                """;
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                StockExportDTO dto = mapRow(rs);
                dto.setItems(getItemsByExportId(conn, dto.getExportId()));
                list.add(dto);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public StockExportDTO getExportById(Long exportId) {
        String sql = """
                SELECT se.*, e.full_name AS employee_name
                FROM stock_exports se
                LEFT JOIN employees e ON se.employee_id = e.employee_id
                WHERE se.export_id = ?
                """;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, exportId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                StockExportDTO dto = mapRow(rs);
                dto.setItems(getItemsByExportId(conn, dto.getExportId()));
                return dto;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // ─────────────────────────────────────────────────────────────
    // WRITE
    // ─────────────────────────────────────────────────────────────

    /**
     * Chèn phiếu xuất kho (header + items) trong một transaction.
     * Trả về export_id được sinh ra, hoặc -1 nếu lỗi.
     */
    public long addExport(StockExportDTO dto) {
        String sqlHeader = """
                INSERT INTO stock_exports
                    (export_code, export_date, employee_id, reason, notes, total_amount, status, created_at, updated_at)
                VALUES (?, ?, ?, ?, ?, ?, ?, NOW(), NOW())
                """;
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(sqlHeader, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, dto.getExportCode());
                ps.setTimestamp(2, dto.getExportDate() != null
                        ? Timestamp.valueOf(dto.getExportDate()) : new Timestamp(System.currentTimeMillis()));
                if (dto.getEmployeeId() != null) ps.setLong(3, dto.getEmployeeId());
                else ps.setNull(3, Types.BIGINT);
                ps.setString(4, dto.getReason() != null ? dto.getReason() : "LOSS");
                ps.setString(5, dto.getNotes());
                ps.setBigDecimal(6, dto.getTotalAmount() != null ? dto.getTotalAmount() : BigDecimal.ZERO);
                ps.setString(7, dto.getStatus() != null ? dto.getStatus() : "DONE");
                ps.executeUpdate();

                ResultSet keys = ps.getGeneratedKeys();
                if (!keys.next()) { conn.rollback(); return -1; }
                long exportId = keys.getLong(1);
                dto.setExportId(exportId);

                addItems(conn, exportId, dto.getItems());
                conn.commit();
                return exportId;
            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
                return -1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public boolean deleteExport(Long exportId) {
        // Items deleted by CASCADE
        String sql = "DELETE FROM stock_exports WHERE export_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, exportId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ─────────────────────────────────────────────────────────────
    // HELPERS
    // ─────────────────────────────────────────────────────────────

    private List<StockExportItemDTO> getItemsByExportId(Connection conn, Long exportId) throws SQLException {
        List<StockExportItemDTO> items = new ArrayList<>();
        String sql = """
                SELECT sei.*, p.product_code, p.name AS product_name
                FROM stock_export_items sei
                LEFT JOIN products p ON sei.product_id = p.product_id
                WHERE sei.export_id = ?
                """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, exportId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                StockExportItemDTO item = new StockExportItemDTO();
                item.setId(rs.getLong("id"));
                item.setExportId(exportId);
                item.setProductId(rs.getLong("product_id"));
                item.setProductCode(rs.getString("product_code"));
                item.setProductName(rs.getString("product_name"));
                item.setQuantity(rs.getLong("quantity"));
                item.setUnitPrice(rs.getBigDecimal("unit_price"));
                item.setNotes(rs.getString("notes"));
                items.add(item);
            }
        }
        return items;
    }

    private void addItems(Connection conn, long exportId, List<StockExportItemDTO> items) throws SQLException {
        if (items == null || items.isEmpty()) return;
        String sql = "INSERT INTO stock_export_items (export_id, product_id, quantity, unit_price, notes) VALUES (?,?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (StockExportItemDTO item : items) {
                ps.setLong(1, exportId);
                ps.setLong(2, item.getProductId());
                ps.setLong(3, item.getQuantity() != null ? item.getQuantity() : 0);
                ps.setBigDecimal(4, item.getUnitPrice() != null ? item.getUnitPrice() : BigDecimal.ZERO);
                ps.setString(5, item.getNotes());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    private StockExportDTO mapRow(ResultSet rs) throws SQLException {
        StockExportDTO dto = new StockExportDTO();
        dto.setExportId(rs.getLong("export_id"));
        dto.setExportCode(rs.getString("export_code"));
        Timestamp t = rs.getTimestamp("export_date");
        if (t != null) dto.setExportDate(t.toLocalDateTime());
        long empId = rs.getLong("employee_id");
        if (!rs.wasNull()) dto.setEmployeeId(empId);
        dto.setEmployeeName(rs.getString("employee_name"));
        dto.setReason(rs.getString("reason"));
        dto.setNotes(rs.getString("notes"));
        dto.setTotalAmount(rs.getBigDecimal("total_amount"));
        dto.setStatus(rs.getString("status"));
        Timestamp ct = rs.getTimestamp("created_at");
        if (ct != null) dto.setCreatedAt(ct.toLocalDateTime());
        Timestamp ut = rs.getTimestamp("updated_at");
        if (ut != null) dto.setUpdatedAt(ut.toLocalDateTime());
        return dto;
    }
}
