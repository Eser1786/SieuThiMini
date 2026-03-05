package DTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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
    public void setId(int id) { this.id = id; }
    public int getId() { return id; }
    public void setCode(String code) { this.code = code; }
    public String getCode() { return code; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getFullName() { return fullName; }
    public void setUsername(String username) { this.username = username; }
    public String getUsername() { return username; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public String getPasswordHash() { return passwordHash; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getPhone() { return phone; }
    public void setEmail(String email) { this.email = email; }
    public String getEmail() { return email; }
    public void setHireDate(LocalDateTime hireDate) { this.hireDate = hireDate; }
    public LocalDateTime getHireDate() { return hireDate; }
    public void setSalary(BigDecimal salary) { this.salary = salary; }
    public BigDecimal getSalary() { return salary; }
    public void setRoleId(int roleId) { this.roleId = roleId; }
    public int getRoleId() { return roleId; }

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