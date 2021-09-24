package com.card;

import com.utilities.Input;

import java.util.Arrays;

public class TestDeck implements Deck{
    public static final String[] SUITS = {
            "Clubs", "Diamonds", "Hearts", "Spades"
    };
    public static final String[] VALUES = {
            "Ace", "2", "3", "4", "5", "6", "7", "8", "9", "10", "Jack", "Queen", "King"
    };

    @Override
    public void shuffle(){}

    @Override
    public Card draw() {
        System.out.println(Arrays.toString(VALUES));
        System.out.println("Which value? (1-13)");
        int valueIndex = Input.getInt(1, VALUES.length) - 1;

        System.out.println(Arrays.toString(SUITS));
        System.out.println("Which suit? (1-4)");
        int suitIndex = Input.getInt(1, SUITS.length) - 1;

        return new Card(SUITS[suitIndex], VALUES[valueIndex]);
    }

    @Override
    public void discard(Card card){}
}
