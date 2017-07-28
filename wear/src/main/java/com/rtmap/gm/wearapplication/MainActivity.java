package com.rtmap.gm.wearapplication;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.BoxInsetLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import me.denley.courier.Courier;
import pub.devrel.easypermissions.EasyPermissions;

import static android.hardware.Sensor.TYPE_AMBIENT_TEMPERATURE;
import static android.hardware.Sensor.TYPE_HEART_RATE;
import static android.hardware.Sensor.TYPE_STEP_COUNTER;
import static android.hardware.SensorManager.SENSOR_DELAY_NORMAL;

public class MainActivity extends WearableActivity implements LocationListener, SensorEventListener {

    private static final SimpleDateFormat AMBIENT_DATE_FORMAT =
            new SimpleDateFormat("HH:mm", Locale.US);
    private static final String TAG = "MainActivity";

    private BoxInsetLayout mContainerView;
    private TextView mTextView;
    private TextView mClockView;
    private SensorManager mSensorManager;
    private Sensor mHrSensor;
    private Sensor mStepSensor;
    private int mBattery = -1;
    private LocationManager mLocationManager;
    private Sensor mTempeartureSensor;
    private float heart = 0;
    private float step = 0;
    private float tempearture = 0;
    String macSerial = "";

    @Override
    public void onLocationChanged(Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        Data data = new Data();
        data.setBattery(mBattery);
        data.setHeart(heart);
        data.setLatitude(latitude);
        data.setLongitude(longitude);
        data.setStep(step);
        data.setTempearture(tempearture);
        data.setAndroidId(macSerial);
//        Courier.deliverMessage(this, "sensor", step + "@" + heart + "@" + latitude + "@" + longitude + "@" + tempearture + "@" + mBattery);
        Courier.deliverMessage(this, "sensor", data);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Log.d("rateService", "sensor event: " + event.sensor.getType() + " = " + event.values[0]);
        if (event.sensor.getType() == TYPE_HEART_RATE) {
            if (event.accuracy == 2 || event.accuracy == 3) {
                heart = event.values[0];
            }
        }
        if (event.sensor.getType() == TYPE_HEART_RATE) {
            if (event.accuracy == 3)
                step = event.values[0];
        }
        if (event.sensor.getType() == TYPE_AMBIENT_TEMPERATURE) {
            if (event.accuracy == 3)
                tempearture = event.values[0];
        }
        mTextView.setText("步数   " + step + "\n" + "心率   " + heart);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private class BatteryReciver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (Intent.ACTION_BATTERY_CHANGED.equals(intent.getAction())) {
                int level = intent.getIntExtra("level", 0);
                int scale = intent.getIntExtra("scale", 0);
                mBattery = level * 100 / scale;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setAmbientEnabled();
        getMyUUID();
        mContainerView = (BoxInsetLayout) findViewById(R.id.container);
        mTextView = (TextView) findViewById(R.id.text);
        mClockView = (TextView) findViewById(R.id.clock);

//        initData();
        Log.e(TAG, "检查权限：" + hasPermission());
        if (!hasPermission()) {
            requestPermissions(new String[]{Manifest.permission.BODY_SENSORS, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
        }
//        startService(new Intent(this, RateService.class));
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (mSensorManager != null) {
            mHrSensor = mSensorManager.getDefaultSensor(TYPE_HEART_RATE);
            mStepSensor = mSensorManager.getDefaultSensor(TYPE_STEP_COUNTER);
            mTempeartureSensor = mSensorManager.getDefaultSensor(TYPE_AMBIENT_TEMPERATURE);
        }

        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if (mLocationManager != null)
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 0, this);

        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        BatteryReciver reciver = new BatteryReciver();
        registerReceiver(reciver, filter);


//        getMac();
    }

    /**
     * 获取MAC地址
     *
     * @return
     */
    public void getMac() {
        StringBuffer buf = new StringBuffer();
        NetworkInterface networkInterface = null;
        try {
            networkInterface = NetworkInterface.getByName("eth1");
            if (networkInterface == null) {
                networkInterface = NetworkInterface.getByName("wlan0");
            }
            if (networkInterface == null) {
            }
            byte[] addr = networkInterface.getHardwareAddress();


            for (byte b : addr) {
                buf.append(String.format("%02X:", b));
            }
            if (buf.length() > 0) {
                buf.deleteCharAt(buf.length() - 1);
            }
            macSerial = buf.toString();
        } catch (SocketException e) {
            e.printStackTrace();
            macSerial = "02:00:00:00:00:02";
        }
    }

    private void getMyUUID() {
        String uuid1 = (String) SPUtil.get(this, "uuid", "");
        if (TextUtils.isEmpty(uuid1)) {
            macSerial = UUID.randomUUID().toString();
            SPUtil.put(this, "uuid", macSerial);
        } else
            macSerial = uuid1;
    }

    private void initData() {
    }

    public boolean hasPermission() {
        return EasyPermissions.hasPermissions(this, Manifest.permission.BODY_SENSORS, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION);
    }

    @Override
    protected void onStart() {
        super.onStart();
//        Courier.startReceiving(this);
        if (mStepSensor != null)
            mSensorManager.registerListener(this, mStepSensor, SENSOR_DELAY_NORMAL);
        if (mHrSensor != null)
            mSensorManager.registerListener(this, mHrSensor, SENSOR_DELAY_NORMAL);
        if (mTempeartureSensor != null)
            mSensorManager.registerListener(this, mTempeartureSensor, SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onStop() {
        super.onStop();
//        Courier.stopReceiving(this);
        if (mLocationManager != null)
            mLocationManager.removeUpdates(this);
        if (mSensorManager != null)
            mSensorManager.unregisterListener(this);
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

//    @ReceiveMessages("sensor")
//    public void getHeartRate(String rate) {
//        String[] split = rate.split("@");
//        mTextView.setText("步数   " + split[0] + "\n" + "心率   " + split[1]);
//        Log.e("mobile", rate);
//    }
}
