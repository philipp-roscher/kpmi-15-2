package org.sausagepan.prototyp.managers;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.TimeUtils;

import org.sausagepan.prototyp.model.components.DynamicBodyComponent;
import org.sausagepan.prototyp.model.components.SpriteComponent;
import org.sausagepan.prototyp.model.components.VelocityComponent;

/**
 * Created by georg on 21.10.15.
 */
public class MovementSystem extends EntitySystem {
    /* ............................................................................ ATTRIBUTES .. */
    private ImmutableArray<Entity> entities;
    private float elapsedTime=0;
    /* ........................................................................... CONSTRUCTOR .. */


    private ComponentMapper<DynamicBodyComponent> pm
            = ComponentMapper.getFor(DynamicBodyComponent.class);
    private ComponentMapper<VelocityComponent> vm
            = ComponentMapper.getFor(VelocityComponent.class);
    private ComponentMapper<SpriteComponent> sm
            = ComponentMapper.getFor(SpriteComponent.class);

    public MovementSystem() {}

    /* ............................................................................... METHODS .. */
    public void addedToEngine(Engine engine) {
        entities = engine.getEntitiesFor(Family.all(
                DynamicBodyComponent.class,
                VelocityComponent.class,
                SpriteComponent.class).get());
    }

    public void update(float deltaTime) {
        elapsedTime += deltaTime;
        for (Entity entity : entities) {
            DynamicBodyComponent body = pm.get(entity);
            VelocityComponent velocity = vm.get(entity);
            SpriteComponent sprite = sm.get(entity);
            body.dynamicBody.setLinearVelocity(
                    MathUtils.sin(elapsedTime),
                    MathUtils.cos(elapsedTime));
            sprite.sprite.setPosition(
                    body.dynamicBody.getPosition().x - sprite.sprite.getWidth() / 2,
                    body.dynamicBody.getPosition().y - sprite.sprite.getHeight() / 2);
        }
    }

    /* ..................................................................... GETTERS & SETTERS .. */
}
