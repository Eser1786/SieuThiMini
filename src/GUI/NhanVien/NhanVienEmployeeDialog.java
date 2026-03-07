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
    private static final Color BTN_IDLE  = new Color(0xD9D9D9);
    private static final int COL_MA = 0, COL_TEN = 1, COL_CHUCVU = 2;
    private static final int COL_SDT = 3, COL_EMAIL = 4, COL_NGAY = 5, COL_PASS = 6;

    static void show(NhanVienPanel parent, Integer prefilledRow, int editRow) {
        boolean isEdit = (prefilledRow != null);
        String title = isEdit ? "Sửa Nhân Viên" : "Thêm Nhân Viên";

        JDialog dlg = new JDialog(SwingUtilities.getWindowAncestor(parent),
                title, Dialog.ModalityType.APPLICATION_MODAL);
        dlg.setLayout(new BorderLayout());

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

        JButton btnChonAnh = parent.makeAppBtn("Chọn ảnh");
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
        dlg.add(photoSection, BorderLayout.NORTH);

        // Form fields: [0]Ho ten, [1]Username, [2]SDT, [3]Email, [4]Luong
        JTextField[] tfs = new JTextField[5];
        String[] fieldLabels = { "Họ và tên *:", "Tên đăng nhập *:", "SĐT (10 số):", "Email:", "Lương (VNĐ):" };
        for (int i = 0; i < tfs.length; i++) {
            tfs[i] = new JTextField(18);
            tfs[i].setFont(new Font("Arial", Font.PLAIN, 13));
        }
        UIUtils.attachMoneyFormatter(tfs[4]);

        SpinnerDateModel dateModel = new SpinnerDateModel();
        JSpinner spNgay = new JSpinner(dateModel);
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(spNgay, "dd/MM/yyyy");
        spNgay.setEditor(dateEditor);
        spNgay.setFont(new Font("Arial", Font.PLAIN, 13));

        JPasswordField pfPass = new JPasswordField(18);
        pfPass.setFont(new Font("Arial", Font.PLAIN, 13));
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

        JLabel[] errLabels = new JLabel[5];
        for (int i = 0; i < errLabels.length; i++) {
            errLabels[i] = new JLabel(" ");
            errLabels[i].setFont(new Font("Arial", Font.ITALIC, 11));
            errLabels[i].setForeground(Color.RED);
        }

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
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createEmptyBorder(12, 20, 8, 20));
        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.HORIZONTAL;
        Font labelFont = new Font("Arial", Font.BOLD, 13);
        Font fieldFont = new Font("Arial", Font.PLAIN, 13);

        int row = 0;
        if (isEdit) {
            gc.gridx = 0; gc.gridy = row; gc.weightx = 0.35; gc.insets = new Insets(5,5,2,5);
            JLabel lMa = new JLabel("Mã nhân viên:"); lMa.setFont(labelFont);
            form.add(lMa, gc);
            gc.gridx = 1; gc.weightx = 0.65;
            JLabel lMaVal = new JLabel(parent.tableModel.getValueAt(prefilledRow, COL_MA).toString());
            lMaVal.setFont(new Font("Arial", Font.BOLD, 13));
            lMaVal.setForeground(ACCENT);
            form.add(lMaVal, gc);
            row++;
        }

        for (int i = 0; i < fieldLabels.length; i++) {
            gc.gridx = 0; gc.gridy = row; gc.weightx = 0.35; gc.insets = new Insets(5,5,2,5);
            JLabel lbl = new JLabel(fieldLabels[i]); lbl.setFont(labelFont);
            form.add(lbl, gc);
            gc.gridx = 1; gc.weightx = 0.65;
            tfs[i].setFont(fieldFont);
            form.add(tfs[i], gc);
            row++;
            gc.gridx = 1; gc.gridy = row; gc.insets = new Insets(0,5,4,5);
            form.add(errLabels[i], gc);
            row++;
        }

        gc.gridx = 0; gc.gridy = row; gc.weightx = 0.35; gc.insets = new Insets(5,5,2,5);
        JLabel lDate = new JLabel("Ngày tham gia:"); lDate.setFont(labelFont);
        form.add(lDate, gc);
        gc.gridx = 1; gc.weightx = 0.65;
        form.add(spNgay, gc);
        row++;

        gc.gridx = 0; gc.gridy = row; gc.weightx = 0.35; gc.insets = new Insets(5,5,2,5);
        JLabel lPass = new JLabel("Mật khẩu *:"); lPass.setFont(labelFont);
        form.add(lPass, gc);
        gc.gridx = 1; gc.weightx = 0.65;
        JPanel passRow = new JPanel(new BorderLayout(4, 0));
        passRow.setOpaque(false);
        passRow.add(pfPass, BorderLayout.CENTER);
        passRow.add(btnShowPass, BorderLayout.EAST);
        form.add(passRow, gc);
        row++;
        gc.gridx = 1; gc.gridy = row; gc.insets = new Insets(0,5,4,5);
        form.add(errLabels[4], gc);
        row++;

        gc.gridx = 0; gc.gridy = row; gc.weightx = 0.35; gc.insets = new Insets(5,5,5,5);
        JLabel lRole = new JLabel("Chức vụ:"); lRole.setFont(labelFont);
        form.add(lRole, gc);
        gc.gridx = 1; gc.weightx = 0.65;
        form.add(cbRole, gc);

        JScrollPane formScroll = new JScrollPane(form);
        formScroll.setBorder(null);
        formScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        dlg.add(formScroll, BorderLayout.CENTER);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
        btns.setBackground(new Color(0xF3F0FA));
        btns.setBorder(BorderFactory.createMatteBorder(1,0,0,0, new Color(0xD1C4E9)));
        JButton btnLuu = new JButton("Lưu");
        btnLuu.setBackground(ACCENT); btnLuu.setForeground(Color.WHITE);
        btnLuu.setFont(new Font("Arial", Font.BOLD, 13));
        btnLuu.setFocusPainted(false); btnLuu.setBorderPainted(false);
        btnLuu.setPreferredSize(new Dimension(80, 32));
        JButton btnHuy = new JButton("Hủy");
        btnHuy.setFont(new Font("Arial", Font.BOLD, 13));
        btnHuy.setBackground(BTN_IDLE); btnHuy.setForeground(Color.DARK_GRAY);
        btnHuy.setFocusPainted(false); btnHuy.setBorderPainted(false);
        btnHuy.setPreferredSize(new Dimension(80, 32));
        btns.add(btnHuy); btns.add(btnLuu);
        dlg.add(btns, BorderLayout.SOUTH);

        btnLuu.addActionListener(e -> {
            for (JLabel el : errLabels) el.setText(" ");

            String ten   = tfs[0].getText().trim();
            String user  = tfs[1].getText().trim();
            String sdt   = tfs[2].getText().trim();
            String email = tfs[3].getText().trim();
            String pass  = new String(pfPass.getPassword()).trim();
            String ngay  = new SimpleDateFormat("dd/MM/yyyy").format((Date) spNgay.getValue());

            boolean hasError = false;
            if (ten.isEmpty())  { errLabels[0].setText("Vui lòng nhập họ và tên"); hasError = true; }
            if (user.isEmpty()) { errLabels[1].setText("Vui lòng nhập tên đăng nhập"); hasError = true; }
            if (!sdt.isEmpty() && !sdt.matches("^0\\d{9}$"))
                { errLabels[2].setText("SĐT phải 10 số, bắt đầu 0"); hasError = true; }
            if (!email.isEmpty() && !email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$"))
                { errLabels[3].setText("Email không hợp lệ"); hasError = true; }

            if (!isEdit || !pass.isEmpty()) {
                if (pass.isEmpty()) {
                    errLabels[4].setText("Vui lòng nhập mật khẩu"); hasError = true;
                } else if (pass.length() < 6) {
                    errLabels[4].setText("Tối thiểu 6 ký tự"); hasError = true;
                } else {
                    boolean hasLetter = pass.chars().anyMatch(Character::isLetter);
                    boolean hasDigit  = pass.chars().anyMatch(Character::isDigit);
                    if (!hasLetter || !hasDigit) { errLabels[4].setText("Phải có cả chữ và số"); hasError = true; }
                }
            }

            if (hasError) { dlg.revalidate(); dlg.repaint(); return; }

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
        dlg.setMinimumSize(new Dimension(500, dlg.getPreferredSize().height));
        dlg.setLocationRelativeTo(parent);
        dlg.setVisible(true);
    }
}
