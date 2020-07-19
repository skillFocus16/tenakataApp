package com.naamini.tenakataapp.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.naamini.tenakataapp.R;

import java.io.IOException;
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

    TextInputEditText etfName, etAge, etMStatus,etHeight,etLocation;
    ImageView pImgView;
    Button btnChooseImg,addStudentBtn;
    private int PICK_IMAGE_REQUEST=123;
    private Uri imgFilePath;
    String sFName,sAge, sMStatus,sHeight,sLocation;
    private Calendar myCalendar = Calendar.getInstance();
    private DatePickerDialog.OnDateSetListener bDateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_student);
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

    }

    private void handleOnClicks() {
         bDateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                /*disable future dates*/
//                    datePicker.setMaxDate(System.currentTimeMillis());
                /*user should be above 18*/
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
//                    int age = currentDate.getYear() - mYear;
                    int age = Calendar.YEAR - year;
                    /*date.setText( new StringBuilder().append("The user is ")
                            .append(age).append(" years old"));*/
                    SimpleDateFormat sdf = new SimpleDateFormat(myDateFormat, Locale.US);
                    etAge.setTextColor(getResources().getColor(R.color.design_default_color_on_secondary));
//                    etAge.setText(sdf.format(myCalendar.getTime()));
                    etAge.setText(sdf.format(age));
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
        });
        btnChooseImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
            }
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
                    replyIntent.putExtra(REPLY_IMG_PATH, imgFilePath);
                    replyIntent.putExtra(REPLY_NAME, sFName);
                    setResult(RESULT_OK, replyIntent);
                }else {
                    Toast.makeText(RegisterStudentActivity.this, R.string.required_field, Toast.LENGTH_LONG).show();
                }

            }
        });

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

        if (String.valueOf(imgFilePath).isEmpty()){
            Toast.makeText(RegisterStudentActivity.this, R.string.img_required_field, Toast.LENGTH_LONG).show();
            valid = false;
        }
        return valid;
    }

    /*for img pick*/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            imgFilePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imgFilePath);
                pImgView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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