package GUI.KhuyenMai;
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
    // ===== Detail fields =====
    private JLabel lbName;
    private JTextArea taDesc;

    private JLabel lbId;
    private JLabel lbStart;
    private JLabel lbEnd;
    private JLabel lbValue;
    private JLabel lbType;
    private JLabel lbMinOrder;
    private static final Color PAGE_BG   = new Color(0xF8F7FF);
    private static final Color RIGHT_BG  = new Color(0x5C4A7F);
    private static final Color CARD_LEFT = new Color(0xD1C4E9);
    private static final Color TBL_HDR   = new Color(0x3D2F5C);
    private static final Color BTN_ADD   = new Color(0x3D2F5C);
    
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
        
        JScrollPane leftScroll = new JScrollPane(buildLeftColumn());
        leftScroll.setBorder(null);
        leftScroll.getVerticalScrollBar().setUnitIncrement(16);
        gc.gridx = 0; gc.weightx = 0.75; gc.insets = new Insets(0, 0, 0, 14);
        body.add(leftScroll, gc);

        JPanel rightCol = buildRightColumn();
        gc.gridx = 1; gc.weightx = 0.25; gc.insets = new Insets(0, 0, 0, 0);
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
    private JPanel buildRightColumn() {

    JPanel col = new JPanel();
    col.setLayout(new BoxLayout(col, BoxLayout.Y_AXIS));
    col.setBackground(RIGHT_BG);
    col.setBorder(BorderFactory.createEmptyBorder(16,16,16,16));

    discountModel = new DefaultTableModel(
        new String[]{"ID", "Tên khuyến mãi", "Giá trị", "Loại giảm"},0
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
        btnRow.add(listBtn("Refresh", new Color(0x388E3C)));
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
                lbValue.setText(String.valueOf(d.getValue()));
                lbType.setText(d.getDiscountType().name());
                lbMinOrder.setText(String.valueOf(d.getMinOrderAmount()));

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
   private void loadDiscountTables(){

    ArrayList<DiscountDTO> list = discountBUS.getAllDiscounts();

    discountModel.setRowCount(0);

    for(DiscountDTO d : list){

        discountModel.addRow(new Object[]{
            d.getId(),
            d.getName(),
            d.getValue(),
            d.getDiscountType().name()
        });

    }
}
}

