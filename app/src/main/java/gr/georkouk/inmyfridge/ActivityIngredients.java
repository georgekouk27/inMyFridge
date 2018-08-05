package gr.georkouk.inmyfridge;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import butterknife.BindView;
import butterknife.ButterKnife;
import gr.georkouk.inmyfridge.adapter.IngredientsRecAdapter;
import gr.georkouk.inmyfridge.interfaces.InterfaceApi;
import gr.georkouk.inmyfridge.model.Ingredient;
import gr.georkouk.inmyfridge.model.ResponseRecipes;
import gr.georkouk.inmyfridge.network.RestClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ActivityIngredients extends AppCompatActivity {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.btSearchRecipes)
    Button btSearchRecipes;
    private IngredientsRecAdapter ingredientsRecAdapter;
    private FirebaseDatabase database;
    private DatabaseReference ingredientsRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingredients);

        ButterKnife.bind(this);
        initializeView();

        this.database = FirebaseDatabase.getInstance();
        this.ingredientsRef = this.database.getReference("ingredients");

        this.ingredientsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Ingredient> ingredientList = new ArrayList<>();

                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                    ingredientList.add(singleSnapshot.getValue(Ingredient.class));
                }

                ingredientsRecAdapter.swapData(ingredientList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void initializeView(){
        this.recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        this.ingredientsRecAdapter = new IngredientsRecAdapter(this);

        this.recyclerView.setAdapter(this.ingredientsRecAdapter);

        this.btSearchRecipes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<Ingredient> ingredients = ingredientsRecAdapter.getSelectedIngredients();

                StringBuilder ingredientsStr = new StringBuilder();

                for(Ingredient ingredient : ingredients){
                    ingredientsStr.append(ingredient.getName()).append(",");
                }

//                Map<String, String> data = new HashMap<>();
//                data.put("includeIngredients", ingredientsStr.substring(0, ingredientsStr.length() - 1));
//                data.put("number", "5");
//                data.put("offset", "0");
//                data.put("ranking", "2");

                Intent intent = new Intent(ActivityIngredients.this, ActivityRecipes.class);
                intent.putExtra("ingredients", ingredientsStr.substring(0, ingredientsStr.length() - 1));

                startActivity(intent);

//                InterfaceApi interfaceApi = RestClient.getClient().create(InterfaceApi.class);
//
//                Call<ResponseRecipes> call = interfaceApi.getRecipes(data);
//
//                call.enqueue(new Callback<ResponseRecipes>() {
//                    @Override
//                    public void onResponse(@NonNull Call<ResponseRecipes> call, @NonNull Response<ResponseRecipes> response) {
//                        Toast.makeText(ActivityIngredients.this, "" + response.body().getRecipes().size(), Toast.LENGTH_SHORT).show();
//                    }
//
//                    @Override
//                    public void onFailure(@NonNull Call<ResponseRecipes> call, @NonNull Throwable t) {
//                        Toast.makeText(ActivityIngredients.this, t.toString(), Toast.LENGTH_SHORT).show();
//                    }
//                });
            }
        });
    }

}
