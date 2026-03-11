package GUI.NhapXuat;

import DTO.PurchaseInvoicesDTO;

import javax.swing.*;
import java.awt.*;


public class NhapXuatPanel extends JPanel {

    private final NhapXuatTableCard tableCard;

    public NhapXuatPanel() {
        setLayout(new BorderLayout());
        tableCard = new NhapXuatTableCard(this);
        add(tableCard, BorderLayout.CENTER);
    }

    
    public void openCreatePopup(Window owner) {
        JDialog dlg = new JDialog(owner, "Tạo phiếu nhập kho", Dialog.ModalityType.APPLICATION_MODAL);
        NhapXuatFormCard form = new NhapXuatFormCard(dlg);
        dlg.setContentPane(form);
        dlg.setSize(1200, 750);
        dlg.setMinimumSize(new Dimension(900, 600));
        dlg.setLocationRelativeTo(owner);
        dlg.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dlg.setVisible(true);
        tableCard.refresh();
    }

    
    public void openDetailPopup(PurchaseInvoicesDTO invoice, Window owner) {
        NhapXuatDetailPopup popup = new NhapXuatDetailPopup(owner, invoice, this);
        popup.setVisible(true);
        tableCard.refresh();
    }

    
    public void openEditPopup(PurchaseInvoicesDTO invoice, Window owner) {
        JDialog dlg = new JDialog(owner, "Sửa phiếu nhập kho", Dialog.ModalityType.APPLICATION_MODAL);
        NhapXuatFormCard form = new NhapXuatFormCard(dlg, invoice);
        dlg.setContentPane(form);
        dlg.setSize(1200, 750);
        dlg.setMinimumSize(new Dimension(900, 600));
        dlg.setLocationRelativeTo(owner);
        dlg.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dlg.setVisible(true);
        tableCard.refresh();
    }

    
    public void showTable() {
        tableCard.refresh();
    }
}
