package com.example.strada;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.math.BigDecimal;
import java.math.MathContext;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class RecordActivity extends AppCompatActivity implements OnMapReadyCallback {

    private LocationService.MyBinder myService = null;

    private MapView mapView;
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        this.startService(new Intent(this, LocationService.class));
        this.bindService(new Intent(this, LocationService.class),
                serviceConnection, Context.BIND_AUTO_CREATE);//bind to the service

        mapView = (MapView) findViewById(R.id.stradaMapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;

        // Add a marker in Sydney, Australia, and move the camera.
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        Log.d("g53mdp", "Map is showing");
    }

    @Override
    public void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState){
        super.onSaveInstanceState(outState, outPersistentState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    private ServiceConnection serviceConnection = new ServiceConnection()
    {
        @Override
        public void onServiceConnected(ComponentName name, IBinder
                service) {
            Log.d("g53mdp", "MainActivity onServiceConnected");
            myService = (LocationService.MyBinder) service; //connect to the service
            myService.registerCallback(callback);
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d("g53mdp", "MainActivity onServiceDisconnected");
            myService = null;
            myService.unregisterCallback(callback);//disconnect from the service
            myService = null;
        }
    };

    ICallback callback = new ICallback() {
        @Override
        public void locationEvent() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(myService.isRecording()){
                        myService.updateLocation();
                        int dist = myService.getDistance();
                        TextView distanceTV = (TextView) findViewById(R.id.distanceTextView);
                        int km = (int) Math.floor(dist/1000);
                        int metres = dist % 1000;
                        metres /= 10;
                        String mPrint;
                        if(metres < 10){
                            mPrint = "0"+metres;
                        } else {
                            mPrint = ""+metres;
                        }
                        distanceTV.setText(""+km+"."+mPrint+" km");

                        int time = myService.getTime();
                        TextView durationTV = (TextView) findViewById(R.id.durationTextView);
                        int remainder = time % 60;
                        int left = time/60;
                        String leftString;
                        if(left < 10){
                            leftString = "0"+left;
                        } else {
                            leftString = ""+left;
                        }
                        String remainderString;
                        if(remainder < 10){
                            remainderString = "0"+remainder;
                        } else {
                            remainderString = ""+remainder;
                        }
                        durationTV.setText(""+leftString+":"+remainderString);

                        int pace = myService.getCurrentPace();
                        TextView paceTV = (TextView) findViewById(R.id.paceTextView);
                        remainder = pace % 60;
                        left = pace/60;
                        leftString = ""+left;
                        if(remainder < 10){
                            remainderString = "0"+remainder;
                        } else {
                            remainderString = ""+remainder;
                        }
                        paceTV.setText(""+leftString+":"+remainderString+" /km");
                    }
                }
            });
        }
    };

    public void onPlayButtonClick(View v){
        myService.record();
        TextView stateTV = (TextView) findViewById(R.id.stateTextView);
        stateTV.setText("RECORDING");
    }

    public void onPauseButtonClick(View v){
        myService.pause();
        TextView stateTV = (TextView) findViewById(R.id.stateTextView);
        stateTV.setText("PAUSED");
    }

    public void onFinishButtonClick(View v){
        if(myService.isRecording()){
            return;
        }

        int dist = myService.getDistance();
        int km = (int) Math.floor(dist/1000);
        int metres = dist % 1000;
        metres /= 10;
        String mPrint;
        if(metres < 10){
            mPrint = "0"+metres;
        } else {
            mPrint = ""+metres;
        }

        int time = myService.getTime();
        int remainder = time % 60;
        int left = time/60;
        String leftString;
        if(left < 10){
            leftString = "0"+left;
        } else {
            leftString = ""+left;
        }
        String remainderString;
        if(remainder < 10){
            remainderString = "0"+remainder;
        } else {
            remainderString = ""+remainder;
        }

        int iPace = myService.getCurrentPace();
        remainder = iPace % 60;
        left = iPace/60;
        leftString = ""+left;
        if(remainder < 10){
            remainderString = "0"+remainder;
        } else {
            remainderString = ""+remainder;
        }

        String distance = ""+km+"."+mPrint+" km";
        String duration = ""+leftString+"m "+remainderString+"s";
        String pace = ""+leftString+":"+remainderString+" /km";

        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = df.format(c);

        Bundle bundle = new Bundle();
        bundle.putInt("function", 2);
        bundle.putString("distance", distance);
        bundle.putString("duration", duration);
        bundle.putString("pace", pace);
        bundle.putString("date", formattedDate);

        Intent intent = new Intent(RecordActivity.this, EditRunActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(serviceConnection!=null) {
            unbindService(serviceConnection);
            serviceConnection = null;
        }
        mapView.onDestroy();
    }
}
