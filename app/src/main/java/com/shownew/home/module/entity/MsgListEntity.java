package com.shownew.home.module.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author Jason
 * @version 1.0
 * @date 2017/5/4 0004
 */

public class MsgListEntity implements Parcelable {


    /**
     * nId : 10
     * nAid : 0
     * nText : 【硬件消息】车辆所处经纬度：30.467935,104.006666
     * nDate : 2017-04-23 23:15:06
     * nFrom : 0
     */

    private String nId;
    private String nAid;
    private String nText;
    private String nDate;
    private String nFrom;

    public String getNId() {
        return nId;
    }

    public void setNId(String nId) {
        this.nId = nId;
    }

    public String getNAid() {
        return nAid;
    }

    public void setNAid(String nAid) {
        this.nAid = nAid;
    }

    public String getNText() {
        return nText;
    }

    public void setNText(String nText) {
        this.nText = nText;
    }

    public String getNDate() {
        return nDate;
    }

    public void setNDate(String nDate) {
        this.nDate = nDate;
    }

    public String getNFrom() {
        return nFrom;
    }

    public void setNFrom(String nFrom) {
        this.nFrom = nFrom;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.nId);
        dest.writeString(this.nAid);
        dest.writeString(this.nText);
        dest.writeString(this.nDate);
        dest.writeString(this.nFrom);
    }

    public MsgListEntity() {
    }

    protected MsgListEntity(Parcel in) {
        this.nId = in.readString();
        this.nAid = in.readString();
        this.nText = in.readString();
        this.nDate = in.readString();
        this.nFrom = in.readString();
    }

    public static final Parcelable.Creator<MsgListEntity> CREATOR = new Parcelable.Creator<MsgListEntity>() {
        @Override
        public MsgListEntity createFromParcel(Parcel source) {
            return new MsgListEntity(source);
        }

        @Override
        public MsgListEntity[] newArray(int size) {
            return new MsgListEntity[size];
        }
    };
}
