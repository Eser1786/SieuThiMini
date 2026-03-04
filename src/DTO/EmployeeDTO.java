package DTO;

public class EmployeeDTO {
    private int id;
    private String code;
    private String fullName;
    private String username;
    private String passwordHash;
    private String phone;
    private String email;
    private java.time.LocalDateTime hireDate;
    private java.math.BigDecimal salary;
    private int roleId;       // foreign key to roles

    // getters/setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public java.time.LocalDateTime getHireDate() { return hireDate; }
    public void setHireDate(java.time.LocalDateTime hireDate) { this.hireDate = hireDate; }
    public java.math.BigDecimal getSalary() { return salary; }
    public void setSalary(java.math.BigDecimal salary) { this.salary = salary; }
    public int getRoleId() { return roleId; }
    public void setRoleId(int roleId) { this.roleId = roleId; }

    @Override
    public String toString() {
        return "EmployeeDTO{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", fullName='" + fullName + '\'' +
                ", username='" + username + '\'' +
                ", roleId=" + roleId +
                '}';
    }
}