package com.naamini.tenakataapp.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.naamini.tenakataapp.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;

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
    public static  Uri TEMP_URI;

    private static final int REQUEST_LOCATION = 101;
    private static final int permsRequestCode = 200;
    public static int REQUEST_PERMISSION = 103;
    private int REQUEST_CAMERA = 0;

    TextInputEditText etfName, etAge, etMStatus, etHeight, etIQ, etLocation;
    ImageView pImgView;
    ImageButton getMap;
    RadioGroup radioGroup;
    RadioButton radioFemale, radioMale;
    Button btnChooseImg, addStudentBtn;
    String sFName, sAge, sMStatus, sHeight, sIQ, sLocation, sGender, sImgPath;
    String isAdmitted;
    double lat, lon;
    Intent intent;
    private LocationManager locationManager;
    public Uri tempUri;
    private String _realPath;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    FirebaseStorage firebaseStorage;
    private String refStudents="students";
    MainActivity mainActivity;

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

        getSupportActionBar().setTitle(R.string.student_reg);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    private void initComponents() {
        mainActivity=new MainActivity();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference(refStudents);
        databaseReference.keepSynced(true);

        etfName = findViewById(R.id.etfName);
        etAge = findViewById(R.id.etAge);
        etMStatus = findViewById(R.id.etMStatus);
        etHeight = findViewById(R.id.etHeight);
        etIQ = findViewById(R.id.etIQ);
        etLocation = findViewById(R.id.etLocation);

        getMap = findViewById(R.id.getMap);

        radioGroup = findViewById(R.id.radioGrp);
        radioFemale = findViewById(R.id.radioF);
        radioMale = findViewById(R.id.radioM);

        pImgView = findViewById(R.id.pImgView);

        btnChooseImg = findViewById(R.id.btnChooseImg);
        addStudentBtn = findViewById(R.id.addStudentBtn);

        intent = getIntent();
    }

    private void handleOnClicks() {
        getMap.setOnClickListener(view -> {
            if ((mainActivity.isOnline())) {
                locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    OnGPS();
                } else {
                    getLocation();
                }
            }else{
                Toast.makeText(RegisterStudentActivity.this, R.string.no_internet, Toast.LENGTH_SHORT).show();
            }
        });
        btnChooseImg.setOnClickListener(view -> {
            if (!checkPermission()) {
                requestPermission();
            } else {
                final CharSequence[] options = {getString(R.string.take_photo), getString(R.string.cancel)};
                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterStudentActivity.this);
                builder.setTitle(R.string.add_photo);
                builder.setItems(options, (dialog, item) -> {
                    if (options[item].equals(getString(R.string.take_photo))) {
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(intent, REQUEST_CAMERA);
                    } else if (options[item].equals(getString(R.string.cancel))) {
                        dialog.dismiss();
                    }
                });
                builder.show();
            }
        });
        addStudentBtn.setOnClickListener(view -> {
            if (validateFields()) {
                //write new student
                if (sLocation.equals(getString(R.string.kenya_code)) && Integer.parseInt(sIQ) >= 100) {
                    isAdmitted = "true";
                } else if (sMStatus.equalsIgnoreCase("Married")) {
                    isAdmitted = "false";
                } else {
                    isAdmitted = "false";
                }
                Intent replyIntent = new Intent();
                replyIntent.putExtra(REPLY_NAME, sFName);
                replyIntent.putExtra(REPLY_GENDER, sGender);
                replyIntent.putExtra(REPLY_AGE, sAge);
                replyIntent.putExtra(REPLY_STATUS, sMStatus);
                replyIntent.putExtra(REPLY_HEIGHT, sHeight);
                replyIntent.putExtra(REPLY_LOCATION, sLocation);
                replyIntent.putExtra(REPLY_IMG_PATH, _realPath);
                replyIntent.putExtra(REPLY_IQ, sIQ);
                replyIntent.putExtra(REPLY_ADMIT, isAdmitted);

                setResult(RESULT_OK, replyIntent);
                finish();
            } else {
                Toast.makeText(RegisterStudentActivity.this, R.string.required_field, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }
    }

    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");

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
        tempUri = getProfileImageUri(getApplicationContext(), thumbnail);
        TEMP_URI = getProfileImageUri(getApplicationContext(), thumbnail);
        File finalFile = new File(getRealProfilePathFromURI(tempUri));
        Glide.with(RegisterStudentActivity.this)
                .load(thumbnail)
                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL))
                .apply(RequestOptions.overrideOf(500, 500))
                .apply(RequestOptions.errorOf(R.color.colorPrimary))
                .into(pImgView);
        pImgView.setVisibility(View.VISIBLE);
    }

    public Uri getProfileImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public String getRealProfilePathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);

        _realPath = cursor.getString(idx);
        return cursor.getString(idx);
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

    private void requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, READ_EXTERNAL_STORAGE)) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.perm_needed)
                    .setMessage(R.string.perm_needed_txt)
                    .setPositiveButton((getString(R.string.ok)), (dialog, which) -> ActivityCompat.requestPermissions(RegisterStudentActivity.this, new String[]{READ_EXTERNAL_STORAGE}, REQUEST_PERMISSION))
                    .setNegativeButton(getString(R.string.cancel), (dialog, which) -> dialog.dismiss()).create().show();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{READ_EXTERNAL_STORAGE}, REQUEST_PERMISSION);
        }
    }


    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA);

        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{READ_EXTERNAL_STORAGE, CAMERA}, permsRequestCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case permsRequestCode:
                if (grantResults.length > 0) {

                    boolean locationAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean cameraAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (locationAccepted && cameraAccepted)
                        Toast.makeText(RegisterStudentActivity.this, R.string.perm_granted, Toast.LENGTH_LONG).show();
                    else {
                        Toast.makeText(RegisterStudentActivity.this, R.string.perm_denied, Toast.LENGTH_LONG).show();

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(READ_EXTERNAL_STORAGE)) {

                                showMessageOKCancel(getString(R.string.allow_perms),
                                        (dialog, which) -> {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermissions(new String[]{READ_EXTERNAL_STORAGE, CAMERA},
                                                        permsRequestCode);
                                            }
                                        });
                                return;
                            }
                        }

                    }
                }


                break;
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(RegisterStudentActivity.this)
                .setMessage(message)
                .setPositiveButton(R.string.ok, okListener)
                .setNegativeButton(R.string.cancel, null)
                .create()
                .show();
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

        if (String.valueOf(_realPath).isEmpty()) {
            Toast.makeText(RegisterStudentActivity.this, R.string.img_required_field, Toast.LENGTH_LONG).show();
            valid = false;
        }
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