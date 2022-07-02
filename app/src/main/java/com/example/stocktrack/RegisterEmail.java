package com.example.stocktrack;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterEmail extends AppCompatActivity {
    TextView verificationMsg;
    EditText emailFieldReg, passwordFieldReg, confPasswordFieldReg;
    Button signUp, signIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_email);

        verificationMsg = findViewById(R.id.emailVerificationMsg);
        emailFieldReg = findViewById(R.id.emailFieldReg);
        passwordFieldReg = findViewById(R.id.passwordFieldReg);
        confPasswordFieldReg = findViewById(R.id.confPasswordFieldReg);
        signUp = findViewById(R.id.btnSignUpReg);
        signIn = findViewById(R.id.btnSignInReg);
        FirebaseAuth authReg = FirebaseAuth.getInstance();

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailReg = emailFieldReg.getText().toString();
                String password = passwordFieldReg.getText().toString();
                String confPassword = confPasswordFieldReg.getText().toString();

                if(emailReg.isEmpty()){
                    emailFieldReg.setError("Email Address cannot be empty!");
                    return;
                }
                if(password.isEmpty()){
                    passwordFieldReg.setError("Password cannot be empty!");
                    return;
                }
                if(confPassword.isEmpty()){
                    confPasswordFieldReg.setError("Email Address cannot be empty!");
                    return;
                }
                if(!password.equals(confPassword)){
                    confPasswordFieldReg.setError(("Confirm Password must be same as Password"));
                    return;
                }

                authReg.createUserWithEmailAndPassword(emailReg, password)
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                emailFieldReg.setText("");
                                passwordFieldReg.setText("");
                                confPasswordFieldReg.setText("");
                                FirebaseUser user = authReg.getCurrentUser();
                                if(user!=null){
                                    user.sendEmailVerification();
                                }
                                verificationMsg.setVisibility(View.VISIBLE);
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    public void run() {
                                        verificationMsg.setVisibility(View.GONE);
                                        authReg.signOut();
                                        startActivity(new Intent(getApplicationContext(), LoginEmail.class));
                                        finish();
                                    }
                                }, 4000);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(RegisterEmail.this, "SignUp Unsuccessful!" + e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), LoginEmail.class));
                finish();
            }
        });

    }
}