package com.actors;

import com.card.Hand;

public abstract class Actor {
    public final String name;
    public final Hand hand;

    public Actor(String name){
        this.name = name;
        hand = new Hand();
    }
}
