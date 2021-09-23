package com.actors;

import com.card.Hand;

import java.util.ArrayList;
import java.util.List;

public abstract class Actor {
    public final String name;
    public final List<Hand> hands;

    public Actor(String name){
        this.name = name;
        hands = new ArrayList<>();
    }
}
