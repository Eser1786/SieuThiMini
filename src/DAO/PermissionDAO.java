package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import DTO.PermissionDTO;

public class PermissionDAO {
    private Connection getConnection() throws SQLException {
        return DBConnection.getConnection();
    }

    public List<PermissionDTO> getAllPermissions() {
        List<PermissionDTO> list = new ArrayList<>();
        String sql = "SELECT * FROM permissions";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                PermissionDTO perm = new PermissionDTO();
                perm.setPermissionID(rs.getLong("permission_id"));
                perm.setPermissionName(rs.getString("permission_name"));
                perm.setDescription(rs.getString("description"));
                list.add(perm);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
