package GUI.KhachHang;

import javax.swing.*;
import java.awt.*;

class KhachHangFormCard extends JPanel {

    KhachHangFormCard(KhachHangPanel parent) {
        super(new BorderLayout());
        setBackground(new Color(0xF0EFF8));

        // ── HEADER (back button) ─────────────────────────────────────────────
        JPanel formHeader = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 18));
        formHeader.setBackground(new Color(0xF0EFF8));
        formHeader.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0xCCCCCC)));

        JButton btnQuayLai = new JButton("← Quay lại danh sách");
        btnQuayLai.setFont(new Font("Arial", Font.BOLD, 22));
        btnQuayLai.setBackground(new Color(0x9B8EA8));
        btnQuayLai.setForeground(Color.WHITE);
        btnQuayLai.setFocusPainted(false);
        btnQuayLai.setBorderPainted(false);
        btnQuayLai.setPreferredSize(new Dimension(300, 48));
        btnQuayLai.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnQuayLai.addActionListener(e -> {
            parent.clearForm();
            parent.enableFormFields(true);
            parent.editingRow = -1;
            parent.innerCard.show(parent, KhachHangPanel.CARD_TABLE);
        });
        formHeader.add(btnQuayLai);
        add(formHeader, BorderLayout.NORTH);

        // ── FORM BODY ────────────────────────────────────────────────────────
        JPanel formBody = new JPanel(new GridBagLayout());
        formBody.setBackground(new Color(0xF0EFF8));
        formBody.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8);
        Font labelFont = new Font("Arial", Font.BOLD, 18);

        // Row 0: Mã KH / Tên
        gbc.gridx = 0; gbc.gridy = 0; formBody.add(lbl("Mã KH:", labelFont), gbc);
        gbc.gridx = 1; formBody.add(parent.tfMaKH, gbc);
        gbc.gridx = 2; formBody.add(lbl("Tên khách hàng:", labelFont), gbc);
        gbc.gridx = 3; formBody.add(parent.tfTen, gbc);

        // Row 1: SĐT / Email
        gbc.gridx = 0; gbc.gridy = 1; formBody.add(lbl("Số điện thoại:", labelFont), gbc);
        gbc.gridx = 1; formBody.add(parent.tfSdt, gbc);
        gbc.gridx = 2; formBody.add(lbl("Email:", labelFont), gbc);
        gbc.gridx = 3; formBody.add(parent.tfEmail, gbc);

        // Row 2: Địa chỉ (full width)
        gbc.gridx = 0; gbc.gridy = 2; formBody.add(lbl("Địa chỉ:", labelFont), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3; formBody.add(parent.tfDiaChi, gbc);
        gbc.gridwidth = 1;

        // Row 3: Điểm / Thời gian ĐK
        gbc.gridx = 0; gbc.gridy = 3; formBody.add(lbl("Điểm tích lũy:", labelFont), gbc);
        gbc.gridx = 1; formBody.add(parent.tfDiem, gbc);
        gbc.gridx = 2; formBody.add(lbl("Thời gian ĐK:", labelFont), gbc);
        gbc.gridx = 3; formBody.add(parent.tfTgDK, gbc);

        // Row 4: Lần cuối mua / Tổng tiền
        gbc.gridx = 0; gbc.gridy = 4; formBody.add(lbl("Lần cuối mua:", labelFont), gbc);
        gbc.gridx = 1; formBody.add(parent.tfLanCuoiMua, gbc);
        gbc.gridx = 2; formBody.add(lbl("Tổng tiền đã mua:", labelFont), gbc);
        gbc.gridx = 3; formBody.add(parent.tfTongTien, gbc);

        // Row 5: Hạng / Trạng thái
        gbc.gridx = 0; gbc.gridy = 5; formBody.add(lbl("Hạng:", labelFont), gbc);
        gbc.gridx = 1; formBody.add(parent.tfHang, gbc);
        gbc.gridx = 2; formBody.add(lbl("Trạng thái:", labelFont), gbc);
        gbc.gridx = 3; formBody.add(parent.tfTrangThai, gbc);

        add(formBody, BorderLayout.CENTER);

        // ── BUTTON PANEL ─────────────────────────────────────────────────────
        JPanel btnWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 16));
        btnWrapper.setBackground(new Color(0xF0EFF8));
        btnWrapper.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(0xCCCCCC)));

        JButton btnLuu     = makeBtn("LƯU",      new Color(0xB83434));
        JButton btnSua     = makeBtn("SỬA",      new Color(0x6677C8));
        JButton btnXoa     = makeBtn("XÓA",      new Color(0xB83434));
        JButton btnHoanTac = makeBtn("HOÀN TÁC", new Color(0xFF7043));
        btnHoanTac.setPreferredSize(new Dimension(180, 52));

        btnSua.setVisible(false);
        btnXoa.setVisible(false);
        btnHoanTac.setVisible(false);

        btnLuu.addActionListener(e -> {
            if (parent.editingRow == -1) {
                parent.saveNewCustomer();
            } else {
                parent.updateCustomer();
                parent.innerCard.show(parent, KhachHangPanel.CARD_TABLE);
            }
        });

        btnSua.addActionListener(e -> {
            if (parent.editingRow >= 0) {
                parent.enableFormFields(true);
                btnSua.setVisible(false);
                btnLuu.setVisible(true);
                btnXoa.setVisible(true);
                btnHoanTac.setVisible(true);
            }
        });

        btnXoa.addActionListener(e -> {
            if (parent.editingRow >= 0) {
                int confirm = JOptionPane.showConfirmDialog(parent,
                    "Bạn có chắc muốn xóa khách hàng này?",
                    "Xác nhận xóa", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (confirm == JOptionPane.YES_OPTION) {
                    parent.tableModel.removeRow(parent.editingRow);
                    parent.clearForm();
                    parent.enableFormFields(true);
                    parent.editingRow = -1;
                    parent.innerCard.show(parent, KhachHangPanel.CARD_TABLE);
                }
            }
        });

        btnHoanTac.addActionListener(e -> {
            parent.loadFormData(parent.editingRow);
            parent.enableFormFields(false);
            btnSua.setVisible(true);
            btnLuu.setVisible(false);
            btnXoa.setVisible(false);
            btnHoanTac.setVisible(false);
        });

        btnWrapper.add(btnLuu);
        btnWrapper.add(btnSua);
        btnWrapper.add(btnXoa);
        btnWrapper.add(btnHoanTac);
        add(btnWrapper, BorderLayout.SOUTH);
    }

    private static JLabel lbl(String text, Font font) {
        JLabel l = new JLabel(text);
        l.setFont(font);
        return l;
    }

    private static JButton makeBtn(String text, Color bg) {
        JButton b = new JButton(text);
        b.setFont(new Font("Arial", Font.BOLD, 24));
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setPreferredSize(new Dimension(160, 52));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }
}
