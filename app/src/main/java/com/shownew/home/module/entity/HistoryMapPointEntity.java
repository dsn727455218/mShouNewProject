package com.shownew.home.module.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by WP on 2017/7/20.
 */

public class HistoryMapPointEntity implements Parcelable {

    /**
     * gGps : 30.465457,104.008757
     * gText : 位置获取不准，请手动查看GPS位置！
     * gDate : 2017-07-19 14:39:00
     */

    private String gGps;
    private String gText;
    private String gDate;
    private boolean isShow;

    public boolean isShow() {
        return isShow;
    }

    public void setShow(boolean show) {
        isShow = show;
    }

    public String getGGps() {
        return gGps;
    }

    public void setGGps(String gGps) {
        this.gGps = gGps;
    }

    public String getGText() {
        return gText;
    }

    public void setGText(String gText) {
        this.gText = gText;
    }

    public String getGDate() {
        return gDate;
    }

    public void setGDate(String gDate) {
        this.gDate = gDate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.gGps);
        dest.writeString(this.gText);
        dest.writeString(this.gDate);
        dest.writeByte(this.isShow ? (byte) 1 : (byte) 0);
    }

    public HistoryMapPointEntity() {
    }

    protected HistoryMapPointEntity(Parcel in) {
        this.gGps = in.readString();
        this.gText = in.readString();
        this.gDate = in.readString();
        this.isShow = in.readByte() != 0;
    }

    public static final Parcelable.Creator<HistoryMapPointEntity> CREATOR = new Parcelable.Creator<HistoryMapPointEntity>() {
        @Override
        public HistoryMapPointEntity createFromParcel(Parcel source) {
            return new HistoryMapPointEntity(source);
        }

        @Override
        public HistoryMapPointEntity[] newArray(int size) {
            return new HistoryMapPointEntity[size];
        }
    };
}
