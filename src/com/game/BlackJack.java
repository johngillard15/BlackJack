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
 * @version 1.3.0
 */

public class BlackJack extends Game {
    private final Deck deck;
    private final Dealer dealer = new Dealer();
    private boolean iNeedHelp = false;
    private boolean dealerNaturalBlackjack = false;

    public BlackJack(){
        super();

        deck = new CheaterStandardDeck();
        deck.shuffle();

        setup();
    }

    public static int getValue(Card card){
        return switch(card.value){
            case "Ace" -> 11;
            case "Jack", "Queen", "King" -> 10;
            default -> Integer.parseInt(card.value);
        };
    }

    private int getHandValue(Hand hand){
        int handScore = 0;

        boolean hasAce = false;
        for(Card card : hand.cards){
            if(card.value.equals("Ace")){
                if(hasAce)
                    handScore += 1;
                else{
                    handScore += 11;
                    hasAce = true;
                }
            }
            else
                handScore += getValue(card);
        }

        if(hasAce && handScore > 21)
            handScore -= 10;

        return handScore;
    }

    private void draw(Hand hand){
        hand.addCard(deck.draw());
    }

    private void discard(Card card){
        deck.discard(card);
    }

    @Override
    public void play(){
        System.out.println("\n--- Welcome to BlackJack. Drinks are on the house. ---");
        CLI.pause();

        do{
            round();
        }while(players.size() > 0);
    }

    private void getFirstHand(Actor actor){
        actor.hands.add(new Hand());
        if(deck instanceof TestDeck)
            System.out.printf("%s draws...\n", actor.name);
        for(int i = 0; i < 2; i++)
            draw(actor.hands.get(0));
    }

    @Override
    protected void round(){
        getFirstHand(dealer);
        dealerNaturalBlackjack = getHandValue(dealer.hands.get(0)) == 21;

        for(Player player : players)
            getFirstHand(player);

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

        for(Player player : players)
            turn(player);

        if(players.size() > 0){
            if(!dealerNaturalBlackjack) dealerTurn();

            displayResults();

            clearTable();
            deck.shuffle();
        }
    }

    private void placeBet(Player player){
        final int LOAN = 1_000;

        if(player.getBalance() <= 0)
            System.out.printf("\nThe casino has graciously lent you $%,d to spend based on your situation.", LOAN);
        System.out.printf("\nBalance: %s %s\n",
                getFormattedBalance(player.getBalance()), player.getBalance() <= 0 ? "($1,000 loan)" : "");
        System.out.println("Place your bet:");

        int bet;
        if(player.getBalance() > 0)
            bet = (Input.getInt(1, player.getBalance()));
        else
            bet = (Input.getInt(1, LOAN));

        player.hands.get(0).setBet(bet);
    }

    @Override
    protected void turn(Player player){
        CLI.cls();
        String name = player.name;
        System.out.printf("- %s%s turn -\n",
                name, name.charAt(name.length() - 1) == 's' ? "'" : "'s");
        CLI.pause();

        System.out.println("\nDealer:"); // TODO: clean this up
        UI.showSideBySide(StandardDeck.getCardGUI(dealer.hands.get(0).cards.get(0)), StandardDeck.getBackCard());
        System.out.printf("Hand score: %s + ?\n", getValue(dealer.hands.get(0).cards.get(0)));

        for(int i = 0; i < player.hands.size(); i++){
            Hand hand = player.hands.get(i);
            int size;

            boolean canDouble = true;
            boolean canSplit;
            boolean splitWithAce = false;
            boolean turnFinished = false;
            do{
                size = player.hands.size();
                System.out.printf("\nYour hand%s:\n", size > 1 ? " (" + (i + 1) + " of " + size + ")" : "");
                showHand(hand);

                int totalBet = 0;
                for(Hand thisHand : player.hands)
                    totalBet += thisHand.getBet();

                System.out.printf("\ncurrent balance: %s (%s - $%,d)\n",
                        getFormattedBalance(player.getBalance() - totalBet),
                        getFormattedBalance(player.getBalance()),
                        totalBet);
                System.out.printf("%16s $%,d + $%,d\n", "this bet:",
                        hand.getBet(), hand.getBonus());

                if(hand.cards.size() == 2) {
                    if(getHandValue(hand) == 21){
                        blackjack(hand);
                        break;
                    }

                    if(size > 1){
                        Hand newHand = player.hands.get(player.hands.size() - 1);
                        if(hand.getCard(0).value.equals("Ace") && newHand.getCard(1).value.equals("Ace"))
                            splitWithAce = true;
                    }
                }

                canSplit = splittable(hand);
                System.out.printf("\nWould you like to (h)it%s%s, or (s)tand?\n",
                        canDouble ? ", (d)ouble down" : "", canDouble && canSplit ? ", (split)" : "");
                System.out.printf("̲hit%s%s, ̲stand ", canDouble ? ", ̲double" : "", canDouble && canSplit ? ", ̲split" : "");

                StringBuilder turnOptions = new StringBuilder("h--s--psst dealer--bust");
                if(canDouble){
                    turnOptions.append("--d");
                    if(canSplit) turnOptions.append("--split");
                }

                String choice = Input.getString(turnOptions.toString().split("--")).toLowerCase();
                switch(choice){
                    case "h", "psst dealer" -> {
                        if(deck instanceof CheaterStandardDeck && choice.equals("psst dealer")) iNeedHelp = true;

                        System.out.println("\nHit me!");
                        turnFinished = hit(hand) || getHandValue(hand) == 21;

                        if(!turnFinished){
                            if(splitWithAce){
                                System.out.println("\nSplit with Ace; turn finished.");
                                CLI.pause();
                                turnFinished = true;
                            }
                            else if(hand.cards.size() == 5 && getHandValue(hand) <= 21) {
                                fiveCardCharlie(hand);
                                turnFinished = true;
                            }
                            else
                                canDouble = false;
                        }
                    }
                    case "s" -> {
                        System.out.printf("\n%s stands%s.\n", player.name, size > 1 ? " on hand " + (i + 1) : "");
                        CLI.pause();

                        turnFinished = true;
                    }
                    case "d" -> {
                        System.out.println("\nDouble or nothing!");

                        doubleDown(hand);
                        System.out.printf("new balance: %s (%s - $%,d)\n",
                                getFormattedBalance(player.getBalance() - totalBet),
                                getFormattedBalance(player.getBalance()),
                                totalBet);

                        turnFinished = true;
                    }
                    case "split" -> {
                        split(player, hand);
                        if(hand.getCard(0).value.equals("Ace") && hand.getCard(1).value.equals("Ace")){
                            System.out.println("\nSplit with Ace; turn finished.");
                            CLI.pause();
                            turnFinished = true;
                        }
                    }
                    case "bust" -> {
                        System.out.println("\nBust! :)");
                        CLI.pause();

                        turnFinished = true;
                    }
                }
            }while(!turnFinished);
        }
    }

    private void showHand(Hand hand){
        hand.sortByValue();
        CardGUI.showHand(hand.cards);

        String handScore = Integer.toString(getHandValue(hand));
        if(getHandValue(hand) == 21)
            handScore = ANSI.GREEN + String.valueOf(getHandValue(hand)) + ANSI.RESET;
        else if(getHandValue(hand) > 21)
            handScore = ANSI.RED + String.valueOf(getHandValue(hand)) + ANSI.RESET;

        System.out.printf("Hand score: %s\n", handScore);
    }

    private boolean hit(Hand hand){
        if(iNeedHelp){
            hand.addCard(((CheaterStandardDeck) deck).cheatDraw());
            iNeedHelp = false;
        }
        else{
            System.out.println("\nDrawing...");
            draw(hand);
        }

        System.out.println("\nNew card:");
        CardGUI.showCard(hand.getCard(hand.cards.size() - 1));


        boolean busted = getHandValue(hand) > 21;
        if(busted)
            System.out.println("\nBust!");

        if(busted || getHandValue(hand) == 21)
            showHand(hand);

        CLI.pause();

        return busted;
    }

    private boolean splittable(Hand hand){
        return getValue(hand.getCard(0)) == getValue(hand.getCard(1));
    }

    private void split(Player player, Hand firstHand){
        player.split(firstHand);
        Hand newHand = player.hands.get(player.hands.size() - 1);

        newHand.addCard(firstHand.getCard(0));
        firstHand.removeCard(0);

        newHand.addCard(deck.draw());
        firstHand.addCard(deck.draw());
    }

    private void doubleDown(Hand hand){
        hand.setBet(hand.getBet() * 2);

        System.out.printf("\n%-12s $%,d\n", "new bet:", hand.getBet());

        if(!hit(hand) && getHandValue(hand) < 21){
            showHand(hand);
            CLI.pause();
        }
    }

    private void blackjack(Hand hand){
        System.out.println("\nBlackJack! (x1.5 winnings)");
        hand.setBonus((int)(hand.getBet() * 0.5));

        String formattedBonus = ANSI.GREEN + String.format("$%,d", hand.getBonus()) + ANSI.RESET;
        System.out.printf("\nnew bet: $%,d + %s\n", hand.getBet(), formattedBonus);
        CLI.pause();
    }

    private void fiveCardCharlie(Hand hand){
        System.out.println("\n5 Card Charlie! (5 to 1 winnings)");
        hand.setBonus(hand.getBet() * 4);

        String formattedBonus = ANSI.GREEN + String.format("$%,d", hand.getBonus()) + ANSI.RESET;
        System.out.printf("\nnew bet: $%,d + %s\n", hand.getBet(), formattedBonus);
        CLI.pause();
    }

    private void dealerTurn(){
        CLI.cls();
        System.out.println("- Dealer's turn -");
        CLI.pause();

        for(Hand hand : dealer.hands){
            boolean standing = false;
            do{
                if(getHandValue(hand) < 17)
                    hit(hand);
                else{
                    if (getHandValue(hand) < 21){
                        System.out.println("Dealer stands.");
                        CLI.pause();
                    }
                    standing = true;
                }
            }while(!standing);
        }
    }

    @Override
    protected void displayResults(){
        CLI.cls();
        System.out.println("\n- Round Results -\n");

        System.out.println("\nDealer's hand:");
        showHand(dealer.hands.get(0));
        if(dealerNaturalBlackjack)
            System.out.println("\nDealer has BlackJack!");

        int dealerScore = getHandValue(dealer.hands.get(0));

        for(Player player : players){
            String namesEndingWithAnS = player.name.charAt(player.name.length() - 1) == 's' ? "'" : "'s";
            System.out.printf("\n- %s%s results -\n", player.name, namesEndingWithAnS);

            int oldBalance = player.getBalance();
            boolean split = player.hands.size() > 1;

            for(int i = 0; i < player.hands.size(); i++){
                Hand hand = player.hands.get(i);

                int bet = hand.getBet();
                int bonus = hand.getBonus();
                int score = getHandValue(hand);
                boolean busted = score > 21;
                boolean fiveCardCharlie = hand.cards.size() == 5 && !busted;
                boolean naturalBlackJack = !split && score == 21 && hand.cards.size() == 2;

                boolean winner = !busted
                        && !dealerNaturalBlackjack
                        && (dealerScore > 21 || (fiveCardCharlie || naturalBlackJack || score > dealerScore));

                String specialHand = fiveCardCharlie ? "(5 Card Charlie)"
                        : naturalBlackJack ? "(BlackJack)"
                        : "";

                System.out.printf("Hand%s score: %,d %s\n",
                        split ? " " + (i + 1) : "", getHandValue(hand), specialHand);

                if(winner){
                    player.wonBet(hand);
                    System.out.printf("%s has won $%,d%s\n",
                            player.name, bet + bonus, split ? " on hand " + (i + 1) : "");
                }
                else if(!busted && score == dealerScore){
                    if(dealerNaturalBlackjack && !naturalBlackJack){
                        player.lostBet(hand);
                        System.out.printf("%s has lost $%,d%s\n",
                                player.name, bet, split ? " on hand " + (i + 1) : "");
                    }
                    else{
                        hand.resetBet();
                        System.out.printf("%s Pushed%s\n", player.name, split ? " on hand " + (i + 1) : "");
                    }
                }
                else{
                    player.lostBet(hand);
                    System.out.printf("%s has lost $%,d%s\n",
                            player.name, bet, split ? " on hand " + (i + 1) : "");
                }
            }

            if(player.getBalance() != oldBalance){
                System.out.printf("\nOld balance: $%,d\nNew balance: %s\n",
                        oldBalance, getFormattedBalance(player.getBalance()));
            }
            else
                System.out.printf("\nBalance: %s\n", getFormattedBalance(player.getBalance()));

            CLI.pause();
        }
    }

    private String getFormattedBalance(int balance){
        return String.format("$%s",
                balance < 0 ? ANSI.format(String.format("%,d", balance), ANSI.RED)
                        : String.format("%,d", balance));
    }

    private void clearHand(Hand hand){
        while(hand.cards.size() > 0){
            Card card = hand.getCard(0);
            hand.removeCard(card);
            discard(card);
        }
    }

    private void clearTable(){
        for(Hand hand : dealer.hands)
            clearHand(hand);
        dealer.hands.clear();

        for(Actor player : players){
            for(Hand hand : player.hands)
                clearHand(hand);
            player.hands.clear();
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

        for(Hand hand : player.hands)
            clearHand(hand);

        players.remove(player);
    }
}
