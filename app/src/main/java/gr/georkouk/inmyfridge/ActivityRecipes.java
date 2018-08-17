package gr.georkouk.inmyfridge;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;
import java.util.HashMap;
import java.util.Map;
import butterknife.BindView;
import butterknife.ButterKnife;
import gr.georkouk.inmyfridge.adapter.RecipesRecAdapter;
import gr.georkouk.inmyfridge.interfaces.InterfaceApi;
import gr.georkouk.inmyfridge.model.Recipe;
import gr.georkouk.inmyfridge.model.ResponseRecipes;
import gr.georkouk.inmyfridge.network.RestClient;
import gr.georkouk.inmyfridge.utils.Constants;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ActivityRecipes extends AppCompatActivity {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    private RecipesRecAdapter recipesRecAdapter;
    private Parcelable layoutManagerState;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipes);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        setTitle(R.string.recipes);

        ButterKnife.bind(this);

        if(getIntent().getExtras() != null){
            String ingredients = getIntent().getExtras().getString(Constants.INGREDIENTS, "");
            String cuisine = getIntent().getExtras().getString(Constants.CUISINE, "");
            String diet = getIntent().getExtras().getString(Constants.DIET, "");
            String mealType = getIntent().getExtras().getString(Constants.MEAL_TYPE, "");
            String intolerances = getIntent().getExtras().getString(Constants.INTOLERANCES, "");
            String minCalories = getIntent().getExtras().getString(Constants.MIN_CALORIES, "");
            String maxCalories = getIntent().getExtras().getString(Constants.MAX_CALORIES, "");

            initializeView(ingredients, cuisine, diet, mealType, intolerances, minCalories, maxCalories);
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(
                Constants.LAYOUT_MANAGER_STATE,
                this.recyclerView.getLayoutManager().onSaveInstanceState()
        );

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        this.layoutManagerState = savedInstanceState.getParcelable(Constants.LAYOUT_MANAGER_STATE);
    }

    private void initializeView(String ingredients, String cuisine, String diet, String mealType,
                                String intolerances, String minCalories, String maxCalories){

        this.recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        this.recipesRecAdapter = new RecipesRecAdapter(this);
        this.recipesRecAdapter.setOnItemClickListener(new RecipesRecAdapter.OnItemclickListener() {
            @Override
            public void onReciperClick(Recipe recipe) {
                Intent intent = new Intent(ActivityRecipes.this, ActivityRecipeDetails.class);
                intent.putExtra("id", recipe.getId());

                startActivity(intent);
            }
        });

        this.recyclerView.setAdapter(this.recipesRecAdapter);

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Food is coming....");
        progressDialog.setTitle("Please wait");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();

        Map<String, String> data = new HashMap<>();
        data.put("cuisine", cuisine);
        data.put("diet", diet);
        data.put("type", mealType);
        data.put("intolerances", intolerances);
        data.put("number", "50");
        data.put("offset", "0");
        data.put("ranking", "2");

        if(!ingredients.equals("")){
            data.put("includeIngredients", ingredients);
        }

        if(!minCalories.equals("")){
            data.put("minCalories", minCalories);
        }

        if(!maxCalories.equals("")){
            data.put("maxCalories", maxCalories);
        }

        InterfaceApi interfaceApi = RestClient.getClient().create(InterfaceApi.class);

        Call<ResponseRecipes> call = interfaceApi.getRecipes(data);

        call.enqueue(new Callback<ResponseRecipes>() {
            @Override
            public void onResponse(@NonNull Call<ResponseRecipes> call, @NonNull Response<ResponseRecipes> response) {
                progressDialog.dismiss();

                if(response.body() == null
                        || response.body().getRecipes() == null
                        || response.body().getRecipes().size() == 0){

                    Toast.makeText(
                            ActivityRecipes.this,
                            "Recipes not found",
                            Toast.LENGTH_SHORT
                    ).show();

                    finish();
                }

                recipesRecAdapter.swapData(response.body().getRecipes());

                restoreRecyclerViewState();
            }

            @Override
            public void onFailure(@NonNull Call<ResponseRecipes> call, @NonNull Throwable t) {
                progressDialog.dismiss();

                Toast.makeText(
                        ActivityRecipes.this,
                        "Problem occured",
                        Toast.LENGTH_SHORT
                ).show();

                Log.e(Constants.LOG_STRING, t.toString());

                finish();
            }
        });
    }

    private void restoreRecyclerViewState(){
        if (this.layoutManagerState != null) {
            this.recyclerView.getLayoutManager().onRestoreInstanceState(this.layoutManagerState);
        }
    }

}
