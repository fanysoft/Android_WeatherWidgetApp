package cz.vancura.weatherwidget.RetrofitGeoCoding;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RetrofitGeoCodingRepo {

    private static final String TAG = "myTAG-RetrofitRepoGeoLocation";

    public static String GeoLocationResult = "";

    // listener
    static RetrofitGeoCodingInterface retrofitGeoCodingInterface;

    public RetrofitGeoCodingRepo(RetrofitGeoCodingInterface retrofitGeoCodingInterface) {
        this.retrofitGeoCodingInterface = retrofitGeoCodingInterface;
    }

    // Translate GPS lat and long to adress via GeoCoding API Http
    @SuppressLint("LongLogTag")
    public static void RetrofitGeoTranlateAsyns(double gpsLatitude, double gpsLongtitude, final Context context) {

        Log.d(TAG, "RetrofitGeoTranlate lat=" + gpsLatitude + " long=" + gpsLongtitude);

        RetrofitGeoCodingAPIInterface apiInterface = RetrofitGeoCodingAPIClient.getClient().create(RetrofitGeoCodingAPIInterface.class);

        String myAPIkey = "AIzaSyBnqcCs485jaJz703mtwE4Zo-_nj6tDSnI"; // can be used only for this project and computer

        // Retrofit HTTP call with params - Url params syntax ?latlng=40.714224,-73.961452&key=YOUR_API_KEY
        String UrlParam1 = gpsLatitude + "," + gpsLongtitude;
        String UrlParam2 = myAPIkey;

        Call<RetrofitGeoCodingPOJO> call = apiInterface.translateGPS(UrlParam1, UrlParam2);
        call.enqueue(new Callback<RetrofitGeoCodingPOJO>() {
            @Override
            public void onResponse(Call<RetrofitGeoCodingPOJO> call, Response<RetrofitGeoCodingPOJO> response) {

                int responseCode = response.code();
                Log.d(TAG,"Retrofit Response code=" + responseCode);

                String displayResponse = "";

                RetrofitGeoCodingPOJO apIresponsePOJO = response.body();

                List<RetrofitGeoCodingPOJO.Result> apIresponsePOJOresultsList = apIresponsePOJO.results;

                RetrofitGeoCodingPOJO.Result result = apIresponsePOJOresultsList.get(0);
                String adress = result.formattedAddress;
                displayResponse += adress;
                Log.d(TAG, adress);

                // problem v jejich datech :
                // pozice napr mesta neni na stalem miste v JSON response - pozice je lisi podle velikosti mesta, napr v Praze je na pozici cca 7, v Plzni cca 6 a v Oseku cca 5
                // zacina se od nejnizsi jednotky - cislo ulice, ulice, ctrvt, mesto, kraj etc
                // da se vyresit protoze v JSON je popis dat - ale i tak nedobry
                // see https://stackoverflow.com/questions/27524422/get-definite-city-name-in-google-maps-reverse-geocoding


                 /*
                 Praha
                 https://maps.googleapis.com/maps/api/geocode/json?latlng=50.0867132,14.4538156&key=AIzaSyBnqcCs485jaJz703mtwE4Zo-_nj6tDSnI
                 result Roháčova 2-22, 130 00 Praha 3-Žižkov, Czechia

                 Plzen
                 https://maps.googleapis.com/maps/api/geocode/json?latlng=49.73843,13.3736367&key=AIzaSyBnqcCs485jaJz703mtwE4Zo-_nj6tDSnI
                 result Soukenická 5, 301 00 Plzeň 3, Czechia

                 Velky Osek
                 https://maps.googleapis.com/maps/api/geocode/json?latlng=50.0986683,15.1862917&key=AIzaSyBnqcCs485jaJz703mtwE4Zo-_nj6tDSnI
                 result U Železárny 616, 28151 Velký Osek, 281 51 Velký Osek, Czechia
                  */


                Log.d(TAG, displayResponse);
                GeoLocationResult = displayResponse;

                // callback - finished
                Log.d(TAG, "RetrofitGeoTranlateAsyns work done - calling callback ..");
                retrofitGeoCodingInterface.OnRetrofitCompleted();

            }

            @Override
            public void onFailure(Call<RetrofitGeoCodingPOJO> call, Throwable t) {

                String error = "Error " + t.getLocalizedMessage();
                Log.e(TAG, error);
                Toast.makeText(context, error, Toast.LENGTH_LONG).show();
                call.cancel();
                GeoLocationResult = error;

                // callback - finished with ERROR
                Log.d(TAG, "RetrofitGeoTranlateAsyns work done with ERROR - calling callback ..");
                retrofitGeoCodingInterface.OnRetrofitError(error);

            }
        });


    }


}
