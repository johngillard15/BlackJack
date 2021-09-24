package com.card;

import com.utilities.CLI;
import com.utilities.Input;
import com.utilities.UI;

import java.util.stream.Collectors;

public class CheaterStandardDeck extends StandardDeck {
    private Card cheatCard = null;
    private boolean inDeck = false;

    @Override
    public Card draw(){
        return super.draw();
    }

    public Card cheatDraw(){
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

            if(checkDeck(suitIndex, valueIndex)){
                System.out.printf("The dealer slides you a%s %s...\n",
                        valueIndex == 0 || valueIndex == 7 ? "n" : "", cheatCard);
            }
            else
                System.out.println("We don't have that card, pick another one...");

            CLI.pause();
        }while(!inDeck);

        return cheatCard;
    }

    private boolean checkDeck(int suitIndex, int valueIndex){
        // ∨∨∨ for one standard deck ∨∨∨
        if(decks > 1){
            cheatCard = new Card(SUITS[suitIndex], VALUES[valueIndex]);
            inDeck = pile.removeIf(thisCard -> thisCard.suit.equals(SUITS[suitIndex]) && thisCard.value.equals(VALUES[valueIndex]));

            return inDeck;
        }
        // ∨∨∨ for multiple decks ∨∨∨
        else{
            try{
                cheatCard = pile.stream()
                        .filter(card -> card.suit.equals(SUITS[suitIndex]) && card.value.equals(VALUES[valueIndex]))
                        .collect(Collectors.toList())
                        .get(0);
                inDeck = pile.remove(cheatCard);

                return true;
            }
            catch (IndexOutOfBoundsException e){
                System.out.println("We don't have that card, pick another one...");

                return false;
            }
        }
    }
}
