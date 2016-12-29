package com.example.plu.myapp.biggift.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by chengXing on 2016/12/28.
 * 文件中的每一个大额礼物
 */

public class LargeGift implements Parcelable {
    private String path;
    private String name;
    private boolean isOk;
    private String errorMessage;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isOk() {
        return isOk;
    }

    public void setOk(boolean ok) {
        isOk = ok;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.path);
        dest.writeString(this.name);
        dest.writeByte(this.isOk ? (byte) 1 : (byte) 0);
        dest.writeString(this.errorMessage);
    }

    public LargeGift() {
    }

    protected LargeGift(Parcel in) {
        this.path = in.readString();
        this.name = in.readString();
        this.isOk = in.readByte() != 0;
        this.errorMessage = in.readString();
    }

    public static final Parcelable.Creator<LargeGift> CREATOR = new Parcelable.Creator<LargeGift>() {
        @Override
        public LargeGift createFromParcel(Parcel source) {
            return new LargeGift(source);
        }

        @Override
        public LargeGift[] newArray(int size) {
            return new LargeGift[size];
        }
    };
}
