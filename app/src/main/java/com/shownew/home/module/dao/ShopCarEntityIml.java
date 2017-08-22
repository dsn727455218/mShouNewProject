package com.shownew.home.module.dao;

import android.os.Parcel;

/**
 * Created by WP on 2017/8/11.
 */

public class ShopCarEntityIml extends ShopCarEntity {

    private String shId;

    public String getShId() {
        return shId;
    }

    public void setShId(String shId) {
        this.shId = shId;
    }

    private String oNote;

    public String getoNote() {
        return oNote;
    }

    public void setoNote(String oNote) {
        this.oNote = oNote;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.shId);
        dest.writeString(this.oNote);
    }

    public ShopCarEntityIml() {
    }

    protected ShopCarEntityIml(Parcel in) {
        super(in);
        this.shId = in.readString();
        this.oNote = in.readString();
    }

    public static final Creator<ShopCarEntityIml> CREATOR = new Creator<ShopCarEntityIml>() {
        @Override
        public ShopCarEntityIml createFromParcel(Parcel source) {
            return new ShopCarEntityIml(source);
        }

        @Override
        public ShopCarEntityIml[] newArray(int size) {
            return new ShopCarEntityIml[size];
        }
    };
}
