package com.naamini.tenakataapp.repository;

import android.app.Application;
import android.os.AsyncTask;

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
//        new insertAsyncTask(mStudentDao).execute(student);
        StudentDatabaseConfig.databaseWriteExecutor.execute(()->{
            mStudentDao.insert(student);
        });
    }


    private static class insertAsyncTask extends AsyncTask<Student, Void, Void> {

        private StudentDao mAsyncTaskDao;

        insertAsyncTask(StudentDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Student... params) {
            mAsyncTaskDao.insert(params[0]);
            return null;
        }
    }

}
