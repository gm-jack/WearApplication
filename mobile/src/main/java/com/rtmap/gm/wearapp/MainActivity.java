package com.rtmap.gm.wearapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.Callback;
import com.lzy.okgo.model.Progress;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.base.Request;

import me.denley.courier.Courier;
import me.denley.courier.ReceiveMessages;

public class MainActivity extends AppCompatActivity {

    public static final String URL = "http://123.56.132.58:8088/PMPMSystem/watchInterface/receivedata.xhtml?paramjson=";
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Courier.startReceiving(this);



    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @ReceiveMessages("sensor")
    public void getHeartRate(String rate) {
        Data data = new Gson().fromJson(rate, Data.class);
        Log.e("mobile", rate.toString());
        ((TextView) findViewById(R.id.tv_main)).setText("步数   " + data.getStep());
        ((TextView) findViewById(R.id.tv_main_heart)).setText("心率   " + data.getHeart());
        OkGo.<String>post(URL + new Gson().toJson(data)).execute(new Callback<String>() {
            @Override
            public String convertResponse(okhttp3.Response response) throws Throwable {
                return null;
            }

            @Override
            public void onStart(Request<String, ? extends Request> request) {

            }

            @Override
            public void onSuccess(Response<String> response) {

            }

            @Override
            public void onCacheSuccess(Response<String> response) {

            }

            @Override
            public void onError(Response<String> response) {

            }

            @Override
            public void onFinish() {

            }

            @Override
            public void uploadProgress(Progress progress) {

            }

            @Override
            public void downloadProgress(Progress progress) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        Courier.stopReceiving(this);
        super.onDestroy();
    }
}
