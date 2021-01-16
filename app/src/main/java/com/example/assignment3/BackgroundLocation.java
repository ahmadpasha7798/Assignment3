package com.example.assignment3;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.HandlerThread;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.preference.PreferenceManager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class BackgroundLocation extends Service {
    FusedLocationProviderClient fusedLocationProviderClient;
    List<locationTodo> items=new ArrayList<locationTodo>();


    private void readOutput() {

        String Filename="test.json";


        try{
            FileInputStream in=openFileInput(Filename);
            InputStreamReader inputStreamReader=new InputStreamReader(in);
            BufferedReader bufferedReader=new BufferedReader(inputStreamReader);
            StringBuilder sb=new StringBuilder();
            String line;

            while((line=bufferedReader.readLine())!=null){
                sb.append(line);
            }

            JSONArray completedArray=new JSONArray(sb.toString());
            for(int i=0;i<completedArray.length();i++){
                JSONObject dummy=completedArray.getJSONObject(i);
                JSONObject temp=dummy.getJSONObject("entry");
                locationTodo obj=new locationTodo();
                obj.Title=temp.getString("Title");
                obj.Description=temp.getString("Description");
                obj.Status=temp.getString("Status");
                obj.locationaware=temp.getBoolean("locationaware");
                obj.location=new LatLng(temp.getDouble("Latitude"), temp.getDouble("Longitude"));
                obj.distance=temp.getInt("Distance");
                items.add(obj);
            }


        } catch (FileNotFoundException e) {
            Toast.makeText(this,"New File!",Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public BackgroundLocation() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("Service","Service Started");
        getLocationUpdates();
        return START_STICKY;
    }

    private void getLocationUpdates() {

        HandlerThread handler=new HandlerThread("ServiceThread");
        handler.start();
        final LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(4000);
        locationRequest.setMaxWaitTime(15 * 1000);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        fusedLocationProviderClient.requestLocationUpdates(locationRequest, new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                SharedPreferences sharedPreferences1= PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());
                boolean allowed1=sharedPreferences1.getBoolean("notify",false);
                if(allowed1)
                if(locationResult!=null)
                {
                    items.clear();
                    readOutput();
                    android.os.Debug.waitForDebugger();
                    Location current=locationResult.getLastLocation();

                    for(int i=0;i<items.size();i++)
                    {
                        if(items.get(i).locationaware&&items.get(i).Status!="Completed"){
                            Location l2=new Location("");
                            l2.setLatitude(items.get(i).location.latitude);
                            l2.setLongitude(items.get(i).location.longitude);
                            float distance=current.distanceTo(l2);
                            int d2=items.get(i).distance;
                            Boolean results=d2>=distance;
                            if(results){
                                //notification

                                Intent intent=new Intent(getApplicationContext(),MainActivity.class);
                                PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),0,intent,0);
                                Intent in=new Intent(getApplicationContext(),NotificationReceiver.class);
                                in.putExtra("message","Stop");
                                PendingIntent buttonPendingIntent=PendingIntent.getBroadcast(getApplicationContext(),1,in,PendingIntent.FLAG_UPDATE_CURRENT);
                                Uri sound= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                                NotificationCompat.Builder builder=new NotificationCompat.Builder(BackgroundLocation.this,"channel");
                                builder.setContentIntent(pendingIntent);
                                builder.setContentTitle("Reminder");
                                builder.setContentText("You are in the vicinity of one of your tasks location");
                                builder.setSmallIcon(R.mipmap.ic_launcher);
                                builder.addAction(R.drawable.checklist, "Stop",buttonPendingIntent);
                                SharedPreferences sharedPreferences= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                                boolean allowed=sharedPreferences.getBoolean("sound",false);
                                if(allowed)
                                    builder.setSound(sound);
                                builder.setAutoCancel(true);
                                Notification notification=builder.build();
                                NotificationManagerCompat notificationManagerCompat=NotificationManagerCompat.from(BackgroundLocation.this);
                                notificationManagerCompat.notify(1,notification);
                            }
                        }
                    }
                }
            }
        }, handler.getLooper());
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            NotificationChannel channel=new NotificationChannel("channel","channel", NotificationManager.IMPORTANCE_HIGH);
            NotificationManager manager=getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
