package com.naamini.tenakataapp.model;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.naamini.tenakataapp.repository.StudentRepository;

import java.util.List;

/**
 * Created by Naamini Yonazi on 19/07/20
 */
public class StudentViewModel extends AndroidViewModel {
    private StudentRepository mRepository;
    private LiveData<List<Student>> mAllStudents;

    public StudentViewModel(Application application) {
        super(application);
        mRepository = new StudentRepository(application);
        mAllStudents = mRepository.getAllStudents();
    }

    public LiveData<List<Student>> getAllStudents(){
        return mAllStudents;
    }

    public void insert(Student student) {
        mRepository.insert(student);
    }

    public void update(String isUploaded, String sName) {
        mRepository.update(isUploaded,sName);
    }

}
