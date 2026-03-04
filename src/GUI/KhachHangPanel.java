package GUI;

import javax.swing.*;
import java.awt.*;

/**
 * Placeholder panel for customer management.  For now it only displays a title.
 * Logic can later be moved here from the original GUI class.
 */
public class KhachHangPanel extends JPanel {
    private CardLayout innerCard;
    private int editingRow = -1;
    private JTextField tfMaKH, tfTen, tfSdt, tfEmail, tfDiaChi, tfDiem, tfTgDK, tfLanCuoiMua, tfTongTien , tfHang, tfTrangThai;

    public static final String CARD_TABLE = "TABLE";
    public KhachHangPanel() {
        innerCard = new CardLayout();
        setLayout(innerCard);

        tfMaKH  = UIUtils.makeField();
        tfTen   = UIUtils.makeField();
        tfSdt   = UIUtils.makeField();
        tfEmail  = UIUtils.makeField();
        tfDiaChi = UIUtils.makeField();
        tfDiem   = UIUtils.makeField();
        tfTgDK   = UIUtils.makeField();
        tfLanCuoiMua   = UIUtils.makeField();
        tfTongTien   = UIUtils.makeField();
        tfHang   = UIUtils.makeField();
        tfTrangThai   = UIUtils.makeField();
        
    }
}