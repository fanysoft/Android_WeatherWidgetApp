package cz.vancura.weatherwidget;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import cz.vancura.weatherwidget.Helper.HelperMethods;
import cz.vancura.weatherwidget.model.Location;
import cz.vancura.weatherwidget.model.roomdb.LocationRepository;
import cz.vancura.weatherwidget.model.roomdb.ReadAsyncTaskInterface;

import static cz.vancura.weatherwidget.model.roomdb.LocationRepository.locationList;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, ReadAsyncTaskInterface {
//public class MapsActivity extends FragmentActivity implements OnMapReadyCallback { - this is without ActionBar

    private static String TAG = "myTAG-MapsActivity";

    private GoogleMap mMap;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        Log.d(TAG, "onCreate");

        context = getApplicationContext();

        // API key for Google map is saved in user gradle.properties to not expose it at GitHub

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    // Manipulates the map once available. This callback is triggered when the map is ready to be used.
    @Override
    public void onMapReady(GoogleMap googleMap) {

        Log.d(TAG, "onMapReady");

        mMap = googleMap;


        // Map - move camera to center of Czech Rep
        LatLng cameraPoint = new LatLng(49.8112336, 15.3535708);
        float cameraZoomLevel = (float) 7.0;
        // Map - center and zoom camera
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(cameraPoint, cameraZoomLevel));


        // Load data from RoomDB to locationList - Async with callback
        LocationRepository locationRepository = new LocationRepository(this, new ReadAsyncTaskInterface() {
            @Override
            public void onTaskCompleted() {
                Log.d(TAG, "callback recieved - reading from dB finished");

                int listSize = locationList.size();
                Log.d(TAG, "locationList.size=" + listSize);

                List<Location> listForMap = new ArrayList<>();

                if (listSize > 0) {
                    // some data already collected by widget

                    int i = 0;
                    for (Location location : locationList) {

                        LatLng point = new LatLng(location.getGPSlat(), location.getGPSlon());

                        // geocoding
                        String geoCoding = location.getGeoCoding();
                        if (geoCoding.contains("Error") || geoCoding.contains("ERROR")) {
                            geoCoding = "Missing GeoCoding";
                        }

                        // date
                        String geoDate = HelperMethods.ConvertEPOCHTimeLong(location.getGeoDate());

                        // age of geoDate - show differenr color for new and older dates
                        BitmapDescriptor bitmapDescriptor = null;
                        int ageType = HelperMethods.JudgeAge(location.getGeoDate());
                        String ageTypeString = "";
                        switch (ageType) {
                            case 1:
                                // new max 1 day old
                                bitmapDescriptor = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE);
                                ageTypeString = "max 1 day old";
                                break;
                            case 2:
                                // max 1 week old
                                bitmapDescriptor = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE);
                                ageTypeString = "max 1 week old";
                                break;
                            case 3:
                                // older than 1 week
                                bitmapDescriptor = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN);
                                ageTypeString = "older than 1 week";
                                break;
                            default:
                                // default
                                bitmapDescriptor = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA);
                                ageTypeString = "default";
                        }

                        Log.d(TAG, "point from dB item=" + i + " " +location.getGPSlat() + " " + location.getGPSlon() + " getGeoCoding=" + location.getGeoCoding() + " geoDate=" + geoDate + " age=" + ageTypeString);


                        // add to map only unique points
                        // step 1 - read from room db only unique GPS locations - done
                        // step 2 - remove last 2 digits from GPS - to remove very close locations like home - done

                        boolean showInMap = true;

                        // 1 original location
                        double pointLatFull = location.getGPSlat();
                        double pointLonFull = location.getGPSlon();

                        // 2. rounded location - removed 2 digits
                        double pointLatRound = (int)(pointLatFull * 100) / 100d;
                        double pointLonRound = (int)(pointLonFull * 100) / 100d;
                        Log.d(TAG, "point from dB item=" + i + " rounded location " + pointLatRound + " " +  pointLonRound);


                        // 3. is in List already ?
                        // loop via list
                        if (listForMap.size()!= 0) {
                            for (Location location1 : listForMap) {

                                if ((location1.getGPSlat() == pointLatRound) && (location1.getGPSlon() == pointLonRound)) {
                                    // already there
                                    showInMap = false;
                                    // do not add to list
                                }
                            }
                        }


                        if (showInMap) {
                            Log.d(TAG, "point - is unique - show in map");

                            // add to list
                            listForMap.add(new Location(pointLatRound, pointLonRound, location.getGeoCoding(), location.getGeoDate()));

                            // Map - add point
                            mMap.addMarker(new MarkerOptions()
                                    .position(point)
                                    .title(geoCoding)
                                    .snippet(geoDate)
                                    .icon(bitmapDescriptor));

                        }else{
                            Log.d(TAG, "point - not adding to map - similar point is already there");
                        }


                        i++;

                    }


                }else{
                    // empty so far

                    View view = findViewById(R.id.map);
                    final Snackbar mySnackbar = Snackbar.make(view, "No data to show - please place Widget to collect some", Snackbar.LENGTH_INDEFINITE);
                    mySnackbar.setAction("Dismiss", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mySnackbar.dismiss();
                        }
                    });
                    mySnackbar.setActionTextColor(ContextCompat.getColor(context, R.color.accent));
                    mySnackbar.show();

                }

            }
        });
        // call Async Job
        locationRepository.getLocations();

    }

    // toto se nepouzije - see above
    @Override
    public void onTaskCompleted() {
        //Log.d(TAG, "callback recieved - finished..");
    }
}