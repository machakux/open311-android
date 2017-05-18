package com.github.codetanzania.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.provider.BaseColumns;

public class Reporter implements Parcelable {

    public static final String NAME = "name";
    public static final String PHONE = "phone";
    public static final String EMAIL = "email";
    public static final String ACCOUNT = "account";

    // @Column(name = "name")
    public String name;

    // @Column(name = "phone", unique = true)
    public String phone;

    // @Column(name = "email", unique = true)
    public String email;

    // @Column(name = "account")
    public String account;

    public Reporter() {}

    protected Reporter(Parcel in) {
        name = in.readString();
        phone = in.readString();
        email = in.readString();
        account = in.readString();
    }

    public static final Creator<Reporter> CREATOR = new Creator<Reporter>() {
        @Override
        public Reporter createFromParcel(Parcel in) {
            return new Reporter(in);
        }

        @Override
        public Reporter[] newArray(int size) {
            return new Reporter[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(phone);
        parcel.writeString(email);
        parcel.writeString(account);
    }

    @Override
    public String toString() {
        return "Reporter{" +
                "name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", account='" + account + '\'' +
                '}';
    }
}
