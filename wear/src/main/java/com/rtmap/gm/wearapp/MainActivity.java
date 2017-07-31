package com.rtmap.gm.wearapp;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
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
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.BoxInsetLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import net.vidageek.mirror.dsl.Mirror;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import me.denley.courier.Courier;
import pub.devrel.easypermissions.EasyPermissions;

import static android.hardware.Sensor.TYPE_AMBIENT_TEMPERATURE;
import static android.hardware.Sensor.TYPE_HEART_RATE;
import static android.hardware.Sensor.TYPE_STEP_COUNTER;
import static android.hardware.SensorManager.SENSOR_DELAY_NORMAL;

public class MainActivity extends WearableActivity implements LocationListener, SensorEventListener, EasyPermissions.PermissionCallbacks {

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
    //    private LocationManager mLocationManager;
    private Sensor mTempeartureSensor;
    private float heart = 0;
    private float step = 0;
    private float tempearture = 0;
    String macSerial = "";
    private double mLatitude;
    private double mLongitude;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mHandler.removeMessages(0);
            Data data = new Data();
            data.setBattery(mBattery);
            data.setHeart(heart);
            data.setLatitude(mLatitude);
            data.setLongitude(mLongitude);
            data.setStep(step);
            data.setTempearture(tempearture);
            data.setAndroidId(macSerial);
//        Courier.deliverMessage(this, "sensor", step + "@" + heart + "@" + latitude + "@" + longitude + "@" + tempearture + "@" + mBattery);
            Courier.deliverMessage(MainActivity.this, "sensor", data.toString());
        }
    };
    private LocationManager mLocationManager;
    private BluetoothAdapter bluetoothAdapter;
//    private GoogleApiClient mGoogleApiClient;


    @Override
    public void onSensorChanged(SensorEvent event) {
        Log.d("rateService", "sensor event: " + event.sensor.getType() + " = " + event.values[0]);
        if (event.sensor.getType() == TYPE_HEART_RATE) {
            if (event.accuracy == 2 || event.accuracy == 3) {
                heart = event.values[0];
            }
        }
        if (event.sensor.getType() == TYPE_STEP_COUNTER) {
            if (event.accuracy == 3)
                step = event.values[0];
        }
        if (event.sensor.getType() == TYPE_AMBIENT_TEMPERATURE) {
            if (event.accuracy == 3)
                tempearture = event.values[0];
        }
        mTextView.setText("步数 " + step + "\n" + "心率 " + heart + "\n" + "定位 " + mLatitude + "  " + mLongitude);
        mHandler.sendEmptyMessageDelayed(0, 500);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    /**
     * 权限
     *
     * @param requestCode
     * @param perms
     */
    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 30, 0, this);
//        requestLocation();
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mLatitude = location.getLatitude();
        mLongitude = location.getLongitude();
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

    /**
     * 定位
     */
//    @Override
//    public void onConnected(@Nullable Bundle bundle) {
//
//    }
//
//    @Override
//    public void onConnectionSuspended(int i) {
//        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
//    }
//
//    @Override
//    public void onConnectionFailed(@NonNull ConnectionResult result) {
//        Log.e(TAG, "onConnectionFailed(): " + result.getErrorMessage());
//    }
//
//    @Override
//    public void onLocationChanged(Location location) {
//        mLatitude = location.getLatitude();
//        mLongitude = location.getLongitude();
//    }

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

    private void requestLocation() {
        Log.d(TAG, "requestLocation()");

        /*
         * mGpsPermissionApproved covers 23+ (M+) style permissions. If that is already approved or
         * the device is pre-23, the app uses mSaveGpsLocation to save the user's location
         * preference.
         */

//        LocationRequest locationRequest = LocationRequest.create()
//                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
//                .setInterval(TimeUnit.SECONDS.toMillis(5))
//                .setFastestInterval(TimeUnit.SECONDS.toMillis(5));
//
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return;
//        }
//        LocationServices.FusedLocationApi
//                .requestLocationUpdates(mGoogleApiClient, locationRequest, this)
//                .setResultCallback(new ResultCallback<Status>() {
//
//                    @Override
//                    public void onResult(Status status) {
//                        if (status.getStatus().isSuccess()) {
//                            if (Log.isLoggable(TAG, Log.DEBUG)) {
//                                Log.d(TAG, "Successfully requested location updates");
//                            }
//                        } else {
//                            Log.e(TAG,
//                                    "Failed in requesting location updates, "
//                                            + "status code: "
//                                            + status.getStatusCode() + ", message: " + status
//                                            .getStatusMessage());
//                        }
//                    }
//                });
    }

    @Override
    protected void onResume() {
        super.onResume();
//        if (mGoogleApiClient != null) {
//            mGoogleApiClient.connect();
//        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setAmbientEnabled();
        getBtAddressViaReflection();
        mContainerView = (BoxInsetLayout) findViewById(R.id.container);
        mTextView = (TextView) findViewById(R.id.text);
        mClockView = (TextView) findViewById(R.id.clock);


        Log.e(TAG, "检查权限：" + hasPermission());
        if (!hasPermission()) {
            requestPermissions(new String[]{Manifest.permission.BODY_SENSORS, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
        }
//        startService(new Intent(this, RateService.class));
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (mSensorManager != null) {
            mHrSensor = mSensorManager.getDefaultSensor(TYPE_HEART_RATE);
            mStepSensor = mSensorManager.getDefaultSensor(TYPE_STEP_COUNTER);
            mTempeartureSensor = mSensorManager.getDefaultSensor(TYPE_AMBIENT_TEMPERATURE);
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location location =
                mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        mLongitude = location.getLongitude();
        mLatitude = location.getLatitude();
        // used for data layer API
//        mGoogleApiClient = new GoogleApiClient.Builder(this)
//                .addApi(LocationServices.API)
//                .addApi(Wearable.API)  // used for data layer API
//                .addConnectionCallbacks(this)
//                .addOnConnectionFailedListener(this)
//                .build();

        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        BatteryReciver reciver = new BatteryReciver();
        registerReceiver(reciver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
//        if ((mGoogleApiClient != null) && (mGoogleApiClient.isConnected()) &&
//                (mGoogleApiClient.isConnecting())) {
//            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
//            mGoogleApiClient.disconnect();
//        }
    }

    private void getBtAddressViaReflection() {
        String uuid1 = (String) SPUtil.get(this, "uuid", "");
        if (TextUtils.isEmpty(uuid1)) {
                bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            Object bluetoothManagerService = new Mirror().on(bluetoothAdapter).get().field("mService");
            if (bluetoothManagerService == null) {
                Log.w(TAG, "couldn't find bluetoothManagerService");
                getMyUUID();
            }
            Object address = new Mirror().on(bluetoothManagerService).invoke().method("getAddress").withoutArgs();
            if (address != null && address instanceof String) {
                Log.w(TAG, "using reflection to get the BT MAC address: " + address);
                macSerial = (String) address;
                SPUtil.put(this, "uuid", macSerial);
            } else {
                getMyUUID();
            }
        } else
            macSerial = uuid1;
    }

    private void getMyUUID() {
        macSerial = UUID.randomUUID().toString();
        SPUtil.put(this, "uuid", macSerial);
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
//        if (mLocationManager != null)
//            mLocationManager.removeUpdates(this);
        if (mSensorManager != null)
            mSensorManager.unregisterListener(this);
        mHandler.removeCallbacksAndMessages(null);
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
