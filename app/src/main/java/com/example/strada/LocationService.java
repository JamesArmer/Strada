package com.example.strada;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.IInterface;
import android.os.RemoteCallbackList;
import android.util.Log;
import android.widget.TextView;

import androidx.core.app.NotificationCompat;

public class LocationService extends Service {
    Tracking tracker;
    LocationManager locationManager;
    LocationListener locationListener;
    Location currentLocation;

    float distanceTravelled;
    int time;

    protected locationServiceState state;

    public enum locationServiceState{
        RECORDING,
        PAUSED
    }

    RemoteCallbackList<MyBinder> remoteCallbackList = new RemoteCallbackList<MyBinder>();
    private final String CHANNEL_ID = "100";
    private int NOTIFICATION_ID = 001;

    private final IBinder binder = new MyBinder();

    protected class Tracking extends Thread implements  Runnable{

        public boolean running = true;

        public Tracking(){
            this.start();
        }

        public void run(){
            while(this.running){
                try {Thread.sleep(1000);} catch(Exception e) {return;}//thread to do the callbacks for the tracking progress
                doCallbacks();
                time += 1;
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d("g53mdp", "service onBind");

        return new MyBinder();
    }

    public void doCallbacks() {
        final int n = remoteCallbackList.beginBroadcast();
        for (int i=0; i<n; i++) {
            remoteCallbackList.getBroadcastItem(i).callback.locationEvent();//call the locationEvent function
        }
        remoteCallbackList.finishBroadcast();
    }

    public class MyBinder extends Binder implements IInterface
    {
        @Override
        public IBinder asBinder() {
            return this;
        }//interface for the record activity

        public void record(){
            LocationService.this.record();
        }

        public void pause(){
            LocationService.this.pause();
        }

        public boolean isRecording(){
            return LocationService.this.isRecording();
        }

        public void updateLocation(){
            LocationService.this.updateLocation();
        }

        public int getDistance(){ return LocationService.this.getDistance();}

        public int getTime(){ return LocationService.this.getTime();}

        public int getCurrentPace(){ return LocationService.this.getCurrentPace();}

        public int getAveragePace(){ return LocationService.this.getAveragePace();}

        public Location getCurrentLocation(){ return LocationService.this.getCurrentLocation();}

        public void registerCallback(ICallback callback) {
            this.callback = callback;
            remoteCallbackList.register(MyBinder.this);
        }

        public void unregisterCallback(ICallback callback) {
            remoteCallbackList.unregister(MyBinder.this);
        }

        ICallback callback;
    }

    @SuppressLint("MissingPermission")
    public void record(){
        if(this.state == locationServiceState.PAUSED) {
            this.state = locationServiceState.RECORDING;
            currentLocation = locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER);
        }
    }

    public void pause(){
        if(this.state == locationServiceState.RECORDING) {
            this.state = locationServiceState.PAUSED;
        }
    }

    public boolean isRecording(){
        if(this.state == locationServiceState.RECORDING) {
            return true;
        } else {
            return false;
        }
    }

    public void updateLocation(){
        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    5, // minimum time interval between updates
                    5, // minimum distance between updates, in metres
                    locationListener);
            distanceTravelled += currentLocation.distanceTo(locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER));
            currentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            //Log.d("g53mdp", ""+distanceTravelled);
        } catch(SecurityException e) {
            Log.d("g53mdp", e.toString());
        }
    }

    public int getDistance(){
        return (int)distanceTravelled;
    }

    public int getTime(){ return time;}

    public int getCurrentPace(){
        if(distanceTravelled == 0){
            return 0;
        }
        float fTime = (float) time;
        float fDist = distanceTravelled;
        fDist /= 1000;
        float fPace = fTime/fDist;
        int iPace = (int) fPace;
        Log.d("g53mdp", ""+fTime+" / "+fDist+" = "+iPace);
        return iPace;
    }

    public int getAveragePace(){
        if(distanceTravelled == 0){
            return 0;
        }
        float fTime = (float) time;
        float fDist = distanceTravelled;
        fDist /= 1000;
        float fPace = fTime/fDist;
        int iPace = (int) fPace;
        Log.d("g53mdp", ""+fTime+" / "+fDist+" = "+iPace);
        return iPace;
    }

    @SuppressWarnings("MissingPermission")
    public Location getCurrentLocation(){
        return locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    }

    @Override
    public void onCreate(){
        Log.d("g53mdp", "service onCreate");
        super.onCreate();
        tracker = new Tracking();
        distanceTravelled = 0;
        time = 0;
        this.state = locationServiceState.PAUSED;

        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        locationListener = new MyLocationListener();

        NotificationManager notificationManager = (NotificationManager)
                getSystemService(NOTIFICATION_SERVICE);
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Strada channel";
            String description = "Strada Tracker";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name,
                    importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            notificationManager.createNotificationChannel(channel);

            Intent intent= new Intent(this, RecordActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this,
                    CHANNEL_ID)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("Strada")
                    .setContentText("Strada currently tracking location")
                    .setContentIntent(pendingIntent)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);
            startForeground(NOTIFICATION_ID, mBuilder.build());
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("g53mdp", "service onStartCommand");
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d("g53mdp", "service onDestroy");
        super.onDestroy();
    }

    @Override
    public void onRebind(Intent intent) {
        Log.d("g53mdp", "service onRebind");
        super.onRebind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d("g53mdp", "service onUnbind");
        return super.onUnbind(intent);
    }
}
