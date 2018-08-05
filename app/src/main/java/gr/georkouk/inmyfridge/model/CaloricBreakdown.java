package gr.georkouk.inmyfridge.model;

import com.google.gson.annotations.SerializedName;

public class CaloricBreakdown {

    @SerializedName("percentProtein")
    private double proteinPercent;

    @SerializedName("percentFat")
    private double fatPercent;

    @SerializedName("percentCarbs")
    private double CarbsPercent;

    public CaloricBreakdown() {
    }

    public double getProteinPercent() {
        return proteinPercent;
    }

    public void setProteinPercent(double proteinPercent) {
        this.proteinPercent = proteinPercent;
    }

    public double getFatPercent() {
        return fatPercent;
    }

    public void setFatPercent(double fatPercent) {
        this.fatPercent = fatPercent;
    }

    public double getCarbsPercent() {
        return CarbsPercent;
    }

    public void setCarbsPercent(double carbsPercent) {
        CarbsPercent = carbsPercent;
    }

}
