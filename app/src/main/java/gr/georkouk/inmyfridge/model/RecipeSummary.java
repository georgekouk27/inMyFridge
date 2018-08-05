package gr.georkouk.inmyfridge.model;

import com.google.gson.annotations.SerializedName;

public class RecipeSummary {

    @SerializedName("summary")
    private String summary;

    public RecipeSummary() {
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

}
