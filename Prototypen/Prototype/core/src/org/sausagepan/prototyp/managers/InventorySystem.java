package org.sausagepan.prototyp.managers;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;

import org.sausagepan.prototyp.model.components.InventoryComponent;
import org.sausagepan.prototyp.model.components.WeaponComponent;

/**
 * Created by Bettina on 03.11.2015.
 */
public class InventorySystem extends EntitySystem {

    private ImmutableArray<Entity> characters;

    private ComponentMapper<InventoryComponent> im = ComponentMapper.getFor(InventoryComponent.class);
    private ComponentMapper<WeaponComponent> wm = ComponentMapper.getFor(WeaponComponent.class);

    public void addedToEngine(Engine engine)
    {
        characters = engine.getEntitiesFor(Family.all(WeaponComponent.class, InventoryComponent.class).get());
    }

    public void setWeaponInInventory()
    {
        for(Entity character: characters)
        {
            WeaponComponent wc = wm.get(character);
            InventoryComponent ic = im.get(character);

            ic.weapon = wc.weapon;
        }
    }
}
