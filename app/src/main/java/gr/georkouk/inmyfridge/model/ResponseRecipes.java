package gr.georkouk.inmyfridge.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;


@SuppressWarnings("unused")
public class ResponseRecipes {

    @SerializedName("results")
    private List<Recipe> recipes;

    public ResponseRecipes() {
    }

    public List<Recipe> getRecipes() {
        return recipes;
    }

    public void setRecipes(List<Recipe> recipes) {
        this.recipes = recipes;
    }

}
