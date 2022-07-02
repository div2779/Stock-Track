package com.example.stocktrack;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class LoginRegisterPhone extends AppCompatActivity {
    Button sendOTP, verifyOTP, resendOTP;
    EditText countryCode, phoneNum, OTP;
    TextView awaitMsg, plusText;
    FirebaseAuth auth;
    String verificationIDReceived;
    PhoneAuthCredential credential;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;
    
    String completePhoneNum;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_register_phone);
        
        sendOTP = findViewById(R.id.btnSendOTP);
        resendOTP = findViewById(R.id.btnResendOTP);
        verifyOTP = findViewById(R.id.btnVerifyOTP);
        countryCode = findViewById(R.id.countryCodeField);
        phoneNum = findViewById(R.id.PhoneNumberField);
        OTP = findViewById(R.id.OTPField);
        plusText = findViewById(R.id.plusText);
        awaitMsg = findViewById(R.id.awaitToResendMsg);
        auth = FirebaseAuth.getInstance();
        
        countryCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                OTP.setText("");
                OTP.setVisibility(View.GONE);
                verifyOTP.setVisibility(View.GONE);
                resendOTP.setVisibility(View.GONE);
                awaitMsg.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        phoneNum.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                OTP.setText("");
                OTP.setVisibility(View.GONE);
                verifyOTP.setVisibility(View.GONE);
                resendOTP.setVisibility(View.GONE);
                awaitMsg.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        
        sendOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String countryCodeVal = countryCode.getText().toString();
                String phoneNumVal = phoneNum.getText().toString();
                if(countryCodeVal.isEmpty()){
                    countryCode.setError("Cannot be Empty");
                    return;
                }
                if(phoneNumVal.isEmpty()){
                    phoneNum.setError("Cannot be Empty");
                    return;
                }
                completePhoneNum = "+" + countryCodeVal + phoneNumVal;

                verifyAndLogin(completePhoneNum);
            }
        });

        resendOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OTP.setText("");
                resendOTP.setEnabled(false);
                verifyAndLogin(completePhoneNum);
            }
        });

        verifyOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String OTPVal;
                OTPVal = OTP.getText().toString();
                if(OTPVal.isEmpty()){
                    OTP.setError("Cannot be empty");
                    return;
                }

                credential = PhoneAuthProvider.getCredential(verificationIDReceived, OTPVal);
                authenticateAndLogin(credential);
            }
        });

        callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                authenticateAndLogin(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Toast.makeText(LoginRegisterPhone.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken){
                super.onCodeSent(s, forceResendingToken);
                verificationIDReceived = s;
                //             !!!Directly could have used credential obtained from onVerificationCompleted.
                //             BUT if auto OTP detection fails, and we have to check user entered OTP, use this method

                OTP.setVisibility(View.VISIBLE);
                verifyOTP.setVisibility(View.VISIBLE);
                resendOTP.setVisibility(View.VISIBLE);
                awaitMsg.setVisibility(View.VISIBLE);

            }

            @Override
            public void onCodeAutoRetrievalTimeOut(@NonNull String verificationId){
                super.onCodeAutoRetrievalTimeOut(verificationId);
                resendOTP.setEnabled(true);
            }
        };
        
    }

    protected void onStart(){
        super.onStart();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null){
            startActivity(new Intent(getApplicationContext(), HomePage.class));
        }
    }

    public void verifyAndLogin(String completePhoneNum){
        PhoneAuthOptions options=
                PhoneAuthOptions.newBuilder(auth)
                        .setPhoneNumber(completePhoneNum)
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(LoginRegisterPhone.this)
                        .setCallbacks(callbacks)
                        .build();

        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    public void authenticateAndLogin(PhoneAuthCredential credential){
        auth.signInWithCredential(credential)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        Toast.makeText(LoginRegisterPhone.this, "Login Successful", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), HomePage.class));
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(LoginRegisterPhone.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}