package com.card;

import java.util.*;

public class StandardDeck implements Deck {
    public static final String[] SUITS = {
            "Clubs", "Diamonds", "Hearts", "Spades"
    };
    public static final String[] VALUES = {
            "Ace", "2", "3", "4", "5", "6", "7", "8", "9", "10", "Jack", "Queen", "King"
    };
    public final List<Card> pile = new ArrayList<>();

    public StandardDeck(){
        for(String suit : SUITS){
            for(String value : VALUES)
                pile.add(new Card(suit, value));
        }
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

    public static class SortBySuit implements Comparator<Card> {
        @Override
        public int compare(Card cardA, Card cardB){
            return Arrays.asList(SUITS).indexOf(cardA.suit) - Arrays.asList(SUITS).indexOf(cardB.suit);
        }
    }
    public static class SortByValue implements Comparator<Card> {
        @Override
        public int compare(Card cardA, Card cardB){
            return Arrays.asList(VALUES).indexOf(cardA.value) - Arrays.asList(VALUES).indexOf(cardB.value);
        }
    }

    @Override
    public String toString() {
        return String.format("Deck: %s", pile);
    }
}
