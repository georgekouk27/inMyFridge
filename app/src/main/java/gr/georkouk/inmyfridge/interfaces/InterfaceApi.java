package gr.georkouk.inmyfridge.interfaces;

import java.util.Map;

import gr.georkouk.inmyfridge.BuildConfig;
import gr.georkouk.inmyfridge.model.RecipeDetails;
import gr.georkouk.inmyfridge.model.RecipeSummary;
import gr.georkouk.inmyfridge.model.ResponseRecipes;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;


public interface InterfaceApi {

    @Headers("X-Mashape-Key: " + BuildConfig.API_KEY)
    @GET("recipes/searchComplex")
    Call<ResponseRecipes> getRecipes(@QueryMap Map<String, String> options);

    @Headers("X-Mashape-Key: " + BuildConfig.API_KEY)
    @GET("recipes/{id}/information")
    Call<RecipeDetails> getRecipeDetails(@Path("id") int id, @Query("includeNutrition") boolean includeNutrition);

    @Headers("X-Mashape-Key: " + BuildConfig.API_KEY)
    @GET("recipes/{id}/summary")
    Call<RecipeSummary> getRecipeSummary(@Path("id") int id);

}
