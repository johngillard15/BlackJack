package com.game;

import com.actors.Dealer;
import com.actors.PlayerWithCards;
import com.actors.BlackJackPlayer;
import com.card.Card;
import com.card.CheaterStandardDeck;
import com.card.Deck;
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
 * @version 0.7.0
 */

public class BlackJack extends Game{
    private final Deck deck; // TODO: multiple decks in blackjack? (new Deck class maybe)
    private final List<BlackJackPlayer> players = new ArrayList<>();
    private final Dealer dealer = new Dealer();
    private int[] tableBets = new int[0];
    private boolean iNeedHelp = false;

    public BlackJack(){
        super(1, 4);

        deck = new CheaterStandardDeck();
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
//        do{
//            round();
//        }while(players.size() > 0);
        round();
    }

    @Override
    protected void round(){
        draw(dealer, 2);

        for(PlayerWithCards player : players)
            draw(player, 2);

        for(PlayerWithCards player : players)
            turn(player);

        dealerTurn();

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

        System.out.println("\nDealer:");
        System.out.println(dealer.hand.cards.get(0));
        System.out.println("? of ?"); // TODO: dealer's face down card, get fancy cards working and make a face down card
        System.out.printf("Hand score: %s + ?\n", getValue(dealer.hand.cards.get(0)));

        boolean hitOnce = false;
        boolean standing = false;
        do{
            showHand(activePlayer);

            System.out.printf("\ncurrent balance: $%,d\n", ((BlackJackPlayer)activePlayer).getBalance());
            System.out.printf("%16s $%,d\n", "this bet:", ((BlackJackPlayer)activePlayer).getBet());

            String choice;
            if(hitOnce){
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

                    System.out.println("Hit me!");
                    hitOnce = true;
                    standing = hit(activePlayer) || getHandValue(activePlayer) == 21;

                    if(getHandValue(activePlayer) == 21){
                        showHand(activePlayer);
                        CLI.pause();
                    }
                }
                case "s" -> {
                    System.out.println("Stand.");
                    standing = true;
                }
                case "d" -> {
                    System.out.println("Double or nothing!");

                    doubleBet((BlackJackPlayer) activePlayer);
                    standing = true;
                }
                case "bust" -> {
                    System.out.println("Bust! :)");
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
                System.out.println("Dealer stands.");
                standing = true;
            }
            else
                hit(dealer);
        }while(!standing);

        showHand(dealer);
    }

    private void placeBet(BlackJackPlayer player){
        System.out.println("\nPlace your bet:");

        int bet = (Input.getInt(0, player.getBalance()));
        player.setBet(bet);
    }

    private void showHand(PlayerWithCards player){
        System.out.printf("\n%s'%s cards:",
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
        System.out.printf("%s drew a%s %s\n",
                player.name, (newCard.value.equals("Ace") || newCard.value.equals("8") ? "n" : ""), newCard);

        if(getHandValue(player) > 21){
            showHand(player);
            System.out.println("Bust!");
            ((BlackJackPlayer)player).setBet(0);
            return true;
        }

        return false;
    }

    private void doubleBet(BlackJackPlayer player){
        int initialBet = player.getBet();
        player.setBalance(player.getBalance() + initialBet);
        player.setBet(initialBet * 2);

        System.out.printf("\nnew balance: %,d\n", player.getBalance());
        System.out.printf("%-12s %,d\n", "new bet:", player.getBet());

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
