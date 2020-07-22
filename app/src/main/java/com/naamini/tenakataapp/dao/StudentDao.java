package com.naamini.tenakataapp.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.naamini.tenakataapp.model.Student;

import java.util.List;

/**
 * Created by Naamini Yonazi on 19/07/20
 */
@Dao
public interface StudentDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Student student);

    @Query("UPDATE student_table SET sUploaded=:isUploaded WHERE student=:sName ")
    void update(String isUploaded, String sName);

    @Query("SELECT * from student_table ORDER BY admissibility DESC")//ORDER BY student ASC")
    LiveData<List<Student>> getAllStudents();

    @Query("SELECT * from student_table WHERE iq > :iq")
    LiveData<List<Student>> getHighIQ(int iq);

    @Query("SELECT * from student_table WHERE location LIKE :location")
    LiveData<List<Student>> checkLocation(String location);

    @Query("SELECT * from student_table WHERE age > :age")
    LiveData<List<Student>> getAge(int age);

    @Query("SELECT * from student_table WHERE iq > :iq AND location LIKE :location")//iq>100, location==KE,
    LiveData<List<Student>> getAdmissibleStudents(int iq, String location);
}
