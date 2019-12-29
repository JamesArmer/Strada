package com.example.strada;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class EditRunActivity extends AppCompatActivity {

    Bundle bundle;

    SimpleCursorAdapter dataAdapter;
    Handler h = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_run);

        getContentResolver(). //register content provider
                registerContentObserver(
                StradaProviderContract.ALL_URI,
                true,
                new StradaObserver(h));

        bundle = getIntent().getExtras();
        String name = bundle.getString("name");
        String date = bundle.getString("date");
        String distance = bundle.getString("distance");
        String duration = bundle.getString("duration");
        String pace = bundle.getString("pace");
        String comments = bundle.getString("comments");

        TextView nameET = (TextView) findViewById(R.id.nameEditText);
        TextView dateTV = (TextView) findViewById(R.id.dateTextView);
        TextView distanceTV = (TextView) findViewById(R.id.distanceTextView);
        TextView durationTV = (TextView) findViewById(R.id.durationTextView);
        TextView speedTV = (TextView) findViewById(R.id.speedTextView);
        EditText commentsET = (EditText) findViewById(R.id.commentsEditText);

        nameET.setText(name);
        dateTV.setText(date);
        distanceTV.setText(distance);
        durationTV.setText(duration);
        speedTV.setText(pace);
        commentsET.setText(comments);
    }

    public void onSaveButtonClick(View v){
        if(bundle.getInt("function")==1){
            EditText commentsET = (EditText) findViewById(R.id.commentsEditText);
            EditText nameET = (EditText) findViewById(R.id.nameEditText);

            String comments = commentsET.getText().toString();
            String name = nameET.getText().toString();

            String runWhere = StradaProviderContract._ID + " = ?";
            int runID = bundle.getInt("ID");
            String[] runIDArg = {""+runID};

            ContentValues update = new ContentValues();
            update.put(StradaProviderContract.NAME, name);
            update.put(StradaProviderContract.COMMENTS, comments);
            getContentResolver().update(StradaProviderContract.RUN_URI, update, runWhere, runIDArg);

            Intent result = new Intent();
            result.putExtras(bundle);
            setResult(Activity.RESULT_OK, result);
            finish();
        }

        if(bundle.getInt("function")==2){
            EditText commentsET = (EditText) findViewById(R.id.commentsEditText);
            EditText nameET = (EditText) findViewById(R.id.nameEditText);

            String comments = commentsET.getText().toString();
            String name = nameET.getText().toString();

            String runWhere = StradaProviderContract._ID + " = ?";
            int runID = bundle.getInt("ID");
            String[] runIDArg = {""+runID};

            ContentValues insert = new ContentValues();
            insert.put(StradaProviderContract.NAME, name);
            insert.put(StradaProviderContract.DATE, bundle.getString("date"));
            insert.put(StradaProviderContract.DISTANCE, bundle.getString("distance"));
            insert.put(StradaProviderContract.DURATION, bundle.getString("duration"));
            insert.put(StradaProviderContract.PACE, bundle.getString("pace"));
            insert.put(StradaProviderContract.COMMENTS, comments);
            getContentResolver().insert(StradaProviderContract.RUN_URI, insert);

            Intent intent = new Intent(EditRunActivity.this, MainActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
        }

    }

    public void onDeleteButtonClick(View v){
        if(bundle.getInt("function") == 2){
            finish();
        }

        int runID = bundle.getInt("ID");
        String[] runIDArg = {""+runID};
        String runWhere = StradaProviderContract._ID +" = ?";
        getContentResolver().delete(StradaProviderContract.RUN_URI, runWhere, runIDArg);
        finish();
    }
}
