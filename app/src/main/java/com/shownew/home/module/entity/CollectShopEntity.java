package com.shownew.home.module.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by WP on 2017/7/28.
 */

public class CollectShopEntity implements Parcelable {


    /**
     * coId : 4
     * coPid : 0
     * coMpid : 15
     * coTitle : 洁柔卷纸 蓝面子3层卫生纸酒店企业采购量贩装27卷X3箱共81卷装
     * coPrice : 185.9
     * coSimg : http://shounew.cn/images/2017-06-26/TB2JKWQtpXXXXbdXXXXXXXXXXXX_!!1135344346.jpg_q90s.jpg
     */

    private int coId;
    private int coPid;
    private int coMpid;
    private String coTitle;
    private double coPrice;
    private String coSimg;

    public int getCoId() {
        return coId;
    }

    public void setCoId(int coId) {
        this.coId = coId;
    }

    public int getCoPid() {
        return coPid;
    }

    public void setCoPid(int coPid) {
        this.coPid = coPid;
    }

    public int getCoMpid() {
        return coMpid;
    }

    public void setCoMpid(int coMpid) {
        this.coMpid = coMpid;
    }

    public String getCoTitle() {
        return coTitle;
    }

    public void setCoTitle(String coTitle) {
        this.coTitle = coTitle;
    }

    public double getCoPrice() {
        return coPrice;
    }

    public void setCoPrice(double coPrice) {
        this.coPrice = coPrice;
    }

    public String getCoSimg() {
        return coSimg;
    }

    public void setCoSimg(String coSimg) {
        this.coSimg = coSimg;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.coId);
        dest.writeInt(this.coPid);
        dest.writeInt(this.coMpid);
        dest.writeString(this.coTitle);
        dest.writeDouble(this.coPrice);
        dest.writeString(this.coSimg);
    }

    public CollectShopEntity() {
    }

    protected CollectShopEntity(Parcel in) {
        this.coId = in.readInt();
        this.coPid = in.readInt();
        this.coMpid = in.readInt();
        this.coTitle = in.readString();
        this.coPrice = in.readDouble();
        this.coSimg = in.readString();
    }

    public static final Parcelable.Creator<CollectShopEntity> CREATOR = new Parcelable.Creator<CollectShopEntity>() {
        @Override
        public CollectShopEntity createFromParcel(Parcel source) {
            return new CollectShopEntity(source);
        }

        @Override
        public CollectShopEntity[] newArray(int size) {
            return new CollectShopEntity[size];
        }
    };
}
