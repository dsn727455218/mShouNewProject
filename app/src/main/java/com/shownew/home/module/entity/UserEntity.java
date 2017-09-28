package com.shownew.home.module.entity;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;

/**用户实体
 * @author Jason
 * @version 1.0
 * @date 2017/3/31 0031
 */

@SuppressLint("ParcelCreator")
public class UserEntity implements Parcelable {
    /**
     * result : 1
     * jSessionId : P90UWCppnEKhQeA9igGzsQ==
     * user : {"uId":9,"uPhone":"15775692033","uIcon":null,"uNickname":null,"uCartype":"电动车","uHardcode":"106480459538400","uAddress":null,"uRemain":0}
     */
    private int result;
    private String jSessionId;
    private UserBean user;

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public String getJSessionId() {
        return jSessionId;
    }

    public void setJSessionId(String jSessionId) {
        this.jSessionId = jSessionId;
    }

    public UserBean getUser() {
        return user;
    }

    public void setUser(UserBean user) {
        this.user = user;
    }

    public class UserBean implements Parcelable{

        private String password;

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        /**
         * uId : 9
         * uPhone : 15775692033
         * uIcon : http://shounew.cn/images/2017-04-27/3eb956b243113bd65c6271e2.ico
         * uNickname : 打酱油
         * uCartype : 电动车
         * uAddress : 四川省成都市双流区
         * uPayaddress : 四川省成都市双流县感知中心779号
         * uPayphone : 157955458888
         * uPayname : alin
         * uRemain : 0
         */

        private String uId;
        private String uPhone;
        private String uIcon;
        private String uNickname;
        private String uCartype;
        private String uAddress;
        private String uPayaddress;
        private String uPayphone;
        private String uPayname;
        private double uRemain;

        public String getUId() {
            return uId;
        }

        public void setUId(String uId) {
            this.uId = uId;
        }

        public String getUPhone() {
            return uPhone;
        }

        public void setUPhone(String uPhone) {
            this.uPhone = uPhone;
        }

        public String getUIcon() {
            return uIcon;
        }

        public void setUIcon(String uIcon) {
            this.uIcon = uIcon;
        }

        public String getUNickname() {
            return uNickname;
        }

        public void setUNickname(String uNickname) {
            this.uNickname = uNickname;
        }

        public String getUCartype() {
            return uCartype;
        }

        public void setUCartype(String uCartype) {
            this.uCartype = uCartype;
        }

        public String getUAddress() {
            return uAddress;
        }

        public void setUAddress(String uAddress) {
            this.uAddress = uAddress;
        }

        public String getUPayaddress() {
            return uPayaddress;
        }

        public void setUPayaddress(String uPayaddress) {
            this.uPayaddress = uPayaddress;
        }

        public String getUPayphone() {
            return uPayphone;
        }

        public void setUPayphone(String uPayphone) {
            this.uPayphone = uPayphone;
        }

        public String getUPayname() {
            return uPayname;
        }

        public void setUPayname(String uPayname) {
            this.uPayname = uPayname;
        }

        public double getURemain() {
            return uRemain;
        }

        public void setURemain(double uRemain) {
            this.uRemain = uRemain;
        }

        public UserBean() {
        }

        protected UserBean(Parcel in) {
            this.password = in.readString();
            this.uId = in.readString();
            this.uPhone = in.readString();
            this.uIcon = in.readString();
            this.uNickname = in.readString();
            this.uCartype = in.readString();
            this.uAddress = in.readString();
            this.uPayaddress = in.readString();
            this.uPayphone = in.readString();
            this.uPayname = in.readString();
            this.uRemain = in.readInt();
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {

        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.result);
        dest.writeString(this.jSessionId);
    }

    public UserEntity() {
    }

    protected UserEntity(Parcel in) {
        this.result = in.readInt();
        this.jSessionId = in.readString();
        this.user = in.readParcelable(UserBean.class.getClassLoader());
    }

}
