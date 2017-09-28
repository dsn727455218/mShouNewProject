package com.shownew.home.module.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**我的车辆详情实体
 * @author Jason
 * @version 1.0
 * @date 2017/4/25 0025
 */

public class CarEntity implements Parcelable {

    private String cDefault;

    public String getcDefault() {
        return cDefault;
    }

    public void setcDefault(String cDefault) {
        this.cDefault = cDefault;
    }

    /**
     * cId : 5
     * cName : 我的倍特
     * cType : 电动车
     * cIcon :
     * cMark : 倍特
     * cBattery : 64
     * cUname : 冉椿林
     * cUidcard : 511322199402469878
     * cHardcode : 123659784653129
     * cFramenum : 54689lhf57ds8dg69
     * cLicencenum : 川A15487
     * cModelnum : Ft185
     * cDate : 2017-04-24 16:53:40
     */

    private String cId;
    private String cName;
    private String cType;
    private String cIcon;
    private String cMark;
    private int cBattery;
    private String cUname;
    private String cUidcard;
    private String cHardcode;
    private String cFramenum;
    private String cLicencenum;
    private String cModelnum;
    private String cDate;

    public String getCId() {
        return cId;
    }

    public void setCId(String cId) {
        this.cId = cId;
    }

    public String getCName() {
        return cName;
    }

    public void setCName(String cName) {
        this.cName = cName;
    }

    public String getCType() {
        return cType;
    }

    public void setCType(String cType) {
        this.cType = cType;
    }

    public String getCIcon() {
        return cIcon;
    }

    public void setCIcon(String cIcon) {
        this.cIcon = cIcon;
    }

    public String getCMark() {
        return cMark;
    }

    public void setCMark(String cMark) {
        this.cMark = cMark;
    }

    public int getCBattery() {
        return cBattery;
    }

    public void setCBattery(int cBattery) {
        this.cBattery = cBattery;
    }

    public String getCUname() {
        return cUname;
    }

    public void setCUname(String cUname) {
        this.cUname = cUname;
    }

    public String getCUidcard() {
        return cUidcard;
    }

    public void setCUidcard(String cUidcard) {
        this.cUidcard = cUidcard;
    }

    public String getCHardcode() {
        return cHardcode;
    }

    public void setCHardcode(String cHardcode) {
        this.cHardcode = cHardcode;
    }

    public String getCFramenum() {
        return cFramenum;
    }

    public void setCFramenum(String cFramenum) {
        this.cFramenum = cFramenum;
    }

    public String getCLicencenum() {
        return cLicencenum;
    }

    public void setCLicencenum(String cLicencenum) {
        this.cLicencenum = cLicencenum;
    }

    public String getCModelnum() {
        return cModelnum;
    }

    public void setCModelnum(String cModelnum) {
        this.cModelnum = cModelnum;
    }

    public String getCDate() {
        return cDate;
    }

    public void setCDate(String cDate) {
        this.cDate = cDate;
    }

    public CarEntity() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.cDefault);
        dest.writeString(this.cId);
        dest.writeString(this.cName);
        dest.writeString(this.cType);
        dest.writeString(this.cIcon);
        dest.writeString(this.cMark);
        dest.writeInt(this.cBattery);
        dest.writeString(this.cUname);
        dest.writeString(this.cUidcard);
        dest.writeString(this.cHardcode);
        dest.writeString(this.cFramenum);
        dest.writeString(this.cLicencenum);
        dest.writeString(this.cModelnum);
        dest.writeString(this.cDate);
    }

    protected CarEntity(Parcel in) {
        this.cDefault = in.readString();
        this.cId = in.readString();
        this.cName = in.readString();
        this.cType = in.readString();
        this.cIcon = in.readString();
        this.cMark = in.readString();
        this.cBattery = in.readInt();
        this.cUname = in.readString();
        this.cUidcard = in.readString();
        this.cHardcode = in.readString();
        this.cFramenum = in.readString();
        this.cLicencenum = in.readString();
        this.cModelnum = in.readString();
        this.cDate = in.readString();
    }

    public static final Creator<CarEntity> CREATOR = new Creator<CarEntity>() {
        @Override
        public CarEntity createFromParcel(Parcel source) {
            return new CarEntity(source);
        }

        @Override
        public CarEntity[] newArray(int size) {
            return new CarEntity[size];
        }
    };
}
