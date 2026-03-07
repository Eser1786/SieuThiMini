package GUI.NhapXuat;

import javax.swing.*;
import java.awt.*;

/**
 * Panel chính của tab Nhập Xuất Kho.
 * Dùng CardLayout để chuyển giữa bảng lịch sử (TABLE) và form tạo phiếu (FORM).
 */
public class NhapXuatPanel extends JPanel {

    public static final String CARD_TABLE = "TABLE";
    public static final String CARD_FORM  = "FORM";

    private final CardLayout innerCard = new CardLayout();
    private final JPanel cards = new JPanel(innerCard);

    private final NhapXuatTableCard tableCard;
    private NhapXuatFormCard formCard;

    public NhapXuatPanel() {
        setLayout(new BorderLayout());
        tableCard = new NhapXuatTableCard(this);
        cards.add(tableCard, CARD_TABLE);
        add(cards, BorderLayout.CENTER);
        innerCard.show(cards, CARD_TABLE);
    }

    /** Chuyển sang form tạo phiếu mới */
    public void showForm() {
        // Tạo lại form mỗi lần để load dữ liệu mới nhất
        if (formCard != null) cards.remove(formCard);
        formCard = new NhapXuatFormCard(this);
        cards.add(formCard, CARD_FORM);
        innerCard.show(cards, CARD_FORM);
    }

    /** Quay lại bảng lịch sử và refresh dữ liệu */
    public void showTable() {
        tableCard.refresh();
        innerCard.show(cards, CARD_TABLE);
    }
}
