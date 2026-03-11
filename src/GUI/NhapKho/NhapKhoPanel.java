package GUI.NhapKho;

import DTO.PurchaseInvoicesDTO;

import javax.swing.*;
import java.awt.*;


public class NhapKhoPanel extends JPanel {

    private final NhapKhoTableCard tableCard;

    public NhapKhoPanel() {
        setLayout(new BorderLayout());
        tableCard = new NhapKhoTableCard(this);
        add(tableCard, BorderLayout.CENTER);
    }

    
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

    
    public void openDetailPopup(PurchaseInvoicesDTO invoice, Window owner) {
        NhapKhoDetailPopup popup = new NhapKhoDetailPopup(owner, invoice, this);
        popup.setVisible(true);
        tableCard.refresh();
    }

    
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

    
    public void showTable() {
        tableCard.refresh();
    }
}
