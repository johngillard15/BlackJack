package com.company;

import com.card.Card;
import com.card.Deck;

public class Main {

    public static void main(String[] args) {
	    // write your code here
        Deck deck = new Deck();

        for(int i = 0; i < 5; i++) {
            int suitIndex = (int) (Math.random() * Card.SUITS.length);
            int valueIndex = (int) (Math.random() * Card.VALUES.length);
            deck.pile.add(new Card(Card.SUITS[suitIndex], Card.VALUES[valueIndex]));
        }

        System.out.println(deck);
    }
}
