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

    // Insert
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertLocation(Location... locations);

}
