package GUI.test_SellerPanel;

import javax.swing.*;
import java.awt.*;

public class POSFrame extends JFrame {

    public POSFrame() {

        setTitle("POS - Siêu Thị Mini");
        setSize(1200,700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        add(new SidebarPanel(), BorderLayout.WEST);
        add(new ProductPanel(), BorderLayout.CENTER);
        add(new CartPanel(), BorderLayout.EAST);

        setVisible(true);
    }

    public static void main(String[] args) {
        new POSFrame();
    }
}