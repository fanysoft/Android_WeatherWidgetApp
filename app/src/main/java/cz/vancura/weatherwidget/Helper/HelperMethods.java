package cz.vancura.weatherwidget.Helper;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class HelperMethods {

    private static final String TAG = "myTAG-HelperMethods";

    public static double GPSLatitude = 0;
    public static double GPSLongtitude = 0;
    public static double GPSTime = 0;


    // return current date in EPOCH format
    public static Long GetCurrentDate(){
        long result =  System.currentTimeMillis();
        // Log.d(TAG,"GetCurrentDate returns " + result); // example 1612609188428
        return result;
    }

    // convert Epoch time to Human readable Date Time - from Long
    public static String ConvertEPOCHTimeLong(Long input){

        DateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.GERMAN);
        String result = format.format(input);
        //Log.d(TAG,"ConvertEPOCHTime returns " + result);
        return result;
    }

    // Convert EPOCH Linux time to Human readable - from double
    public static String ConvertEPOCHTimeDouble(double inTime){

        Date date = new Date((long) inTime);
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.GERMANY);
        String outTime = dateFormat.format(date);

        return  outTime;

    }

    // Convert long time to days, hours, min, secs
    public static String HowOldTime(long inputMilisec){

        String result = "";

        long seconds = inputMilisec / 1000;
        int day = (int)TimeUnit.SECONDS.toDays(seconds);
        long hours = TimeUnit.SECONDS.toHours(seconds) - (day *24);
        long minute = TimeUnit.SECONDS.toMinutes(seconds) - (TimeUnit.SECONDS.toHours(seconds)* 60);
        long second = TimeUnit.SECONDS.toSeconds(seconds) - (TimeUnit.SECONDS.toMinutes(seconds) *60);

        result = day + "days " + hours + "hours " + minute + "min " + second + "sec";

        //Log.d(TAG, "HowOldTime input =" + inputMilisec +" returns " + result);
        return result;

    }


    // according to timestamp - judge how old it is - for icon color in map
    public static int JudgeAge(Long dateInput){

        int result = 0;

        long dateNow = GetCurrentDate();
        long diff = dateNow - dateInput;
        //Log.d(TAG, "JudgeAge - diff=" + diff);

        // 1 Day = 86,400,000 Milliseconds
        // 7 Days = 604,800,000 Milliseconds
        if (diff < 86400000){
            // it is less than 1 day old
            result = 1;
        }else if(diff < 604800000){
            // it is max 1 week old
            result = 2;
        }else{
            // it is older than 1 weel
            result = 3;
        }

        //Log.d(TAG, "JudgeAge - result=" + result);
        return result;

    }

    // test internet connection
    public static boolean isOnlineTest(Context context) {

        // Connection status - Google recommended
        // https://developer.android.com/training/monitoring-device-state/connectivity-status-type
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        boolean result = netInfo != null && netInfo.isConnectedOrConnecting();
        //Log.d(TAG, "isOnlineTest() returns result2=" + result);

        return result;
    }


    // listener for Location
    private static OnLocationInterface onLocationInterface;

    // constructor with Location listener
    public HelperMethods(OnLocationInterface onLocationInterface) {
        this.onLocationInterface = onLocationInterface;
    }

    // Get GPS location - lat + long + time
    @SuppressLint("MissingPermission")
    public static void GetLocationAsync(final Context context){

        Log.d(TAG, "GetLocation()");

        // Location provider
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);

        fusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {

                Log.d(TAG, "fusedLocationClient onSuccess");

                if (location != null ){

                    // GPS position
                    GPSLatitude = location.getLatitude();
                    Log.d(TAG, "gpsLatitude=" + GPSLatitude);
                    GPSLongtitude = location.getLongitude();
                    Log.d(TAG, "gpsLongtitude=" + GPSLongtitude);

                    // GPS how old
                    GPSTime = location.getTime(); // Return the UTC time of this fix, in milliseconds since January 1, 1970.

                    // callback - it is async - job is done
                    Log.d(TAG, "callback - GetLocationAsync() is done ..");
                    onLocationInterface.onLocationCompleted();

                }else{

                    Log.d(TAG, "fusedLocationClient location is null");
                    Toast.makeText(context, "Unable to get GPS location", Toast.LENGTH_LONG).show();
                    onLocationInterface.onLocationError();

                }

            }
        });

    }


    // OpenWeather.org icon set
    public static String GiveMeIcon(String input) {

        //Log.d(TAG, "input=" + input);
        input = input.toLowerCase();

        String url = "";

        // according to https://openweathermap.org/weather-conditions#Weather-Condition-Codes-2

        switch(input) {
            case "clouds":
                url = "https://openweathermap.org/img/wn/04d@2x.png";
                break;
            case "clear":
                url = "https://openweathermap.org/img/wn/01d@2x.png";
                break;
            case "snow":
                url = "https://openweathermap.org/img/wn/13d@2x.png";
                break;
            case "rain":
                url = "https://openweathermap.org/img/wn/10d@2x.png";
                break;
            case "drizzle":
                url = "https://openweathermap.org/img/wn/09d@2x.png";
                break;
            case "thunderstorm":
                url = "https://openweathermap.org/img/wn/11d@2x.png";
                break;
            case "mist":
                url = "https://openweathermap.org/img/wn/50d@2x.png";
                break;
            case "fog":
                url = "https://openweathermap.org/img/wn/50d@2x.png";
                break;
            default:
                url = "https://openweathermap.org/themes/openweathermap/assets/img/logo_white_cropped.png";
        }


        //Log.d(TAG, "GiveMeIcon - input=" + input + " output=" + url);
        return url;
    }

    // Check permission
    public static Boolean CheckPerm(Context context) {

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return false;
        }else{
            return true;
        }

    }


}
