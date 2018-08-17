package gr.georkouk.inmyfridge;

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
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
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


public class ActivityIngredients extends AppCompatActivity {

    private static final String LAYOUT_MANAGER_STATE = "LAYOUT_MANAGER_STATE";
    private static final String ISROTATING = "isRotating";
    private static final String MIN_CALORIES = "minCalories";
    private static final String MAX_CALORIES = "maxCalories";
    private static final String MAIN_INGREDIENTS_POS = "mainIngredientPos";
    private static final String CUISINE_POS = "cuisinePos";
    private static final String DIET_POS = "dietPos";
    private static final String MEAL_TYPE_POS = "mealTypePos";
    private static final String INTOLERANCES = "intolerances";
    private static final String INGREDIENTS = "ingredients";

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

        //check if user has rotated the screen to hide splashScreen and load selections
        boolean isRotating = false;
        String selectedIngredients = "";
        if(savedInstanceState != null){
            isRotating = savedInstanceState.getBoolean(ISROTATING, false);
            selectedIngredients = savedInstanceState.getString(INGREDIENTS, "");

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

            }
        });

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(
                LAYOUT_MANAGER_STATE,
                this.recyclerView.getLayoutManager().onSaveInstanceState()
        );

        outState.putBoolean(ISROTATING, true);
        outState.putString(MIN_CALORIES, this.etMinCalories.getText().toString());
        outState.putString(MAX_CALORIES, this.etMaxCalories.getText().toString());
        outState.putInt(MAIN_INGREDIENTS_POS, this.spMainIngredient.getSelectedItemPosition());
        outState.putInt(CUISINE_POS, this.spCuisine.getSelectedItemPosition());
        outState.putInt(DIET_POS, this.spDiet.getSelectedItemPosition());
        outState.putInt(MEAL_TYPE_POS, this.spMealType.getSelectedItemPosition());
        outState.putString(INTOLERANCES, getSelectedIntolerances());
        outState.putString(INGREDIENTS, this.ingredientsRecAdapter.getSelectedIngredientsStr());

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        this.layoutManagerState = savedInstanceState.getParcelable(LAYOUT_MANAGER_STATE);

        restoreFilters(savedInstanceState);
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
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,
                this.drawer,
                this.toolbar,
                R.string.app_name,
                R.string.app_name
        );

        this.drawer.addDrawerListener(toggle);
        toggle.syncState();

        this.recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        this.ingredientsRecAdapter = new IngredientsRecAdapter(this);

        this.recyclerView.setAdapter(this.ingredientsRecAdapter);

        this.btSearchRecipes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ActivityIngredients.this, ActivityRecipes.class);

                String selectedIngredients = ingredientsRecAdapter.getSelectedIngredientsStr();

                if(spMainIngredient.getSelectedItemPosition() > 0){
                    String mainIngredient =
                            mainIngredientsDrawer.get(spMainIngredient.getSelectedItemPosition() - 1).getName();

                    selectedIngredients += "," + mainIngredient;
                }

                intent.putExtra("ingredients", selectedIngredients);

                String cuisine = "";
                if(spCuisine.getSelectedItemPosition() > 0){
                    cuisine = cuisineDrawer.get(spCuisine.getSelectedItemPosition() - 1).getName();
                }

                intent.putExtra("cuisine", cuisine);

                String dieet = "";
                if(spDiet.getSelectedItemPosition() > 0){
                    dieet = dietDrawer.get(spDiet.getSelectedItemPosition() - 1).getName();
                }

                intent.putExtra("diet", dieet);

                String mealType = "";
                if(spMealType.getSelectedItemPosition() > 0){
                    mealType = mealTypeDrawer.get(spMealType.getSelectedItemPosition() - 1).getName();
                }

                intent.putExtra("mealType", mealType);

                intent.putExtra("intolerances", getSelectedIntolerances());

                intent.putExtra("minCalories", etMinCalories.getText().toString());
                intent.putExtra("maxCalories", etMaxCalories.getText().toString());

                clearSelections();

                startActivity(intent);
            }
        });

        String[] mainIngredientsArray = getResources().getStringArray(R.array.mainIngredientsArray);

        List<String> spMainIngredientList = new ArrayList<>();

        mainIngredientsDrawer = new ArrayList<>();
        spMainIngredientList.add("Select");
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

        cuisineDrawer = new ArrayList<>();
        cuisineDrawer.add(new DrawerItem("greek", "Greek"));
        cuisineDrawer.add(new DrawerItem("chinese", "Chinese"));
        cuisineDrawer.add(new DrawerItem("japanese", "Japanese"));
        cuisineDrawer.add(new DrawerItem("korean", "Korean"));
        cuisineDrawer.add(new DrawerItem("vietnamese", "Vietnamese"));
        cuisineDrawer.add(new DrawerItem("thai", "Thai"));
        cuisineDrawer.add(new DrawerItem("indian", "Indian"));
        cuisineDrawer.add(new DrawerItem("british", "British"));
        cuisineDrawer.add(new DrawerItem("french", "French"));
        cuisineDrawer.add(new DrawerItem("italian", "Italian"));
        cuisineDrawer.add(new DrawerItem("mexican", "Mexican"));
        cuisineDrawer.add(new DrawerItem("spanish", "Spanish"));
        cuisineDrawer.add(new DrawerItem("american", "American"));
        cuisineDrawer.add(new DrawerItem("german", "German"));

        List<String> spCuisineList = new ArrayList<>();
        spCuisineList.add("Select");
        for(DrawerItem item : cuisineDrawer){
            spCuisineList.add(item.getName());
        }

        ArrayAdapter<String> cuisineAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                spCuisineList
        );

        spCuisine.setAdapter(cuisineAdapter);

        dietDrawer = new ArrayList<>();
        dietDrawer.add(new DrawerItem("pescetarian", "Pescetarian"));
        dietDrawer.add(new DrawerItem("vegetarian", "Vegetarian"));
        dietDrawer.add(new DrawerItem("lactoVegetarian", "Lacto Vegetarian"));
        dietDrawer.add(new DrawerItem("ovoVegetarian", "Ovo Vegetarian"));
        dietDrawer.add(new DrawerItem("vegan", "Vegan"));
        dietDrawer.add(new DrawerItem("paleo", "Paleo"));
        dietDrawer.add(new DrawerItem("primal", "Primal"));

        List<String> spDietList = new ArrayList<>();
        spDietList.add("Select");
        for(DrawerItem item : dietDrawer){
            spDietList.add(item.getName());
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

        mealTypeDrawer = new ArrayList<>();
        mealTypeDrawer.add(new DrawerItem("maincourse", "Main course"));
        mealTypeDrawer.add(new DrawerItem("sidedish", "Side dish"));
        mealTypeDrawer.add(new DrawerItem("dessert", "Dessert"));
        mealTypeDrawer.add(new DrawerItem("appetizer", "Appetizer"));
        mealTypeDrawer.add(new DrawerItem("salad", "Salad"));
        mealTypeDrawer.add(new DrawerItem("bread", "Bread"));
        mealTypeDrawer.add(new DrawerItem("breakfast", "Breakfast"));
        mealTypeDrawer.add(new DrawerItem("soup", "Soup"));
        mealTypeDrawer.add(new DrawerItem("beverage", "Beverage"));
        mealTypeDrawer.add(new DrawerItem("sauce", "Sauce"));
        mealTypeDrawer.add(new DrawerItem("drink", "Drink"));

        List<String> spMealTypeList = new ArrayList<>();
        spMealTypeList.add("Select");
        for(DrawerItem item : this.mealTypeDrawer){
            spMealTypeList.add(item.getName());
        }

        ArrayAdapter<String> mealTypeAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                spMealTypeList
        );

        spMealType.setAdapter(mealTypeAdapter);

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
        this.etMinCalories.setText(bundle.getString(MIN_CALORIES, ""));
        this.etMaxCalories.setText(bundle.getString(MAX_CALORIES, ""));

        this.spMainIngredient.setSelection(bundle.getInt(MAIN_INGREDIENTS_POS, 0));
        this.spCuisine.setSelection(bundle.getInt(CUISINE_POS, 0));
        this.spDiet.setSelection(bundle.getInt(DIET_POS, 0));
        this.spMealType.setSelection(bundle.getInt(MEAL_TYPE_POS, 0));

        String intolerances = bundle.getString(INTOLERANCES, "");
        String[] inolerancesStr = intolerances.split(",");
        for(String intolerance : inolerancesStr){
            Switch switchItem = layoutIntolerances.findViewWithTag(intolerance);

            if(switchItem != null){
                switchItem.setChecked(true);
            }
        }
    }

}
