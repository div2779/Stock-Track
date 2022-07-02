package com.example.stocktrack;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;


public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> implements Filterable {

    private ArrayList<CardData> cardListComplete;
    private ArrayList<CardData> filteredCardList;
    private String currentSpinnerSelection;
    private FirebaseFirestore db;
    private String userUID;
    private Map<String,Object> userData;
    private HashSet<String> userStocksInCurrentList;
    private CardTrie cardTrie;


    public class ViewHolder extends RecyclerView.ViewHolder{
        private final TextView stName;
        private final TextView stSymbol;
        private final TextView stMarket;
        private final ImageView addStocksButton;
        private final ImageView inStocksButton;


        public ViewHolder(View view) {
            super(view);
            stName = (TextView) view.findViewById(R.id.stWatchlistNameProfile);
            stSymbol = (TextView) view.findViewById(R.id.stPriceWatchlist);
            stMarket = (TextView) view.findViewById(R.id.stMarketWatchlist);
            addStocksButton = (ImageView) view.findViewById(R.id.editWatchlistBtn);
            inStocksButton = (ImageView) view.findViewById(R.id.deleteWatchlistBtn);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(itemView.getContext(), StockPage.class);
                    intent.putExtra("msg", filteredCardList.get(getAdapterPosition()).stSymbol);
                    itemView.getContext().startActivity(intent);
                }
            });
//            addStocksButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    ImageView putButton = itemView.findViewById(R.id.putInList);
//                    ImageView alreadyInButton = itemView.findViewById(R.id.putInList);
//                    putButton.setVisibility(View.GONE);
//                    alreadyInButton.setVisibility(View.VISIBLE);
//                }
//            });
        }

//        @Override
//        public void onClick(View v) {
//            if(v.==st)
//            Intent intent = new Intent(itemView.getContext(), StockPage.class);
//            intent.putExtra("msg", filteredCardList.get(getAdapterPosition()).stSymbol);
//            itemView.getContext().startActivity(intent);
//        }
    }

    public CustomAdapter(ArrayList<CardData> dataSet, String selection, FirebaseFirestore dbConnection, String uid, Map<String,Object> data) {
        cardListComplete = dataSet;
        filteredCardList = new ArrayList<CardData>();

        cardTrie = new CardTrie();
        for(CardData cardData: dataSet){
            cardTrie.insertCard(cardData);
        }

        currentSpinnerSelection = selection;
        db = dbConnection;
        userUID=uid;
        userData=data;
        ArrayList<String> list = new ArrayList<>();
        Object obj = userData.get(currentSpinnerSelection);
        if(obj!="")
            list = (ArrayList)userData.get(currentSpinnerSelection);
        userStocksInCurrentList = new HashSet<>(list);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.row_items, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, @SuppressLint("RecyclerView") final int position) {
        CardData cardData = filteredCardList.get(position);
        viewHolder.stName.setText(cardData.stName);
        viewHolder.stSymbol.setText(cardData.stSymbol);
        viewHolder.stMarket.setText(cardData.stMarket);
        if(userStocksInCurrentList.contains(cardData.stSymbol)){
            viewHolder.inStocksButton.setVisibility(View.VISIBLE);
            viewHolder.addStocksButton.setVisibility(View.GONE);
        }
        else{
            viewHolder.inStocksButton.setVisibility(View.GONE);
            viewHolder.addStocksButton.setVisibility(View.VISIBLE);
        }
        viewHolder.addStocksButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userStocksInCurrentList.add(cardData.stSymbol);
                ArrayList<String> list = new ArrayList<>();
                Object obj = userData.get(currentSpinnerSelection);
                if(obj!="")
                    list = (ArrayList)userData.get(currentSpinnerSelection);
                list.add(cardData.stSymbol);
                userData.put(currentSpinnerSelection, list);
                db.collection("users").document(userUID)
                                .set(userData);
                Toast.makeText(view.getContext(), "Stock Successfully added to "+currentSpinnerSelection, Toast.LENGTH_SHORT).show();
                notifyDataSetChanged();
            }
        });
        viewHolder.inStocksButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userStocksInCurrentList.remove(cardData.stSymbol);
                ArrayList<String> list = new ArrayList<>();
                Object obj = userData.get(currentSpinnerSelection);
                if(obj!="")
                    list = (ArrayList)userData.get(currentSpinnerSelection);
                list.remove(cardData.stSymbol);
                userData.put(currentSpinnerSelection, list);
                db.collection("users").document(userUID)
                        .set(userData);
                Toast.makeText(view.getContext(), "Stock Successfully removed from "+currentSpinnerSelection, Toast.LENGTH_SHORT).show();
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return filteredCardList.size();
    }

    @Override
    public Filter getFilter() {
        return cardFilter;
    }

    private Filter cardFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<CardData> filteredCardDataList = new ArrayList<>();
            FilterResults filterResults = new FilterResults();
            if(constraint!=null && constraint.length()>0){
//                String query = constraint.toString().toLowerCase().trim();
//                for(CardData cardData: cardListComplete){
//                    if(cardData.stName.toLowerCase().contains(query) ||
//                            cardData.stSymbol.toLowerCase().contains(query)){
//                        filteredCardDataList.add(cardData);
//                    }
//                }
                String query = constraint.toString();
                filteredCardDataList = cardTrie.searchCardStartingWith(query);
            }
            filterResults.values = filteredCardDataList;
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredCardList.clear();
            filteredCardList.addAll((ArrayList) results.values);
            notifyDataSetChanged();
        }

        public void setUserList(HashSet<String> hashSet){
            userStocksInCurrentList = hashSet;
            notifyDataSetChanged();
        }
    };
}
