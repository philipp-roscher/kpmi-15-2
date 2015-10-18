package org.sausagepan.prototyp.model.components;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.TimeUtils;

import org.sausagepan.prototyp.Utils.UnitConverter;
import org.sausagepan.prototyp.model.Bullet;
import org.sausagepan.prototyp.model.ContainerMessage;
import org.sausagepan.prototyp.model.PlayerAttributeContainer;

import java.util.Iterator;

/**
 * Created by georg on 16.10.15.
 *
 * Player component for handling battle events.
 */
public class PlayerBattleComponent extends PlayerComponent {

    /* ............................................................................ ATTRIBUTES .. */
    private Pool<Bullet>    bulletPool;     // pool of available bullets
    private Array<Bullet>   activeBullets;  // bullets flying through the air right now
    private Rectangle       attackCollider; // represents the characters body in battles
    private boolean         attacking;      // whether the character is attacking at the moment

    private long lastAttack = 0;

    /* ........................................................................... CONSTRUCTOR .. */
    public PlayerBattleComponent(final PlayerAttributeContainer attributes) {
        super(attributes);
        this.activeBullets = new Array<Bullet>();
        this.attacking = false;
        this.attackCollider
                = new Rectangle(attributes.getNetPos().position.x-.35f,attributes.getNetPos().position.y-.5f,
                .7f,1);

        // Pools
        this.bulletPool = new Pool<Bullet>() {
            @Override
            protected Bullet newObject() {
                return new Bullet(attributes.getNetPos().position.x, attributes.getNetPos().position.y,
                        .1f, .1f,
                        attributes.getNormDir());
            }
        };
    }


    /* ............................................................................... METHODS .. */
    @Override
    public void update(float elapsedTime) {
        this.attackCollider.x = attributes.getPosition().x;
        this.attackCollider.y = attributes.getPosition().y;

        // update bullets
        updateBullets();
    }

    @Override
    public void update(ContainerMessage message) {
        // TODO
    }

    public void attack() {
        if(TimeUtils.timeSinceMillis(lastAttack) < 100) return;
        attacking = true;
        lastAttack = TimeUtils.millis();
    }

    public void stopAttacking() {
        this.attacking = false;
    }

    /**
     * Spawns new bullets
     */
    public void shoot() {
        System.out.println("Shoot!");
        if(TimeUtils.timeSinceMillis(lastAttack) < 100) return; // maximum 10 bullets per second
        Bullet newBullet = bulletPool.obtain();                 // obtain new bullet from pool
        newBullet.init(                                         // initialize obtained bullet
                attributes.getNetPos().position.x,
                attributes.getNetPos().position.y,
                attributes.getNormDir());
        activeBullets.add(newBullet);                           // add initialized bullet to active bullets
        lastAttack = TimeUtils.millis();                        // remember spawn time
    }

    /**
     * Updates bullets positions and returns them to the pool, if they reach the screens edge
     */
    public void updateBullets() {

        Iterator<Bullet> i = activeBullets.iterator();
        while (i.hasNext()) {
            Bullet b = i.next();
            b.x += Gdx.graphics.getDeltaTime() * 1 * b.direction.x;
            b.y += Gdx.graphics.getDeltaTime() * 1 * b.direction.y;

            if(b.x > UnitConverter.pixelsToMeters(800 * 10) ||
                    b.x < -1*UnitConverter.pixelsToMeters(800*10) ||
                    b.y > UnitConverter.pixelsToMeters(480*10) ||
                    b.y < -1*UnitConverter.pixelsToMeters(480*10)) {

                b.reset();
                i.remove();
            }
        }
    }

    public Array<Bullet> getActiveBullets() {
        return activeBullets;
    }

    public Rectangle getAttackCollider() {
        return attackCollider;
    }

    public void debugRenderer(ShapeRenderer shp) {
        shp.begin(ShapeRenderer.ShapeType.Line);
        shp.setColor(1,0,0,1);
        shp.rect(attackCollider.x,attackCollider.y,attackCollider.width,attackCollider.height);
        shp.end();
    }
}
