package gr.georkouk.inmyfridge.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Nutrition {

    @SerializedName("nutrients")
    List<Nutrient> nutrients;

    @SerializedName("caloricBreakdown")
    CaloricBreakdown caloricBreakdown;

    public Nutrition() {
    }

    public List<Nutrient> getNutrients() {
        return nutrients;
    }

    public void setNutrients(List<Nutrient> nutrients) {
        this.nutrients = nutrients;
    }

    public CaloricBreakdown getCaloricBreakdown() {
        return caloricBreakdown;
    }

    public void setCaloricBreakdown(CaloricBreakdown caloricBreakdown) {
        this.caloricBreakdown = caloricBreakdown;
    }

}
