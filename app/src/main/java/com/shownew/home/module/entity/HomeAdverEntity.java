package com.shownew.home.module.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 广告实体
 * @author Jason
 * @version 1.0
 * @date 2017/5/4 0004
 */

public class HomeAdverEntity implements Parcelable {

    /**
     * aId : 3
     * aImg : http://112.74.55.239/images/2017-04-21/zFSBgPsN6dYGnOipMh+EnQ==.jpg
     * aPid ：商品id   默认为0  不是商品     否则 进入商品详情
     */

    private String aId;
    private String aImg;
    private String aPid;
    private String aUrl;
    private  String aMpid;

    public String getaMpid() {
        return aMpid;
    }

    public void setaMpid(String aMpid) {
        this.aMpid = aMpid;
    }

    public String getaUrl() {
        return aUrl;
    }

    public void setaUrl(String aUrl) {
        this.aUrl = aUrl;
    }

    public String getaId() {
        return aId;
    }

    public void setaId(String aId) {
        this.aId = aId;
    }

    public String getaImg() {
        return aImg;
    }

    public void setaImg(String aImg) {
        this.aImg = aImg;
    }

    public String getaPid() {
        return aPid;
    }

    public void setaPid(String aPid) {
        this.aPid = aPid;
    }

    public static Creator<HomeAdverEntity> getCREATOR() {
        return CREATOR;
    }

    public String getAId() {
        return aId;
    }

    public void setAId(String aId) {
        this.aId = aId;
    }

    public String getAImg() {
        return aImg;
    }

    public void setAImg(String aImg) {
        this.aImg = aImg;
    }

    public HomeAdverEntity() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.aId);
        dest.writeString(this.aImg);
        dest.writeString(this.aPid);
        dest.writeString(this.aUrl);
        dest.writeString(this.aMpid);
    }

    protected HomeAdverEntity(Parcel in) {
        this.aId = in.readString();
        this.aImg = in.readString();
        this.aPid = in.readString();
        this.aUrl = in.readString();
        this.aMpid = in.readString();
    }

    public static final Creator<HomeAdverEntity> CREATOR = new Creator<HomeAdverEntity>() {
        @Override
        public HomeAdverEntity createFromParcel(Parcel source) {
            return new HomeAdverEntity(source);
        }

        @Override
        public HomeAdverEntity[] newArray(int size) {
            return new HomeAdverEntity[size];
        }
    };
}
