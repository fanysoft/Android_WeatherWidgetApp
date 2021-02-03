package cz.vancura.weatherwidget.Service;

import android.Manifest;
import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.AppWidgetTarget;
import com.bumptech.glide.request.transition.Transition;

import java.util.ArrayList;
import java.util.List;

import cz.vancura.weatherwidget.GetPermissionActivity;
import cz.vancura.weatherwidget.Helper.HelperMethods;
import cz.vancura.weatherwidget.Helper.OnLocationInterface;
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
Service For Android lower than 8.0 devices - startService
this class may by outdated - for full fuctionality see MyForeGroundService
 */

public class MyService extends IntentService implements OnLocationInterface, RetrofitGeoCodingInterface {

    private static final String TAG = "myTAG-MyService";

    private static String StringToWidgetGPS = "init GPS";
    private static String StringToWidgetGeoloc = "init GeoLoc";
    private static String StringToWidgetWeatherStatus = "init Weather";
    private static List<String> StringToWidgetWeatherList;
    private static List<String> StringToWidgetWeatherCelsiusList;
    private static List<String> IconToWidgetWeatherList;

    RemoteViews remoteViews;
    AppWidgetManager appWidgetManager;
    int[] allWidgetIds;

    private static HelperMethods helperMethods;
    private static RetrofitGeoCodingRepo retrofitRepoGeoLocation;
    private static RetrofitWeatherRepo retrofitWeatherRepo;

    Context context;

    public MyService() {
        super("MyService");
    }

    public MyService(String name) {
        super(name);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        context = this;

        // init Lists
        StringToWidgetWeatherList = new ArrayList<>();
        StringToWidgetWeatherCelsiusList = new ArrayList<>();
        IconToWidgetWeatherList = new ArrayList<>();
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        Log.d(TAG, "onHandleIntent - lets do do some work now ..");

        // Check permission
        if (HelperMethods.CheckPerm(this)){

            // granted
            Log.d(TAG, "Location Permission already granted");

            // Get GPS position
            Log.d(TAG, "Service calling for location ..");
            // Method is Async with callback
            helperMethods = new HelperMethods(new OnLocationInterface() {
                @Override
                public void onLocationCompleted() {

                    Log.d(TAG, "onLocationCompleted - GPS position is valid");

                    String GPSLocation = "GPS Lat = " + GPSLatitude + "\n" + "GPS Long = " + GPSLongtitude + "\n";
                    String GPSTimeString = "GPS time = " + ConvertEPOCHTime(GPSTime);
                    StringToWidgetGPS = GPSLocation + GPSTimeString;


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

                            StringToWidgetGeoloc = "Geocoding ERROR " + error;

                            // dB room - insert
                            Log.d(TAG, "inserting data do room db ..");
                            LocationRepository locationRepository = new LocationRepository(context, new ReadAsyncTaskInterface() {
                                @Override
                                public void onTaskCompleted() {
                                    // none - callback is needed for read from dB only
                                }
                            });
                            locationRepository.insertLocation(new Location(GPSLatitude, GPSLongtitude,StringToWidgetGeoloc));

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
            helperMethods.GetLocationAsync(this);



        }else{
            // not granted
            Log.d(TAG, "Permission not granted - will ask user to grand it now ..");

            // request user - nelze ze Service, jen z Activity
            // https://stackoverflow.com/questions/33867088/request-location-permissions-from-a-service-android-m
            // start activity to get perm
            Intent dialogIntent = new Intent(this, GetPermissionActivity.class);
            dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(dialogIntent);

        }

        // Widget variables - nezavisly na MainActivity takze muze bezet i kdyz je app vypnuta
        appWidgetManager = AppWidgetManager.getInstance(context);
        allWidgetIds = intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS);

        UpdateWidgetGUI();

    }


    public void UpdateWidgetGUI() {

        Log.d(TAG, "UpdateWidgetGUI()");

        for (int widgetId : allWidgetIds) {

            remoteViews = new RemoteViews(context.getPackageName(), R.layout.weather_app_widget);

            long currentTime = System.currentTimeMillis();
            String currentTimeString = ConvertEPOCHTime(currentTime);

            Log.d(TAG, "updating widget GUI ..");

            // nadpis
            remoteViews.setTextViewText(R.id.appwidget_text1, "MyService (lower than Android 8)");
            // last refresh time
            remoteViews.setTextViewText(R.id.appwidget_text2, "Last widget refresh = " + currentTimeString);
            // GPS
            remoteViews.setTextViewText(R.id.appwidget_text3, StringToWidgetGPS);
            // GeoCoding
            remoteViews.setTextViewText(R.id.appwidget_text4, StringToWidgetGeoloc);
            // Weather status
            if (StringToWidgetWeatherStatus.contains("ERROR")) {
                remoteViews.setTextViewText(R.id.appwidget_text_now_title, StringToWidgetWeatherStatus);
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
            Intent clickIntent = new Intent(context, WeatherAppWidget.class);

            clickIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            clickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, allWidgetIds);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, clickIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setOnClickPendingIntent(R.id.widgetLayout, pendingIntent);

            // update widget
            appWidgetManager.updateAppWidget(widgetId, remoteViews);

        }


    }




    // nepouzije se - see above
    @Override
    public void onLocationCompleted() {
        Log.d(TAG, "onLocationCompleted - void");
    }

    // nepouzije se - see above
    @Override
    public void onLocationError() {

    }


    // nepouzije se - see above
    @Override
    public void OnRetrofitCompleted() {
        Log.d(TAG, "OnRetrofitCompleted - void");
    }

    // nepouzije se - see above
    @Override
    public void OnRetrofitError(String error) {

    }



}