package cz.vancura.weatherwidget;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

// Activity to request Permission (we can not get it directly from Widget)

public class GetPermissionActivity extends AppCompatActivity {

    private static String TAG = "myTAG-GetPermissionActivity";
    private final int MY_PERMISSIONS_REQUEST = 22;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perm);

        Log.d(TAG, "onCreate");

        // check permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Persmission not granted");
            // request it
            requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS_REQUEST);
        }else {
            // ok
            // activity end
            finish();
        }

    }


    // Rermission callback
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        switch (requestCode) {
            case 22 : {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay!
                    Log.d(TAG, "Permission granted in runtime by user");
                    // activity end
                    finish();
                } else {
                    // permission denied, boo!
                    Log.d(TAG, "Permission denied in runtime by user - end");
                    // activity end
                    finish();
                }
                return;
            }
        }
    }


}