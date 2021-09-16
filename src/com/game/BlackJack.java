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
 * @version 0.9.0
 */

public class BlackJack extends Game{
    private final Deck deck; // TODO: multiple decks in blackjack? (new Blackjack Deck class maybe)
    // TODO: ask for number of decks
    private final List<BlackJackPlayer> players = new ArrayList<>();
    private final Dealer dealer = new Dealer();
    private boolean iNeedHelp = false;
    private boolean playerLeft = false;

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
    protected void turn(PlayerWithCards activePlayer){
        CLI.cls();
        String name = activePlayer.name;
        System.out.printf("- %s%s turn -\n",
                name, name.charAt(name.length() - 1) == 's' ? "'" : "'s");
        CLI.pause();

        placeBet((BlackJackPlayer) activePlayer);

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
            String thisBalance = String.format("$%,d", playerBalance);
            String formattedBalance = playerBalance < 0 ? ANSI.RED + thisBalance + ANSI.RESET : thisBalance;
            System.out.printf("\ncurrent balance: $%,d (%s - $%,d)\n",
                    playerBalance - playerBet, formattedBalance, playerBet);
            System.out.printf("%16s $%,d + $%,d\n", "this bet:",
                    playerBet, ((BlackJackPlayer) activePlayer).getBonus());

            String choice;
            if(standing)
                choice = "s";
            else if(hitOnce){
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
                    CLI.pause();

                    standing = true;
                }
                case "d" -> {
                    System.out.println("\nDouble or nothing!");

                    doubleDown((BlackJackPlayer) activePlayer);
                    standing = true;
                }
                case "bust" -> {
                    System.out.println("\nBust! :)");
                    CLI.pause();

                    standing = true;
                }
                case "l" -> {
                    leaveTable((BlackJackPlayer) activePlayer);

                    standing = true;
                }
            }
        }while(!standing);
    }

    private void placeBet(BlackJackPlayer player){
        System.out.println("\nPlace your bet:");

        int bet;
        if(player.getBalance() < 0)
            bet = (Input.getInt(0, -player.getBalance()));
        else
            bet = (Input.getInt(0, player.getBalance()));

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

    private void doubleDown(BlackJackPlayer player){
        player.setBet(player.getBet() * 2);

        System.out.printf("\n%-12s $%,d\n", "new bet:", player.getBet());
        System.out.printf("new balance: $%,d ($%,d - $%,d)\n",
                player.getBalance() - player.getBet(), player.getBalance(), player.getBet());

        if(!hit(player)) {
            showHand(player);
            CLI.pause();
        }
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
            System.out.printf("\n%s has left the table with $%,d.\n", player.name, player.getBalance());
        else if(player.getBalance() <= -10_000)
            System.out.printf("\n%s is off to remortgage their house.\n", player.name);
        else
            System.out.printf("\n%s owes the casino $%,d. They'll be well taken care of in the back room.\n",
                    player.name, -player.getBalance());

        CLI.pause();

        players.remove(player);
        playerLeft = true;
    }

    private void dealerTurn(){
        CLI.cls();
        System.out.println("- Dealer's turn -");
        CLI.pause();

        boolean standing = false;
        do{
            if(getHandValue(dealer) >= 17) {
                if(getHandValue(dealer) < 21)
                    System.out.println("Dealer stands.");
                CLI.pause();

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

        for(BlackJackPlayer player : players){
            int bet = player.getBet();
            int bonus = player.getBonus();
            int score = getHandValue(player);
            boolean fiveCardCharlie = player.hand.cards.size() == 5;

            boolean winner = score <= 21
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
}
