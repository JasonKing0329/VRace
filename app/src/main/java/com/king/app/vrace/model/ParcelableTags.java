package com.king.app.vrace.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/6/29 13:52
 */
public class ParcelableTags implements Parcelable {

    private List<Long> tagIdList;

    public List<Long> getTagIdList() {
        return tagIdList;
    }

    public void setTagIdList(List<Long> tagIdList) {
        this.tagIdList = tagIdList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(this.tagIdList);
    }

    public ParcelableTags() {
    }

    protected ParcelableTags(Parcel in) {
        this.tagIdList = new ArrayList<Long>();
        in.readList(this.tagIdList, Long.class.getClassLoader());
    }

    public static final Parcelable.Creator<ParcelableTags> CREATOR = new Parcelable.Creator<ParcelableTags>() {
        @Override
        public ParcelableTags createFromParcel(Parcel source) {
            return new ParcelableTags(source);
        }

        @Override
        public ParcelableTags[] newArray(int size) {
            return new ParcelableTags[size];
        }
    };
}
