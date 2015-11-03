package org.sausagepan.prototyp.model.items;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.TimeUtils;

import org.sausagepan.prototyp.enums.Damagetype;
import org.sausagepan.prototyp.graphics.EntitySprite;
import org.sausagepan.prototyp.model.Bullet;

/**
 * Created by georg on 02.11.15.
 */
public class Bow extends WeaponItem {
    /* ............................................................................ ATTRIBUTES .. */
    public Array<Rectangle> arrows;
    public EntitySprite arrowSprite;
    public Pool<Bullet> arrowPool;
    /* ........................................................................... CONSTRUCTOR .. */
    public Bow(
            TextureRegion region, int strength, Damagetype damagetype, TextureRegion arrowTexture) {
        super(region, strength, damagetype);
        this.arrows = new Array<Rectangle>();
        this.sprite = new EntitySprite(arrowTexture);

        // TODO
        // Pools
        this.arrowPool = new Pool<Bullet>() {
            @Override
            protected Bullet newObject() {
                return new Bullet(1,1,1,1,new Vector2());
            }
        };
    }
    /* ............................................................................... METHODS .. */

    /**
     *
     * @param startPos  position of the firing character
     * @param dirVector direction in which arrows should fly
     */
    public void shoot(Vector2 startPos, Vector2 dirVector) {
        System.out.println("Shoot!");
        //if(TimeUtils.timeSinceMillis(lastAttack) < 100) return; // maximum 10 bullets per second
        //Bullet newBullet = bulletPool.obtain();                 // obtain new bullet from pool
        //newBullet.init();
        //activeBullets.add(newBullet);                           // add initialized bullet to
        // active bullets
        //lastAttack = TimeUtils.millis();                        // remember spawn time
    }
    /* ..................................................................... GETTERS & SETTERS .. */
}
