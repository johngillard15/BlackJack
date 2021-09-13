package com.game;

import com.card.Card;
import com.card.Deck;
import com.card.StandardDeck;

public class BlackJack {
    protected final Deck deck;

    public BlackJack(){
        deck = new StandardDeck();

        //System.out.println("deck size: " + deck.pile.size());
        System.out.println(deck);
        deck.shuffle();
    }

    public void play(){
        System.out.println();
        
        for (int i = 0; i < 5; i++) {
            System.out.println(deck.deal());
        }
    }
}
