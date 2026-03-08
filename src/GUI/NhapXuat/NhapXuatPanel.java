package GUI.NhapXuat;

import javax.swing.*;
import java.awt.*;

/** Panel chính của tab Nhập Kho. */
public class NhapXuatPanel extends JPanel {

    private final NhapXuatTableCard tableCard;

    public NhapXuatPanel() {
        setLayout(new BorderLayout());
        tableCard = new NhapXuatTableCard(this);
        add(tableCard, BorderLayout.CENTER);
    }

    /** Mở popup tạo phiếu nhập kho mới */
    public void openCreatePopup(Window owner) {
        JDialog dlg = new JDialog(owner, "Tạo phiếu nhập kho", Dialog.ModalityType.APPLICATION_MODAL);
        NhapXuatFormCard form = new NhapXuatFormCard(dlg);
        dlg.setContentPane(form);
        dlg.setSize(1200, 750);
        dlg.setMinimumSize(new Dimension(900, 600));
        dlg.setLocationRelativeTo(owner);
        dlg.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dlg.setVisible(true);
        // Refresh table after dialog closes
        tableCard.refresh();
    }

    /** Refresh bảng lịch sử */
    public void showTable() {
        tableCard.refresh();
    }
}
