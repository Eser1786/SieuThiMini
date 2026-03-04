package BUS;

import DAO.ProductDAO;
import DTO.ProductDTO;
import java.util.ArrayList;

public class ProductBUS {
    private ProductDAO dao;
    public ProductBUS(){
        dao = new ProductDAO();
    }
    public ArrayList<ProductDTO> getAllProducts(){
        return dao.getAllProducts();
    }
    public boolean addProduct(ProductDTO p){
        if(p.getName()==null || p.getName().isEmpty()){
            System.out.println("Tên sản phẩm không được trống");
            return false;
        }
        return dao.addProduct(p);
    }
}
