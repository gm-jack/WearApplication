package com.rtmap.gm.wearapplication;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.WearableListenerService;

import me.denley.courier.Courier;

import static android.hardware.Sensor.TYPE_AMBIENT_TEMPERATURE;
import static android.hardware.Sensor.TYPE_HEART_RATE;
import static android.hardware.Sensor.TYPE_STEP_COUNTER;
import static android.hardware.SensorManager.SENSOR_DELAY_NORMAL;

public class RateService extends WearableListenerService implements SensorEventListener {
    private Sensor mTempeartureSensor;
    private Sensor mStepSensor;
    private Sensor mHrSensor;
    private SensorManager mSensorManager;
    private String step = "--";
    private String heart = "--";
    private String tempearture = "--";
    private final LocationManager mLocationManager;

    public RateService() {
        Log.d("rateService", "RateService");
        mLocationManager = (LocationManager) MyApplication.getInstance().getSystemService(Context.LOCATION_SERVICE);
        mSensorManager = (SensorManager) MyApplication.getInstance().getSystemService(Context.SENSOR_SERVICE);
        mHrSensor = mSensorManager.getDefaultSensor(TYPE_HEART_RATE);
        mStepSensor = mSensorManager.getDefaultSensor(TYPE_STEP_COUNTER);
        mTempeartureSensor = mSensorManager.getDefaultSensor(TYPE_AMBIENT_TEMPERATURE);

        getLocation();
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    }


    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("rateService", "onCreate ");
        if (mStepSensor != null)
            mSensorManager.registerListener(this, mStepSensor, SENSOR_DELAY_NORMAL);
        if (mHrSensor != null)
            mSensorManager.registerListener(this, mHrSensor, SENSOR_DELAY_NORMAL);
        if (mTempeartureSensor != null)
            mSensorManager.registerListener(this, mTempeartureSensor, SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onPeerConnected(Node node) {
        super.onPeerConnected(node);
        Log.d("rateService", "onPeerConnected ");
    }

    @Override
    public void onDestroy() {
        Log.d("rateService", "onDestroy ");
        mSensorManager.unregisterListener(this);
        super.onDestroy();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Log.d("rateService", "sensor event: " + event.sensor.getType() + " = " + event.values[0]);
        if (event.sensor.getType() == TYPE_HEART_RATE) {
            if (event.accuracy == 2 || event.accuracy == 3) {
                heart = String.valueOf(event.values[0]);
            }
        }
        if (event.sensor.getType() == TYPE_HEART_RATE) {
            if (event.accuracy == 3)
                step = String.valueOf(event.values[0]);
        }
        if (event.sensor.getType() == TYPE_AMBIENT_TEMPERATURE) {
            if (event.accuracy == 3)
                tempearture = String.valueOf(event.values[0]);
        }
        Courier.deliverMessage(this, "sensor", step + "@" + heart);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
