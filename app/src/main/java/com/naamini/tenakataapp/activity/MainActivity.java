package com.naamini.tenakataapp.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
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
        initToolbar();
        initComponents();
        handleOnClicks();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void initToolbar() {
      /*  Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle(getString(R.string.app_name));
*/
    }

    private void initComponents() {
        fabNewBtn = findViewById(R.id.fabNewBtn);
        recyclerView = findViewById(R.id.recyclerView);
        noContentLayout = findViewById(R.id.noContentLayout);

        students = new ArrayList<>();

        adapter = new StudentListAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        mStudentViewModel = ViewModelProviders.of(this).get(StudentViewModel.class);
        mStudentViewModel.getAllStudents().observe(this, new Observer<List<Student>>() {
            @Override
            public void onChanged(@Nullable final List<Student> students) {
                // Update the cached copy of the words in the adapter.
                if (adapter.getItemCount()==0){
                    noContentLayout.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                }else {
                    adapter.setStudents(students);
                    noContentLayout.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                }
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
        } else {
            Toast.makeText(
                    getApplicationContext(),
                    "Not saved",
                    Toast.LENGTH_LONG).show();
        }
    }

}