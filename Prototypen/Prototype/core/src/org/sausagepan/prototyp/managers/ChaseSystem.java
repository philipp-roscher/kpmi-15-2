package org.sausagepan.prototyp.managers;

import org.sausagepan.prototyp.Utils.CompMappers;
import org.sausagepan.prototyp.model.components.ChaseComponent;
import org.sausagepan.prototyp.model.components.DynamicBodyComponent;
import org.sausagepan.prototyp.model.components.InputComponent;
import org.sausagepan.prototyp.model.components.WeaponComponent;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.Vector2;


/**
 * Created by georg on 15.12.15.
 */
public class ChaseSystem extends EntitySystem implements EntityListener {

    /* ............................................................................ ATTRIBUTES .. */
    private ImmutableArray<Entity> entities;
    /* ........................................................................... CONSTRUCTOR .. */
    public ChaseSystem(){}
    /* ............................................................................... METHODS .. */
    @SuppressWarnings("unchecked")
    public void addedToEngine(Engine engine) {
        entities = engine.getEntitiesFor(Family.all(
                ChaseComponent.class, DynamicBodyComponent.class)
                .get());
    }

    public void update(float deltaTime) {
        for(Entity e : entities) {
            // Follow enemy
            DynamicBodyComponent dbc = CompMappers.dynBody.get(e);
            ChaseComponent chc = CompMappers.chase.get(e);
            //System.out.println(dbc.dynamicBody.getPosition());
            //System.out.println(chc.body.getPosition());

            Vector2 distance = new Vector2(
                    chc.body.getPosition().x - dbc.dynamicBody.getPosition().x,
                    chc.body.getPosition().y - dbc.dynamicBody.getPosition().y
                    );
            
            //start attacking
            if ( Math.abs(distance.len()) <= 1.5f) {
                //System.out.println("in Attack radius");
                e.getComponent(WeaponComponent.class).weapon.justUsed = true;
                e.getComponent(InputComponent.class).weaponDrawn = true;
            }

            //stop attacking
            if ( Math.abs(distance.len()) >= 1.5f) {
                //System.out.println("out of attack radius");
                e.getComponent(WeaponComponent.class).weapon.justUsed = false;
                e.getComponent(InputComponent.class).weaponDrawn = false;
            }

            distance.nor();
            distance.x *= 2;
            distance.y *= 2;

            dbc.dynamicBody.setLinearVelocity(distance);
        }
    }


    /**
     * Called whenever an {@link Entity} is added to {@link Engine} or a specific {@link Family} See
     * {@link Engine#addEntityListener(EntityListener)} and {@link Engine#addEntityListener(Family, EntityListener)}
     *
     * @param entity
     */
    @Override
    public void entityAdded(Entity entity) {
        addedToEngine(this.getEngine());
        //System.out.println("Following");
    }

    /**
     * Called whenever an {@link Entity} is removed from {@link Engine} or a specific {@link Family} See
     * {@link Engine#addEntityListener(EntityListener)} and {@link Engine#addEntityListener(Family, EntityListener)}
     *
     * @param entity
     */
    @Override
    public void entityRemoved(Entity entity) {
        CompMappers.dynBody.get(entity).dynamicBody.setLinearVelocity(0,0);
        addedToEngine(this.getEngine());
        //System.out.println("Giving up");
    }
    /* ..................................................................... GETTERS & SETTERS .. */
}
