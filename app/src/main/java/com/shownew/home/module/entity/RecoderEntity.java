package com.shownew.home.module.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**消费记录
 * @author Jason
 * @version 1.0
 * @date 2017/5/5 0005
 */

public class RecoderEntity implements Parcelable {

    /**
     * rId : 1
     * rTitle : 我的消费记录1
     * rNum : 1
     * rPrice : 12
     * rDate : 2017-04-25 05:00:00
     */

    private String rId;
    private String rTitle;
    private int rNum;
    private double rPrice;
    private String rDate;

    public String getRId() {
        return rId;
    }

    public void setRId(String rId) {
        this.rId = rId;
    }

    public String getRTitle() {
        return rTitle;
    }

    public void setRTitle(String rTitle) {
        this.rTitle = rTitle;
    }

    public int getRNum() {
        return rNum;
    }

    public void setRNum(int rNum) {
        this.rNum = rNum;
    }

    public double getRPrice() {
        return rPrice;
    }

    public void setRPrice(int rPrice) {
        this.rPrice = rPrice;
    }

    public String getRDate() {
        return rDate;
    }

    public void setRDate(String rDate) {
        this.rDate = rDate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.rId);
        dest.writeString(this.rTitle);
        dest.writeInt(this.rNum);
        dest.writeDouble(this.rPrice);
        dest.writeString(this.rDate);
    }

    public RecoderEntity() {
    }

    protected RecoderEntity(Parcel in) {
        this.rId = in.readString();
        this.rTitle = in.readString();
        this.rNum = in.readInt();
        this.rPrice = in.readDouble();
        this.rDate = in.readString();
    }

    public static final Parcelable.Creator<RecoderEntity> CREATOR = new Parcelable.Creator<RecoderEntity>() {
        @Override
        public RecoderEntity createFromParcel(Parcel source) {
            return new RecoderEntity(source);
        }

        @Override
        public RecoderEntity[] newArray(int size) {
            return new RecoderEntity[size];
        }
    };
}
