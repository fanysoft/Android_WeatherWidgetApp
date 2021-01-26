package cz.vancura.weatherwidget.RetrofitWeather;

// My POJO class for Weather

public class MyWeahterPOJO {

    String Info;
    double TempCurrent;
    double HumCurrent;
    double PrercipCurrent;
    String WeatherMainCurrent;
    String WeatherDescCurrent;
    String IconUrl;

    public MyWeahterPOJO(String info, double tempCurrent, double humCurrent, double prercipCurrent, String weatherMainCurrent, String weatherDescCurrent, String iconUrl) {
        Info = info;
        TempCurrent = tempCurrent;
        HumCurrent = humCurrent;
        PrercipCurrent = prercipCurrent;
        WeatherMainCurrent = weatherMainCurrent;
        WeatherDescCurrent = weatherDescCurrent;
        IconUrl = iconUrl;
    }

    // Getter and Setters
    public double getTempCurrent() {
        return TempCurrent;
    }

    public void setTempCurrent(double tempCurrent) {
        TempCurrent = tempCurrent;
    }

    public double getHumCurrent() {
        return HumCurrent;
    }

    public String getInfo() {
        return Info;
    }

    public void setInfo(String info) {
        Info = info;
    }

    public void setHumCurrent(double humCurrent) {
        HumCurrent = humCurrent;
    }

    public double getPrercipCurrent() {
        return PrercipCurrent;
    }

    public void setPrercipCurrent(double prercipCurrent) {
        PrercipCurrent = prercipCurrent;
    }

    public String getWeatherMainCurrent() {
        return WeatherMainCurrent;
    }

    public void setWeatherMainCurrent(String weatherMainCurrent) {
        WeatherMainCurrent = weatherMainCurrent;
    }

    public String getWeatherDescCurrent() {
        return WeatherDescCurrent;
    }

    public void setWeatherDescCurrent(String weatherDescCurrent) {
        WeatherDescCurrent = weatherDescCurrent;
    }

    public String getIconUrl() {
        return IconUrl;
    }

    public void setIconUrl(String iconUrl) {
        IconUrl = iconUrl;
    }
}
