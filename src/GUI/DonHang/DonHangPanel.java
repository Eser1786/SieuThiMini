package GUI.DonHang;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.Dialog;

/**
 * Panel quản lý đơn hàng.
 * Orchestrates 4 inner cards via CardLayout:
 *   TABLE → DETAIL → INVOICE → CREATE
 *
 * Each card lives in its own class:
 *   DonHangTableCard, DonHangDetailCard, DonHangInvoiceCard, DonHangCreateCard
 */
public class DonHangPanel extends JPanel {

    static final String CARD_TABLE   = "TABLE";
    static final String CARD_DETAIL  = "DETAIL";
    static final String CARD_INVOICE = "INVOICE";
    static final String CARD_CREATE  = "CREATE";

    private final CardLayout innerCard = new CardLayout();

    DefaultTableModel tableModel;
    int currentRow = -1;
    final java.util.HashMap<String, String> nhanVienMap = new java.util.HashMap<>();
    final java.util.HashMap<String, String> timeMap    = new java.util.HashMap<>();
    final java.util.HashMap<String, OrderDetailData> orderDataMap = new java.util.HashMap<>();

    static class OrderDetailData {
        String ten = "", phone = "", diaChi = "", payMethod = "", notes = "", maKM = "", time = "";
        long discAmt = 0;
        final java.util.List<Item> items = new java.util.ArrayList<>();
        static class Item { String code = "", name = ""; long unitPrice; int qty; }
    }

    private final DonHangDetailCard  detailCard;
    private final DonHangInvoiceCard invoiceCard;
    JDialog createDialog = null;

    public DonHangPanel() {
        setLayout(innerCard);
        DonHangTableCard  tableCardPanel  = new DonHangTableCard(this);
        detailCard  = new DonHangDetailCard(this);
        invoiceCard = new DonHangInvoiceCard(this);
        DonHangCreateCard createCardPanel = new DonHangCreateCard(this);

        add(tableCardPanel,  CARD_TABLE);
        add(detailCard,      CARD_DETAIL);
        add(invoiceCard,     CARD_INVOICE);
        add(createCardPanel, CARD_CREATE);
        innerCard.show(this, CARD_TABLE);
    }

    void showCard(String card) {
        if (CARD_TABLE.equals(card) && createDialog != null && createDialog.isVisible()) {
            createDialog.dispose();
            createDialog = null;
            return;
        }
        innerCard.show(this, card);
    }

    void openCreatePopup(java.awt.Window owner) {
        if (createDialog != null && createDialog.isVisible()) {
            createDialog.toFront();
            return;
        }
        createDialog = new JDialog(owner,
                "+ T\u1ea1o \u0111\u01a1n h\u00e0ng m\u1edbi",
                Dialog.ModalityType.APPLICATION_MODAL);
        DonHangCreateCard card = new DonHangCreateCard(this);
        createDialog.setContentPane(card);
        createDialog.setSize(1280, 780);
        createDialog.setMinimumSize(new Dimension(960, 620));
        createDialog.setLocationRelativeTo(owner);
        createDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        createDialog.setVisible(true);
    }

    void showDetail(int modelRow) {
        currentRow = modelRow;
        detailCard.loadDetail(modelRow);
        showCard(CARD_DETAIL);
    }

    void showInvoice(int modelRow) {
        currentRow = modelRow;
        invoiceCard.loadInvoice(modelRow);
        showCard(CARD_INVOICE);
    }

    /** Shared button factory used by all cards */
    static JButton makeFootBtn(String text, Color bg) {
        JButton b = new JButton(text);
        b.setFont(new Font("Arial", Font.BOLD, 14));
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setPreferredSize(new Dimension(180, 42));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }
}
