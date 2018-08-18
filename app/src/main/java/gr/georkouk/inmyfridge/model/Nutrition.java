package gr.georkouk.inmyfridge.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

@SuppressWarnings("unused")
public class Nutrition {

    @SerializedName("nutrients")
    private List<Nutrient> nutrients;

    @SerializedName("caloricBreakdown")
    private CaloricBreakdown caloricBreakdown;

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
