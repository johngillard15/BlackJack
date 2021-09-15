package com.actors;

public class BlackJackPlayer extends PlayerWithCards {
    private int balance; // TODO: maybe Gambling interface/class?
    private int bet = 0;

    public BlackJackPlayer(String name){
        super(name);
        balance = 1_000;
    }

    public BlackJackPlayer(String name, int balance){
        super(name);
        this.balance = balance;
    }

    public int getBalance(){
        return balance;
    }

    public void setBalance(int balance){
        this.balance = balance;
    }

    public int getBet() {
        return bet;
    }

    public void setBet(int bet) {
        this.bet = bet;
        balance -= bet;
    }
}
