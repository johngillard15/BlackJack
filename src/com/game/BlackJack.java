package com.game;

import com.actors.Actor;
import com.actors.Dealer;
import com.actors.Player;
import com.card.*;
import com.utilities.ANSI;
import com.utilities.CLI;
import com.utilities.Input;
import com.utilities.UI;

/**
 * <h1>BlackJack</h1>
 *
 * <p>My BlackJack game.</p>
 *
 * <br>
 *
 * @since 10/9/2021
 * @author John Gilard
 * @version 0.12.0
 */

public class BlackJack extends Game {
    private final Deck deck; // TODO: multiple decks in blackjack? (new Blackjack Deck class maybe)
    // TODO: ask for number of decks
    // TODO: maybe make type Actor when betting moves to the hand
    // TODO: maybe make list of hands here then associate with player
//    private final List<Hand> hands = new ArrayList<>();
    private final Dealer dealer = new Dealer();
    private boolean iNeedHelp = false;
    private boolean playerLeft = false;

    public BlackJack(){
        super(1, 4);

        deck = new CheaterStandardDeck();
        deck.shuffle();

        setup();
    }

    private void draw(Actor player){
        player.hand.addCard(deck.draw());
    }

    private void draw(Actor player, int count){
        for(int i = 0; i < count; i++)
            draw(player);
    }

    private void discard(Card card){
        deck.discard(card);
    }

    private int getHandValue(Actor player){
        int handScore = 0;

        int aces = 0;
        for(Card card : player.hand.cards) {
            if(card.value.equals("Ace")) ++aces;
            handScore += getValue(card);
        }

        while(aces > 0 && !(handScore + 10 > 21)){
            handScore += 10;
            --aces;
        }

        return handScore;
    }

    public static int getValue(Card card){
        return switch(card.value){
            case "Ace" -> 1;
            case "Jack", "Queen", "King" -> 10;
            default -> Integer.parseInt(card.value);
        };
    }

    @Override
    public void play(){
        System.out.println("\n--- Welcome to BlackJack. Drinks are on the house. ---");
        CLI.pause();

        do{
            round();
        }while(players.size() > 0);
    }

    @Override
    protected void round(){
        if(deck instanceof TestDeck)
            System.out.println("Dealer draws...");
        draw(dealer, 2);

        for(Player player : players) {
            if(deck instanceof TestDeck)
                System.out.printf("%s draws...", player.name);
            draw(player, 2);
            System.out.println(player.hand.cards.size());
        }

        for(int i = 0; i < players.size(); i++){
            turn(players.get(i));

            if(playerLeft){
                --i;
                playerLeft = false;
            }
        }

        if(players.size() > 0){
            dealerTurn();

            displayResults();

            clearTable();
            deck.shuffle();
        }
    }

    @Override
    protected void turn(Player activePlayer){
        // TODO: make a list of hands for splits and iterate through it each turn
        CLI.cls();
        String name = activePlayer.name;
        System.out.printf("- %s%s turn -\n",
                name, name.charAt(name.length() - 1) == 's' ? "'" : "'s");
        CLI.pause();

        System.out.println("Would you like to place a (b)et or (l)eave the table?");
        System.out.print("̲bet or ̲leave ");
        switch(Input.getString("b", "l").toLowerCase()){
            case "b" -> placeBet(activePlayer);
            case "l" -> {
                leaveTable(activePlayer);
                return;
            }
        }

        System.out.println("\nDealer:"); // TODO: end round if dealer starts with 21
        UI.showSideBySide(StandardDeck.getCardGUI(dealer.hand.cards.get(0)), StandardDeck.getBackCard());
        System.out.printf("Hand score: %s + ?\n", getValue(dealer.hand.cards.get(0)));

        boolean standing = false;
        do{
            showHand(activePlayer);

            if(getHandValue(activePlayer) == 21 && activePlayer.hand.cards.size() == 2) {
                blackjack(activePlayer);
                standing = true;
            }

            int playerBalance = activePlayer.getBalance();
            int playerBet = activePlayer.getBet();
            String thisBalance = String.format("$%,d", playerBalance);
            String formattedBalance = playerBalance < 0 ? ANSI.RED + thisBalance + ANSI.RESET : thisBalance;
            System.out.printf("\ncurrent balance: $%,d (%s - $%,d)\n",
                    playerBalance - playerBet, formattedBalance, playerBet);
            System.out.printf("%16s $%,d + $%,d\n", "this bet:",
                    playerBet, activePlayer.getBonus());

            String choice;
            if(standing)
                choice = "s";
            else if(activePlayer.hand.cards.size() == 2){
                System.out.println("\nWould you like to (h)it, (s)tand, or (l)eave?");
                System.out.print("̲hit, ̲stand, or ̲leave "); // ̲
                choice = Input.getString("h", "s", "psst dealer", "bust", "l").toLowerCase();
            }
            else{
                System.out.println("\nWould you like to (h)it, (s)tand, (d)ouble down, or (l)eave?");
                System.out.print("̲hit, ̲stand, ̲double, ̲leave "); // ̲
                choice = Input.getString("h", "s", "d", "psst dealer", "bust", "l").toLowerCase();
            }

            switch (choice) {
                case "h", "psst dealer" -> {
                    if(deck instanceof CheaterStandardDeck && choice.equals("psst dealer")) iNeedHelp = true;

                    System.out.println("\nHit me!");
                    standing = hit(activePlayer) || getHandValue(activePlayer) == 21;

                    if(activePlayer.hand.cards.size() == 5 && getHandValue(activePlayer) <= 21) {
                        fiveCardCharlie(activePlayer);
                        standing = true;
                    }

                    if(getHandValue(activePlayer) == 21){
                        showHand(activePlayer);
                        CLI.pause();
                    }
                }
                case "s" -> {
                    System.out.printf("\n%s stands.\n", activePlayer.name);
                    CLI.pause();

                    standing = true;
                }
                case "d" -> {
                    System.out.println("\nDouble or nothing!");

                    doubleDown(activePlayer);
                    standing = true;
                }
                case "bust" -> {
                    System.out.println("\nBust! :)");
                    CLI.pause();

                    standing = true;
                }
                case "l" -> {
                    leaveTable(activePlayer);

                    standing = true;
                }
            }
        }while(!standing);
    }

    private void placeBet(Player player){
        int loan = 1_000;

        if(player.getBalance() <= 0) {
            System.out.print("\nThe casino has graciously lent you $1,000 to spend based on your situation.");
        }
        System.out.printf("\nbalance: $%,d %s\n",
                player.getBalance(), player.getBalance() <= 0 ? "($1,000 loan)" : "");
        System.out.println("Place your bet:");

        int bet;
        if(player.getBalance() > 0)
            bet = (Input.getInt(0, player.getBalance()));
        else
            bet = (Input.getInt(0, loan));

        player.setBet(bet);
    }

    private void showHand(Actor player){
        System.out.printf("\n%s%s cards:\n",
                player.name, player.name.charAt(player.name.length() - 1) == 's' ? "'" : "'s");

        player.hand.sortByValue();
        CardGUI.showHand(player.hand.cards);

        int handScore = getHandValue(player);
        System.out.printf("Hand score: %s\n", handScore == 21 ? ANSI.GREEN + handScore + ANSI.RESET : handScore);
    }

    private boolean hit(Actor player){
        if(iNeedHelp){
            player.hand.addCard(((CheaterStandardDeck) deck).cheatDraw());
            iNeedHelp = false;
        }
        else{
            System.out.printf("\n%s draws...", player.name);
            draw(player);
        }

        System.out.println("\nNew card:");
        CardGUI.showCard(player.hand.getCard(player.hand.cards.size() - 1));

        if(getHandValue(player) > 21){
            showHand(player);
            System.out.printf("\n%s Busts!\n", player.name);
            CLI.pause();

            return true;
        }
        else
            CLI.pause();

        return false;
    }

    private void doubleDown(Player player){
        player.setBet(player.getBet() * 2);

        System.out.printf("\n%-12s $%,d\n", "new bet:", player.getBet());
        System.out.printf("new balance: $%,d ($%,d - $%,d)\n",
                player.getBalance() - player.getBet(), player.getBalance(), player.getBet());

        if(!hit(player))
            showHand(player);
    }

    private void blackjack(Player player){
        System.out.println("\nBlackJack! (x1.5 winnings)");
        player.setBonus((int)(player.getBet() * 0.5));

        String formattedBonus = ANSI.GREEN + String.format("$%,d", player.getBonus()) + ANSI.RESET;
        System.out.printf("\nnew bet: $%,d + %s\n", player.getBet(), formattedBonus);
        CLI.pause();
    }

    private void fiveCardCharlie(Player player){
        System.out.println("\n5 Card Charlie! (5 to 1 winnings)");
        player.setBonus(player.getBet() * 4);

        String formattedBonus = ANSI.GREEN + String.format("$%,d", player.getBonus()) + ANSI.RESET;
        System.out.printf("\nnew bet: $%,d + %s\n", player.getBet(), formattedBonus);
        CLI.pause();
    }

    private void dealerTurn(){
        CLI.cls();
        System.out.println("- Dealer's turn -");
        CLI.pause();

        boolean standing = false;
        do{
            if(getHandValue(dealer) >= 17) {
                if(getHandValue(dealer) < 21) {
                    System.out.println("Dealer stands.");
                    CLI.pause();
                }

                standing = true;
            }
            else
                hit(dealer);
        }while(!standing);
    }

    @Override
    protected void displayResults(){
        CLI.cls();
        System.out.println("\n- Round Results -\n");

        showHand(dealer);
        int dealerScore = getHandValue(dealer);

        for(Player player : players){
            int bet = player.getBet();
            int bonus = player.getBonus();
            int score = getHandValue(player);
            boolean busted = score > 21;
            boolean fiveCardCharlie = player.hand.cards.size() == 5 && !busted;

            boolean winner = !busted
                    && (dealerScore > 21
                    || (score > dealerScore || fiveCardCharlie));

            System.out.printf("\n%s%s score: %,d %s\n",
                    player.name, player.name.charAt(player.name.length() - 1) == 's' ? "'" : "'s",
                    getHandValue(player), fiveCardCharlie ? "(5 Card Charlie)" : "");
            if(winner){
                player.wonBet();
                System.out.printf("%s has won $%,d (total: $%,d)\n",
                        player.name, bet + bonus, player.getBalance());
            }
            else if(score == dealerScore){
                System.out.printf("%s Pushed\n", player.name);
                player.pushed();
            }
            else{
                player.lostBet();
                System.out.printf("%s has lost $%,d (total: $%,d)\n", player.name, bet, player.getBalance());
            }
        }

        CLI.pause();
    }

    private void clearTable(){
        while(dealer.hand.cards.size() > 0){
            Card card = dealer.hand.getCard(0);
            dealer.hand.removeCard(card);
            discard(card);
        }

        for(Actor player : players){
            while(player.hand.cards.size() > 0){
                Card card = player.hand.getCard(0);
                player.hand.removeCard(card);
                discard(card);
            }
        }
    }

    private void leaveTable(Player player){
        if(player.getBalance() >= 0)
            System.out.printf("\n%s has left the table with $%,d.\n", player.name, player.getBalance());
        else if(player.getBalance() <= -10_000)
            System.out.printf("\n%s is off to remortgage their house.\n", player.name);
        else
            System.out.printf("\n%s owes the casino $%,d. They'll be well taken care of in the back room.\n",
                    player.name, -player.getBalance());

        CLI.pause();

        while(player.hand.cards.size() > 0){
            Card card = player.hand.getCard(0);
            player.hand.removeCard(card);
            discard(card);
        }

        players.remove(player);
        playerLeft = true;
    }
}
