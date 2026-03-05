package GUI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.print.*;
import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * Tiện ích xuất dữ liệu bảng ra file CSV (mở được bằng Excel)
 * và xuất PDF bằng Java Print API thuần (không cần thư viện ngoài).
 */
public class ExportUtils {

    /*
     * ─────────────────────────────────────────────
     * XUẤT CSV (Excel đọc được)
     * ─────────────────────────────────────────────
     */
    /**
     * Mở hộp thoại chọn nơi lưu, rồi ghi toàn bộ bảng ra file .csv
     * với BOM UTF-8 để Excel hiển thị tiếng Việt đúng.
     *
     * @param parent  component cha để dialog hiện đúng vị trí
     * @param model   model của JTable cần xuất
     * @param tenFile tên file mặc định (không cần đuôi)
     */
    public static void xuatCSV(Component parent, DefaultTableModel model, String tenFile) {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Lưu file Excel (CSV)");
        fc.setSelectedFile(new File(tenFile + ".csv"));
        fc.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("CSV Files (*.csv)", "csv"));

        if (fc.showSaveDialog(parent) != JFileChooser.APPROVE_OPTION)
            return;

        File file = fc.getSelectedFile();
        if (!file.getName().toLowerCase().endsWith(".csv"))
            file = new File(file.getAbsolutePath() + ".csv");

        try (BufferedWriter bw = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {

            // BOM UTF-8 để Excel đọc đúng tiếng Việt
            bw.write('\uFEFF');

            // Ghi header
            int cols = model.getColumnCount();
            StringBuilder sb = new StringBuilder();
            for (int c = 0; c < cols; c++) {
                // Bỏ cột "Thao tác" ở cuối nếu có
                if (model.getColumnName(c).equalsIgnoreCase("Thao tác"))
                    continue;
                if (sb.length() > 0)
                    sb.append(',');
                sb.append('"').append(model.getColumnName(c).replace("\"", "\"\"")).append('"');
            }
            bw.write(sb.toString());
            bw.newLine();

            // Ghi dữ liệu
            for (int r = 0; r < model.getRowCount(); r++) {
                sb.setLength(0);
                boolean dau = true;
                for (int c = 0; c < cols; c++) {
                    if (model.getColumnName(c).equalsIgnoreCase("Thao tác"))
                        continue;
                    if (!dau)
                        sb.append(',');
                    dau = false;
                    Object val = model.getValueAt(r, c);
                    String cell = val == null ? "" : val.toString();
                    sb.append('"').append(cell.replace("\"", "\"\"")).append('"');
                }
                bw.write(sb.toString());
                bw.newLine();
            }

            JOptionPane.showMessageDialog(parent,
                    "Xuất file thành công!\n" + file.getAbsolutePath(),
                    "Xuất Excel (CSV)", JOptionPane.INFORMATION_MESSAGE);

        } catch (IOException ex) {
            JOptionPane.showMessageDialog(parent,
                    "Lỗi khi xuất file: " + ex.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    /*
     * ─────────────────────────────────────────────
     * XUẤT PDF (dùng Java Print API + Graphics2D)
     * ─────────────────────────────────────────────
     */
    /**
     * Render nội dung bảng ra PDF bằng Java Print API.
     * Hiển thị print dialog để người dùng chọn máy in / xuất PDF.
     *
     * @param parent component cha
     * @param model  model của JTable
     * @param tieuDe tiêu đề in trên đầu trang
     */
    public static void xuatPDF(Component parent, DefaultTableModel model, String tieuDe) {
        PrinterJob job = PrinterJob.getPrinterJob();

        // Thiết lập trang nằm ngang (landscape) nếu nhiều cột
        PageFormat pf = job.defaultPage();
        Paper paper = pf.getPaper();
        if (model.getColumnCount() > 6) {
            pf.setOrientation(PageFormat.LANDSCAPE);
            paper.setImageableArea(20, 20, paper.getWidth() - 40, paper.getHeight() - 40);
        } else {
            pf.setOrientation(PageFormat.PORTRAIT);
            paper.setImageableArea(20, 20, paper.getWidth() - 40, paper.getHeight() - 40);
        }
        pf.setPaper(paper);

        job.setPrintable(new TablePrintable(model, tieuDe), pf);
        job.setJobName(tieuDe);

        if (job.printDialog()) {
            try {
                job.print();
            } catch (PrinterException ex) {
                JOptionPane.showMessageDialog(parent,
                        "Lỗi khi in/xuất PDF: " + ex.getMessage(),
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /*
     * ─────────────────────────────────────────────
     * Printable nội bộ — vẽ bảng lên Graphics2D
     * ─────────────────────────────────────────────
     */
    private static class TablePrintable implements Printable {
        private final DefaultTableModel model;
        private final String tieuDe;

        // Phân trang
        private int[] pageStartRows; // dòng bắt đầu của mỗi trang
        private boolean initialized = false;

        private static final int ROW_H = 22; // chiều cao mỗi dòng (px)
        private static final int HEADER_H = 30; // chiều cao header bảng
        private static final int TITLE_H = 40; // chiều cao tiêu đề trang

        TablePrintable(DefaultTableModel model, String tieuDe) {
            this.model = model;
            this.tieuDe = tieuDe;
        }

        @Override
        public int print(Graphics graphics, PageFormat pageFormat, int pageIndex)
                throws PrinterException {

            Graphics2D g2 = (Graphics2D) graphics;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            double iw = pageFormat.getImageableWidth();
            double ih = pageFormat.getImageableHeight();
            g2.translate(pageFormat.getImageableX(), pageFormat.getImageableY());

            // Khởi tạo phân trang lần đầu
            if (!initialized) {
                int rowsPerPage = (int) ((ih - TITLE_H - HEADER_H) / ROW_H);
                int totalRows = model.getRowCount();
                int pages = (int) Math.ceil((double) totalRows / rowsPerPage);
                pageStartRows = new int[pages];
                for (int i = 0; i < pages; i++)
                    pageStartRows[i] = i * rowsPerPage;
                initialized = true;
            }

            if (pageIndex >= pageStartRows.length)
                return NO_SUCH_PAGE;

            int cols = model.getColumnCount();
            // Lọc bỏ cột "Thao tác"
            java.util.List<Integer> visibleCols = new java.util.ArrayList<>();
            for (int c = 0; c < cols; c++)
                if (!model.getColumnName(c).equalsIgnoreCase("Thao tác"))
                    visibleCols.add(c);

            int vcnt = visibleCols.size();
            double cw = iw / vcnt; // chiều rộng mỗi cột (đều nhau)

            // ── Tiêu đề trang ──
            g2.setFont(new Font("Arial", Font.BOLD, 16));
            g2.setColor(new Color(0x2F2C35));
            FontMetrics fm = g2.getFontMetrics();
            int tx = (int) (iw / 2 - fm.stringWidth(tieuDe) / 2);
            g2.drawString(tieuDe, tx, 24);

            // Số trang
            g2.setFont(new Font("Arial", Font.PLAIN, 10));
            String pg = "Trang " + (pageIndex + 1) + "/" + pageStartRows.length;
            g2.drawString(pg, (int) (iw - g2.getFontMetrics().stringWidth(pg)), 24);

            // ── Header bảng ──
            int y = TITLE_H;
            g2.setColor(new Color(0xAF9FCB));
            g2.fillRect(0, y, (int) iw, HEADER_H);
            g2.setFont(new Font("Arial", Font.BOLD, 11));
            g2.setColor(Color.WHITE);
            for (int i = 0; i < vcnt; i++) {
                int ci = visibleCols.get(i);
                String hdr = model.getColumnName(ci);
                int cx = (int) (i * cw + cw / 2 - g2.getFontMetrics().stringWidth(hdr) / 2);
                g2.drawString(hdr, cx, y + 20);
            }
            y += HEADER_H;

            // ── Dữ liệu ──
            g2.setFont(new Font("Arial", Font.PLAIN, 10));
            int startRow = pageStartRows[pageIndex];
            int rowsPerPage = (int) ((ih - TITLE_H - HEADER_H) / ROW_H);
            int endRow = Math.min(startRow + rowsPerPage, model.getRowCount());

            for (int r = startRow; r < endRow; r++) {
                // Màu xen kẽ
                g2.setColor(r % 2 == 0 ? Color.WHITE : new Color(0xF3F0FA));
                g2.fillRect(0, y, (int) iw, ROW_H);

                // Đường kẻ ngang
                g2.setColor(new Color(0xDDDDDD));
                g2.drawLine(0, y + ROW_H - 1, (int) iw, y + ROW_H - 1);

                g2.setColor(Color.BLACK);
                FontMetrics rowFM = g2.getFontMetrics();
                for (int i = 0; i < vcnt; i++) {
                    int ci = visibleCols.get(i);
                    Object val = model.getValueAt(r, ci);
                    String cell = val == null ? "" : val.toString();
                    // Cắt ngắn nếu quá dài
                    while (cell.length() > 2 && rowFM.stringWidth(cell) > cw - 6)
                        cell = cell.substring(0, cell.length() - 1);
                    int cx = (int) (i * cw + 4);
                    g2.drawString(cell, cx, y + 15);
                }
                y += ROW_H;
            }

            // Viền ngoài bảng
            g2.setColor(new Color(0xAAAAAA));
            g2.drawRect(0, TITLE_H, (int) iw - 1, (HEADER_H + (endRow - startRow) * ROW_H));

            return PAGE_EXISTS;
        }
    }

    /*
     * ─────────────────────────────────────────────
     * HELPER — tạo nút xuất chuẩn style app
     * ─────────────────────────────────────────────
     */
    public static JButton makeExportButton(String label, Color bg) {
        // Tẩy emoji nếu có vì font mặc định Java trên Windows thường lỗi ô vuông
        String cleanLabel = label.replace("📄", "").replace("📊", "").trim();
        JButton btn = new JButton(cleanLabel);
        btn.setFont(new Font("Arial", Font.BOLD, 16));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setPreferredSize(new Dimension(160, 42));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(bg.darker());
            }

            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(bg);
            }
        });
        return btn;
    }
}
