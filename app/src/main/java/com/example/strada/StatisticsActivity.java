package com.example.strada;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class StatisticsActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    SimpleCursorAdapter dataAdapter;
    Handler h = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        getContentResolver(). //register content provider
                registerContentObserver(
                StradaProviderContract.ALL_URI,
                true,
                new StradaObserver(h));

        queryStatsRun(null, null); //show all the runs

        Spinner spinner = (Spinner) findViewById(R.id.dateSpinner); //set up the spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.dates_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

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
                Intent intent = new Intent(StatisticsActivity.this, EditRunActivity.class);
                intent.putExtras(bundle);
                startActivity(intent); //start single run activity with the values of the run clicked
            }
        });
    }

    public void queryStatsRun(String selection, String[] selectionArgs){
        String sortOrder = StradaProviderContract.DATE + " DESC"; //show newest runs first

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

        Cursor cursor = getContentResolver().query(StradaProviderContract.RUN_URI, projection, selection, selectionArgs, sortOrder);

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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) { //change selection when the spinner is changed
        String selection = (String) parent.getItemAtPosition(position);
        switch (selection){
            case("All"): //select all the runs
                queryStatsRun(null, null);
                break;
            case("This Week"): //select all the runs this week
                int currentWeek = new Time().getWeekNumber();
                queryStatsRun("DATE(date) >= DATE('now', 'weekday 0', '-7 days')", null);
                break;
            case("This Month"): //select all the runs this month
                DateFormat dateFormat = new SimpleDateFormat("MM");
                Date date = new Date();
                String month = dateFormat.format(date);
                String[] selectionArgs = {month};
                queryStatsRun("strftime('%m', date) = ?", selectionArgs);
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
