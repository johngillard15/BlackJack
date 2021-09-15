package com.card;

import com.utilities.CLI;
import com.utilities.Input;
import com.utilities.UI;

import java.util.List;
import java.util.stream.Collectors;

public class CheaterStandardDeck extends StandardDeck {

    @Override
    public Card draw(){
        return super.draw();
    }

    public Card cheatDraw(){
        Card cheatCard = null;

        boolean inDeck = false;
        do{
            UI.listerator(VALUES);
            System.out.println("Value:");
            int valueIndex = Input.getInt(1, VALUES.length) - 1;
            System.out.println(VALUES[valueIndex]);

            System.out.println("\nof...\n");

            UI.listerator(SUITS); // TODO: fix spacing in output
            System.out.print("Suit:\n");
            int suitIndex = Input.getInt(1, SUITS.length) - 1;
            System.out.println(SUITS[suitIndex]);

//            try{
//                cheatCard = pile.stream()
//                        .filter(card -> card.suit.equals(SUITS[suitIndex]) && card.value.equals(VALUES[valueIndex]))
//                        .collect(Collectors.toList())
//                        .get(0);
//                inDeck = pile.remove(cheatCard);
//
//                System.out.printf("The dealer slides you a%s %s...\n", valueIndex == 0 ? "n" : "", cheatCard);
//            }
//            catch(IndexOutOfBoundsException e){
//                System.out.println("We don't have that card, pick another one...");
//            }

            cheatCard = new Card(SUITS[suitIndex], VALUES[valueIndex]);
            inDeck = pile.removeIf(thisCard -> thisCard.suit.equals(SUITS[suitIndex]) && thisCard.value.equals(VALUES[valueIndex]));
            // there should only be one of each card so this^ is fine

            if(inDeck)
                System.out.printf("The dealer slides you the %s...\n", cheatCard);
            else
                System.out.println("We don't have that card, pick another one...");

            CLI.pause();
        }while(!inDeck);

        return cheatCard;
    }
}
