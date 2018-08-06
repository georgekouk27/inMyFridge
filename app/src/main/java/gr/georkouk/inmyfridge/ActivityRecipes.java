package gr.georkouk.inmyfridge;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ActivityRecipes extends AppCompatActivity {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    private RecipesRecAdapter recipesRecAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipes);

        ButterKnife.bind(this);

        if(getIntent().getExtras() != null){
            String ingredients = getIntent().getExtras().getString("ingredients", "");
            String cuisine = getIntent().getExtras().getString("cuisine", "");
            String diet = getIntent().getExtras().getString("diet", "");
            String mealType = getIntent().getExtras().getString("mealType", "");
            String intolerances = getIntent().getExtras().getString("intolerances", "");

            initializeView(ingredients, cuisine, diet, mealType, intolerances);
        }

    }

    private void initializeView(String ingredients, String cuisine, String diet, String mealType, String intolerances){
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
        data.put("includeIngredients", ingredients);
        data.put("cuisine", cuisine);
        data.put("diet", diet);
        data.put("type", mealType);
        data.put("intolerances", intolerances);
        data.put("number", "50");
        data.put("offset", "0");
        data.put("ranking", "2");

        InterfaceApi interfaceApi = RestClient.getClient().create(InterfaceApi.class);

        Call<ResponseRecipes> call = interfaceApi.getRecipes(data);

        call.enqueue(new Callback<ResponseRecipes>() {
            @Override
            public void onResponse(@NonNull Call<ResponseRecipes> call, @NonNull Response<ResponseRecipes> response) {
                progressDialog.dismiss();

                recipesRecAdapter.swapData(response.body().getRecipes());
            }

            @Override
            public void onFailure(@NonNull Call<ResponseRecipes> call, @NonNull Throwable t) {
                progressDialog.dismiss();

                Toast.makeText(ActivityRecipes.this, t.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
