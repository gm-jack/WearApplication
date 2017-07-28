package com.rtmap.gm.wearapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lzy.okgo.OkGo;

import java.io.IOException;

import me.denley.courier.Courier;
import me.denley.courier.ReceiveMessages;

public class MainActivity extends AppCompatActivity {

    public static final String URL = "http://123.56.132.58:8088/PMPMSystem/watchInterface/receivedata.xhtml?paramjson=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Courier.startReceiving(this);
//        Data data = new Data();
//        data.setBattery(0);
//        data.setHeart(0);
//        data.setLatitude(0);
//        data.setLongitude(0);
//        data.setStep(0);
//        data.setTempearture(0);
//        data.setAndroidId("");
//
//        Log.e("data", new Gson().toJson(data));
//        startService(new Intent(this, RateService.class));
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @ReceiveMessages("sensor")
    public void getHeartRate(Data rate) {
        Log.e("mobile", rate.toString());
        ((TextView) findViewById(R.id.tv_main)).setText("步数   " + rate.getStep());
        ((TextView) findViewById(R.id.tv_main_heart)).setText("心率   " + rate.getHeart());
        try {
            OkGo.post(URL + new Gson().toJson(rate)).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        Courier.stopReceiving(this);
        super.onDestroy();
    }

}
