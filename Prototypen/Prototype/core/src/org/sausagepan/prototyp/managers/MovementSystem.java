package org.sausagepan.prototyp.managers;

import org.sausagepan.prototyp.model.components.DynamicBodyComponent;
import org.sausagepan.prototyp.model.entities.EntityFamilies;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.ai.steer.behaviors.Seek;

/**
 * Created by georg on 21.10.15.
 */
public class MovementSystem extends EntitySystem implements EntityListener {
    /* ............................................................................ ATTRIBUTES .. */
    private ImmutableArray<Entity> entities;
    private float elapsedTime;
    private World world;
    private Seek seek;
    /* ........................................................................... CONSTRUCTOR .. */


    private ComponentMapper<DynamicBodyComponent> pm
            = ComponentMapper.getFor(DynamicBodyComponent.class);

    public MovementSystem(World world) {
    	this.world = world;
    	this.elapsedTime = 0;
    }

    /* ............................................................................... METHODS .. */
    public void addedToEngine(Engine engine) {
        entities = engine.getEntitiesFor(EntityFamilies.monsterFamily);
    }

    public void update(float deltaTime) {
        elapsedTime += deltaTime;
        for (Entity entity : entities) {
            DynamicBodyComponent body = pm.get(entity);
            body.dynamicBody.setLinearVelocity(
                    MathUtils.sin(elapsedTime),
                    MathUtils.cos(elapsedTime));
        }
    }

    @Override
    public void entityAdded(Entity entity) {
        addedToEngine(this.getEngine());
    }

    @Override
    public void entityRemoved(Entity entity) {
        addedToEngine(this.getEngine());
    }

    /* ..................................................................... GETTERS & SETTERS .. */
}
