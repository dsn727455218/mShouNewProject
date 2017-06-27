package com.shownew.home.module.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**低价保险的实体
 * @author Jason
 * @version 1.0
 * @date 2017/4/26 0026
 */

public class InsuranceEntity implements Parcelable {

    /**
     * iId : 3
     * iName : 首牛车险
     * iImg :
     * iPhone : 18989565241
     * iAddress :
     * iGps : 104.569874,40.256987
     */

    private int iId;
    private String iName;
    private String iImg;
    private String iPhone;
    private String iAddress;
    private String iGps;

    public int getIId() {
        return iId;
    }

    public void setIId(int iId) {
        this.iId = iId;
    }

    public String getIName() {
        return iName;
    }

    public void setIName(String iName) {
        this.iName = iName;
    }

    public String getIImg() {
        return iImg;
    }

    public void setIImg(String iImg) {
        this.iImg = iImg;
    }

    public String getIPhone() {
        return iPhone;
    }

    public void setIPhone(String iPhone) {
        this.iPhone = iPhone;
    }

    public String getIAddress() {
        return iAddress;
    }

    public void setIAddress(String iAddress) {
        this.iAddress = iAddress;
    }

    public String getIGps() {
        return iGps;
    }

    public void setIGps(String iGps) {
        this.iGps = iGps;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.iId);
        dest.writeString(this.iName);
        dest.writeString(this.iImg);
        dest.writeString(this.iPhone);
        dest.writeString(this.iAddress);
        dest.writeString(this.iGps);
    }

    public InsuranceEntity() {
    }

    protected InsuranceEntity(Parcel in) {
        this.iId = in.readInt();
        this.iName = in.readString();
        this.iImg = in.readString();
        this.iPhone = in.readString();
        this.iAddress = in.readString();
        this.iGps = in.readString();
    }

    public static final Parcelable.Creator<InsuranceEntity> CREATOR = new Parcelable.Creator<InsuranceEntity>() {
        @Override
        public InsuranceEntity createFromParcel(Parcel source) {
            return new InsuranceEntity(source);
        }

        @Override
        public InsuranceEntity[] newArray(int size) {
            return new InsuranceEntity[size];
        }
    };
}
