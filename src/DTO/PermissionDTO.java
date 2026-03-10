package DTO;

public class PermissionDTO {
    private Long permissionID;
    private String permissionName;
    private String description;

    public Long getPermissionID(){return permissionID;}
    public String getPermissionName(){return permissionName;}
    public String getDescription(){return description;}

    public void setPermissionID(Long permissionID){this.permissionID = permissionID;}
    public void setPermissionName(String permissionName){this.permissionName = permissionName;}
    public void setDescription(String description){this.description = description;}
}
