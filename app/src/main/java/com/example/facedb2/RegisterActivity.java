package com.example.facedb2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.common.RequestBuilder;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.bumptech.glide.Glide;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.mikhaellopez.circularimageview.CircularImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import pub.devrel.easypermissions.EasyPermissions;

public class RegisterActivity extends AppCompatActivity {

    EditText firstname,lastname,dateofbirth,employeeid,mobileno,email;
    Button submitbtn;
    ImageButton backBtn;
    CircularImageView profileimage;
    File image;
    String[] mimetype = {"image/png","image/jpg","image/jpeg"};
    String firstName,lastName,dateofBirth,employeeId,mobileNo,emailText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        //Hooks
        firstname = findViewById(R.id.firstname_register_page);
        lastname = findViewById(R.id.lastname_register_page);
        dateofbirth = findViewById(R.id.dob_register_page);
        email = findViewById(R.id.email_register_page);
        employeeid = findViewById(R.id.empid_register_page);
        mobileno = findViewById(R.id.mobno_register_page);
        submitbtn = findViewById(R.id.btn_submit_register_page);
        profileimage = findViewById(R.id.img_profile);
        backBtn = findViewById(R.id.btn_back_register);
        //Back Button
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        dateofbirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int year = calendar.get(Calendar.YEAR)-20;
                DatePickerDialog datePickerDialog = new DatePickerDialog(v.getContext(),android.R.style.Theme_Holo_Light_Dialog, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(Calendar.YEAR,year);
                        calendar.set(Calendar.MONTH,month);
                        calendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);
                        dateofbirth.setText(new SimpleDateFormat("MM/dd/yyyy").format(calendar.getTime()));
                    }
                }, year, month, day);
                datePickerDialog.getDatePicker().setMaxDate(new Date().getTime());
                datePickerDialog.show();
            }
        });

        submitbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    checkFields();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void checkFields() throws IOException, InterruptedException {
        firstName = firstname.getText().toString();
        lastName = lastname.getText().toString();
        dateofBirth = dateofbirth.getText().toString().trim();
        employeeId = employeeid.getText().toString().trim();
        mobileNo = mobileno.getText().toString().trim();
        emailText = email.getText().toString().trim();

        if(!isConnected())
            Toast.makeText(this, "Please Connect to the Internet First", Toast.LENGTH_SHORT).show();
        else if(firstName.isEmpty())
            Toast.makeText(this, "Enter First Name", Toast.LENGTH_SHORT).show();
        else if(lastName.isEmpty())
            Toast.makeText(this, "Enter Last Name", Toast.LENGTH_SHORT).show();
        else if(dateofBirth.isEmpty())
            Toast.makeText(this, "Enter Date of Birth", Toast.LENGTH_SHORT).show();
        else if(mobileNo.isEmpty())
            Toast.makeText(this, "Enter Mobile Number", Toast.LENGTH_SHORT).show();
        else if(profileimage.getDrawable()==null)
            Toast.makeText(this, "Please Place your Image", Toast.LENGTH_SHORT).show();
        else
        {
            final String imageFileName = firstName+lastName+employeeId+".jpg";
            final File imageFile = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES),imageFileName);
            image.renameTo(imageFile);
            Integer companyId,locationId;
            companyId = getIntent().getIntExtra("companyId",0);
            locationId = getIntent().getIntExtra("locationId",0);
            Log.d("hello1", "checkFields: " + companyId);
            Log.d("hello2","locationid" + locationId);


            //Android Networking *Calling the API and making a POST Request*
            AndroidNetworking.upload("http://facedb.fahm-technologies.com/api/Facedb/AddEmployee")
                    .addMultipartFile("image",imageFile)
                    .addMultipartParameter("empfirstname",firstName)
                    .addMultipartParameter("emplastname",lastName)
                    .addMultipartParameter("dob",dateofBirth)
                    .addMultipartParameter("empphone1",mobileNo)
                    .addMultipartParameter("Employeeid",employeeId)
                    .addMultipartParameter("empemail",emailText)
                    .addMultipartParameter("companyid", String.valueOf(companyId))
                    .addMultipartParameter("locationid", String.valueOf(locationId))
                    .setPriority(Priority.HIGH)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            String uid = null;
                            try {
                                if(response.getBoolean("status")) {
                                    uid = response.getJSONArray("data").getJSONObject(0).getString("EmpUniqueId");
                                    Log.d("uid",uid);
                                    Intent intent = new Intent(RegisterActivity.this, AdminActivity.class);
                                    intent.putExtra("name", firstName + " " + lastName);
                                    intent.putExtra("uid", uid);
                                    startActivity(intent);
                                    Animatoo.animateSlideRight(RegisterActivity.this);
                                }
                                else {
                                    Toast.makeText(RegisterActivity.this, "Please Try Again", Toast.LENGTH_SHORT).show();
                                    Log.i("Error:",response.getString("message"));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(ANError anError) {
                            Toast.makeText(RegisterActivity.this, anError.getErrorBody(), Toast.LENGTH_SHORT).show();
                        }
                    });

        }


    }

    public void placeImage(View view) {
        ImagePicker.Companion.with(this)
                .compress(1024)
                .maxResultSize(800,800)
                .galleryMimeTypes(mimetype)
                .start();
    }

    //For Loading image into ImageView and taking data from ImagePicker
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode== Activity.RESULT_OK)
        {
            image = ImagePicker.Companion.getFile(data);
            Glide.with(this)
                    .load(image)
                    .into(profileimage);
        }
    }
    //Check if internet is Connected
    public boolean isConnected() throws IOException, InterruptedException {
        final String command = "ping -c 1 google.com";
        return Runtime.getRuntime().exec(command).waitFor() == 0;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Animatoo.animateSlideLeft(RegisterActivity.this);
    }

}