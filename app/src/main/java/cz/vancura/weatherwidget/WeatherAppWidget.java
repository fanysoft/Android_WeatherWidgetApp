package cz.vancura.weatherwidget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import cz.vancura.weatherwidget.Service.MyForeGroundService;
import cz.vancura.weatherwidget.Service.MyService;

// Widget Class


public class WeatherAppWidget extends AppWidgetProvider{

    private static String TAG = "myTAG-WeatherAppWidget";

    static AppWidgetManager appWidgetManagerGlobal;
    static RemoteViews remoteViewsGlobal;
    static int appWidgetIdGlobal;


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        Log.d(TAG, "onUpdate");

        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            appWidgetIdGlobal = appWidgetId;
        }


        // Build the intent to call the service
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            Log.d(TAG, "For Android 8.0 and newer devices - startForegroundService ..");
            Toast.makeText(context, "Launching ForegroundService..", Toast.LENGTH_SHORT).show();

            // Build the intent to call the foreground service
            Intent intent = new Intent(context.getApplicationContext(), MyForeGroundService.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
            intent.setAction(MyForeGroundService.ACTION_START_FOREGROUND_SERVICE);
            context.startForegroundService(intent);

        } else {

            Log.d(TAG, "For Android lower than 8.0 devices - startService ..");
            Toast.makeText(context, "Launching Service..", Toast.LENGTH_SHORT).show();

            // Build the intent to call the service
            Intent intent = new Intent(context.getApplicationContext(), MyService.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
            context.startService(intent);
        }


        // Instruct the widget manager to update the widget
        appWidgetManagerGlobal.updateAppWidget(appWidgetIdGlobal, remoteViewsGlobal);

    }

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d(TAG, "onReceive");

        // global widget variables - init
        appWidgetManagerGlobal = AppWidgetManager.getInstance(context.getApplicationContext());
        remoteViewsGlobal = new RemoteViews(context.getPackageName(), R.layout.weather_app_widget);

        ComponentName thisWidget = new ComponentName(context.getApplicationContext(), WeatherAppWidget.class);
        int[] appWidgetIds = appWidgetManagerGlobal.getAppWidgetIds(thisWidget);

        if (appWidgetIds != null && appWidgetIds.length > 0) {
            onUpdate(context, appWidgetManagerGlobal, appWidgetIds);
        }
    }

    // This is called when the first widget is created
    @Override
    public void onEnabled(Context context) {
        Log.d(TAG, "onEnabled");
    }

    // This is called when the last widget is disabled
    @Override
    public void onDisabled(Context context) {
        Log.d(TAG, "onDisabled");
    }

    // This is called when the widget is first placed and any time the widget is resized.
    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
        Log.d(TAG, "onAppWidgetOptionsChanged");
    }

}

