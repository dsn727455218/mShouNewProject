package com.shownew.home.module.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by WP on 2017/8/7.
 */

public class AllEvelateEntity implements Parcelable {

    /**
     * dId : 28
     * dPid : 0
     * dMpid : 11
     * dNicecount : 0
     * dText : 巨蟹座我们
     * dImg :
     * dDate : 2017-08-05 18:25:50
     * subList : [{"sdId":11,"sdText":"真的太坑了","sdImg":"http://shounew.cn/images/2017-08-04/60d7b000dd2198a29dcc16a920.jpg20170804181221615,http://shounew.cn/images/2017-08-04/60d7b000dd2198a29dcc16a9739.png20170804181227660","sdType":0,"sdDaynum":1},{"sdId":12,"sdText":"受不鸟了。。。","sdImg":"http://shounew.cn/images/2017-08-04/60d7b000dd2198a29dcc16a920.jpg20170804181221615,http://shounew.cn/images/2017-08-04/60d7b000dd2198a29dcc16a9739.png20170804181227660","sdType":0,"sdDaynum":1}]
     * dIsnice : 0
     * dUname : 修改昵称
     * dUicon : http://shounew.cn/images/2017-08-05/1b143e209c886f6da06e75a2.png20170805170514890
     */

    private long dId;
    private long dPid;
    private long dMpid;
    private long dNicecount;
    private String dText;
    private String dImg;
    private String dDate;
    private long dIsnice;
    private String dUname;
    private String dUicon;
    private ArrayList<SubListBean> subList;

    public long getDId() {
        return dId;
    }

    public void setDId(long dId) {
        this.dId = dId;
    }

    public long getDPid() {
        return dPid;
    }

    public void setDPid(long dPid) {
        this.dPid = dPid;
    }

    public long getDMpid() {
        return dMpid;
    }

    public void setDMpid(long dMpid) {
        this.dMpid = dMpid;
    }

    public long getDNicecount() {
        return dNicecount;
    }

    public void setDNicecount(long dNicecount) {
        this.dNicecount = dNicecount;
    }

    public String getDText() {
        return dText;
    }

    public void setDText(String dText) {
        this.dText = dText;
    }

    public String getDImg() {
        return dImg;
    }

    public void setDImg(String dImg) {
        this.dImg = dImg;
    }

    public String getDDate() {
        return dDate;
    }

    public void setDDate(String dDate) {
        this.dDate = dDate;
    }

    public long getDIsnice() {
        return dIsnice;
    }

    public void setDIsnice(long dIsnice) {
        this.dIsnice = dIsnice;
    }

    public String getDUname() {
        return dUname;
    }

    public void setDUname(String dUname) {
        this.dUname = dUname;
    }

    public String getDUicon() {
        return dUicon;
    }

    public void setDUicon(String dUicon) {
        this.dUicon = dUicon;
    }

    public ArrayList<SubListBean> getSubList() {
        return subList;
    }

    public void setSubList(ArrayList<SubListBean> subList) {
        this.subList = subList;
    }

    public  class SubListBean {
        /**
         * sdId : 11
         * sdText : 真的太坑了
         * sdImg : http://shounew.cn/images/2017-08-04/60d7b000dd2198a29dcc16a920.jpg20170804181221615,http://shounew.cn/images/2017-08-04/60d7b000dd2198a29dcc16a9739.png20170804181227660
         * sdType : 0
         * sdDaynum : 1
         */

        private long sdId;
        private String sdText;
        private String sdImg;
        private long sdType;
        private long sdDaynum;

        public long getSdId() {
            return sdId;
        }

        public void setSdId(long sdId) {
            this.sdId = sdId;
        }

        public String getSdText() {
            return sdText;
        }

        public void setSdText(String sdText) {
            this.sdText = sdText;
        }

        public String getSdImg() {
            return sdImg;
        }

        public void setSdImg(String sdImg) {
            this.sdImg = sdImg;
        }

        public long getSdType() {
            return sdType;
        }

        public void setSdType(long sdType) {
            this.sdType = sdType;
        }

        public long getSdDaynum() {
            return sdDaynum;
        }

        public void setSdDaynum(long sdDaynum) {
            this.sdDaynum = sdDaynum;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.dId);
        dest.writeLong(this.dPid);
        dest.writeLong(this.dMpid);
        dest.writeLong(this.dNicecount);
        dest.writeString(this.dText);
        dest.writeString(this.dImg);
        dest.writeString(this.dDate);
        dest.writeLong(this.dIsnice);
        dest.writeString(this.dUname);
        dest.writeString(this.dUicon);
        dest.writeList(this.subList);
    }

    public AllEvelateEntity() {
    }

    protected AllEvelateEntity(Parcel in) {
        this.dId = in.readLong();
        this.dPid = in.readLong();
        this.dMpid = in.readLong();
        this.dNicecount = in.readLong();
        this.dText = in.readString();
        this.dImg = in.readString();
        this.dDate = in.readString();
        this.dIsnice = in.readLong();
        this.dUname = in.readString();
        this.dUicon = in.readString();
        this.subList = new ArrayList<SubListBean>();
        in.readList(this.subList, SubListBean.class.getClassLoader());
    }

    public static final Parcelable.Creator<AllEvelateEntity> CREATOR = new Parcelable.Creator<AllEvelateEntity>() {
        @Override
        public AllEvelateEntity createFromParcel(Parcel source) {
            return new AllEvelateEntity(source);
        }

        @Override
        public AllEvelateEntity[] newArray(int size) {
            return new AllEvelateEntity[size];
        }
    };
}
