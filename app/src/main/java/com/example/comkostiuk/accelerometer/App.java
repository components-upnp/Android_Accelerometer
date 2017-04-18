package com.example.comkostiuk.accelerometer;

import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.comkostiuk.accelerometer.upnp.Service;

import org.fourthline.cling.android.AndroidUpnpServiceImpl;
import org.fourthline.cling.model.meta.ActionArgument;

import java.util.Timer;
import java.util.TimerTask;

public class App extends AppCompatActivity {

    private SensorManager sensorManager = null;
    private Sensor acclerometer = null;
    private ServiceConnection serviceConnection;
    private Service service;
    private boolean emit;

    final SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {

            Direction direction = Direction.AUCUN;


            if (event.values[0] < -4.5 ){
                direction = Direction.DROITE;
                System.out.println("DROITE");
            }
            else if (event.values[0] > 4.5 ){
                direction = Direction.GAUCHE;
                System.out.println("GAUCHE");
            }

            /*if (event.values[1] > 4.5 && (direction != Direction.AUCUN) ){
                direction = Direction.BAS;
                System.out.println("BAS");
            }
            else if ((event.values[1] < -4.5 && (direction != Direction.AUCUN)) ){
                direction = Direction.HAUT;
                System.out.println("HAUT");
            }*/

            if ((service.getAccelerometerService() != null) && emit && (direction != Direction.AUCUN)) {
                emit = false;
                service.getAccelerometerService().getManager().getImplementation().setDirection(direction.toString());
                service.getAccelerometerService().getManager().getImplementation().setDirection("AUCUN");
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        emit = true;
                    }
                },1000);
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            System.err.println("Changement précision!!!!");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        acclerometer = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);

        service = new Service();
        serviceConnection = null;
        emit = true;

        while (serviceConnection == null)
            serviceConnection = service.getService();

        getApplicationContext().bindService(
                new Intent(this, AndroidUpnpServiceImpl.class),
                serviceConnection,
                Context.BIND_AUTO_CREATE
        );

        sensorManager.registerListener(sensorEventListener,
                acclerometer,
                100000);
    }

    @Override
    protected  void onPause() {
        super.onPause();
        sensorManager.unregisterListener(sensorEventListener,
                acclerometer);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(sensorEventListener,
                acclerometer,
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    public static void pause(long ms){
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
    }
}
