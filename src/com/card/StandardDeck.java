package com.card;

import com.utilities.ANSI;

import java.util.*;

public class StandardDeck implements Deck {
    public static final String[] SUITS = { // TODO: enums for suit and value
            "Clubs", "Diamonds", "Hearts", "Spades"
    };
    public static final String[] VALUES = { // TODO: use these as integers and grab strings separately (or maybe just use index as numerical value...)
            "Ace", "2", "3", "4", "5", "6", "7", "8", "9", "10", "Jack", "Queen", "King"
    };
    List<Card> pile = new ArrayList<>();

    public StandardDeck(){
        for(String suit : SUITS){
            for(String value : VALUES)
                pile.add(new Card(suit, value));
        }
    }

    public void shuffle(){
        Collections.shuffle(pile);
    }

    public Card draw(){
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

    public static String getBackCard(){
        return """
                ╭─────────╮
                │╠╬╬╬╬╬╬╬╣│
                │╠╬╬╬╬╬╬╬╣│
                │╠╬╬╬╬╬╬╬╣│
                │╠╬╬╬╬╬╬╬╣│
                │╠╬╬╬╬╬╬╬╣│
                ╰─────────╯""";
    }

    public static String getCardGUI(Card card){
        String formattedCard =
                """
                ╭─────────╮
                │%s       │
                │         │
                │    %s   │
                │         │
                │       %s│
                ╰─────────╯""";

        String suitFace = switch(card.suit){
            case "Clubs" -> "♣";
            case "Diamonds" -> "♦";
            case "Hearts" -> "♥";
            case "Spades" -> "♠";
            default -> throw new IllegalStateException("Unexpected suit: " + card.suit);
        } + "   ";

        String valueFace = switch(card.value){
            case "Ace" -> "A";
            case "Jack" -> "J";
            case "Queen" -> "Q";
            case "King" -> "K";
            default -> card.value;
        };

        String top = valueFace, bot = valueFace;

        if(!card.value.equals("10")){
            top = valueFace + " ";
            bot = " " + valueFace;
        }

//        if(card.suit.equals("Hearts") || card.suit.equals("Diamonds")){
//            suitFace = ANSI.RED + suitFace + ANSI.RESET;
//            top = ANSI.RED + top + ANSI.RESET;
//            bot = ANSI.RED + bot + ANSI.RESET;
//        }

        return String.format(formattedCard, top, suitFace, bot);
    }


    @Override
    public String toString() {
        return String.format("Deck: %s", pile);
    }
}
