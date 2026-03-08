import DAO.DBConnection;
import DAO.RoleDAO;
import DAO.EmployeeDAO;

public class TestDB {
    public static void main(String[] args) {
        try {
            System.out.println("Testing database connection...");
            var con = DBConnection.getConnection();
            if (con != null) {
                System.out.println("Connection successful!");
                con.close();
            } else {
                System.out.println("Connection failed!");
            }

            System.out.println("Testing RoleDAO...");
            RoleDAO roleDAO = new RoleDAO();
            var roles = roleDAO.getAllRoles();
            System.out.println("Loaded roles: " + roles.size());
            for (var role : roles) {
                System.out.println("Role: " + role.getName());
            }

            System.out.println("Testing EmployeeDAO...");
            EmployeeDAO empDAO = new EmployeeDAO();
            var employees = empDAO.getAllEmployees();
            System.out.println("Loaded employees: " + employees.size());
            for (var emp : employees) {
                System.out.println("Employee: " + emp.getFullName() + " - Role ID: " + emp.getRoleId());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}