package gr.georkouk.inmyfridge.model;

public class Ingredient {

    private String name;
    private String image;
    private boolean isSelected;

    public Ingredient() {
        this.isSelected = false;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getImageUrl(){
        return "https://firebasestorage.googleapis.com/v0/b/inmyfridge-aed4a.appspot.com/o/images%2F" +
                getImage() +
                "?alt=media";
    }

}
