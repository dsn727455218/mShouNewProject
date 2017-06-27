package com.shownew.home.module.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author Jason
 * @version 1.0
 * @date 2017/5/25 0025
 */

public class ShopTypeEntity implements Parcelable {

    /**
     * tId : 1
     * tName : 整车特卖
     * tImg :
     */

    private int tId;
    private String tName;
    private String tImg;

    public int getTId() {
        return tId;
    }

    public void setTId(int tId) {
        this.tId = tId;
    }

    public String getTName() {
        return tName;
    }

    public void setTName(String tName) {
        this.tName = tName;
    }

    public String getTImg() {
        return tImg;
    }

    public void setTImg(String tImg) {
        this.tImg = tImg;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.tId);
        dest.writeString(this.tName);
        dest.writeString(this.tImg);
    }

    public ShopTypeEntity() {
    }

    protected ShopTypeEntity(Parcel in) {
        this.tId = in.readInt();
        this.tName = in.readString();
        this.tImg = in.readString();
    }

    public static final Parcelable.Creator<ShopTypeEntity> CREATOR = new Parcelable.Creator<ShopTypeEntity>() {
        @Override
        public ShopTypeEntity createFromParcel(Parcel source) {
            return new ShopTypeEntity(source);
        }

        @Override
        public ShopTypeEntity[] newArray(int size) {
            return new ShopTypeEntity[size];
        }
    };
}
