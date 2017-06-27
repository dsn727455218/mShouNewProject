package com.shownew.home.module.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author Jason
 * @version 1.0
 * @date 2017/5/3 0003
 */

public class AddressEntity implements Parcelable {

    /**
     * lId : 3
     * lName : 冉椿林
     * lPhone : 157955458888
     * lCity : 四川省成都市双流区
     * lAddress : 四川省成都市双流县感知中心779号
     */

    private String lId;
    private String lName;
    private String lPhone;
    private String lCity;
    private String lAddress;

    protected AddressEntity(Parcel in) {
        lId = in.readString();
        lName = in.readString();
        lPhone = in.readString();
        lCity = in.readString();
        lAddress = in.readString();
    }

    public static final Creator<AddressEntity> CREATOR = new Creator<AddressEntity>() {
        @Override
        public AddressEntity createFromParcel(Parcel in) {
            return new AddressEntity(in);
        }

        @Override
        public AddressEntity[] newArray(int size) {
            return new AddressEntity[size];
        }
    };

    public String getLId() {
        return lId;
    }

    public void setLId(String lId) {
        this.lId = lId;
    }

    public String getLName() {
        return lName;
    }

    public void setLName(String lName) {
        this.lName = lName;
    }

    public String getLPhone() {
        return lPhone;
    }

    public void setLPhone(String lPhone) {
        this.lPhone = lPhone;
    }

    public String getLCity() {
        return lCity;
    }

    public void setLCity(String lCity) {
        this.lCity = lCity;
    }

    public String getLAddress() {
        return lAddress;
    }

    public void setLAddress(String lAddress) {
        this.lAddress = lAddress;
    }



    public AddressEntity() {
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(lId);
        dest.writeString(lName);
        dest.writeString(lPhone);
        dest.writeString(lCity);
        dest.writeString(lAddress);
    }
}
