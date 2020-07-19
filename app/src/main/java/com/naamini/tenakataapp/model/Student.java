package com.naamini.tenakataapp.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

/**
 * Created by Naamini Yonazi on 19/07/20
 */
@Entity(tableName = "student_table")
public class Student {

    @PrimaryKey(autoGenerate = true)
    public int sID;

    @NonNull
    @ColumnInfo(name = "student")
    public String sName;
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
    @ColumnInfo(name = "dateCreated")
    public String sCreatedOn;

    @Ignore
    public Student() {
    }

    public Student(@NonNull String sName,  String sAge,  String sMaritalStatus,
                    String sHeight,  String sLocation, String sProfileImg,
                    String sIqTest,  String sCreatedOn) {
        this.sName = sName;
        this.sAge = sAge;
        this.sMaritalStatus = sMaritalStatus;
        this.sHeight = sHeight;
        this.sLocation = sLocation;
        this.sProfileImg = sProfileImg;
        this.sIqTest = sIqTest;
        this.sCreatedOn = sCreatedOn;
    }

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

    public String getsCreatedOn() {
        return sCreatedOn;
    }

    public void setsCreatedOn(String sCreatedOn) {
        this.sCreatedOn = sCreatedOn;
    }
}
