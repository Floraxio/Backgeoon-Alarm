package com.oby.cordova.plugin;

import android.app.Service;
import android.content.Intent;
import android.app.PendingIntent;
import android.os.IBinder;

import android.app.NotificationManager;
import android.app.Notification;

import java.lang.Thread;

import android.widget.Toast;
import android.util.Log;
import android.os.SystemClock;

public class Backservice extends Service {
    private static final String TAG = "Backservice";
    /* ever running ? */
    private boolean isRunning;
    private NotificationManager mNM;
    private Thread backgroundThread;

    // Unique Identification Number for the Notification.
    // We use it on Notification start, and to cancel it.
    private int NOTIFICATION = 666;

    public Backservice() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        this.isRunning = false;
        this.backgroundThread = new Thread(myTask);

        mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        // Display a notification about us starting.  We put an icon in the status bar.
        showNotification();

        Toast.makeText(this, "The new Service was Created", Toast.LENGTH_LONG).show();

    }
    /* old && deprecated api */
    @Override
    public void onStart(Intent intent, int startId) {
        // For time consuming an long tasks you can launch a new thread here...
        handleStart(intent, startId);
    }
    /* new api function */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handleStart(intent, startId);
        return START_NOT_STICKY;
    }
    @Override
    public void onDestroy() {
        this.isRunning = false;
        // Cancel the persistent notification.
        mNM.cancel(NOTIFICATION);
        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();

    }


    /**
     * Show a notification while this service is running.
     */
    private void showNotification() {
        // In this sample, we'll use the same text for the ticker and the expanded notification
        CharSequence text = "Back Service is Start";

        // The PendingIntent to launch our activity if the user selects this notification
            // Build a Notification required for running service in foreground.
        Intent main = new Intent(this, Backgeoonalarm.class);
        main.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, main,  PendingIntent.FLAG_UPDATE_CURRENT);

        //PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, AlarmReceiver.class), 0);

        // Set the info for the views that show in the notification panel.
        Notification notification = new Notification.Builder(this)
                .setSmallIcon(android.R.drawable.ic_menu_mylocation)  // the status icon
                .setTicker(text)  // the status text
                .setWhen(System.currentTimeMillis())  // the time stamp
                .setContentTitle("Geocode en cours..")  // the label of the entry
                .setContentText(text)  // the contents of the entry
                .setContentIntent(pendingIntent)  // The intent to send when the entry is clicked
                .build();

        // Send the notification.
        mNM.notify(NOTIFICATION, notification);
    }

    /* Master entry of the service */
    public int handleStart(Intent intent, int startId){
        Log.v(TAG, "handleStart Service.");

        if(!this.isRunning) {
            Log.v(TAG, "different de - isRunning - relaunch action.");

            this.isRunning = true;
            //start the process
            this.backgroundThread.start();
        } else {
            Log.v(TAG, "ever - isRunning.");
        }

        Toast.makeText(this, " handleStart", Toast.LENGTH_SHORT).show();

        return START_STICKY;
    }
    /* Master task of the service */
    private Runnable myTask = new Runnable() {
        public void run() {
            // Do something here
            Log.v(TAG, "Runnable run thread.");

            



            // my interface :-) 
            SystemClock.sleep(10000); // sleep 10 sec




            // internal stopping the thread.
            stopSelf();
        }
    };
}