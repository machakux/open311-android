package com.github.codetanzania.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

// @Table(name = "comment", id = BaseColumns._ID)
public class Comment implements Parcelable {

    public static final String SERVICE = "service";
    public static final String COMMENTER = "commenter";
    public static final String TIMESTAMP = "timestamp";
    public static final String CONTENT = "content";

    // @Column(name = "service", notNull = true)
    public Service service;

    // @Column(name = "commentor", notNull = true)
    public String  commentor;

    // @Column(name = "tsp", notNull = true)
    public Date    timestamp;

    // @Column(name = "content", notNull = true)
    public String  content;

    public Comment() {}

    protected Comment(Parcel in) {
        commentor = in.readString();
        content = in.readString();
    }

    public static final Creator<Comment> CREATOR = new Creator<Comment>() {
        @Override
        public Comment createFromParcel(Parcel in) {
            return new Comment(in);
        }

        @Override
        public Comment[] newArray(int size) {
            return new Comment[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(commentor);
        parcel.writeString(content);
    }

    @Override
    public String toString() {
        return "Comment{" +
                "service=" + service +
                ", commentor='" + commentor + '\'' +
                ", timestamp=" + timestamp +
                ", content='" + content + '\'' +
                '}';
    }
}
