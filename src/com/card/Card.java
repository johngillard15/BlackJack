package com.card;

import java.util.Arrays;
import java.util.Comparator;

public class Card {
    public static final String[] SUITS = {
            "Clubs", "Diamonds", "Hearts", "Spades"
    };
    public static final String[] VALUES = {
            "Ace", "2", "3", "4", "5", "6", "7", "8", "9", "10", "Jack", "Queen", "King"
    };
    public String suit;
    public String value;

    public Card(String suit, String value){
        this.suit = suit;
        this.value = value;
    }

    public static Card newCard(String suit, String value){
        return new Card(suit, value);
    }

    public String getCardGUI(){
        return ""; // do something here once i make a standard deck of cards
    }

    public static class SortBySuit implements Comparator<Card> {
        @Override
        public int compare(Card cardA, Card cardB){
            return Arrays.asList(SUITS).indexOf(cardA.suit) - Arrays.asList(SUITS).indexOf(cardB.suit);
        }
    }
    public static class SortByValue implements Comparator<Card> {
        @Override
        public int compare(Card cardA, Card cardB){
            return Arrays.asList(VALUES).indexOf(cardA.value) - Arrays.asList(VALUES).indexOf(cardB.value);
        }
    }

    @Override
    public String toString() {
        return String.format("｢%s of %s｣", value, suit);
    }
}