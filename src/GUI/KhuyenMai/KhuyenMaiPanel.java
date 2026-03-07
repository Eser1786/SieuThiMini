package GUI.KhuyenMai;
import com.toedter.calendar.JDateChooser;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;

import java.util.ArrayList;
import java.util.Locale;

import BUS.DiscountBUS;
import DTO.DiscountDTO;
import GUI.ExportUtils;


public class KhuyenMaiPanel extends JPanel {
    private DiscountBUS discountBUS = new DiscountBUS();
    // ===== Detail fields =====
    private JLabel lbName;
    private JTextArea taDesc;
    JButton btnExcel;
    JButton btnPDF; 
    private JLabel lbId;
    private JLabel lbStart;
    private JLabel lbEnd;
    private JLabel lbValue;
    private JLabel lbType;
    private JLabel lbMinOrder;
    private JLabel lbStatus;
    private static final Color PAGE_BG   = new Color(0xF8F7FF);
    private static final Color RIGHT_BG  = new Color(0x5C4A7F);
    private static final Color CARD_LEFT = new Color(0xD1C4E9);
    private static final Color TBL_HDR   = new Color(0x3D2F5C);
    private static final Color BTN_ADD   = new Color(0x3D2F5C);
    

    private DefaultTableModel discountModel;

    public KhuyenMaiPanel() {
        setLayout(new BorderLayout());
        setBackground(PAGE_BG);
        add(buildPageHeader(), BorderLayout.NORTH);
        
        JScrollPane leftScroll = new JScrollPane(buildLeftColumn());
leftScroll.setBorder(null);
leftScroll.getVerticalScrollBar().setUnitIncrement(16);

JPanel rightCol = buildRightColumn();

JSplitPane split = new JSplitPane(
        JSplitPane.HORIZONTAL_SPLIT,
        leftScroll,
        rightCol
);

split.setResizeWeight(0.65); // 65% - 35%
split.setDividerSize(6);
split.setBorder(null);
btnExcel.addActionListener(e -> {

    ArrayList<DiscountDTO> list = discountBUS.getAllDiscounts();

    DefaultTableModel exportModel = new DefaultTableModel(
        new String[]{
            "ID",
            "Tên khuyến mãi",
            "Loại giảm",
            "Giá trị",
            "Đơn tối thiểu",
            "Ngày bắt đầu",
            "Ngày kết thúc",
            "Trạng thái",
            "Tự động áp dụng",
            "Mô tả"
        }, 0
    );

    for (DiscountDTO d : list) {

        exportModel.addRow(new Object[]{
            d.getId(),
            d.getName(),
            d.getDiscountType(),
            d.getFormattedValue(),
            d.getMinOrderAmount(),
            d.getStartDate(),
            d.getEndDate(),
            d.getStatus(),
            d.getIsAutoApply(),
            d.getDescription()
        });

    }

    ExportUtils.xuatCSV(this, exportModel, "DanhSachKhuyenMai");

});

btnPDF.addActionListener(e -> {

    ArrayList<DiscountDTO> list = discountBUS.getAllDiscounts();

    DefaultTableModel exportModel = new DefaultTableModel(
        new String[]{
            "ID",
            "Tên khuyến mãi",
            "Loại",
            "Giá trị",
            "Đơn tối thiểu",
            "Ngày bắt đầu",
            "Ngày kết thúc",
            "Trạng thái",
            "Tự động",
            "Mô tả"
        }, 0
    );

    NumberFormat nf = NumberFormat.getInstance(new Locale("vi","VN"));

    for (DiscountDTO d : list) {

        exportModel.addRow(new Object[]{
            d.getId(),
            d.getName(),
            d.getDiscountType(),
            d.getFormattedValue(),
            nf.format(d.getMinOrderAmount()) + " VND",
            d.getStartDate(),
            d.getEndDate(),
            d.getStatus(),
            d.getIsAutoApply() ? "Có" : "Không",
            d.getDescription()
        });

    }

    ExportUtils.xuatPDF(this, exportModel, "DanhSachKhuyenMai");

});
add(split, BorderLayout.CENTER);
         loadDiscountTables(); // load database
    }

    // ── Page header ──────────────────────────────────────────────────────────
   private JPanel buildPageHeader() {

    JPanel p = new JPanel(new BorderLayout());
    p.setBackground(PAGE_BG);
    p.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0xDDDDDD)),
            BorderFactory.createEmptyBorder(10, 20, 10, 20)));

    /* ===== LEFT SIDE (TITLE) ===== */

    JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
    left.setBackground(PAGE_BG);

    JPanel bar = new JPanel();
    bar.setPreferredSize(new Dimension(5, 26));
    bar.setBackground(RIGHT_BG);

    JLabel lbl = new JLabel("QUẢN LÝ KHUYẾN MÃI");
    lbl.setFont(new Font("Arial", Font.BOLD, 20));

    left.add(bar);
    left.add(lbl);

    /* ===== RIGHT SIDE (EXPORT BUTTON) ===== */

    JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT,10,0));
    right.setBackground(PAGE_BG);

    btnExcel = ExportUtils.makeExportButton(
            "Export Excel",
            new Color(0x1976D2)
    );

    btnPDF = ExportUtils.makeExportButton(
            "Export PDF",
            new Color(0xD32F2F)
    );

    right.add(btnExcel);
    right.add(btnPDF);

    /* ===== ADD TO HEADER ===== */

    p.add(left, BorderLayout.WEST);
    p.add(right, BorderLayout.EAST);

    return p;
}

    // ── Left column (2 detail cards stacked) ─────────────────────────────────
    private JPanel buildLeftColumn() {

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
                "   Tổng giá trị tối thiểu để áp dụng: ", //min_order_amount
                "   Trạng thái:" //status
            }
    ));
    col.add(Box.createVerticalStrut(20));

    // THÊM DÒNG NÀY
    col.add(Box.createVerticalGlue());

    return col;
}
    /**
     * Dark-left / white-right detail card.
     * descLabel != null  → show textarea (voucher)
     * descLabel == null  → show image placeholder (product discount)
     */
    private JPanel buildDetailCard(String title, String nameLabel, String descLabel, String[] fields) {

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

    JLabel descTitle = new JLabel(descLabel);
    descTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

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
lbStatus = new JLabel("...");

JLabel[] valueLabels = {
    lbId,
    lbStart,
    lbEnd,
    lbValue,
    lbType,
    lbMinOrder,
    lbStatus
};

for(int i = 0; i < fields.length; i++){
    right.add(new JLabel(fields[i]));
    right.add(valueLabels[i]);
}

    // LEFT width 40%
    gbc.gridx = 0;
    gbc.weightx = 0.4;
    content.add(left, gbc);

    // RIGHT width 60%
    gbc.gridx = 1;
    gbc.weightx = 0.6;
    content.add(right, gbc);

    card.add(content, BorderLayout.CENTER);
content.add(right, gbc);


/* ===== THÊM ĐOẠN NÀY ===== */

JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

JButton btnEdit = new JButton("SỬA");
JButton btnDelete = new JButton("XÓA");

btnPanel.add(btnEdit);
btnPanel.add(btnDelete);

card.add(btnPanel, BorderLayout.SOUTH);

/* ===== ACTION SỬA ===== */

btnEdit.addActionListener(e -> {

    if(lbId.getText().equals("...")){
        JOptionPane.showMessageDialog(this,"Chọn khuyến mãi trước");
        return;
    }

    int id = Integer.parseInt(lbId.getText());

    DiscountDTO d = discountBUS.getDiscountById(id);

    if(d != null){
        showEditDiscountDialog(d);
    }

});

/* ===== ACTION XÓA ===== */

btnDelete.addActionListener(e -> {

    if(lbId.getText().equals("...")){
        JOptionPane.showMessageDialog(this,"Chọn khuyến mãi trước");
        return;
    }

    int confirm = JOptionPane.showConfirmDialog(
            this,
            "Xóa khuyến mãi này?",
            "Confirm",
            JOptionPane.YES_NO_OPTION
    );

    if(confirm == JOptionPane.YES_OPTION){

        int id = Integer.parseInt(lbId.getText());

        String result = discountBUS.deleteDiscount(id);

        if(result.equals("SUCCESS")){
            JOptionPane.showMessageDialog(this,"Đã xóa khuyến mãi");
                clearDetailCard();
            loadDiscountTables();
        }else{
            JOptionPane.showMessageDialog(this,result);
        }
    }

});

/* ===== HẾT PHẦN THÊM ===== */

    return card;
}

    // ── Right column ─────────────────────────────────────────────────────────
    private JPanel buildRightColumn() {

    JPanel col = new JPanel();
    col.setLayout(new BoxLayout(col, BoxLayout.Y_AXIS));
    col.setBackground(RIGHT_BG);
    col.setBorder(BorderFactory.createEmptyBorder(16,16,16,16));
            
    discountModel = new DefaultTableModel(
        new String[]{"ID", "Tên khuyến mãi", "Giá trị", "Loại giảm","Trạng thái"},0
    ){
        @Override
        public boolean isCellEditable(int r,int c){
            return false;
        }
    };

    col.add(buildListSection(
        "Danh sách khuyến mãi",
        "+ THÊM KHUYẾN MÃI",
        discountModel,
        this::showAddDiscountDialog
    ));

    return col;
}
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
        JButton btnRefresh = listBtn("Refresh", new Color(0x388E3C));
        btnRefresh.addActionListener(e -> loadDiscountTables());
        btnRow.add(btnRefresh);
        JButton btnAdd = listBtn(addLabel, BTN_ADD);
        btnAdd.addActionListener(e -> onAdd.run());
        btnRow.add(btnAdd);
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
        JPanel filterRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
filterRow.setBackground(RIGHT_BG);
filterRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        JComboBox<String> cbType = new JComboBox<>(new String[]{
            "ALL TYPE",
            "PERCENT",
            "FIXED"
        });

        JComboBox<String> cbStatus = new JComboBox<>(new String[]{
            "ALL STATUS",
            "ACTIVE",
            "EXPIRED"
        });

        filterRow.add(new JLabel("Type:"){{setForeground(Color.WHITE);}});
        filterRow.add(cbType);

        filterRow.add(new JLabel("Status:"){{setForeground(Color.WHITE);}});
        filterRow.add(cbStatus);

        top.add(filterRow);
        top.add(Box.createVerticalStrut(8));
        
                   
        btnSearch.addActionListener(e -> {
            
    String keyword = tfSearch.getText().trim().toLowerCase();
    String type = cbType.getSelectedItem().toString();
    String status = cbStatus.getSelectedItem().toString();

    ArrayList<DiscountDTO> list = discountBUS.getAllDiscounts();

    discountModel.setRowCount(0);

    for(DiscountDTO d : list){

        boolean matchName =
                keyword.isEmpty() ||
                d.getName().toLowerCase().contains(keyword);

        boolean matchType =
                type.equals("ALL TYPE") ||
                d.getDiscountType().name().equals(type);

        boolean matchStatus =
                status.equals("ALL STATUS") ||
                d.getStatus().name().equals(status);

        if(matchName && matchType && matchStatus){

            discountModel.addRow(new Object[]{
                d.getId(),
                d.getName(),
                d.getValue(),
                d.getDiscountType().name(),
                d.getStatus().name()
            });

        }
    }

});
        // Table
        JTable table = new JTable(model);
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

                /* ===== FORMAT VALUE ===== */

                if(d.getDiscountType().name().equals("PERCENT")){
                    lbValue.setText(d.getValue() + " %");
                }else{
                    lbValue.setText(d.getValue() + " VND");
                }

                lbType.setText(d.getDiscountType().name());

                /* ===== MIN ORDER LUÔN VND ===== */

                NumberFormat nf = NumberFormat.getInstance(new Locale("vi","VN"));
                lbMinOrder.setText(nf.format(d.getMinOrderAmount()) + " VND");

                lbStatus.setText(d.getStatus().name());
            }
        }
    }
});
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

   private void showAddDiscountDialog() {

    JDialog dlg = new JDialog(
            SwingUtilities.getWindowAncestor(this),
            "Thêm khuyến mãi",
            Dialog.ModalityType.APPLICATION_MODAL
    );

    dlg.setLayout(new BorderLayout());

    JPanel form = new JPanel(new GridLayout(8,2,10,10));
    form.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));

    JTextField tfName = new JTextField();
    JTextField tfDesc = new JTextField();
    JTextField tfValue = new JTextField();
    JTextField tfMinOrder = new JTextField();

    // ===== Combobox loại giảm =====
    JComboBox<String> cbType = new JComboBox<>(
            new String[]{"PERCENT","FIXED"}
    );
    // ===== Combobox status =====
    JComboBox<String> cbStatus = new JComboBox<>(
        new String[]{"ACTIVE","EXPIRED"}
    );
    // ===== Date chooser =====
    JDateChooser dcStart = new JDateChooser();
    JDateChooser dcEnd = new JDateChooser();

    dcStart.setDateFormatString("dd/MM/yyyy");
    dcEnd.setDateFormatString("dd/MM/yyyy");

    form.add(new JLabel("   Tên khuyến mãi:"));
    form.add(tfName);

    form.add(new JLabel("   Mô tả:"));
    form.add(tfDesc);

    form.add(new JLabel("Giá trị giảm:"));
    form.add(tfValue);


    form.add(new JLabel("Loại giảm:"));
    form.add(cbType);
    
    form.add(new JLabel("Trạng thái:"));
    form.add(cbStatus);
    
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
                cbStatus.getSelectedItem().toString(), // thêm dòng này
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
   private void loadDiscountTables(){

    ArrayList<DiscountDTO> list = discountBUS.getAllDiscounts();

    discountModel.setRowCount(0);

    for(DiscountDTO d : list){

    if(!d.getStatus().name().equals("INACTIVE")){

        discountModel.addRow(new Object[]{
            d.getId(),
            d.getName(),
            d.getValue(),
            d.getDiscountType().name(),
            d.getStatus().name()
        });

    }

}
}
private void showEditDiscountDialog(DiscountDTO d){

    JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Sửa khuyến mãi", true);
    dialog.setSize(400,450);
    dialog.setLocationRelativeTo(this);
    dialog.setLayout(new GridLayout(9,2,10,10));

    JTextField tfName = new JTextField(d.getName());
    JTextField tfDesc = new JTextField(d.getDescription());
    JTextField tfValue = new JTextField(String.valueOf(d.getValue()));
    JTextField tfMinOrder = new JTextField(String.valueOf(d.getMinOrderAmount()));

    JComboBox<String> cbType = new JComboBox<>(new String[]{"PERCENT","FIXED"});
    cbType.setSelectedItem(d.getDiscountType().name());

    JComboBox<String> cbStatus = new JComboBox<>(new String[]{"ACTIVE","EXPIRED"});
    cbStatus.setSelectedItem(d.getStatus().name());

    JDateChooser dcStart = new JDateChooser();
    JDateChooser dcEnd = new JDateChooser();

    dcStart.setDate(java.sql.Date.valueOf(d.getStartDate()));
dcEnd.setDate(java.sql.Date.valueOf(d.getEndDate()));
    JButton btnSave = new JButton("Cập nhật");

    dialog.add(new JLabel("Tên"));
    dialog.add(tfName);

    dialog.add(new JLabel("Mô tả"));
    dialog.add(tfDesc);

    dialog.add(new JLabel("Giá trị"));
    dialog.add(tfValue);

    dialog.add(new JLabel("Loại"));
    dialog.add(cbType);

    dialog.add(new JLabel("Min order"));
    dialog.add(tfMinOrder);

    dialog.add(new JLabel("Trạng thái"));
    dialog.add(cbStatus);

    dialog.add(new JLabel("Ngày bắt đầu"));
    dialog.add(dcStart);

    dialog.add(new JLabel("Ngày kết thúc"));
    dialog.add(dcEnd);

    dialog.add(new JLabel(""));
    dialog.add(btnSave);

    btnSave.addActionListener(e ->{

        String start = new SimpleDateFormat("yyyy-MM-dd").format(dcStart.getDate());
        String end = new SimpleDateFormat("yyyy-MM-dd").format(dcEnd.getDate());

        boolean result = discountBUS.updateDiscount(

        d.getId(),
        tfName.getText().trim(),
        tfDesc.getText().trim(),
        Double.parseDouble(tfValue.getText().trim()),
        cbType.getSelectedItem().toString(),
        start,
        end,
        Double.parseDouble(tfMinOrder.getText().trim()),
        cbStatus.getSelectedItem().toString()

);

        if(result){
            JOptionPane.showMessageDialog(dialog,"Cập nhật thành công");
            loadDiscountTables();
            dialog.dispose();
        }else{
            JOptionPane.showMessageDialog(dialog,"Cập nhật thất bại");
        }

    });

    dialog.setVisible(true);
}
private void clearDetailCard(){

    lbName.setText("Tên khuyến mãi");
    taDesc.setText("");

    lbId.setText("...");
    lbStart.setText("...");
    lbEnd.setText("...");
    lbValue.setText("...");
    lbType.setText("...");
    lbMinOrder.setText("...");
    lbStatus.setText("...");

}
}

