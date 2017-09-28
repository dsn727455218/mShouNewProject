package com.shownew.home.module.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**城市选择 实体
 * @author Jason
 * @version 1.0
 * @date 2017/4/20 0020
 */

public class CityEntity implements Parcelable {


    /**
     * id : 1
     * privince : 安徽省
     * city : [{"id":"7","city":"安庆市","district":[{"id":"823","district":"枞阳县"}]}]
     */

    private String id;
    private String privince;
    private List<CityBean> city;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPrivince() {
        return privince;
    }

    public void setPrivince(String privince) {
        this.privince = privince;
    }

    public List<CityBean> getCity() {
        return city;
    }

    public void setCity(List<CityBean> city) {
        this.city = city;
    }

    public static class CityBean {
        /**
         * id : 7
         * city : 安庆市
         * district : [{"id":"823","district":"枞阳县"}]
         */

        private String id;
        private String city;
        private List<DistrictBean> district;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public List<DistrictBean> getDistrict() {
            return district;
        }

        public void setDistrict(List<DistrictBean> district) {
            this.district = district;
        }

        public static class DistrictBean {
            /**
             * id : 823
             * district : 枞阳县
             */

            private String id;
            private String district;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getDistrict() {
                return district;
            }

            public void setDistrict(String district) {
                this.district = district;
            }
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.privince);
        dest.writeList(this.city);
    }

    public CityEntity() {
    }

    protected CityEntity(Parcel in) {
        this.id = in.readString();
        this.privince = in.readString();
        this.city = new ArrayList<CityBean>();
        in.readList(this.city, CityBean.class.getClassLoader());
    }

    public static final Parcelable.Creator<CityEntity> CREATOR = new Parcelable.Creator<CityEntity>() {
        @Override
        public CityEntity createFromParcel(Parcel source) {
            return new CityEntity(source);
        }

        @Override
        public CityEntity[] newArray(int size) {
            return new CityEntity[size];
        }
    };
}
