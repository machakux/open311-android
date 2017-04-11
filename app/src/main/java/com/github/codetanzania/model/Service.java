package com.github.codetanzania.model;

import android.os.Parcel;
import android.os.Parcelable;

// @Table(name = "service", id = BaseColumns._ID)
public class Service implements Parcelable {

    public static final String CODE = "code";
    public static final String NAME = "name";
    public static final String DESCRIPTION = "description";
    public static final String COLOR = "color";

    // @Column(name = "code")
    public String code;

    // @Column(name = "name")
    public String name;

    // @Column(name = "description")
    public String description;

    // @Column(name = "color")
    public String color;

    public Service() {}

    protected Service(Parcel in) {
        code = in.readString();
        name = in.readString();
        description = in.readString();
    }

    public static final Creator<Service> CREATOR = new Creator<Service>() {
        @Override
        public Service createFromParcel(Parcel in) {
            return new Service(in);
        }

        @Override
        public Service[] newArray(int size) {
            return new Service[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(code);
        parcel.writeString(name);
        parcel.writeString(description);
    }

    @Override
    public String toString() {
        return "Service{" +
                "code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", color='" + color + '\'' +
                '}';
    }
}