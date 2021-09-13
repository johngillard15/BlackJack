package com.card;
public interface Deck {

    void shuffle();
    Card deal();
    void discard(Card card);
}
