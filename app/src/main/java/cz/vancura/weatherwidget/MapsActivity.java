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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;

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

        // TODO hide secret key from google_maps_api.xml from GitHub

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    // Manipulates the map once available. This callback is triggered when the map is ready to be used.
    @Override
    public void onMapReady(GoogleMap googleMap) {

        Log.d(TAG, "onMapReady");

        mMap = googleMap;


        // Load data from RoomDB to locationList - Async
        LocationRepository locationRepository = new LocationRepository(this, new ReadAsyncTaskInterface() {
            @Override
            public void onTaskCompleted() {
                Log.d(TAG, "callback recieved - reading from dB finished");

                int listSize = locationList.size();
                Log.d(TAG, "locationList.size=" + listSize);

                if (listSize > 0) {
                    // some data already collected by widget

                    int i = 0;
                    for (Location location : locationList) {

                        Log.d(TAG, "Adding point to map item=" + i + " " +location.getGPSlat() + " " + location.getGPSlon() + " " + location.getGeoCoding());

                        LatLng point = new LatLng(location.getGPSlat(), location.getGPSlon());
                        String descr = location.getGeoCoding();
                        if (location.getGeoCoding().contains("Error") || location.getGeoCoding().contains("ERROR")) {
                            descr = "Missing GeoCoding";
                        }

                        // Map - add point
                        mMap.addMarker(new MarkerOptions()
                                .position(point)
                                .title(descr)
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))); // color of icon


                        // Map - move camera to center of Czech Rep
                        LatLng cameraPoint = new LatLng(49.8112336, 15.3535708);
                        float cameraZoomLevel = (float) 7.0;
                        // Map -center and zoom camera
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(cameraPoint, cameraZoomLevel));

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