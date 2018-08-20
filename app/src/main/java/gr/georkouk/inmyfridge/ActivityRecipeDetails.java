package gr.georkouk.inmyfridge;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import java.io.IOException;
import java.util.Locale;
import butterknife.BindView;
import butterknife.ButterKnife;
import gr.georkouk.inmyfridge.interfaces.InterfaceApi;
import gr.georkouk.inmyfridge.model.ExtendedIngredient;
import gr.georkouk.inmyfridge.model.Nutrient;
import gr.georkouk.inmyfridge.model.RecipeDetails;
import gr.georkouk.inmyfridge.model.RecipeSummary;
import gr.georkouk.inmyfridge.model.Step;
import gr.georkouk.inmyfridge.network.RestClient;
import gr.georkouk.inmyfridge.utils.Constants;
import gr.georkouk.inmyfridge.utils.ExpandableTextview;
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
    @BindView(R.id.layoutNutrients)
    LinearLayout layoutNutrients;
    @BindView(R.id.tvProteinPercent)
    TextView tvProteinPercent;
    @BindView(R.id.tvFatPercent)
    TextView tvFatPercent;
    @BindView(R.id.tvCarbsPercent)
    TextView tvCarbsPercent;
    @BindView(R.id.tvRecipeSummary)
    ExpandableTextview tvRecipeSummary;
    @BindView(R.id.layoutRoot)
    LinearLayout layoutRoot;
    @BindView(R.id.nestedScrollView)
    NestedScrollView nestedScrollView;
    private int[] scrollPosition;
    private RecipeDetails recipe;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_details);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        setTitle(R.string.recipeDetails);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        ButterKnife.bind(this);

        if(savedInstanceState != null) {
            scrollPosition = savedInstanceState.getIntArray(Constants.NESTED_SCROLLVIEW_STATE);
        }

        if(getIntent().getExtras() != null){
            int id = getIntent().getExtras().getInt("id", 0);

            initializeView(id);
        }
        else{
            finish();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putIntArray(
                Constants.NESTED_SCROLLVIEW_STATE,
                new int[]{ nestedScrollView.getScrollX(), nestedScrollView.getScrollY() }
        );
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.details_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.btShare) {
            if(!recipe.getSpoonacularSourceUrl().equals("")) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);

                shareIntent.setType("text/plain");

                shareIntent.putExtra(
                        Intent.EXTRA_TEXT,
                        recipe.getSpoonacularSourceUrl());

                startActivity(Intent.createChooser(shareIntent, getString(R.string.share1)));
            }
            else{
                Toast.makeText(
                        this,
                        getString(R.string.noRecipeUrl),
                        Toast.LENGTH_SHORT
                ).show();
            }
        }
        else if(item.getItemId() == android.R.id.home){
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    private void initializeView(int id){
        layoutRoot.setVisibility(View.INVISIBLE);

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.foodIsComing));
        progressDialog.setTitle(getString(R.string.pleaseWait));
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();

        InterfaceApi interfaceApi = RestClient.getClient().create(InterfaceApi.class);

        Call<RecipeDetails> call = interfaceApi.getRecipeDetails(id, true);

        call.enqueue(new Callback<RecipeDetails>() {
            @Override
            public void onResponse(@NonNull Call<RecipeDetails> call, @NonNull Response<RecipeDetails> response) {
                progressDialog.dismiss();

                if(response.body() == null){
                    showErrorLoadingMessage();

                    finish();
                }
                else {
                    fillView(response.body());
                }
            }

            @Override
            public void onFailure(@NonNull Call<RecipeDetails> call, @NonNull Throwable t) {
                progressDialog.dismiss();

                Log.e(Constants.LOG_STRING, t.toString());

                showErrorLoadingMessage();
            }
        });

        AsyncInterface asyncInterface = new AsyncInterface() {
            @Override
            public void onAsyncFinished(String result) {
                if(!result.equals("")) {
                    tvRecipeSummary.setText(Html.fromHtml(result));
                    tvRecipeSummary.setVisibility(View.VISIBLE);
                }
                else{
                    tvRecipeSummary.setVisibility(View.GONE);
                }
            }
        };

        new GetRecipeSummary(asyncInterface).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, id);
    }

    private boolean fillView(RecipeDetails recipe){
        if(recipe == null){
            return false;
        }

        this.recipe = recipe;

        layoutRoot.setVisibility(View.VISIBLE);

        tvRecipeName.setText(recipe.getTitle());
        tvServings.setText(String.valueOf(recipe.getServings()));

        String prep = recipe.getPreparationTime() + " " + getString(R.string.min);
        tvPreparationTime.setText(prep);

        String total = recipe.getReadyInTime() + " " + getString(R.string.min);
        tvTotalTime.setText(total);

        RequestOptions options = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .centerCrop()
                .dontTransform()
                .placeholder(R.drawable.food)
                .error(R.drawable.food);

        Glide.with(this)
                .load(recipe.getImageUrl())
                .apply(options)
                .into(ivIcon);

        showIngredients(recipe);

        showInstructions(recipe);

        showNutrients(recipe);

        tvProteinPercent.setText(
                String.valueOf(recipe.getNutrition().getCaloricBreakdown().getProteinPercent())
        );

        tvFatPercent.setText(
                String.valueOf(recipe.getNutrition().getCaloricBreakdown().getFatPercent())
        );

        tvCarbsPercent.setText(
                String.valueOf(recipe.getNutrition().getCaloricBreakdown().getCarbsPercent())
        );

        if (scrollPosition != null) {
            nestedScrollView.post(new Runnable() {
                public void run() {
                    nestedScrollView.scrollTo(scrollPosition[0], scrollPosition[1]);
                }
            });
        }

        return true;
    }

    private void showErrorLoadingMessage(){
        Toast.makeText(
                ActivityRecipeDetails.this,
                getString(R.string.errorLoadingRecipe),
                Toast.LENGTH_SHORT
        ).show();
    }

    @SuppressLint("StaticFieldLeak")
    private class GetRecipeSummary extends AsyncTask<Integer, Integer, String> {

        private AsyncInterface asyncInterface;

        GetRecipeSummary(AsyncInterface asyncInterface){
            this.asyncInterface = asyncInterface;
        }

        @Override
        protected String doInBackground(Integer... data) {
            InterfaceApi interfaceApi = RestClient.getClient().create(InterfaceApi.class);

            String summary = "";
            Call<RecipeSummary> call = interfaceApi.getRecipeSummary(data[0]);
            try {
                Response<RecipeSummary> response = call.execute();

                if(response != null
                        && response.body() != null) {

                    summary = response.body().getSummary();
                }
            }
            catch (IOException e) {
                e.printStackTrace();

                Log.e(Constants.LOG_STRING, getString(R.string.errorLoadingRecipeSummary));
            }
            catch (NullPointerException n){
                n.printStackTrace();

                Log.e(Constants.LOG_STRING, getString(R.string.errorLoadingRecipeSummary));
            }

            return summary;
        }

        @Override
        protected void onPostExecute(String summary) {
            if(this.asyncInterface != null){
                this.asyncInterface.onAsyncFinished(summary);
            }

            super.onPostExecute(summary);
        }

    }

    private interface AsyncInterface {
        void onAsyncFinished(String result);
    }

    private void showIngredients(RecipeDetails recipe){
        for(ExtendedIngredient ingredient : recipe.getIngredients()){
            LinearLayout linearLayout = new LinearLayout(this);
            linearLayout.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));
            linearLayout.setPadding(0, 8, 0, 0);

            TextView tvAmount = new TextView(this);

            tvAmount.setText(
                    String.format(Locale.ROOT,"%.2f", ingredient.getAmount())
            );
            tvAmount.setLayoutParams(new LinearLayout.LayoutParams(
                    200,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));
            tvAmount.setTypeface(Typeface.DEFAULT_BOLD);
            tvAmount.setGravity(Gravity.END);
            tvAmount.setTextColor(getResources().getColor(R.color.colorAccent));
            tvAmount.setPadding(0, 0, 40, 0);

            linearLayout.addView(tvAmount);

            TextView tvUnit = new TextView(this);
            tvUnit.setText(ingredient.getUnit());
            tvUnit.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));
            tvUnit.setPadding(0, 0, 10, 0);

            if(ingredient.getUnit() != null && ingredient.getUnit().equals("")) {
                tvUnit.setVisibility(View.GONE);
            }

            linearLayout.addView(tvUnit);

            TextView tvName = new TextView(this);
            tvName.setText(ingredient.getName());
            tvName.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));

            linearLayout.addView(tvName);

            layoutIngredients.addView(linearLayout);
        }
    }

    private void showInstructions(RecipeDetails recipe){
        if(recipe.getInstructions() != null && recipe.getInstructions().size() > 0) {
            for (Step step : recipe.getInstructions().get(0).getSteps()) {
                LinearLayout linearLayout = new LinearLayout(this);
                linearLayout.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                ));
                linearLayout.setPadding(0, 8, 0, 0);

                TextView tvStep = new TextView(this);
                tvStep.setText(String.valueOf(step.getNumber()));
                tvStep.setLayoutParams(new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        0.05f
                ));
                tvStep.setTypeface(Typeface.DEFAULT_BOLD);
                tvStep.setTextColor(getResources().getColor(R.color.colorAccent));

                linearLayout.addView(tvStep);

                TextView tvText = new TextView(this);
                tvText.setText(step.getStep());
                tvText.setLayoutParams(new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        0.95f
                ));

                linearLayout.addView(tvText);

                layoutSteps.addView(linearLayout);
            }
        }
    }

    private void showNutrients(RecipeDetails recipe){
        for(Nutrient nutrient : recipe.getNutrition().getNutrients()){
            LinearLayout linearLayout = new LinearLayout(this);
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);
            linearLayout.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));
            linearLayout.setPadding(0, 8, 0, 0);

            TextView textView = new TextView(this);
            textView.setText(nutrient.getTitle());
            textView.setLayoutParams(new LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    0.5f
            ));
            textView.setTypeface(Typeface.DEFAULT_BOLD);

            linearLayout.addView(textView);

            TextView tvValue = new TextView(this);
            tvValue.setText(String.valueOf(nutrient.getAmount()));
            tvValue.setLayoutParams(new LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    0.2f
            ));
            tvValue.setGravity(Gravity.END);
            tvValue.setPadding(0, 0, 15, 0);

            linearLayout.addView(tvValue);

            TextView tvUnit = new TextView(this);
            tvUnit.setText(String.valueOf(nutrient.getUnit()));
            tvUnit.setLayoutParams(new LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    0.1f
            ));

            linearLayout.addView(tvUnit);

            TextView tvDailyPercent = new TextView(this);
            String tmp = nutrient.getDailyPercent() + " %";
            tvDailyPercent.setText(tmp);
            tvDailyPercent.setLayoutParams(new LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    0.2f
            ));
            tvDailyPercent.setGravity(Gravity.END);

            linearLayout.addView(tvDailyPercent);

            layoutNutrients.addView(linearLayout);
        }
    }

}
