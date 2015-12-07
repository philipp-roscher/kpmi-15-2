package org.sausagepan.prototyp.managers;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;

import org.sausagepan.prototyp.model.Maze;
import org.sausagepan.prototyp.model.components.WeaponComponent;
import org.sausagepan.prototyp.model.items.Bow;

/**
 * Takes all {@link Entity}s capable of joining the battle and process their actions against each
 * other.
 * Created by Georg on 26.06.2015.
 */
public class BulletSystem extends ObservingEntitySystem {


    /* ............................................................................ ATTRIBUTES .. */
    private ObservableEngine engine;
    private Maze maze;

    private ImmutableArray<Entity> entities;

    private ComponentMapper<WeaponComponent> wm
            = ComponentMapper.getFor(WeaponComponent.class);

    BulletSystem(ObservableEngine engine, Maze maze) {
        this.engine = engine;
        this.maze = maze;
    }

    /* ............................................................................... METHODS .. */

    public void addedToEngine(ObservableEngine engine) {
        entities = engine.getEntitiesFor(Family.all(WeaponComponent.class).get());
    }

    public void update(float deltaTime) {
        // deletes bullets on collision with walls only!
        MapObjects mo = maze.getColliders();
        Rectangle r;
        for(Entity e : entities) {
            WeaponComponent weapon = wm.get(e);
            // Handle Bow
            if(weapon.weapon.getClass().equals(Bow.class)) {
                Bow bow = (Bow)weapon.weapon;
                for(MapObject m : mo) {
                    r = ((RectangleMapObject) m).getRectangle();
                    if(bow.checkHit(new Rectangle(r.x/32f, r.y/32f, r.width/32f, r.height/32f)) != -1)
                        System.out.println("Arrow hit a wall and was removed");
                }
            }
        }
    }

    /* ..................................................................... GETTERS & SETTERS .. */


}
