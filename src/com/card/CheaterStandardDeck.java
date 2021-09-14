package com.card;

import com.utilities.CLI;
import com.utilities.Input;
import com.utilities.UI;

public class CheaterStandardDeck extends StandardDeck {

    @Override
    public Card deal(){
        UI.listerator(VALUES);
        System.out.println("Value:");
        int valueIndex = Input.getInt(1, VALUES.length) - 1;
        System.out.println(VALUES[valueIndex]);

        System.out.println("of...");

        UI.listerator(SUITS);
        System.out.print("Suit:");
        int suitIndex = Input.getInt(1, SUITS.length) - 1;
        System.out.println(SUITS[suitIndex]);

        Card card = new Card(SUITS[suitIndex], VALUES[valueIndex]);

        System.out.printf("The dealer slides you a%s %s...\n", valueIndex == 0 ? "n" : "", card);
        CLI.pause();

        return card;
    }
}
