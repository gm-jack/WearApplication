package com.rtmap.gm.wearapplication;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.BoxInsetLayout;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import me.denley.courier.Courier;
import me.denley.courier.ReceiveMessages;

public class MainActivity extends WearableActivity {

    private static final SimpleDateFormat AMBIENT_DATE_FORMAT =
            new SimpleDateFormat("HH:mm", Locale.US);
    private static final String TAG = "MainActivity";

    private BoxInsetLayout mContainerView;
    private TextView mTextView;
    private TextView mClockView;
    private SensorManager mSensorManager;
    private Sensor mHrSensor;
    private Sensor mStepSensor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setAmbientEnabled();

        mContainerView = (BoxInsetLayout) findViewById(R.id.container);
        mTextView = (TextView) findViewById(R.id.text);
        mClockView = (TextView) findViewById(R.id.clock);

        Log.e(TAG, "检查BODY_SENEORS权限：" + hasPermission());
        if (!hasPermission()) {
            requestPermissions(new String[]{Manifest.permission.BODY_SENSORS, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
        }
        startService(new Intent(this, RateService.class));
//        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
//
//        /*** 遍历出智能手表设备中所以的Seneor ***/
//        List<Sensor> sensors = mSensorManager.getSensorList(Sensor.TYPE_ALL);
//        for (Sensor sensor : sensors) {
//
//            Log.e(TAG,
//                    "遍历sensor:[name=" + sensor.getName() + ";type="
//                            + sensor.getType() + ";vendor="
//                            + sensor.getVendor() + "]");
//        }
//
//        // Sensor.TYPE_HEART_RATE or 65562
//        mHrSensor = mSensorManager.getDefaultSensor(TYPE_HEART_RATE);
//        mStepSensor = mSensorManager.getDefaultSensor(TYPE_STEP_COUNTER);
//        Log.e("TAG", "mHrSensor:" + mHrSensor.toString());
    }

    public boolean hasPermission() {
//        checkPermission()
        PackageManager pm = getPackageManager();
        int permission = pm.checkPermission("android.permission.BODY_SENSORS",
                "org.gztech.heartrate");
        boolean isPer = permission == PackageManager.PERMISSION_GRANTED ? true
                : false;
        return isPer;
    }

    //    @ReceiveMessages("/incoming_sms")
//    public void getHeartRate(String rate) {
//        mTextView.setText(rate);
//    }
    @Override
    protected void onStart() {
        super.onStart();
        Courier.startReceiving(this);
//        mSensorManager.registerListener(this, mHrSensor, 3);
//        mSensorManager.registerListener(this, mStepSensor, SENSOR_DELAY_GAME);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Courier.stopReceiving(this);
//        mSensorManager.unregisterListener(this);
    }


    @Override
    public void onEnterAmbient(Bundle ambientDetails) {
        super.onEnterAmbient(ambientDetails);
        updateDisplay();
    }

    @Override
    public void onUpdateAmbient() {
        super.onUpdateAmbient();
        updateDisplay();
    }

    @Override
    public void onExitAmbient() {
        updateDisplay();
        super.onExitAmbient();
    }

    private void updateDisplay() {
        if (isAmbient()) {
            mContainerView.setBackgroundColor(getResources().getColor(android.R.color.black));
            mTextView.setTextColor(getResources().getColor(android.R.color.white));
            mClockView.setVisibility(View.VISIBLE);

            mClockView.setText(AMBIENT_DATE_FORMAT.format(new Date()));
        } else {
            mContainerView.setBackground(null);
            mTextView.setTextColor(getResources().getColor(android.R.color.black));
            mClockView.setVisibility(View.GONE);
        }
    }

    @ReceiveMessages("sensor")
    public void getHeartRate(String rate) {
        String[] split = rate.split("@");
        for (int i = 0; i < split.length; i++) {
            mTextView.setText("步数   " + split[0] + "\n" + "心率   " + split[1]);
        }
        Log.e("mobile", rate);
    }
//    @Override
//    public void onSensorChanged(final SensorEvent event) {
//        if(event.sensor.getType()==TYPE_HEART_RATE) {
//            if (event.accuracy == 2 || event.accuracy == 3) {
//                if (mTextView.getText().toString().equals("心率获取中...")) {
//                    mTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
//                }
//                Log.d(TAG, "sensor event: " + event.accuracy + " = " + event.values[0]);
//                mTextView.setText(String.valueOf(event.values[0]));
//                Courier.deliverMessage(this, "sensor", String.valueOf(event.values[0]));
//            }
//        }else if(event.sensor.getType()==TYPE_STEP_COUNTER){
//
//        }
//    }

//    @Override
//    public void onAccuracyChanged(Sensor sensor, int accuracy) {
//        Log.d(TAG, "accuracy changed: " + accuracy);
//}
}
