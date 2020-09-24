package com.example.facedb2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;

public class SecondActivity extends AppCompatActivity {
    Button loginBtn;
    TextView registerText;
    Context ctx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        //Hooks
        loginBtn = findViewById(R.id.btn_login_second_page);
        registerText = findViewById(R.id.register_second_page);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Start Login Activity when Login Btn Clicked
                startActivity(new Intent(SecondActivity.this,LoginActivity.class));
                //For Right Slide Transition
                Animatoo.animateSlideRight(SecondActivity.this);
            }
        });

        //Implement RegisterText according to Instructuions
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //Slide Left when back btn pressed
        Animatoo.animateSlideLeft(SecondActivity.this);
    }
}