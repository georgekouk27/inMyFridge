package gr.georkouk.inmyfridge;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import butterknife.BindView;
import butterknife.ButterKnife;
import gr.georkouk.inmyfridge.interfaces.InterfaceApi;
import gr.georkouk.inmyfridge.model.ExtendedIngredient;
import gr.georkouk.inmyfridge.model.RecipeDetails;
import gr.georkouk.inmyfridge.model.Step;
import gr.georkouk.inmyfridge.network.RestClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


@SuppressWarnings("UnusedReturnValue")
public class ActivityRecipeDetails extends AppCompatActivity {

    @BindView(R.id.tvRecipeName)
    TextView tvRecipeName;
    @BindView(R.id.tvServings)
    TextView tvServings;
    @BindView(R.id.tvPreparationTime)
    TextView tvPreparationTime;
    @BindView(R.id.tvTotalTime)
    TextView tvTotalTime;
    @BindView(R.id.ivIcon)
    ImageView ivIcon;
    @BindView(R.id.layoutIngredients)
    LinearLayout layoutIngredients;
    @BindView(R.id.layoutSteps)
    LinearLayout layoutSteps;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_details);

        ButterKnife.bind(this);

        if(getIntent().getExtras() != null){
            int id = getIntent().getExtras().getInt("id", 0);

            initializeView(id);
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void initializeView(int id){

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Food is coming....");
        progressDialog.setTitle("Please wait");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();

        InterfaceApi interfaceApi = RestClient.getClient().create(InterfaceApi.class);

        Call<RecipeDetails> call = interfaceApi.getRecipeDetails(id, false);

        call.enqueue(new Callback<RecipeDetails>() {
            @Override
            public void onResponse(@NonNull Call<RecipeDetails> call, @NonNull Response<RecipeDetails> response) {
                progressDialog.dismiss();

                fillView(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<RecipeDetails> call, @NonNull Throwable t) {
                progressDialog.dismiss();

                Toast.makeText(ActivityRecipeDetails.this, t.toString(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private boolean fillView(RecipeDetails recipe){
        if(recipe == null){
            return false;
        }

        tvRecipeName.setText(recipe.getTitle());
        tvServings.setText(String.valueOf(recipe.getServings()));
        tvPreparationTime.setText(String.valueOf(recipe.getPreparationTime()) + " min");
        tvTotalTime.setText(String.valueOf(recipe.getReadyInTime()) + " min");

        RequestOptions options = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .centerCrop()
                .dontTransform()
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher);

        Glide.with(this)
                .load(recipe.getImageUrl())
                .apply(options)
                .into(ivIcon);

        for(ExtendedIngredient ingredient : recipe.getIngredients()){
            TextView textView = new TextView(this);
            textView.setText(ingredient.getOriginalString());
            textView.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT)
            );

            layoutIngredients.addView(textView);
        }

        if(recipe.getInstructions() != null && recipe.getInstructions().size() > 0) {
            for (Step step : recipe.getInstructions().get(0).getSteps()) {
                TextView textView = new TextView(this);
                textView.setText(step.getNumber() + " " + step.getStep());
                textView.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT)
                );

                layoutSteps.addView(textView);
            }
        }

        return true;
    }

}
