package gr.georkouk.inmyfridge.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import gr.georkouk.inmyfridge.R;
import gr.georkouk.inmyfridge.model.Ingredient;


public class IngredientsRecAdapter extends RecyclerView.Adapter<IngredientsRecAdapter.ViewHolder>{

    private Context context;
    private List<Ingredient> ingredients;


    public IngredientsRecAdapter(Context context){
        this.context = context;
        this.ingredients = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ingredient_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final Ingredient ingredient = this.ingredients.get(position);

        holder.tvName.setText(ingredient.getName());

        RequestOptions options = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .centerCrop()
                .dontTransform()
                .placeholder(R.drawable.food)
                .error(R.drawable.food);

        Glide.with(context)
                .load(ingredient.getImageUrl())
                .apply(options)
                .into(holder.icon);

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ingredient.setSelected(!ingredient.isSelected());

                notifyItemChanged(position);
            }
        });

        if(ingredient.isSelected()){
            holder.view.setBackgroundColor(context.getResources().getColor(R.color.colorPrimaryLight));
        }
        else{
            holder.view.setBackgroundColor(Color.WHITE);
        }
    }

    @Override
    public int getItemCount() {
        return (null != this.ingredients ? this.ingredients.size() : 0);
    }

    public void swapData(List<Ingredient> ingredients){
        this.ingredients = ingredients;

        notifyDataSetChanged();
    }

    public void resetSelection(){
        for(Ingredient ingredient : ingredients){
            ingredient.setSelected(false);
        }

        notifyDataSetChanged();
    }

    public String getSelectedIngredientsStr(){
        StringBuilder selected = new StringBuilder();

        for(Ingredient ingredient : ingredients){
            if(ingredient.isSelected()){
                selected.append(ingredient.getName()).append(",");
            }
        }

        if(selected.length() > 1){
            return selected.substring(0, selected.length() - 1);
        }
        else{
            return "";
        }
    }

    public void restoreSelections(String ingredientsStr){
        String[] ingrStr = ingredientsStr.split(",");

        for(String ingredientString : ingrStr){
            for(Ingredient ingredient : ingredients){
                if(ingredient.getName().equals(ingredientString)){
                    ingredient.setSelected(true);
                }
            }
        }

        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        private View view;
        @BindView(R.id.ivIcon)
        ImageView icon;
        @BindView(R.id.tvName)
        TextView tvName;

        ViewHolder(View itemView) {
            super(itemView);

            this.view = itemView;

            ButterKnife.bind(this, view);
        }

    }

}
