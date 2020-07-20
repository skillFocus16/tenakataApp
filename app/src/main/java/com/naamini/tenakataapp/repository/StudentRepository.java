package com.naamini.tenakataapp.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.naamini.tenakataapp.StudentDatabaseConfig;
import com.naamini.tenakataapp.dao.StudentDao;
import com.naamini.tenakataapp.model.Student;

import java.util.List;

/**
 * Created by Naamini Yonazi on 19/07/20
 */
public class StudentRepository {

    private StudentDao mStudentDao;
    private LiveData<List<Student>> mAllStudents;

    public StudentRepository(Application application) {
        StudentDatabaseConfig db = StudentDatabaseConfig.getDatabase(application);
        mStudentDao = db.studentDao();
        mAllStudents = mStudentDao.getAllStudents();
    }

    public LiveData<List<Student>> getAllStudents(){
        return mAllStudents;
    }

    public void insert (Student student){
        StudentDatabaseConfig.databaseWriteExecutor.execute(()->{
            mStudentDao.insert(student);
        });
    }
}
