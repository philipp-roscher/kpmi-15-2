package org.sausagepan.prototyp.managers;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;

import org.sausagepan.prototyp.model.components.NetworkTransmissionComponent;
import org.sausagepan.prototyp.model.components.WeaponComponent;
import org.sausagepan.prototyp.model.items.Bow;
import org.sausagepan.prototyp.model.items.Sword;

/**
 * Created by georg on 22.10.15.
 */
public class WeaponSystem extends EntitySystem implements EntityListener {
    /* ............................................................................ ATTRIBUTES .. */
    private ImmutableArray<Entity> entities;
    /* ........................................................................... CONSTRUCTOR .. */

    private ComponentMapper<WeaponComponent> wm
            = ComponentMapper.getFor(WeaponComponent.class);
    private ComponentMapper<NetworkTransmissionComponent> ntm
            = ComponentMapper.getFor(NetworkTransmissionComponent.class);

    public WeaponSystem() {}

    /* ............................................................................... METHODS .. */
    public void addedToEngine(Engine engine) {
        entities = engine.getEntitiesFor(Family.all(
                WeaponComponent.class,
                NetworkTransmissionComponent.class).get());
    }

    public void update(float deltaTime) {
        for (Entity entity : entities) {
            WeaponComponent weapon = wm.get(entity);
            NetworkTransmissionComponent ntc = ntm.get(entity);
            if(weapon.weapon.justUsed) {
                // Handle Sword
                if(weapon.weapon.getClass().equals(Sword.class))
                    ntc.attack = true;

                // Handle Bow
                if(weapon.weapon.getClass().equals(Bow.class)) {
                    ntc.shoot = true;
                    weapon.weapon.justUsed = false;
                }
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
