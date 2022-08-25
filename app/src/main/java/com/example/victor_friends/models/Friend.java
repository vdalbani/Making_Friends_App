package com.example.victor_friends.models;

import android.os.Parcel;
import android.os.Parcelable;


/**
 * Victor_Friends created by vitto
 */

//Implement the parcelable
public class Friend implements Parcelable {

    private String friendName;
    private String friendEmail;
    private int friendNumber;
    private String friendAddress;

    public Friend() {
    }

    public Friend(String friendName, String friendEmail, int friendNumber, String friendAddress) {
        this.friendName = friendName;
        this.friendEmail = friendEmail;
        this.friendNumber = friendNumber;
        this.friendAddress = friendAddress;
    }

//    PARCEL
    protected Friend(Parcel in) {
        friendName = in.readString();
        friendEmail = in.readString();
        friendNumber = in.readInt();
        friendAddress = in.readString();
    }

    public static final Creator<Friend> CREATOR = new Creator<Friend>() {
        @Override
        public Friend createFromParcel(Parcel in) {
            return new Friend(in);
        }

        @Override
        public Friend[] newArray(int size) {
            return new Friend[size];
        }
    };

    public String getFriendName() {
        return friendName;
    }

    public void setFriendName(String friendName) {
        this.friendName = friendName;
    }

    public String getFriendEmail() {
        return friendEmail;
    }

    public void setFriendEmail(String friendEmail) {
        this.friendEmail = friendEmail;
    }

    public int getFriendNumber() {
        return friendNumber;
    }

    public void setFriendNumber(int friendNumber) {
        this.friendNumber = friendNumber;
    }

    public String getFriendAddress() {
        return friendAddress;
    }

    public void setFriendAddress(String friendAddress) {
        this.friendAddress = friendAddress;
    }

    @Override
    public String toString() {
        return "Friend{" +
                "friendName='" + friendName + '\'' +
                ", friendEmail='" + friendEmail + '\'' +
                ", friendNumber=" + friendNumber +
                ", friendAddress='" + friendAddress + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(friendName);
        dest.writeString(friendEmail);
        dest.writeInt(friendNumber);
        dest.writeString(friendAddress);
    }
}
