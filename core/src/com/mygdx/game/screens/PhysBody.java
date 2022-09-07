package com.mygdx.game.screens;

import com.badlogic.gdx.math.Vector2;

public class PhysBody {
    public Vector2 position;
    public Vector2 size;
    public String name;

    public PhysBody(String name, Vector2 position, Vector2 size){
        this.name = name;
        this.size = size;
        this.position = position;
    }
}
