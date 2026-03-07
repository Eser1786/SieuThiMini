package GUI.KhachHang;

import BUS.CustomerBUS;
import DTO.CustomerDTO;
import DTO.enums.CustomerEnum.CustomerStatus;
import DTO.enums.CustomerEnum.CustomerType;
import GUI.UIUtils;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class KhachHangPanel extends JPanel {

    CardLayout innerCard;
    int editingRow = -1;
    DefaultTableModel tableModel;

    JTextField tfMaKH, tfTen, tfSdt, tfEmail, tfDiaChi,
               tfDiem, tfTgDK, tfLanCuoiMua, tfTongTien, tfHang, tfTrangThai;

    public static final String CARD_TABLE = "TABLE";
    public static final String CARD_THEM  = "THEM";

    // Hidden column indices
    static final int COL_DIEM       = 8;
    static final int COL_TGDK       = 9;
    static final int COL_LANCUOIMUA = 10;
    static final int COL_TONGTIEN   = 11;
    static final int COL_ID         = 12;

    public KhachHangPanel() {
        innerCard = new CardLayout();
        setLayout(innerCard);

        tfMaKH       = UIUtils.makeField();
        tfTen        = UIUtils.makeField();
        tfSdt        = UIUtils.makeField();
        tfEmail      = UIUtils.makeField();
        tfDiaChi     = UIUtils.makeField();
        tfDiem       = UIUtils.makeField();
        tfTgDK       = UIUtils.makeField();
        tfLanCuoiMua = UIUtils.makeField();
        tfTongTien   = UIUtils.makeField();
        tfHang       = UIUtils.makeField();
        tfTrangThai  = UIUtils.makeField();

        String[] columns = {
                "Mã KH", "Tên", "Số điện thoại", "Email",
                "Địa chỉ", "Hạng", "Trạng thái", "Thao tác",
                "Điểm", "TgDK", "LanCuoiMua", "TongTien", "ID"
        };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int column) { return column == 7; }
        };
        loadCustomers();

        add(new KhachHangTableCard(this), CARD_TABLE);
        add(new KhachHangFormCard(this),  CARD_THEM);
        innerCard.show(this, CARD_TABLE);
    }

    void loadCustomers() {
        tableModel.setRowCount(0);
        try {
            ArrayList<CustomerDTO> list = new CustomerBUS().getAllCustomers();
            if (list == null) return;
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            for (CustomerDTO c : list) {
                String hang     = typeToVN(c.getType());
                String tt       = statusToVN(c.getStatus());
                String diem     = String.valueOf(c.getLoyaltyPoints());
                String tgDK     = c.getCreatedAt() != null ? c.getCreatedAt().format(fmt) : "";
                String lanCuoi  = c.getLastPurchaseAt() != null ? c.getLastPurchaseAt().format(fmt) : "";
                String tongTien = c.getTotalSpent() != null
                                  ? String.format("%,.0f\u0111", c.getTotalSpent()) : "0\u0111";
                tableModel.addRow(new Object[]{
                    c.getCode(), c.getFullName(), c.getPhone(), c.getEmail(),
                    c.getAddress(), hang, tt, "",
                    diem, tgDK, lanCuoi, tongTien, c.getId()
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static String typeToVN(CustomerType t) {
        if (t == null) return "Đồng";
        return switch (t) {
            case REGULAR -> "Đồng";
            case SILVER  -> "Bạc";
            case GOLD    -> "Vàng";
            case DIAMOND -> "Kim cương";
        };
    }

    static String statusToVN(CustomerStatus s) {
        if (s == null) return "Hoạt động";
        return switch (s) {
            case ACTIVE   -> "Hoạt động";
            case INACTIVE -> "Không hoạt động";
            case BLOCKED  -> "Bị chặn";
        };
    }

    static CustomerType vnToType(String s) {
        return switch (s) {
            case "Bạc"       -> CustomerType.SILVER;
            case "Vàng"      -> CustomerType.GOLD;
            case "Kim cương" -> CustomerType.DIAMOND;
            default          -> CustomerType.REGULAR;
        };
    }

    static CustomerStatus vnToStatus(String s) {
        if ("Không hoạt động".equals(s)) return CustomerStatus.INACTIVE;
        if ("Bị chặn".equals(s))         return CustomerStatus.BLOCKED;
        return CustomerStatus.ACTIVE;
    }

    void clearForm() {
        tfMaKH.setText(""); tfTen.setText(""); tfSdt.setText(""); tfEmail.setText("");
        tfDiaChi.setText(""); tfDiem.setText(""); tfTgDK.setText("");
        tfLanCuoiMua.setText(""); tfTongTien.setText(""); tfHang.setText(""); tfTrangThai.setText("");
    }

    void enableFormFields(boolean enable) {
        for (JTextField f : new JTextField[]{
                tfMaKH, tfTen, tfSdt, tfEmail, tfDiaChi,
                tfDiem, tfTgDK, tfLanCuoiMua, tfTongTien, tfHang, tfTrangThai})
            f.setEditable(enable);
    }

    void loadFormData(int row) {
        tfMaKH.setText(tableModel.getValueAt(row, 0).toString());
        tfTen.setText(tableModel.getValueAt(row, 1).toString());
        tfSdt.setText(tableModel.getValueAt(row, 2).toString());
        tfEmail.setText(tableModel.getValueAt(row, 3).toString());
        tfDiaChi.setText(tableModel.getValueAt(row, 4).toString());
        tfHang.setText(tableModel.getValueAt(row, 5).toString());
        tfTrangThai.setText(tableModel.getValueAt(row, 6).toString());
        tfDiem.setText(tableModel.getValueAt(row, COL_DIEM).toString());
        tfTgDK.setText(tableModel.getValueAt(row, COL_TGDK).toString());
        tfLanCuoiMua.setText(tableModel.getValueAt(row, COL_LANCUOIMUA).toString());
        tfTongTien.setText(tableModel.getValueAt(row, COL_TONGTIEN).toString());
    }

    void saveNewCustomer() {
        if (tfMaKH.getText().trim().isEmpty() || tfTen.getText().trim().isEmpty()
                || tfSdt.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Vui lòng nhập Mã KH, Tên và Số điện thoại!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        tableModel.addRow(new Object[]{
            tfMaKH.getText(), tfTen.getText(), tfSdt.getText(), tfEmail.getText(),
            tfDiaChi.getText(), tfHang.getText(), tfTrangThai.getText(), "",
            "0", "", "", "0đ", 0
        });
        clearForm();
        innerCard.show(this, CARD_TABLE);
    }

    void updateCustomer() {
        if (editingRow >= 0 && editingRow < tableModel.getRowCount()) {
            tableModel.setValueAt(tfMaKH.getText(),      editingRow, 0);
            tableModel.setValueAt(tfTen.getText(),       editingRow, 1);
            tableModel.setValueAt(tfSdt.getText(),       editingRow, 2);
            tableModel.setValueAt(tfEmail.getText(),     editingRow, 3);
            tableModel.setValueAt(tfDiaChi.getText(),    editingRow, 4);
            tableModel.setValueAt(tfHang.getText(),      editingRow, 5);
            tableModel.setValueAt(tfTrangThai.getText(), editingRow, 6);
            clearForm();
            enableFormFields(true);
            editingRow = -1;
        }
    }

    /** Used by DonHangCreateCard to navigate directly to the add form. */
    public void showCard(String card) {
        innerCard.show(this, card);
    }
}

