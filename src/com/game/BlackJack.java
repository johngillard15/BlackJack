package com.game;

import com.card.CheaterStandardDeck;
import com.card.Deck;

public class BlackJack {
    protected final Deck deck; // TODO: multiple decks in blackjack?

    public BlackJack(){
        deck = new CheaterStandardDeck();

        System.out.printf("Deck size: %d cards\n", deck.pile.size());
        System.out.println(deck);

        deck.shuffle();
    }

    public void play(){
        System.out.println();

        for (int i = 0; i < 5; i++) {
            System.out.printf("Deck size: %d cards\n", deck.pile.size());
            System.out.println(deck.deal());
        }
    }
}
