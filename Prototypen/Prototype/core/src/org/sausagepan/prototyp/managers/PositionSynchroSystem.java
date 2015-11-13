package org.sausagepan.prototyp.managers;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import org.sausagepan.prototyp.model.components.CharacterSpriteComponent;
import org.sausagepan.prototyp.model.components.DynamicBodyComponent;
import org.sausagepan.prototyp.model.components.InjurableAreaComponent;
import org.sausagepan.prototyp.model.components.InputComponent;
import org.sausagepan.prototyp.model.components.LightComponent;
import org.sausagepan.prototyp.model.components.WeaponComponent;
import org.sausagepan.prototyp.model.entities.MonsterEntity;
import org.sausagepan.prototyp.model.items.Sword;

/**
 * Created by georg on 28.10.15.
 */
public class PositionSynchroSystem extends ObservingEntitySystem {
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
    private ComponentMapper<InjurableAreaComponent> jm
            = ComponentMapper.getFor(InjurableAreaComponent.class);
    /* ........................................................................... CONSTRUCTOR .. */
    public PositionSynchroSystem() {};

    /* ............................................................................... METHODS .. */
    /*
    Update method should synchronize positions of components

     */
    public void addedToEngine(ObservableEngine engine) {
        entities = engine.getEntitiesFor(Family.all(
                DynamicBodyComponent.class).one(
                LightComponent.class,
                CharacterSpriteComponent.class,
                WeaponComponent.class,
                InputComponent.class,
                InjurableAreaComponent.class
        ).get());
    }

    public void update(float deltaTime) {
        for (Entity entity : entities) {
            DynamicBodyComponent body = pm.get(entity);

            // Synchronize CharacterSprite with DynamicBody
            if(entity.getComponent(CharacterSpriteComponent.class) != null) {
                CharacterSpriteComponent sprite = sm.get(entity);
                sprite.sprite.setPosition(
                        body.dynamicBody.getPosition().x - sprite.sprite.getWidth()/2,
                        body.dynamicBody.getPosition().y - body.fixture.getShape().getRadius()
                );
            }

            // Synchronize Weapon with DynamicBody
            if(entity.getComponent(WeaponComponent.class) != null &&
                    entity.getComponent(InputComponent.class) != null) {
                WeaponComponent weapon = wm.get(entity);
                InputComponent input = im.get(entity);

                switch(input.direction) {
                    case NORTH:
                        weapon.weapon.sprite.setPosition(body.dynamicBody.getPosition().x
                                        - weapon.weapon.sprite.getWidth() / 2,
                                body.dynamicBody .getPosition().y + .5f);break;
                    case WEST:
                        weapon.weapon.sprite.setPosition(body.dynamicBody.getPosition().x
                                        - weapon.weapon.sprite.getWidth() - .5f,
                                body.dynamicBody.getPosition().y
                                        - weapon.weapon.sprite.getHeight()/2);break;
                    case EAST:
                        weapon.weapon.sprite.setPosition(
                                body.dynamicBody.getPosition().x + .5f,
                                body.dynamicBody.getPosition().y
                                        - weapon.weapon.sprite.getHeight()/2);break;
                    default:
                        weapon.weapon.sprite.setPosition(body.dynamicBody.getPosition().x
                                        - weapon.weapon.sprite.getWidth()/2,
                                body.dynamicBody.getPosition().y - .5f
                                        - weapon.weapon.sprite.getHeight());break;
                }
                weapon.weapon.sprite.setOriginCenter();

                // Synchronize Swords DamageArea with DynamicBody
                if(weapon.weapon.getClass().equals(Sword.class)) {
                    // Set Swords damage Area
                    Rectangle rect = ((Sword)weapon.weapon).damageArea;
                    rect.setPosition(body.dynamicBody.getPosition());
                    switch (input.direction) {
                        case NORTH:
                            rect.x -= rect.width / 2;
                            rect.y += .5f;
                            break;
                        case WEST:
                            rect.x -= 1.f;
                            rect.y -= rect.height / 2;
                            break;
                        case EAST:
                            rect.x += .5f;
                            rect.y -= rect.height / 2;
                            break;
                        default:
                            rect.x -= rect.width / 2;
                            rect.y -= 1.f;
                            break;
                    }
                }
            }

            // Synchronize Light with DynamicBody
            if(entity.getComponent(LightComponent.class) != null) {
                LightComponent light = lm.get(entity);

                light.spriteLight.setPosition(
                        body.dynamicBody.getPosition().x,
                        body.dynamicBody.getPosition().y
                );
            }

            // Synchronize InjurableArea with DynamicBody
            if(entity.getComponent(InjurableAreaComponent.class) != null) {
                InjurableAreaComponent area = jm.get(entity);
                area.area.setPosition(
                        body.dynamicBody.getPosition().x-area.area.width/2,
                        body.dynamicBody.getPosition().y-body.fixture.getShape().getRadius()
                );
            }
        }
    }
    
    /* ..................................................................... GETTERS & SETTERS .. */
}
