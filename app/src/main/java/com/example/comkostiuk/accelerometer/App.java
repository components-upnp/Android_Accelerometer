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

public class App extends AppCompatActivity {

    private SensorManager sensorManager = null;
    private Sensor acclerometer = null;
    private ServiceConnection serviceConnection;
    private Service service;

    StringBuilder builder = new StringBuilder();

    float [] history = new float[2];
    String [] direction = {"NONE","NONE"};

    final SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            float xChange = history[0] - event.values[0];
            float yChange = history[1] - event.values[1];

            history[0] = event.values[0];
            history[1] = event.values[1];

            if (xChange > 0.3){
                direction[0] = "LEFT";
            }
            else if (xChange < -2){
                direction[0] = "RIGHT";
            }

            if (yChange > 2){
                direction[1] = "DOWN";
            }
            else if (yChange < -2){
                direction[1] = "UP";
            }

            builder.setLength(0);
            builder.append("x: ");
            builder.append(direction[0]);
            builder.append(" y: ");
            builder.append(direction[1]);

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
        acclerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        sensorManager.registerListener(sensorEventListener,
                acclerometer,
                SensorManager.SENSOR_DELAY_FASTEST);

        service = new Service();
        serviceConnection = service.getService();

        getApplicationContext().bindService(
                new Intent(this, AndroidUpnpServiceImpl.class),
                serviceConnection,
                Context.BIND_AUTO_CREATE
        );
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
}
