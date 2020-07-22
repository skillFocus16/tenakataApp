package com.naamini.tenakataapp.activity;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.util.LruCache;
import android.view.View;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.UUID;

import static com.naamini.tenakataapp.activity.RegisterStudentActivity.REPLY_ADMIT;
import static com.naamini.tenakataapp.activity.RegisterStudentActivity.REPLY_AGE;
import static com.naamini.tenakataapp.activity.RegisterStudentActivity.REPLY_GENDER;
import static com.naamini.tenakataapp.activity.RegisterStudentActivity.REPLY_HEIGHT;
import static com.naamini.tenakataapp.activity.RegisterStudentActivity.REPLY_IMG_PATH;
import static com.naamini.tenakataapp.activity.RegisterStudentActivity.REPLY_IQ;
import static com.naamini.tenakataapp.activity.RegisterStudentActivity.REPLY_IsUPloaded;
import static com.naamini.tenakataapp.activity.RegisterStudentActivity.REPLY_LOCATION;
import static com.naamini.tenakataapp.activity.RegisterStudentActivity.REPLY_NAME;
import static com.naamini.tenakataapp.activity.RegisterStudentActivity.REPLY_STATUS;

/**
 * Created by Naamini Yonazi on 19/07/20
 */
public class MainActivity extends AppCompatActivity {

    public TextView noContentLayout;
    public String profilePath = "images/profile/";
    FloatingActionButton fabNewBtn, viewPdfBtn;
    RecyclerView recyclerView;
    ArrayList<Student> students;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    private StudentViewModel mStudentViewModel;
    private int NEW_STUDENT_ACTIVITY_REQUEST_CODE = 1;
    private StudentListAdapter adapter;
    private String refStudents = "students";
    private Student s;
    private String key;
    private Bitmap bigBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initComponents();
        getStudents();
        handleOnClicks();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void initComponents() {
        fabNewBtn = findViewById(R.id.fabNewBtn);
        viewPdfBtn = findViewById(R.id.viewPdfBtn);
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
    }

    private void getStudents() {
        mStudentViewModel.getAllStudents().observe(MainActivity.this, students -> {
            adapter.setStudents(students);
            adapter.notifyDataSetChanged();
        });
    }

    private void handleOnClicks() {
        fabNewBtn.setOnClickListener(view -> {
            Intent i = new Intent(MainActivity.this, RegisterStudentActivity.class);
            startActivityForResult(i, NEW_STUDENT_ACTIVITY_REQUEST_CODE);
        });
        viewPdfBtn.setOnClickListener(view -> {
            if (adapter.getItemCount() != 0) {
                getScreenshotFromRecyclerView(recyclerView);
                loadAndOpenPdf();
            } else {
                Toast.makeText(MainActivity.this, getString(R.string.noStudentAvailable), Toast.LENGTH_SHORT).show();
            }
        });

        if (adapter.getItemCount() == 0) {
            if (adapter.getItemCount() != 0) {
                noContentLayout.setVisibility(View.VISIBLE);
                viewPdfBtn.setVisibility(View.GONE);
            } else {
                noContentLayout.setVisibility(View.GONE);
                viewPdfBtn.setVisibility(View.VISIBLE);
                adapter.setStudents(students);
                adapter.notifyDataSetChanged();
            }
        }

    }

    private void loadAndOpenPdf() {
        PdfDocument document = new PdfDocument();
        View content = new View(this);

        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bigBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);

            content = recyclerView;
            content.setBackgroundColor(Color.parseColor("#303030"));
            int height = content.getHeight() + bigBitmap.getHeight();

            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(1570,
                    height, 1).create();

            // create a new page from the PageInfo
            PdfDocument.Page page = document.startPage(pageInfo);

            // repaint the user's text into the page
            content.draw(page.getCanvas());

            // do final processing of the page
            document.finishPage(page);

            // saving pdf document to sdcard
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy - HH-mm-ss", Locale.getDefault());
            String pdfName = "Tenakata Report"
                    + sdf.format(Calendar.getInstance().getTime()) + ".pdf";

            // all created files will be saved at path /sdcard/PDFDemo_AndroidSRC/
            File outputFile = new File(Environment.getExternalStorageDirectory().getPath(), pdfName);

            try {
                outputFile.createNewFile();
                OutputStream out = new FileOutputStream(outputFile);
                document.writeTo(out);
                document.close();
                out.close();
                Toast.makeText(this, "PDF created successfully", Toast.LENGTH_SHORT).show();

                Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
                pdfIntent.setDataAndType(Uri.fromFile(outputFile), "application/pdf");
                pdfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                try {
                    startActivity(Intent.createChooser(pdfIntent, "OPen pdf with:"));
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(MainActivity.this, "Can't read pdf file", Toast.LENGTH_SHORT).show();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == NEW_STUDENT_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            key = databaseReference.child(refStudents).push().getKey();
            s = new Student(data.getStringExtra(REPLY_NAME),
                    data.getStringExtra(REPLY_GENDER),
                    data.getStringExtra(REPLY_AGE),
                    data.getStringExtra(REPLY_STATUS),
                    data.getStringExtra(REPLY_HEIGHT),
                    data.getStringExtra(REPLY_LOCATION),
                    data.getStringExtra(REPLY_IMG_PATH),
                    data.getStringExtra(REPLY_IQ),
                    data.getStringExtra(REPLY_ADMIT),
                    data.getStringExtra(REPLY_IsUPloaded)
            );
            mStudentViewModel.insert(s);
            Toast.makeText(MainActivity.this, R.string.reg_success, Toast.LENGTH_SHORT).show();
            getStudents();
            if (isOnline()) {
                if (s.issUploaded().equalsIgnoreCase("true")) {
                    Toast.makeText(MainActivity.this, "Already uploaded to Firebase", Toast.LENGTH_SHORT).show();
                } else {
                    uploadStudentToFirebase();
                }
            } else {
                Toast.makeText(MainActivity.this, R.string.no_internet, Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(
                    getApplicationContext(),
                    R.string.cancelled,
                    Toast.LENGTH_LONG).show();
        }
    }

    private void uploadImageToFirebase(final StorageReference storageReference, Uri filePath, final String storagePath, final String key) {
        if (filePath != null) {
            final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            StorageReference ref = storageReference.child(storagePath + key + "_" + UUID.randomUUID().toString());
            ref.putFile(filePath)
                    .addOnSuccessListener(taskSnapshot -> {
                        progressDialog.dismiss();
                        Toast.makeText(MainActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        progressDialog.dismiss();
                        Log.e("errorUP?:", e.toString());
                        Toast.makeText(MainActivity.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    })
                    .addOnProgressListener(taskSnapshot -> {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                .getTotalByteCount());
                        progressDialog.setMessage("Uploaded " + (int) progress + "%");
                    });
        }
    }

    public void uploadStudentToFirebase() {
        s.setsUploaded("true");//writing to firebase
        databaseReference.child(key).setValue(s)
                .addOnSuccessListener(aVoid -> {
                            mStudentViewModel.update("true", s.getsName());//updating room
                            Toast.makeText(MainActivity.this, "Firebase Success", Toast.LENGTH_SHORT).show();
                            uploadImageToFirebase(storageReference, Uri.fromFile(new File(s.getsProfileImg())), profilePath, key);
                        }
                )
                .addOnFailureListener(e -> Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_LONG).show());
    }

    public Bitmap getScreenshotFromRecyclerView(RecyclerView view) {
        RecyclerView.Adapter adapter = view.getAdapter();
        bigBitmap = null;
        if (adapter != null) {
            int size = adapter.getItemCount();
            int height = 0;
            Paint paint = new Paint();
            int iHeight = 0;
            final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

            // Use 1/8th of the available memory for this memory cache.
            final int cacheSize = maxMemory / 8;
            LruCache<String, Bitmap> bitmaCache = new LruCache<>(cacheSize);
            for (int i = 0; i < size; i++) {
                RecyclerView.ViewHolder holder = adapter.createViewHolder(view, adapter.getItemViewType(i));
                adapter.onBindViewHolder(holder, i);
                holder.itemView.measure(View.MeasureSpec.makeMeasureSpec(view.getWidth(), View.MeasureSpec.EXACTLY),
                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
                holder.itemView.layout(0, 0, holder.itemView.getMeasuredWidth(), holder.itemView.getMeasuredHeight());
                holder.itemView.setDrawingCacheEnabled(true);
                holder.itemView.buildDrawingCache();
                Bitmap drawingCache = holder.itemView.getDrawingCache();
                if (drawingCache != null) {

                    bitmaCache.put(String.valueOf(i), drawingCache);
                }
                height += holder.itemView.getMeasuredHeight();
            }

            bigBitmap = Bitmap.createBitmap(view.getMeasuredWidth(), height, Bitmap.Config.ARGB_8888);
            Canvas bigCanvas = new Canvas(bigBitmap);
            bigCanvas.drawColor(Color.WHITE);

            for (int i = 0; i < size; i++) {
                Bitmap bitmap = bitmaCache.get(String.valueOf(i));
                bigCanvas.drawBitmap(bitmap, 0f, iHeight, paint);
                iHeight += bitmap.getHeight();
                bitmap.recycle();
            }
        }
        return bigBitmap;
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