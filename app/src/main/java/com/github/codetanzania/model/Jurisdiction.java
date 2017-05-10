package com.github.codetanzania.model;

import android.os.Parcel;
import android.os.Parcelable;


// @Table(name = "jurisdiction", id = BaseColumns._ID)
public class Jurisdiction implements Parcelable {

    public static final String CODE       = "code";
    public static final String NAME       = "name";
    public static final String DOMAIN     = "domain";
    public static final String ABOUT      = "about";
    public static final String LOCATION   = "location";
    public static final String BOUNDARIES = "boundaries";

    // @Column(name = "code", notNull = true, unique = true)
    public String code;

    // @Column(name = "name", notNull = true, unique = true, index = true)
    public String name;

    // @Column(name = "domain")
    public String domain;

    // @Column(name = "about")
    public String about;

    // @Column(name = "location")
    public LongLat location;

    // @Column(name = "boundaries")
    public String boundaries;

    public Jurisdiction() {}

    protected Jurisdiction(Parcel in) {
        code = in.readString();
        name = in.readString();
        domain = in.readString();
        about = in.readString();
        location = in.readParcelable(LongLat.class.getClassLoader());
        boundaries = in.readString();
    }

    public static final Creator<Jurisdiction> CREATOR = new Creator<Jurisdiction>() {
        @Override
        public Jurisdiction createFromParcel(Parcel in) {
            return new Jurisdiction(in);
        }

        @Override
        public Jurisdiction[] newArray(int size) {
            return new Jurisdiction[size];
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
        parcel.writeString(domain);
        parcel.writeString(about);
        parcel.writeParcelable(location, i);
        parcel.writeString(boundaries);
    }

    @Override
    public String toString() {
        return "Jurisdiction{" +
                "code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", domain='" + domain + '\'' +
                ", about='" + about + '\'' +
                ", location=" + location +
                ", boundaries='" + boundaries + '\'' +
                '}';
    }
}