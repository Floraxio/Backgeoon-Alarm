package com.oby.cordova.plugin;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import android.app.NotificationManager;
import java.lang.Thread;



import android.widget.Toast;
import android.util.Log;

public class Backservice extends Service {
    private static final String TAG = "Backservice";
    /* ever running ? */
    private boolean isRunning;


    private NotificationManager mNM;

    // Unique Identification Number for the Notification.
    // We use it on Notification start, and to cancel it.
    private int NOTIFICATION = R.string.local_service_started;



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
        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();

    }


    /**
     * Show a notification while this service is running.
     */
    private void showNotification() {
        // In this sample, we'll use the same text for the ticker and the expanded notification
        CharSequence text = "Service Start";

        // The PendingIntent to launch our activity if the user selects this notification
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, LocalServiceActivities.Controller.class), 0);

        // Set the info for the views that show in the notification panel.
        Notification notification = new Notification.Builder(this)
                .setSmallIcon(android.R.drawable.ic_menu_mylocation)  // the status icon
                .setTicker(text)  // the status text
                .setWhen(System.currentTimeMillis())  // the time stamp
                .setContentTitle(getText(R.string.local_service_label))  // the label of the entry
                .setContentText(text)  // the contents of the entry
                .setContentIntent(contentIntent)  // The intent to send when the entry is clicked
                .build();

        // Send the notification.
        mNM.notify(NOTIFICATION, notification);
    }

    /* Master entry of the service */
    public void handleStart(Intent intent, int startId){
        Log.v(TAG, "handleStart Service.");

        Toast.makeText(this, " handleStart", Toast.LENGTH_LONG).show();


        if(!this.isRunning) {
            Log.v(TAG, "diff - isRunning.");

            this.isRunning = true;
            //start the process
            this.backgroundThread.start();
        } else {
            Log.v(TAG, "ever - isRunning.");
        }


        return START_STICKY;
    }
    /* Master task of the service */
    private Runnable myTask = new Runnable() {
        public void run() {
            // Do something here


            Log.v(TAG, "Runnable run thread.");

            stopSelf();
        }
    };
}