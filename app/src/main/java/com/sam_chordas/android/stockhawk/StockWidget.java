package com.sam_chordas.android.stockhawk;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

import com.sam_chordas.android.stockhawk.service.MyRemoteViewsService;
import com.sam_chordas.android.stockhawk.ui.MyStocksActivity;

/**
 * Created by knoxpo on 20/06/16.
 */
public class StockWidget extends AppWidgetProvider {

    private static final String TAG = StockWidget.class.getSimpleName();

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.d(TAG, "Update called");
        for (int appWidgetId : appWidgetIds) {
            updateWidget(context, appWidgetManager, appWidgetId);
        }
    }

    private void updateWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId){
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.layout_widget);

        remoteViews.setTextViewText(R.id.tv_widget_title,context.getString(R.string.app_name));
        remoteViews.setRemoteAdapter(R.id.list_stocks,
                new Intent(context, MyRemoteViewsService.class));

        Intent intent = new Intent(context, MyStocksActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        remoteViews.setOnClickPendingIntent(R.id.tv_widget_title, pendingIntent);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
    }

}
