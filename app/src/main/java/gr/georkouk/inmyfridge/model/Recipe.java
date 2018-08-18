package gr.georkouk.inmyfridge.model;

import com.google.gson.annotations.SerializedName;

@SuppressWarnings("unused")
public class Recipe {

    @SerializedName("id")
    private int id;

    @SerializedName("title")
    private String title;

    @SerializedName("usedIngredientCount")
    private int usedIngredientCount;

    @SerializedName("missedIngredientCount")
    private int missedIngredientCount;

    @SerializedName("likes")
    private int likes;

    @SerializedName("image")
    private String imageUrl;


    public Recipe() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getUsedIngredientCount() {
        return usedIngredientCount;
    }

    public void setUsedIngredientCount(int usedIngredientCount) {
        this.usedIngredientCount = usedIngredientCount;
    }

    public int getMissedIngredientCount() {
        return missedIngredientCount;
    }

    public void setMissedIngredientCount(int missedIngredientCount) {
        this.missedIngredientCount = missedIngredientCount;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

}
