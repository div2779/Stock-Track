package com.example.stocktrack;

public class WatchlistData {
    String stName;
    String stPrice;
    String stMarket;
    String stChange;
    String stChangePercent;
    String stPrevClose;
    String stOpen;
    String stDayRange;
    String st52WRange;
    WatchlistData(String stName, String stPrice, String stMarket, String stChange, String stChangePercent,
                  String stPrevClose, String stOpen, String stDayRange, String st52WRange){
        this.stName = stName;
        this.stPrice = String.format("%.2f",Double.parseDouble(stPrice));
        this.stMarket = stMarket;
        this.stChange = String.format("%.2f",Double.parseDouble(stChange));
        this.stChangePercent = String.format("%.2f",Double.parseDouble(stChangePercent));
        this.stPrevClose = stPrevClose;
        this.stOpen = stOpen;
        this.stDayRange = stDayRange;
        this.st52WRange = st52WRange;
    }
}