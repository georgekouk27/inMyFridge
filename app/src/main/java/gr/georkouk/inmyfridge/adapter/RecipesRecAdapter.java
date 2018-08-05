package gr.georkouk.inmyfridge.adapter;

import android.content.Context;
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
import gr.georkouk.inmyfridge.model.Recipe;


public class RecipesRecAdapter extends RecyclerView.Adapter<RecipesRecAdapter.ViewHolder> {

    private Context context;
    private List<Recipe> recipes;
    private OnItemclickListener onItemClickListener;


    public RecipesRecAdapter(Context context){
        this.context = context;
        this.recipes = new ArrayList<>();
    }

    @NonNull
    @Override
    public RecipesRecAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recipe_item, parent, false);

        return new RecipesRecAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipesRecAdapter.ViewHolder holder, int position) {
        final Recipe recipe = this.recipes.get(position);

        holder.tvName.setText(recipe.getTitle());
        holder.tvMissingNum.setText(String.valueOf(recipe.getMissedIngredientCount()));

        RequestOptions options = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .centerCrop()
                .dontTransform()
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher);

        Glide.with(context)
                .load(recipe.getImageUrl())
                .apply(options)
                .into(holder.icon);

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClickListener.onReciperClick(recipe);
            }
        });

    }

    @Override
    public int getItemCount() {
        return (null != this.recipes ? this.recipes.size() : 0);
    }

    public void swapData(List<Recipe> recipes){
        this.recipes = recipes;

        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemclickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        private View view;
        @BindView(R.id.ivRecipeImage)
        ImageView icon;
        @BindView(R.id.tvRecipeName)
        TextView tvName;
        @BindView(R.id.tvMissingNum)
        TextView tvMissingNum;

        ViewHolder(View itemView) {
            super(itemView);

            this.view = itemView;

            ButterKnife.bind(this, view);
        }

    }

    public interface OnItemclickListener {

        void onReciperClick(Recipe recipe);

    }

}
