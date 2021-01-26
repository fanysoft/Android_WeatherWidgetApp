package cz.vancura.weatherwidget.RetrofitWeather;

public interface RetrofitWeatherInterface {
    void OnRetrofitCompleted();
    void OnRetrofitError(String error);
}
