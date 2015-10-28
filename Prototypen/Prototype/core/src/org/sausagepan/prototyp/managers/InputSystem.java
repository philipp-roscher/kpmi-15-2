package org.sausagepan.prototyp.managers;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.MathUtils;

import org.sausagepan.prototyp.model.components.DynamicBodyComponent;
import org.sausagepan.prototyp.model.components.InputComponent;
import org.sausagepan.prototyp.model.components.SpriteComponent;
import org.sausagepan.prototyp.model.components.VelocityComponent;

/**
 * Created by georg on 28.10.15.
 */
public class InputSystem extends EntitySystem {
    /* ............................................................................ ATTRIBUTES .. */
    private ImmutableArray<Entity> entities;
    private float elapsedTime=0;

    private ComponentMapper<DynamicBodyComponent> pm
            = ComponentMapper.getFor(DynamicBodyComponent.class);
    private ComponentMapper<InputComponent> im
            = ComponentMapper.getFor(InputComponent.class);
    private ComponentMapper<SpriteComponent> sm
            = ComponentMapper.getFor(SpriteComponent.class);

    /* ........................................................................... CONSTRUCTOR .. */
    public InputSystem() {}
    
    /* ............................................................................... METHODS .. */
    public void addedToEngine(Engine engine) {
        entities = engine.getEntitiesFor(Family.all(
                DynamicBodyComponent.class,
                InputComponent.class,
                SpriteComponent.class).get());
    }

    public void update(float deltaTime) {
        elapsedTime += deltaTime;
        for (Entity entity : entities) {
            DynamicBodyComponent body = pm.get(entity);
            InputComponent input = im.get(entity);
            SpriteComponent sprite = sm.get(entity);
            if(input.moving)
                switch(input.direction) {
                    case NORTH: body.dynamicBody.setLinearVelocity(0,10);break;
                    case SOUTH: body.dynamicBody.setLinearVelocity(0,-10);break;
                    case EAST: body.dynamicBody.setLinearVelocity(10,0);break;
                    case WEST: body.dynamicBody.setLinearVelocity(-10,0);break;
                    default: body.dynamicBody.setLinearVelocity(0,0);break;
                }
            else body.dynamicBody.setLinearVelocity(0,0);
            sprite.sprite.setPosition(
                    body.dynamicBody.getPosition().x,
                    body.dynamicBody.getPosition().y
            );
        }
    }
    
    /* ..................................................................... GETTERS & SETTERS .. */
}
