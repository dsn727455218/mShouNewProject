package com.shownew.home.module.entity;

/**售后网点的实体
 * @author Jason
 * @version 1.0
 * @date 2017/4/26 0026
 */

public class AfterMarkerEntity {

    /**
     * bId : 12
     * bName : 大众汽车售后
     * bImg :
     * bPhone : 1645854644
     * bAddress :
     * bGps : 105.254698,54.259685
     */

    private int bId;
    private String bName;
    private String bImg;
    private String bPhone;
    private String bAddress;
    private String bGps;

    public int getBId() {
        return bId;
    }

    public void setBId(int bId) {
        this.bId = bId;
    }

    public String getBName() {
        return bName;
    }

    public void setBName(String bName) {
        this.bName = bName;
    }

    public String getBImg() {
        return bImg;
    }

    public void setBImg(String bImg) {
        this.bImg = bImg;
    }

    public String getBPhone() {
        return bPhone;
    }

    public void setBPhone(String bPhone) {
        this.bPhone = bPhone;
    }

    public String getBAddress() {
        return bAddress;
    }

    public void setBAddress(String bAddress) {
        this.bAddress = bAddress;
    }

    public String getBGps() {
        return bGps;
    }

    public void setBGps(String bGps) {
        this.bGps = bGps;
    }
}
