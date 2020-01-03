package com.example.strada;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.os.Environment;
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
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.MathContext;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static android.os.Environment.DIRECTORY_PICTURES;

public class RecordActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnPolylineClickListener {

    private LocationService.MyBinder myService = null;

    private MapView mapView;
    private GoogleMap mMap;
    private Location currentLocation;
    private Location previousLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        this.bindService(new Intent(this, LocationService.class),
                serviceConnection, Context.BIND_AUTO_CREATE);//bind to the service


        mapView = (MapView) findViewById(R.id.stradaMapView); //set up the mapView
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
    }

    public void createPreviousLocation(){
        previousLocation = myService.getCurrentLocation();
    } //get an initial location for previous location, used in creating the polylines on the map

    @Override
    public void onResume() { //mapView functions that need to be implemented from the onMapReadyCallback
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

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        mMap.setMyLocationEnabled(true); //show location on map
        LatLng wollaton = new LatLng(52.95349, -1.199395); //set an initial location
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(wollaton, 16.0f)); //move the camera with a good zoom level

        mMap.setOnPolylineClickListener(this);

        Log.d("g53mdp", "Map is showing");
    }

    public void moveCamera(){ //move the camera to the current location
        currentLocation = myService.getCurrentLocation();
        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude())));
    }

    public void drawLine(){ //draw a line between the previous and current location
        Polyline polyline1 = mMap.addPolyline(new PolylineOptions()
                .clickable(false)
                .add(new LatLng(previousLocation.getLatitude(), previousLocation.getLongitude()),
                     new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude())));
        previousLocation = currentLocation;
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
        public void locationEvent() { //callback function executed every second that the service is recording
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(myService.isRecording()){
                        moveCamera();
                        drawLine();
                        myService.updateLocation();
                        myService.incrementTime();

                        TextView distanceTV = findViewById(R.id.distanceTextView);
                        String dist = getDistanceString(); //update the distance
                        distanceTV.setText(dist);

                        TextView durationTV = findViewById(R.id.durationTextView);
                        int time = myService.getTime(); //update the time in the correct format
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

                        int pace = myService.getCurrentPace(); //update the pace in the correct format
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

    public String getDistanceString(){ //show the distance in the correct format
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
        return ""+km+"."+mPrint+" km";
    }

    public void onPlayButtonClick(View v){ //start recording and show the user
        createPreviousLocation();
        myService.record();
        TextView stateTV = findViewById(R.id.stateTextView);
        stateTV.setText("RECORDING");
    }

    public void onPauseButtonClick(View v){ //stop recording and show the user
        myService.pause();
        TextView stateTV = findViewById(R.id.stateTextView);
        stateTV.setText("PAUSED");
    }

    public void onFinishButtonClick(View v){
        if(myService.isRecording()){ //if still recording then do nothing
            return;
        }

        int time = myService.getTime(); //show the time in the correct format
        int remainder = time % 60;
        int left = time/60;
        String durationMinutes;
        if(left < 10){
            durationMinutes = "0"+left;
        } else {
            durationMinutes = ""+left;
        }
        String durationRemainder;
        if(remainder < 10){
            durationRemainder = "0"+remainder;
        } else {
            durationRemainder = ""+remainder;
        }

        int iPace = myService.getCurrentPace(); //show the pace in the correct format
        remainder = iPace % 60;
        left = iPace/60;
        String paceMinutes = ""+left;
        String paceRemainder;
        if(remainder < 10){
            paceRemainder = "0"+remainder;
        } else {
            paceRemainder = ""+remainder;
        }

        String distance = getDistanceString();
        String duration = ""+durationMinutes+"m "+durationRemainder+"s";
        String pace = ""+paceMinutes+":"+paceRemainder+" /km";

        Date c = Calendar.getInstance().getTime(); //get the current date in the correct format
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = df.format(c);

        myService.resetTime(); //reset the time to zero if the user wants to record again straight after

        Bundle bundle = new Bundle(); //add values to bundle
        bundle.putInt("function", 2);
        bundle.putString("distance", distance);
        bundle.putString("duration", duration);
        bundle.putString("pace", pace);
        bundle.putString("date", formattedDate);

        Intent intent = new Intent(RecordActivity.this, EditRunActivity.class);
        intent.putExtras(bundle);
        startActivity(intent); //start edit run activity
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(serviceConnection!=null) { //unbind from the service
            unbindService(serviceConnection);
            serviceConnection = null;
        }
        mapView.onDestroy();
        myService.pause();;
    }

    @Override
    public void onPolylineClick(Polyline polyline) {

    }
}
