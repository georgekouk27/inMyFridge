package gr.georkouk.inmyfridge.widget;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import gr.georkouk.inmyfridge.R;


public class RecipeAppWidgetUpdateService extends IntentService {

    public static final String ACTION_UPDATE_LIST_VIEW = "gr.georkouk.inmyfridge.update_app_widget_list";

    public RecipeAppWidgetUpdateService() {
        super("RecipeAppWidgetUpdateService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, RecipeAppWidgetProvider.class));

        RecipeAppWidgetProvider.updateAllAppWidget(this, appWidgetManager,appWidgetIds);

        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.listView);
    }

    public static void startActionUpdateAppWidgets(Context context) {
        Intent intent = new Intent(context, RecipeAppWidgetUpdateService.class);
        intent.setAction(ACTION_UPDATE_LIST_VIEW);
        context.startService(intent);
    }

}
