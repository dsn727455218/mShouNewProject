package com.shownew.home.module.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author Jason
 * @version 1.0
 * @date 2017/4/14 0014
 */

public class DeviceEntity implements Parcelable {

    /**
     * isLock 0-锁定 1-解锁          electricity 电量（百分比：0-100）             isMute 0-静音 1-非静音             check、distance暂不使用
     * gps : 30.467935,104.006666
     * electricity : 20
     * isLock : 1
     * isMute : 0
     * distance : null
     * check : null
     */

    private String gps;
    private String electricity;
    private String isLock;
    private String isMute;
    private Object distance;
    private Object check;

    public String getGps() {
        return gps;
    }

    public void setGps(String gps) {
        this.gps = gps;
    }

    public String getElectricity() {
        return electricity;
    }

    public void setElectricity(String electricity) {
        this.electricity = electricity;
    }

    public String getIsLock() {
        return isLock;
    }

    public void setIsLock(String isLock) {
        this.isLock = isLock;
    }

    public String getIsMute() {
        return isMute;
    }

    public void setIsMute(String isMute) {
        this.isMute = isMute;
    }

    public Object getDistance() {
        return distance;
    }

    public void setDistance(Object distance) {
        this.distance = distance;
    }

    public Object getCheck() {
        return check;
    }

    public void setCheck(Object check) {
        this.check = check;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.gps);
        dest.writeString(this.electricity);
        dest.writeString(this.isLock);
        dest.writeString(this.isMute);

    }

    public DeviceEntity() {
    }

    protected DeviceEntity(Parcel in) {
        this.gps = in.readString();
        this.electricity = in.readString();
        this.isLock = in.readString();
        this.isMute = in.readString();
        this.distance = in.readParcelable(Object.class.getClassLoader());
        this.check = in.readParcelable(Object.class.getClassLoader());
    }

    public static final Parcelable.Creator<DeviceEntity> CREATOR = new Parcelable.Creator<DeviceEntity>() {
        @Override
        public DeviceEntity createFromParcel(Parcel source) {
            return new DeviceEntity(source);
        }

        @Override
        public DeviceEntity[] newArray(int size) {
            return new DeviceEntity[size];
        }
    };
}
