package gr.georkouk.inmyfridge;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import gr.georkouk.inmyfridge.adapter.IngredientsRecAdapter;
import gr.georkouk.inmyfridge.model.DrawerItem;
import gr.georkouk.inmyfridge.model.Ingredient;
import gr.georkouk.inmyfridge.network.ConnectionManager;
import gr.georkouk.inmyfridge.utils.Constants;


public class ActivityIngredients extends AppCompatActivity {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.btSearchRecipes)
    Button btSearchRecipes;
    @BindView(R.id.spMainIngredient)
    Spinner spMainIngredient;
    @BindView(R.id.spCuisine)
    Spinner spCuisine;
    @BindView(R.id.spDiet)
    Spinner spDiet;
    @BindView(R.id.spMealType)
    Spinner spMealType;
    @BindView(R.id.layoutIntolerances)
    LinearLayout layoutIntolerances;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;
    @BindView(R.id.etMinCalories)
    EditText etMinCalories;
    @BindView(R.id.etMaxCalories)
    EditText etMaxCalories;
    @BindView(R.id.layoutProgress)
    ConstraintLayout layoutProgress;
    @BindView(R.id.activity_main_root)
    ConstraintLayout layoutIngredients;
    private Toolbar toolbar;
    private IngredientsRecAdapter ingredientsRecAdapter;
    private List<DrawerItem> mainIngredientsDrawer;
    private List<DrawerItem> cuisineDrawer;
    private List<DrawerItem> dietDrawer;
    private List<DrawerItem> mealTypeDrawer;
    private List<DrawerItem> intolerancesDrawer;
    private Parcelable layoutManagerState;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingredients);

        this.toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(this.toolbar);

        ButterKnife.bind(this);
        initializeView();

        checkForInternetConnection();

        //check if user has rotated the screen to hide splashScreen and load selections
        boolean isRotating = false;
        String selectedIngredients = "";
        if(savedInstanceState != null){
            isRotating = savedInstanceState.getBoolean(Constants.ISROTATING, false);
            selectedIngredients = savedInstanceState.getString(Constants.INGREDIENTS, "");

            if(isRotating){
                this.layoutProgress.setVisibility(View.GONE);
                this.layoutIngredients.setVisibility(View.VISIBLE);
            }
        }

        final long tStart = System.currentTimeMillis();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ingredientsRef = database.getReference("ingredients");

        final boolean finalIsRotating = isRotating;
        final String finalSelectedIngredients = selectedIngredients;
        ingredientsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Ingredient> ingredientList = new ArrayList<>();

                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                    ingredientList.add(singleSnapshot.getValue(Ingredient.class));
                }

                ingredientsRecAdapter.swapData(ingredientList);

                restoreRecyclerViewState(finalSelectedIngredients);

                if(!finalIsRotating) {
                    long tEnd = System.currentTimeMillis();
                    long tDelta = tEnd - tStart;
                    double elapsedSeconds = tDelta / 1000.0;

                    if (elapsedSeconds < 3) {
                        long time = (long) ((3 - elapsedSeconds) * 1000);

                        new CountDownTimer(time, 100) {

                            public void onTick(long millisUntilFinished) {

                            }

                            public void onFinish() {
                                layoutProgress.setVisibility(View.GONE);
                                layoutIngredients.setVisibility(View.VISIBLE);
                            }

                        }.start();
                    }
                    else {
                        layoutProgress.setVisibility(View.GONE);
                        layoutIngredients.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(
                        ActivityIngredients.this,
                        getString(R.string.problemOccured),
                        Toast.LENGTH_SHORT
                ).show();

                Log.e(Constants.LOG_STRING, databaseError.getMessage());
            }
        });

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(
                Constants.LAYOUT_MANAGER_STATE,
                this.recyclerView.getLayoutManager().onSaveInstanceState()
        );

        outState.putBoolean(Constants.ISROTATING, true);
        outState.putString(Constants.MIN_CALORIES, this.etMinCalories.getText().toString());
        outState.putString(Constants.MAX_CALORIES, this.etMaxCalories.getText().toString());
        outState.putInt(Constants.MAIN_INGREDIENTS_POS, this.spMainIngredient.getSelectedItemPosition());
        outState.putInt(Constants.CUISINE_POS, this.spCuisine.getSelectedItemPosition());
        outState.putInt(Constants.DIET_POS, this.spDiet.getSelectedItemPosition());
        outState.putInt(Constants.MEAL_TYPE_POS, this.spMealType.getSelectedItemPosition());
        outState.putString(Constants.INTOLERANCES, getSelectedIntolerances());
        outState.putString(Constants.INGREDIENTS, this.ingredientsRecAdapter.getSelectedIngredientsStr());

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        this.layoutManagerState = savedInstanceState.getParcelable(Constants.LAYOUT_MANAGER_STATE);

        restoreFilters(savedInstanceState);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    @Override
    public void onBackPressed() {
        if (this.drawer.isDrawerOpen(GravityCompat.START)) {
            this.drawer.closeDrawer(GravityCompat.START);
        }
        else {
            super.onBackPressed();
        }
    }

    private void initializeView(){
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,
                this.drawer,
                this.toolbar,
                R.string.app_name,
                R.string.app_name
        ){
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);

                InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                assert imm != null;
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };

        this.drawer.addDrawerListener(toggle);
        toggle.syncState();

        this.recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        this.ingredientsRecAdapter = new IngredientsRecAdapter(this);

        this.recyclerView.setAdapter(this.ingredientsRecAdapter);

        this.btSearchRecipes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openSearchActivity();
            }
        });

        loadFilters();
    }

    private void clearSelections(){
        this.ingredientsRecAdapter.resetSelection();

        this.spMainIngredient.setSelection(0);
        this.spCuisine.setSelection(0);
        this.spDiet.setSelection(0);
        this.spMealType.setSelection(0);

        this.etMaxCalories.setText("");
        this.etMinCalories.setText("");

        for(DrawerItem item : this.intolerancesDrawer){
            Switch switchItem = this.layoutIntolerances.findViewWithTag(item.getId());
            switchItem.setChecked(false);
        }
    }

    private void restoreRecyclerViewState(String selectedIngredients){
        if (this.layoutManagerState != null) {
            this.recyclerView.getLayoutManager().onRestoreInstanceState(this.layoutManagerState);
        }

        this.ingredientsRecAdapter.restoreSelections(selectedIngredients);
    }

    private String getSelectedIntolerances(){
        StringBuilder intolerancesStr = new StringBuilder();

        for(DrawerItem item : this.intolerancesDrawer){
            Switch switchItem = this.layoutIntolerances.findViewWithTag(item.getId());

            if(switchItem.isChecked()){
                intolerancesStr.append(item.getName()).append(",");
            }
        }

        if(intolerancesStr.length() > 1) {
            return intolerancesStr.substring(0, intolerancesStr.length() - 1);
        }
        else{
            return "";
        }
    }

    private void restoreFilters(Bundle bundle){
        this.etMinCalories.setText(bundle.getString(Constants.MIN_CALORIES, ""));
        this.etMaxCalories.setText(bundle.getString(Constants.MAX_CALORIES, ""));

        this.spMainIngredient.setSelection(bundle.getInt(Constants.MAIN_INGREDIENTS_POS, 0));
        this.spCuisine.setSelection(bundle.getInt(Constants.CUISINE_POS, 0));
        this.spDiet.setSelection(bundle.getInt(Constants.DIET_POS, 0));
        this.spMealType.setSelection(bundle.getInt(Constants.MEAL_TYPE_POS, 0));

        String intolerances = bundle.getString(Constants.INTOLERANCES, "");
        String[] inolerancesStr = intolerances.split(",");
        for(String intolerance : inolerancesStr){
            Switch switchItem = layoutIntolerances.findViewWithTag(intolerance);

            if(switchItem != null){
                switchItem.setChecked(true);
            }
        }
    }

    private void checkForInternetConnection(){
        if(!ConnectionManager.isConnected(this)){
            Toast.makeText(
                    this,
                    getString(R.string.noInternet),
                    Toast.LENGTH_LONG
            ).show();
        }
    }

    private void loadFilters(){
        String[] mainIngredientsArray = getResources().getStringArray(R.array.mainIngredientsArray);
        mainIngredientsDrawer = new ArrayList<>();
        List<String> spMainIngredientList = new ArrayList<>();
        spMainIngredientList.add(getString(R.string.select));
        for(String item : mainIngredientsArray){
            mainIngredientsDrawer.add(new DrawerItem(item, item));

            spMainIngredientList.add(item);
        }

        ArrayAdapter<String> mainIngredientAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                spMainIngredientList
        );

        spMainIngredient.setAdapter(mainIngredientAdapter);

        String[] cuisinesArray = getResources().getStringArray(R.array.cuisinesArray);
        cuisineDrawer = new ArrayList<>();
        List<String> spCuisineList = new ArrayList<>();
        spCuisineList.add(getString(R.string.select));
        for(String item : cuisinesArray){
            cuisineDrawer.add(new DrawerItem(item, item));

            spCuisineList.add(item);
        }

        ArrayAdapter<String> cuisineAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                spCuisineList
        );

        spCuisine.setAdapter(cuisineAdapter);

        String[] dietsArray = getResources().getStringArray(R.array.dietsArray);
        dietDrawer = new ArrayList<>();

        List<String> spDietList = new ArrayList<>();
        spDietList.add(getString(R.string.select));
        for(String item : dietsArray){
            dietDrawer.add(new DrawerItem(item, item));

            spDietList.add(item);
        }

        ArrayAdapter<String> dietAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                spDietList
        );

        spDiet.setAdapter(dietAdapter);

        String[] intolerancesArray = getResources().getStringArray(R.array.intolerancesArray);
        intolerancesDrawer = new ArrayList<>();
        for(String item : intolerancesArray){
            intolerancesDrawer.add(new DrawerItem(item, item));

            Switch switchItem = new Switch(this);
            switchItem.setText(item);
            switchItem.setTag(item);
            switchItem.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));

            layoutIntolerances.addView(switchItem);
        }

        String[] mealTypesArray = getResources().getStringArray(R.array.mailTypesArray);
        mealTypeDrawer = new ArrayList<>();

        List<String> spMealTypeList = new ArrayList<>();
        spMealTypeList.add(getString(R.string.select));
        for(String item : mealTypesArray){
            mealTypeDrawer.add(new DrawerItem(item, item));

            spMealTypeList.add(item);
        }

        ArrayAdapter<String> mealTypeAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                spMealTypeList
        );

        spMealType.setAdapter(mealTypeAdapter);
    }

    private void openSearchActivity(){
        Intent intent = new Intent(ActivityIngredients.this, ActivityRecipes.class);

        String selectedIngredients = ingredientsRecAdapter.getSelectedIngredientsStr();

        if(spMainIngredient.getSelectedItemPosition() > 0){
            String mainIngredient =
                    mainIngredientsDrawer.get(spMainIngredient.getSelectedItemPosition() - 1).getName();

            selectedIngredients += "," + mainIngredient;
        }

        intent.putExtra(Constants.INGREDIENTS, selectedIngredients);

        String cuisine = "";
        if(spCuisine.getSelectedItemPosition() > 0){
            cuisine = cuisineDrawer.get(spCuisine.getSelectedItemPosition() - 1).getName();
        }

        intent.putExtra(Constants.CUISINE, cuisine);

        String dieet = "";
        if(spDiet.getSelectedItemPosition() > 0){
            dieet = dietDrawer.get(spDiet.getSelectedItemPosition() - 1).getName();
        }

        intent.putExtra(Constants.DIET, dieet);

        String mealType = "";
        if(spMealType.getSelectedItemPosition() > 0){
            mealType = mealTypeDrawer.get(spMealType.getSelectedItemPosition() - 1).getName();
        }

        intent.putExtra(Constants.MEAL_TYPE, mealType);

        intent.putExtra(Constants.INTOLERANCES, getSelectedIntolerances());

        intent.putExtra(Constants.MIN_CALORIES, etMinCalories.getText().toString());
        intent.putExtra(Constants.MAX_CALORIES, etMaxCalories.getText().toString());

        clearSelections();

        startActivity(intent);
    }

}
