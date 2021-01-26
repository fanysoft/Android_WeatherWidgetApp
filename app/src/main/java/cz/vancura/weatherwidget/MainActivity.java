package cz.vancura.weatherwidget;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import cz.vancura.weatherwidget.model.roomdb.LocationRepository;

public class MainActivity extends AppCompatActivity {

    private static String TAG = "myTAG-MainActivity";

    public static boolean MainActivityIsActive = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "activity started ..");

        MainActivityIsActive = true;


        // empty



    }



    @Override
    protected void onStop() {
        super.onStop();
        MainActivityIsActive = false;
    }


    // Menu - create
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    // Menu - selected
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_stats:

                Log.d(TAG, "menu selected - show statistics ..");

                // launch Map Activity
                Context context = MainActivity.this;
                Class destinationActivity = MapsActivity.class;
                Intent intent = new Intent(context, destinationActivity);
                startActivity(intent);

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }


    }

}

