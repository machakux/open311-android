package com.github.codetanzania.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.provider.BaseColumns;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

@Table(name = "reporter", id = BaseColumns._ID)
public class Reporter implements Parcelable {

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
