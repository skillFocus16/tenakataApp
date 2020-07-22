package com.naamini.tenakataapp.activity;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
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
    private String key, imgPath;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Bitmap bigBitmap;
    private ImageView img;

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

        pref = getApplicationContext().getSharedPreferences("TenakataPref", 0); // 0 - for private mode
        editor = pref.edit();

        fabNewBtn = findViewById(R.id.fabNewBtn);
        viewPdfBtn = findViewById(R.id.viewPdfBtn);
        recyclerView = findViewById(R.id.recyclerview);
        noContentLayout = findViewById(R.id.noContentLayout);
        img = findViewById(R.id.img);

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
//            if (adapter.getItemCount()==0){
//            if (students.size()==0){
//                noContentLayout.setVisibility(View.VISIBLE);
//                viewPdfBtn.setVisibility(View.GONE);
//            }else {
//                noContentLayout.setVisibility(View.GONE);
            viewPdfBtn.setVisibility(View.VISIBLE);
            adapter.setStudents(students);
            adapter.notifyDataSetChanged();

//            }
        });
    }

    private void handleOnClicks() {
        fabNewBtn.setOnClickListener(view -> {
            Intent i = new Intent(MainActivity.this, RegisterStudentActivity.class);
            startActivityForResult(i, NEW_STUDENT_ACTIVITY_REQUEST_CODE);
        });
        viewPdfBtn.setOnClickListener(view -> {
//            Intent i = new Intent(MainActivity.this, PDFActivity.class);
//            createContent();
            PdfDocument document = new PdfDocument();
            View content = new View(this);
            getScreenshotFromRecyclerView(recyclerView);
            //Convert to byte array
            try {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bigBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();
               /* Image myImg = Image.getInstance(stream.toByteArray());
                myImg.setAlignment(Image.MIDDLE);*/

                Glide.with(this)
                        .load(bigBitmap)
                        .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL))
                        .apply(RequestOptions.overrideOf(500, 500))
                        .into(img);

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
                String pdfName = "newTenakata"
                        + sdf.format(Calendar.getInstance().getTime()) + ".pdf";

                // all created files will be saved at path /sdcard/PDFDemo_AndroidSRC/
                File outputFile = new File(Environment.getExternalStorageDirectory().getPath(), pdfName);

                try {
                    outputFile.createNewFile();
                    OutputStream out = new FileOutputStream(outputFile);
                    document.writeTo(out);
                    document.close();
                    out.close();
                    Toast.makeText(this, "PDF gerado com sucesso", Toast.LENGTH_SHORT).show();

                    /*File pdfFile = new File(filePath + "/" + pdfFileName + pdfExtension);
                    newUriPath = Uri.fromFile(pdfFile);

*/                    Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
                    pdfIntent.setDataAndType(Uri.fromFile(outputFile), "application/pdf");
                    pdfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    try {
                        startActivity(Intent.createChooser(pdfIntent, "OPen pdf with:"));
                    } catch (ActivityNotFoundException e) {
                        Toast.makeText(MainActivity.this, "Can't read pdf file", Toast.LENGTH_SHORT).show();
                    }

                    Log.i("Gerou", "pdf");
                } catch (IOException e) {
                    e.printStackTrace();
                }


             /*     Intent i = new Intent(MainActivity.this, PDFActivity.class);
                i.putExtra("students", byteArray);
                startActivity(i);*/
              /*
                Document document = new Document();
                PdfWriter.getInstance(document, new FileOutputStream("Tenakata PDF Report"));
                document.open();
                Image img = Image.getInstance(String.valueOf(bigBitmap));
                document.add(new Paragraph("Sample 1: This is simple image demo."));
                document.add(img);
                document.close();
                System.out.println("Done");*/

            } catch (Exception e) {
                e.printStackTrace();
            }


//            Intent i = new Intent(MainActivity.this, PDFActivity.class);
//                    .setAction(
//                    "Naamini YOnazi"
//                            students.toString()
//                    );//s.getsName());
           /* Bundle bundle = new Bundle();
            bundle.putBundle("students", bigBitmap);
            i.putExtras(bundle);*/
//            startActivity(i);
//            i.putExtra("students",my);
//            i.putExtra("filePath", );
//            startActivity(i);
        });

        /*adapter.setOnItemClickListener(new StudentListAdapter.OnItemClickListener() {
                                           @Override
                                           public void onItemClick(View view, Student obj, int pos) {
                                                           Log.e("dfgh?:", obj.issUploaded());

                                               uploadImageToFirebase(storageReference,Uri.parse(obj.getsProfileImg()), profilePath, key);

                                           }
                                       });*/
     /*   adapter.setOnItemClickListener((adapterView, view, i, l) -> {
            Log.e("dfgh?:", adapter.getStudentAtPosition(i).issUploaded());

            if (isOnline()) {
//                uploadStudentToFirebase();
                uploadImageToFirebase(storageReference, Uri.parse(students.get(i).getsProfileImg()), profilePath, key);
            } else {
                Toast.makeText(MainActivity.this, R.string.no_internet, Toast.LENGTH_SHORT).show();
            }
//            Log.e("dfgh?:", students.get(i).issUploaded());

                Intent intent = new Intent(MainActivity.this, PDFActivity.class).setAction(students.get(i).getsName());;
//            i.putStringArrayListExtra("students", students);
//            i.putExtra("filePath", );
            startActivity(intent);

        });*/
    }



/*

    private void createContent(Bitmap bitmaps) {
        PdfDocument document = new PdfDocument();
        PdfDocument.Page page = null;
        // crate a page description
        for (int i = 0; i < bitmaps.size(); i++) {
            Bitmap bitmap = bitmaps.get(i);
            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(1400, 1979, i).create();

            // start a page
            page = document.startPage(pageInfo);
            if (page == null) {
                return;
            }
            Canvas canvas = page.getCanvas();
            canvas.drawBitmap(bitmap, 0, 0, null);
            document.finishPage(page);
        }
    }
*/

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
//                holder.itemView.setDrawingCacheEnabled(false);
//                holder.itemView.destroyDrawingCache();
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


           /* editor.putString("recyclerViewData", String.valueOf(bigBitmap));
            editor.apply();
            editor.commit();*/

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