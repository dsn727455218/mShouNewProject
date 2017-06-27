package com.shownew.home.module.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author Jason
 * @version 1.0
 * @date 2017/6/3 0003
 */

public class ShopMaillTypeEntity implements Parcelable {


    /**
     * mtId : 1
     * mtName : 地方特产
     * mtImg : http://shounew.cn/images/sys-image/zhengchetemai.png
     */

    private int mtId;
    private String mtName;
    private String mtImg;

    public int getMtId() {
        return mtId;
    }

    public void setMtId(int mtId) {
        this.mtId = mtId;
    }

    public String getMtName() {
        return mtName;
    }

    public void setMtName(String mtName) {
        this.mtName = mtName;
    }

    public String getMtImg() {
        return mtImg;
    }

    public void setMtImg(String mtImg) {
        this.mtImg = mtImg;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mtId);
        dest.writeString(this.mtName);
        dest.writeString(this.mtImg);
    }

    public ShopMaillTypeEntity() {
    }

    protected ShopMaillTypeEntity(Parcel in) {
        this.mtId = in.readInt();
        this.mtName = in.readString();
        this.mtImg = in.readString();
    }

    public static final Parcelable.Creator<ShopMaillTypeEntity> CREATOR = new Parcelable.Creator<ShopMaillTypeEntity>() {
        @Override
        public ShopMaillTypeEntity createFromParcel(Parcel source) {
            return new ShopMaillTypeEntity(source);
        }

        @Override
        public ShopMaillTypeEntity[] newArray(int size) {
            return new ShopMaillTypeEntity[size];
        }
    };
}
