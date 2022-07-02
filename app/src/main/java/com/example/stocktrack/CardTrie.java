package com.example.stocktrack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class CardTrie {
    CardTrieNode root;

    public class CardTrieNode{
        Map<Character, CardTrieNode> children;
        boolean isWordEnd;
        CardData cardData;

        public CardTrieNode(){
            this.children = new HashMap<>();
            this.isWordEnd = false;
            this.cardData = null;
        }

        public Boolean hasCardTrieNode(Character letter){
            return this.children.containsKey(letter);
        }

        public CardTrieNode getCardTrieNode(Character letter){
            return this.children.get(letter);
        }

        public void putCardTrieNode(Character letter){
            this.children.put(letter, new CardTrieNode());
        }

        public Boolean isEnd(){
            return this.isWordEnd;
        }

        public void setEnd(){
            this.isWordEnd = true;
        }

        public CardData getCardData(){
            return this.cardData;
        }

        public void setCardData(CardData cardData){
            this.cardData = cardData;
        }
    }

    public CardTrie() {
        root = new CardTrieNode();
    }

    private void insertWord(String word, CardData cardData){
        CardTrieNode node = root;
        word = word.toLowerCase();
        for(int i=0; i<word.length(); i++){
            if(word.charAt(i) == '.') continue;

            if(!node.hasCardTrieNode(word.charAt(i))){
                node.putCardTrieNode(word.charAt(i));
            }
            node = node.getCardTrieNode(word.charAt(i));
        }
        node.setCardData(cardData);
        node.setEnd();
    }

    public void insertCard(CardData cardData){
        insertWord(cardData.stName, cardData);
        insertWord(cardData.stSymbol, cardData);
    }

    private HashSet<CardData> getAllCardList(CardTrieNode currNode){
        HashSet<CardData> cardDataHashSet = new HashSet<>();
        if(currNode.isEnd())
            cardDataHashSet.add(currNode.getCardData());
        for(Character key : currNode.children.keySet()){
            cardDataHashSet.addAll(getAllCardList(currNode.getCardTrieNode(key)));
        }
        return cardDataHashSet;
    }

    public ArrayList<CardData> searchCardStartingWith(String query){
        CardTrieNode node = root;
        Boolean found = true;
        query = query.trim().toLowerCase();
        for(int i=0; i<query.length(); i++){
            if(query.charAt(i) == '.') continue;

            if(!node.hasCardTrieNode(query.charAt(i))){
                found = false;
                break;
            }
            node = node.getCardTrieNode(query.charAt(i));
        }
        ArrayList<CardData> cardDataArrayList = new ArrayList<>();
        if(found.equals(true)){
            HashSet<CardData> cardDataSet = new HashSet<>();
            cardDataSet = getAllCardList(node);
            cardDataArrayList.addAll(cardDataSet);
        }
        return cardDataArrayList;
    }

}