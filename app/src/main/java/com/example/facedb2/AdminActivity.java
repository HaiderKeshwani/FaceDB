package com.example.facedb2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;

public class AdminActivity extends AppCompatActivity {
    ImageButton copytext;
    TextView name,uid,addNewEmployee;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        //Hooks
        copytext = findViewById(R.id.btn_copy_text);
        name = findViewById(R.id.name_admin_page);
        uid = findViewById(R.id.uid_admin_page);
        addNewEmployee = findViewById(R.id.addNewEmployee_admin);
        //Setting Name and uid from response from api
        name.setText(getIntent().getExtras().getString("name"));
        uid.setText(getIntent().getExtras().getString("uid"));

        //Copy Text Button Implementation
        copytext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_TEXT,uid.getText().toString());
                intent.setType("text/plain");

                Intent shareIntent = Intent.createChooser(intent,null);
                startActivity(shareIntent);
            }
        });
        addNewEmployee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminActivity.this,RegisterActivity.class));
                Animatoo.animateSlideRight(AdminActivity.this);
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Animatoo.animateSlideLeft(AdminActivity.this);
    }
}