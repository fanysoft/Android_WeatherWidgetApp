package cz.vancura.weatherwidget.RetrofitWeather;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cz.vancura.weatherwidget.BuildConfig;
import cz.vancura.weatherwidget.Helper.HelperMethods;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RetrofitWeatherRepo {

    private static final String TAG = "myTAG-RetrofitWeatherRepo";

    public static List<MyWeahterPOJO> myWeahterList = new ArrayList<>();

    // API key is saved in user gradle.properties to not expose it at GitHub
    static final String OpenWeatherApiKey = BuildConfig.myAPIkeyWeather;
    static String units = "metric";

    // listener
    static RetrofitWeatherInterface retrofitWeatherInterface;

    public RetrofitWeatherRepo(RetrofitWeatherInterface retrofitWeatherInterface) {
        this.retrofitWeatherInterface = retrofitWeatherInterface;
    }

    public static void RetrofitWeatherAsync(double Lat, double Lon, final Context context) {

        // Retrofit apiInterface
        RetrofitWeatherAPIInterface apiInterface = RetrofitWeatherAPIClient.getClient().create(RetrofitWeatherAPIInterface.class);

        // Retrofit call
        Call<RetrofitWeatherPOJO> call = apiInterface.doGetWeather(Lat, Lon, OpenWeatherApiKey, units);
        call.enqueue(new Callback<RetrofitWeatherPOJO>() {
            @SuppressLint("LongLogTag")
            @Override
            public void onResponse(Call<RetrofitWeatherPOJO> call, Response<RetrofitWeatherPOJO> response) {

                Log.d(TAG, "response code (200 is OK)="+ response.code());

                // be carefull - JSON structure response from API can differs - better to use try.catch
                try{

                    RetrofitWeatherPOJO weatherPOJO = response.body();

                    // current
                    double TempCurrent = weatherPOJO.current.temp;
                    double HumCurrent = weatherPOJO.current.humidity;

                    double PrercipCurrent = 0;
                    // minutely je v JSON jen nekdy
                    if(weatherPOJO.minutely !=null){
                        // precipitation je v JSON jen nekdy - jen kdyz bude prset
                        if (weatherPOJO.minutely.get(0).precipitation != null){
                             PrercipCurrent = weatherPOJO.minutely.get(0).precipitation;
                        }
                    }

                    String WeatherMainCurrent = weatherPOJO.current.weather.get(0).main;
                    String WeatherDescCurrent = weatherPOJO.current.weather.get(0).description;
                    String WeatherIconCurrent = HelperMethods.GiveMeIcon(WeatherMainCurrent);

                    //Log.d(TAG, "current : Temp=" + TempCurrent + " " + WeatherMainCurrent + "/" + WeatherDescCurrent + "\n");
                    myWeahterList.add(new MyWeahterPOJO("current", TempCurrent, HumCurrent, PrercipCurrent, WeatherMainCurrent, WeatherDescCurrent, WeatherIconCurrent));


                    // hour +2
                    double TempHourPlus2 = weatherPOJO.hourly.get(1).temp;
                    // double PrercipHourPlus2 = weatherPOJO.hourly.get(1).precipitation; // where available - muze byt v datech ale nemusi
                    String WeatherMainHourPlus2 = weatherPOJO.hourly.get(1).weather.get(0).main;
                    String WeatherDescHourPlus2 = weatherPOJO.hourly.get(1).weather.get(0).description;
                    String WeatherIconHourPlus2 = HelperMethods.GiveMeIcon(WeatherMainHourPlus2);

                    //Log.d(TAG, "hour +2 : Temp=" + TempHourPlus2 + " " + WeatherMainHourPlus2 + "/" + WeatherDescHourPlus2 + "\n");
                    myWeahterList.add(new MyWeahterPOJO("hour +2", TempHourPlus2, 0, 0, WeatherMainHourPlus2, WeatherDescHourPlus2, WeatherIconHourPlus2));


                    // hour +4
                    double TempHourPlus4 = weatherPOJO.hourly.get(3).temp;
                    String WeatherMainHourPlus4 = weatherPOJO.hourly.get(3).weather.get(0).main;
                    String WeatherDescHourPlus4 = weatherPOJO.hourly.get(3).weather.get(0).description;
                    String WeatherIconHourPlus4 = HelperMethods.GiveMeIcon(WeatherMainHourPlus4);

                    //Log.d(TAG, "hour +4 : Temp=" + TempHourPlus4 + " " + WeatherMainHourPlus4 + "/" + WeatherDescHourPlus4 + "\n");
                    myWeahterList.add(new MyWeahterPOJO("hour +4", TempHourPlus4, 0, 0, WeatherMainHourPlus4, WeatherDescHourPlus4, WeatherIconHourPlus4));

                    // ..


                    // day +1
                    double TempDayPlus1 = weatherPOJO.daily.get(0).temp.day;
                    String WeatherMainDayPlus1 = weatherPOJO.daily.get(0).weather.get(0).main;
                    String WeatherDescDayPlus1 = weatherPOJO.daily.get(0).weather.get(0).description;
                    String WeatherIconDayPlus1 = HelperMethods.GiveMeIcon(WeatherMainDayPlus1);

                    //Log.d(TAG, "day +1 : Temp=" + TempDayPlus1 + " " + WeatherMainDayPlus1 + "/" +WeatherDescDayPlus1 + "\n");
                    myWeahterList.add(new MyWeahterPOJO("day +1", TempDayPlus1, 0, 0, WeatherMainDayPlus1, WeatherDescDayPlus1, WeatherIconDayPlus1));


                    // day +2
                    double TempDayPlus2 = weatherPOJO.daily.get(1).temp.day;
                    String WeatherMainDayPlus2 = weatherPOJO.daily.get(1).weather.get(0).main;
                    String WeatherDescDayPlus2 = weatherPOJO.daily.get(1).weather.get(0).description;
                    String WeatherIconDayPlus2 = HelperMethods.GiveMeIcon(WeatherMainDayPlus2);

                    //Log.d(TAG, "day +2 : Temp=" + TempDayPlus2 + " " + WeatherMainDayPlus2 + "/" + WeatherDescDayPlus2 + "\n");
                    myWeahterList.add(new MyWeahterPOJO("day +2", TempDayPlus2, 0, 0, WeatherMainDayPlus2, WeatherDescDayPlus2, WeatherIconDayPlus2));


                    // day +3
                    double TempDayPlus3 = weatherPOJO.daily.get(2).temp.day;
                    String WeatherMainDayPlus3 = weatherPOJO.daily.get(2).weather.get(0).main;
                    String WeatherDescDayPlus3 = weatherPOJO.daily.get(2).weather.get(0).description;
                    String WeatherIconDayPlus3 = HelperMethods.GiveMeIcon(WeatherMainDayPlus3);

                    // Log.d(TAG, "day +3 : Temp=" + TempDayPlus3 + " " + WeatherMainDayPlus3 + "/" + WeatherDescDayPlus3 + "\n");
                    myWeahterList.add(new MyWeahterPOJO("day +3", TempDayPlus3, 0, 0, WeatherMainDayPlus3, WeatherDescDayPlus3, WeatherIconDayPlus3));


                    // callback - finished
                    Log.d(TAG, "RetrofitWeatherAsync work done - calling callback ..");
                    retrofitWeatherInterface.OnRetrofitCompleted();


                }catch (Exception e){
                    Log.d(TAG, "ERROR at line " + e.getStackTrace()[0].getLineNumber() + " content=" + e.getLocalizedMessage());
                    Toast.makeText(context, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }

            }

            @SuppressLint("LongLogTag")
            @Override
            public void onFailure(Call<RetrofitWeatherPOJO> call, Throwable t) {

                String error = "Retrofit Error " + t.getLocalizedMessage();
                Log.e(TAG, error);
                Toast.makeText(context, error, Toast.LENGTH_LONG).show();
                call.cancel();

                // callback - finished with ERROR
                Log.d(TAG, "RetrofitWeatherAsync work done with ERROR - calling callback ..");
                retrofitWeatherInterface.OnRetrofitError(error);
            }

        });

    }
    
}
