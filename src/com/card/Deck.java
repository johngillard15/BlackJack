package com.card;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Deck {
    public final List<Card> pile;

    public Deck(){
        pile = new ArrayList<>();
    }

    public void shuffle(){
        Collections.shuffle(pile);
    }

    public Card deal(){
        return pile.remove(pile.size() - 1);
    }

    public void discard(Card card){
        pile.add(card);
    }

    @Override
    public String toString() {
        return String.format("Deck: %s", pile);
    }
}
