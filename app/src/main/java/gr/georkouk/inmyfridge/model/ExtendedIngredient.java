package gr.georkouk.inmyfridge.model;

import com.google.gson.annotations.SerializedName;

@SuppressWarnings("unused")
public class ExtendedIngredient {

    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String name;

    @SerializedName("originalString")
    private String originalString;

    @SerializedName("amount")
    private double amount;

    @SerializedName("unit")
    private String unit;


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

}
