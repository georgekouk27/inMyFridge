package gr.georkouk.inmyfridge;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
    private IngredientsRecAdapter ingredientsRecAdapter;
    private FirebaseDatabase database;
    private DatabaseReference ingredientsRef;
    private List<DrawerItem> mainIngredientsDrawer;
    private List<DrawerItem> cuisineDrawer;
    private List<DrawerItem> dietDrawer;
    private List<DrawerItem> mealTypeDrawer;
    private List<DrawerItem> intolerancesDrawer;


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

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,
                drawer,
                toolbar,
                R.string.app_name,
                R.string.app_name
        );

        drawer.addDrawerListener(toggle);
        toggle.syncState();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else {
            super.onBackPressed();
        }
    }

    private void initializeView(){
        this.recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        this.ingredientsRecAdapter = new IngredientsRecAdapter(this);

        this.recyclerView.setAdapter(this.ingredientsRecAdapter);

        this.btSearchRecipes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ActivityIngredients.this, ActivityRecipes.class);

                List<Ingredient> ingredients = ingredientsRecAdapter.getSelectedIngredients();

                StringBuilder ingredientsStr = new StringBuilder();

                for(Ingredient ingredient : ingredients){
                    ingredientsStr.append(ingredient.getName()).append(",");
                }

                if(spMainIngredient.getSelectedItemPosition() > 0){
                    String mainIngredient =
                            mainIngredientsDrawer.get(spMainIngredient.getSelectedItemPosition() - 1).getName();

                    ingredientsStr.append(mainIngredient).append(",");
                }

                intent.putExtra("ingredients", ingredientsStr.substring(0, ingredientsStr.length() - 1));

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

                StringBuilder intolerancesStr = new StringBuilder();

                for(DrawerItem item : intolerancesDrawer){
                    Switch switchItem = layoutIntolerances.findViewWithTag(item.getId());

                    if(switchItem.isChecked()){
                        intolerancesStr.append(item.getName()).append(",");
                    }
                }

                if(intolerancesStr.length() > 1) {
                    intent.putExtra("intolerances", intolerancesStr.substring(0, intolerancesStr.length() - 1));
                }
                else{
                    intent.putExtra("intolerances", "");
                }

                startActivity(intent);
            }
        });


        mainIngredientsDrawer = new ArrayList<>();
        mainIngredientsDrawer.add(new DrawerItem("chicken", "Chicken"));
        mainIngredientsDrawer.add(new DrawerItem("turkey", "Turkey"));
        mainIngredientsDrawer.add(new DrawerItem("vegetables", "Vegetables"));
        mainIngredientsDrawer.add(new DrawerItem("beef", "Beef"));
        mainIngredientsDrawer.add(new DrawerItem("fork", "Pork"));
        mainIngredientsDrawer.add(new DrawerItem("beans", "Beans"));
        mainIngredientsDrawer.add(new DrawerItem("fruit", "Fruit"));
        mainIngredientsDrawer.add(new DrawerItem("eggs", "Eggs"));
        mainIngredientsDrawer.add(new DrawerItem("fish", "Fish"));
        mainIngredientsDrawer.add(new DrawerItem("rice", "Rice"));
        mainIngredientsDrawer.add(new DrawerItem("cheece", "Cheece"));

        List<String> spMainIngredientList = new ArrayList<>();
        spMainIngredientList.add("Select");
        for(DrawerItem item : mainIngredientsDrawer){
            spMainIngredientList.add(item.getName());
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

        intolerancesDrawer = new ArrayList<>();
        intolerancesDrawer.add(new DrawerItem("tvDairy", "Dairy"));
        intolerancesDrawer.add(new DrawerItem("tvEgg", "Egg"));
        intolerancesDrawer.add(new DrawerItem("tvGluten", "Gluten"));
        intolerancesDrawer.add(new DrawerItem("tvPeanut", "Peanut"));
        intolerancesDrawer.add(new DrawerItem("tvSesame", "Sesame"));
        intolerancesDrawer.add(new DrawerItem("tvSeafood", "Seafood"));
        intolerancesDrawer.add(new DrawerItem("tvShellfish", "Shellfish"));
        intolerancesDrawer.add(new DrawerItem("tvSoy", "Soy"));
        intolerancesDrawer.add(new DrawerItem("tvSulfite", "Sulfite"));
        intolerancesDrawer.add(new DrawerItem("tvWheat", "Wheat"));

        for(DrawerItem item : intolerancesDrawer){
            Switch switchItem = new Switch(this);
            switchItem.setText(item.getName());
            switchItem.setTag(item.getId());
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
        for(DrawerItem item : mealTypeDrawer){
            spMealTypeList.add(item.getName());
        }

        ArrayAdapter<String> mealTypeAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                spMealTypeList
        );

        spMealType.setAdapter(mealTypeAdapter);

    }

}
