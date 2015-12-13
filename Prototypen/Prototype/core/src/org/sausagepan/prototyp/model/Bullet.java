package org.sausagepan.prototyp.model;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;

/**
 * Created by Georg on 26.06.2015.
 */
public class Bullet extends Rectangle implements Pool.Poolable {
    private static final long serialVersionUID = 1L;

    public int id;
    public Vector2 direction;

    public Bullet (float x, float y, float width, float height, Vector2 direction) {
        super(x,y,width,height);
        this.direction = new Vector2(direction.x, direction.y);
    }

    @Override
    public void reset() { }

    public void init(float x, float y, Vector2 direction) {
        this.direction.x = direction.x;
        this.direction.y = direction.y;
        super.x = x;
        super.y = y;
    }
}
