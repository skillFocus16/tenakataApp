package com.naamini.tenakataapp.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Created by Naamini Yonazi on 19/07/20
 */
@Entity(tableName = "student_table")
public class Student {

    public int sID;
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "student")
    public String sName;
    public String sAge;
    public String sMaritalStatus;
    public String sHeight;
    public String sLocation;
    public String sProfileImg;
    public String sIqTest;

    public Student(@NonNull String sName, String sAge, String sMaritalStatus, String sHeight,
                   String sLocation, String sProfileImg, String sIqTest) {
        this.sName = sName;
        this.sAge = sAge;
        this.sMaritalStatus = sMaritalStatus;
        this.sHeight = sHeight;
        this.sLocation = sLocation;
        this.sProfileImg = sProfileImg;
        this.sIqTest = sIqTest;
    }

    public int getsID() {
        return sID;
    }

    public String getsName() {
        return sName;
    }

    public String getsAge() {
        return sAge;
    }

    public String getsMaritalStatus() {
        return sMaritalStatus;
    }

    public String getsHeight() {
        return sHeight;
    }

    public String getsLocation() {
        return sLocation;
    }

    public String getsProfileImg() {
        return sProfileImg;
    }

    public String getsIqTest() {
        return sIqTest;
    }
}
