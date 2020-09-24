package com.example.facedb2;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.bumptech.glide.Glide;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.mikhaellopez.circularimageview.CircularImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class EmployeeRegistrationActivity extends AppCompatActivity {

    CircularImageView bigProfileImage;
    EditText dob,uniqueId;
    File registrationImage;
    Button submitBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_registration);

        //Hooks
        bigProfileImage = findViewById(R.id.img_profile_registration_page);
        dob = findViewById(R.id.dob_employee_registration_page);
        uniqueId = findViewById(R.id.uid_employee_registration_page);
        submitBtn = findViewById(R.id.btn_submit_employee_registration_page);

        dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int year = calendar.get(Calendar.YEAR)-20;
                DatePickerDialog datePickerDialog  = new DatePickerDialog(v.getContext(), android.R.style.Theme_Holo_Light, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(Calendar.YEAR,year);
                        calendar.set(Calendar.MONTH,month);
                        calendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);
                        dob.setText(new SimpleDateFormat("MM/dd/yyyy").format(calendar.getTime()));
                    }
                },year,month,day);
                datePickerDialog.getDatePicker().setMaxDate(new Date().getTime());
                datePickerDialog.show();
            }
        });

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String dobText = dob.getText().toString();
                String uniqueIdText = uniqueId.getText().toString().trim();

                if(dobText.isEmpty())
                    Toast.makeText(EmployeeRegistrationActivity.this, "Please Enter Date of Birth to Verify Registration", Toast.LENGTH_LONG).show();
                else if(uniqueIdText.isEmpty())
                    Toast.makeText(EmployeeRegistrationActivity.this, "Please Enter Unique ID given by HR", Toast.LENGTH_LONG).show();
                else
                {
                    AndroidNetworking.upload("http://facedb.fahm-technologies.com/api/FaceDb/IdentifyEmployee")
                            .addMultipartFile("file",registrationImage)
                            .addMultipartParameter("dob",dobText)
                            .addMultipartParameter("EmpUniqueId",uniqueIdText)
                            .setPriority(Priority.HIGH)
                            .build()
                            .getAsJSONObject(new JSONObjectRequestListener() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        if(response.getBoolean("status")) {
                                            Intent intent = new Intent(EmployeeRegistrationActivity.this, EmployeeRegistrationSucessFailure.class);
                                            intent.putExtra("status","RegistrationSuccess");
                                            intent.putExtra("textDisplayed","Successfully Registered !");
                                            startActivity(intent);
                                            Animatoo.animateSlideRight(EmployeeRegistrationActivity.this);
                                        }
                                        else {
                                            Toast.makeText(EmployeeRegistrationActivity.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }

                                @Override
                                public void onError(ANError anError) {
                                    Toast.makeText(EmployeeRegistrationActivity.this, anError.getMessage(), Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(EmployeeRegistrationActivity.this,EmployeeRegistrationSucessFailure.class);
                                    intent.putExtra("status","RegistrationFailure");
                                    intent.putExtra("textDisplayed","Details are not matching!");
                                    startActivity(intent);
                                    Animatoo.animateSlideRight(EmployeeRegistrationActivity.this);
                                }
                            });
                }
            }
        });


    }

    public void getImageEmployeeRegistration(View view) {
        ImagePicker.Companion.with(this)
                .cameraOnly()
                .compress(1024)
                .maxResultSize(800,800)
                .start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK)
        {
            registrationImage = ImagePicker.Companion.getFile(data);
            Glide.with(this)
                    .load(registrationImage)
                    .into(bigProfileImage);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Animatoo.animateSlideLeft(EmployeeRegistrationActivity.this);
    }
}