package cz.vancura.weatherwidget.RetrofitWeather;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RetrofitWeatherAPIInterface {

    // GET s URL params
    // full URl example https://api.openweathermap.org/data/2.5/onecall?lat=50.08804&lon=14.42076&appid=3bb0b846b54199e0bb7cbb55f8875c61
    @GET("/data/2.5/onecall?") // Url konec - cely https://reqres.in/api/users?
    Call<RetrofitWeatherPOJO> doGetWeather(@Query("lat") double urlParamLat,
                                           @Query("lon") double urlParamLon,
                                           @Query("appid") String urlParamAppId,
                                           @Query("units") String urlParamUnits
    );


}
