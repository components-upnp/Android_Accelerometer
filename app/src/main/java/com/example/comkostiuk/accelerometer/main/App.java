package com.example.comkostiuk.accelerometer.main;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.example.comkostiuk.accelerometer.R;


public class App extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app);

        startService(new Intent(this, AppService.class));

        finish();
    }
}
