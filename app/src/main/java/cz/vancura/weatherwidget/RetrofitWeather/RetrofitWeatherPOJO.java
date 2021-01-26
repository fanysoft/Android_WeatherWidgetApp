package cz.vancura.weatherwidget.RetrofitWeather;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;


// Class structure based on JSON returned from API

public class RetrofitWeatherPOJO {

    // JSON - level 1

    @SerializedName("lat")
    @Expose
    public Double lat;

    @SerializedName("lon")
    @Expose
    public Double lon;

    @SerializedName("timezone")
    @Expose
    public String timezone;

    @SerializedName("timezone_offset")
    @Expose
    public Integer timezoneOffset;

    @SerializedName("current")
    @Expose
    public Current current;

    @SerializedName("minutely")
    @Expose
    public List<Minutely> minutely = null;

    @SerializedName("hourly")
    @Expose
    public List<Hourly> hourly = null;

    @SerializedName("daily")
    @Expose
    public List<Daily> daily = null;

    @SerializedName("alerts")
    @Expose
    public List<Alert> alerts = null;


    // JSON - level 2

    public class Current {

        @SerializedName("dt")
        @Expose
        public Integer dt;
        @SerializedName("sunrise")
        @Expose
        public Integer sunrise;
        @SerializedName("sunset")
        @Expose
        public Integer sunset;
        @SerializedName("temp")
        @Expose
        public Double temp;
        @SerializedName("feels_like")
        @Expose
        public Double feelsLike;
        @SerializedName("pressure")
        @Expose
        public Integer pressure;
        @SerializedName("humidity")
        @Expose
        public Integer humidity;
        @SerializedName("dew_point")
        @Expose
        public Double dewPoint;
        @SerializedName("uvi")
        @Expose
        public Double uvi;
        @SerializedName("clouds")
        @Expose
        public Integer clouds;
        @SerializedName("visibility")
        @Expose
        public Integer visibility;
        @SerializedName("wind_speed")
        @Expose
        public Double windSpeed;
        @SerializedName("wind_deg")
        @Expose
        public Integer windDeg;
        @SerializedName("weather")
        @Expose
        public List<Weather> weather = null;

    }

    public class Minutely {

        @SerializedName("dt")
        @Expose
        public Integer dt;

        @SerializedName("precipitation")
        @Expose
        public Double precipitation;

    }

    public class Hourly {

        @SerializedName("dt")
        @Expose
        public Integer dt;
        @SerializedName("temp")
        @Expose
        public Double temp;
        @SerializedName("feels_like")
        @Expose
        public Double feelsLike;
        @SerializedName("pressure")
        @Expose
        public Integer pressure;
        @SerializedName("humidity")
        @Expose
        public Integer humidity;
        @SerializedName("dew_point")
        @Expose
        public Double dewPoint;
        @SerializedName("uvi")
        @Expose
        public Double uvi;
        @SerializedName("clouds")
        @Expose
        public Integer clouds;
        @SerializedName("visibility")
        @Expose
        public Integer visibility;
        @SerializedName("wind_speed")
        @Expose
        public Double windSpeed;
        @SerializedName("wind_deg")
        @Expose
        public Integer windDeg;
        @SerializedName("weather")
        @Expose
        public List<Weather> weather = null;
        @SerializedName("pop")
        @Expose
        public double pop;
        @SerializedName("snow")
        @Expose
        public Snow snow;

    }

    public class Daily {

        @SerializedName("dt")
        @Expose
        public Integer dt;
        @SerializedName("sunrise")
        @Expose
        public Integer sunrise;
        @SerializedName("sunset")
        @Expose
        public Integer sunset;
        @SerializedName("temp")
        @Expose
        public Temp temp;
        @SerializedName("feels_like")
        @Expose
        public FeelsLike feelsLike;
        @SerializedName("pressure")
        @Expose
        public Integer pressure;
        @SerializedName("humidity")
        @Expose
        public Integer humidity;
        @SerializedName("dew_point")
        @Expose
        public Double dewPoint;
        @SerializedName("wind_speed")
        @Expose
        public Double windSpeed;
        @SerializedName("wind_deg")
        @Expose
        public Integer windDeg;
        @SerializedName("weather")
        @Expose
        public List<Weather> weather = null;
        @SerializedName("clouds")
        @Expose
        public Integer clouds;
        @SerializedName("pop")
        @Expose
        public double pop;
        @SerializedName("uvi")
        @Expose
        public double uvi;
        @SerializedName("snow")
        @Expose
        public Double snow;

    }

    public class Alert {

        @SerializedName("sender_name")
        @Expose
        public String senderName;
        @SerializedName("event")
        @Expose
        public String event;
        @SerializedName("start")
        @Expose
        public Integer start;
        @SerializedName("end")
        @Expose
        public Integer end;
        @SerializedName("description")
        @Expose
        public String description;

    }


    // JSON - level 3
    public class Weather {

        @SerializedName("id")
        @Expose
        public Integer id;

        @SerializedName("main")
        @Expose
        public String main;

        @SerializedName("description")
        @Expose
        public String description;

        @SerializedName("icon")
        @Expose
        public String icon;

    }

    public class Snow {

        @SerializedName("1h")
        @Expose
        public Double _1h;

    }

    public class Temp {

        @SerializedName("day")
        @Expose
        public Double day;
        @SerializedName("min")
        @Expose
        public Double min;
        @SerializedName("max")
        @Expose
        public Double max;
        @SerializedName("night")
        @Expose
        public Double night;
        @SerializedName("eve")
        @Expose
        public Double eve;
        @SerializedName("morn")
        @Expose
        public Double morn;

    }


    public class FeelsLike {

        @SerializedName("day")
        @Expose
        public Double day;
        @SerializedName("night")
        @Expose
        public Double night;
        @SerializedName("eve")
        @Expose
        public Double eve;
        @SerializedName("morn")
        @Expose
        public Double morn;

    }


}
