package gr.georkouk.inmyfridge.model;

public class DrawerItem {

    private String id;
    private String name;


    public DrawerItem(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public DrawerItem() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}