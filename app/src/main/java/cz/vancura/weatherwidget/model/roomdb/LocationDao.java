package cz.vancura.weatherwidget.model.roomdb;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import cz.vancura.weatherwidget.model.Location;


@Dao
public interface LocationDao {

    // Read
    @Query("SELECT * FROM location")
    List<Location> getLocations();

    // Read only unique GPS locations
    @Query("SELECT * FROM location group by GPS_lat, GPS_lon")
    // https://stackoverflow.com/questions/66966918/room-db-distinct-for-combination-of-2-fields-gps
    // use groub by or DISTINCT - "GPS_lat" and "GPS_lon"
    List<Location> getUniqueLocations();

    // Insert
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertLocation(Location... locations);

}
