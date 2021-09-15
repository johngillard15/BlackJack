package com.game;

import com.actors.Actor;
import com.actors.PlayerWithCards;
import com.utilities.Input;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>Provides a template for a text based console game.</p>
 *
 * @since 13/8/2021
 * @author John
 * @version 1.2.0
 */

public abstract class Game {
    protected List<Actor> players = new ArrayList<>();
    public final int MIN_PLAYERS;
    public final int MAX_PLAYERS;

    public Game(){
        MIN_PLAYERS = 1;
        MAX_PLAYERS = -1;
    }

    public Game(int MIN_PLAYERS){
        this.MIN_PLAYERS = MIN_PLAYERS;
        MAX_PLAYERS = -1;
    }

    public Game(int MIN_PLAYERS, int MAX_PLAYERS){
        this.MIN_PLAYERS = MIN_PLAYERS;
        this.MAX_PLAYERS = MAX_PLAYERS;
    }

    protected void setup(){
        getPlayers(getPlayerCount());
    }

    protected void getPlayers(int numPlayers){
        do{
            System.out.printf("\nPlayer %d, what is your name?\n", players.size() + 1);
            String name = Input.getString();
            System.out.printf("Hello, %s.\n", name);

            players.add(new PlayerWithCards(name));
        }while(players.size() < numPlayers);
    }

    protected int getPlayerCount(){
        System.out.println("How many players will there be?");
        System.out.print("players ");
        return MAX_PLAYERS == -1 ? Input.getInt(MIN_PLAYERS) : Input.getInt(MIN_PLAYERS, MAX_PLAYERS);
    }

    public abstract void play();
    protected abstract void round();
    protected abstract void turn(PlayerWithCards activePlayer);
    protected abstract void displayResults();
}