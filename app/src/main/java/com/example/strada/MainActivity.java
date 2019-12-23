package com.example.strada;

import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class MainActivity extends AppCompatActivity {

    SimpleCursorAdapter dataAdapter;
    Handler h = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getContentResolver(). //register content provider
                registerContentObserver(
                StradaProviderContract.ALL_URI,
                true,
                new StradaObserver(h));

        queryRun();
    }


    public void queryRun(){
        String[] projection = new String[]{
                StradaProviderContract._ID,
                StradaProviderContract.NAME,
                StradaProviderContract.DATE,
                StradaProviderContract.DISTANCE,
                StradaProviderContract.DURATION,
                StradaProviderContract.SPEED,
                StradaProviderContract.COMMENTS
        };

        String colsToDisplay [] = new String[]{
                StradaProviderContract.NAME,
                StradaProviderContract.DATE,
                StradaProviderContract.DISTANCE,
                StradaProviderContract.DURATION,
                StradaProviderContract.SPEED
        };

        int[] colResIds = new int[]{
                R.id.nameTextView,
                R.id.dateTextView,
                R.id.distanceTextView,
                R.id.durationTextView,
                R.id.speedTextView
        };

        Cursor cursor = getContentResolver().query(StradaProviderContract.RUN_URI, projection, null, null, null);

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
}
