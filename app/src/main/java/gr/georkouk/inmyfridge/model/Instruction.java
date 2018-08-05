package gr.georkouk.inmyfridge.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Instruction {

    @SerializedName("steps")
    private List<Step> steps;

    public Instruction() {
    }

    public List<Step> getSteps() {
        return steps;
    }

    public void setSteps(List<Step> steps) {
        this.steps = steps;
    }

}
