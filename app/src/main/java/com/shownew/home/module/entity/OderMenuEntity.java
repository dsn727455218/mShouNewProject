package com.shownew.home.module.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author Jason
 * @version 1.0
 * @date 2017/5/5 0005
 */

public class OderMenuEntity implements Parcelable {

    /**
     * oId : 1
     * oPid : 1
     * oNum : 1
     * oState : 0
     * oDate : 2017-04-12 15:00:00
     * oTitle : 首牛多功能充电器
     * oSimg : http://112.74.55.239/images/2015-15-23/sd.jpg
     * oTotalprice : 308
     */
    private String oMpid;

    public String getoMpid() {
        return oMpid;
    }

    public void setoMpid(String oMpid) {
        this.oMpid = oMpid;
    }

    private String oId;
    private int oPid;
    private int oNum;
    private int oState;
    private String oDate;
    private String oTitle;
    private String oSimg;
    private String oTotalprice;

    public String getOId() {
        return oId;
    }

    public void setOId(String oId) {
        this.oId = oId;
    }

    public int getOPid() {
        return oPid;
    }

    public void setOPid(int oPid) {
        this.oPid = oPid;
    }

    public int getONum() {
        return oNum;
    }

    public void setONum(int oNum) {
        this.oNum = oNum;
    }

    public int getOState() {
        return oState;
    }

    public void setOState(int oState) {
        this.oState = oState;
    }

    public String getODate() {
        return oDate;
    }

    public void setODate(String oDate) {
        this.oDate = oDate;
    }

    public String getOTitle() {
        return oTitle;
    }

    public void setOTitle(String oTitle) {
        this.oTitle = oTitle;
    }

    public String getOSimg() {
        return oSimg;
    }

    public void setOSimg(String oSimg) {
        this.oSimg = oSimg;
    }

    public String getOTotalprice() {
        return oTotalprice;
    }

    public void setOTotalprice(String oTotalprice) {
        this.oTotalprice = oTotalprice;
    }

    public OderMenuEntity() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.oMpid);
        dest.writeString(this.oId);
        dest.writeInt(this.oPid);
        dest.writeInt(this.oNum);
        dest.writeInt(this.oState);
        dest.writeString(this.oDate);
        dest.writeString(this.oTitle);
        dest.writeString(this.oSimg);
        dest.writeString(this.oTotalprice);
    }

    protected OderMenuEntity(Parcel in) {
        this.oMpid = in.readString();
        this.oId = in.readString();
        this.oPid = in.readInt();
        this.oNum = in.readInt();
        this.oState = in.readInt();
        this.oDate = in.readString();
        this.oTitle = in.readString();
        this.oSimg = in.readString();
        this.oTotalprice = in.readString();
    }

    public static final Creator<OderMenuEntity> CREATOR = new Creator<OderMenuEntity>() {
        @Override
        public OderMenuEntity createFromParcel(Parcel source) {
            return new OderMenuEntity(source);
        }

        @Override
        public OderMenuEntity[] newArray(int size) {
            return new OderMenuEntity[size];
        }
    };
}
