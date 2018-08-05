package gr.georkouk.inmyfridge.model;

import com.google.gson.annotations.SerializedName;

public class ExtendedIngredient {

    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String name;

    @SerializedName("originalString")
    private String originalString;


    public ExtendedIngredient() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOriginalString() {
        return originalString;
    }

    public void setOriginalString(String originalString) {
        this.originalString = originalString;
    }

}
