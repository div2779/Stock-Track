package com.example.stocktrack;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

public class StockPage extends AppCompatActivity {
    WebView webView;
    ProgressBar pgBar;
    String yfinanceURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_page);

        webView = findViewById(R.id.webView);
        pgBar = findViewById(R.id.progressBar);

        Intent intent = getIntent();
        String stockSymbol = intent.getStringExtra("msg");

        yfinanceURL = "https://www.finance.yahoo.com/quote/" + stockSymbol + "?p=" + stockSymbol + "&tsrc=fin-srch";

        webView.getSettings().setAllowUniversalAccessFromFileURLs(true);
        webView.getSettings().setAllowFileAccessFromFileURLs(true);
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setJavaScriptEnabled(true);

        webView.loadUrl(yfinanceURL);
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageStarted(WebView view, String Url, Bitmap favicon){
                pgBar.setVisibility(View.VISIBLE);
            }
            @Override
            public void onPageFinished(WebView view, String Url){
                pgBar.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onBackPressed(){
        if(webView.canGoBack()){
            webView.goBack();
        }
        else{
            super.onBackPressed();
        }
    }
}