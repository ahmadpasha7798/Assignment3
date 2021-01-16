package com.example.assignment3;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import androidx.preference.PreferenceManager;

public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        android.os.Debug.waitForDebugger();
        String message=intent.getStringExtra("message");
        SharedPreferences sharedPreferences= PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());
        sharedPreferences.edit().putBoolean("notify",false).commit();
        Toast.makeText(context,"Stopping Location Service",Toast.LENGTH_SHORT).show();
    }
}
