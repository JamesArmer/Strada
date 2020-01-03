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

        bundle = getIntent().getExtras(); //get extras from the passing activity

        TextView nameET = findViewById(R.id.nameEditText); //find all the views
        TextView dateTV = findViewById(R.id.dateTextView);
        TextView distanceTV = findViewById(R.id.distanceTextView);
        TextView durationTV = findViewById(R.id.durationTextView);
        TextView speedTV = findViewById(R.id.speedTextView);
        EditText commentsET = findViewById(R.id.commentsEditText);

        nameET.setText(bundle.getString("name")); //show the values in the text views
        dateTV.setText(bundle.getString("date"));
        distanceTV.setText(bundle.getString("distance"));
        durationTV.setText(bundle.getString("duration"));
        speedTV.setText(bundle.getString("pace"));
        commentsET.setText(bundle.getString("comments"));
    }

    public void onSaveButtonClick(View v){
        if(bundle.getInt("function")==1){ //if the activity has been started by the main activity
            EditText commentsET = findViewById(R.id.commentsEditText);
            EditText nameET = findViewById(R.id.nameEditText);

            String comments = commentsET.getText().toString();
            String name = nameET.getText().toString();

            String runWhere = StradaProviderContract._ID + " = ?"; //where clause for the run ID
            int runID = bundle.getInt("ID"); //get the passed ID
            String[] runIDArg = {""+runID}; //put into a string list

            ContentValues update = new ContentValues(); //update the name and comments
            update.put(StradaProviderContract.NAME, name);
            update.put(StradaProviderContract.COMMENTS, comments);
            getContentResolver().update(StradaProviderContract.RUN_URI, update, runWhere, runIDArg);

            Intent result = new Intent();
            result.putExtras(bundle);
            setResult(Activity.RESULT_OK, result);
            finish(); //return to the main activity
        }

        if(bundle.getInt("function")==2){ //if the activity has been started from the record activity
            EditText commentsET = findViewById(R.id.commentsEditText);
            EditText nameET = findViewById(R.id.nameEditText);

            String comments = commentsET.getText().toString();
            String name = nameET.getText().toString();

            ContentValues insert = new ContentValues(); //insert a new entry into run
            insert.put(StradaProviderContract.NAME, name);
            insert.put(StradaProviderContract.DATE, bundle.getString("date"));
            insert.put(StradaProviderContract.DISTANCE, bundle.getString("distance"));
            insert.put(StradaProviderContract.DURATION, bundle.getString("duration"));
            insert.put(StradaProviderContract.PACE, bundle.getString("pace"));
            insert.put(StradaProviderContract.COMMENTS, comments);
            getContentResolver().insert(StradaProviderContract.RUN_URI, insert);

            Intent intent = new Intent(EditRunActivity.this, MainActivity.class);
            intent.putExtras(bundle);
            startActivity(intent); //gp back to the main activity
        }

    }

    public void onDeleteButtonClick(View v){
        if(bundle.getInt("function") == 2){ //if it's a new entry then no need to delete
            finish();
        }

        int runID = bundle.getInt("ID"); //delete the run by ID
        String[] runIDArg = {""+runID};
        String runWhere = StradaProviderContract._ID +" = ?";
        getContentResolver().delete(StradaProviderContract.RUN_URI, runWhere, runIDArg);
        finish();
    }
}
