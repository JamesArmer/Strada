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
