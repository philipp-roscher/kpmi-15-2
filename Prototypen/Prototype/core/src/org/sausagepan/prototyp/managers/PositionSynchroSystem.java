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
import org.sausagepan.prototyp.model.entities.MonsterEntity;

/**
 * Created by georg on 28.10.15.
 */
public class PositionSynchroSystem extends EntitySystem {
    /* ............................................................................ ATTRIBUTES .. */
    private ImmutableArray<Entity> entities;

    private ComponentMapper<DynamicBodyComponent> pm
            = ComponentMapper.getFor(DynamicBodyComponent.class);
    private ComponentMapper<CharacterSpriteComponent> sm
            = ComponentMapper.getFor(CharacterSpriteComponent.class);
    private ComponentMapper<WeaponComponent> wm
            = ComponentMapper.getFor(WeaponComponent.class);
    private ComponentMapper<LightComponent> lm
            = ComponentMapper.getFor(LightComponent.class);
    private ComponentMapper<InputComponent> im
            = ComponentMapper.getFor(InputComponent.class);
    /* ........................................................................... CONSTRUCTOR .. */
    public PositionSynchroSystem() {};

    /* ............................................................................... METHODS .. */
    /*
    Update method should synchronize positions of components

     */
    public void addedToEngine(Engine engine) {
        entities = engine.getEntitiesFor(Family.all(
                DynamicBodyComponent.class).one(
                LightComponent.class,
                CharacterSpriteComponent.class,
                WeaponComponent.class,
                InputComponent.class
        ).get());
    }

    public void update(float deltaTime) {
        for (Entity entity : entities) {
            DynamicBodyComponent body = pm.get(entity);

            if(entity.getComponent(CharacterSpriteComponent.class) != null) {
                CharacterSpriteComponent sprite = sm.get(entity);
                sprite.sprite.setPosition(
                        body.dynamicBody.getPosition().x - sprite.sprite.getWidth()/2,
                        body.dynamicBody.getPosition().y - body.fixture.getShape().getRadius()
                );
            }
            if(entity.getComponent(WeaponComponent.class) != null &&
                    entity.getComponent(InputComponent.class) != null) {
                WeaponComponent weapon = wm.get(entity);
                InputComponent input = im.get(entity);

                switch(input.direction) {
                    case NORTH:
                        weapon.sprite.setPosition(
                                body.dynamicBody.getPosition().x - weapon.sprite.getWidth()/2,
                                body.dynamicBody.getPosition().y + .5f
                        );break;
                    case WEST:
                        weapon.sprite.setPosition(
                                body.dynamicBody.getPosition().x - weapon.sprite.getWidth() - .5f,
                                body.dynamicBody.getPosition().y - weapon.sprite.getHeight()/2
                        );break;
                    case EAST:
                        weapon.sprite.setPosition(
                                body.dynamicBody.getPosition().x + .5f,
                                body.dynamicBody.getPosition().y - weapon.sprite.getHeight()/2
                        );break;
                    default:
                        weapon.sprite.setPosition(
                                body.dynamicBody.getPosition().x - weapon.sprite.getWidth()/2,
                                body.dynamicBody.getPosition().y - .5f
                                        - weapon.sprite.getHeight()
                        );break;
                }
                weapon.sprite.setOriginCenter();
            }
            if(entity.getComponent(LightComponent.class) != null) {
                LightComponent light = lm.get(entity);

                light.spriteLight.setPosition(
                        body.dynamicBody.getPosition().x,
                        body.dynamicBody.getPosition().y
                );
            }
        }
    }
    
    /* ..................................................................... GETTERS & SETTERS .. */
}
