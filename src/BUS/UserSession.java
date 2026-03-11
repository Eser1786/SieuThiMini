package BUS;

import DTO.EmployeeDTO;


public class UserSession {
    private static EmployeeDTO currentUser = null;

    public static void setCurrentUser(EmployeeDTO user) {
        currentUser = user;
    }

    public static EmployeeDTO getCurrentUser() {
        return currentUser;
    }

    public static void logout() {
        currentUser = null;
    }

    public static boolean isLoggedIn() {
        return currentUser != null;
    }

    public static boolean hasPermission(String permission) {
        if (currentUser == null) return false;
        EmployeeBUS empBUS = new EmployeeBUS();
        return empBUS.hasPermission((long) currentUser.getId(), permission);
    }
}