package GUI.test_SellerPanel;

import javax.swing.*;
import java.awt.*;

public class ProductPanel extends JPanel {

    JPanel productGrid;

    public ProductPanel(){

        setLayout(new BorderLayout());
        setBackground(new Color(0xF3F0FF));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.setBackground(new Color(0xF3F0FF));

        JComboBox<String> cbCategory = new JComboBox<>();
        cbCategory.addItem("Đồ ăn vặt");

        JTextField txtSearch = new JTextField(20);

        top.add(cbCategory);
        top.add(txtSearch);

        add(top,BorderLayout.NORTH);

        productGrid = new JPanel();
        productGrid.setLayout(new GridLayout(0,3,15,15));
        productGrid.setBackground(new Color(0xF3F0FF));
        productGrid.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        JScrollPane scroll = new JScrollPane(productGrid);
        scroll.setBorder(null);

        add(scroll,BorderLayout.CENTER);

        loadProducts();
    }

    void loadProducts(){

        for(int i=0;i<9;i++){
            productGrid.add(createProductCard(
                    "Kẹo Trident",
                    29300,
                    "img/trident.png"
            ));
        }
    }

    JPanel createProductCard(String name,double price,String imgPath){

        JPanel card = new JPanel();
        card.setLayout(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createLineBorder(new Color(0xE0E0E0)));

        JLabel img = new JLabel();
        img.setHorizontalAlignment(SwingConstants.CENTER);

        ImageIcon icon = new ImageIcon(imgPath);
        Image scale = icon.getImage().getScaledInstance(60,60,Image.SCALE_SMOOTH);
        img.setIcon(new ImageIcon(scale));

        JLabel lbName = new JLabel(name,SwingConstants.CENTER);
        JLabel lbPrice = new JLabel("Giá tiền: "+price,SwingConstants.CENTER);

        JButton btnAdd = new JButton("Thêm");
        btnAdd.setBackground(new Color(0xAF9FCB));
        btnAdd.setForeground(Color.WHITE);

        JPanel bottom = new JPanel(new GridLayout(3,1));
        bottom.add(lbName);
        bottom.add(lbPrice);
        bottom.add(btnAdd);

        card.add(img,BorderLayout.NORTH);
        card.add(bottom,BorderLayout.CENTER);

        return card;
    }
}