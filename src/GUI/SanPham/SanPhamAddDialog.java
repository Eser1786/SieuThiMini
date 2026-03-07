package GUI.SanPham;

import com.toedter.calendar.JDateChooser;
import java.text.SimpleDateFormat;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.Dialog;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import GUI.UIUtils;

/** Dialog add San Pham mới — tách từ SanPhamPanel.showThemPopup */
class SanPhamAddDialog {

    static void show(Component parent, DefaultTableModel model) {
        Window owner = SwingUtilities.getWindowAncestor(parent);
        JDialog popup = new JDialog(owner, "Thêm sản phẩm", Dialog.ModalityType.APPLICATION_MODAL);
        popup.setResizable(false);
        popup.setLayout(new BorderLayout());

        // ── Header ────────────────────────────────────────────────────────────
        JPanel hdr = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 14));
        hdr.setBackground(new Color(0xAF9FCB));
        JLabel hdrLbl = new JLabel("Thêm sản phẩm mới");
        hdrLbl.setFont(new Font("Arial", Font.BOLD, 18));
        hdrLbl.setForeground(Color.WHITE);
        hdr.add(hdrLbl);

        // ── Photo section (NhanVien style) ────────────────────────────────────
        final String[] tmpPhotoPath = {null};

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

        // ── Form (2-column GridBagLayout) ─────────────────────────────────────
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(new Color(0xF0EFF8));
        form.setBorder(BorderFactory.createEmptyBorder(18, 28, 18, 28));
        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.HORIZONTAL;
        g.insets = new Insets(7, 6, 7, 6);
        Font lf = new Font("Arial", Font.BOLD, 13);
        Dimension fd = new Dimension(200, 32);

        JTextField fMa     = UIUtils.makeField(); fMa.setPreferredSize(fd);
        fMa.setText(String.format("SP%03d", model.getRowCount() + 1));
        fMa.setEditable(false);
        fMa.setBackground(new Color(0xE8E6F0));
        fMa.setForeground(new Color(0x888888));
        JTextField fTen    = UIUtils.makeField(); fTen.setPreferredSize(fd);
        JTextField fMoTa   = UIUtils.makeField(); fMoTa.setPreferredSize(fd);
        JTextField fNCC    = UIUtils.makeField(); fNCC.setPreferredSize(fd);
        JTextField fDM     = UIUtils.makeField(); fDM.setPreferredSize(fd);
        JTextField fGiaVon = UIUtils.makeField(); fGiaVon.setPreferredSize(fd);
        JTextField fGiaBan = UIUtils.makeField(); fGiaBan.setPreferredSize(fd);
        JTextField fSL     = UIUtils.makeField(); fSL.setPreferredSize(fd);
        JTextField fTonMin = UIUtils.makeField(); fTonMin.setPreferredSize(fd);
        JTextField fXX     = UIUtils.makeField(); fXX.setPreferredSize(fd);
        JTextField fViTri  = UIUtils.makeField(); fViTri.setPreferredSize(fd);
        JTextField fDonVi  = UIUtils.makeField(); fDonVi.setPreferredSize(fd);
        JTextField fTT     = UIUtils.makeField(); fTT.setPreferredSize(fd);
        JTextField fKM     = UIUtils.makeField(); fKM.setPreferredSize(fd);

        JDateChooser dcNgaySX = new JDateChooser();
        dcNgaySX.setDateFormatString("dd/MM/yyyy");
        dcNgaySX.setPreferredSize(fd);
        JDateChooser dcNgayHH = new JDateChooser();
        dcNgayHH.setDateFormatString("dd/MM/yyyy");
        dcNgayHH.setPreferredSize(fd);

        Object[][] rows = {
            { "Mã SP:",              fMa,       "Tên sản phẩm:",      fTen    },
            { "Mô tả:",              fMoTa,     "Nhà cung cấp:",      fNCC    },
            { "Danh mục:",           fDM,       "Giá vốn (VNĐ):",     fGiaVon },
            { "Giá bán (VNĐ):",      fGiaBan,   "Số lượng:",           fSL     },
            { "Tồn kho tối thiểu:",  fTonMin,   "Xuất xứ:",            fXX     },
            { "Ngày sản xuất:",      dcNgaySX,  "Ngày hết hạn:",      dcNgayHH },
            { "Vị trí:",             fViTri,    "Đơn vị:",             fDonVi  },
            { "Trạng thái:",         fTT,       "Khuyến mãi:",         fKM     }
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

        JButton btnLuu = new JButton("Lưu");
        btnLuu.setFont(new Font("Arial", Font.BOLD, 13));
        btnLuu.setBackground(new Color(0x5C4A7F));
        btnLuu.setForeground(Color.WHITE);
        btnLuu.setBorder(BorderFactory.createEmptyBorder(9, 22, 9, 22));
        btnLuu.setOpaque(true); btnLuu.setBorderPainted(false); btnLuu.setFocusPainted(false);
        btnLuu.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JButton btnHuy = new JButton("Hủy");
        btnHuy.setFont(new Font("Arial", Font.BOLD, 13));
        btnHuy.setBackground(new Color(0x9B8EA8));
        btnHuy.setForeground(Color.WHITE);
        btnHuy.setBorder(BorderFactory.createEmptyBorder(9, 22, 9, 22));
        btnHuy.setOpaque(true); btnHuy.setBorderPainted(false); btnHuy.setFocusPainted(false);
        btnHuy.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnHuy.addActionListener(e -> popup.dispose());

        btnLuu.addActionListener(e -> {
            // ── Validation ────────────────────────────────────────────────────
            if (fTen.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(popup, "Tên sản phẩm không được để trống.", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
                fTen.requestFocus(); return;
            }
            if (fDM.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(popup, "Danh mục không được để trống.", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
                fDM.requestFocus(); return;
            }
            if (fNCC.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(popup, "Nhà cung cấp không được để trống.", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
                fNCC.requestFocus(); return;
            }
            double giaVon = 0, giaBan = 0;
            try { giaVon = Double.parseDouble(fGiaVon.getText().trim()); if (giaVon < 0) throw new NumberFormatException(); }
            catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(popup, "Giá vốn phải là số không âm.", "Dữ liệu không hợp lệ", JOptionPane.WARNING_MESSAGE);
                fGiaVon.requestFocus(); return;
            }
            try { giaBan = Double.parseDouble(fGiaBan.getText().trim()); if (giaBan < 0) throw new NumberFormatException(); }
            catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(popup, "Giá bán phải là số không âm.", "Dữ liệu không hợp lệ", JOptionPane.WARNING_MESSAGE);
                fGiaBan.requestFocus(); return;
            }
            if (giaBan < giaVon) {
                JOptionPane.showMessageDialog(popup, "Giá bán phải lớn hơn hoặc bằng giá vốn.", "Dữ liệu không hợp lệ", JOptionPane.WARNING_MESSAGE);
                fGiaBan.requestFocus(); return;
            }
            int sl = 0;
            try { sl = Integer.parseInt(fSL.getText().trim()); if (sl < 0) throw new NumberFormatException(); }
            catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(popup, "Số lượng phải là số nguyên không âm.", "Dữ liệu không hợp lệ", JOptionPane.WARNING_MESSAGE);
                fSL.requestFocus(); return;
            }
            if (!fTonMin.getText().trim().isEmpty()) {
                try { int t = Integer.parseInt(fTonMin.getText().trim()); if (t < 0) throw new NumberFormatException(); }
                catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(popup, "Tồn kho tối thiểu phải là số nguyên không âm.", "Dữ liệu không hợp lệ", JOptionPane.WARNING_MESSAGE);
                    fTonMin.requestFocus(); return;
                }
            }
            if (dcNgaySX.getDate() != null && dcNgayHH.getDate() != null
                    && !dcNgayHH.getDate().after(dcNgaySX.getDate())) {
                JOptionPane.showMessageDialog(popup, "Ngày hết hạn phải sau ngày sản xuất.", "Dữ liệu không hợp lệ", JOptionPane.WARNING_MESSAGE);
                return;
            }
            // ── Save ──────────────────────────────────────────────────────────
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            String ngaySX = dcNgaySX.getDate() != null ? sdf.format(dcNgaySX.getDate()) : "";
            String ngayHH = dcNgayHH.getDate() != null ? sdf.format(dcNgayHH.getDate()) : "";
            String kho = sl > 0 ? "Còn hàng" : "Hết hàng";
            model.addRow(new Object[]{
                fMa.getText(), tmpPhotoPath[0] != null ? tmpPhotoPath[0] : "", fTen.getText(), fGiaBan.getText(),
                sl, kho, ngayHH, fKM.getText().isEmpty() ? "-" : fKM.getText(), "",
                fMoTa.getText(), fNCC.getText(), fDM.getText(), fGiaVon.getText(),
                fTonMin.getText(), fXX.getText(), ngaySX,
                fViTri.getText(), fDonVi.getText(), fTT.getText()
            });
            popup.dispose();
        });
        footer.add(btnLuu);
        footer.add(btnHuy);
        popup.add(footer, BorderLayout.SOUTH);
        popup.pack();
        popup.setMinimumSize(new Dimension(700, popup.getPreferredSize().height));
        popup.setLocationRelativeTo(parent);
        popup.setVisible(true);
    }
}
