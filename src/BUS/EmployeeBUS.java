package BUS;

import DAO.EmployeeDAO;
import DTO.EmployeeDTO;
import java.util.ArrayList;

public class EmployeeBUS {
    private EmployeeDAO dao;
    public EmployeeBUS(){
        dao = new EmployeeDAO();
    }
    public ArrayList<EmployeeDTO> getAllEmployees(){
        return dao.getAllEmployees();
    }
    public boolean addEmployee(EmployeeDTO emp){
        if(emp.getFullName()==null || emp.getFullName().isEmpty()){
            System.out.println("Tên nhân viên không được trống");
            return false;
        }
        if(emp.getUsername()==null || emp.getUsername().isEmpty()){
            System.out.println("Username không được trống");
            return false;
        }
        return dao.addEmployee(emp);
    }
}
