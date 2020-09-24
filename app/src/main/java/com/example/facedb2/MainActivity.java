package com.example.facedb2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;


public class MainActivity extends AppCompatActivity {
    Button btn1,btn2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Hooks
        btn1 = findViewById(R.id.button_first);
        btn2 = findViewById(R.id.button2_first);

        //Shared Preferences to Navigate
        //If Employee Registration is Successful then Navigate to Attendance Directly

        SharedPreferences sp = getSharedPreferences("facedb.EMPLOYEE_REGISTRATION_STATUS",MODE_PRIVATE);
        if(sp.getString("STATUS","FAILURE").equals("SUCCESS"))
        {
            startActivity(new Intent(MainActivity.this,EmployeeAttendanceActivity.class));
            Animatoo.animateSlideRight(MainActivity.this);
        }

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SecondActivity.class);
                startActivity(intent);
                Animatoo.animateSlideRight(MainActivity.this);
            }
        });

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,EmployeeRegistrationActivity.class);
                startActivity(intent);
                Animatoo.animateSlideRight(MainActivity.this);
            }
        });

    }

}