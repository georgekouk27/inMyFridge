package gr.georkouk.inmyfridge.widget;

import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import gr.georkouk.inmyfridge.R;


public class RecipeAppWidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsService.RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new AppWidgetListView(this.getApplicationContext());
    }

}

class AppWidgetListView implements RemoteViewsService.RemoteViewsFactory {

    private List<String> dataList;
    private Context context;

    public AppWidgetListView(Context applicationContext) {
        this.context = applicationContext;

        String[] mainIngredientsArray = context.getResources().getStringArray(R.array.mainIngredientsArray);
        dataList = new ArrayList<>(Arrays.asList(mainIngredientsArray));
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.list_item_widget);

        views.setTextViewText(R.id.tvName, dataList.get(position));

        Intent fillInIntent = new Intent();
        fillInIntent.putExtra("ingredients", dataList.get(position));
        views.setOnClickFillInIntent(R.id.parentView, fillInIntent);

        return views;
    }

    @Override
    public RemoteViews getLoadingView() {

        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

}
