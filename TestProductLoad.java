import GUI.SanPham.SanPhamPanel;
import javax.swing.table.DefaultTableModel;

public class TestProductLoad {
    public static void main(String[] args) {
        SanPhamPanel panel = new SanPhamPanel();
        DefaultTableModel model = panel.productModel;
        System.out.println("Rows: " + model.getRowCount());
        if (model.getRowCount()>0) {
            Object cat = model.getValueAt(0,7);
            System.out.println("First product category cell = " + cat);
        }
    }
}
