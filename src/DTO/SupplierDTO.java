package DTO;

import java.time.LocalDateTime;


public class SupplierDTO {
    private int id;
    private String code;
    private String name;
    private String address;
    private String email;
    private String phone;
    private String contactPerson;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public int getID(){return id;}
    public String getCode(){return code;}
    public String getName(){return name;}
    public String getAddress(){return address;}
    public String getEmail(){return email;}
    public String getPhone(){return phone;}
    public String getContactPerson(){return contactPerson;}
    public LocalDateTime getCreatedAt(){return createdAt;}
    public LocalDateTime getUpdatedAt(){return updatedAt;}

    public void setID(int id){this.id = id;}
    public void setCode(String code){this.code = code;}
    public void setName(String name){this.name = name;}
    public void setAddress(String address){this.address = address;}
    public void setEmail(String email){this.email = email; }
    public void setPhone(String phone){this.phone = phone;}
    public void setContactPerson(String contactPerson){this.contactPerson = contactPerson;}
    public void setCreatedAt(LocalDateTime createdAt){this.createdAt = createdAt;}
    public void setUpdatedAt(LocalDateTime updatedAt){this.updatedAt = updatedAt;}

    @Override
    public String toString(){
         return "CustomerDTO{" + 
                "id=" + id +
                ", code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", email=" + email + '\'' +
                ", phone=" + phone + '\'' +
                ", contact person='" + contactPerson + '\'' +
                ", created at='" + createdAt + '\'' +
                ", updated at=" + updatedAt +
                '}';
    }



}
