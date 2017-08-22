package com.shownew.home.module.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * @author Jason
 * @version 1.0
 * @date 2017/6/3 0003
 */

public class ShopMallDetailEntity implements Parcelable {

    /**
     * mpId : 1
     * mpTitle : 成都玉骑铃电动车72v悍将 奇蕾倍特安尔达雅迪 同款电动车电瓶车
     * mpPrice : 0.01
     * mpOldprice : 3000
     * mpImg : ["http://shounew.cn/images/2017-05-08/TB2utWgjbVkpuFjSspcXXbSMVXa_!!1972626667.jpg","http://shounew.cn/images/2017-05-08/TB2f9SfjgFkpuFjSspnXXb4qFXa_!!1972626667.jpg","http://shounew.cn/images/2017-05-08/TB2KhB4jgxlpuFjSszgXXcJdpXa_!!1972626667.jpg","http://shounew.cn/images/2017-05-08/TB2Jeyqjl8lpuFjy0FnXXcZyXXa_!!1972626667.jpg","http://shounew.cn/images/2017-05-08/TB2UO0Ok00opuFjSZFxXXaDNVXa_!!1972626667.jpg"]
     * mpImgs : ["http://shounew.cn/images/2017-05-08/TB2f9SfjgFkpuFjSspnXXb4qFXa_!!1972626667.jpg","http://shounew.cn/images/2017-05-08/TB2KhB4jgxlpuFjSszgXXcJdpXa_!!1972626667.jpg","http://shounew.cn/images/2017-05-08/TB2Jeyqjl8lpuFjy0FnXXcZyXXa_!!1972626667.jpg","http://shounew.cn/images/2017-05-08/TB2UO0Ok00opuFjSZFxXXaDNVXa_!!1972626667.jpg"]
     * mpSimg : http://shounew.cn/images/2017-05-08/TB2utWgjbVkpujSspcXXbSMVXa_!!197262666.jpg
     * mpAllprice : 0.01,1999.00,1999.00,2799.00,2799.00,2799.00
     * mpColor : 白色裸车,红色裸车,黑色裸车,白色整车,红色整车,黑色整车
     * mpKdprice : 0.01
     * mpAddress : 四川成都
     */
    /**
     *  mpCollect：0-未收藏 1-已收藏
     */
    private int mpCollect;

    public int getMpCollect() {
        return mpCollect;
    }

    public void setMpCollect(int mpCollect) {
        this.mpCollect = mpCollect;
    }

    public void setMpOldprice(double mpOldprice) {
        this.mpOldprice = mpOldprice;
    }

    public static Creator<ShopMallDetailEntity> getCREATOR() {
        return CREATOR;
    }

    private String mpId;
    private String mpTitle;
    private double mpPrice;
    private double mpOldprice;
    private String mpSimg;
    private String mpAllprice;
    private String mpColor;
    private double mpKdprice;
    private String mpAddress;
    private List<String> mpImg;
    private List<String> mpImgs;

    public String getMpId() {
        return mpId;
    }

    public void setMpId(String mpId) {
        this.mpId = mpId;
    }

    public String getMpTitle() {
        return mpTitle;
    }

    public void setMpTitle(String mpTitle) {
        this.mpTitle = mpTitle;
    }

    public double getMpPrice() {
        return mpPrice;
    }

    public void setMpPrice(double mpPrice) {
        this.mpPrice = mpPrice;
    }

    public double getMpOldprice() {
        return mpOldprice;
    }

    public void setMpOldprice(int mpOldprice) {
        this.mpOldprice = mpOldprice;
    }

    public String getMpSimg() {
        return mpSimg;
    }

    public void setMpSimg(String mpSimg) {
        this.mpSimg = mpSimg;
    }

    public String getMpAllprice() {
        return mpAllprice;
    }

    public void setMpAllprice(String mpAllprice) {
        this.mpAllprice = mpAllprice;
    }

    public String getMpColor() {
        return mpColor;
    }

    public void setMpColor(String mpColor) {
        this.mpColor = mpColor;
    }

    public double getMpKdprice() {
        return mpKdprice;
    }

    public void setMpKdprice(double mpKdprice) {
        this.mpKdprice = mpKdprice;
    }

    public String getMpAddress() {
        return mpAddress;
    }

    public void setMpAddress(String mpAddress) {
        this.mpAddress = mpAddress;
    }

    public List<String> getMpImg() {
        return mpImg;
    }

    public void setMpImg(List<String> mpImg) {
        this.mpImg = mpImg;
    }

    public List<String> getMpImgs() {
        return mpImgs;
    }

    public void setMpImgs(List<String> mpImgs) {
        this.mpImgs = mpImgs;
    }

    public ShopMallDetailEntity() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mpCollect);
        dest.writeString(this.mpId);
        dest.writeString(this.mpTitle);
        dest.writeDouble(this.mpPrice);
        dest.writeDouble(this.mpOldprice);
        dest.writeString(this.mpSimg);
        dest.writeString(this.mpAllprice);
        dest.writeString(this.mpColor);
        dest.writeDouble(this.mpKdprice);
        dest.writeString(this.mpAddress);
        dest.writeStringList(this.mpImg);
        dest.writeStringList(this.mpImgs);
    }

    protected ShopMallDetailEntity(Parcel in) {
        this.mpCollect = in.readInt();
        this.mpId = in.readString();
        this.mpTitle = in.readString();
        this.mpPrice = in.readDouble();
        this.mpOldprice = in.readDouble();
        this.mpSimg = in.readString();
        this.mpAllprice = in.readString();
        this.mpColor = in.readString();
        this.mpKdprice = in.readDouble();
        this.mpAddress = in.readString();
        this.mpImg = in.createStringArrayList();
        this.mpImgs = in.createStringArrayList();
    }

    public static final Creator<ShopMallDetailEntity> CREATOR = new Creator<ShopMallDetailEntity>() {
        @Override
        public ShopMallDetailEntity createFromParcel(Parcel source) {
            return new ShopMallDetailEntity(source);
        }

        @Override
        public ShopMallDetailEntity[] newArray(int size) {
            return new ShopMallDetailEntity[size];
        }
    };
}
