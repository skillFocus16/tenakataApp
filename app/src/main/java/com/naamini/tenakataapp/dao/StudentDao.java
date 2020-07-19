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

    @Query("SELECT * from student_table ORDER BY student ASC")
    LiveData<List<Student>> getAllStudents();

}
