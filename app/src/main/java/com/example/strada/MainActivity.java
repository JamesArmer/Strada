package com.example.strada;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
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
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    SimpleCursorAdapter dataAdapter;
    Handler h = new Handler();

    static final int EDIT_RUN_RESULT_CODE = 1;

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

        final ListView lv = (ListView) findViewById(R.id.runListView);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() { //add on click listener for the list view
            public void onItemClick(AdapterView<?> myAdapter,
                                    View myView,
                                    int myItemInt,
                                    long mylng) {
                Cursor selectedFromList = (Cursor) lv.getItemAtPosition(myItemInt); //get values from the clicked recipe
                int ID = selectedFromList.getInt(selectedFromList.getColumnIndexOrThrow(StradaProviderContract._ID));
                String name = selectedFromList.getString(selectedFromList.getColumnIndexOrThrow(StradaProviderContract.NAME));
                String date = selectedFromList.getString(selectedFromList.getColumnIndexOrThrow(StradaProviderContract.DATE));
                String distance = selectedFromList.getString(selectedFromList.getColumnIndexOrThrow(StradaProviderContract.DISTANCE));
                String duration = selectedFromList.getString(selectedFromList.getColumnIndexOrThrow(StradaProviderContract.DURATION));
                String pace = selectedFromList.getString(selectedFromList.getColumnIndexOrThrow(StradaProviderContract.PACE));
                String comments = selectedFromList.getString(selectedFromList.getColumnIndexOrThrow(StradaProviderContract.COMMENTS));

                Bundle bundle = new Bundle();
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
                startActivityForResult(intent,EDIT_RUN_RESULT_CODE); //start single run activity with the ID of the run clicked
            }
        });
    }

    public void queryRun(){
        String sortOrder = StradaProviderContract.DATE + " DESC";

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

    public void onRecordButtonClick(View v){
        Intent intent = new Intent(MainActivity.this, RecordActivity.class);
        startActivity(intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case(EDIT_RUN_RESULT_CODE):
                if(resultCode == Activity.RESULT_OK){
                    Bundle bundle = data.getExtras();
                }
                break;
        }
        queryRun();
    }
}
