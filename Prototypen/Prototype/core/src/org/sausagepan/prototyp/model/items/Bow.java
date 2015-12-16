package org.sausagepan.prototyp.model.items;

import java.util.Iterator;

import org.sausagepan.prototyp.Utils.UnitConverter;
import org.sausagepan.prototyp.enums.Damagetype;
import org.sausagepan.prototyp.graphics.EntitySprite;
import org.sausagepan.prototyp.model.Bullet;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.TimeUtils;

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
    public void shoot(Vector2 startPos, Vector2 dirVector, int bulletId) {
        if(TimeUtils.timeSinceMillis(lastAttack) < 100) return; // maximum 10 bullets per second
        Bullet newArrow = arrowPool.obtain();           // obtain new bullet from pool
        newArrow.init(startPos.x, startPos.y, dirVector);
        newArrow.id = bulletId;
        activeArrows.add(newArrow);                           // add initialized bullet to active bullets
        lastAttack = TimeUtils.millis();                // remember spawn time
    }

    /**
     * Move Arrows around
     */
    public void updateArrows(float delta) {
        for(Bullet b : activeArrows) {
            b.x += delta * b.direction.x;
            b.y += delta * b.direction.y;
        }
    }

    public int checkHit(Rectangle hittableArea) {
        Iterator<Bullet> i = activeArrows.iterator();
        while (i.hasNext()) {
            Bullet arrow = i.next();
            if(hittableArea.overlaps(arrow)) {
                arrow.reset();
                i.remove();
                return arrow.id;
            }
        }
        return -1;
    }

    public void deleteBullet(int bulletId) {
        Iterator<Bullet> i = activeArrows.iterator();
        while (i.hasNext()) {
            Bullet arrow = i.next();
            if(arrow.id == bulletId) {
                arrow.reset();
                i.remove();
                return;
            }
        }
    }
    /* ..................................................................... GETTERS & SETTERS .. */
}
