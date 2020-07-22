package com.naamini.tenakataapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

/**
 * Created by Naamini Yonazi on 19/07/20
 */
@Entity(tableName = "student_table")
public class Student implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    public int sID;

    @NonNull
    @ColumnInfo(name = "student")
    public String sName;
    @ColumnInfo(name = "gender")
    public String gender;
    @ColumnInfo(name = "age")
    public String sAge;
    @ColumnInfo(name = "marital_status")
    public String sMaritalStatus;
    @ColumnInfo(name = "height")
    public String sHeight;
    @ColumnInfo(name = "location")
    public String sLocation;
    @ColumnInfo(name = "image")
    public String sProfileImg;
    @ColumnInfo(name = "iq")
    public String sIqTest;
    @ColumnInfo(name = "admissibility")
    public String sAdmissibility;
    public String sUploaded;

    @Ignore
    public Student() {
    }

    public Student(@NonNull String sName, String gender, String sAge, String sMaritalStatus, String sHeight,
                   String sLocation, String sProfileImg, String sIqTest, String sAdmissibility, String sUploaded) {
        this.sName = sName;
        this.gender = gender;
        this.sAge = sAge;
        this.sMaritalStatus = sMaritalStatus;
        this.sHeight = sHeight;
        this.sLocation = sLocation;
        this.sProfileImg = sProfileImg;
        this.sIqTest = sIqTest;
        this.sAdmissibility = sAdmissibility;
        this.sUploaded= sUploaded;
    }

    protected Student(Parcel in) {
        sID = in.readInt();
        sName = in.readString();
        gender = in.readString();
        sAge = in.readString();
        sMaritalStatus = in.readString();
        sHeight = in.readString();
        sLocation = in.readString();
        sProfileImg = in.readString();
        sIqTest = in.readString();
        sAdmissibility = in.readString();
        sUploaded = in.readString();
    }

    public static final Creator<Student> CREATOR = new Creator<Student>() {
        @Override
        public Student createFromParcel(Parcel in) {
            return new Student(in);
        }

        @Override
        public Student[] newArray(int size) {
            return new Student[size];
        }
    };

    public int getsID() {
        return sID;
    }

    public void setsID(int sID) {
        this.sID = sID;
    }

    @NonNull
    public String getsName() {
        return sName;
    }

    public void setsName(@NonNull String sName) {
        this.sName = sName;
    }


    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getsAge() {
        return sAge;
    }

    public void setsAge( String sAge) {
        this.sAge = sAge;
    }

    public String getsMaritalStatus() {
        return sMaritalStatus;
    }

    public void setsMaritalStatus( String sMaritalStatus) {
        this.sMaritalStatus = sMaritalStatus;
    }

    public String getsHeight() {
        return sHeight;
    }

    public void setsHeight( String sHeight) {
        this.sHeight = sHeight;
    }

    public String getsLocation() {
        return sLocation;
    }

    public void setsLocation( String sLocation) {
        this.sLocation = sLocation;
    }

    public String getsProfileImg() {
        return sProfileImg;
    }

    public void setsProfileImg(String sProfileImg) {
        this.sProfileImg = sProfileImg;
    }


    public String getsIqTest() {
        return sIqTest;
    }

    public void setsIqTest( String sIqTest) {
        this.sIqTest = sIqTest;
    }

    public String getsAdmissibility() {
        return sAdmissibility;
    }

    public void setsAdmissibility(String sAdmissibility) {
        this.sAdmissibility = sAdmissibility;
    }

    public String issUploaded() {
        return sUploaded;
    }

    public void setsUploaded(String sUploaded) {
        this.sUploaded = sUploaded;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(sID);
        parcel.writeString(sName);
        parcel.writeString(gender);
        parcel.writeString(sAge);
        parcel.writeString(sMaritalStatus);
        parcel.writeString(sHeight);
        parcel.writeString(sLocation);
        parcel.writeString(sProfileImg);
        parcel.writeString(sIqTest);
        parcel.writeString(sAdmissibility);
        parcel.writeString(sUploaded);
    }
}
