package gr.georkouk.inmyfridge.model;

import com.google.gson.annotations.SerializedName;

@SuppressWarnings("unused")
public class Nutrient {

    @SerializedName("title")
    private String title;

    @SerializedName("amount")
    private double amount;

    @SerializedName("unit")
    private String unit;

    @SerializedName("percentOfDailyNeeds")
    private double dailyPercent;

    public Nutrient() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public double getDailyPercent() {
        return dailyPercent;
    }

    public void setDailyPercent(double dailyPercent) {
        this.dailyPercent = dailyPercent;
    }

}
