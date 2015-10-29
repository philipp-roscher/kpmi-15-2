package org.sausagepan.prototyp.managers;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;

import org.sausagepan.prototyp.model.components.CharacterSpriteComponent;
import org.sausagepan.prototyp.model.components.DynamicBodyComponent;
import org.sausagepan.prototyp.model.components.InputComponent;
import org.sausagepan.prototyp.model.components.LightComponent;
import org.sausagepan.prototyp.model.components.WeaponComponent;

/**
 * Created by georg on 28.10.15.
 */
public class PositionSynchroSystem extends EntitySystem {
    /* ............................................................................ ATTRIBUTES .. */
    private ImmutableArray<Entity> entities;
    private float elapsedTime=0;

    private ComponentMapper<DynamicBodyComponent> pm
            = ComponentMapper.getFor(DynamicBodyComponent.class);
    private ComponentMapper<CharacterSpriteComponent> sm
            = ComponentMapper.getFor(CharacterSpriteComponent.class);
    private ComponentMapper<WeaponComponent> wm
            = ComponentMapper.getFor(WeaponComponent.class);
    private ComponentMapper<LightComponent> lm
            = ComponentMapper.getFor(LightComponent.class);
    /* ........................................................................... CONSTRUCTOR .. */
    public PositionSynchroSystem() {};

    /* ............................................................................... METHODS .. */
    /*
    Update method should synchronize positions of components

     */
    public void addedToEngine(Engine engine) {
        entities = engine.getEntitiesFor(Family.all(
                DynamicBodyComponent.class,
                InputComponent.class,
                CharacterSpriteComponent.class,
                LightComponent.class).get());
    }

    public void update(float deltaTime) {
        elapsedTime += deltaTime;
        for (Entity entity : entities) {
            DynamicBodyComponent body = pm.get(entity);
            WeaponComponent weapon = wm.get(entity);
            CharacterSpriteComponent sprite = sm.get(entity);
            LightComponent light = lm.get(entity);

            sprite.sprite.setPosition(
                    body.dynamicBody.getPosition().x - sprite.sprite.getWidth()/2,
                    body.dynamicBody.getPosition().y - body.fixture.getShape().getRadius()
            );

            weapon.sprite.setPosition(
                    body.dynamicBody.getPosition().x - sprite.sprite.getWidth()/2,
                    body.dynamicBody.getPosition().y
            );

            light.spriteLight.setPosition(
                    body.dynamicBody.getPosition().x,
                    body.dynamicBody.getPosition().y
            );


        }
    }
    
    /* ..................................................................... GETTERS & SETTERS .. */
}
