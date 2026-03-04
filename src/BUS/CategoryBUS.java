package BUS;

import DTO.CategoryDTO;
import DAO.CategoryDAO;
// import java.sql.*;
import java.util.ArrayList;

// import DAO.CategoryDAO;
public class CategoryBUS {
    private CategoryDAO categoryDAO;
    public CategoryBUS() {
        categoryDAO = new CategoryDAO();
    }
    public ArrayList<CategoryDTO> getAllCategories() {
        return categoryDAO.getAllCategory();
    }
    public boolean addCategory(CategoryDTO category) {
        if(category.getName() == null || category.getName().isEmpty()){
            System.out.println("Tên danh mục không được để trống.");
            return false;
        }
        
        return categoryDAO.addCategory(category);
    }
    
}
