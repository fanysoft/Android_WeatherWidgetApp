package cz.vancura.weatherwidget.model.roomdb;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import cz.vancura.weatherwidget.Helper.HelperMethods;
import cz.vancura.weatherwidget.model.Location;

import static cz.vancura.weatherwidget.MainActivity.MainActivityIsActive;

public class LocationRepository {

    private static String TAG = "myTAG-LocationRepository";

    // dao for interaction with the database
    private LocationDao locationDao;
    // list
    public static List<Location> locationList;
    // db
    static LocationDatabase db;
    // callback for Async - listener
    private static ReadAsyncTaskInterface listener;

    Context context;


    // constructor
    public LocationRepository(Context context, ReadAsyncTaskInterface listener) {
        Log.d(TAG, "constructor");
        db = LocationDatabase.getDatabase(context);
        locationDao = db.locationDao();
        this.context = context;
        locationList = new ArrayList<>();
        this.listener = listener;
    }


    // read wrapper
    public void getLocations() {
        Log.d(TAG, "getLocations ..");
        new ReadAsyncTask().execute();
       }


    // read - async job
    public static class ReadAsyncTask extends AsyncTask<Void, Void, List<Location>> {

        //private WeakReference<MainActivity> activityReference;

        /*
        // only retain a weak reference to the activity
        ReadAsyncTask(MainActivity context) {
            activityReference = new WeakReference<>(context);
        }
         */

        @Override
        protected List<Location> doInBackground(Void... voids) {
            return db.locationDao().getUniqueLocations();
        }

        @Override
        protected void onPostExecute(List<Location> locations) {

            if (locations != null && locations.size() > 0) {
                Log.d(TAG, "Room dB - size=" + locations.size());

                int i = 0;
                for (Location location : locations) {
                    Log.d(TAG, "Room dB - looping locations in dB item=" + i + " " +location.getGPSlat() + " " + location.getGPSlon() + " " + location.getGeoCoding() + " " + location.getGeoDate() + " " + HelperMethods.ConvertEPOCHTimeLong(location.getGeoDate()));
                    i++;
                }

            }else{
                Log.d(TAG, "Room dB - empty");
            }

            locationList = locations;

            // listener call job is done
            listener.onTaskCompleted();

        }
    }



    // insert wrapper
    public void insertLocation (Location location) {
        Log.d(TAG, "insertLocation ..");
        new InsertAsyncTask(location).execute();
    }


    // insert - async job
    private static class InsertAsyncTask extends AsyncTask<Void, Void, Boolean> {

        //private WeakReference<MainActivity> activityReference;
        private Location location;

        // only retain a weak reference to the activity
        InsertAsyncTask(Location location) {
            //activityReference = new WeakReference<>(context);
            this.location = location;
        }

        @Override
        protected Boolean doInBackground(Void... objs) {
            db.locationDao().insertLocation(location);
            locationList.add(location);
            return true;
        }

        // onPostExecute runs on main thread
        @Override
        protected void onPostExecute(Boolean bool) {
            // none
        }
    }


}
