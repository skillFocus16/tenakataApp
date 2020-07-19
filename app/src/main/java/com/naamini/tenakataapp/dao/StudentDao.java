package com.naamini.tenakataapp.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.naamini.tenakataapp.model.Student;

import java.util.List;

/**
 * Created by Naamini Yonazi on 19/07/20
 */
@Dao
public interface StudentDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Student student);

    @Query("SELECT * from student_table ORDER BY dateCreated DESC")//ORDER BY student ASC")
    LiveData<List<Student>> getAllStudents();

    @Query("SELECT * from student_table WHERE iq > :iq")
    LiveData<List<Student>> getHighIQ(int iq);

    @Query("SELECT * from student_table WHERE location LIKE :location")
    LiveData<List<Student>> checkLocation(String location);

    @Query("SELECT * from student_table WHERE age > :age")
    LiveData<List<Student>> getAge(int age);

}
