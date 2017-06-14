package com.example.comkostiuk.accelerometer.main;

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
import org.fourthline.cling.android.AndroidUpnpServiceImpl;
import java.io.File;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by mkostiuk on 14/06/2017.
 */

public class AppService extends Service {

    private SensorManager sensorManager = null;
    private Sensor acclerometer = null;
    private com.example.comkostiuk.accelerometer.upnp.Service service;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        new Thread().start();

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


        return START_STICKY;
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }

}
