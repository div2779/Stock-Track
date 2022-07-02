package com.example.stocktrack;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ProfileFragment extends Fragment {
    TextView logout;
    TextView emailView, emailLabel;
    TextView mobileView, mobileLabel;
    TextView username, updateUsernameBtn;
    TextView manageWatchlists;
    TextView createWatchlist;
    TextView changePassword;
    RecyclerView watchlistRecycler;
    FirebaseAuth auth;
    FirebaseUser user;
    String userUID;
    Map<String,Object> userData;
    ArrayList<String> watchlists;
    FirebaseFirestore db;

    public ProfileFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_profile, container, false);
        emailView = view.findViewById(R.id.EmailViewProfile);
        emailLabel = view.findViewById(R.id.EmailTextLabel);
        mobileView = view.findViewById(R.id.MobileNumberViewProfile);
        mobileLabel = view.findViewById(R.id.MobileLabel);
        username = view.findViewById(R.id.usernameViewProfile);
        changePassword = view.findViewById(R.id.changePasswordBtn);
        updateUsernameBtn = view.findViewById(R.id.updateUsernameBtn);
        manageWatchlists = view.findViewById(R.id.ManageWatchlistButton);
        createWatchlist = view.findViewById(R.id.createNewWatchlist);
        watchlistRecycler = view.findViewById(R.id.manageWatchlistRecycler);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        userUID = user.getUid();
        db = FirebaseFirestore.getInstance();
        userData = new HashMap<>();
        watchlists = new ArrayList<>();
        logout = view.findViewById(R.id.logoutButton);


        fillInUserDetail();
        getWatchlistsAndSetAdapter();

        manageWatchlists.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(createWatchlist.getVisibility()==View.GONE){
                    createWatchlist.setVisibility(View.VISIBLE);
                    watchlistRecycler.setVisibility(View.VISIBLE);
                }
                else{
                    createWatchlist.setVisibility(View.GONE);
                    watchlistRecycler.setVisibility(View.GONE);
                }
            }
        });

        updateUsernameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View dialogView = getLayoutInflater().inflate(R.layout.dialog_watchlist, null);
                EditText inputText = dialogView.findViewById(R.id.enterNameField);
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Username")
                        .setMessage("Please enter new username name:")
                        .setCancelable(true)
                        .setView(dialogView)
                        .setPositiveButton("Set", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String newName = inputText.getText().toString();
                                username.setText(newName);
                                UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(newName)
                                        .build();
                                user.updateProfile(profile);
                                dialog.cancel();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View dialogView = getLayoutInflater().inflate(R.layout.dialog_change_password, null);
                EditText password = dialogView.findViewById(R.id.enterNameField);
                EditText confPassword = dialogView.findViewById(R.id.enterConfirmPasswordField);
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Change Password")
                        .setMessage("Please enter new password:")
                        .setCancelable(true)
                        .setView(dialogView)
                        .setPositiveButton("Set", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String passwordVal = password.getText().toString();
                                String confPasswordVal = confPassword.getText().toString();
                                if(passwordVal.length()==0){
                                    password.setError("Cannot be empty");
                                }
                                if(confPasswordVal.length()==0){
                                    confPassword.setError("Cannot be empty");
                                }
                                if(!passwordVal.equals(confPasswordVal)){
                                    confPassword.setError("Passwords are not same!");
                                }
                                user.updatePassword(confPasswordVal);
                                Toast.makeText(getContext(), "Password Changed Successfully", Toast.LENGTH_SHORT).show();
                                dialog.cancel();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userLogout();
            }
        });




        return view;
    }

    private void fillInUserDetail(){
        if(!user.getDisplayName().isEmpty()){
            username.setText(user.getDisplayName().toString());
        }
        if(!user.getEmail().isEmpty()){
            emailLabel.setVisibility(View.VISIBLE);
            mobileLabel.setVisibility(View.GONE);
            emailView.setText(user.getEmail());
            mobileView.setVisibility(View.GONE);
            changePassword.setVisibility(View.VISIBLE);
        }
        if(!user.getPhoneNumber().isEmpty()){
            emailLabel.setVisibility(View.GONE);
            mobileLabel.setVisibility(View.VISIBLE);
            mobileView.setText(user.getPhoneNumber());
            emailView.setVisibility(View.GONE);
            changePassword.setVisibility(View.GONE);
        }
    }

    private void getWatchlistsAndSetAdapter(){
        db.collection("users").document(userUID)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()) {
                            userData = documentSnapshot.getData();
                        }
                        else{
                            Map<String, Object> firstEntry = new HashMap<>();
                            firstEntry.put("Watchlist", Arrays.asList());
                            userData.put("Watchlist", Arrays.asList());
                            db.collection("users").document(userUID).set(firstEntry);
                        }
                        for(Map.Entry<String,Object> entry: userData.entrySet()){
                            watchlists.add(entry.getKey());
                        }
                        setWatchlistProfileAdapter();
                    }
                });
    }

    private void setWatchlistProfileAdapter(){
        WatchlistProfileAdapter watchlistProfileAdapter = new WatchlistProfileAdapter(watchlists);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        watchlistRecycler.setLayoutManager(linearLayoutManager);
        watchlistProfileAdapter.setOnWatchlistCLickListener(new WatchlistProfileAdapter.onWatchlistClickListener() {
            @Override
            public void onEditClick(int position) {
                View dialogView = getLayoutInflater().inflate(R.layout.dialog_watchlist, null);
                EditText inputText = dialogView.findViewById(R.id.enterNameField);
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Edit Watchlist Name")
                        .setMessage("Please enter new watchlist name:")
                        .setCancelable(true)
                        .setView(dialogView)
                        .setPositiveButton("Set", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String newName = inputText.getText().toString();
                                String oldName = watchlists.get(position);
                                Object obj = userData.get(oldName);
                                userData.remove(oldName);
                                userData.put(newName, obj);
                                db.collection("users").document(userUID).set(userData);
                                watchlistProfileAdapter.changeName(position, newName);
                                watchlistProfileAdapter.notifyDataSetChanged();
                                dialog.cancel();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }

            @Override
            public void onDeleteClick(int position) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Confirm Watchlist Deletion")
                        .setMessage("You won't be able to retrieve this watchlist again.\n Delete watchlist?")
                        .setCancelable(true)
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String toDelete = watchlists.get(position);
                                userData.remove(toDelete);
                                db.collection("users").document(userUID).set(userData);
                                watchlistProfileAdapter.deleteName(position);
                                watchlistProfileAdapter.notifyDataSetChanged();
                                dialog.cancel();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

        createWatchlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View dialogView = getLayoutInflater().inflate(R.layout.dialog_watchlist, null);
                EditText inputText = dialogView.findViewById(R.id.enterNameField);
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("New Watchlist")
                        .setMessage("Please enter new watchlist name:")
                        .setCancelable(true)
                        .setView(dialogView)
                        .setPositiveButton("Create", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String newName = inputText.getText().toString();
                                userData.put(newName, Arrays.asList());
                                db.collection("users").document(userUID).set(userData);
                                watchlistProfileAdapter.addName(newName);
                                watchlistProfileAdapter.notifyDataSetChanged();
                                dialog.cancel();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

        watchlistRecycler.setAdapter(watchlistProfileAdapter);
    }

    private void userLogout(){
        if(user!=null){
            auth.signOut();
            startActivity(new Intent(getActivity().getApplicationContext(), LoginPage.class));
        }
    }
}