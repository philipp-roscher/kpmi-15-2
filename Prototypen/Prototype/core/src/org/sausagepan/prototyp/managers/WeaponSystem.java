package org.sausagepan.prototyp.managers;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;

import org.sausagepan.prototyp.model.components.DynamicBodyComponent;
import org.sausagepan.prototyp.model.components.InputComponent;
import org.sausagepan.prototyp.model.components.PositionComponent;
import org.sausagepan.prototyp.model.components.SkyDirectionComponent;
import org.sausagepan.prototyp.model.components.WeaponComponent;

/**
 * Created by georg on 22.10.15.
 */
public class WeaponSystem extends EntitySystem {
    /* ............................................................................ ATTRIBUTES .. */
    private ImmutableArray<Entity> entities;
    /* ........................................................................... CONSTRUCTOR .. */


    private ComponentMapper<WeaponComponent> wm
            = ComponentMapper.getFor(WeaponComponent.class);
    private ComponentMapper<InputComponent> im
            = ComponentMapper.getFor(InputComponent.class);

    public WeaponSystem() {}

    /* ............................................................................... METHODS .. */
    public void addedToEngine(Engine engine) {
        entities = engine.getEntitiesFor(Family.all(
                WeaponComponent.class,
                InputComponent.class).get());
    }

    public void update(float deltaTime) {
        for (Entity entity : entities) {
            WeaponComponent weapon = wm.get(entity);
            InputComponent input = im.get(entity);
            int rotation;
            switch(input.direction) {
                case SOUTH: rotation = 90;  break;
                case EAST:  rotation = 180; break;
                case WEST:  rotation = 0;   break;
                default:    rotation = -90; break;
            }
            weapon.sprite.setRotation(rotation);
            if(!input.attacking) weapon.sprite.visible = false;
            else                 weapon.sprite.visible = true;
        }
    }

    /* ........................................................................... CONSTRUCTOR .. */
    
    /* ............................................................................... METHODS .. */
    
    /* ..................................................................... GETTERS & SETTERS .. */
}
