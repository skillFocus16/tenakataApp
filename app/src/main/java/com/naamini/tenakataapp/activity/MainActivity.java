package com.naamini.tenakataapp.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.naamini.tenakataapp.R;
import com.naamini.tenakataapp.adapter.StudentListAdapter;
import com.naamini.tenakataapp.model.Student;
import com.naamini.tenakataapp.model.StudentViewModel;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    FloatingActionButton fabNewBtn;
    RecyclerView recyclerView;
    TextView noContentLayout;

    private StudentViewModel mStudentViewModel;
    private int NEW_STUDENT_ACTIVITY_REQUEST_CODE=1;
    private StudentListAdapter adapter;
    ArrayList<Student> students;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initComponents();
        handleOnClicks();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void initComponents() {
        fabNewBtn = findViewById(R.id.fabNewBtn);
        recyclerView = findViewById(R.id.recyclerview);
        noContentLayout = findViewById(R.id.noContentLayout);

        students = new ArrayList<>();

        adapter = new StudentListAdapter(MainActivity.this, students);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

//        mStudentViewModel = ViewModelProviders.of(this).get(StudentViewModel.class);
        mStudentViewModel = new ViewModelProvider(MainActivity.this).get(StudentViewModel.class);
        mStudentViewModel.getAllStudents().observe(MainActivity.this, new Observer<List<Student>>() {
            @Override
            public void onChanged(@Nullable final List<Student> students) {
                // Update the cached copy of the words in the adapter.
                adapter.setStudents(students);
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void handleOnClicks() {
        fabNewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, RegisterStudentActivity.class);
                startActivityForResult(i, NEW_STUDENT_ACTIVITY_REQUEST_CODE);
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == NEW_STUDENT_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            Student s = new Student(data.getStringExtra(RegisterStudentActivity.REPLY_NAME),
                    data.getStringExtra(RegisterStudentActivity.REPLY_AGE),
                    data.getStringExtra(RegisterStudentActivity.REPLY_STATUS),
                    data.getStringExtra(RegisterStudentActivity.REPLY_HEIGHT),
                    data.getStringExtra(RegisterStudentActivity.REPLY_LOCATION),
                    data.getStringExtra(RegisterStudentActivity.REPLY_IMG_PATH),
                    data.getStringExtra(RegisterStudentActivity.REPLY_NAME));
            mStudentViewModel.insert(s);
            Toast.makeText(MainActivity.this, "SAVED!", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(
                    getApplicationContext(),
                    "Not saved",
                    Toast.LENGTH_LONG).show();
        }
    }

}