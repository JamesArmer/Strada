package com.example.strada;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class EditRunActivity extends AppCompatActivity {

    Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_run);

        bundle = getIntent().getExtras();
        String name = bundle.getString("name");
        String date = bundle.getString("date");
        String distance = bundle.getString("distance");
        String duration = bundle.getString("duration");
        String speed = bundle.getString("speed");
        String comments = bundle.getString("comments");

        TextView nameTV = (TextView) findViewById(R.id.nameTextView);
        TextView dateTV = (TextView) findViewById(R.id.dateTextView);
        TextView distanceTV = (TextView) findViewById(R.id.distanceTextView);
        TextView durationTV = (TextView) findViewById(R.id.durationTextView);
        TextView speedTV = (TextView) findViewById(R.id.speedTextView);
        EditText commentsET = (EditText) findViewById(R.id.commentsEditText);

        nameTV.setText(name);
        dateTV.setText(date);
        distanceTV.setText(distance);
        durationTV.setText(duration);
        speedTV.setText(speed);
        commentsET.setText(comments);
    }

    public void onSaveButtonClick(View v){
        EditText commentsET = (EditText) findViewById(R.id.commentsEditText);
        String comments = commentsET.getText().toString();
        bundle.putString("comments", comments);

        Intent result = new Intent();
        result.putExtras(bundle);
        setResult(Activity.RESULT_OK, result);
        finish();
    }
}
