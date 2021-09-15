package com.card;

import java.util.ArrayList;
import java.util.List;

public interface Deck {

    void shuffle();
    Card draw();
    void discard(Card card);
}
