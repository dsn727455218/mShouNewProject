package com.shownew.home.module.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**快递实体
 * @author Jason
 * @version 1.0
 * @date 2017/5/26 0026
 */

public class KdEntity implements Parcelable {

    /**
     * AcceptStation : 【广东省深圳市宝安区龙华公司】 已收件
     * AcceptTime : 2017-03-28 01:07:51
     */

    private String AcceptStation;
    private String AcceptTime;

    public String getAcceptStation() {
        return AcceptStation;
    }

    public void setAcceptStation(String AcceptStation) {
        this.AcceptStation = AcceptStation;
    }

    public String getAcceptTime() {
        return AcceptTime;
    }

    public void setAcceptTime(String AcceptTime) {
        this.AcceptTime = AcceptTime;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.AcceptStation);
        dest.writeString(this.AcceptTime);
    }

    public KdEntity() {
    }

    protected KdEntity(Parcel in) {
        this.AcceptStation = in.readString();
        this.AcceptTime = in.readString();
    }

    public static final Parcelable.Creator<KdEntity> CREATOR = new Parcelable.Creator<KdEntity>() {
        @Override
        public KdEntity createFromParcel(Parcel source) {
            return new KdEntity(source);
        }

        @Override
        public KdEntity[] newArray(int size) {
            return new KdEntity[size];
        }
    };
}
