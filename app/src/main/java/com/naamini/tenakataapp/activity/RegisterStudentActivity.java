package com.naamini.tenakataapp.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.naamini.tenakataapp.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class RegisterStudentActivity extends AppCompatActivity {

    public static final String REPLY_NAME = "rName";
    public static final String REPLY_GENDER = "rGender";
    public static final String REPLY_AGE = "rAge";
    public static final String REPLY_STATUS = "rStatus";
    public static final String REPLY_HEIGHT = "rHeight";
    public static final String REPLY_LOCATION = "rLocation";
    public static final String REPLY_IMG_PATH = "rImage";
    public static final String REPLY_IQ = "rIq";
    public static final String REPLY_ADMIT = "rAdmit";

    private static final int REQUEST_LOCATION = 101;
    public static String myDateFormat = "yyyy-MM-dd";
    final private int PERMISSION_WRITE_EXTERNAL_CAMERA = 100;

    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;

    TextInputEditText etfName, etAge, etMStatus, etHeight, etIQ, etLocation;
    ImageView pImgView;
    ImageButton getMap;
    RadioGroup radioGroup;
    RadioButton radioFemale, radioMale;
    Button btnChooseImg, addStudentBtn;
    String sFName, sAge, sMStatus, sHeight, sIQ, sLocation, sGender,sImgPath;
    boolean isAdmitted;
    double lat, lon;
    Intent intent;
    private LocationManager locationManager;

    public static void setErrorMsg(String msg, EditText viewId) {
        int ecolor = Color.WHITE;
        String estring = msg;
        ForegroundColorSpan fgcspan = new ForegroundColorSpan(ecolor);
        SpannableStringBuilder sbuilder = new SpannableStringBuilder(estring);
        sbuilder.setSpan(fgcspan, 0, estring.length(), 0);
        viewId.setError(sbuilder);
    }

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
        etIQ = findViewById(R.id.etIQ);
        etLocation = findViewById(R.id.etLocation);

        getMap=findViewById(R.id.getMap);

        radioGroup = findViewById(R.id.radioGrp);
        radioFemale = findViewById(R.id.radioF);
        radioMale = findViewById(R.id.radioM);

        pImgView = findViewById(R.id.pImgView);

        btnChooseImg = findViewById(R.id.btnChooseImg);
        addStudentBtn = findViewById(R.id.addStudentBtn);

        intent = getIntent();

    }

    private void handleOnClicks() {
        getMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    OnGPS();
                } else {
                    getLocation();
                }
            }
        });
        btnChooseImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                if (!checkPermission()) {
                    requestPermission();
                } else {
                    final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterStudentActivity.this);
                    builder.setTitle("Add Photo:");
                    builder.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int item) {
                            if (options[item].equals("Take Photo")) {
                                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                startActivityForResult(intent, REQUEST_CAMERA);
                            } else if (options[item].equals("Choose from Gallery")) {
                                Intent intent = new Intent();
                                intent.setAction(Intent.ACTION_PICK);
                                intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                startActivityForResult(intent, 2);

                            } else if (options[item].equals("Cancel")) {
                                dialog.dismiss();
                            }
                        }
                    });
                    builder.show();
               /* Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
*/
                }
            }
        });
        addStudentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateFields()) {
                    //write new student
                    if (sLocation.equals("KE") && Integer.parseInt(sIQ)>=100){
                        isAdmitted=true;
                    }else if (sMStatus.equalsIgnoreCase("Married")){
                        isAdmitted=false;
                    }else {
                        isAdmitted=false;
                    }
                    Intent replyIntent = new Intent();
                    replyIntent.putExtra(REPLY_NAME, sFName);
                    replyIntent.putExtra(REPLY_GENDER, sGender);
                    replyIntent.putExtra(REPLY_AGE, sAge);
                    replyIntent.putExtra(REPLY_STATUS, sMStatus);
                    replyIntent.putExtra(REPLY_HEIGHT, sHeight);
                    replyIntent.putExtra(REPLY_LOCATION, sLocation);
                    replyIntent.putExtra(REPLY_IMG_PATH, sImgPath);
                    replyIntent.putExtra(REPLY_IQ, sIQ);
                    replyIntent.putExtra(REPLY_ADMIT, isAdmitted);

                    setResult(RESULT_OK, replyIntent);
                    finish();
                } else {
                    Toast.makeText(RegisterStudentActivity.this, R.string.required_field, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }
    }

    private void onSelectFromGalleryResult(Intent data) {

    }

    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");

        Log.e("File?: ", String.valueOf(destination));
        sImgPath = String.valueOf(destination);

        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        pImgView.setImageBitmap(thumbnail);
        pImgView.setVisibility(View.VISIBLE);
    }


    private void OnGPS() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.enable_gps).setCancelable(false).setPositiveButton(R.string.yes, (dialog, which) -> startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))).setNegativeButton(R.string.no, (dialog, which) -> dialog.cancel());
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(
                RegisterStudentActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                RegisterStudentActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        } else {
            Geocoder gcd = new Geocoder(RegisterStudentActivity.this);
            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            Criteria criteria = new Criteria();
            String bestProvider = locationManager.getBestProvider(criteria, false);
            Location location = locationManager.getLastKnownLocation(bestProvider);
            LocationListener loc_listener = new LocationListener() {

                public void onLocationChanged(Location l) {
                }

                public void onProviderEnabled(String p) {
                }

                public void onProviderDisabled(String p) {
                }

                public void onStatusChanged(String p, int status, Bundle extras) {
                }
            };
            locationManager.requestLocationUpdates(bestProvider, 0, 0, loc_listener);
            location = locationManager.getLastKnownLocation(bestProvider);
            try {
                lat = location.getLatitude();
                lon = location.getLongitude();
                List<Address> addresss = null;
                try {
                    addresss = gcd.getFromLocation(lat,lon,1);
//                    addresss = gcd.getFromLocation(-1.328664, 36.833734, 1);//KE codes

                    String code = addresss.get(0).getCountryCode();
                    etLocation.setText(code);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (NullPointerException e) {
                lat = -1.0;
                lon = -1.0;
            }
        }
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
        sIQ = etIQ.getText().toString();
        sLocation = etLocation.getText().toString();

        int selectedId = radioGroup.getCheckedRadioButtonId();
        if (selectedId == radioFemale.getId()) {
            sGender = radioFemale.getText().toString();
        } else {
            sGender = radioMale.getText().toString();
        }

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
        if (sIQ.isEmpty()) {
            setErrorMsg(getString(R.string.required_field), etIQ);
            valid = false;
        } else {
            etIQ.setError(null);
        }

        if (sLocation.isEmpty()) {
            setErrorMsg(getString(R.string.required_field), etLocation);
            valid = false;
        } else {
            etLocation.setError(null);
        }

        if ((radioGroup.getCheckedRadioButtonId() == -1)) {
            radioMale.setError(getString(R.string.choose_gender));
            radioFemale.setError(getString(R.string.choose_gender));
            valid = false;
        } else {
            radioMale.setError(null);
            radioFemale.setError(null);
        }

        if (String.valueOf(sImgPath).isEmpty()){
            Toast.makeText(RegisterStudentActivity.this, R.string.img_required_field, Toast.LENGTH_LONG).show();
            valid = false;
        }
        return valid;
    }

}