package com.example.strada;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    SimpleCursorAdapter dataAdapter;
    Handler h = new Handler();

    static final int MY_PERMISSIONS_REQUEST = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(this, //prompt the user to enable permissions
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_EXTERNAL_STORAGE
                    , Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST);
        }

        LocalBroadcastReceiver receiver = new LocalBroadcastReceiver(); //set up the local broadcast
        IntentFilter filter = new IntentFilter("com.example.strada.MY_LOCAL_CUSTOM_BROADCAST");
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, filter);

        getContentResolver(). //register content provider
                registerContentObserver(
                StradaProviderContract.ALL_URI,
                true,
                new StradaObserver(h));

        queryRun(); //query the runs to fill the list view

        final ListView lv = (ListView) findViewById(R.id.runListView);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() { //add on click listener for the list view
            public void onItemClick(AdapterView<?> myAdapter,
                                    View myView,
                                    int myItemInt,
                                    long mylng) { //allow the runs in the list view to be clicked
                Cursor selectedFromList = (Cursor) lv.getItemAtPosition(myItemInt); //get values from the clicked run
                int ID = selectedFromList.getInt(selectedFromList.getColumnIndexOrThrow(StradaProviderContract._ID));
                String name = selectedFromList.getString(selectedFromList.getColumnIndexOrThrow(StradaProviderContract.NAME));
                String date = selectedFromList.getString(selectedFromList.getColumnIndexOrThrow(StradaProviderContract.DATE));
                String distance = selectedFromList.getString(selectedFromList.getColumnIndexOrThrow(StradaProviderContract.DISTANCE));
                String duration = selectedFromList.getString(selectedFromList.getColumnIndexOrThrow(StradaProviderContract.DURATION));
                String pace = selectedFromList.getString(selectedFromList.getColumnIndexOrThrow(StradaProviderContract.PACE));
                String comments = selectedFromList.getString(selectedFromList.getColumnIndexOrThrow(StradaProviderContract.COMMENTS));

                Bundle bundle = new Bundle(); //add all the values to the bundle
                bundle.putInt("function", 1);
                bundle.putInt("ID", ID);
                bundle.putString("name", name);
                bundle.putString("date", date);
                bundle.putString("distance", distance);
                bundle.putString("duration", duration);
                bundle.putString("pace", pace);
                bundle.putString("comments", comments);
                Intent intent = new Intent(MainActivity.this, EditRunActivity.class);
                intent.putExtras(bundle);
                startActivity(intent); //start single run activity with the values of the run clicked
            }
        });
    }

    public void queryRun(){ //query the run table for all the runs
        String sortOrder = StradaProviderContract.DATE + " DESC"; //show the most recent first

        String[] projection = new String[]{
                StradaProviderContract._ID,
                StradaProviderContract.NAME,
                StradaProviderContract.DATE,
                StradaProviderContract.DISTANCE,
                StradaProviderContract.DURATION,
                StradaProviderContract.PACE,
                StradaProviderContract.COMMENTS
        };

        String colsToDisplay [] = new String[]{
                StradaProviderContract.NAME,
                StradaProviderContract.DATE,
                StradaProviderContract.DISTANCE,
                StradaProviderContract.DURATION,
                StradaProviderContract.PACE
        };

        int[] colResIds = new int[]{
                R.id.nameTextView,
                R.id.dateTextView,
                R.id.distanceTextView,
                R.id.durationTextView,
                R.id.speedTextView
        };

        Cursor cursor = getContentResolver().query(StradaProviderContract.RUN_URI, projection, null, null, sortOrder);

        dataAdapter = new SimpleCursorAdapter(
                this,
                R.layout.run_layout,
                cursor,
                colsToDisplay,
                colResIds,
                0);

        ListView listView = (ListView) findViewById(R.id.runListView);
        listView.setAdapter(dataAdapter);
    }

    public void onRecordButtonClick(View v){ //start the record activity
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction("com.example.strada.MY_LOCAL_CUSTOM_BROADCAST");
        LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent); //send the local broadcast to start the service

        Intent intent = new Intent(MainActivity.this, RecordActivity.class);
        startActivity(intent);
    }

    public void onStatsButtonClick(View v){ //start the statistics activity
        Intent intent = new Intent(MainActivity.this, StatisticsActivity.class);
        startActivity(intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        queryRun(); //query the runs again to show any updates
    }
}
