package cz.vancura.weatherwidget.Service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.AppWidgetTarget;
import com.bumptech.glide.request.transition.Transition;

import java.util.ArrayList;
import java.util.List;

import cz.vancura.weatherwidget.GetPermissionActivity;
import cz.vancura.weatherwidget.Helper.HelperMethods;
import cz.vancura.weatherwidget.Helper.OnLocationInterface;
import cz.vancura.weatherwidget.MainActivity;
import cz.vancura.weatherwidget.R;
import cz.vancura.weatherwidget.RetrofitGeoCoding.RetrofitGeoCodingInterface;
import cz.vancura.weatherwidget.RetrofitGeoCoding.RetrofitGeoCodingRepo;
import cz.vancura.weatherwidget.RetrofitWeather.RetrofitWeatherInterface;
import cz.vancura.weatherwidget.RetrofitWeather.RetrofitWeatherRepo;
import cz.vancura.weatherwidget.WeatherAppWidget;
import cz.vancura.weatherwidget.model.Location;
import cz.vancura.weatherwidget.model.roomdb.LocationRepository;
import cz.vancura.weatherwidget.model.roomdb.ReadAsyncTaskInterface;

import static cz.vancura.weatherwidget.Helper.HelperMethods.ConvertEPOCHTime;
import static cz.vancura.weatherwidget.Helper.HelperMethods.GPSLatitude;
import static cz.vancura.weatherwidget.Helper.HelperMethods.GPSLongtitude;
import static cz.vancura.weatherwidget.Helper.HelperMethods.GPSTime;
import static cz.vancura.weatherwidget.RetrofitGeoCoding.RetrofitGeoCodingRepo.GeoLocationResult;
import static cz.vancura.weatherwidget.RetrofitWeather.RetrofitWeatherRepo.myWeahterList;

/*
Service For Android 8.0 and newer devices - startForegroundService
 */
public class MyForeGroundService extends Service {

    private static String TAG = "myTAG-MyForeGroundService";

    private static String StringToWidgetGPS = "init GPS";
    private static String StringToWidgetGeoloc = "init GeoLoc";
    private static String StringToWidgetWeatherStatus = "init Weather";
    private static List<String> StringToWidgetWeatherList;
    private static List<String> StringToWidgetWeatherCelsiusList;
    private static List<String> IconToWidgetWeatherList;

    static String CHANNEL_ID = "1";
    static int ONGOING_NOTIFICATION_ID = 2;
    public static final String ACTION_START_FOREGROUND_SERVICE = "ACTION_START_FOREGROUND_SERVICE";

    Intent intentGlobal;
    Context context;
    private boolean jobCancelled = false;

    RemoteViews remoteViews;
    AppWidgetManager appWidgetManager;
    int[] allWidgetIds;

    private static HelperMethods helperMethods;
    private static RetrofitGeoCodingRepo retrofitRepoGeoLocation;
    private static RetrofitWeatherRepo retrofitWeatherRepo;

    public MyForeGroundService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate()");

        context = this;

        // init Lists
        StringToWidgetWeatherList = new ArrayList<>();
        StringToWidgetWeatherCelsiusList = new ArrayList<>();
        IconToWidgetWeatherList = new ArrayList<>();

    }


    // Intent recieved
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if(intent != null) {

            intentGlobal = intent;

            String action = intent.getAction();
            Log.d(TAG, "onStartCommand - action=" + action);

            startForegroundService();

        }
        return super.onStartCommand(intent, flags, startId);

    }

    private void startForegroundService() {

        Log.d(TAG, "startForegroundService()");

        // do some work here
        doBackgroundWork();

        // Start foreground service and show notification
        startForeground(ONGOING_NOTIFICATION_ID, makeNotification("Widget update now"));

        // done - stop service and remove notification
        // Note : po vykonani prace chci zastavit Service a skryt co nejdrive Notification aby si user nevsimnul -  realne neni Notification vubec videt
        stopForegroundService();
    }

    private void stopForegroundService(){
        Log.d(TAG, "stopForegroundService()");
        // Stop foreground service and remove the notification.
        stopForeground(true);
        stopSelf();
    }

    // vlastni work
    private void doBackgroundWork() {

        Log.d(TAG, "doBackgroundWork start ..");

        new Thread(new Runnable() {
            @Override
            public void run() {

                    // we have work to do

                    // Check permission
                    if (HelperMethods.CheckPerm(context)){

                        // granted
                        Log.d(TAG, "Location Permission already granted");

                        // Get GPS position
                        Log.d(TAG, "Service calling for location ..");

                        // Method is Async with callback
                        helperMethods = new HelperMethods(new OnLocationInterface() {
                            @Override
                            public void onLocationCompleted() {

                                Log.d(TAG, "onLocationCompleted");

                                StringToWidgetGPS = "GPS " + GPSLatitude + ", " + GPSLongtitude + ", " + ConvertEPOCHTime(GPSTime);

                                // Call Retrofit to get Geocoding translation
                                // Method is Async with callback
                                Log.d(TAG, "Service calling for Geocoding translation ..");
                                retrofitRepoGeoLocation = new RetrofitGeoCodingRepo(new RetrofitGeoCodingInterface() {
                                    @Override
                                    public void OnRetrofitCompleted() {

                                        Log.d(TAG, "OnRetrofitCompleted - geoCoding data are ready");

                                        StringToWidgetGeoloc = GeoLocationResult;

                                        // dB room - insert
                                        Log.d(TAG, "inserting data do room db ..");
                                        LocationRepository locationRepository = new LocationRepository(context, new ReadAsyncTaskInterface() {
                                            @Override
                                            public void onTaskCompleted() {
                                                // none - callback is needed for read from dB only
                                            }
                                        });
                                        locationRepository.insertLocation(new Location(GPSLatitude, GPSLongtitude,StringToWidgetGeoloc));

                                        // db - check after insert - read id - only for debug
                                        locationRepository.getLocations();

                                        // refresh Widget
                                        UpdateWidgetGUI();

                                    }

                                    @Override
                                    public void OnRetrofitError(String error) {

                                        StringToWidgetGeoloc = "GeoCoding ERROR " + error;

                                        // dB room - insert
                                        Log.d(TAG, "inserting data do room db ..");
                                        LocationRepository locationRepository = new LocationRepository(context, new ReadAsyncTaskInterface() {
                                            @Override
                                            public void onTaskCompleted() {
                                                // none - callback is needed for read from dB only
                                            }
                                        });
                                        locationRepository.insertLocation(new Location(GPSLatitude, GPSLongtitude,StringToWidgetGeoloc));

                                        // db - check after insert - read id - only for debug
                                        locationRepository.getLocations();

                                        // refresh Widget
                                        UpdateWidgetGUI();
                                    }
                                });
                                retrofitRepoGeoLocation.RetrofitGeoTranlateAsyns(GPSLatitude, GPSLongtitude, context);


                                // Call Retrofit to get Weather
                                // Method is Async with callback
                                Log.d(TAG, "Service calling for Weather data ..");
                                retrofitWeatherRepo = new RetrofitWeatherRepo(new RetrofitWeatherInterface() {
                                    @Override
                                    public void OnRetrofitCompleted() {

                                        Log.d(TAG, "OnRetrofitCompleted - weather data are ready");

                                        StringToWidgetWeatherStatus = "OK";

                                        for (int i=0; i<3; i++){

                                            // weather
                                            String StringWeatherSmallLetter = myWeahterList.get(i).getWeatherDescCurrent();
                                            String StringWeatherBigLetter = StringWeatherSmallLetter.substring(0, 1).toUpperCase() + StringWeatherSmallLetter.substring(1);
                                            StringToWidgetWeatherList.add(StringWeatherBigLetter);

                                            // celsius
                                            String StringCelsius = Math.round(myWeahterList.get(i).getTempCurrent()) +"°C";
                                            StringToWidgetWeatherCelsiusList.add(StringCelsius);

                                            // icon
                                            String IconUrl = myWeahterList.get(i).getIconUrl();
                                            IconToWidgetWeatherList.add(IconUrl);

                                            Log.d(TAG, "loop " + i + StringWeatherBigLetter + " " + StringCelsius + " " + IconUrl);

                                        }

                                        // refresh Widget
                                        UpdateWidgetGUI();

                                    }

                                    @Override
                                    public void OnRetrofitError(String error) {

                                        StringToWidgetWeatherStatus = "Weather ERROR " + error;

                                        // refresh Widget
                                        UpdateWidgetGUI();
                                    }
                                });
                                retrofitWeatherRepo.RetrofitWeatherAsync(GPSLatitude, GPSLongtitude, context);


                            }

                            @Override
                            public void onLocationError() {
                                // location is null or unable to get location

                                StringToWidgetGPS = "Unable to get GPS position";

                                // refresh Widget
                                UpdateWidgetGUI();

                            }
                        });
                        helperMethods.GetLocationAsync(context);



                    }else{
                        // not granted
                        Log.d(TAG, "Permission not granted - will ask user to grand it now ..");

                        // request user - nelze ze Service, jen z Activity
                        // https://stackoverflow.com/questions/33867088/request-location-permissions-from-a-service-android-m
                        // start activity to get perm
                        Intent dialogIntent = new Intent(context, GetPermissionActivity.class);
                        dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(dialogIntent);

                    }


                    // Widget variables - nezavisly na MainActivity takze muze bezet i kdyz je app vypnuta
                    appWidgetManager = AppWidgetManager.getInstance(context);
                    allWidgetIds = intentGlobal.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS);

                    UpdateWidgetGUI();


            }
        }).start();


    }


    public void UpdateWidgetGUI() {

        Log.d(TAG, "UpdateWidgetGUI()");

        for (int widgetId : allWidgetIds) {

            remoteViews = new RemoteViews(this.getApplicationContext().getPackageName(), R.layout.weather_app_widget);

            long currentTime = System.currentTimeMillis();
            String currentTimeString = ConvertEPOCHTime(currentTime);

            Log.d(TAG, "updating widget GUI ..");

            // nadpis
            remoteViews.setTextViewText(R.id.appwidget_text1, "MyForegroundService");
            // last refresh time
            remoteViews.setTextViewText(R.id.appwidget_text2, "Last widget refresh = " + currentTimeString);
            // GPS
            remoteViews.setTextViewText(R.id.appwidget_text3, StringToWidgetGPS);
            // GeoCoding
            remoteViews.setTextViewText(R.id.appwidget_text4, StringToWidgetGeoloc);
            // Weather status
            if (StringToWidgetWeatherStatus.contains("ERROR")) {
                // show status
                remoteViews.setViewVisibility(R.id.appwidget_text5, View.VISIBLE);
                // hide weather
                remoteViews.setViewVisibility(R.id.weatherLayout, View.GONE);
                remoteViews.setTextViewText(R.id.appwidget_text5, StringToWidgetWeatherStatus);
            }else{
                // hide status
                remoteViews.setViewVisibility(R.id.appwidget_text5, View.GONE);
                // show weather
                remoteViews.setViewVisibility(R.id.weatherLayout, View.VISIBLE);
            }


            if (StringToWidgetWeatherList.size() > 0 ) {

                if (StringToWidgetWeatherStatus.equals("OK")){

                    RequestOptions options = new RequestOptions().override(300, 300).placeholder(R.mipmap.ic_launcher).error(R.mipmap.ic_launcher_round);

                    // Weather - now

                    // Weather - text
                    remoteViews.setTextViewText(R.id.appwidget_text_now, StringToWidgetWeatherList.get(0));
                    remoteViews.setTextViewText(R.id.appwidget_text_nowC, StringToWidgetWeatherCelsiusList.get(0));
                    // Weather - icon via Glide lib
                    // https://stackoverflow.com/questions/47993270/load-imageview-from-url-into-remoteview-of-a-home-screen-widget
                    // https://futurestud.io/tutorials/glide-loading-images-into-notifications-and-appwidgets
                    AppWidgetTarget appWidgetTarget = new AppWidgetTarget(this.getApplicationContext(), R.id.imageView_now, remoteViews, allWidgetIds) {
                        @Override
                        public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                            super.onResourceReady(resource, transition);
                        }
                    };
                    Glide.with(this.getApplicationContext())
                            .asBitmap()
                            .load(IconToWidgetWeatherList.get(0))
                            .apply(options)
                            .into(appWidgetTarget);


                    // Weather +2h

                    // Weather - text
                    remoteViews.setTextViewText(R.id.appwidget_text_h2, StringToWidgetWeatherList.get(1));
                    remoteViews.setTextViewText(R.id.appwidget_text_h2C, StringToWidgetWeatherCelsiusList.get(1));
                    // Weather - icon via Glide lib
                    // https://stackoverflow.com/questions/47993270/load-imageview-from-url-into-remoteview-of-a-home-screen-widget
                    // https://futurestud.io/tutorials/glide-loading-images-into-notifications-and-appwidgets
                    appWidgetTarget = new AppWidgetTarget(this.getApplicationContext(), R.id.imageView_h2, remoteViews, allWidgetIds) {
                        @Override
                        public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                            super.onResourceReady(resource, transition);
                        }
                    };
                    Glide.with(this.getApplicationContext())
                            .asBitmap()
                            .load(IconToWidgetWeatherList.get(1))
                            .apply(options)
                            .into(appWidgetTarget);


                    // Weather +4h

                    // Weather - text
                    remoteViews.setTextViewText(R.id.appwidget_text_h4, StringToWidgetWeatherList.get(2));
                    remoteViews.setTextViewText(R.id.appwidget_text_h4C, StringToWidgetWeatherCelsiusList.get(2));
                    // Weather - icon via Glide lib
                    // https://stackoverflow.com/questions/47993270/load-imageview-from-url-into-remoteview-of-a-home-screen-widget
                    // https://futurestud.io/tutorials/glide-loading-images-into-notifications-and-appwidgets
                    appWidgetTarget = new AppWidgetTarget(this.getApplicationContext(), R.id.imageView_h4, remoteViews, allWidgetIds) {
                        @Override
                        public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                            super.onResourceReady(resource, transition);
                        }
                    };
                    Glide.with(this.getApplicationContext())
                            .asBitmap()
                            .load(IconToWidgetWeatherList.get(2))
                            .apply(options)
                            .into(appWidgetTarget);


                }

            }



            // Register an onClickListener
            Intent clickIntent = new Intent(this.getApplicationContext(), WeatherAppWidget.class);

            clickIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            clickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, allWidgetIds);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, clickIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setOnClickPendingIntent(R.id.widgetLayout, pendingIntent);

            // update widget
            appWidgetManager.updateAppWidget(widgetId, remoteViews);

        }


    }


    // must show Notification in Foreground Service
    private Notification makeNotification(String text){

        Log.d(TAG, "makeNotification text=" + text);

        // if Android 8 (API 26) - create Notification Channel
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){

            CharSequence name = "CH_NAME1";
            String description = "about CH_NAME1";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

        }

        // Create notification default intent.
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        // Create a Notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Notification Title")
                .setContentText(text)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(text))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setOnlyAlertOnce(true) // so when data is updated don't make sound and alert in android 8.0+
                .setOngoing(true)
                .setContentIntent(pendingIntent);


        //  If the build version is greater than JELLY_BEAN (Android 4.2) and lower than OREO (Android 8), set the notification's priority to PRIORITY_HIGH.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN && Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            builder.setPriority(NotificationCompat.PRIORITY_HIGH);
        }

        Notification notification = builder.build();

        return notification;
    }



}
