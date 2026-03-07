package GUI.Kho;

import javax.swing.JPanel;
import java.awt.BorderLayout;

public class KhoPanel extends JPanel {
    public KhoPanel() {
        super(new BorderLayout());
        add(new KhoTableCard(), BorderLayout.CENTER);
    }
}
