package org.sausagepan.prototyp.model.items;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.TimeUtils;

import org.sausagepan.prototyp.Utils.UnitConverter;
import org.sausagepan.prototyp.enums.Damagetype;
import org.sausagepan.prototyp.graphics.EntitySprite;
import org.sausagepan.prototyp.model.Bullet;

import java.util.Iterator;

/**
 * Created by georg on 02.11.15.
 */
public class Bow extends WeaponItem {
    /* ............................................................................ ATTRIBUTES .. */
    public Array<Bullet> activeArrows;
    public EntitySprite arrowSprite;
    public Pool<Bullet> arrowPool;
    private long lastAttack;
    /* ........................................................................... CONSTRUCTOR .. */
    public Bow(
            TextureRegion region, int strength, Damagetype damagetype, TextureRegion arrowTexture) {
        super(region, strength, damagetype);
        this.activeArrows = new Array<Bullet>();
        this.arrowSprite = new EntitySprite(arrowTexture);
        this.arrowSprite.setSize(
                UnitConverter.pixelsToMeters(this.arrowSprite.getRegionWidth()),
                UnitConverter.pixelsToMeters(this.arrowSprite.getRegionHeight())
        );
        this.lastAttack = 0;

        // TODO
        // Pools
        this.arrowPool = new Pool<Bullet>() {
            @Override
            protected Bullet newObject() {
                return new Bullet(0,0,.1f,.1f,new Vector2(0,0));
            }
        };
    }
    /* ............................................................................... METHODS .. */

    /**
     *
     * @param startPos  position of the firing character
     * @param dirVector direction in which activeArrows should fly
     */
    public void shoot(Vector2 startPos, Vector2 dirVector) {
        System.out.println("Shoot!");
        if(TimeUtils.timeSinceMillis(lastAttack) < 100) return; // maximum 10 bullets per second
        Bullet newArrow = arrowPool.obtain();           // obtain new bullet from pool
        newArrow.init(startPos.x, startPos.y, dirVector);
        activeArrows.add(newArrow);                           // add initialized bullet to active bullets
        lastAttack = TimeUtils.millis();                // remember spawn time
        System.out.println(activeArrows);
    }

    /**
     * Move Arrows around
     */
    public void updateArrows() {
        Iterator<Bullet> i = activeArrows.iterator();
        while (i.hasNext()) {
            Bullet b = i.next();
            b.x += Gdx.graphics.getDeltaTime() * 1 * b.direction.x;
            b.y += Gdx.graphics.getDeltaTime() * 1 * b.direction.y;

            // if arrows leave sight distance, remove them
            if(b.x > UnitConverter.pixelsToMeters(800 * 10) ||
                    b.x < -1*UnitConverter.pixelsToMeters(800*10) ||
                    b.y > UnitConverter.pixelsToMeters(480*10) ||
                    b.y < -1*UnitConverter.pixelsToMeters(480*10)) {
                b.reset();
                i.remove();
            }
        }
    }

    public boolean checkHit(Rectangle hittableArea) {
        Iterator<Bullet> i = activeArrows.iterator();
        while (i.hasNext()) {
            Bullet arrow = i.next();
            if(hittableArea.overlaps(arrow)) {
                System.out.println("Arrow hit something");
                arrow.reset();
                i.remove();
                return true;
            }
        }
        return false;
    }
    /* ..................................................................... GETTERS & SETTERS .. */
}
