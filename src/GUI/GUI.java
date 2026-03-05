package GUI;

import javax.swing.*;


/**
 * Top-level frame.  Delegates everything to MainPanel.
 */
public class GUI extends JFrame {
    public GUI() {
        setTitle("Siu Thị 36");
        setSize(1440, 1024);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        add(new MainPanel());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GUI frame = new GUI();
            frame.setVisible(true);
        });
    }
}
