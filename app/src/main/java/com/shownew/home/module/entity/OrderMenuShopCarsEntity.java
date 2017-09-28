package com.shownew.home.module.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**订单实体
 * Created by WP on 2017/8/14.
 */

public class OrderMenuShopCarsEntity implements Parcelable {

    /**
     * oPid : 0
     * oMpid : 11
     * oColor : 红色
     * oPrice : 24.5
     * oKdprice : 2
     * oNum : 1
     * oNote : 绿色的哈
     */
    private String oShid;

    public String getoShid() {
        return oShid;
    }

    public void setoShid(String oShid) {
        this.oShid = oShid;
    }

    private String oPid;
    private String oMpid;
    private String oColor;
    private double oPrice;
    private double oKdprice;
    private int oNum;
    private String oNote;

    public String getOPid() {
        return oPid;
    }

    public void setOPid(String oPid) {
        this.oPid = oPid;
    }

    public String getOMpid() {
        return oMpid;
    }

    public void setOMpid(String oMpid) {
        this.oMpid = oMpid;
    }

    public String getOColor() {
        return oColor;
    }

    public void setOColor(String oColor) {
        this.oColor = oColor;
    }

    public double getOPrice() {
        return oPrice;
    }

    public void setOPrice(double oPrice) {
        this.oPrice = oPrice;
    }

    public double getOKdprice() {
        return oKdprice;
    }

    public void setOKdprice(double oKdprice) {
        this.oKdprice = oKdprice;
    }

    public int getONum() {
        return oNum;
    }

    public void setONum(int oNum) {
        this.oNum = oNum;
    }

    public String getONote() {
        return oNote;
    }

    public void setONote(String oNote) {
        this.oNote = oNote;
    }

    public OrderMenuShopCarsEntity() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.oShid);
        dest.writeString(this.oPid);
        dest.writeString(this.oMpid);
        dest.writeString(this.oColor);
        dest.writeDouble(this.oPrice);
        dest.writeDouble(this.oKdprice);
        dest.writeInt(this.oNum);
        dest.writeString(this.oNote);
    }

    protected OrderMenuShopCarsEntity(Parcel in) {
        this.oShid = in.readString();
        this.oPid = in.readString();
        this.oMpid = in.readString();
        this.oColor = in.readString();
        this.oPrice = in.readDouble();
        this.oKdprice = in.readDouble();
        this.oNum = in.readInt();
        this.oNote = in.readString();
    }

    public static final Creator<OrderMenuShopCarsEntity> CREATOR = new Creator<OrderMenuShopCarsEntity>() {
        @Override
        public OrderMenuShopCarsEntity createFromParcel(Parcel source) {
            return new OrderMenuShopCarsEntity(source);
        }

        @Override
        public OrderMenuShopCarsEntity[] newArray(int size) {
            return new OrderMenuShopCarsEntity[size];
        }
    };
}
