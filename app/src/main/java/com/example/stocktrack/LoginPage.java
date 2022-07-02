package com.example.stocktrack;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import javax.annotation.Nullable;

public class LoginPage extends AppCompatActivity {
    public static final int GOOGLE_SIGNIN_CODE = 1007;
    Button emailBtn, phoneBtn, googleBtn, createAccBtn;
//    private SignInClient client;
//    private BeginSignInRequest signInRequest;
    GoogleSignInClient googleClient;
    GoogleSignInOptions googleOptions;
    FirebaseAuth auth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        emailBtn = findViewById(R.id.btnSignInEmailRegMain);
        phoneBtn = findViewById(R.id.btnSignInPhoneRegMain);
        googleBtn = findViewById(R.id.btnSignInGoogleRegMain);
        createAccBtn = findViewById(R.id.btnSignInRegMain);

        auth = FirebaseAuth.getInstance();

        emailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), LoginEmail.class));
            }
        });
        phoneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), LoginRegisterPhone.class));
            }
        });
        createAccBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), RegisterPage.class));
            }
        });

        //Google Sign In
//        GoogleSignInClient signin = GoogleSignIn.getClient(this, )
//        client = Identity.getSignInClient(this);
//        signInRequest = BeginSignInRequest
//                .builder()
//                .setGoogleIdTokenRequestOptions(BeginSignInRequest.GoogleIdTokenRequestOptions
//                        .builder()
//                        .setSupported(true)
//                        .setServerClientId(getString(R.string.webClientID))
//                        .setFilterByAuthorizedAccounts(true)
//                        .build())
//                .setAutoSelectEnabled(false)
//                .build();
//
//        googleBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                Intent signIntent = client.getSignInIntent();
////                startActivityForResult(signIntent, GOOGLE_SIGNIN_CODE);
//                client.beginSignIn(signInRequest)
//                        .addOnSuccessListener(new OnSuccessListener<BeginSignInResult>() {
//                            @Override
//                            public void onSuccess(BeginSignInResult beginSignInResult) {
//                                try {
//                                    startIntentSenderForResult(beginSignInResult.getPendingIntent().getIntentSender(),
//                                            GOOGLE_SIGNIN_CODE, null, 0, 0, 0);
//                                } catch (IntentSender.SendIntentException e) {
//                                    Toast.makeText(LoginPage.this, e.getMessage(), Toast.LENGTH_SHORT).show();
//                                }
//                            }
//                        })
//                        .addOnFailureListener(new OnFailureListener() {
//                            @Override
//                            public void onFailure(@NonNull Exception e) {
//                                Toast.makeText(LoginPage.this, e.getMessage(), Toast.LENGTH_SHORT).show();
//                            }
//                        });
//            }
//        });


        googleOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN)
                .requestIdToken("860814994311-1jmepu1503igkpuo5d66i0ddc5434lsq.apps.googleusercontent.com")
                .requestEmail()
                .build();

        googleClient = GoogleSignIn.getClient(this, googleOptions);

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if(account!=null){
            Toast.makeText(this, "User Logged In already", Toast.LENGTH_SHORT).show();
        }

        googleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = googleClient.getSignInIntent();
                startActivityForResult(signInIntent, GOOGLE_SIGNIN_CODE);
            }
        });


    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
//        super.onActivityResult(requestCode, resultCode, data);
//        if(requestCode == GOOGLE_SIGNIN_CODE){
//            try{
//                SignInCredential credential = client.getSignInCredentialFromIntent(data);
//                String idToken = credential.getGoogleIdToken();
//                if (idToken !=  null) {
//                    // Got an ID token from Google. Use it to authenticate
//                    // with Firebase.
//                    AuthCredential firebaseCredential = GoogleAuthProvider.getCredential(idToken, null);
//                    auth.signInWithCredential(firebaseCredential)
//                            .addOnSuccessListener(LoginPage.this, new OnSuccessListener<AuthResult>() {
//                                @Override
//                                public void onSuccess(AuthResult authResult) {
//                                    Toast.makeText(LoginPage.this, "SignIn Successful", Toast.LENGTH_SHORT).show();
//                                    startActivity(new Intent(getApplicationContext(), HomePage.class));
//                                }
//                            })
//                            .addOnFailureListener(LoginPage.this, new OnFailureListener() {
//                                @Override
//                                public void onFailure(@NonNull Exception e) {
//                                    Toast.makeText(LoginPage.this, e.getMessage(), Toast.LENGTH_SHORT).show();
//                                    auth.signOut();
//                                }
//                            });
//                }
//            }
//            catch(ApiException e){
//                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        }
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GOOGLE_SIGNIN_CODE){
            Task<GoogleSignInAccount> signInTask = GoogleSignIn.getSignedInAccountFromIntent(data);
            try{
                GoogleSignInAccount signInAcct = signInTask.getResult(ApiException.class);
                Toast.makeText(this, "GoogleLoginSuccessful", Toast.LENGTH_SHORT).show();
            }
            catch(ApiException e){
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    protected void onStart(){
        super.onStart();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null){
            startActivity(new Intent(getApplicationContext(), HomePage.class));
        }
    }
}