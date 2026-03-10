package GUI.SanPham;

import BUS.CategoryBUS;
import BUS.SupplierBUS;
import DTO.CategoryDTO;
import DTO.SupplierDTO;
import GUI.UIUtils;
import com.toedter.calendar.JDateChooser;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/** Dialog chi tiết + sửa San Pham — tách từ SanPhamPanel */
class SanPhamDetailDialog {

    static void showDetail(Component parent, int modelRow, DefaultTableModel model, JTable bang) {
        Window owner = SwingUtilities.getWindowAncestor(parent);
        JDialog detail = new JDialog(owner, "Chi tiết sản phẩm", Dialog.ModalityType.APPLICATION_MODAL);
        detail.setSize(520, 640);
        detail.setLocationRelativeTo(parent);
        detail.setResizable(false);
        detail.getContentPane().setBackground(new Color(0xF0EFF8));
        detail.setLayout(new BorderLayout(0, 0));

        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 14));
        header.setBackground(new Color(0xAF9FCB));
        JLabel lblTitle = new JLabel("Thông tin sản phẩm");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 20)); lblTitle.setForeground(Color.WHITE);
        header.add(lblTitle);
        detail.add(header, BorderLayout.NORTH);

        String[] lbls = {
            "Mã SP", "Tên sản phẩm", "Mô tả", "Nhà cung cấp", "Danh mục",
            "Giá vốn", "Giá bán", "Số lượng", "Tồn kho tối thiểu",
            "Xuất xứ", "Ngày sản xuất", "Ngày hết hạn",
            "Vị trí", "Đơn vị", "Trạng thái"
        };
        int[] colIdx = { 0, 2, 9, 10, 11, 12, 3, 4, 13, 14, 15, 6, 16, 17, 18 };

        JPanel body = new JPanel(new GridLayout(lbls.length, 2, 12, 10));
        body.setBackground(new Color(0xF0EFF8));
        body.setBorder(BorderFactory.createEmptyBorder(16, 36, 16, 36));
        for (int i = 0; i < lbls.length; i++) {
            JLabel lbl = new JLabel(lbls[i] + ":");
            lbl.setFont(new Font("Arial", Font.BOLD, 15));
            Object v = model.getValueAt(modelRow, colIdx[i]);
            JLabel val = new JLabel(v == null ? "-" : v.toString());
            val.setFont(new Font("Arial", Font.PLAIN, 15));
            body.add(lbl); body.add(val);
        }
        JScrollPane scrollDetail = new JScrollPane(body);
        scrollDetail.setBorder(null);
        scrollDetail.getVerticalScrollBar().setUnitIncrement(16);
        detail.add(scrollDetail, BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 14));
        footer.setBackground(new Color(0xF0EFF8));
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(0xCCCCCC)));

        JButton btnSua = new JButton("Sửa");
        styleBtn(btnSua, new Color(0x6677C8), 110, 40);
        btnSua.addActionListener(e -> { detail.dispose(); showEdit(parent, modelRow, model, bang); });

        JButton btnXoa = new JButton("Xóa");
        styleBtn(btnXoa, new Color(0xB83434), 110, 40);
        btnXoa.addActionListener(e -> {
            int c1 = JOptionPane.showConfirmDialog(detail,
                "Bạn có chắc muốn xóa sản phẩm \"" + model.getValueAt(modelRow, 2) + "\"?",
                "Xác nhận xóa", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (c1 != JOptionPane.YES_OPTION) return;
            int c2 = JOptionPane.showConfirmDialog(detail,
                "Xác nhận lần cuối: Sản phẩm sẽ bị ẩn vĩnh viễn. Tiếp tục?",
                "Xác nhận lần 2", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (c2 != JOptionPane.YES_OPTION) return;
            Object idObj = model.getValueAt(modelRow, 19);
            if (idObj != null) {
                new BUS.ProductBUS().softDeleteProduct((Integer) idObj);
            }
            detail.dispose();
            if (parent instanceof SanPhamPanel) {
                ((SanPhamPanel) parent).loadProducts();
            }
        });

        JButton btnDong = new JButton("Đóng");
        styleBtn(btnDong, new Color(0x9B8EA8), 110, 40);
        btnDong.addActionListener(e -> detail.dispose());

        footer.add(btnSua); footer.add(btnXoa); footer.add(btnDong);
        detail.add(footer, BorderLayout.SOUTH);
        detail.setVisible(true);
    }

    static void showEdit(Component parent, int modelRow, DefaultTableModel model, JTable bang) {
        Window owner = SwingUtilities.getWindowAncestor(parent);
        JDialog popup = new JDialog(owner, "Sửa sản phẩm", Dialog.ModalityType.APPLICATION_MODAL);
        popup.setResizable(false);
        popup.setLayout(new BorderLayout());

        // ── Header ────────────────────────────────────────────────────────────
        JPanel hdr = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 14));
        hdr.setBackground(new Color(0xAF9FCB));
        JLabel hdrLbl = new JLabel("Chỉnh sửa thông tin");
        hdrLbl.setFont(new Font("Arial", Font.BOLD, 18));
        hdrLbl.setForeground(Color.WHITE);
        hdr.add(hdrLbl);

        // ── Photo section ────────────────────────────────────────────────────
        final String[] tmpPhotoPath = { model.getValueAt(modelRow, 1) != null ? model.getValueAt(modelRow, 1).toString() : null };

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

        // Load existing photo
        if (tmpPhotoPath[0] != null && !tmpPhotoPath[0].isEmpty()) {
            try {
                BufferedImage img = ImageIO.read(new File(tmpPhotoPath[0]));
                if (img != null) photoPreview.setIcon(new ImageIcon(img.getScaledInstance(80, 80, Image.SCALE_SMOOTH)));
            } catch (Exception ignored) {}
        }

        JButton btnAnh = new JButton("Chọn ảnh");
        btnAnh.setFont(new Font("Arial", Font.BOLD, 13));
        btnAnh.setBackground(new Color(0xD9D9D9));
        btnAnh.setForeground(new Color(0x333333));
        btnAnh.setFocusPainted(false);
        btnAnh.setBorderPainted(false);
        btnAnh.setOpaque(true);
        btnAnh.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAnh.addActionListener(ev -> {
            java.awt.FileDialog fileDlg = new java.awt.FileDialog(popup, "Chọn ảnh sản phẩm", java.awt.FileDialog.LOAD);
            fileDlg.setFilenameFilter((dir, name) -> {
                String n = name.toLowerCase();
                return n.endsWith(".jpg") || n.endsWith(".jpeg") || n.endsWith(".png");
            });
            fileDlg.setVisible(true);
            String chosenDir  = fileDlg.getDirectory();
            String chosenFile = fileDlg.getFile();
            if (chosenDir == null || chosenFile == null) return;
            try {
                File f = new File(chosenDir, chosenFile);
                BufferedImage img = ImageIO.read(f);
                if (img != null) {
                    tmpPhotoPath[0] = f.getAbsolutePath();
                    photoPreview.setIcon(new ImageIcon(img.getScaledInstance(80, 80, Image.SCALE_SMOOTH)));
                    photoPreview.repaint();
                }
            } catch (Exception ex) { /* ignore */ }
        });

        JLabel lbPhotoHint = new JLabel("<html><font color='gray' size='2'>JPG / PNG<br>tự động scale</font></html>");
        JPanel photoSection = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 8));
        photoSection.setBackground(new Color(0xF3F0FA));
        photoSection.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0xD1C4E9)));
        JPanel photoRight = new JPanel(new GridLayout(2, 1, 0, 4));
        photoRight.setOpaque(false);
        photoRight.add(btnAnh);
        photoRight.add(lbPhotoHint);
        photoSection.add(photoPreview);
        photoSection.add(photoRight);

        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.add(hdr, BorderLayout.NORTH);
        northPanel.add(photoSection, BorderLayout.SOUTH);
        popup.add(northPanel, BorderLayout.NORTH);

        // ── Form (2-column GridBagLayout, matching SanPhamAddDialog) ─────────
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(new Color(0xF0EFF8));
        form.setBorder(BorderFactory.createEmptyBorder(18, 28, 18, 28));
        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.HORIZONTAL;
        g.insets = new Insets(7, 6, 7, 6);
        Font lf = new Font("Arial", Font.BOLD, 13);
        Dimension fd = new Dimension(200, 32);

        // ── Fields ───────────────────────────────────────────────────────────
        String getMa  = val(model, modelRow, 0);
        String getTen = val(model, modelRow, 2);
        String getMoTa = val(model, modelRow, 9);
        String getNCC  = val(model, modelRow, 10);
        String getDM   = val(model, modelRow, 11);
        String getGiaVon = val(model, modelRow, 12);
        String getGiaBan = val(model, modelRow, 3);
        String getSL     = val(model, modelRow, 4);
        String getTonMin = val(model, modelRow, 13);
        String getXX     = val(model, modelRow, 14);
        String getNgaySX = val(model, modelRow, 15);
        String getNgayHH = val(model, modelRow, 6);
        String getViTri  = val(model, modelRow, 16);
        String getDonVi  = val(model, modelRow, 17);
        String getTT     = val(model, modelRow, 18);

        JTextField fMa = UIUtils.makeField(); fMa.setPreferredSize(fd);
        fMa.setText(getMa);
        fMa.setEditable(false);
        fMa.setBackground(new Color(0xE8E6F0));
        fMa.setForeground(new Color(0x888888));

        JTextField fTen    = UIUtils.makeField(); fTen.setPreferredSize(fd); fTen.setText(getTen);
        JTextField fMoTa   = UIUtils.makeField(); fMoTa.setPreferredSize(fd); fMoTa.setText(getMoTa);
        JTextField fGiaVon = UIUtils.makeField(); fGiaVon.setPreferredSize(fd); fGiaVon.setText(getGiaVon);
        JTextField fGiaBan = UIUtils.makeField(); fGiaBan.setPreferredSize(fd); fGiaBan.setText(getGiaBan);
        JTextField fSL     = UIUtils.makeField(); fSL.setPreferredSize(fd); fSL.setText(getSL);
        fSL.setEditable(false);
        fSL.setBackground(new Color(0xE8E6F0));
        fSL.setForeground(new Color(0x888888));
        JTextField fTonMin = UIUtils.makeField(); fTonMin.setPreferredSize(fd); fTonMin.setText(getTonMin);
        JTextField fXX     = UIUtils.makeField(); fXX.setPreferredSize(fd); fXX.setText(getXX);
        JTextField fViTri  = UIUtils.makeField(); fViTri.setPreferredSize(fd); fViTri.setText(getViTri);
        JTextField fDonVi  = UIUtils.makeField(); fDonVi.setPreferredSize(fd); fDonVi.setText(getDonVi);

        JComboBox<String> cbNCC = new JComboBox<>();
        cbNCC.setPreferredSize(fd);
        UIUtils.styleComboBox(cbNCC);
        try {
            List<SupplierDTO> suppliers = new SupplierBUS().getAllSuppliers();
            cbNCC.addItem("");
            for (SupplierDTO s : suppliers) cbNCC.addItem(s.getName());
            cbNCC.setSelectedItem(getNCC);
        } catch (Exception ignored) { cbNCC.addItem(getNCC); cbNCC.setSelectedItem(getNCC); }

        JComboBox<String> cbDM = new JComboBox<>();
        cbDM.setPreferredSize(fd);
        UIUtils.styleComboBox(cbDM);
        try {
            List<CategoryDTO> categories = new CategoryBUS().getAllCategories();
            cbDM.addItem("");
            for (CategoryDTO c : categories) cbDM.addItem(c.getName());
            cbDM.setSelectedItem(getDM);
        } catch (Exception ignored) { cbDM.addItem(getDM); cbDM.setSelectedItem(getDM); }

        JComboBox<String> cbTT = new JComboBox<>(new String[]{"ACTIVE", "INACTIVE", "DISCONTINUED"});
        cbTT.setPreferredSize(fd);
        UIUtils.styleComboBox(cbTT);
        cbTT.setSelectedItem(getTT.isEmpty() ? "ACTIVE" : getTT);

        JDateChooser dcNgaySX = new JDateChooser();
        dcNgaySX.setDateFormatString("dd/MM/yyyy");
        dcNgaySX.setPreferredSize(fd);
        JDateChooser dcNgayHH = new JDateChooser();
        dcNgayHH.setDateFormatString("dd/MM/yyyy");
        dcNgayHH.setPreferredSize(fd);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        try { if (!getNgaySX.isEmpty()) dcNgaySX.setDate(sdf.parse(getNgaySX)); } catch (Exception ignored) {}
        try { if (!getNgayHH.isEmpty()) dcNgayHH.setDate(sdf.parse(getNgayHH)); } catch (Exception ignored) {}

        Object[][] rows = {
            { "Mã SP:",              fMa,       "Tên sản phẩm:",      fTen     },
            { "Mô tả:",              fMoTa,     "Nhà cung cấp:",      cbNCC    },
            { "Danh mục:",           cbDM,      "Giá vốn (VNĐ):",     fGiaVon  },
            { "Giá bán (VNĐ):",      fGiaBan,   "Số lượng:",           fSL      },
            { "Tồn kho tối thiểu:",  fTonMin,   "Xuất xứ:",            fXX      },
            { "Ngày sản xuất:",      dcNgaySX,  "Ngày hết hạn:",      dcNgayHH },
            { "Vị trí:",             fViTri,    "Đơn vị:",             fDonVi   },
            { "Trạng thái:",         cbTT,      null,                  null     }
        };
        for (int i = 0; i < rows.length; i++) {
            g.gridy = i;
            g.gridx = 0; g.weightx = 0;
            JLabel l0 = new JLabel((String) rows[i][0]); l0.setFont(lf);
            form.add(l0, g);
            g.gridx = 1; g.weightx = 1;
            form.add((Component) rows[i][1], g);
            if (rows[i][2] != null) {
                g.gridx = 2; g.weightx = 0;
                JLabel l1 = new JLabel((String) rows[i][2]); l1.setFont(lf);
                form.add(l1, g);
                g.gridx = 3; g.weightx = 1;
                form.add((Component) rows[i][3], g);
            }
        }
        popup.add(form, BorderLayout.CENTER);

        // ── Footer ────────────────────────────────────────────────────────────
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 12));
        footer.setBackground(new Color(0xF0EFF8));
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(0xCCCCCC)));

        JButton btnHuy = new JButton("Hủy");
        styleBtn(btnHuy, new Color(0x9B8EA8), 100, 40);
        btnHuy.addActionListener(e -> {
            int cf = JOptionPane.showConfirmDialog(popup,
                    "Bạn có chắc muốn hủy? Thay đổi chưa lưu sẽ bị mất.",
                    "Xác nhận hủy", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (cf == JOptionPane.YES_OPTION) popup.dispose();
        });

        JButton btnLuu = new JButton("Lưu");
        styleBtn(btnLuu, new Color(0x5C4A7F), 100, 40);
        btnLuu.addActionListener(e -> {
            String ten = fTen.getText().trim();
            if (ten.isEmpty()) {
                JOptionPane.showMessageDialog(popup, "Tên sản phẩm không được để trống.", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
                fTen.requestFocus(); return;
            }
            String giaVonStr = fGiaVon.getText().trim();
            String giaBanStr = fGiaBan.getText().trim();
            double giaVon = 0, giaBan = 0;
            try { giaVon = Double.parseDouble(giaVonStr); } catch (NumberFormatException ignored) {}
            try { giaBan = Double.parseDouble(giaBanStr); } catch (NumberFormatException ignored) {}
            if (!giaVonStr.isEmpty() && !giaBanStr.isEmpty() && giaBan <= giaVon) {
                JOptionPane.showMessageDialog(popup, "Giá bán phải lớn hơn giá vốn!", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
                fGiaBan.requestFocus(); return;
            }
            if (dcNgaySX.getDate() != null && dcNgayHH.getDate() != null
                    && !dcNgayHH.getDate().after(dcNgaySX.getDate())) {
                JOptionPane.showMessageDialog(popup, "Ngày hết hạn phải sau ngày sản xuất.", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String ngaySX = dcNgaySX.getDate() != null ? sdf.format(dcNgaySX.getDate()) : "";
            String ngayHH = dcNgayHH.getDate() != null ? sdf.format(dcNgayHH.getDate()) : "";

            // Copy new photo to img/products if changed
            if (tmpPhotoPath[0] != null && !tmpPhotoPath[0].isEmpty()) {
                String existingPath = model.getValueAt(modelRow, 1) != null ? model.getValueAt(modelRow, 1).toString() : "";
                if (!tmpPhotoPath[0].equals(existingPath)) {
                    try {
                        File src = new File(tmpPhotoPath[0]);
                        String ext = tmpPhotoPath[0].contains(".") ? tmpPhotoPath[0].substring(tmpPhotoPath[0].lastIndexOf('.')) : ".png";
                        String maSP = getMa;
                        File dest = new File("img/products/" + maSP + ext);
                        dest.getParentFile().mkdirs();
                        java.nio.file.Files.copy(src.toPath(), dest.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                        tmpPhotoPath[0] = dest.getAbsolutePath();
                    } catch (Exception ex) {
                        System.err.println("Photo copy failed: " + ex.getMessage());
                    }
                }
            }

            model.setValueAt(fTen.getText(), modelRow, 2);
            model.setValueAt(fMoTa.getText(), modelRow, 9);
            model.setValueAt(cbNCC.getSelectedItem() != null ? cbNCC.getSelectedItem().toString() : "", modelRow, 10);
            model.setValueAt(cbDM.getSelectedItem() != null ? cbDM.getSelectedItem().toString() : "", modelRow, 11);
            model.setValueAt(fGiaVon.getText(), modelRow, 12);
            model.setValueAt(fGiaBan.getText(), modelRow, 3);
            model.setValueAt(fTonMin.getText(), modelRow, 13);
            model.setValueAt(fXX.getText(), modelRow, 14);
            model.setValueAt(ngaySX, modelRow, 15);
            model.setValueAt(ngayHH, modelRow, 6);
            model.setValueAt(fViTri.getText(), modelRow, 16);
            model.setValueAt(fDonVi.getText(), modelRow, 17);
            model.setValueAt(cbTT.getSelectedItem() != null ? cbTT.getSelectedItem().toString() : "ACTIVE", modelRow, 18);
            if (tmpPhotoPath[0] != null && !tmpPhotoPath[0].isEmpty()) {
                model.setValueAt(tmpPhotoPath[0], modelRow, 1);
            }
            bang.repaint();
            popup.dispose();
        });

        footer.add(btnHuy); footer.add(btnLuu);
        popup.add(footer, BorderLayout.SOUTH);
        popup.pack();
        popup.setMinimumSize(new Dimension(700, popup.getPreferredSize().height));
        popup.setLocationRelativeTo(parent);
        popup.setVisible(true);
    }

    private static String val(DefaultTableModel model, int row, int col) {
        Object v = model.getValueAt(row, col);
        return v == null ? "" : v.toString();
    }

    private static void styleBtn(JButton b, Color bg, int w, int h) {
        b.setFont(new Font("Arial", Font.BOLD, 15));
        b.setBackground(bg); b.setForeground(Color.WHITE);
        b.setFocusPainted(false); b.setBorderPainted(false);
        b.setPreferredSize(new Dimension(w, h));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
}
