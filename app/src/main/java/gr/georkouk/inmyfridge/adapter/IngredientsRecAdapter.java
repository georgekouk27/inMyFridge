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
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher);

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
            holder.view.setBackgroundColor(context.getResources().getColor(R.color.colorAccent));
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

    public List<Ingredient> getSelectedIngredients(){
        List<Ingredient> selected = new ArrayList<>();

        for(Ingredient ingredient : ingredients){
            if(ingredient.isSelected()){
                selected.add(ingredient);
            }
        }

        return selected;
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
