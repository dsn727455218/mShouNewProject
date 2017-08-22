package com.shownew.home.module.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * @author Jason
 * @version 1.0
 * @date 2017/5/6 0006
 */

public class SuperMarkeDetailEntity implements Parcelable {

    /**
     * pId : 1
     * pTitle : 首牛多功能充电器
     * pPrice : 298
     * pOldprice : 500
     * pImg : ["http://112.74.55.239/images/2015-15-23/sd.jpg","http://112.74.55.239/images/2015-15-23/sd.jpg","http://112.74.55.239/images/2015-15-23/sd.jpg"]
     * pImgs : ["http://112.74.55.239/images/2015-15-23/sd.jpg","http://112.74.55.239/images/2015-15-23/sd.jpg","http://112.74.55.239/images/2015-15-23/sd.jpg"]
     * pSimg : http://112.74.55.239/images/2015-15-23/sd.jpg
     * pColor : 白色,红色,绿色
     * pKdprice : 10
     * pAddress : 四川成都
     */

    private String pId;
    private String pTitle;
    private String pPrice;
    private double pOldprice;
    private String pSimg;
    private String pColor;
    private double pKdprice;
    private String pAddress;
    private String pAllprice;
    private String pOwn;  //判断是否是首牛
    private int pCollect;

    public int getpCollect() {
        return pCollect;
    }

    public void setpCollect(int pCollect) {
        this.pCollect = pCollect;
    }

    public String getpOwn() {
        return pOwn;
    }

    public void setpOwn(String pOwn) {
        this.pOwn = pOwn;
    }

    public String getpAllprice() {
        return pAllprice;
    }

    public void setpAllprice(String pAllprice) {
        this.pAllprice = pAllprice;
    }

    private ArrayList<String> pImg;
    private ArrayList<String> pImgs;

    public String getPId() {
        return pId;
    }

    public void setPId(String pId) {
        this.pId = pId;
    }

    public String getPTitle() {
        return pTitle;
    }

    public void setPTitle(String pTitle) {
        this.pTitle = pTitle;
    }

    public String getPPrice() {
        return pPrice;
    }

    public void setPPrice(String pPrice) {
        this.pPrice = pPrice;
    }

    public double getPOldprice() {
        return pOldprice;
    }

    public void setPOldprice(double pOldprice) {
        this.pOldprice = pOldprice;
    }

    public String getPSimg() {
        return pSimg;
    }

    public void setPSimg(String pSimg) {
        this.pSimg = pSimg;
    }

    public String getPColor() {
        return pColor;
    }

    public void setPColor(String pColor) {
        this.pColor = pColor;
    }

    public double getPKdprice() {
        return pKdprice;
    }

    public void setPKdprice(double pKdprice) {
        this.pKdprice = pKdprice;
    }

    public String getPAddress() {
        return pAddress;
    }

    public void setPAddress(String pAddress) {
        this.pAddress = pAddress;
    }

    public ArrayList<String> getPImg() {
        return pImg;
    }

    public void setPImg(ArrayList<String> pImg) {
        this.pImg = pImg;
    }

    public ArrayList<String> getPImgs() {
        return pImgs;
    }

    public void setPImgs(ArrayList<String> pImgs) {
        this.pImgs = pImgs;
    }


    public SuperMarkeDetailEntity() {
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
        dest.writeDouble(this.pOldprice);
        dest.writeString(this.pSimg);
        dest.writeString(this.pColor);
        dest.writeDouble(this.pKdprice);
        dest.writeString(this.pAddress);
        dest.writeString(this.pAllprice);
        dest.writeString(this.pOwn);
        dest.writeInt(this.pCollect);
        dest.writeStringList(this.pImg);
        dest.writeStringList(this.pImgs);
    }

    protected SuperMarkeDetailEntity(Parcel in) {
        this.pId = in.readString();
        this.pTitle = in.readString();
        this.pPrice = in.readString();
        this.pOldprice = in.readDouble();
        this.pSimg = in.readString();
        this.pColor = in.readString();
        this.pKdprice = in.readDouble();
        this.pAddress = in.readString();
        this.pAllprice = in.readString();
        this.pOwn = in.readString();
        this.pCollect = in.readInt();
        this.pImg = in.createStringArrayList();
        this.pImgs = in.createStringArrayList();
    }

    public static final Creator<SuperMarkeDetailEntity> CREATOR = new Creator<SuperMarkeDetailEntity>() {
        @Override
        public SuperMarkeDetailEntity createFromParcel(Parcel source) {
            return new SuperMarkeDetailEntity(source);
        }

        @Override
        public SuperMarkeDetailEntity[] newArray(int size) {
            return new SuperMarkeDetailEntity[size];
        }
    };
}
