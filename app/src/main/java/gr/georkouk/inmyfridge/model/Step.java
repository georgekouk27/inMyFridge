package gr.georkouk.inmyfridge.model;

import com.google.gson.annotations.SerializedName;

public class Step {

    @SerializedName("number")
    private int number;

    @SerializedName("step")
    private String step;

    public Step() {
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getStep() {
        return step;
    }

    public void setStep(String step) {
        this.step = step;
    }

}
