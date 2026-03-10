package GUI.NhapXuat;

import BUS.EmployeeBUS;
import BUS.ProductBUS;
import BUS.PurchaseInvoicesBUS;
import BUS.SupplierBUS;
import DTO.*;
import DTO.enums.PurchaseInvoicesEnum.PurchaseInvoicesStatus;

import com.toedter.calendar.JDateChooser;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/** Form t?o / s?a phi?u nh?p kho. Layout 2 c?t gi?ng DonHangCreateCard. */
public class NhapXuatFormCard extends JPanel {

    private final Window dialogOwner;
    private final PurchaseInvoicesDTO editInvoice;

    public NhapXuatFormCard(Window dialogOwner) {
        this(dialogOwner, null);
    }

    public NhapXuatFormCard(Window dialogOwner, PurchaseInvoicesDTO existing) {
        this.dialogOwner = dialogOwner;
        this.editInvoice = existing;

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Simple UI for now
        add(new JLabel("Form phiếu nhập kho - đang phát triển"), BorderLayout.CENTER);

        JButton btnClose = new JButton("Đóng");
        btnClose.addActionListener(e -> {
            if (dialogOwner instanceof JDialog) {
                ((JDialog) dialogOwner).dispose();
            }
        });
        add(btnClose, BorderLayout.SOUTH);
    }
}


