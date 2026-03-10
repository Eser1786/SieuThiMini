package DTO;

public class CategoryDTO {
    private int id;
    private String name;
    private String description;

    public int getID(){return id;}
    public String getName(){return name;}
    public String getDescription(){return description;}

    public void setID(int id){this.id = id;}
    public void setName(String name){this.name = name;}
    public void setDescription(String description){this.description = description;}

    @Override
    public String toString(){
        return "CategoryDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\''  +
                '}';
    }
}
