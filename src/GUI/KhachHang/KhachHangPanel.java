package GUI.KhachHang;

import GUI.UIUtils;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class KhachHangPanel extends JPanel {

    CardLayout innerCard;
    int editingRow = -1;
    DefaultTableModel tableModel;

    JTextField tfMaKH, tfTen, tfSdt, tfEmail, tfDiaChi,
               tfDiem, tfTgDK, tfLanCuoiMua, tfTongTien, tfHang, tfTrangThai;

    public static final String CARD_TABLE = "TABLE";
    public static final String CARD_THEM  = "THEM";

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
                "Địa chỉ", "Hạng", "Trạng thái", "Thao tác"
        };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int column) { return column == 7; }
        };
        tableModel.addRow(new Object[]{"KH001", "Lê Đỗ Thái Anh",    "098754321",  "anhdo@gmail.com",    "213LDTA",         "Bạc",       "Hoạt động",         ""});
        tableModel.addRow(new Object[]{"KH002", "Lý Nguyễn",          "0915987654", "nguyenly@gmail.com", "456 Nguyễn Trãi", "Vàng",      "Hoạt động",         ""});
        tableModel.addRow(new Object[]{"KH003", "Nguyễn Hoàng Sang",  "0933777888", "sangnguyen@gmail.com","789KTX",         "Đồng",      "Không hoạt động",   ""});
        tableModel.addRow(new Object[]{"KH004", "Trân dơ hầy",        "0977111222", "tranbado@gmail.com", "Bụi chúi",        "Kim cương", "Hoạt động",         ""});
        tableModel.addRow(new Object[]{"KH005", "Phạm Quang Vinh",    "0903123456", "vinh.pham@gmail.com", "12 Lê Lợi",       "Vàng",      "Hoạt động",         ""});
        tableModel.addRow(new Object[]{"KH006", "Đặng Mỹ Linh",       "0988123987", "linh.dang@gmail.com", "88 Pasteur",      "Bạc",       "Hoạt động",         ""});

        add(new KhachHangTableCard(this), CARD_TABLE);
        add(new KhachHangFormCard(this),  CARD_THEM);
        innerCard.show(this, CARD_TABLE);
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
        String maKH = tableModel.getValueAt(row, 0).toString();
        tfDiem.setText(getDiemFromData(maKH));
        tfTgDK.setText(getTgDKFromData(maKH));
        tfLanCuoiMua.setText(getLanCuoiMuaFromData(maKH));
        tfTongTien.setText(getTongTienFromData(maKH));
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
            tfDiaChi.getText(), tfHang.getText(), tfTrangThai.getText(), ""
        });
        clearForm();
        innerCard.show(this, CARD_TABLE);
    }

    void updateCustomer() {
        if (editingRow >= 0 && editingRow < tableModel.getRowCount()) {
            tableModel.setValueAt(tfMaKH.getText(),    editingRow, 0);
            tableModel.setValueAt(tfTen.getText(),     editingRow, 1);
            tableModel.setValueAt(tfSdt.getText(),     editingRow, 2);
            tableModel.setValueAt(tfEmail.getText(),   editingRow, 3);
            tableModel.setValueAt(tfDiaChi.getText(),  editingRow, 4);
            tableModel.setValueAt(tfHang.getText(),    editingRow, 5);
            tableModel.setValueAt(tfTrangThai.getText(), editingRow, 6);
            clearForm();
            enableFormFields(true);
            editingRow = -1;
        }
    }

    String getDiemFromData(String maKH) {
        return switch (maKH) {
            case "KH001" -> "36"; case "KH002" -> "580";
            case "KH003" -> "120"; case "KH004" -> "950";
            default -> "0";
        };
    }

    String getTgDKFromData(String maKH) {
        return switch (maKH) {
            case "KH001" -> "01/01/2026"; case "KH002" -> "15/03/2023";
            case "KH003" -> "20/06/2023"; case "KH004" -> "10/10/2023";
            default -> "01/01/2024";
        };
    }

    String getLanCuoiMuaFromData(String maKH) {
        return switch (maKH) {
            case "KH001" -> "15/03/2026"; case "KH002" -> "20/03/2024";
            case "KH003" -> "10/02/2024"; case "KH004" -> "11/03/2024";
            default -> "01/01/2024";
        };
    }

    String getTongTienFromData(String maKH) {
        return switch (maKH) {
            case "KH001" -> "3.600.000đ"; case "KH002" -> "12.070.000đ";
            case "KH003" -> "1.012.000đ"; case "KH004" -> "27.000.000đ";
            default -> "0đ";
        };
    }

    /** Used by DonHangCreateCard to navigate directly to the add form. */
    public void showCard(String card) {
        innerCard.show(this, card);
    }
}
