package com.shownew.home.module.dao;


import android.os.Parcel;
import android.os.Parcelable;

public class ShopCarEntity implements Parcelable {
    public Integer get_id() {
        return _id;
    }

    public void set_id(Integer _id) {
        this._id = _id;
    }

    public Integer getShPid() {
        return shPid;
    }

    public void setShPid(Integer shPid) {
        this.shPid = shPid;
    }

    public Integer getShMpid() {
        return shMpid;
    }

    public void setShMpid(Integer shMpid) {
        this.shMpid = shMpid;
    }

    public String getShTitle() {
        return shTitle;
    }

    public void setShTitle(String shTitle) {
        this.shTitle = shTitle;
    }

    public String getShSimg() {
        return shSimg;
    }

    public void setShSimg(String shSimg) {
        this.shSimg = shSimg;
    }

    public String getShColor() {
        return shColor;
    }

    public void setShColor(String shColor) {
        this.shColor = shColor;
    }

    public Double getShPrice() {
        return shPrice;
    }

    public void setShPrice(Double shPrice) {
        this.shPrice = shPrice;
    }

    public Double getShKdprice() {
        return shKdprice;
    }

    public void setShKdprice(Double shKdprice) {
        this.shKdprice = shKdprice;
    }

    public Integer getShNum() {
        return shNum;
    }

    public void setShNum(Integer shNum) {
        this.shNum = shNum;
    }

    public String getShDate() {
        return shDate;
    }

    public void setShDate(String shDate) {
        this.shDate = shDate;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public boolean isEdit() {
        return isEdit;
    }

    public void setEdit(boolean edit) {
        isEdit = edit;
    }

    private Integer _id;
    private Integer shPid;

    private Integer shMpid;

    private String shTitle;

    private String shSimg;

    private String shColor;

    private Double shPrice;
    private Double singlePrice=0d;

    public Double getSinglePrice() {
        return singlePrice;
    }

    public void setSinglePrice(Double singlePrice) {
        this.singlePrice = singlePrice;
    }

    private Double shKdprice;

    private Integer shNum;

    private String shDate;

    private boolean isSelect;

    private boolean isEdit;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this._id);
        dest.writeValue(this.shPid);
        dest.writeValue(this.shMpid);
        dest.writeString(this.shTitle);
        dest.writeString(this.shSimg);
        dest.writeString(this.shColor);
        dest.writeValue(this.shPrice);
        dest.writeValue(this.singlePrice);
        dest.writeValue(this.shKdprice);
        dest.writeValue(this.shNum);
        dest.writeString(this.shDate);
        dest.writeByte(this.isSelect ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isEdit ? (byte) 1 : (byte) 0);
    }

    public ShopCarEntity() {
    }

    protected ShopCarEntity(Parcel in) {
        this._id = (Integer) in.readValue(Integer.class.getClassLoader());
        this.shPid = (Integer) in.readValue(Integer.class.getClassLoader());
        this.shMpid = (Integer) in.readValue(Integer.class.getClassLoader());
        this.shTitle = in.readString();
        this.shSimg = in.readString();
        this.shColor = in.readString();
        this.shPrice = (Double) in.readValue(Double.class.getClassLoader());
        this.singlePrice = (Double) in.readValue(Double.class.getClassLoader());
        this.shKdprice = (Double) in.readValue(Double.class.getClassLoader());
        this.shNum = (Integer) in.readValue(Integer.class.getClassLoader());
        this.shDate = in.readString();
        this.isSelect = in.readByte() != 0;
        this.isEdit = in.readByte() != 0;
    }

}
