package com.actors;

import com.card.Hand;

public class PlayerWithCards implements Actor {
    public final String name;
    public final Hand hand;

    public PlayerWithCards(String name){
        this.name = name;

        hand = new Hand();
    }
}
