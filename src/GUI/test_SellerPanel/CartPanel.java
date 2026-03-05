package GUI.test_SellerPanel;
import javax.swing.*;
import java.awt.*;
public
class CartPanel extends JPanel{

    DefaultListModel<String> cartModel;

    public CartPanel(){

        setPreferredSize(new Dimension(300,0));
        setLayout(new BorderLayout());
        setBackground(new Color(0xAF9FCB));

        JLabel title = new JLabel("ĐƠN HÀNG",SwingConstants.CENTER);
        title.setForeground(Color.WHITE);

        add(title,BorderLayout.NORTH);

        cartModel = new DefaultListModel<>();
        JList<String> cartList = new JList<>(cartModel);

        add(new JScrollPane(cartList),BorderLayout.CENTER);

        JPanel bottom = new JPanel(new GridLayout(4,2));

        bottom.add(new JLabel("TỔNG:"));
        bottom.add(new JLabel("0"));

        bottom.add(new JLabel("GIẢM GIÁ:"));
        bottom.add(new JLabel("0"));

        bottom.add(new JLabel("PHẢI THU:"));
        bottom.add(new JLabel("0"));

        JButton pay = new JButton("THANH TOÁN");

        add(bottom,BorderLayout.SOUTH);
        add(pay,BorderLayout.PAGE_END);
    }
}