package com.shownew.home.module.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**首牛商城
 * @author Jason
 * @version 1.0
 * @date 2017/6/3 0003
 */

public class ShopMallListEntity implements Parcelable {

    /**
     * mpId : 1
     * mpTitle : 成都玉骑铃电动车72v悍将 奇蕾倍特安尔达雅迪 同款电动车电瓶车
     * mpPrice : 0.01
     * mpOldprice : 3000
     * mpSimg : http://shounew.cn/images/2017-05-08/TB2utWgjbVkpujSspcXXbSMVXa_!!197262666.jpg
     */

    private String mpId;
    private String mpTitle;
    private double mpPrice;
    private double mpOldprice;
    private String mpSimg;

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mpId);
        dest.writeString(this.mpTitle);
        dest.writeDouble(this.mpPrice);
        dest.writeDouble(this.mpOldprice);
        dest.writeString(this.mpSimg);
    }

    public ShopMallListEntity() {
    }

    protected ShopMallListEntity(Parcel in) {
        this.mpId = in.readString();
        this.mpTitle = in.readString();
        this.mpPrice = in.readDouble();
        this.mpOldprice = in.readInt();
        this.mpSimg = in.readString();
    }

    public static final Parcelable.Creator<ShopMallListEntity> CREATOR = new Parcelable.Creator<ShopMallListEntity>() {
        @Override
        public ShopMallListEntity createFromParcel(Parcel source) {
            return new ShopMallListEntity(source);
        }

        @Override
        public ShopMallListEntity[] newArray(int size) {
            return new ShopMallListEntity[size];
        }
    };
}
