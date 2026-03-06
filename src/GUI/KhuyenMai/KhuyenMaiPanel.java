package GUI.KhuyenMai;
<<<<<<< HEAD
import com.toedter.calendar.JDateChooser;
import java.text.SimpleDateFormat;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import BUS.DiscountBUS;
import DTO.DiscountDTO;


public class KhuyenMaiPanel extends JPanel {
    
    private DiscountBUS discountBUS = new DiscountBUS();
<<<<<<< Updated upstream
    // ===== Detail fields =====
    private JLabel lbName;
    private JTextArea taDesc;

    private JLabel lbId;
    private JLabel lbStart;
    private JLabel lbEnd;
    private JLabel lbValue;
    private JLabel lbType;
    private JLabel lbMinOrder;
=======

>>>>>>> Stashed changes
=======

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.ArrayList;
import BUS.DiscountBUS;
import DTO.DiscountDTO;
import BUS.SalesBUS;
import DTO.SaleDTO;
import DAO.SaleDAO;
import GUI.ExportUtils;
import java.util.List;
public class KhuyenMaiPanel extends JPanel {
    private DiscountBUS discountBUS = new DiscountBUS();
    private SalesBUS saleBUS = new SalesBUS();
>>>>>>> f48d17fd11c61dce6f0e7447679c56fc52a7648f
    private static final Color PAGE_BG   = new Color(0xF8F7FF);
    private static final Color RIGHT_BG  = new Color(0x5C4A7F);
    private static final Color CARD_LEFT = new Color(0xD1C4E9);
    private static final Color TBL_HDR   = new Color(0x3D2F5C);
    private static final Color BTN_ADD   = new Color(0x3D2F5C);
<<<<<<< HEAD
    // ===== CARD VOUCHER =====
private JLabel lbVoucherId = new JLabel();
private JLabel lbVoucherName = new JLabel();
private JLabel lbVoucherStart = new JLabel();
private JLabel lbVoucherMin = new JLabel();
private JLabel lbVoucherPercent = new JLabel();

private JLabel lbVoucherEnd = new JLabel();



// voucher detail
private JLabel vStart = new JLabel();
private JLabel vMin = new JLabel();
private JLabel vPercent = new JLabel();

private JLabel vEnd = new JLabel();


// product discount detail
private JLabel pOrigin = new JLabel();
private JLabel pPromo = new JLabel();
private JLabel pPercent = new JLabel();
private JLabel pStart = new JLabel();
private JLabel pEnd = new JLabel();
=======

>>>>>>> f48d17fd11c61dce6f0e7447679c56fc52a7648f
    private DefaultTableModel voucherModel;
    private DefaultTableModel discountModel;

    public KhuyenMaiPanel() {
        setLayout(new BorderLayout());
        setBackground(PAGE_BG);
        add(buildPageHeader(), BorderLayout.NORTH);
        
        JPanel body = new JPanel(new GridBagLayout());
        body.setBackground(PAGE_BG);
        body.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));

        GridBagConstraints gc = new GridBagConstraints();
        gc.gridy = 0; gc.weighty = 1.0; gc.fill = GridBagConstraints.BOTH;
<<<<<<< HEAD
        
        JScrollPane leftScroll = new JScrollPane(buildLeftColumn());
        leftScroll.setBorder(null);
        leftScroll.getVerticalScrollBar().setUnitIncrement(16);
        gc.gridx = 0; gc.weightx = 0.75; gc.insets = new Insets(0, 0, 0, 14);
        body.add(leftScroll, gc);

        JPanel rightCol = buildRightColumn();
        gc.gridx = 1; gc.weightx = 0.25; gc.insets = new Insets(0, 0, 0, 0);
=======

        JScrollPane leftScroll = new JScrollPane(buildLeftColumn());
        leftScroll.setBorder(null);
        leftScroll.getVerticalScrollBar().setUnitIncrement(16);
        gc.gridx = 0; gc.weightx = 0.58; gc.insets = new Insets(0, 0, 0, 14);
        body.add(leftScroll, gc);

        JPanel rightCol = buildRightColumn();
        gc.gridx = 1; gc.weightx = 0.42; gc.insets = new Insets(0, 0, 0, 0);
>>>>>>> f48d17fd11c61dce6f0e7447679c56fc52a7648f
        body.add(rightCol, gc);

        add(body, BorderLayout.CENTER);
         loadDiscountTables(); // load database
    }

    // ── Page header ──────────────────────────────────────────────────────────
    private JPanel buildPageHeader() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 12));
        p.setBackground(PAGE_BG);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0xDDDDDD)),
                BorderFactory.createEmptyBorder(0, 20, 0, 20)));
        JPanel bar = new JPanel();
        bar.setPreferredSize(new Dimension(5, 26));
        bar.setBackground(RIGHT_BG);
        p.add(bar);
        p.add(Box.createHorizontalStrut(12));
        JLabel lbl = new JLabel("QUẢN LÝ KHUYẾN MÃI");
        lbl.setFont(new Font("Arial", Font.BOLD, 20));
        p.add(lbl);
        return p;
    }

    // ── Left column (2 detail cards stacked) ─────────────────────────────────
    private JPanel buildLeftColumn() {
<<<<<<< HEAD
<<<<<<< Updated upstream
=======

    JPanel col = new JPanel();
    col.setLayout(new BoxLayout(col, BoxLayout.Y_AXIS));
    col.setBackground(PAGE_BG);

    // ===== PRODUCT DISCOUNT (PERCENT) =====
    JLabel[] productValues = {
        pOrigin, pPromo, pPercent, pStart, pEnd
    };

    col.add(buildDetailCard(
            "Số khuyến mãi", "TÊN SẢN PHẨM", null,
            new String[]{
                "Giá gốc :",
                "Giá khuyến mãi:",
                "% Giảm :",
                "Ngày bắt đầu :",
                "Ngày kết thúc :"
            },
            productValues
    ));

    col.add(Box.createVerticalStrut(20));

    // ===== VOUCHER (FIXED) =====
    JLabel[] voucherValues = {
        vStart, vMin, vPercent, vEnd
    };

    col.add(buildDetailCard(
            "Số voucher", "MÃ VOUCHER", "Mô tả",
            new String[]{
                "Ngày bắt đầu :",
                "Giá trị tối thiểu :",
                "% Giảm :",
                "Ngày kết thúc :"
            },
            voucherValues
    ));

    col.add(Box.createVerticalStrut(20));

    return col;
}
>>>>>>> Stashed changes

    JPanel col = new JPanel();
    col.setLayout(new BoxLayout(col, BoxLayout.Y_AXIS));
    col.setBackground(PAGE_BG);

    col.add(buildDetailCard(
            "Chi tiết khuyến mãi", 
            "Tên khuyến mãi", 
            "Mô tả",
            new String[]{
                "   Mã giảm:", //discount_id
                "   Ngày bắt đầu:", //start_date
                "   Ngày kết thúc:", //end_date
                "   Giá trị giảm:", //value
                "   Loại giảm:", //discount_type
                "   Tổng giá trị tối thiểu để áp dụng: " //min_order_amount
            }
    ));
    col.add(Box.createVerticalStrut(20));

    // THÊM DÒNG NÀY
    col.add(Box.createVerticalGlue());

    return col;
}
=======
        JPanel col = new JPanel();
        col.setLayout(new BoxLayout(col, BoxLayout.Y_AXIS));
        col.setBackground(PAGE_BG);
        col.add(buildDetailCard(
                "S\u1ED1 voucher", "M\u00C3 VOUCHER", "M\u00f4 t\u1EA3",
                new String[]{"Ng\u00e0y b\u1eaft \u0111\u1ea7u :", "Gi\u00e1 tr\u1ecb t\u1ed1i thi\u1ec3u :",
                        "% Gi\u1ea3m :", "S\u1ed1 l\u01b0\u1ee3t s\u1eed d\u1ee5ng:",
                        "Ng\u00e0y k\u1ebft th\u00fac :", "Khuy\u1ebfn m\u00e3i t\u1ed1i \u0111a :"}));
        col.add(Box.createVerticalStrut(20));
        col.add(buildDetailCard(
                "S\u1ed1 khuy\u1ebfn m\u00e3i", "T\u00caN S\u1ea2N PH\u1ea8M", null,
                new String[]{"Gi\u00e1 g\u1ed1c :", "Gi\u00e1 khuy\u1ebfn m\u00e3i:",
                        "% Gi\u1ea3m :", "Ng\u00e0y b\u1eaft \u0111\u1ea7u :",
                        "Ng\u00e0y k\u1ebft th\u00fac :"}));
        col.add(Box.createVerticalStrut(20));
        return col;
    }

>>>>>>> f48d17fd11c61dce6f0e7447679c56fc52a7648f
    /**
     * Dark-left / white-right detail card.
     * descLabel != null  → show textarea (voucher)
     * descLabel == null  → show image placeholder (product discount)
     */
<<<<<<< HEAD
<<<<<<< Updated upstream
    private JPanel buildDetailCard(String title, String nameLabel, String descLabel, String[] fields) {
=======
    private JPanel buildDetailCard(String idLabel, String nameLabel,
                               String descLabel,
                               String[] fields,
                               JLabel[] valueLabels) {
=======
    private JPanel buildDetailCard(String idLabel, String nameLabel,
                                   String descLabel, String[] fields) {
>>>>>>> f48d17fd11c61dce6f0e7447679c56fc52a7648f
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(PAGE_BG);
        card.setBorder(BorderFactory.createLineBorder(new Color(0xBBBBBB)));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
<<<<<<< HEAD
>>>>>>> Stashed changes

    JPanel card = new JPanel(new BorderLayout());
    card.setBackground(Color.WHITE);
    card.setBorder(BorderFactory.createTitledBorder(title));

    JPanel content = new JPanel(new GridBagLayout());
    content.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.fill = GridBagConstraints.BOTH;
    gbc.gridy = 0;
gbc.weighty = 1;          // thêm dòng này
gbc.anchor = GridBagConstraints.NORTH;  // thêm dòng này
    // ===== LEFT PANEL =====
    JPanel left = new JPanel();
    left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
    left.setBackground(CARD_LEFT);
    left.setBorder(BorderFactory.createEmptyBorder(15,15,15,15));

    JLabel nameTitle = new JLabel(nameLabel);
    nameTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

    lbName = new JLabel("Tên khuyến mãi");
lbName.setFont(new Font("Segoe UI", Font.BOLD, 16));
lbName.setAlignmentX(Component.CENTER_ALIGNMENT);

taDesc = new JTextArea(6,18);
taDesc.setLineWrap(true);
taDesc.setWrapStyleWord(true);
taDesc.setBorder(BorderFactory.createLineBorder(new Color(130,110,200)));

    JSeparator line = new JSeparator();

<<<<<<< Updated upstream
    JLabel descTitle = new JLabel(descLabel);
    descTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
=======
=======

        // Title bar
        JPanel hdr = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 7));
        hdr.setBackground(new Color(0xEDE7F6));
        hdr.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0xD1C4E9)));
        JLabel lbHdr = new JLabel("Thông tin chi tiết");
        lbHdr.setFont(new Font("Arial", Font.BOLD, 13));
        lbHdr.setForeground(new Color(0x3D2F5C));
        hdr.add(lbHdr);
        card.add(hdr, BorderLayout.NORTH);

        JPanel body = new JPanel(new GridLayout(1, 2));

        // Left panel
        JPanel left = new JPanel();
        left.setBackground(CARD_LEFT);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setBorder(BorderFactory.createEmptyBorder(20, 16, 20, 16));

        JLabel lbId = new JLabel(idLabel);
        lbId.setFont(new Font("Arial", Font.PLAIN, 11));
        lbId.setForeground(new Color(0x5C4A7F));
        lbId.setAlignmentX(Component.CENTER_ALIGNMENT);
        left.add(lbId);
        left.add(Box.createVerticalStrut(8));

        JLabel lbName = new JLabel(nameLabel);
        lbName.setFont(new Font("Arial", Font.BOLD, 15));
        lbName.setForeground(new Color(0x2F2C35));
        lbName.setAlignmentX(Component.CENTER_ALIGNMENT);
        left.add(lbName);
        left.add(Box.createVerticalStrut(6));

        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(0x9575CD));
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sep.setAlignmentX(Component.CENTER_ALIGNMENT);
        left.add(sep);
        left.add(Box.createVerticalStrut(12));

        if (descLabel != null) {
            JLabel lbDL = new JLabel(descLabel);
            lbDL.setFont(new Font("Arial", Font.PLAIN, 11));
            lbDL.setForeground(new Color(0x5C4A7F));
            lbDL.setAlignmentX(Component.CENTER_ALIGNMENT);
            left.add(lbDL);
            left.add(Box.createVerticalStrut(4));
            JTextArea ta = new JTextArea();
            ta.setFont(new Font("Arial", Font.PLAIN, 12));
            ta.setLineWrap(true); ta.setWrapStyleWord(true);
            ta.setBackground(Color.WHITE);
            ta.setForeground(new Color(0x2F2C35));
            JScrollPane tas = new JScrollPane(ta);
            tas.setBorder(BorderFactory.createLineBorder(new Color(0x9575CD)));
            tas.setAlignmentX(Component.CENTER_ALIGNMENT);
            tas.setPreferredSize(new Dimension(0, 85));
            tas.setMaximumSize(new Dimension(Integer.MAX_VALUE, 85));
            left.add(tas);
        } else {
            JPanel imgBox = new JPanel() {
                private final Color IC = new Color(92, 74, 127);
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    int pw = getWidth(), ph = getHeight();
                    double scale = Math.min((pw - 8) / 24.0, (ph - 8) / 24.0);
                    double ox = (pw - 24 * scale) / 2.0, oy = (ph - 24 * scale) / 2.0;
                    g2.translate(ox, oy);
                    g2.scale(scale, scale);
                    g2.setColor(IC);
                    g2.setStroke(new BasicStroke(2.0f));
                    g2.draw(new java.awt.geom.RoundRectangle2D.Double(3, 4, 18, 16, 4, 4));
                    g2.fill(new java.awt.geom.Ellipse2D.Double(7.2, 7.2, 3.6, 3.6));
                    java.awt.geom.Path2D.Float mountain = new java.awt.geom.Path2D.Float();
                    mountain.moveTo(5, 18); mountain.lineTo(10.5f, 12); mountain.lineTo(14, 15);
                    mountain.lineTo(16.5f, 13); mountain.lineTo(19, 18); mountain.closePath();
                    g2.fill(mountain);
                    g2.dispose();
                }
            };
            imgBox.setBackground(Color.WHITE);
            imgBox.setBorder(BorderFactory.createLineBorder(new Color(0x9575CD)));
            imgBox.setPreferredSize(new Dimension(110, 100));
            imgBox.setMaximumSize(new Dimension(120, 100));
            imgBox.setAlignmentX(Component.CENTER_ALIGNMENT);
            left.add(imgBox);
        }
        left.add(Box.createVerticalGlue());

>>>>>>> f48d17fd11c61dce6f0e7447679c56fc52a7648f
        // Right panel
        JPanel right = new JPanel(new GridBagLayout());
        right.setBackground(Color.WHITE);
        right.setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));
        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.HORIZONTAL;
        g.insets = new Insets(5, 4, 5, 4);
        Font lf = new Font("Arial", Font.BOLD, 13);
        for (int i = 0; i < fields.length; i++) {
            g.gridx = 0; g.gridy = i; g.weightx = 0.45;
            JLabel l = new JLabel(fields[i]); l.setFont(lf);
            right.add(l, g);
            g.gridx = 1; g.weightx = 0.55;
<<<<<<< HEAD
            right.add(valueLabels[i], g);
=======
            right.add(new JLabel(""), g);
>>>>>>> f48d17fd11c61dce6f0e7447679c56fc52a7648f
        }
        g.gridx = 0; g.gridy = fields.length;
        g.gridwidth = 2; g.weighty = 1.0; g.fill = GridBagConstraints.BOTH;
        right.add(new JLabel(), g);
<<<<<<< HEAD
>>>>>>> Stashed changes

    JTextArea desc = new JTextArea(6,18);
    desc.setLineWrap(true);
    desc.setWrapStyleWord(true);
    desc.setBorder(BorderFactory.createLineBorder(new Color(130,110,200)));

    left.add(nameTitle);
left.add(Box.createVerticalStrut(5));
left.add(lbName);
left.add(Box.createVerticalStrut(10));
left.add(line);
left.add(Box.createVerticalStrut(10));
left.add(descTitle);
left.add(Box.createVerticalStrut(10));
left.add(taDesc);

    // ===== RIGHT PANEL =====
    JPanel right = new JPanel(new GridLayout(fields.length,2,10,10));
    right.setBackground(Color.WHITE);

    lbId = new JLabel("...");
lbStart = new JLabel("...");
lbEnd = new JLabel("...");
lbValue = new JLabel("...");
lbType = new JLabel("...");
lbMinOrder = new JLabel("...");

right.add(new JLabel("Mã giảm:"));
right.add(lbId);

right.add(new JLabel("Ngày bắt đầu:"));
right.add(lbStart);

right.add(new JLabel("Ngày kết thúc:"));
right.add(lbEnd);

right.add(new JLabel("Giá trị giảm:"));
right.add(lbValue);

right.add(new JLabel("Loại giảm:"));
right.add(lbType);

right.add(new JLabel("Tổng giá trị tối thiểu để áp dụng:"));
right.add(lbMinOrder);

    // LEFT width 40%
    gbc.gridx = 0;
    gbc.weightx = 0.4;
    content.add(left, gbc);

    // RIGHT width 60%
    gbc.gridx = 1;
    gbc.weightx = 0.6;
    content.add(right, gbc);

    card.add(content, BorderLayout.CENTER);

    return card;
}

    // ── Right column ─────────────────────────────────────────────────────────
<<<<<<< Updated upstream
    private JPanel buildRightColumn() {
=======
   private JPanel buildRightColumn() {
>>>>>>> Stashed changes

    JPanel col = new JPanel();
    col.setLayout(new BoxLayout(col, BoxLayout.Y_AXIS));
    col.setBackground(RIGHT_BG);
<<<<<<< Updated upstream
    col.setBorder(BorderFactory.createEmptyBorder(16,16,16,16));

    discountModel = new DefaultTableModel(
        new String[]{"ID", "Tên khuyến mãi", "Giá trị", "Loại giảm"},0
    ){
        @Override
        public boolean isCellEditable(int r,int c){
            return false;
        }
    };
=======
    col.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

    // ===== PERCENT (DISCOUNT PRODUCT) =====
    discountModel = new DefaultTableModel(
            new String[]{"Mã Giảm", "Mã SP", "% Giảm"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };

    col.add(buildListSection(
            "Danh sách giảm giá sản phẩm",
            "+ THÊM KHUYẾN MÃI",
            discountModel,
            this::showAddDiscountDialog
    ));

    col.add(Box.createVerticalStrut(24));

    // ===== FIXED (VOUCHER) =====
    voucherModel = new DefaultTableModel(
            new String[]{"Mã voucher","Tên Voucher","% Giảm"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };

    col.add(buildListSection(
            "Danh sách voucher",
            "+ THÊM VOUCHER",
            voucherModel,
            this::showAddVoucherDialog
    ));

    return col;
}
>>>>>>> Stashed changes

    col.add(buildListSection(
        "Danh sách khuyến mãi",
        "+ THÊM KHUYẾN MÃI",
        discountModel,
        this::showAddDiscountDialog
    ));

    return col;
}
=======

        body.add(left); body.add(right);
        card.add(body, BorderLayout.CENTER);
        return card;
    }

    // ── Right column ─────────────────────────────────────────────────────────
    private JPanel buildRightColumn() {
        JPanel col = new JPanel();
        col.setLayout(new BoxLayout(col, BoxLayout.Y_AXIS));
        col.setBackground(RIGHT_BG);
        col.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        voucherModel = new DefaultTableModel(
                new String[]{ "M\u00e3 voucher","Tên Voucher", "% Gi\u1ea3m"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        col.add(buildListSection("Danh s\u00e1ch voucher", "+ TH\u00caM VOUCHER", voucherModel, this::showAddVoucherDialog));
        col.add(Box.createVerticalStrut(24));

        discountModel = new DefaultTableModel(
                new String[]{"M\u00e3 Gi\u1ea3m", "M\u00e3 SP", "% Gi\u1ea3m"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        col.add(buildListSection("Danh s\u00e1ch gi\u1ea3m gi\u00e1 s\u1ea3n ph\u1ea9m",
                "+ TH\u00caM KHUY\u1EBEN M\u00C3I", discountModel, this::showAddDiscountDialog));
        return col;
    }

>>>>>>> f48d17fd11c61dce6f0e7447679c56fc52a7648f
    private JPanel buildListSection(String title, String addLabel, DefaultTableModel model, Runnable onAdd) {
        JPanel section = new JPanel(new BorderLayout(0, 8));
        section.setBackground(RIGHT_BG);
        section.setAlignmentX(Component.LEFT_ALIGNMENT);
        section.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

        JPanel top = new JPanel();
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        top.setBackground(RIGHT_BG);

        JLabel lbTitle = new JLabel(title);
        lbTitle.setFont(new Font("Arial", Font.BOLD | Font.ITALIC, 17));
        lbTitle.setForeground(Color.WHITE);
        lbTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        top.add(lbTitle);
        top.add(Box.createVerticalStrut(10));

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        btnRow.setBackground(RIGHT_BG);
        btnRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnRow.add(listBtn("Refresh", new Color(0x388E3C)));
        JButton btnAdd = listBtn(addLabel, BTN_ADD);
        btnAdd.addActionListener(e -> onAdd.run());
        btnRow.add(btnAdd);
<<<<<<< HEAD
=======

        JButton btnPDF    = ExportUtils.makeExportButton("Xuất PDF",   new Color(0x7B52AB));
        JButton btnExcel  = ExportUtils.makeExportButton("Xuất Excel", new Color(0x2E7D32));
        JButton btnImport = ExportUtils.makeImportButton("Nhập CSV");
        btnPDF.setFont(new Font("Arial", Font.BOLD, 12));
        btnExcel.setFont(new Font("Arial", Font.BOLD, 12));
        btnImport.setFont(new Font("Arial", Font.BOLD, 12));
        btnPDF.addActionListener(e -> ExportUtils.xuatPDF(this, model, title));
        btnExcel.addActionListener(e -> ExportUtils.xuatCSV(this, model, title.replaceAll("\\W+", "_")));
        btnImport.addActionListener(e -> {
            List<String[]> rows = ExportUtils.importCSV(this);
            if (rows == null) return;
            for (String[] r : rows) model.addRow((Object[])r);
        });
        btnRow.add(btnPDF); btnRow.add(btnExcel); btnRow.add(btnImport);
>>>>>>> f48d17fd11c61dce6f0e7447679c56fc52a7648f
        top.add(btnRow);
        top.add(Box.createVerticalStrut(8));

        JPanel searchRow = new JPanel(new BorderLayout(6, 0));
        searchRow.setBackground(RIGHT_BG);
        searchRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        JTextField tfSearch = new JTextField();
        tfSearch.setFont(new Font("Arial", Font.PLAIN, 13));
        JButton btnSearch = listBtn("SEARCH", TBL_HDR);
        btnSearch.setPreferredSize(new Dimension(86, 30));
        searchRow.add(tfSearch, BorderLayout.CENTER);
        searchRow.add(btnSearch, BorderLayout.EAST);
        top.add(searchRow);
        top.add(Box.createVerticalStrut(8));

        section.add(top, BorderLayout.NORTH);

        // Table
        JTable table = new JTable(model);
<<<<<<< HEAD

table.getSelectionModel().addListSelectionListener(e -> {

    if (!e.getValueIsAdjusting()) {

        int row = table.getSelectedRow();

        if (row >= 0) {

            String id = table.getValueAt(row, 0).toString();

            showDiscountDetail(id);

        }

    }

});
=======
>>>>>>> f48d17fd11c61dce6f0e7447679c56fc52a7648f
        table.setFont(new Font("Arial", Font.PLAIN, 13));
        table.setRowHeight(30);
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(true);
        table.setGridColor(new Color(0xD1C4E9));
        table.setFillsViewportHeight(true);
        table.setPreferredScrollableViewportSize(new Dimension(300, 180));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        table.getTableHeader().setBackground(TBL_HDR);
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setReorderingAllowed(false);
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable t, Object v, boolean sel, boolean foc, int r, int c) {
                Component comp = super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                comp.setBackground(sel ? new Color(0xC5B3E6)
                        : (r % 2 == 0 ? Color.WHITE : new Color(0xE8E0F0)));
                return comp;
            }
        });
<<<<<<< HEAD
        table.getSelectionModel().addListSelectionListener(e -> {

    if(!e.getValueIsAdjusting()){

        int row = table.getSelectedRow();

        if(row >= 0){

            int id = (int) table.getValueAt(row,0);

            DiscountDTO d = discountBUS.getDiscountById(id);

            if(d != null){

                lbName.setText(d.getName());
                taDesc.setText(d.getDescription());

                lbId.setText(String.valueOf(d.getId()));
                lbStart.setText(String.valueOf(d.getStartDate()));
                lbEnd.setText(String.valueOf(d.getEndDate()));
                lbValue.setText(String.valueOf(d.getValue()));
                lbType.setText(d.getDiscountType().name());
                lbMinOrder.setText(String.valueOf(d.getMinOrderAmount()));

            }
        }
    }

});
=======

>>>>>>> f48d17fd11c61dce6f0e7447679c56fc52a7648f
        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createLineBorder(TBL_HDR));
        section.add(sp, BorderLayout.CENTER);
        return section;
    }

    private JButton listBtn(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.BOLD, 12));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

<<<<<<< HEAD
   private void showAddDiscountDialog() {

    JDialog dlg = new JDialog(
            SwingUtilities.getWindowAncestor(this),
            "Thêm khuyến mãi",
            Dialog.ModalityType.APPLICATION_MODAL
    );

    dlg.setLayout(new BorderLayout());

    JPanel form = new JPanel(new GridLayout(7,2,10,10));
    form.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));

    JTextField tfName = new JTextField();
    JTextField tfDesc = new JTextField();
    JTextField tfValue = new JTextField();
    JTextField tfMinOrder = new JTextField();

    // ===== Combobox loại giảm =====
    JComboBox<String> cbType = new JComboBox<>(
            new String[]{"PERCENT","FIXED"}
    );

    // ===== Date chooser =====
    JDateChooser dcStart = new JDateChooser();
    JDateChooser dcEnd = new JDateChooser();

    dcStart.setDateFormatString("dd/MM/yyyy");
    dcEnd.setDateFormatString("dd/MM/yyyy");

    form.add(new JLabel("Tên khuyến mãi:"));
    form.add(tfName);

    form.add(new JLabel("Mô tả:"));
    form.add(tfDesc);

    form.add(new JLabel("Giá trị giảm:"));
    form.add(tfValue);

    form.add(new JLabel("Loại giảm:"));
    form.add(cbType);

    form.add(new JLabel("Ngày bắt đầu:"));
    form.add(dcStart);

    form.add(new JLabel("Ngày kết thúc:"));
    form.add(dcEnd);

    form.add(new JLabel("Min order:"));
    form.add(tfMinOrder);

    dlg.add(form,BorderLayout.CENTER);

    // ===== BUTTON =====
    JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

    JButton btnSave = new JButton("Lưu");
    JButton btnCancel = new JButton("Hủy");

    btnPanel.add(btnCancel);
    btnPanel.add(btnSave);

    dlg.add(btnPanel,BorderLayout.SOUTH);

    // ===== SAVE =====
    btnSave.addActionListener(e -> {

    try{

        // ===== VALIDATE DATE =====

        if(dcStart.getDate() == null || dcEnd.getDate() == null){

            JOptionPane.showMessageDialog(
                    dlg,
                    "Vui lòng chọn ngày bắt đầu và ngày kết thúc"
            );

            return;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        String start = sdf.format(dcStart.getDate());
        String end   = sdf.format(dcEnd.getDate());

        String result = discountBUS.addDiscount(

                tfName.getText().trim(),
                tfDesc.getText().trim(),
                tfValue.getText().trim(),
                cbType.getSelectedItem().toString(),
                start,
                end,
                tfMinOrder.getText().trim()
        );

        if(result.equals("SUCCESS")){

            JOptionPane.showMessageDialog(dlg,"Thêm khuyến mãi thành công");

            loadDiscountTables();

            dlg.dispose();

        }else{

            JOptionPane.showMessageDialog(dlg,result);

        }

    }catch(Exception ex){

        ex.printStackTrace(); // debug console

        JOptionPane.showMessageDialog(
                dlg,
                "Lỗi hệ thống",
                "Error",
                JOptionPane.ERROR_MESSAGE
                
        );

    }

});

    btnCancel.addActionListener(e -> dlg.dispose());

    dlg.pack();
    dlg.setLocationRelativeTo(this);
    dlg.setVisible(true);
}
=======
    private void showAddVoucherDialog() {
        JDialog dlg = new JDialog(SwingUtilities.getWindowAncestor(this),
                "Th\u00eam Voucher", Dialog.ModalityType.APPLICATION_MODAL);
        dlg.setLayout(new BorderLayout());
        String[] labels = {
            "M\u00e3 voucher:", "M\u00f4 t\u1ea3:", "Ng\u00e0y b\u1eaft \u0111\u1ea7u (dd/MM/yyyy):",
            "Gi\u00e1 tr\u1ecb t\u1ed1i thi\u1ec3u (\u0111):", "% Gi\u1ea3m:", "S\u1ed1 l\u01b0\u1ee3t s\u1eed d\u1ee5ng:",
            "Ng\u00e0y k\u1ebft th\u00fac (dd/MM/yyyy):", "Khuy\u1ebfn m\u00e3i t\u1ed1i \u0111a (\u0111):"
        };
        JTextField[] tfs = new JTextField[labels.length];
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(16, 20, 8, 20));
        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.HORIZONTAL; g.insets = new Insets(5, 5, 5, 5);
        for (int i = 0; i < labels.length; i++) {
            g.gridx = 0; g.gridy = i; g.weightx = 0.5;
            JLabel l = new JLabel(labels[i]);
            l.setFont(new Font("Arial", Font.BOLD, 13));
            form.add(l, g);
            g.gridx = 1; g.weightx = 0.5;
            tfs[i] = new JTextField(18);
            tfs[i].setFont(new Font("Arial", Font.PLAIN, 13));
            form.add(tfs[i], g);
        }
        dlg.add(form, BorderLayout.CENTER);
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
        JButton btnLuu = new JButton("L\u01b0u");
        btnLuu.setBackground(new Color(0x5C4A7F));
        btnLuu.setForeground(Color.WHITE);
        btnLuu.setFocusPainted(false);
        JButton btnHuy = new JButton("H\u1ee7y");
        btnHuy.setFocusPainted(false);
        btns.add(btnHuy); btns.add(btnLuu);
        dlg.add(btns, BorderLayout.SOUTH);
        btnLuu.addActionListener(e -> {
            java.util.List<String> errs = new java.util.ArrayList<>();
            if (tfs[0].getText().trim().isEmpty())
                errs.add("- Vui l\u00f2ng nh\u1eadp m\u00e3 voucher.");
            if (tfs[2].getText().trim().isEmpty())
                errs.add("- Vui l\u00f2ng nh\u1eadp ng\u00e0y b\u1eaft \u0111\u1ea7u.");
            if (tfs[3].getText().trim().isEmpty())
                errs.add("- Vui l\u00f2ng nh\u1eadp gi\u00e1 tr\u1ecb t\u1ed1i thi\u1ec3u.");
            if (tfs[4].getText().trim().isEmpty())
                errs.add("- Vui l\u00f2ng nh\u1eadp % gi\u1ea3m.");
            if (tfs[5].getText().trim().isEmpty())
                errs.add("- Vui l\u00f2ng nh\u1eadp s\u1ed1 l\u01b0\u1ee3t s\u1eed d\u1ee5ng.");
            if (tfs[6].getText().trim().isEmpty())
                errs.add("- Vui l\u00f2ng nh\u1eadp ng\u00e0y k\u1ebft th\u00fac.");
            if (!errs.isEmpty()) {
                JOptionPane.showMessageDialog(dlg, String.join("\n", errs),
                        "Thi\u1ebfu th\u00f4ng tin", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String ma = tfs[0].getText().trim();
            voucherModel.addRow(new Object[]{
                voucherModel.getRowCount() + 1, ma,
                tfs[4].getText().trim(), tfs[5].getText().trim()
            });
            dlg.dispose();
        });
        btnHuy.addActionListener(e -> {

            boolean dirty = false;

            for (JTextField f : tfs) {

                if (!f.getText().trim().isEmpty()) { dirty = true; break; }

            }

            if (dirty) {

                int cf = JOptionPane.showConfirmDialog(dlg,

                        "Bạn có chắc muốn hủy? Thông tin đã nhập sẽ mất.",

                        "Xác nhận hủy", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

                if (cf != JOptionPane.YES_OPTION) return;

            }

            dlg.dispose();

        });
        dlg.pack();
        dlg.setMinimumSize(new Dimension(440, dlg.getPreferredSize().height));
        dlg.setLocationRelativeTo(this);
        dlg.setVisible(true);
    }

    private void showAddDiscountDialog() {
        JDialog dlg = new JDialog(SwingUtilities.getWindowAncestor(this),
                "Th\u00eam Khuy\u1ebfn M\u00e3i S\u1ea3n Ph\u1ea9m", Dialog.ModalityType.APPLICATION_MODAL);
        dlg.setLayout(new BorderLayout());
        String[] labels = {
            "M\u00e3 gi\u1ea3m:", "M\u00e3 s\u1ea3n ph\u1ea9m:", "% Gi\u1ea3m:",
            "Ng\u00e0y b\u1eaft \u0111\u1ea7u (dd/MM/yyyy):", "Ng\u00e0y k\u1ebft th\u00fac (dd/MM/yyyy):"
        };
        JTextField[] tfs = new JTextField[labels.length];
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(16, 20, 8, 20));
        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.HORIZONTAL; g.insets = new Insets(5, 5, 5, 5);
        for (int i = 0; i < labels.length; i++) {
            g.gridx = 0; g.gridy = i; g.weightx = 0.5;
            JLabel l = new JLabel(labels[i]);
            l.setFont(new Font("Arial", Font.BOLD, 13));
            form.add(l, g);
            g.gridx = 1; g.weightx = 0.5;
            tfs[i] = new JTextField(18);
            tfs[i].setFont(new Font("Arial", Font.PLAIN, 13));
            form.add(tfs[i], g);
        }
        dlg.add(form, BorderLayout.CENTER);
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
        JButton btnLuu = new JButton("L\u01b0u");
        btnLuu.setBackground(new Color(0x5C4A7F));
        btnLuu.setForeground(Color.WHITE);
        btnLuu.setFocusPainted(false);
        JButton btnHuy = new JButton("H\u1ee7y");
        btnHuy.setFocusPainted(false);
        btns.add(btnHuy); btns.add(btnLuu);
        dlg.add(btns, BorderLayout.SOUTH);
        btnLuu.addActionListener(e -> {
            java.util.List<String> errs = new java.util.ArrayList<>();
            String maGiam   = tfs[0].getText().trim();
            String maSP     = tfs[1].getText().trim();
            String phanTram = tfs[2].getText().trim();
            String ngayBD   = tfs[3].getText().trim();
            String ngayKT   = tfs[4].getText().trim();

            if (maGiam.isEmpty())   errs.add("- Vui l\u00f2ng nh\u1eadp m\u00e3 gi\u1ea3m.");
            if (maSP.isEmpty())     errs.add("- Vui l\u00f2ng nh\u1eadp m\u00e3 s\u1ea3n ph\u1ea9m.");
            if (phanTram.isEmpty()) {
                errs.add("- Vui l\u00f2ng nh\u1eadp % gi\u1ea3m.");
            } else {
                try {
                    double pct = Double.parseDouble(phanTram.replace("%", "").trim());
                    if (pct <= 0 || pct > 100)
                        errs.add("- % Gi\u1ea3m ph\u1ea3i l\u00e0 s\u1ed1 trong kho\u1ea3ng (0, 100].");
                } catch (NumberFormatException ex2) {
                    errs.add("- % Gi\u1ea3m ph\u1ea3i l\u00e0 s\u1ed1 h\u1ee3p l\u1ec7.");
                }
            }
            if (ngayBD.isEmpty()) errs.add("- Vui l\u00f2ng nh\u1eadp ng\u00e0y b\u1eaft \u0111\u1ea7u.");
            if (ngayKT.isEmpty()) errs.add("- Vui l\u00f2ng nh\u1eadp ng\u00e0y k\u1ebft th\u00fac.");
            if (!ngayBD.isEmpty() && !ngayKT.isEmpty()) {
                try {
                    java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");
                    sdf.setLenient(false);
                    java.util.Date bd = sdf.parse(ngayBD);
                    java.util.Date kt = sdf.parse(ngayKT);
                    if (!bd.before(kt))
                        errs.add("- Ng\u00e0y k\u1ebft th\u00fac ph\u1ea3i sau ng\u00e0y b\u1eaft \u0111\u1ea7u.");
                } catch (java.text.ParseException ex2) {
                    errs.add("- Ng\u00e0y ph\u1ea3i \u0111\u00fang \u0111\u1ecbnh d\u1ea1ng dd/MM/yyyy.");
                }
            }
            if (!errs.isEmpty()) {
                JOptionPane.showMessageDialog(dlg, String.join("\n", errs),
                        "Thi\u1ebfu th\u00f4ng tin", JOptionPane.WARNING_MESSAGE);
                return;
            }
            discountModel.addRow(new Object[]{ maGiam, maSP, phanTram });
            dlg.dispose();
        });
        btnHuy.addActionListener(e -> {

            boolean dirty = false;

            for (JTextField f : tfs) {

                if (!f.getText().trim().isEmpty()) { dirty = true; break; }

            }

            if (dirty) {

                int cf = JOptionPane.showConfirmDialog(dlg,

                        "Bạn có chắc muốn hủy? Thông tin đã nhập sẽ mất.",

                        "Xác nhận hủy", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

                if (cf != JOptionPane.YES_OPTION) return;

            }

            dlg.dispose();

        });
        dlg.pack();
        dlg.setMinimumSize(new Dimension(420, dlg.getPreferredSize().height));
        dlg.setLocationRelativeTo(this);
        dlg.setVisible(true);
    }
>>>>>>> f48d17fd11c61dce6f0e7447679c56fc52a7648f
   private void loadDiscountTables(){

    ArrayList<DiscountDTO> list = discountBUS.getAllDiscounts();

<<<<<<< HEAD
=======
    voucherModel.setRowCount(0);
>>>>>>> f48d17fd11c61dce6f0e7447679c56fc52a7648f
    discountModel.setRowCount(0);

    for(DiscountDTO d : list){

<<<<<<< HEAD
        discountModel.addRow(new Object[]{
            d.getId(),
            d.getName(),
            d.getValue(),
            d.getDiscountType().name()
        });

    }
}
private void loadVoucherDetail(int row){

    ArrayList<DiscountDTO> list = discountBUS.getAllDiscounts();

    for(DiscountDTO d : list){

        if(d.getDiscountType().name().equals("FIXED") 
           && d.getId() == (int)voucherModel.getValueAt(row,0)){

            lbVoucherId.setText(String.valueOf(d.getId()));
            lbVoucherName.setText(d.getName());
            lbVoucherStart.setText(d.getStartDate().toString());
            lbVoucherMin.setText(String.valueOf(d.getMinOrderAmount()));
            lbVoucherPercent.setText(String.valueOf(d.getValue()));
            lbVoucherEnd.setText(d.getEndDate().toString());
=======
        if(d.getDiscountType().name().equals("FIXED")){

            voucherModel.addRow(new Object[]{
                d.getId(),
                d.getName(),
                d.getValue()
            });

        }

        if(d.getDiscountType().name().equals("PERCENT")){

            discountModel.addRow(new Object[]{
                d.getId(),
                d.getName(),
                d.getValue()
            });
>>>>>>> f48d17fd11c61dce6f0e7447679c56fc52a7648f

        }

    }
<<<<<<< HEAD

}
private void showDiscountDetail(String id){

    DiscountDTO d = discountBUS.getDiscountById(Integer.parseInt(id));

    if(d == null) return;

    if(d.getDiscountType().name().equals("FIXED")){

        vStart.setText(d.getStartDate().toString());
        vMin.setText(String.valueOf(d.getMinOrderAmount()));
        vPercent.setText(String.valueOf(d.getValue()));
        vEnd.setText(d.getEndDate().toString());

    }

    if(d.getDiscountType().name().equals("PERCENT")){

        pPercent.setText(String.valueOf(d.getValue()));
        pStart.setText(d.getStartDate().toString());
        pEnd.setText(d.getEndDate().toString());

    }
}
}

=======
}
}


>>>>>>> f48d17fd11c61dce6f0e7447679c56fc52a7648f
