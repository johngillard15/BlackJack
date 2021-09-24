package com.card;

public interface Deck {

    void shuffle();
    Card draw();
    void discard(Card card);
}
