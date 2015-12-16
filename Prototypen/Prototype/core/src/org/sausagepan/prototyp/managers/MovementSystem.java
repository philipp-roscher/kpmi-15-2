package org.sausagepan.prototyp.managers;

import org.sausagepan.prototyp.Utils.CompMappers;
import org.sausagepan.prototyp.enums.Direction;
import org.sausagepan.prototyp.model.components.DynamicBodyComponent;
import org.sausagepan.prototyp.model.components.InputComponent;
import org.sausagepan.prototyp.model.entities.EntityFamilies;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

/**
 * Created by georg on 21.10.15.
 */
public class MovementSystem extends EntitySystem implements EntityListener {
    /* ............................................................................ ATTRIBUTES .. */
	private ImmutableArray<Entity> movementEntities;
	private ImmutableArray<Entity> entities;
    private float elapsedTime;
    /* ........................................................................... CONSTRUCTOR .. */


    private ComponentMapper<DynamicBodyComponent> pm
            = ComponentMapper.getFor(DynamicBodyComponent.class);

    public MovementSystem(World world) {
    	this.elapsedTime = 0;
    }

    /* ............................................................................... METHODS .. */
    public void addedToEngine(Engine engine) {
        movementEntities = engine.getEntitiesFor(EntityFamilies.monsterMovementFamily);
        entities = engine.getEntitiesFor(EntityFamilies.monsterFamily);
    }

    public void update(float deltaTime) {
        elapsedTime += deltaTime;
        
        // move monsters without ChaseComponent
        for (Entity entity : movementEntities) {
            DynamicBodyComponent body = pm.get(entity);
            body.dynamicBody.setLinearVelocity(
                    MathUtils.sin(elapsedTime),
                    MathUtils.cos(elapsedTime));
        }

        // update direction of ALL monsters, even those with ChaseComponent
        for (Entity entity : entities) {
            DynamicBodyComponent body = pm.get(entity);
            InputComponent input = CompMappers.input.get(entity);
        	Vector2 velocity = body.dynamicBody.getLinearVelocity();
        	
            if(Math.abs(velocity.x) > Math.abs(velocity.y)) {
                if(velocity.x > 0) input.direction = Direction.EAST;
                else input.direction = Direction.WEST;
            } else {
                if(velocity.y > 0) input.direction = Direction.NORTH;
                else input.direction = Direction.SOUTH;
            }
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
