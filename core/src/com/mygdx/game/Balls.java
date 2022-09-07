package com.mygdx.game;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

import java.util.ArrayList;


public class Balls {
    public Body body;
    public Rectangle rectangle;
    public Vector2 size;

    public Balls(Body body, Rectangle rectangle, Vector2 size) {
        this.body = body;
        this.rectangle = rectangle;
        this.size = size;
    }

}
