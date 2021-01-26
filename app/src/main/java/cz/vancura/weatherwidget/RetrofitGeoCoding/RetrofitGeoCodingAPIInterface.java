package cz.vancura.weatherwidget.RetrofitGeoCoding;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RetrofitGeoCodingAPIInterface {

    @GET("maps/api/geocode/json") // Url konec
    Call<RetrofitGeoCodingPOJO> translateGPS(@Query("latlng") String latlng, @Query("key") String apikey);


}
