package cz.vancura.weatherwidget.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Location {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "GPS_lat")
    double GPSlat;

    @ColumnInfo(name = "GPS_lon")
    double GPSlon;

    @ColumnInfo(name = "GeoCoding")
    String GeoCoding;


    public Location(double GPSlat, double GPSlon, String GeoCoding) {
        this.GPSlat = GPSlat;
        this.GPSlon = GPSlon;
        this.GeoCoding = GeoCoding;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getGPSlat() {
        return GPSlat;
    }

    public void setGPSlat(double GPSlat) {
        this.GPSlat = GPSlat;
    }

    public double getGPSlon() {
        return GPSlon;
    }

    public void setGPSlon(double GPSlon) {
        this.GPSlon = GPSlon;
    }

    public String getGeoCoding() {
        return GeoCoding;
    }

    public void setGeoCoding(String geoCoding) {
        GeoCoding = geoCoding;
    }
}





