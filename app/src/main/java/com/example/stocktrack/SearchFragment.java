package com.example.stocktrack;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchFragment extends Fragment {
    private RecyclerView searchResultsView;
    private ArrayList<CardData> cardDataArrayList;
    private EditText searchViewBlank;
    private ImageView searchIcon;
    private Spinner spinner;
    private FirebaseFirestore db;
    private Map<String, Object> userData;
    private String userUID;
    private String currSpinnerSelection;

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_search, container, false);

        searchResultsView = view.findViewById(R.id.searchRecycler);
        searchViewBlank = view.findViewById(R.id.searchBox);
        searchIcon = view.findViewById(R.id.searchIcon);
        spinner = view.findViewById(R.id.spinner);
        cardDataArrayList = new ArrayList<CardData>();
        userUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        db = FirebaseFirestore.getInstance();
        userData = new HashMap<>();

        getStockDataAndSetAdapter();
//        addDataToFirestore("Tata Consultancy Services Ltd.","TCS.NS","NSE");
//        addDataToFirestore("Hindalco Industries Ltd.","HINDALCO.NS","NSE");
//        addDataToFirestore("Coal India Ltd.","COALINDIA.NS","NSE");
//        addDataToFirestore("Nestlé India Ltd.","NESTLEIND.NS","NSE");
//        addDataToFirestore("Tata Steel Ltd.","TATASTEEL.NS","NSE");
//        addDataToFirestore("Cipla Ltd.","CIPLA.NS","NSE");
//        addDataToFirestore("Shree Cement Ltd.","SHREECEM.NS","NSE");
//        addDataToFirestore("NTPC Ltd.","NTPC.NS","NSE");
//        addDataToFirestore("Kotak Mahindra Bank Ltd.","KOTAKBANK.NS","NSE");
//        addDataToFirestore("Bharti Airtel Ltd.","Bharti Airtel Ltd.","NSE");
//        addDataToFirestore("Apollo Hospitals Enterprise Ltd.","APOLLOHOSP.NS","NSE");
//        addDataToFirestore("Titan Company Ltd.","TITAN.NS","NSE");
//        addDataToFirestore("Reliance Industries Ltd.","RELIANCE.NS","NSE");
//        addDataToFirestore("Maruti Suzuki India Ltd.","MARUTI.NS","NSE");
//        addDataToFirestore("ITC Ltd.","ITC.NS","NSE");
//        addDataToFirestore("UltraTech Cement Ltd.","ULTRACEMCO.NS","NSE");
//        addDataToFirestore("Bajaj Auto Ltd.","BAJAJ-AUTO.NS","NSE");
//        addDataToFirestore("MM.NS","MM.NS","NSE");
//        addDataToFirestore("Mahindra & Mahindra Ltd.","M&M.NS","NSE");
//        addDataToFirestore("IndusInd Bank Ltd.","INDUSINDBK.NS","NSE");
//        addDataToFirestore("Bajaj Finserv Ltd.","BAJAJFINSV.NS","NSE");
//        addDataToFirestore("Oil and Natural Gas Corp. Ltd.","ONGC.NS","NSE");
//        addDataToFirestore("Britannia Industries Ltd.","BRITANNIA.NS","NSE");
//        addDataToFirestore("Hero MotoCorp Ltd.","HEROMOTOCO.NS","NSE");
//        addDataToFirestore("Tata Consumer Products Ltd.","TATACONSUM.NS","NSE");
//        addDataToFirestore("Larsen & Toubro Ltd.","LT.NS","NSE");
//        addDataToFirestore("Wipro Ltd.","WIPRO.NS","NSE");
//        addDataToFirestore("HDFC Life Insurance Company Ltd.","HDFCLIFE.NS","NSE");
//        addDataToFirestore("Tech Mahindra Ltd.","TECHM.NS","NSE");
//        addDataToFirestore("ICICI Bank Ltd.","ICICIBANK.NS","NSE");
//        addDataToFirestore("Grasim Industries Ltd.","GRASIM.NS","NSE");
//        addDataToFirestore("HCL Technology Ltd.","HCLTECH.NS","NSE");
//        addDataToFirestore("State Bank of India","SBIN.NS","NSE");
//        addDataToFirestore("SBI Life Insurance Company Ltd.","SBILIFE.NS","NSE");
//        addDataToFirestore("Eicher Motors Ltd.","EICHERMOT.NS","NSE");
//        addDataToFirestore("Asian Paints Ltd.","ASIANPAINT.NS","NSE");
//        addDataToFirestore("UPL Ltd.","UPL.NS","NSE");
//        addDataToFirestore("Housing Development Finance Corp. Ltd.","HDFC.NS","NSE");
//        addDataToFirestore("Power Grid Corp. of India Ltd.","POWERGRID.NS","NSE");
//        addDataToFirestore("Tata Motors Ltd.","TATMOTORS.NS","NSE");
//        addDataToFirestore("Hindustan Unilever Ltd.","HINDUNILVR.NS","NSE");
//        addDataToFirestore("Divi's Laboratories Ltd.","DIVISLAB.NS","NSE");
//        addDataToFirestore("Bharat Petroleum Corp. Ltd.","BPCL.NS","NSE");
//        addDataToFirestore("HDFC Bank Ltd.","HDFCBANK.NS","NSE");
//        addDataToFirestore("Sun Pharmaceutical Industries Ltd.","SUNPHARMA.NS","NSE");
//        addDataToFirestore("Dr. Reddy's Laboratories Ltd.","DRREDDY.NS","NSE");
//        addDataToFirestore("Adani Ports & Special Economic Zone Ltd.","ADANIPORTS.NS","NSE");
//        addDataToFirestore("JSW Steel Ltd.","JSWSTEEL.NS","NSE");
//        addDataToFirestore("Infosys Ltd.","INFY.NS","NSE");
//        addDataToFirestore("Axis Bank Ltd.","AXISBANK.NS","NSE");
//
//        addDataToFirestore("Reliance Industries Ltd.","RELIANCE.BO","BSE");
//        addDataToFirestore("Tata Consultancy Services Ltd.","TCS.BO","BSE");
//        addDataToFirestore("HDFC Bank Ltd.","HDFCBANK.BO","BSE");
//        addDataToFirestore("Infosys Ltd.","INFY.BO","BSE");
//        addDataToFirestore("Hindustan Unilever Ltd.","HINDUNILVR.BO","BSE");
//        addDataToFirestore("ICICI Bank Ltd.","ICICIBANK.BO","BSE");
//        addDataToFirestore("State Bank of India","SBIN.BO","BSE");
//        addDataToFirestore("Housing Development Finance Corp. Ltd.","HDFC.BO","BSE");
//        addDataToFirestore("Bharti Airtel Ltd.","Bharti Airtel Ltd.","BSE");
//        addDataToFirestore("Kotak Mahindra Bank Ltd.","KOTAKBANK.BO","BSE");
//        addDataToFirestore("ITC Ltd.","ITC.BO","BSE");
//        addDataToFirestore("Bajaj Finance Ltd.","BAJFINANCE.BO","BSE");
//        addDataToFirestore("HCL Technology Ltd.","HCLTECH.BO","BSE");
//        addDataToFirestore("Asian Paints Ltd.","ASIANPAINT.BO","BSE");
//        addDataToFirestore("Wipro Ltd.","WIPRO.BO","BSE");
//        addDataToFirestore("Maruti Suzuki India Ltd.","MARUTI.BO","BSE");
//        addDataToFirestore("Larsen & Toubro Ltd.","LT.BO","BSE");
//        addDataToFirestore("Axis Bank Ltd.","AXISBANK.BO","BSE");
//        addDataToFirestore("Sun Pharmaceutical Industries Ltd.","SUNPHARMA.BO","BSE");
//        addDataToFirestore("Titan Company Ltd.","TITAN.BO","BSE");
//        addDataToFirestore("Bajaj Finserv Ltd.","BAJAJFIBOV.BO","BSE");
//        addDataToFirestore("Nestlé India Ltd.","NESTLEIND.BO","BSE");
//        addDataToFirestore("UltraTech Cement Ltd.","ULTRACEMCO.BO","BSE");
//        addDataToFirestore("Power Grid Corp. of India Ltd.","POWERGRID.BO","BSE");
//        addDataToFirestore("NTPC Ltd.","NTPC.BO","BSE");
//        addDataToFirestore("Mahindra & Mahindra Ltd.","M&M.BO","BSE");
//        addDataToFirestore("Tata Steel Ltd.","TATASTEEL.BO","BSE");
//        addDataToFirestore("Tech Mahindra Ltd.","TECHM.BO","BSE");
//        addDataToFirestore("Dr. Reddy's Laboratories Ltd.","DRREDDY.BO","BSE");
//        addDataToFirestore("IndusInd Bank Ltd.","INDUSINDBK.BO","BSE");

//        addDataToFirestore("a","b","c");
        return view;
    }

    private void getStockDataAndSetAdapter(){
        ArrayList<CardData> newCards = new ArrayList<>();
        db.collection("stockData").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                            for (DocumentSnapshot d : list) {
                                String name = d.get("stName").toString();
                                String symbol = d.get("stSymbol").toString();
                                String market = d.get("stMarket").toString();
                                cardDataArrayList.add(new CardData(name, symbol, market));
                            }
                            getUserData();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity().getApplicationContext(), "Fail to get the data.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void getUserData(){
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
                    setSpinner();
                }
            });
    }

    private void setSpinner(){
        ArrayList<String> lists = new ArrayList<String>();

        for(Map.Entry<String,Object> entry: userData.entrySet()){
            lists.add(entry.getKey());
        }
        ArrayAdapter<String> listsAdapter = new ArrayAdapter<String>(getActivity().getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, lists);
        spinner.setAdapter(listsAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                searchViewBlank.setText("");
                currSpinnerSelection = spinner.getSelectedItem().toString();
                setAdapterAndSearch();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
    }

    private void setAdapterAndSearch(){
        CustomAdapter customAdapter = new CustomAdapter(cardDataArrayList, currSpinnerSelection, db, userUID, userData);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        searchResultsView.setLayoutManager(linearLayoutManager);
        searchResultsView.setAdapter(customAdapter);

        searchViewBlank.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(v.hasFocus()){
                    searchIcon.setVisibility(View.GONE);
                }
                else{
                    searchIcon.setVisibility(View.VISIBLE);
                }
            }
        });

        searchViewBlank.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                String newText = searchViewBlank.getText().toString();
                customAdapter.getFilter().filter(newText);
            }
        });
    }

//
//    private void addDataToFirestore(String stName, String stSymbol, String stMarket) {
//    //first retrieve user file: all the watchlists using document snapshot(not collection)
//        // we will get a hash Map = {"list1" : ...
////                                  {"list2" : ...
////                                      ....
////        traverse required list, add new data in existing list, or add new list etc, etc
////        use document.set(Hashmap) to replace the old data with this new map
//        Map<String, Object> data = new HashMap<>();
//        data.put("knsdkckdlmw", Arrays.asList("c","b"));
//
//        db.collection("users").document("ABCDE").set(data);
////        data.put("stSymbol", stSymbol);
////        data.put("stMarket", stMarket);
////        ArrayList<String> a = new ArrayList<String>();
////        a.add(stName);
////        data.put("My")
////        db.collection("stockData").document("testUser2").collection("MyFavList1")
//////        db.collection("stockData")
////        .add(data).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
////            @Override
////            public void onSuccess(DocumentReference documentReference) {
////                Toast.makeText(getActivity().getApplicationContext(), "Your Course has been added to Firebase Firestore", Toast.LENGTH_SHORT).show();
////            }
////        }).addOnFailureListener(new OnFailureListener() {
////            @Override
////            public void onFailure(@NonNull Exception e) {
////                Toast.makeText(getActivity().getApplicationContext(), "Fail to add course \n" + e, Toast.LENGTH_SHORT).show();
////            }
////        });
//
//
////        db.collection("users").document("testUser2")
////                .set(data)
////                .addOnSuccessListener(new OnSuccessListener<Void>() {
////                    @Override
////                    public void onSuccess(Void aVoid) {
////                        Log.d("TAG", "DocumentSnapshot successfully written!");
////                    }
////                })
////                .addOnFailureListener(new OnFailureListener() {
////                    @Override
////                    public void onFailure(@NonNull Exception e) {
////                        Log.w("TAG", "Error writing document", e);
////                    }
////                });
//    }
}