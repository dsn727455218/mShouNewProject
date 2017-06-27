package com.shownew.home.module.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author Jason
 * @version 1.0
 * @date 2017/5/5 0005
 */

public class SelectEntity implements Parcelable {
    private boolean select;
    private String ids;
    private String content;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public SelectEntity(boolean select, String ids, String content) {
        this.select = select;
        this.ids = ids;
        this.content = content;
    }

    public boolean isSelect() {
        return select;
    }

    public void setSelect(boolean select) {
        this.select = select;
    }

    public String getIds() {
        return ids;
    }

    public void setIds(String ids) {
        this.ids = ids;
    }

    public static Creator<SelectEntity> getCREATOR() {
        return CREATOR;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(this.select ? (byte) 1 : (byte) 0);
        dest.writeString(this.ids);
    }

    public SelectEntity() {
    }

    public SelectEntity(boolean select, String ids) {
        this.select = select;
        this.ids = ids;
    }

    protected SelectEntity(Parcel in) {
        this.select = in.readByte() != 0;
        this.ids = in.readString();
    }

    public static final Parcelable.Creator<SelectEntity> CREATOR = new Parcelable.Creator<SelectEntity>() {
        @Override
        public SelectEntity createFromParcel(Parcel source) {
            return new SelectEntity(source);
        }

        @Override
        public SelectEntity[] newArray(int size) {
            return new SelectEntity[size];
        }
    };
}
