package com.example.stocktrack;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


public class WatchlistAdapter extends RecyclerView.Adapter<WatchlistAdapter.ViewHolder> {

    private ArrayList<WatchlistData> userStocksInCurrentList;


    public class ViewHolder extends RecyclerView.ViewHolder{
        private final TextView stName;
        private final TextView stPrice;
        private final TextView stMarket;
        private final TextView stChange;
        private final TextView stChangePercent;
        private final TextView stPrevClose;
        private final TextView stOpen;
        private final TextView stDayRange;
        private final TextView st52WRange;
        private final ImageView greenImg;
        private final ImageView redImg;


        public ViewHolder(View view) {
            super(view);
            stName = (TextView) view.findViewById(R.id.stWatchlistNameProfile);
            stPrice = (TextView) view.findViewById(R.id.stPriceWatchlist);
            stMarket = (TextView) view.findViewById(R.id.stMarketWatchlist);
            stChange = (TextView) view.findViewById(R.id.stChangeWatchlist);
            stChangePercent = (TextView) view.findViewById(R.id.stChangePercentWatchlist);
            stPrevClose = (TextView) view.findViewById(R.id.stPrevCloseWatchlist);
            stOpen = (TextView) view.findViewById(R.id.stOpenWatchlist);
            stDayRange = (TextView) view.findViewById(R.id.stDayRangeWatchlist);
            st52WRange = (TextView) view.findViewById(R.id.st52WRangeWatchlist);
            greenImg = (ImageView) view.findViewById(R.id.greenImg);
            redImg = (ImageView) view.findViewById(R.id.redImg);
      }
    }

    public WatchlistAdapter(ArrayList<WatchlistData> dataSet) {
        userStocksInCurrentList = dataSet;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.watchlist_row_items, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        WatchlistData watchlistData = userStocksInCurrentList.get(position);
        viewHolder.stName.setText(watchlistData.stName);
        viewHolder.stPrice.setText(watchlistData.stPrice);
        viewHolder.stMarket.setText(watchlistData.stMarket);
        viewHolder.stChange.setText(watchlistData.stChange);
        viewHolder.stChangePercent.setText(watchlistData.stChangePercent);
        viewHolder.stPrevClose.setText(watchlistData.stPrevClose);
        viewHolder.stOpen.setText(watchlistData.stOpen);
        viewHolder.stDayRange.setText(watchlistData.stDayRange);
        viewHolder.st52WRange.setText(watchlistData.st52WRange);

        if(Double.parseDouble(watchlistData.stChangePercent)<0){
            viewHolder.greenImg.setVisibility(View.GONE);
            viewHolder.redImg.setVisibility(View.VISIBLE);
        }
        else{
            viewHolder.greenImg.setVisibility(View.VISIBLE);
            viewHolder.redImg.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return userStocksInCurrentList.size();
    }

    public void clearData(){
        userStocksInCurrentList.clear();
    }
}
