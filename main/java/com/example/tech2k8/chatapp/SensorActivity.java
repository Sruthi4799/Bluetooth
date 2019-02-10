package com.example.tech2k8.chatapp;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class SensorActivity extends AppCompatActivity {

    private SensorManager sensorManager;
    private Sensor sensor;
    private TextView outputView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor);

        outputView=findViewById(R.id.output);
        sensorManager=(SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor=sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                outputView.setText("x: "+event.values[0]+" Y "+event.values[1] +" Z "+event.values[2]);
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        },sensor,100);
    }
}
