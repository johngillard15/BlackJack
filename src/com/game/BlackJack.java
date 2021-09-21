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
 * @version 0.12.4
 */

public class BlackJack extends Game {
    private final Deck deck;
    // TODO: maybe make type Actor when betting moves to the hand
    // TODO: maybe make list of hands here then associate with player
//    private final List<Hand> hands = new ArrayList<>();
    private final Dealer dealer = new Dealer();
    private boolean iNeedHelp = false;
    private boolean playerLeft = false;
    private boolean dealerNaturalBlackjack = false;

    public BlackJack(){
        super();

        deck = new TestDeck();
        deck.shuffle();

        setup();
    }

    private void draw(Actor player){
        player.hand.addCard(deck.draw());
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

        while(aces > 0 && handScore > 21){
            handScore -= 10;
            --aces;
        }

        return handScore;
    }

    public static int getValue(Card card){
        return switch(card.value){
            case "Ace" -> 11;
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
        for(int i = 0; i < 2; i++)
            draw(dealer);

        dealerNaturalBlackjack = getHandValue(dealer) == 21;

        boolean newPlayers = true;
        do{
            System.out.println("\nAre there any new players at the table? (y/n)");
            switch (Input.getString("y", "n", "yes", "no").toLowerCase()) {
                case "y", "yes" -> {
                    System.out.println("New player, what is your name?");
                    players.add(new Player(Input.getString()));
                }
                case "n", "no" -> newPlayers = false;
            }
        }while(newPlayers);

        for(int i = 0; i < players.size(); i++){
            System.out.printf("\n%s, would you like to place a (b)et or (l)eave the table?\n", players.get(i).name);
            System.out.print("̲bet or ̲leave ");
            switch (Input.getString("b", "l").toLowerCase()) {
                case "b" -> placeBet(players.get(i));
                case "l" -> {
                    leaveTable(players.get(i));
                    --i;
                }
            }
        }

        for(Player player : players) {
            if(deck instanceof TestDeck)
                System.out.printf("%s draws...\n", player.name);
            for(int i = 0; i < 2; i++)
                draw(player);
        }

        for(int i = 0; i < players.size(); i++){
            turn(players.get(i));

            if(playerLeft){
                --i;
                playerLeft = false;
            }
        }

        if(players.size() > 0){
            if(!dealerNaturalBlackjack) dealerTurn();

            displayResults();

            clearTable();
            deck.shuffle();
        }
    }

    @Override
    protected void turn(Player player){
        // TODO: make a list of hands for splits and iterate through it each turn
        CLI.cls();
        String name = player.name;
        System.out.printf("- %s%s turn -\n",
                name, name.charAt(name.length() - 1) == 's' ? "'" : "'s");
        CLI.pause();

        System.out.println("\nDealer:");
        UI.showSideBySide(StandardDeck.getCardGUI(dealer.hand.cards.get(0)), StandardDeck.getBackCard());
        System.out.printf("Hand score: %s + ?\n", getValue(dealer.hand.cards.get(0)));

        boolean standing = dealerNaturalBlackjack;
        do{
            showHand(player);

            showWallet(player);

            if(getHandValue(player) == 21 && player.hand.cards.size() == 2) {
                blackjack(player);
                return;
            }

            String choice;
            if(player.hand.cards.size() == 2){
                System.out.println("\nWould you like to (h)it, (s)tand, or (d)ouble down?");
                System.out.print("̲hit, ̲stand, ̲double ");
                choice = Input.getString("h", "s", "d", "psst dealer", "bust").toLowerCase();
            }
            else{
                System.out.println("\nWould you like to (h)it or (s)tand?");
                System.out.print("̲hit, ̲stand "); // ̲
                choice = Input.getString("h", "s", "psst dealer", "bust").toLowerCase();
            }

            switch (choice) {
                case "h", "psst dealer" -> {
                    if(deck instanceof CheaterStandardDeck && choice.equals("psst dealer")) iNeedHelp = true;

                    System.out.println("\nHit me!");
                    standing = hit(player) || getHandValue(player) == 21;

                    if(player.hand.cards.size() == 5 && getHandValue(player) <= 21) {
                        fiveCardCharlie(player);
                        standing = true;
                    }
                }
                case "s" -> {
                    System.out.printf("\n%s stands.\n", player.name);
                    CLI.pause();

                    standing = true;
                }
                case "d" -> {
                    System.out.println("\nDouble or nothing!");

                    doubleDown(player);
                    standing = true;
                }
                case "bust" -> {
                    System.out.println("\nBust! :)");
                    CLI.pause();

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

    private void showWallet(Player player){
        int playerBalance = player.getBalance();
        int playerBet = player.getBet();
        String thisBalance = String.format("$%,d", playerBalance);
        String formattedBalance = playerBalance < 0 ? ANSI.RED + thisBalance + ANSI.RESET : thisBalance;
        System.out.printf("\ncurrent balance: $%,d (%s - $%,d)\n",
                playerBalance - playerBet, formattedBalance, playerBet);
        System.out.printf("%16s $%,d + $%,d\n", "this bet:",
                playerBet, player.getBonus());
    }

    private void showHand(Actor player){
        System.out.printf("\n%s%s cards:\n",
                player.name, player.name.charAt(player.name.length() - 1) == 's' ? "'" : "'s");

        player.hand.sortByValue();
        CardGUI.showHand(player.hand.cards);

        String handScore = Integer.toString(getHandValue(player));
        if(getHandValue(player) == 21)
            handScore = ANSI.GREEN + getHandValue(player) + ANSI.RESET;
        else if(getHandValue(player) > 21)
            handScore = ANSI.RED + getHandValue(player) + ANSI.RESET;

        System.out.printf("Hand score: %s\n", handScore);
    }

    private boolean hit(Actor player){
        if(iNeedHelp){
            player.hand.addCard(((CheaterStandardDeck) deck).cheatDraw());
            iNeedHelp = false;
        }
        else{
            System.out.printf("\n%s draws...\n", player.name);
            draw(player);
        }

        System.out.println("\nNew card:");
        CardGUI.showCard(player.hand.getCard(player.hand.cards.size() - 1));


        boolean busted = getHandValue(player) > 21;
        if(busted)
            System.out.printf("\n%s Busts!\n", player.name);

        if(busted || getHandValue(player) == 21)
            showHand(player);

        CLI.pause();

        return busted;
    }

    private void doubleDown(Player player){
        player.setBet(player.getBet() * 2);

        System.out.printf("\n%-12s $%,d\n", "new bet:", player.getBet());
        System.out.printf("new balance: $%,d ($%,d - $%,d)\n",
                player.getBalance() - player.getBet(), player.getBalance(), player.getBet());

        if(!hit(player)){
            showHand(player);
            CLI.pause();
        }
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
        if(dealerNaturalBlackjack)
            System.out.println("\nDealer has BlackJack!");

        int dealerScore = getHandValue(dealer);

        for(Player player : players){
            int bet = player.getBet();
            int bonus = player.getBonus();
            int score = getHandValue(player);
            boolean busted = score > 21;
            boolean fiveCardCharlie = player.hand.cards.size() == 5 && !busted;
            boolean naturalBlackJack = score == 21 && player.hand.cards.size() == 2;

            boolean winner = !busted
                    && !dealerNaturalBlackjack
                    && (dealerScore > 21 || (fiveCardCharlie || naturalBlackJack || score > dealerScore));

            String specialHand = fiveCardCharlie ? "(5 Card Charlie)"
                    : naturalBlackJack ? "(BlackJack)"
                        : "";

            System.out.printf("\n%s%s score: %,d %s\n",
                    player.name, player.name.charAt(player.name.length() - 1) == 's' ? "'" : "'s",
                    getHandValue(player), specialHand);

            if(winner){
                player.wonBet();
                System.out.printf("%s has won $%,d (total: $%,d)\n",
                        player.name, bet + bonus, player.getBalance());
            }
            else if(!busted && score == dealerScore){
                if(dealerNaturalBlackjack && !naturalBlackJack){
                    player.lostBet();
                    System.out.printf("%s has lost $%,d (total: $%,d)\n",
                            player.name, bet, player.getBalance());
                }
                else{
                    player.pushed();
                    System.out.printf("%s Pushed\n", player.name);
                }
            }
            else{
                player.lostBet();
                System.out.printf("%s has lost $%,d (total: $%,d)\n",
                        player.name, bet, player.getBalance());
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
