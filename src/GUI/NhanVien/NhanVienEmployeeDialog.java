package GUI.NhanVien;

import DTO.RoleDTO;
import GUI.UIUtils;
import javax.swing.*;
import java.awt.*;
import java.awt.Dialog;
import java.awt.image.BufferedImage;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.imageio.ImageIO;

/**
 * Dialog Thêm / Sửa nhân viên — tách từ NhanVienPanel.showEmployeeDialog.
 */
class NhanVienEmployeeDialog {

    private static final Color ACCENT    = new Color(0x5C4A7F);
    private static final int COL_MA = 0, COL_TEN = 1, COL_CHUCVU = 2;
    private static final int COL_SDT = 3, COL_EMAIL = 4, COL_NGAY = 5, COL_PASS = 6;

    static void show(NhanVienPanel parent, Integer prefilledRow, int editRow) {
        boolean isEdit = (prefilledRow != null);
        String title = isEdit ? "Sửa Nhân Viên" : "Thêm Nhân Viên";

        JDialog dlg = new JDialog(SwingUtilities.getWindowAncestor(parent),
                title, Dialog.ModalityType.APPLICATION_MODAL);
        dlg.setResizable(false);
        dlg.setLayout(new BorderLayout());

        // Header
        JPanel hdr = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 14));
        hdr.setBackground(new Color(0xAF9FCB));
        JLabel hdrLbl = new JLabel(isEdit ? "Sửa thông tin nhân viên" : "Thêm nhân viên mới");
        hdrLbl.setFont(new Font("Arial", Font.BOLD, 18));
        hdrLbl.setForeground(Color.WHITE);
        hdr.add(hdrLbl);

        // Photo section
        final String[] tmpPhotoPath = {null};
        JPanel photoSection = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 8));
        photoSection.setBackground(new Color(0xF3F0FA));
        photoSection.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0xD1C4E9)));

        JLabel photoPreview = new JLabel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getIcon() == null) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setColor(new Color(0xBBBBBB));
                    g2.setFont(new Font("Arial", Font.PLAIN, 11));
                    String hint = "Chưa có ảnh";
                    FontMetrics fm = g2.getFontMetrics();
                    g2.drawString(hint, (getWidth()-fm.stringWidth(hint))/2, getHeight()/2 + fm.getAscent()/2);
                    g2.dispose();
                }
            }
        };
        photoPreview.setPreferredSize(new Dimension(80, 80));
        photoPreview.setBorder(BorderFactory.createLineBorder(new Color(0xAAAAAA)));
        photoPreview.setBackground(Color.WHITE);
        photoPreview.setOpaque(true);
        photoPreview.setHorizontalAlignment(SwingConstants.CENTER);

        if (isEdit) {
            String ma = parent.tableModel.getValueAt(prefilledRow, COL_MA).toString();
            String path = parent.photoPathMap.get(ma);
            if (path != null) {
                try {
                    BufferedImage img = ImageIO.read(new File(path));
                    if (img != null) photoPreview.setIcon(new ImageIcon(img.getScaledInstance(80, 80, Image.SCALE_SMOOTH)));
                    tmpPhotoPath[0] = path;
                } catch (Exception ignored) {}
            }
        }

        JButton btnChonAnh = new JButton("Chọn ảnh");
        btnChonAnh.setFont(new Font("Arial", Font.BOLD, 13));
        btnChonAnh.setBackground(new Color(0xD9D9D9));
        btnChonAnh.setForeground(new Color(0x333333));
        btnChonAnh.setFocusPainted(false);
        btnChonAnh.setBorderPainted(false);
        btnChonAnh.setOpaque(true);
        btnChonAnh.setCursor(new Cursor(Cursor.HAND_CURSOR));
        JLabel lbPhotoHint = new JLabel("<html><font color='gray' size='2'>Vuông, 256–512px<br>(tự động scale)</font></html>");

        btnChonAnh.addActionListener(ev -> {
            Window owner = SwingUtilities.getWindowAncestor(dlg);
            java.awt.FileDialog fd = new java.awt.FileDialog(
                (owner instanceof java.awt.Frame) ? (java.awt.Frame) owner : null,
                "Chọn ảnh nhân viên", java.awt.FileDialog.LOAD);
            fd.setFilenameFilter((dir, name) -> {
                String n = name.toLowerCase();
                return n.endsWith(".jpg") || n.endsWith(".jpeg") || n.endsWith(".png");
            });
            fd.setVisible(true);
            String chosenDir  = fd.getDirectory();
            String chosenFile = fd.getFile();
            if (chosenDir == null || chosenFile == null) return;
            File f = new File(chosenDir, chosenFile);
            try {
                BufferedImage img = ImageIO.read(f);
                if (img == null) { JOptionPane.showMessageDialog(dlg, "Không đọc được file ảnh.", "Lỗi", JOptionPane.ERROR_MESSAGE); return; }
                if (img.getWidth() != img.getHeight())
                    JOptionPane.showMessageDialog(dlg, "Ảnh không vuông — sẽ bị cắt khi hiển thị.", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                tmpPhotoPath[0] = f.getAbsolutePath();
                photoPreview.setIcon(new ImageIcon(img.getScaledInstance(80, 80, Image.SCALE_SMOOTH)));
                photoPreview.repaint();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dlg, "Lỗi khi đọc ảnh: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        photoSection.add(photoPreview);
        JPanel photoRight = new JPanel(new GridLayout(2, 1, 0, 4));
        photoRight.setOpaque(false);
        photoRight.add(btnChonAnh);
        photoRight.add(lbPhotoHint);
        photoSection.add(photoRight);

        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.add(hdr, BorderLayout.NORTH);
        northPanel.add(photoSection, BorderLayout.SOUTH);
        dlg.add(northPanel, BorderLayout.NORTH);

        // Form fields: [0]Ho ten, [1]Username, [2]SDT, [3]Email, [4]Luong
        JTextField[] tfs = new JTextField[5];
        for (int i = 0; i < tfs.length; i++) {
            tfs[i] = UIUtils.makeField();
            tfs[i].setPreferredSize(new Dimension(200, 32));
        }
        UIUtils.attachMoneyFormatter(tfs[4]);

        SpinnerDateModel dateModel = new SpinnerDateModel();
        JSpinner spNgay = new JSpinner(dateModel);
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(spNgay, "dd/MM/yyyy");
        spNgay.setEditor(dateEditor);
        spNgay.setPreferredSize(new Dimension(200, 32));
        spNgay.setFont(new Font("Arial", Font.PLAIN, 13));

        JPasswordField pfPass = new JPasswordField(18);
        pfPass.setFont(new Font("Arial", Font.PLAIN, 13));
        pfPass.setPreferredSize(new Dimension(200, 32));
        JButton btnShowPass = new JButton(UIUtils.iconEyeOpen(18, ACCENT));
        btnShowPass.setToolTipText("Hiện / Ẩn mật khẩu");
        btnShowPass.setPreferredSize(new Dimension(32, 32));
        btnShowPass.setFocusPainted(false);
        btnShowPass.setBorderPainted(false);
        btnShowPass.setContentAreaFilled(false);
        btnShowPass.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnShowPass.addActionListener(ev -> {
            if (pfPass.getEchoChar() != 0) {
                pfPass.setEchoChar((char) 0);
                btnShowPass.setIcon(UIUtils.iconEyeOff(18, ACCENT));
            } else {
                pfPass.setEchoChar('\u2022');
                btnShowPass.setIcon(UIUtils.iconEyeOpen(18, ACCENT));
            }
        });

        String[] roleNames = parent.roles.isEmpty()
            ? new String[]{"Nhân viên", "Quản lý"}
            : parent.roles.stream().map(RoleDTO::getName).toArray(String[]::new);
        JComboBox<String> cbRole = new JComboBox<>(roleNames);
        UIUtils.styleComboBox(cbRole);
        cbRole.setPreferredSize(new Dimension(200, 32));

        JTextField tfMa = UIUtils.makeField();
        tfMa.setPreferredSize(new Dimension(200, 32));
        tfMa.setEditable(false);
        tfMa.setBackground(new Color(0xE8E6F0));
        tfMa.setForeground(new Color(0x888888));
        tfMa.setText(isEdit ? parent.tableModel.getValueAt(prefilledRow, COL_MA).toString() : parent.generateMaNV());

        if (isEdit) {
            tfs[0].setText(parent.tableModel.getValueAt(prefilledRow, COL_TEN).toString());
            tfs[2].setText(parent.tableModel.getValueAt(prefilledRow, COL_SDT).toString());
            tfs[3].setText(parent.tableModel.getValueAt(prefilledRow, COL_EMAIL).toString());
            String ngayStr = parent.tableModel.getValueAt(prefilledRow, COL_NGAY).toString();
            if (!ngayStr.isEmpty()) {
                try { dateModel.setValue(new SimpleDateFormat("dd/MM/yyyy").parse(ngayStr)); }
                catch (Exception ignored) {}
            }
            String currentRole = parent.tableModel.getValueAt(prefilledRow, COL_CHUCVU).toString();
            for (int i = 0; i < cbRole.getItemCount(); i++) {
                if (cbRole.getItemAt(i).equals(currentRole)) { cbRole.setSelectedIndex(i); break; }
            }
        }

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(new Color(0xF0EFF8));
        form.setBorder(BorderFactory.createEmptyBorder(18, 28, 18, 28));
        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.insets = new Insets(7, 6, 7, 6);
        Font labelFont = new Font("Arial", Font.BOLD, 13);

        JPanel passRow = new JPanel(new BorderLayout(4, 0));
        passRow.setOpaque(false);
        passRow.add(pfPass, BorderLayout.CENTER);
        passRow.add(btnShowPass, BorderLayout.EAST);

        Object[][] rows = {
            {"Mã nhân viên:", tfMa, "Chức vụ:", cbRole},
            {"Họ và tên *:", tfs[0], "Tên đăng nhập *:", tfs[1]},
            {"SĐT (10 số):", tfs[2], "Email:", tfs[3]},
            {"Lương (VNĐ):", tfs[4], "Ngày tham gia:", spNgay},
            {"Mật khẩu *:", passRow, null, null}
        };

        for (int i = 0; i < rows.length; i++) {
            gc.gridy = i;
            gc.gridx = 0; gc.weightx = 0;
            JLabel l0 = new JLabel((String) rows[i][0]);
            l0.setFont(labelFont);
            form.add(l0, gc);

            gc.gridx = 1; gc.weightx = 1;
            form.add((Component) rows[i][1], gc);

            if (rows[i][2] != null) {
                gc.gridx = 2; gc.weightx = 0;
                JLabel l1 = new JLabel((String) rows[i][2]);
                l1.setFont(labelFont);
                form.add(l1, gc);

                gc.gridx = 3; gc.weightx = 1;
                form.add((Component) rows[i][3], gc);
            }
        }

        dlg.add(form, BorderLayout.CENTER);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
        btns.setBackground(new Color(0xF0EFF8));
        btns.setBorder(BorderFactory.createMatteBorder(1,0,0,0, new Color(0xCCCCCC)));
        JButton btnLuu = new JButton("Lưu");
        btnLuu.setFont(new Font("Arial", Font.BOLD, 13));
        btnLuu.setBackground(ACCENT); btnLuu.setForeground(Color.WHITE);
        btnLuu.setBorder(BorderFactory.createEmptyBorder(9, 22, 9, 22));
        btnLuu.setOpaque(true); btnLuu.setBorderPainted(false); btnLuu.setFocusPainted(false);
        btnLuu.setCursor(new Cursor(Cursor.HAND_CURSOR));
        JButton btnHuy = new JButton("Hủy");
        btnHuy.setFont(new Font("Arial", Font.BOLD, 13));
        btnHuy.setBackground(new Color(0x9B8EA8)); btnHuy.setForeground(Color.WHITE);
        btnHuy.setBorder(BorderFactory.createEmptyBorder(9, 22, 9, 22));
        btnHuy.setOpaque(true); btnHuy.setBorderPainted(false); btnHuy.setFocusPainted(false);
        btnHuy.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btns.add(btnLuu); btns.add(btnHuy);
        dlg.add(btns, BorderLayout.SOUTH);

        btnLuu.addActionListener(e -> {
            String ten   = tfs[0].getText().trim();
            String user  = tfs[1].getText().trim();
            String sdt   = tfs[2].getText().trim();
            String email = tfs[3].getText().trim();
            String pass  = new String(pfPass.getPassword()).trim();
            String ngay  = new SimpleDateFormat("dd/MM/yyyy").format((Date) spNgay.getValue());

            if (ten.isEmpty()) {
                JOptionPane.showMessageDialog(dlg, "Vui lòng nhập họ và tên.", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
                tfs[0].requestFocus();
                return;
            }
            if (user.isEmpty()) {
                JOptionPane.showMessageDialog(dlg, "Vui lòng nhập tên đăng nhập.", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
                tfs[1].requestFocus();
                return;
            }
            if (!sdt.isEmpty() && !sdt.matches("^0\\d{9}$")) {
                JOptionPane.showMessageDialog(dlg, "SĐT phải 10 số và bắt đầu bằng 0.", "Dữ liệu không hợp lệ", JOptionPane.WARNING_MESSAGE);
                tfs[2].requestFocus();
                return;
            }
            if (!email.isEmpty() && !email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
                JOptionPane.showMessageDialog(dlg, "Email không hợp lệ.", "Dữ liệu không hợp lệ", JOptionPane.WARNING_MESSAGE);
                tfs[3].requestFocus();
                return;
            }

            boolean hasError = false;
            if (!isEdit || !pass.isEmpty()) {
                if (pass.isEmpty()) {
                    JOptionPane.showMessageDialog(dlg, "Vui lòng nhập mật khẩu.", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
                    pfPass.requestFocus();
                    hasError = true;
                } else if (pass.length() < 6) {
                    JOptionPane.showMessageDialog(dlg, "Mật khẩu tối thiểu 6 ký tự.", "Dữ liệu không hợp lệ", JOptionPane.WARNING_MESSAGE);
                    pfPass.requestFocus();
                    hasError = true;
                } else {
                    boolean hasLetter = pass.chars().anyMatch(Character::isLetter);
                    boolean hasDigit  = pass.chars().anyMatch(Character::isDigit);
                    if (!hasLetter || !hasDigit) {
                        JOptionPane.showMessageDialog(dlg, "Mật khẩu phải có cả chữ và số.", "Dữ liệu không hợp lệ", JOptionPane.WARNING_MESSAGE);
                        pfPass.requestFocus();
                        hasError = true;
                    }
                }
            }

            if (hasError) return;

            String ma = isEdit ? parent.tableModel.getValueAt(prefilledRow, COL_MA).toString() : parent.generateMaNV();

            if (tmpPhotoPath[0] != null && !tmpPhotoPath[0].isEmpty()) {
                try {
                    File src = new File(tmpPhotoPath[0]);
                    String ext = tmpPhotoPath[0].contains(".") ? tmpPhotoPath[0].substring(tmpPhotoPath[0].lastIndexOf('.')) : ".png";
                    File dest = new File("img/employees/" + ma + ext);
                    dest.getParentFile().mkdirs();
                    java.nio.file.Files.copy(src.toPath(), dest.toPath(),
                            java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                    parent.photoPathMap.put(ma, dest.getAbsolutePath());
                } catch (Exception ex) {
                    System.err.println("Photo copy failed: " + ex.getMessage());
                }
            }

            String finalPass = (pass.isEmpty() && isEdit)
                ? parent.tableModel.getValueAt(prefilledRow, COL_PASS).toString()
                : pass;

            if (isEdit) {
                parent.tableModel.setValueAt(ten,  editRow, COL_TEN);
                parent.tableModel.setValueAt(cbRole.getSelectedItem().toString(), editRow, COL_CHUCVU);
                parent.tableModel.setValueAt(sdt,  editRow, COL_SDT);
                parent.tableModel.setValueAt(email, editRow, COL_EMAIL);
                parent.tableModel.setValueAt(ngay, editRow, COL_NGAY);
                parent.tableModel.setValueAt(finalPass, editRow, COL_PASS);
                parent.fillDetail(editRow);
            } else {
                parent.tableModel.addRow(new Object[]{
                    ma, ten, cbRole.getSelectedItem().toString(), sdt, email, ngay, finalPass
                });
            }
            dlg.dispose();
        });

        btnHuy.addActionListener(e -> {
            if (isEdit) {
                int cf = JOptionPane.showConfirmDialog(dlg,
                        "Hủy sửa? Thay đổi chưa lưu sẽ mất.",
                        "Xác nhận", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (cf != JOptionPane.YES_OPTION) return;
            } else {
                boolean dirty = false;
                for (JTextField f : tfs) if (!f.getText().trim().isEmpty()) { dirty = true; break; }
                if (!dirty) dirty = new String(pfPass.getPassword()).trim().length() > 0;
                if (dirty) {
                    int cf = JOptionPane.showConfirmDialog(dlg,
                            "Bạn có chắc muốn hủy? Thông tin đã nhập sẽ mất.",
                            "Xác nhận hủy", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                    if (cf != JOptionPane.YES_OPTION) return;
                }
            }
            dlg.dispose();
        });

        dlg.pack();
        dlg.setMinimumSize(new Dimension(700, dlg.getPreferredSize().height));
        dlg.setLocationRelativeTo(parent);
        dlg.setVisible(true);
    }
}
