package org.sausagepan.prototyp.model;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by Georg on 26.06.2015.
 */
public class Bullet extends Rectangle {
    public Vector2 direction;

    public Bullet (float x, float y, float width, float height, Vector2 direction) {
        super(x,y,width,height);
        this.direction = new Vector2(direction.x, direction.y);
    }
}
