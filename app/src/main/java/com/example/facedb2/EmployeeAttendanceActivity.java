package com.example.facedb2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.bumptech.glide.Glide;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.mikhaellopez.circularimageview.CircularImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class EmployeeAttendanceActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    CircularImageView employeeAttendanceImage;
    RadioGroup radioGroupAttendance;
    RadioButton radioButtonInOut;
    TextView currentAddress,lastAddress,lastClockingDate,lastClockingInTime,lastClockingOutTime;
    Button submitAttendanceEmployee;
    File attendanceImage;
    private static final int REQUEST_LOCATION = 786;
    private FusedLocationProviderClient fusedLocationClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_attendance);

        //Hooks
        employeeAttendanceImage = findViewById(R.id.img_profile_attendance);
        radioGroupAttendance = findViewById(R.id.radio_group_employee_attendance);
        currentAddress = findViewById(R.id.current_address_employee);
        lastAddress = findViewById(R.id.last_address_attendance);
        lastClockingDate = findViewById(R.id.last_clocking_date);
        lastClockingInTime = findViewById(R.id.last_clocking_in_time);
        lastClockingOutTime = findViewById(R.id.last_clocking_out_time);
        submitAttendanceEmployee = findViewById(R.id.submit_employee_attendance_page);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        String[] locationPermissions = {Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION};
        if(EasyPermissions.hasPermissions(this,locationPermissions))
            getCurrentAddress();
        else
            EasyPermissions.requestPermissions(this,"You will need this permission to Access Location",
                    REQUEST_LOCATION,locationPermissions);

        //Get Last Clocking Date, Time, Address etc
        getLastAttendance();

        submitAttendanceEmployee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentAddress.getText().toString().isEmpty())
                    Toast.makeText(EmployeeAttendanceActivity.this, "Please Wait for the Address to Load", Toast.LENGTH_SHORT).show();
                else if(employeeAttendanceImage.getDrawable()==null)
                    Toast.makeText(EmployeeAttendanceActivity.this, "Please Place your Photo", Toast.LENGTH_SHORT).show();
                else
                {
                    int selectedId = radioGroupAttendance.getCheckedRadioButtonId();
                    radioButtonInOut = findViewById(selectedId);

                    String timeStamp = new SimpleDateFormat("yyMMdd_HHmmss").format(new Date());
                    String imageFileName = "Attendance_"+timeStamp+"_"+radioButtonInOut.getText().toString().trim();

                    final File attendanceFinalImage = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), imageFileName + ".jpg");
                    attendanceImage.renameTo(attendanceFinalImage);

                    SharedPreferences sp = getSharedPreferences("facedb.EMPLOYEE_REGISTRATION_STATUS",MODE_PRIVATE);
                    String dob = sp.getString("dob","0/0/0");
                    String empuniqueid = sp.getString("empuniqueid","0000");


                    AndroidNetworking.upload("http://facedb.fahm-technologies.com/api/FaceDb/IdentifyEmployee")
                            .addMultipartFile("file",attendanceFinalImage)
                            .addMultipartParameter("dob",dob)
                            .addMultipartParameter("EmpUniqueId",empuniqueid)
                            .setPriority(Priority.HIGH)
                            .build()
                            .getAsJSONObject(new JSONObjectRequestListener() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    Integer recognisedEmployee=null;
                                    String recognisedEmployeeId = null;
                                    try {
                                        recognisedEmployee = response.getJSONArray("data").getJSONObject(0).getInt("empuniqueid");
                                        recognisedEmployeeId = response.getJSONArray("data").getJSONObject(0).getString("employeeid");
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    Toast.makeText(EmployeeAttendanceActivity.this, "Employee Id: " +recognisedEmployeeId+"\nEmployee Unique ID: " + recognisedEmployee, Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent(EmployeeAttendanceActivity.this,EmployeeRegistrationSucessFailure.class);
                                    intent.putExtra("from","AttendanceSuccess");
                                    startActivity(intent);
                                    Animatoo.animateSlideRight(EmployeeAttendanceActivity.this);
                                    //Set Shared Preferences for Last Address
                                    setLastAttendance(radioButtonInOut.getText().toString(),currentAddress.getText().toString());
                                }

                                @Override
                                public void onError(ANError anError) {
                                    Toast.makeText(EmployeeAttendanceActivity.this, anError.getMessage(), Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(EmployeeAttendanceActivity.this,EmployeeRegistrationSucessFailure.class);
                                    intent.putExtra("textDisplayed","Your Details are not Matching");
                                    intent.putExtra("status","AttendanceFailure");
                                    startActivity(intent);
                                    Animatoo.animateSlideRight(EmployeeAttendanceActivity.this);
                                }
                            });
                }
            }
        });
    }

    private void setLastAttendance(String radioOption,String address) {
        SharedPreferences sharedPreferences = getSharedPreferences("facedb.LAST_ATTENDANCE",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("LAST_CLOCKING_DATE",new SimpleDateFormat("dd/MM/yyyy").format(new Date()));
        if(radioOption.equals("In"))
            editor.putString("LAST_CLOCKING_IN_TIME",new SimpleDateFormat("hh:mm aa").format(new Date()));
        else
            editor.putString("LAST_CLOCKING_OUT_TIME",new SimpleDateFormat("hh:mm aa").format(new Date()));
        editor.putString("LAST_ADDRESS",address);
        editor.apply();
    }

    private void getLastAttendance() {
        SharedPreferences sharedPreferences = getSharedPreferences("facedb.LAST_ATTENDANCE",MODE_PRIVATE);
        lastClockingDate.setText("Last Clocking Date: "+ sharedPreferences.getString("LAST_CLOCKING_DATE","-"));
        lastAddress.setText(sharedPreferences.getString("LAST_ADDRESS",""));
        lastClockingInTime.setText("Last Clocking In Time: " + sharedPreferences.getString("LAST_CLOCKING_IN_TIME","-"));
        lastClockingOutTime.setText("Last Clocking Out Time: " + sharedPreferences.getString("LAST_CLOCKING_OUT_TIME","-"));
    }

    //Get Location Address using Fused Location Provider
    @SuppressLint("MissingPermission")
    private void getCurrentAddress() {
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(EmployeeAttendanceActivity.this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if(location!=null) {
                            Geocoder geocoder = new Geocoder(EmployeeAttendanceActivity.this);
                            List<Address> address = null;
                            try {
                                address = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            currentAddress.setText(address.get(0).getAddressLine(0));
                            }
                        }
                });
    }
    //Image Picker Functions
    public void getImageEmployeeAttendance(View view) {
        ImagePicker.Companion.with(this)
                .cameraOnly()
                .compress(1024)
                .maxResultSize(800,800)
                .start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == Activity.RESULT_OK)
        {
            attendanceImage = ImagePicker.Companion.getFile(data);
            Glide.with(this)
                    .load(attendanceImage)
                    .into(employeeAttendanceImage);
        }
    }
    //All Easy Permissions Interface Functions
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        //Forward all the data to Easy Permissions
        EasyPermissions.onRequestPermissionsResult(requestCode,permissions,grantResults,this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        if(requestCode==REQUEST_LOCATION)
            getCurrentAddress();
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        if(EasyPermissions.somePermissionPermanentlyDenied(this,perms))
            new AppSettingsDialog.Builder(this).build().show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Animatoo.animateSlideLeft(EmployeeAttendanceActivity.this);
    }
}