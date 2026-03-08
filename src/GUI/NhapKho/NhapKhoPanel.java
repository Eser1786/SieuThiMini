package GUI.NhapKho;

import DTO.PurchaseInvoicesDTO;

import javax.swing.*;
import java.awt.*;

/** Panel chính của tab Nhập Kho. */
public class NhapKhoPanel extends JPanel {

    private final NhapKhoTableCard tableCard;

    public NhapKhoPanel() {
        setLayout(new BorderLayout());
        tableCard = new NhapKhoTableCard(this);
        add(tableCard, BorderLayout.CENTER);
    }

    /** Mở popup tạo phiếu nhập kho mới */
    public void openCreatePopup(Window owner) {
        JDialog dlg = new JDialog(owner, "Tạo phiếu nhập kho", Dialog.ModalityType.APPLICATION_MODAL);
        NhapKhoFormCard form = new NhapKhoFormCard(dlg);
        dlg.setContentPane(form);
        dlg.setSize(1200, 750);
        dlg.setMinimumSize(new Dimension(900, 600));
        dlg.setLocationRelativeTo(owner);
        dlg.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dlg.setVisible(true);
        tableCard.refresh();
    }

    /** Mở popup chi tiết phiếu nhập kho */
    public void openDetailPopup(PurchaseInvoicesDTO invoice, Window owner) {
        NhapKhoDetailPopup popup = new NhapKhoDetailPopup(owner, invoice, this);
        popup.setVisible(true);
        tableCard.refresh();
    }

    /** Mở popup sửa phiếu nhập kho (chỉ dành cho PENDING) */
    public void openEditPopup(PurchaseInvoicesDTO invoice, Window owner) {
        JDialog dlg = new JDialog(owner, "Sửa phiếu nhập kho", Dialog.ModalityType.APPLICATION_MODAL);
        NhapKhoFormCard form = new NhapKhoFormCard(dlg, invoice);
        dlg.setContentPane(form);
        dlg.setSize(1200, 750);
        dlg.setMinimumSize(new Dimension(900, 600));
        dlg.setLocationRelativeTo(owner);
        dlg.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dlg.setVisible(true);
        tableCard.refresh();
    }

    /** Refresh bảng lịch sử */
    public void showTable() {
        tableCard.refresh();
    }
}
