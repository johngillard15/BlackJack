package com.card;

public class Card {
    public final String suit;
    public final String value;

    public Card(String suit, String value){
        this.suit = suit;
        this.value = value;
    }

    public String getCardGUI(){
        String formattedCard =
                """
                ╭─────────╮
                │%s     │
                │         │
                │    %s  │
                │         │
                │     %s│
                ╰─────────╯""";

        String suitFace, valueFace = "";

        switch(suit){
            case "Clubs" -> {
                suitFace = "♣";
            }
            case "Diamonds" -> {
                suitFace = "♦";
            }
            case "Hearts" -> {
                suitFace = "♥";
            }
            case "Spades" -> {
                suitFace = "♠";
            }
            default -> throw new IllegalStateException("Unexpected suit: " + suit);
        }

        switch(value){
            case "Ace" -> {
                valueFace = "A";
            }
            case "Jack" -> {
                valueFace = "J";
            }
            case "Queen" -> {
                valueFace = "Q";
            }
            case "King" -> {
                valueFace = "K";
            }
            default -> {
                if(!value.equals("10"))
                    valueFace = " " + value;
            }
        }

        return String.format(formattedCard, suitFace + valueFace, suitFace, suitFace + valueFace);
    }

    @Override
    public String toString() {
        return String.format("%s of %s", value, suit);
    }

//    @Override
//    public String toString() {
//        String formattedCard =
//                """
//                        ╭─────────╮
//                        │%s     │
//                        │         │
//                        │    %s  │
//                        │         │
//                        │     %s│
//                        ╰─────────╯""";
//
//        String suitFace, valueFace = "";
//
//        switch(suit){
//            case "Clubs" -> {
//                suitFace = "♣";
//            }
//            case "Diamonds" -> {
//                suitFace = "♦";
//            }
//            case "Hearts" -> {
//                suitFace = "♥";
//            }
//            case "Spades" -> {
//                suitFace = "♠";
//            }
//            default -> throw new IllegalStateException("Unexpected suit: " + suit);
//        }
//
//        switch(value){
//            case "Ace" -> {
//                valueFace = "A";
//            }
//            case "Jack" -> {
//                valueFace = "J";
//            }
//            case "Queen" -> {
//                valueFace = "Q";
//            }
//            case "King" -> {
//                valueFace = "K";
//            }
//            default -> {
//                if(!value.equals("10"))
//                    valueFace = " " + value;
//            }
//        }
//
//        return String.format(formattedCard, suitFace + valueFace, suitFace, suitFace + valueFace);
//    }
}