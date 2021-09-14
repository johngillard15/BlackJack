package com.card;

import java.util.ArrayList;
import java.util.List;

public interface Deck {
    List<Card> pile = new ArrayList<>();

    void shuffle();
    Card deal();
    void discard(Card card);
}
