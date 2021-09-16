package com.game;

import com.actors.Dealer;
import com.actors.PlayerWithCards;
import com.actors.BlackJackPlayer;
import com.card.Card;
import com.card.CheaterStandardDeck;
import com.card.Deck;
import com.card.TestDeck;
import com.utilities.ANSI;
import com.utilities.CLI;
import com.utilities.Input;

import java.util.ArrayList;
import java.util.List;

/**
 * <h1>BlackJack</h1>
 *
 * <p>My BlackJack game.</p>
 *
 * <br>
 *
 * @since 10/9/2021
 * @author John Gilard
 * @version 0.8.0
 */

public class BlackJack extends Game{
    private final Deck deck; // TODO: multiple decks in blackjack? (new Blackjack Deck class maybe)
    // TODO: ask for number of decks
    private final List<BlackJackPlayer> players = new ArrayList<>();
    private final Dealer dealer = new Dealer();
    private boolean iNeedHelp = false;

    public BlackJack(){
        super(1, 4);

        deck = new TestDeck();
        deck.shuffle();

        setup();
    }

    @Override
    protected void getPlayers(int numPlayers){
        do{
            System.out.printf("\nPlayer %d, what is your name?\n", players.size() + 1);
            String name = Input.getString();
            System.out.printf("Hello, %s.\n", name);

            players.add(new BlackJackPlayer(name));
        }while(players.size() < numPlayers);
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
        draw(dealer, 2);

        for(PlayerWithCards player : players)
            draw(player, 2);

        for(PlayerWithCards player : players)
            turn(player);

        dealerTurn();

        checkWinners();

        clearTable();
        deck.shuffle();
    }

    @Override
    protected void turn(PlayerWithCards activePlayer){
        CLI.cls();
        String name = activePlayer.name;
        System.out.printf("- %s%s turn -\n",
                name, name.charAt(name.length() - 1) == 's' ? "'" : "'s");
        CLI.pause();

        for(BlackJackPlayer player : players)
            placeBet(player);

        System.out.println("\nDealer:"); // TODO: end round if dealer starts with 21
        System.out.println(dealer.hand.cards.get(0));
        System.out.println("? of ?"); // TODO: dealer's face down card, get fancy cards working and make a face down card
        System.out.printf("Hand score: %s + ?\n", getValue(dealer.hand.cards.get(0)));

        boolean hitOnce = false;
        boolean standing = false;
        do{
            showHand(activePlayer);

            if(getHandValue(activePlayer) == 21 && activePlayer.hand.cards.size() == 2) {
                blackjack((BlackJackPlayer) activePlayer);
                standing = true;
            }

            int playerBalance = ((BlackJackPlayer)activePlayer).getBalance();
            int playerBet = ((BlackJackPlayer)activePlayer).getBet();
            String thisBalance = String.format("%,d", playerBalance);
            String formattedBalance = ((BlackJackPlayer)activePlayer).getBalance() < 0
                    ? ANSI.RED + thisBalance + ANSI.RESET
                    : thisBalance;
            System.out.printf("\ncurrent balance: $%,d ($%s - $%,d)\n",
                    playerBalance - playerBet, formattedBalance, playerBet);
            System.out.printf("%16s $%,d\n", "this bet:", playerBet);

            String choice;
            if(standing)
                choice = "s";
            else if(hitOnce){
                System.out.println("\nWould you like to (h)it or (s)tand?");
                System.out.print("̲hit or ̲stand "); // ̲
                choice = Input.getString("h", "s", "psst dealer", "bust").toLowerCase();
            }
            else{
                System.out.println("\nWould you like to (h)it, (s)tand, or (d)ouble your bet?");
                System.out.print("̲hit, ̲stand, ̲double "); // ̲
                choice = Input.getString("h", "s", "d", "psst dealer", "bust").toLowerCase();
            }

            switch (choice) {
                case "h", "psst dealer" -> {
                    if(deck instanceof CheaterStandardDeck && choice.equals("psst dealer")) iNeedHelp = true;

                    System.out.println("\nHit me!");
                    hitOnce = true;
                    standing = hit(activePlayer) || getHandValue(activePlayer) == 21;

                    if(activePlayer.hand.cards.size() == 5 && getHandValue(activePlayer) <= 21) {
                        fiveCardCharlie((BlackJackPlayer) activePlayer);
                        standing = true;
                    }

                    if(getHandValue(activePlayer) == 21){
                        showHand(activePlayer);
                        CLI.pause();
                    }
                }
                case "s" -> {
                    System.out.printf("\n%s stands.\n", activePlayer.name);
                    standing = true;
                }
                case "d" -> {
                    System.out.println("\nDouble or nothing!");

                    doubleDown((BlackJackPlayer) activePlayer);
                    standing = true;
                }
                case "bust" -> {
                    System.out.println("\nBust! :)");
                    standing = true;
                }
            }
        }while(!standing);
    }

    private void dealerTurn(){
        CLI.cls();
        System.out.println("- Dealer's turn -");
        CLI.pause();

        boolean standing = false;
        do{
            if(getHandValue(dealer) >= 17) {
                if(getHandValue(dealer) < 21) System.out.println("Dealer stands.");
                standing = true;
            }
            else
                hit(dealer);
        }while(!standing);

        if(!(getHandValue(dealer) > 21))
            showHand(dealer);
        CLI.pause();
    }

    private void placeBet(BlackJackPlayer player){
        System.out.println("\nPlace your bet:");

        int bet = (Input.getInt(0, player.getBalance()));
        player.setBet(bet);
    }

    private void showHand(PlayerWithCards player){
        System.out.printf("\n%s%s cards:\n",
                player.name, player.name.charAt(player.name.length() - 1) == 's' ? "'" : "'s");

        player.hand.sortByValue();
        for(Card card : player.hand.cards)
            System.out.println(card);

        int handScore = getHandValue(player);
        System.out.printf("Hand score: %s\n", handScore == 21 ? ANSI.GREEN + handScore + ANSI.RESET : handScore);
    }

    private boolean hit(PlayerWithCards player){
        if(iNeedHelp){
            player.hand.addCard(((CheaterStandardDeck) deck).cheatDraw());
            iNeedHelp = false;
        }
        else
            draw(player);

        Card newCard = player.hand.getCard(player.hand.cards.size() - 1);
        System.out.printf("\n%s drew a%s %s\n",
                player.name, (newCard.value.equals("Ace") || newCard.value.equals("8") ? "n" : ""), newCard);

        if(getHandValue(player) > 21){
            showHand(player);
            System.out.printf("\n%s Busts!\n", player.name);
            CLI.pause();

            return true;
        }

        return false;
    }

    private void doubleDown(BlackJackPlayer player){
        player.setBet(player.getBet() * 2);

        System.out.printf("\n%-12s $%,d\n", "new bet:", player.getBet());
        System.out.printf("new balance: $%,d ($%,d - $%,d)\n",
                player.getBalance() - player.getBet(), player.getBalance(), player.getBet());

        if(!hit(player)) showHand(player);
    }

    private void draw(PlayerWithCards player){
        player.hand.addCard(deck.draw());
    }

    private void draw(PlayerWithCards player, int count){
        for(int i = 0; i < count; i++)
            draw(player);
    }

    private int getHandValue(PlayerWithCards player){
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

    private void checkWinners(){
        int dealerScore = getHandValue(dealer);
        System.out.println("Dealer's score: " + dealerScore);

        for(BlackJackPlayer player : players){
            int bet = player.getBet();
            int playerScore = getHandValue(player);
            boolean winner = playerScore <= 21
                    && (dealerScore > 21
                    || (playerScore > dealerScore || player.hand.cards.size() == 5));

            if(winner){
                player.wonBet();
                System.out.printf("\n%s has won $%,d (total: $%,d)\n",
                        player.name, bet + player.getBonus(), player.getBalance());
            }
            else if(playerScore == dealerScore){
                System.out.printf("\n%s Pushed\n", player.name);
                player.pushed();
            }
            else{
                player.lostBet();
                System.out.printf("\n%s has lost $%,d (total: $%,d)\n", player.name, bet, player.getBalance());
            }
        }

        CLI.pause();
    }

    private void blackjack(BlackJackPlayer player){
        System.out.println("\nBlackJack! (x1.5 winnings)");
        player.setBonus((int)(player.getBet() * 0.5));

        String formattedBonus = ANSI.GREEN + String.format("$%,d", player.getBonus()) + ANSI.RESET;
        System.out.printf("\nnew bet: $%,d + %s\n", player.getBet(), formattedBonus);
        CLI.pause();
    }
    private void fiveCardCharlie(BlackJackPlayer player){
        System.out.println("\n5 Card Charlie! (5 to 1 winnings)");
        player.setBonus(player.getBet() * 4);

        String formattedBonus = ANSI.GREEN + String.format("$%,d", player.getBonus()) + ANSI.RESET;
        System.out.printf("\nnew bet: $%,d + %s\n", player.getBet(), formattedBonus);
        CLI.pause();
    }

    private void clearTable(){
        while(dealer.hand.cards.size() > 0){
            Card card = dealer.hand.getCard(0);
            dealer.hand.removeCard(card);
        }

        for(PlayerWithCards player : players){
            while(player.hand.cards.size() > 0){
                Card card = player.hand.getCard(0);
                player.hand.removeCard(card);
            }
        }
    }

    private void leaveTable(BlackJackPlayer player){
        if(player.getBalance() >= 0)
            System.out.printf("%s has left the table with $%,d.", player.name, player.getBalance());
        else
            System.out.printf("%s owes the casino $%,d. They'll be well taken care of in the back room.",
                    player.name, player.getBalance());

        players.remove(player);
    }

    @Override
    protected void displayResults(){
        return;
    }
}
