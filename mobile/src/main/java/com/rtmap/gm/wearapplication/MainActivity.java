package com.rtmap.gm.wearapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import me.denley.courier.Courier;
import me.denley.courier.ReceiveMessages;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Courier.startReceiving(this);
//        startService(new Intent(this, RateService.class));
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @ReceiveMessages("sensor")
    public void getHeartRate(String rate) {
        String[] split = rate.split("@");
        for (int i = 0; i < split.length; i++) {
            ((TextView) findViewById(R.id.tv_main)).setText("步数   " + split[0]);
            ((TextView) findViewById(R.id.tv_main_heart)).setText("心率   " + split[1]);
        }

        Log.e("mobile", rate);
    }

    @Override
    protected void onDestroy() {
        Courier.stopReceiving(this);
        super.onDestroy();
    }
}
