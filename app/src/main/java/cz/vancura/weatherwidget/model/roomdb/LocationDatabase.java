package cz.vancura.weatherwidget.model.roomdb;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import cz.vancura.weatherwidget.model.Location;

@Database(entities = {Location.class}, version = 3)
public abstract class LocationDatabase extends RoomDatabase {

    public abstract LocationDao locationDao();
    private static LocationDatabase dbInstance;

    // create dB
    public static LocationDatabase getDatabase(final Context context) {

        if (dbInstance == null) {
            synchronized (LocationDatabase.class) {
                if (dbInstance == null) {
                    // Create database here
                    dbInstance = Room.databaseBuilder(context.getApplicationContext(), LocationDatabase.class, "location_database")
                            .fallbackToDestructiveMigration()
                            //.addCallback(sRoomDatabaseCallback)
                            .build();
                }
            }
        }
        return dbInstance;
    }


}
