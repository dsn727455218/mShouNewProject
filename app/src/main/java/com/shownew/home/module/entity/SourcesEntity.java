package com.shownew.home.module.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author Jason
 * @version 1.0
 * @date 2017/5/19 0019
 */

public class SourcesEntity implements Parcelable {


    /**
     * sType : 0
     * sImg : http://112.74.55.239/images/2017-04-24/d7acdabbc85da90b6bddd2c0.jpg
     * sPid : 0
     */

    private int sType;
    private String sImg;
    private String sPid;

    public int getSType() {
        return sType;
    }

    public void setSType(int sType) {
        this.sType = sType;
    }

    public String getSImg() {
        return sImg;
    }

    public void setSImg(String sImg) {
        this.sImg = sImg;
    }

    public String getSPid() {
        return sPid;
    }

    public void setSPid(String sPid) {
        this.sPid = sPid;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.sType);
        dest.writeString(this.sImg);
        dest.writeString(this.sPid);
    }

    public SourcesEntity() {
    }

    protected SourcesEntity(Parcel in) {
        this.sType = in.readInt();
        this.sImg = in.readString();
        this.sPid = in.readString();
    }

    public static final Parcelable.Creator<SourcesEntity> CREATOR = new Parcelable.Creator<SourcesEntity>() {
        @Override
        public SourcesEntity createFromParcel(Parcel source) {
            return new SourcesEntity(source);
        }

        @Override
        public SourcesEntity[] newArray(int size) {
            return new SourcesEntity[size];
        }
    };
}
