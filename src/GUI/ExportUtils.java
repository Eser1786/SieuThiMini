package GUI;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.print.*;
import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * Tiện ích xuất dữ liệu bảng ra file CSV (mở được bằng Excel)
 * và xuất PDF trực tiếp ra file.
 */
public class ExportUtils {

    /*
     * ─────────────────────────────────────────────
     * Helper: Tạo JFileChooser với System Look And Feel
     * ─────────────────────────────────────────────
     */
    private static JFileChooser getSystemFileChooser(String title, String extDesc, String ext) {
        // Lưu lại L&F hiện tại
        LookAndFeel oldLaf = UIManager.getLookAndFeel();
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle(title);
        fc.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(extDesc, ext));

        // Khôi phục L&F cũ để không ảnh hưởng app
        try {
            UIManager.setLookAndFeel(oldLaf);
        } catch (Exception ignored) {
        }
        return fc;
    }

    /*
     * ─────────────────────────────────────────────
     * XUẤT CSV (Excel đọc được)
     * ─────────────────────────────────────────────
     */
    public static void xuatCSV(Component parent, DefaultTableModel model, String tenFile) {
        JFileChooser fc = getSystemFileChooser("Lưu file Excel (CSV)", "CSV Files (*.csv)", "csv");
        fc.setSelectedFile(new File(tenFile + ".csv"));

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
     * XUẤT PDF (ghi trực tiếp ra file, không mở Print dialog)
     * ─────────────────────────────────────────────
     */
    public static void xuatPDF(Component parent, DefaultTableModel model, String tieuDe) {
        JFileChooser fc = getSystemFileChooser("Lưu file PDF", "PDF Files (*.pdf)", "pdf");
        fc.setSelectedFile(new File(tieuDe + ".pdf"));

        if (fc.showSaveDialog(parent) != JFileChooser.APPROVE_OPTION)
            return;

        File file = fc.getSelectedFile();
        if (!file.getName().toLowerCase().endsWith(".pdf"))
            file = new File(file.getAbsolutePath() + ".pdf");

        try {
            Rectangle pageSize = model.getColumnCount() > 6 ? PageSize.A4.rotate() : PageSize.A4;
            Document document = new Document(pageSize, 20, 20, 24, 20);
            PdfWriter.getInstance(document, new FileOutputStream(file));
            document.open();

            BaseFont baseFont;
            try {
                baseFont = BaseFont.createFont("C:/Windows/Fonts/arial.ttf", BaseFont.IDENTITY_H,
                        BaseFont.EMBEDDED);
            } catch (Exception e) {
                baseFont = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.NOT_EMBEDDED);
            }

            com.lowagie.text.Font titleFont = new com.lowagie.text.Font(baseFont, 16, com.lowagie.text.Font.BOLD);
            com.lowagie.text.Font headerFont = new com.lowagie.text.Font(baseFont, 10, com.lowagie.text.Font.BOLD);
            com.lowagie.text.Font cellFont = new com.lowagie.text.Font(baseFont, 9);

            Paragraph title = new Paragraph(tieuDe, titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(10f);
            document.add(title);

            java.util.List<Integer> visibleCols = new java.util.ArrayList<>();
            for (int c = 0; c < model.getColumnCount(); c++) {
                if (!model.getColumnName(c).equalsIgnoreCase("Thao tác")) {
                    visibleCols.add(c);
                }
            }

            PdfPTable table = new PdfPTable(visibleCols.size());
            table.setWidthPercentage(100f);
            table.setHeaderRows(1);

            for (int ci : visibleCols) {
                PdfPCell headerCell = new PdfPCell(new com.lowagie.text.Phrase(model.getColumnName(ci), headerFont));
                headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                headerCell.setBackgroundColor(new Color(175, 159, 203));
                headerCell.setPadding(5f);
                table.addCell(headerCell);
            }

            for (int r = 0; r < model.getRowCount(); r++) {
                for (int ci : visibleCols) {
                    Object val = model.getValueAt(r, ci);
                    String txt = val == null ? "" : val.toString();
                    PdfPCell cell = new PdfPCell(new com.lowagie.text.Phrase(txt, cellFont));
                    cell.setPadding(4f);
                    table.addCell(cell);
                }
            }

            document.add(table);
            document.close();

            JOptionPane.showMessageDialog(parent,
                    "Xuất PDF thành công!\n" + file.getAbsolutePath(),
                    "Xuất PDF", JOptionPane.INFORMATION_MESSAGE);
        } catch (Throwable ex) {
            JOptionPane.showMessageDialog(parent,
                "Lỗi khi xuất PDF: " + ex.getMessage()
                    + "\n\nNếu bạn chạy bằng 'Run Java', hãy đảm bảo classpath có lib/openpdf-1.3.39.jar hoặc chạy bằng run_local.bat.",
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
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
