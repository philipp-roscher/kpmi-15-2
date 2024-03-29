package org.sausagepan.prototyp.managers;

import org.sausagepan.prototyp.Utils.CompMappers;
import org.sausagepan.prototyp.model.components.CharacterSpriteComponent;
import org.sausagepan.prototyp.model.components.DynamicBodyComponent;
import org.sausagepan.prototyp.model.components.InjurableAreaComponent;
import org.sausagepan.prototyp.model.components.InputComponent;
import org.sausagepan.prototyp.model.components.LightComponent;
import org.sausagepan.prototyp.model.components.WeaponComponent;
import org.sausagepan.prototyp.model.entities.EntityFamilies;
import org.sausagepan.prototyp.model.items.Sword;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.Rectangle;

/**
 * Created by georg on 28.10.15.
 */
public class PositionSynchroSystem extends EntitySystem implements EntityListener {
    /* ............................................................................ ATTRIBUTES .. */
    private ImmutableArray<Entity> entities;

    /* ........................................................................... CONSTRUCTOR .. */
    public PositionSynchroSystem() {}

    /* ............................................................................... METHODS .. */
    /*
    Update method should synchronize positions of components

     */
    public void addedToEngine(Engine engine) {
        entities = engine.getEntitiesFor(EntityFamilies.positionSynchroFamily);
    }

    public void update(float deltaTime) {
        for (Entity entity : entities) {
            DynamicBodyComponent body = CompMappers.dynBody.get(entity);

            // Synchronize CharacterSprite with DynamicBody
            if(entity.getComponent(CharacterSpriteComponent.class) != null) {
                CharacterSpriteComponent sprite = CompMappers.charSprite.get(entity);
                sprite.sprite.setPosition(
                        body.dynamicBody.getPosition().x - sprite.sprite.getWidth()/2,
                        body.dynamicBody.getPosition().y - body.fixture.getShape().getRadius()
                );
            }

            // Synchronize Weapon with DynamicBody
            if(entity.getComponent(WeaponComponent.class) != null &&
                    entity.getComponent(InputComponent.class) != null) {
                WeaponComponent weapon = CompMappers.weapon.get(entity);
                InputComponent input = CompMappers.input.get(entity);

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
                LightComponent light = CompMappers.light.get(entity);

                light.spriteLight.setPosition(
                        body.dynamicBody.getPosition().x,
                        body.dynamicBody.getPosition().y
                );
            }

            // Synchronize InjurableArea with DynamicBody
            if(entity.getComponent(InjurableAreaComponent.class) != null) {
                InjurableAreaComponent area = CompMappers.injurableArea.get(entity);
                area.area.setPosition(
                        body.dynamicBody.getPosition().x-area.area.width/2,
                        body.dynamicBody.getPosition().y-body.fixture.getShape().getRadius()
                );
            }


            // Synchronize Sensor with DynamicBody
            if(CompMappers.sensor.get(entity) != null)
                CompMappers.sensor.get(entity).sensor.setTransform(
                        body.dynamicBody.getPosition(), 0f);
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
