package com.naamini.tenakataapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.textfield.TextInputEditText;
import com.naamini.tenakataapp.BuildConfig;
import com.naamini.tenakataapp.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class RegisterStudentActivity extends AppCompatActivity {

    public static final String REPLY_NAME = "rName";
    public static final String REPLY_AGE = "rAge";
    public static final String REPLY_STATUS = "rStatus";
    public static final String REPLY_HEIGHT = "rHeight";
    public static final String REPLY_LOCATION = "rLocation";
    public static final String REPLY_IMG_PATH = "rImage";
    public static String myDateFormat = "yyyy-MM-dd";

    final private int PERMISSION_WRITE_EXTERNAL_CAMERA= 100;

    TextInputEditText etfName, etAge, etMStatus,etHeight,etLocation;
    ImageView pImgView;
    Button btnChooseImg,addStudentBtn;
    String sFName,sAge, sMStatus,sHeight,sLocation;
    private Calendar myCalendar = Calendar.getInstance();
    private DatePickerDialog.OnDateSetListener bDateListener;
    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_student);
        initToolbar();
        initComponents();
        handleOnClicks();
        if (!checkPermission()) {
            requestPermission();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Student Registration");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    private void initComponents() {
        etfName = findViewById(R.id.etfName);
        etAge = findViewById(R.id.etAge);
        etMStatus = findViewById(R.id.etMStatus);
        etHeight = findViewById(R.id.etHeight);
        etLocation = findViewById(R.id.etLocation);

        pImgView = findViewById(R.id.pImgView);

        btnChooseImg = findViewById(R.id.btnChooseImg);
        addStudentBtn = findViewById(R.id.addStudentBtn);

        intent = getIntent();

    }

    private void handleOnClicks() {
        /* bDateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                *//*disable future dates*//*
//                    datePicker.setMaxDate(System.currentTimeMillis());
                *//*user should be above 18*//*
                Calendar userAge = new GregorianCalendar(year, monthOfYear, dayOfMonth);
                Calendar minAdultAge = new GregorianCalendar();
                minAdultAge.add(Calendar.YEAR, -18);

                if (minAdultAge.before(userAge)) {
                    etAge.setTextColor(Color.RED);
                    etAge.setText(getString(R.string.invalid_date));
                } else if (myCalendar.after(System.currentTimeMillis())) {
                    etAge.setTextColor(Color.RED);
                    etAge.setText(getString(R.string.invalid_date));
                } else {
                    Date currentDate = new Date();
                    Log.e("current date?: ", String.valueOf(currentDate.getYear()));
//                    int age = currentDate.getYear() - mYear;
                    int age = myCalendar.get(Calendar.YEAR) - currentDate.getYear();//
                    Log.e("current date?: ", String.valueOf(userAge.getWeekYear())+":"+age);
                    *//*date.setText( new StringBuilder().append("The user is ")
                            .append(age).append(" years old"));*//*
//                    SimpleDateFormat sdf = new SimpleDateFormat(myDateFormat, Locale.US);
                    etAge.setTextColor(getResources().getColor(R.color.design_default_color_on_secondary));
//                    etAge.setText(sdf.format(myCalendar.getTime()));
                    etAge.setText(String.valueOf(age));
                }
            }
        };

        etAge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(RegisterStudentActivity.this, bDateListener, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });*/
        btnChooseImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                final CharSequence[] options = { "Take Photo", "Choose from Gallery","Cancel" };
                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterStudentActivity.this);
                builder.setTitle("Add Photo!");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if (options[item].equals("Take Photo")) {
                        }
                        else if (options[item].equals("Choose from Gallery")) {
                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_PICK);
                            intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(intent, 2);

                        }
                        else if (options[item].equals("Cancel")) {
                            dialog.dismiss();
                        }
                    }
                });
                builder.show();
               /* Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
*/            }
        });
        addStudentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateFields()){
                    //write new student
                    Intent replyIntent = new Intent();
                    replyIntent.putExtra(REPLY_NAME, sFName);
                    replyIntent.putExtra(REPLY_NAME, sAge);
                    replyIntent.putExtra(REPLY_NAME, sMStatus);
                    replyIntent.putExtra(REPLY_NAME, sHeight);
                    replyIntent.putExtra(REPLY_NAME, sLocation);
                    replyIntent.putExtra(REPLY_IMG_PATH, "NEEEEEMMMMMMYYYYYYYYYYY");
                    replyIntent.putExtra(REPLY_NAME, sFName);
                    setResult(RESULT_OK, replyIntent);
                    finish();
                }else {
                    Toast.makeText(RegisterStudentActivity.this, R.string.required_field, Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    protected boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    protected void requestPermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
            //Toast.makeText(this, "Write External Storage permission allows us to do store images. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, PERMISSION_WRITE_EXTERNAL_CAMERA);
            }
        }
    }

     private boolean validateFields() {
        sFName = etfName.getText().toString();
        sAge = etAge.getText().toString();
        sMStatus = etMStatus.getText().toString();
        sHeight = etHeight.getText().toString();
        sLocation= etLocation.getText().toString();

        boolean valid = true;

        if (sFName.isEmpty()) {
            setErrorMsg(getString(R.string.required_field), etfName);
            valid = false;
        } else {
            etfName.setError(null);
        }

        if (sAge.isEmpty()) {
            setErrorMsg(getString(R.string.required_field), etAge);
            valid = false;
        } else {
            etAge.setError(null);
        }

        if (sMStatus.isEmpty()) {
            setErrorMsg(getString(R.string.required_field), etMStatus);
            valid = false;
        } else {
            etMStatus.setError(null);
        }

        if (sHeight.isEmpty()) {
            setErrorMsg(getString(R.string.required_field), etHeight);
            valid = false;
        } else {
            etHeight.setError(null);
        }

        if (sLocation.isEmpty() ) {
            setErrorMsg(getString(R.string.required_field), etLocation);
            valid = false;
        } else {
            etLocation.setError(null);
        }

/*
        if (String.valueOf(imgFilePath).isEmpty()){
            Toast.makeText(RegisterStudentActivity.this, R.string.img_required_field, Toast.LENGTH_LONG).show();
            valid = false;
        }
*/
        return valid;
    }

    public static void setErrorMsg(String msg, EditText viewId) {
        int ecolor = Color.WHITE;
        String estring = msg;
        ForegroundColorSpan fgcspan = new ForegroundColorSpan(ecolor);
        SpannableStringBuilder sbuilder = new SpannableStringBuilder(estring);
        sbuilder.setSpan(fgcspan, 0, estring.length(), 0);
        viewId.setError(sbuilder);
    }

}