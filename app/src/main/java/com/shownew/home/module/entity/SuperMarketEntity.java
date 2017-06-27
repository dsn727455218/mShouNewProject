package com.shownew.home.module.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author Jason
 * @version 1.0
 * @date 2017/5/6 0006
 */

public class SuperMarketEntity implements Parcelable {

    /**
     * pId : 2
     * pTitle : 倍特f22电动车
     * pPrice : 1250
     * pOldprice : 2000
     * pSimg : http://112.74.55.239/images/2015-15-23/sd.jpg
     */

    private String pId;
    private String pTitle;
    private String pPrice;
    private String pOldprice;
    private String pSimg;

    public String getpOwn() {
        return pOwn;
    }

    public void setpOwn(String pOwn) {
        this.pOwn = pOwn;
    }

    public static Creator<SuperMarketEntity> getCREATOR() {
        return CREATOR;
    }

    private String pOwn;

    public String getpId() {
        return pId;
    }

    public SuperMarketEntity(String pId, String pTitle, String pPrice, String pOldprice, String pSimg) {
        this.pId = pId;
        this.pTitle = pTitle;
        this.pPrice = pPrice;
        this.pOldprice = pOldprice;
        this.pSimg = pSimg;
    }

    public void setpId(String pId) {
        this.pId = pId;
    }

    public String getpTitle() {
        return pTitle;
    }

    public void setpTitle(String pTitle) {
        this.pTitle = pTitle;
    }

    public String getpPrice() {
        return pPrice;
    }

    public void setpPrice(String pPrice) {
        this.pPrice = pPrice;
    }

    public String getpOldprice() {
        return pOldprice;
    }

    public void setpOldprice(String pOldprice) {
        this.pOldprice = pOldprice;
    }

    public String getpSimg() {
        return pSimg;
    }

    public void setpSimg(String pSimg) {
        this.pSimg = pSimg;
    }

    public SuperMarketEntity() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.pId);
        dest.writeString(this.pTitle);
        dest.writeString(this.pPrice);
        dest.writeString(this.pOldprice);
        dest.writeString(this.pSimg);
        dest.writeString(this.pOwn);
    }

    protected SuperMarketEntity(Parcel in) {
        this.pId = in.readString();
        this.pTitle = in.readString();
        this.pPrice = in.readString();
        this.pOldprice = in.readString();
        this.pSimg = in.readString();
        this.pOwn = in.readString();
    }

    public static final Creator<SuperMarketEntity> CREATOR = new Creator<SuperMarketEntity>() {
        @Override
        public SuperMarketEntity createFromParcel(Parcel source) {
            return new SuperMarketEntity(source);
        }

        @Override
        public SuperMarketEntity[] newArray(int size) {
            return new SuperMarketEntity[size];
        }
    };
}
