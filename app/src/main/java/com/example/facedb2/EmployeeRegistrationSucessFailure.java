package com.example.facedb2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;

public class EmployeeRegistrationSucessFailure extends AppCompatActivity {

    TextView textView1,textView2;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_registration_sucess_failure);

        textView1 = findViewById(R.id.textView_success_failure);
        textView2 = findViewById(R.id.textView2_success_failure);
        imageView = findViewById(R.id.image_success_failure_page);

        if(getIntent().getExtras().getString("status").equals("AttendanceSuccess"))
        {
            textView1.setText(getIntent().getExtras().getString("textDisplayed"));
            textView2.setVisibility(View.GONE);
            imageView.setImageResource(R.drawable.thumbs_up);
            finishAffinity();
        }
        else if(getIntent().getExtras().getString("status").equals("AttendanceFailure"))
        {
            textView1.setText(getIntent().getExtras().getString("textDisplayed"));
            textView1.setTextColor(Color.RED);
            textView2.setVisibility(View.GONE);
            imageView.setImageResource(R.drawable.thumbs_down);
        }
        else if(getIntent().getExtras().getString("status").equals("RegistrationSuccess"))
        {
            textView1.setText(getIntent().getExtras().getString("textDisplayed"));
            textView2.setVisibility(View.VISIBLE);
            imageView.setImageResource(R.drawable.thumbs_up);

            SharedPreferences sp = getSharedPreferences("facedb.EMPLOYEE_REGISTRATION_STATUS",MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("STATUS","SUCCESS");
            editor.apply();

            finishAffinity();
        }
        else
        {
            textView1.setText(getIntent().getExtras().getString("textDisplayed"));
            textView1.setTextColor(Color.RED);
            textView2.setVisibility(View.VISIBLE);
            imageView.setImageResource(R.drawable.thumbs_down);
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Animatoo.animateSlideLeft(EmployeeRegistrationSucessFailure.this);
    }
}