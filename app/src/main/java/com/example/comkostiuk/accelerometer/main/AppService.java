package com.example.comkostiuk.accelerometer.main;

import android.app.IntentService;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.NotificationCompat;

import com.example.comkostiuk.accelerometer.R;

import org.fourthline.cling.android.AndroidUpnpServiceImpl;
import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import xdroid.toaster.Toaster;


/**
 * Created by mkostiuk on 14/06/2017.
 */


public class AppService extends Service {

    private SensorManager sensorManager = null;
    private Sensor acclerometer = null;
    private com.example.comkostiuk.accelerometer.upnp.Service service;

    public static final int NOTIFICATION_ID = 1;

    public static final String ACTION_1 = "action_1";


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        new Thread().start();

        displayNotification(this);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);

        File dir;
        System.err.println(Build.BRAND);
        if (Build.BRAND.toString().equals("htc_europe"))
            dir = new File("/mnt/emmc/AndroidAccelerometer/");
        else
            dir = new File(Environment.getExternalStorageDirectory().getPath() + "/AndroidAccelerometer/");

        while (!dir.exists()) {
            dir.mkdir();
            dir.setReadable(true);
            dir.setExecutable(true);
            dir.setWritable(true);
        }

        service = new com.example.comkostiuk.accelerometer.upnp.Service();

        getApplicationContext().bindService(
                new Intent(this, AndroidUpnpServiceImpl.class),
                service.getService(),
                Context.BIND_AUTO_CREATE
        );

        //On récupère l'accéléromètre en mode linéaire
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        acclerometer = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);

        //On ajoute un listener à l'accéléromètre, avec une fréquence de lecture de 100 000 µs
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                sensorManager.registerListener(new SensorListener(service.getAccelerometerService(), service.getUdnAccelerometer().toString()),
                        acclerometer,
                        100000);

            }
        }, 3000);


        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }

    //fonction permettant d'afficher une notification test à l'utilisateur lors d'un évènement
    public static void displayNotification(Context context) {

        Intent action1Intent = new Intent(context, NotificationActionService.class)
                .setAction(ACTION_1);

        PendingIntent action1PendingIntent = PendingIntent.getService(context, 0,
                action1Intent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_UPDATE_CURRENT);

        android.support.v4.app.NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.mipmap.ic_launcher_round)
                        .setContentTitle("Accelerometer")
                        .setContentText("Cliquer ici pour fermer l'application")
                        .addAction(new NotificationCompat.Action(R.mipmap.ic_launcher_round,
                                "Fermer", action1PendingIntent))
                        .setContentIntent(action1PendingIntent)
                        .setAutoCancel(true)
                        ;

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
    }


    //Classe interne permettant de créer un service gérant les actions faites sur la notification
    //L'utilisateur peut passer au fichier audio suivant ou précédent
    public static class NotificationActionService extends IntentService {
        public NotificationActionService() {
            super(NotificationActionService.class.getSimpleName());
        }

        @Override
        protected void onHandleIntent(Intent intent) {
            String action = intent.getAction();
            Toaster.toast("Received notification action: " + action);
            if (ACTION_1.equals(action)) {
                // TODO: handle action 1.
                System.out.println("Action 1 notification");

                Intent it = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
                getApplicationContext().sendBroadcast(it);

                System.exit(0);
            }
        }
    }

}
