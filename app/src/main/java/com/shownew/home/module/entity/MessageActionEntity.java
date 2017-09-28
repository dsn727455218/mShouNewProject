package com.shownew.home.module.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**活动消息
 * @author Jason
 * @version 1.0
 * @date 2017/5/24 0024
 */

public class MessageActionEntity implements Parcelable {


    /**
     * nId : 2
     * nText : 充值得好礼
     * nDate : 2017-04-22 12:00:01
     * nFrom : 2
     * nImg : http://112.74.55.239/images/2017-04-20/20170420165744448106480459538400.jpg
     * nUrl : http://112.74.55.239/images/2017-04-21/zFSBgPsN6dYGnOipMh+EnQ==.html
     * nPid : 0
     */

    private int nId;
    private String nText;
    private String nDate;
    private int nFrom;
    private String nImg;
    private String nUrl;
    private String nPid;
   private String nMpid;

    public String getnMpid() {
        return nMpid;
    }

    public void setnMpid(String nMpid) {
        this.nMpid = nMpid;
    }

    public static Creator<MessageActionEntity> getCREATOR() {
        return CREATOR;
    }

    public int getNId() {
        return nId;
    }

    public void setNId(int nId) {
        this.nId = nId;
    }

    public String getNText() {
        return nText;
    }

    public void setNText(String nText) {
        this.nText = nText;
    }

    public String getNDate() {
        return nDate;
    }

    public void setNDate(String nDate) {
        this.nDate = nDate;
    }

    public int getNFrom() {
        return nFrom;
    }

    public void setNFrom(int nFrom) {
        this.nFrom = nFrom;
    }

    public String getNImg() {
        return nImg;
    }

    public void setNImg(String nImg) {
        this.nImg = nImg;
    }

    public String getNUrl() {
        return nUrl;
    }

    public void setNUrl(String nUrl) {
        this.nUrl = nUrl;
    }

    public String getNPid() {
        return nPid;
    }

    public void setNPid(String nPid) {
        this.nPid = nPid;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.nId);
        dest.writeString(this.nText);
        dest.writeString(this.nDate);
        dest.writeInt(this.nFrom);
        dest.writeString(this.nImg);
        dest.writeString(this.nUrl);
        dest.writeString(this.nPid);
    }

    public MessageActionEntity() {
    }

    protected MessageActionEntity(Parcel in) {
        this.nId = in.readInt();
        this.nText = in.readString();
        this.nDate = in.readString();
        this.nFrom = in.readInt();
        this.nImg = in.readString();
        this.nUrl = in.readString();
        this.nPid = in.readString();
    }

    public static final Parcelable.Creator<MessageActionEntity> CREATOR = new Parcelable.Creator<MessageActionEntity>() {
        @Override
        public MessageActionEntity createFromParcel(Parcel source) {
            return new MessageActionEntity(source);
        }

        @Override
        public MessageActionEntity[] newArray(int size) {
            return new MessageActionEntity[size];
        }
    };
}
