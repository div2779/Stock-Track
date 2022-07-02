package com.example.stocktrack;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


public class WatchlistProfileAdapter extends RecyclerView.Adapter<WatchlistProfileAdapter.ViewHolder> {

    private ArrayList<String> watchlistNames;
    onWatchlistClickListener watchlistClickListener;

    public interface onWatchlistClickListener{
        void onEditClick(int position);
        void onDeleteClick(int position);
    }

    public void setOnWatchlistCLickListener(onWatchlistClickListener listener){
        watchlistClickListener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private final TextView stName;
        private final ImageView editBtn;
        private final ImageView deleteBtn;


        public ViewHolder(View view, onWatchlistClickListener listener) {
            super(view);
            stName = (TextView) view.findViewById(R.id.stWatchlistNameProfile);
            editBtn = (ImageView) view.findViewById(R.id.editWatchlistBtn);
            deleteBtn = (ImageView) view.findViewById(R.id.deleteWatchlistBtn);
            watchlistClickListener = listener;

            editBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener!=null){
                        int position = getAdapterPosition();
                        if(position!=RecyclerView.NO_POSITION){
                            listener.onEditClick(position);
                        }
                    }
                }
            });
            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener!=null){
                        int position = getAdapterPosition();
                        if(position!=RecyclerView.NO_POSITION){
                            listener.onDeleteClick(position);
                        }
                    }
                }
            });
      }
    }

    public WatchlistProfileAdapter(ArrayList<String> dataSet) {
        watchlistNames = dataSet;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.profile_watchlist_row_items, viewGroup, false);

        return new ViewHolder(view, watchlistClickListener);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        String name = watchlistNames.get(position);
        viewHolder.stName.setText(name);
    }

    @Override
    public int getItemCount() {
        return watchlistNames.size();
    }

    public  void addName(String name){
        watchlistNames.add(name);
    }

    public  void changeName(int position, String to){
        watchlistNames.remove(position);
        watchlistNames.add(to);
    }

    public  void deleteName(int position){
        watchlistNames.remove(position);
    }
}
