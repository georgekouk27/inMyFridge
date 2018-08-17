package gr.georkouk.inmyfridge.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RecipeDetails {

    @SerializedName("id")
    private int id;

    @SerializedName("title")
    private String title;

    @SerializedName("image")
    private String imageUrl;

    @SerializedName("spoonacularSourceUrl")
    private String spoonacularSourceUrl;

    @SerializedName("preparationMinutes")
    private int preparationTime;

    @SerializedName("cookingMinutes")
    private int cookingTime;

    @SerializedName("readyInMinutes")
    private int readyInTime;

    @SerializedName("servings")
    private int servings;

    @SerializedName("extendedIngredients")
    private List<ExtendedIngredient> ingredients;

    @SerializedName("analyzedInstructions")
    private List<Instruction> instructions;

    @SerializedName("nutrition")
    private Nutrition nutrition;


    public RecipeDetails() {
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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getSpoonacularSourceUrl() {
        return spoonacularSourceUrl;
    }

    public void setSpoonacularSourceUrl(String spoonacularSourceUrl) {
        this.spoonacularSourceUrl = spoonacularSourceUrl;
    }

    public int getPreparationTime() {
        return preparationTime;
    }

    public void setPreparationTime(int preparationTime) {
        this.preparationTime = preparationTime;
    }

    public int getCookingTime() {
        return cookingTime;
    }

    public void setCookingTime(int cookingTime) {
        this.cookingTime = cookingTime;
    }

    public int getReadyInTime() {
        return readyInTime;
    }

    public void setReadyInTime(int readyInTime) {
        this.readyInTime = readyInTime;
    }

    public int getServings() {
        return servings;
    }

    public void setServings(int servings) {
        this.servings = servings;
    }

    public List<ExtendedIngredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<ExtendedIngredient> ingredients) {
        this.ingredients = ingredients;
    }

    public List<Instruction> getInstructions() {
        return instructions;
    }

    public void setInstructions(List<Instruction> instructions) {
        this.instructions = instructions;
    }

    public Nutrition getNutrition() {
        return nutrition;
    }

    public void setNutrition(Nutrition nutrition) {
        this.nutrition = nutrition;
    }

}
