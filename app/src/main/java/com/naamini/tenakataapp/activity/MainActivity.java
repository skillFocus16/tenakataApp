package com.naamini.tenakataapp.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.naamini.tenakataapp.R;
import com.naamini.tenakataapp.adapter.StudentListAdapter;
import com.naamini.tenakataapp.model.Student;
import com.naamini.tenakataapp.model.StudentViewModel;

import java.util.ArrayList;
import java.util.UUID;

import static com.naamini.tenakataapp.activity.RegisterStudentActivity.REPLY_ADMIT;
import static com.naamini.tenakataapp.activity.RegisterStudentActivity.REPLY_AGE;
import static com.naamini.tenakataapp.activity.RegisterStudentActivity.REPLY_GENDER;
import static com.naamini.tenakataapp.activity.RegisterStudentActivity.REPLY_HEIGHT;
import static com.naamini.tenakataapp.activity.RegisterStudentActivity.REPLY_IMG_PATH;
import static com.naamini.tenakataapp.activity.RegisterStudentActivity.REPLY_IQ;
import static com.naamini.tenakataapp.activity.RegisterStudentActivity.REPLY_LOCATION;
import static com.naamini.tenakataapp.activity.RegisterStudentActivity.REPLY_NAME;
import static com.naamini.tenakataapp.activity.RegisterStudentActivity.REPLY_STATUS;
/**
 * Created by Naamini Yonazi on 19/07/20
 */
public class MainActivity extends AppCompatActivity {

    FloatingActionButton fabNewBtn;
    RecyclerView recyclerView;
    public TextView noContentLayout;

    private StudentViewModel mStudentViewModel;
    private int NEW_STUDENT_ACTIVITY_REQUEST_CODE=1;
    private StudentListAdapter adapter;
    ArrayList<Student> students;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    private String refStudents="students";
    public String profilePath = "images/profile/";
    private Student s;
    private String key, imgPath;

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

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference(refStudents);
        databaseReference.keepSynced(true);

        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        adapter = new StudentListAdapter(MainActivity.this, students);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        mStudentViewModel = new ViewModelProvider(MainActivity.this).get(StudentViewModel.class);
        mStudentViewModel.getAllStudents().observe(MainActivity.this, students -> {
                adapter.setStudents(students);
                adapter.notifyDataSetChanged();
        });
    }

    private void handleOnClicks() {
        fabNewBtn.setOnClickListener(view -> {
            Intent i = new Intent(MainActivity.this, RegisterStudentActivity.class);
            startActivityForResult(i, NEW_STUDENT_ACTIVITY_REQUEST_CODE);
           /* Intent i = new Intent(MainActivity.this, PDFActivity.class);
            i.putExtra("students", students);
//            i.putExtra("filePath", );
            startActivity(i);*/
        });
        adapter.setOnItemClickListener((adapterView, view, i, l) -> {
            if (isOnline()) {
                uploadStudentToFirebase();
//                uploadImageToFirebase(s,storageReference, TEMP_URI, profilePath, key);
            }else{
                Toast.makeText(MainActivity.this, R.string.no_internet, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == NEW_STUDENT_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            key = databaseReference.child(refStudents).push().getKey();
            s = new Student(data.getStringExtra(REPLY_NAME),
                    data.getStringExtra(REPLY_GENDER ),
                    data.getStringExtra(REPLY_AGE ),
                    data.getStringExtra(REPLY_STATUS),
                    data.getStringExtra(REPLY_HEIGHT),
                    data.getStringExtra(REPLY_LOCATION),
                    data.getStringExtra(REPLY_IMG_PATH),
                    data.getStringExtra(REPLY_IQ),
                    data.getStringExtra(REPLY_ADMIT)
            );
            mStudentViewModel.insert(s);
            Toast.makeText(MainActivity.this, R.string.reg_success, Toast.LENGTH_SHORT).show();

            if (isOnline()) {
                uploadStudentToFirebase();
//                uploadImageToFirebase(s, storageReference, Uri.parse(data.getStringExtra(REPLY_IMG_PATH)), profilePath, key);            //send to firebase
            }else{
                Toast.makeText(MainActivity.this, R.string.no_internet, Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(
                    getApplicationContext(),
                    R.string.cancelled,
                    Toast.LENGTH_LONG).show();
        }
    }

    public void uploadImageToFirebase(Student s,final StorageReference storageReference, Uri filePath, final String storagePath, final String key) {
        if (filePath != null) {
            final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            StorageReference ref = storageReference.child(storagePath + key + UUID.randomUUID().toString());
            ref.putFile(filePath)
                    .addOnSuccessListener(taskSnapshot -> {
                        progressDialog.dismiss();
                        Toast.makeText(MainActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                       uploadStudentToFirebase();
                    })
                    .addOnFailureListener(e -> {
                        progressDialog.dismiss();
                        Toast.makeText(MainActivity.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    })
                    .addOnProgressListener(taskSnapshot -> {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                .getTotalByteCount());
                        progressDialog.setMessage("Uploaded " + (int) progress + "%");
                    });
        }
    }

    private void uploadStudentToFirebase() {
        databaseReference.child(key).setValue(s)
                .addOnSuccessListener(aVoid -> Toast.makeText(MainActivity.this, "Firebase Success", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_LONG).show());
    }

    public boolean isOnline() {
        try {
            ConnectivityManager conMgr = (ConnectivityManager) getApplication().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = conMgr.getActiveNetworkInfo();

            if (netInfo == null || !netInfo.isConnected() || !netInfo.isAvailable()) {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }
}