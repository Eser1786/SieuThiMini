package GUI.NhapXuat;

import BUS.PurchaseInvoicesBUS;
import DAO.PurchaseInvoicesDAO;
import DTO.PurchaseInvoicesDTO;
import DTO.PurchaseInvoiceItemsDTO;
import DTO.enums.PurchaseInvoicesEnum.PurchaseInvoicesStatus;
import GUI.UIUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;

/** Popup chi tiết phiếu nhập kho với khả năng xác nhận nhập kho */
public class NhapXuatDetailPopup extends JDialog {

    private final PurchaseInvoicesDTO invoice;
    private final NhapXuatPanel parent;

    public NhapXuatDetailPopup(Window owner, PurchaseInvoicesDTO invoice, NhapXuatPanel parent) {
        super(owner, "Chi tiết phiếu nhập kho", ModalityType.APPLICATION_MODAL);
        this.invoice = invoice;
        this.parent = parent;

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(0xF8F7FF));
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0xDEE2E6)),
                BorderFactory.createEmptyBorder(16, 20, 16, 20)));

        JLabel title = new JLabel("Chi tiết phiếu nhập kho: " + invoice.getInvoiceCode());
        title.setFont(new Font("Arial", Font.BOLD, 18));
        title.setForeground(new Color(0x37474F));
        header.add(title, BorderLayout.WEST);

        JButton btnClose = new JButton("×");
        btnClose.setFont(new Font("Arial", Font.BOLD, 20));
        btnClose.setForeground(new Color(0x666666));
        btnClose.setBackground(new Color(0xF8F7FF));
        btnClose.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        btnClose.setFocusPainted(false);
        btnClose.addActionListener(e -> dispose());
        header.add(btnClose, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);

        // Body
        JPanel body = new JPanel(new GridBagLayout());
        body.setBackground(Color.WHITE);
        body.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(8, 8, 8, 8);
        g.anchor = GridBagConstraints.WEST;

        Font labelFont = new Font("Arial", Font.BOLD, 14);
        Font valueFont = new Font("Arial", Font.PLAIN, 14);

        // Row 1: Mã phiếu, Ngày nhập
        g.gridy = 0;
        body.add(createLabel("Mã phiếu:", labelFont), g);
        g.gridx = 1;
        body.add(createValue(invoice.getInvoiceCode(), valueFont), g);
        g.gridx = 2;
        body.add(createLabel("Ngày nhập:", labelFont), g);
        g.gridx = 3;
        body.add(createValue(invoice.getDateIn() != null ? invoice.getDateIn().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : "-", valueFont), g);

        // Row 2: Nhà cung cấp, Nhân viên
        g.gridy = 1; g.gridx = 0;
        body.add(createLabel("Nhà cung cấp:", labelFont), g);
        g.gridx = 1;
        body.add(createValue(invoice.getSupplierName() != null ? invoice.getSupplierName() : "-", valueFont), g);
        g.gridx = 2;
        body.add(createLabel("Nhân viên:", labelFont), g);
        g.gridx = 3;
        body.add(createValue(invoice.getEmployeeName() != null ? invoice.getEmployeeName() : "-", valueFont), g);

        // Row 3: Tổng tiền, Trạng thái
        g.gridy = 2; g.gridx = 0;
        body.add(createLabel("Tổng tiền:", labelFont), g);
        g.gridx = 1;
        body.add(createValue(invoice.getTotalAmount() != null ? String.format("%,.0f đ", invoice.getTotalAmount()) : "-", valueFont), g);
        g.gridx = 2;
        body.add(createLabel("Trạng thái:", labelFont), g);
        g.gridx = 3;
        String statusDisplay = switch (invoice.getStatus()) {
            case PurchaseInvoicesStatus.PENDING -> "Chờ xác nhận";
            case PurchaseInvoicesStatus.RECEIVED -> "Đã nhập";
            case PurchaseInvoicesStatus.CANCELLED -> "Đã hủy";
            default -> "-";
        };
        body.add(createValue(statusDisplay, valueFont), g);

        // Items table
        g.gridy = 3; g.gridx = 0; g.gridwidth = 4; g.fill = GridBagConstraints.BOTH; g.weightx = 1.0; g.weighty = 1.0;
        body.add(createItemsTable(), g);

        add(body, BorderLayout.CENTER);

        // Footer with buttons
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footer.setBackground(Color.WHITE);
        footer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(0xDEE2E6)),
                BorderFactory.createEmptyBorder(16, 20, 16, 20)));

        JButton btnCancel = new JButton("Đóng");
        btnCancel.setFont(new Font("Arial", Font.BOLD, 14));
        btnCancel.setBackground(new Color(0x6C757D));
        btnCancel.setForeground(Color.WHITE);
        btnCancel.setFocusPainted(false);
        btnCancel.addActionListener(e -> dispose());

        JButton btnConfirm = new JButton("Xác nhận nhập kho");
        btnConfirm.setFont(new Font("Arial", Font.BOLD, 14));
        btnConfirm.setBackground(new Color(0x28A745));
        btnConfirm.setForeground(Color.WHITE);
        btnConfirm.setFocusPainted(false);

        // Only show confirm button if status is PENDING
        if (invoice.getStatus() == PurchaseInvoicesStatus.PENDING) {
            btnConfirm.addActionListener(e -> confirmReceipt());
            footer.add(btnConfirm);
        }

        footer.add(btnCancel);
        add(footer, BorderLayout.SOUTH);

        setSize(900, 600);
        setLocationRelativeTo(owner);
    }

    private JLabel createLabel(String text, Font font) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        return label;
    }

    private JLabel createValue(String text, Font font) {
        JLabel value = new JLabel(text);
        value.setFont(font);
        value.setForeground(new Color(0x495057));
        return value;
    }

    private JScrollPane createItemsTable() {
        String[] columns = {"STT", "Mã sản phẩm", "Tên sản phẩm", "Số lượng", "Đơn giá", "Thành tiền"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        if (invoice.getItems() != null) {
            int stt = 1;
            for (PurchaseInvoiceItemsDTO item : invoice.getItems()) {
                BigDecimal lineTotal = item.getSubtotal() != null ? item.getSubtotal() :
                    (item.getUnitPrice() != null && item.getQuantity() != null ?
                     item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())) : BigDecimal.ZERO);

                model.addRow(new Object[]{
                    stt++,
                    item.getProductCode() != null ? item.getProductCode() : "-",
                    item.getProductName() != null ? item.getProductName() : "-",
                    item.getQuantity(),
                    item.getUnitPrice() != null ? String.format("%,.0f đ", item.getUnitPrice()) : "-",
                    String.format("%,.0f đ", lineTotal)
                });
            }
        }

        JTable table = new JTable(model);
        table.setFont(new Font("Arial", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        table.setRowHeight(30);
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(true);
        table.setGridColor(new Color(0xEEEEEE));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(850, 200));
        return scrollPane;
    }

    private void confirmReceipt() {
        int result = JOptionPane.showConfirmDialog(this,
            "Xác nhận đã nhập kho phiếu " + invoice.getInvoiceCode() + "?\n" +
            "Hành động này sẽ cập nhật trạng thái thành 'Đã nhập' và không thể hoàn tác.",
            "Xác nhận nhập kho",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);

        if (result == JOptionPane.YES_OPTION) {
            PurchaseInvoicesDAO dao = new PurchaseInvoicesDAO();
            boolean success = dao.updateStatus(invoice.getInvoiceId(), "RECEIVED");

            if (success) {
                JOptionPane.showMessageDialog(this,
                    "Đã xác nhận nhập kho thành công!",
                    "Thành công",
                    JOptionPane.INFORMATION_MESSAGE);

                // Update local invoice status
                invoice.setStatus(PurchaseInvoicesStatus.RECEIVED);

                // Refresh parent table
                parent.showTable();

                // Close dialog
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                    "Có lỗi xảy ra khi cập nhật trạng thái.",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}