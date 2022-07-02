package com.example.stocktrack;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HomeFragment extends Fragment {
    TextView name1,symbol1,market1,price1,change1,percChange1;
    TextView name2,symbol2,market2,price2,change2,percChange2;
    ImageView arrow1, arrow2;
    CardView card1, card2;
    String jsonData;
    Spinner watchlistSpinner;
    RecyclerView watchlistRecycler;
    String currentSpinnerSelection;
    FirebaseFirestore db;
    String userUID;
    Map<String,Object> userData;
    ProgressBar pgBar;
    ArrayList<WatchlistData> watchlistDataArrayList;

    public HomeFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        bindView(view);

        setIndexCards();
        getUserData();
        return view;
    }

    private void bindView(View view){
        pgBar = view.findViewById(R.id.progressBar2);
        card1 = view.findViewById(R.id.card1);
        name1 = view.findViewById(R.id.card1_stName);
        symbol1 = view.findViewById(R.id.card1_stSymbol);
        market1 = view.findViewById(R.id.card1_stMarket);
        price1 = view.findViewById(R.id.card1_stPrice);
        change1 = view.findViewById(R.id.card1_stChange);
        percChange1 = view.findViewById(R.id.card1_stChangePerc);
        arrow1 = view.findViewById(R.id.card1_stArrow);

        card2 = view.findViewById(R.id.card2);
        name2 = view.findViewById(R.id.card2_stName);
        symbol2 = view.findViewById(R.id.card2_stSymbol);
        market2 = view.findViewById(R.id.card2_stMarket);
        price2 = view.findViewById(R.id.card2_stPrice);
        change2 = view.findViewById(R.id.card2_stChange);
        percChange2 = view.findViewById(R.id.card2_stChangePerc);
        arrow2 = view.findViewById(R.id.card2_stArrow);

        card1.setVisibility(View.GONE);
        card2.setVisibility(View.GONE);

        watchlistSpinner = view.findViewById(R.id.spinnerWatchlist);
        watchlistRecycler = view.findViewById(R.id.recyclerViewWatclist);

        userUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        db = FirebaseFirestore.getInstance();
        userData = new HashMap<>();
        watchlistDataArrayList = new ArrayList<>();
    }

    private void setIndexCards(){
        okhttp3.OkHttpClient client = new OkHttpClient();
        okhttp3.Request request = new Request.Builder()
                .url("https://yh-finance.p.rapidapi.com/market/v2/get-quotes?region=IN&symbols=%5ENSEI%2C%5EBSESN")
                .get()
                .addHeader("X-RapidAPI-Key", BuildConfig.RAPIDAPI_API_KEY)
                .addHeader("X-RapidAPI-Host", "yh-finance.p.rapidapi.com")
                .build();
        pgBar.setVisibility(View.VISIBLE);
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String responseString = response.body().string();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            jsonData = responseString;
                            if (!jsonData.isEmpty()) {
                                JsonElement jsonElement = new JsonParser().parse(jsonData);
                                JsonObject jsonObject = jsonElement.getAsJsonObject();
                                JsonObject quoteResponse = jsonObject.getAsJsonObject("quoteResponse");
                                JsonArray results = quoteResponse.getAsJsonArray("result");
                                JsonObject quote0 = results.get(0).getAsJsonObject();
                                JsonObject quote1 = results.get(1).getAsJsonObject();

                                setCards(card1,name1,symbol1,market1,price1,change1,percChange1,arrow1,quote0);
                                setCards(card2,name2,symbol2,market2,price2,change2,percChange2,arrow2,quote1);
                                pgBar.setVisibility(View.GONE);

                                card1.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(getActivity().getApplicationContext(), StockPage.class);
                                        intent.putExtra("msg",symbol1.getText().toString());
                                        startActivity(intent);
                                    }
                                });
                                card2.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(getActivity().getApplicationContext(), StockPage.class);
                                        intent.putExtra("msg",symbol2.getText().toString());
                                        startActivity(intent);
                                    }
                                });
                            } else {
                                Log.d("json not read", "sad");
                            }
                        }
                    });
                } else {
                    System.out.println("Error");
                }
            }
        });
        client.connectionPool().evictAll();
    }

    private void setCards(CardView card, TextView Name,TextView symbol,TextView market,
                         TextView price,TextView change,TextView percChange, ImageView arrow,
                         JsonObject quote){
        Name.setText(quote.get("shortName").toString().replaceAll("^\"|\"$", ""));
        symbol.setText(quote.get("symbol").toString().replaceAll("^\"|\"$", ""));
        market.setText(quote.get("fullExchangeName").toString().replaceAll("^\"|\"$", ""));
        price.setText(String.format("%.2f",Double.parseDouble(quote.get("regularMarketPrice").toString())));
        change.setText(String.format("%.2f",Double.parseDouble(quote.get("regularMarketChange").toString())));

        String percChangeVal =  quote.get("regularMarketChangePercent").toString();
        percChange.setText(percChangeVal);
        double val = Double.parseDouble(percChangeVal);
        percChange.setText(String.format("%.2f",val)+"%");

        arrow.setBackgroundResource(0);
        if(val<0)
            arrow.setBackgroundResource(R.drawable.ic_baseline_arrow_drop_down_24);
        else
            arrow.setBackgroundResource(R.drawable.ic_baseline_arrow_drop_up_24);
        card.setVisibility(View.VISIBLE);
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
        watchlistSpinner.setAdapter(listsAdapter);

        watchlistSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                pgBar.setVisibility(View.VISIBLE);
                WatchlistAdapter prevAdapter = (WatchlistAdapter)watchlistRecycler.getAdapter();
                if(prevAdapter!=null) {
                    prevAdapter.clearData();
                    prevAdapter.notifyDataSetChanged();
                }
                currentSpinnerSelection = watchlistSpinner.getSelectedItem().toString();
                watchlistDataArrayList.clear();
                fillWatchlistArrayList();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
    }

    private void fillWatchlistArrayList(){
        Object obj = userData.get(currentSpinnerSelection);
        ArrayList<String> list = new ArrayList<>();
        if(obj!=""){
            list = (ArrayList<String>) obj;
        }
        if(!list.isEmpty()) {
            String URL = "https://yh-finance.p.rapidapi.com/market/v2/get-quotes?region=IN&symbols=";
            for (String symbol : list) {
                URL += symbol + "%2C";
            }
            URL = URL.substring(0, URL.length() - 3);
            okhttp3.OkHttpClient client = new OkHttpClient();
            okhttp3.Request request = new Request.Builder()
                    .url(URL)
                    .get()
                    .addHeader("X-RapidAPI-Key", BuildConfig.RAPIDAPI_API_KEY)
                    .addHeader("X-RapidAPI-Host", "yh-finance.p.rapidapi.com")
                    .build();
            //        pgBar.setVisibility(View.VISIBLE);
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        final String responseString = response.body().string();
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                jsonData = responseString;
                                if (!jsonData.isEmpty()) {
                                    JsonElement jsonElement = new JsonParser().parse(jsonData);
                                    JsonObject jsonObject = jsonElement.getAsJsonObject();
                                    JsonObject quoteResponse = jsonObject.getAsJsonObject("quoteResponse");
                                    JsonArray results = quoteResponse.getAsJsonArray("result");
                                    for (JsonElement element : results) {
                                        JsonObject quote = element.getAsJsonObject();
                                        addWatchlistData(card1, name1, symbol1, market1, price1, change1, percChange1, arrow1, quote);
                                    }
                                    setAdapterAndSearch();
                                    //                                pgBar.setVisibility(View.GONE);
                                } else {
                                    Log.d("json not read", "sad");
                                }
                            }
                        });
                    } else {
                        System.out.println("Error");
                    }
                }
            });
            client.connectionPool().evictAll();
        }
    }

    private void addWatchlistData(CardView card, TextView Name,TextView symbol,TextView market,
                          TextView price,TextView change,TextView percChange, ImageView arrow,
                          JsonObject quote){
        String stName = quote.get("shortName").toString().replaceAll("^\"|\"$", "");
        String stPrice = quote.get("regularMarketPrice").toString().replaceAll("^\"|\"$", "");
        String stMarket = quote.get("fullExchangeName").toString().replaceAll("^\"|\"$", "");
        String stChange = quote.get("regularMarketChange").toString();
        String stChangePercent = quote.get("regularMarketChangePercent").toString();
        String stPrevClose = quote.get("regularMarketPreviousClose").toString();
        String stOpen = quote.get("regularMarketOpen").toString();
        String stDayRange = quote.get("regularMarketDayRange").toString().replaceAll("^\"|\"$", "");
        String st52WRange = quote.get("fiftyTwoWeekRange").toString().replaceAll("^\"|\"$", "");

        watchlistDataArrayList.add(new WatchlistData(stName,stPrice,stMarket,stChange,stChangePercent,stPrevClose,stOpen,stDayRange,st52WRange));
    }


    private void setAdapterAndSearch(){
        WatchlistAdapter watchlistAdapter = new WatchlistAdapter(watchlistDataArrayList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        watchlistRecycler.setLayoutManager(linearLayoutManager);
        watchlistRecycler.setAdapter(watchlistAdapter);
        pgBar.setVisibility(View.GONE);
    }

}