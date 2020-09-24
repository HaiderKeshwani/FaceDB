package com.example.facedb2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.blogspot.atifsoftwares.animatoolib.Animatoo;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    EditText email,password;
    Button loginBtn;
    TextView needHelp,registerText;
    Context ctx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Hooks
        email = findViewById(R.id.email_login_page);
        password = findViewById(R.id.password_login_page);
        loginBtn = findViewById(R.id.btn_login_login_page);
        needHelp = findViewById(R.id.help_login_page);
        registerText = findViewById(R.id.register_login_page);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailText = email.getText().toString().trim();
                String passwordText = password.getText().toString();
                if(emailText.isEmpty())
                    Toast.makeText(LoginActivity.this, "Please Enter Email to Login", Toast.LENGTH_SHORT).show();
                else if(passwordText.isEmpty())
                    Toast.makeText(LoginActivity.this, "Please Enter Password to Login", Toast.LENGTH_SHORT).show();
                else if(!Patterns.EMAIL_ADDRESS.matcher(emailText).matches())
                    Toast.makeText(LoginActivity.this, "Please Enter a Valid Email Address", Toast.LENGTH_SHORT).show();
                else if(passwordText.length()< 8)
                    Toast.makeText(LoginActivity.this, "Please Enter A Correct Password", Toast.LENGTH_SHORT).show();
                else
                {
                    JSONObject object = new JSONObject();
                    try {
                        object.put("Username",emailText);
                        object.put("Password",passwordText);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    AndroidNetworking.post("http://facedb.fahm-technologies.com/api/Facedb/Login")
                            .addJSONObjectBody(object)
                            .setPriority(Priority.IMMEDIATE)
                            .build()
                            .getAsJSONObject(new JSONObjectRequestListener() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        if(response.getBoolean("status"))
                                        {
                                            Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
                                            intent.putExtra("companyId",response.getJSONArray("data")
                                                    .getJSONObject(0).getInt("companyid"));
                                            intent.putExtra("locationId",response.getJSONArray("data")
                                                    .getJSONObject(0).getInt("locationid"));
                                            startActivity(intent);
                                            Animatoo.animateSlideRight(LoginActivity.this);
                                        }
                                        else
                                        {
                                            Toast.makeText(LoginActivity.this, "Invalid Email/Password", Toast.LENGTH_SHORT).show();
                                        }

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onError(ANError anError) {
                                    Toast.makeText(LoginActivity.this, anError.getMessage(), Toast.LENGTH_SHORT).show();
                                    Log.i("Error",anError.getErrorBody());
                                }
                            });
                }
            }
        });


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Animatoo.animateSlideLeft(LoginActivity.this);
    }
}