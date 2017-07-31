package com.rtmap.gm.wearapp;

import java.io.Serializable;

/**
 * Created by yxy
 * on 2017/7/28.
 */

public class Data implements Serializable{
    private float heart;
    private float step;
    private double latitude;
    private double longitude;
    private float tempearture;
    private int battery;
    private String androidId;

    public String getAndroidId() {
        return androidId;
    }

    public void setAndroidId(String androidId) {
        this.androidId = androidId;
    }

    public float getHeart() {
        return heart;
    }

    public void setHeart(float heart) {
        this.heart = heart;
    }

    public float getStep() {
        return step;
    }

    public void setStep(float step) {
        this.step = step;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public float getTempearture() {
        return tempearture;
    }

    public void setTempearture(float tempearture) {
        this.tempearture = tempearture;
    }

    public int getBattery() {
        return battery;
    }

    public void setBattery(int battery) {
        this.battery = battery;
    }

    @Override
    public String toString() {
        return "{" +
                "heart=" + heart +
                ", step=" + step +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", tempearture=" + tempearture +
                ", battery=" + battery +
                ", androidId='" + androidId + '\'' +
                '}';
    }
}
