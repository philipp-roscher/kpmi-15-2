package org.sausagepan.prototyp.managers;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;

import org.sausagepan.prototyp.model.components.DynamicBodyComponent;
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
    private ComponentMapper<SkyDirectionComponent> dm
            = ComponentMapper.getFor(SkyDirectionComponent.class);
    private ComponentMapper<PositionComponent> pm
            = ComponentMapper.getFor(PositionComponent.class);

    public WeaponSystem() {}

    /* ............................................................................... METHODS .. */
    public void addedToEngine(Engine engine) {
        entities = engine.getEntitiesFor(Family.all(
                WeaponComponent.class,
                SkyDirectionComponent.class,
                PositionComponent.class).get());
    }

    public void update(float deltaTime) {
        for (Entity entity : entities) {
            SkyDirectionComponent direction = dm.get(entity);
            WeaponComponent weapon = wm.get(entity);
            PositionComponent position = pm.get(entity);
            weapon.sprite.setPosition(position.x, position.y);
            int rotation;
            switch(direction.skyDirection) {
                case SOUTH: rotation = 180; break;
                case EAST:  rotation = 90; break;
                case WEST:  rotation = 270; break;
                default:    rotation = 0; break;
            }
            weapon.sprite.setRotation(rotation);
        }
    }

    /* ........................................................................... CONSTRUCTOR .. */
    
    /* ............................................................................... METHODS .. */
    
    /* ..................................................................... GETTERS & SETTERS .. */
}
