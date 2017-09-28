package com.shownew.home.module.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**订单   详情
 * @author Jason
 * @version 1.0
 * @date 2017/5/20 0020
 */

public class OderDetailEntity implements Parcelable {


    /**
     * oId : 58
     * oPid : 1
     * oPrice : 0.01
     * oColor : 白色裸车
     * oNum : 1
     * oState : 1
     * oKdNo :
     * oKdCompany :
     * oDate : 2017-05-18 18:08:47
     * oTitle : 成都玉骑铃电动车72v悍将 奇蕾倍特安尔达雅迪 同款电动车电瓶车
     * oSimg : http://shounew.cn/images/2017-05-08/TB2utWgjbVkpujSspcXXbSMVXa_!!197262666.jpg
     * oTotalprice : 0.02
     * oKdprice : 0.01
     * oKdname : 邓升发
     * oKdaddress : 北京市北京市东城区静默
     * oKdphone : 15775692242
     */
    private int oMpid;


    public int getoMpid() {
        return oMpid;
    }

    public void setoMpid(int oMpid) {
        this.oMpid = oMpid;
    }

    private int oId;
    private int oPid;
    private double oPrice;
    private String oColor;
    private int oNum;
    private int oState;
    private String oKdNo;
    private String oKdCompany;
    private String oDate;
    private String oTitle;
    private String oSimg;
    private double oTotalprice;
    private double oKdprice;
    private String oKdname;
    private String oKdaddress;
    private String oKdphone;

    public int getOId() {
        return oId;
    }

    public void setOId(int oId) {
        this.oId = oId;
    }

    public int getOPid() {
        return oPid;
    }

    public void setOPid(int oPid) {
        this.oPid = oPid;
    }

    public double getOPrice() {
        return oPrice;
    }

    public void setOPrice(double oPrice) {
        this.oPrice = oPrice;
    }

    public String getOColor() {
        return oColor;
    }

    public void setOColor(String oColor) {
        this.oColor = oColor;
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

    public String getOKdNo() {
        return oKdNo;
    }

    public void setOKdNo(String oKdNo) {
        this.oKdNo = oKdNo;
    }

    public String getOKdCompany() {
        return oKdCompany;
    }

    public void setOKdCompany(String oKdCompany) {
        this.oKdCompany = oKdCompany;
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

    public double getOTotalprice() {
        return oTotalprice;
    }

    public void setOTotalprice(double oTotalprice) {
        this.oTotalprice = oTotalprice;
    }

    public double getOKdprice() {
        return oKdprice;
    }

    public void setOKdprice(double oKdprice) {
        this.oKdprice = oKdprice;
    }

    public String getOKdname() {
        return oKdname;
    }

    public void setOKdname(String oKdname) {
        this.oKdname = oKdname;
    }

    public String getOKdaddress() {
        return oKdaddress;
    }

    public void setOKdaddress(String oKdaddress) {
        this.oKdaddress = oKdaddress;
    }

    public String getOKdphone() {
        return oKdphone;
    }

    public void setOKdphone(String oKdphone) {
        this.oKdphone = oKdphone;
    }

    public OderDetailEntity() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.oMpid);
        dest.writeInt(this.oId);
        dest.writeInt(this.oPid);
        dest.writeDouble(this.oPrice);
        dest.writeString(this.oColor);
        dest.writeInt(this.oNum);
        dest.writeInt(this.oState);
        dest.writeString(this.oKdNo);
        dest.writeString(this.oKdCompany);
        dest.writeString(this.oDate);
        dest.writeString(this.oTitle);
        dest.writeString(this.oSimg);
        dest.writeDouble(this.oTotalprice);
        dest.writeDouble(this.oKdprice);
        dest.writeString(this.oKdname);
        dest.writeString(this.oKdaddress);
        dest.writeString(this.oKdphone);
    }

    protected OderDetailEntity(Parcel in) {
        this.oMpid = in.readInt();
        this.oId = in.readInt();
        this.oPid = in.readInt();
        this.oPrice = in.readDouble();
        this.oColor = in.readString();
        this.oNum = in.readInt();
        this.oState = in.readInt();
        this.oKdNo = in.readString();
        this.oKdCompany = in.readString();
        this.oDate = in.readString();
        this.oTitle = in.readString();
        this.oSimg = in.readString();
        this.oTotalprice = in.readDouble();
        this.oKdprice = in.readDouble();
        this.oKdname = in.readString();
        this.oKdaddress = in.readString();
        this.oKdphone = in.readString();
    }

    public static final Creator<OderDetailEntity> CREATOR = new Creator<OderDetailEntity>() {
        @Override
        public OderDetailEntity createFromParcel(Parcel source) {
            return new OderDetailEntity(source);
        }

        @Override
        public OderDetailEntity[] newArray(int size) {
            return new OderDetailEntity[size];
        }
    };
}
