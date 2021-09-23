package com.actors;

import com.card.Card;
import com.card.Hand;

public class Player extends Actor {
    private int balance; // TODO: maybe Gambling interface/class?

    public Player(String name){
        super(name);
        balance = 1_000;
    }

    public Player(String name, int balance){
        super(name);
        this.balance = balance;
    }

    public void addCard(Card card, int index){
        hands.get(index).addCard(card);
    }

    public int getBalance(){
        return balance;
    }

    public void setBalance(int balance){
        this.balance = balance;
    }

    public void wonBet(Hand hand){
        balance += hand.getBet() + hand.getBonus();
        hand.resetBet();
    }

    public void wonBet(int index){
        balance += hands.get(index).getBet() + hands.get(index).getBonus();
        hands.get(index).resetBet();
    }

    public void lostBet(Hand hand){
        balance -= hand.getBet();
        hand.resetBet();
    }

    public void lostBet(int index){
        balance -= hands.get(index).getBet();
        hands.get(index).resetBet();
    }
}
