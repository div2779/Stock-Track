package com.example.stocktrack;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class LoginEmail extends AppCompatActivity {
    private static final int RC_SIGN_IN = 6364;
    EditText emailField;
    EditText passwordField;
    Button login;
    Button createAcc;

    public boolean verifyEmail(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        return user != null && user.isEmailVerified();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_email);

        emailField = findViewById(R.id.emailField);
        passwordField = findViewById(R.id.passwordField);
        login = findViewById(R.id.btnSendOTP);
        createAcc = findViewById(R.id.btnCreateAcc);
        FirebaseAuth auth = FirebaseAuth.getInstance();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String loginEmail = emailField.getText().toString();
                String loginPassword = passwordField.getText().toString();
                if(loginEmail.isEmpty()){
                    emailField.setError("Email Address cannot be empty!");
                    return;
                }
                if(loginPassword.isEmpty()){
                    passwordField.setError("Password cannot be empty!");
                    return;
                }
                auth.signInWithEmailAndPassword(loginEmail, loginPassword)
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                if(verifyEmail()){
                                    Toast.makeText(LoginEmail.this, "Login Successful!", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(getApplicationContext(), HomePage.class));
                                    finish();
                                }
                                else{
                                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                    if(user!=null){
                                        user.sendEmailVerification();
                                    }
                                    Toast.makeText(LoginEmail.this, "Email Address is not Verified. Verification Link has been sent again.", Toast.LENGTH_SHORT).show();
                                    FirebaseAuth.getInstance().signOut();
                                }

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(LoginEmail.this, "Login Unsuccessful!" + e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });

        createAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), RegisterEmail.class));
                finish();
            }
        });

    }

    protected void onStart() {
        super.onStart();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null){
            if(verifyEmail()){
                startActivity(new Intent(getApplicationContext(), HomePage.class));
                finish();
            }
            else{
                FirebaseAuth.getInstance().signOut();
            }
        }
    }


}