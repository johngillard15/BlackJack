package com.card;

public class Card {
    public final String suit;
    public final String value;

    public Card(String suit, String value){
        this.suit = suit;
        this.value = value;
    }

    @Override
    public String toString() {
        return String.format("%s of %s", value, suit);
    }
}