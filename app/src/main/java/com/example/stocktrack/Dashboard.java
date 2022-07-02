package com.example.stocktrack;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Dashboard extends AppCompatActivity {
    String jsonData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        jsonData="";
        Button logout = findViewById(R.id.btnLogout);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(user!=null){
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(getApplicationContext(), LoginPage.class));
                }
            }
        });

//        RequestQueue queue = Volley.newRequestQueue(Dashboard.this);
//        String url = "https://www.7timer.info/bin/astro.php?lon=113.2&lat=23.1&ac=0&unit=metric&output=json&tzshift=0";

//        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        jsonData = response;
//                        Toast.makeText(Dashboard.this, response, Toast.LENGTH_SHORT).show();
//                    }
//                }, new Response.ErrorListener(){
//                        @Override
//                            public void onErrorResponse(VolleyError error) {
//                            Toast.makeText(Dashboard.this, "Error Occured", Toast.LENGTH_SHORT).show();
//                        }
//                });
//
//        queue.add(stringRequest);

//        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.GET, url,
//                new com.android.volley.Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        jsonData = response;
//                        Toast.makeText(Dashboard.this, response, Toast.LENGTH_SHORT).show();
//                    }
//                }, new com.android.volley.Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Toast.makeText(Dashboard.this, "Error Occurred", Toast.LENGTH_SHORT).show();
//            }
//        });
//
//        queue.add(stringRequest);


        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://yh-finance.p.rapidapi.com/market/v2/get-quotes?region=US&symbols=%5ENSEI%2C%5EBSESN")
                .get()
                .addHeader("X-RapidAPI-Key", BuildConfig.RAPIDAPI_API_KEY)
                .addHeader("X-RapidAPI-Host", "yh-finance.p.rapidapi.com")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response.isSuccessful()){
                    final String responseString = response.body().string();
                    Dashboard.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.d("json assigned value", "before");
                            jsonData = responseString;
                            Log.d("json assigned value", "after");
//                            System.out.println(responseString);
                            Toast.makeText(Dashboard.this, jsonData, Toast.LENGTH_SHORT).show();
                            if(!jsonData.isEmpty()) {
                                Log.d("jsonData read", "reached here");
                                JsonElement jsonElement = new JsonParser().parse(jsonData);
                                JsonObject jsonObject = jsonElement.getAsJsonObject();
                                JsonObject quoteResponse = jsonObject.getAsJsonObject("quoteResponse");
                                JsonArray results = quoteResponse.getAsJsonArray("result");
                                JsonObject quote0 = results.get(0).getAsJsonObject();
                                JsonObject quote1 = results.get(1).getAsJsonObject();
                                Toast.makeText(Dashboard.this, quote0.get("shortName").toString(), Toast.LENGTH_SHORT).show();
                                Toast.makeText(Dashboard.this, quote1.get("shortName").toString(), Toast.LENGTH_SHORT).show();
                            }
                            else{
                                Log.d("json not read", "sad");
                            }
                        }
                    });
                }
                else{
                    System.out.println("Error");
                }
            }
        });
        client.connectionPool().evictAll();


        Log.d("jsonData Not read yet", "json = "+jsonData);
        if(!jsonData.isEmpty()) {
            Log.d("jsonData read", "reached here");
            JsonElement jsonElement = new JsonParser().parse(jsonData);
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            JsonObject quoteResponse = jsonObject.getAsJsonObject("quoteResponse");
            JsonArray results = quoteResponse.getAsJsonArray("result");
            JsonObject quote0 = results.get(0).getAsJsonObject();
            JsonObject quote1 = results.get(1).getAsJsonObject();
        }
        else{
            Log.d("json not read", "sad");
        }



//        JSONObject jsonObject = new JSONObject();
//        try {
//            JsonParser parser = new JsonParser();
//            jsonObject = (JSONObject) parser.parse(jsonData);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }

//        Map quoteResponse = null;
//        quoteResponse = ((Map)jsonObject.get("quoteResponse"));
//        JSONArray results = (JSONArray) quoteResponse.get("result");
//        for(int i=0; i< results.length(); i++){
//            Map quote = null;
//            try {
//                quote = ((Map)results.get(i));
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//            String stockName = (String) quote.get("shortName");
//            String stockSymbol = (String) quote.get("symbol");
//            String stockExchange = (String) quote.get("fullExchangeName");
//            Double marketPrice = (Double) quote.get("regularMarketPrice");
//            Double marketChange = (Double) quote.get("regularMarketChange");
//            Double marketChangePercent = (Double) quote.get("regularMarketChangePercent");
//            System.out.println(stockName+" "+stockSymbol+" "+stockExchange+" "+marketPrice+" "+marketChange+" "+marketChangePercent);
//        }


    }
}