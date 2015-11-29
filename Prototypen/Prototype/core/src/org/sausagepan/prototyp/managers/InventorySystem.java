package org.sausagepan.prototyp.managers;

import org.sausagepan.prototyp.model.Maze;
import org.sausagepan.prototyp.model.components.DynamicBodyComponent;
import org.sausagepan.prototyp.model.components.InjurableAreaComponent;
import org.sausagepan.prototyp.model.components.InventoryComponent;
import org.sausagepan.prototyp.model.components.NetworkComponent;
import org.sausagepan.prototyp.model.components.NetworkTransmissionComponent;
import org.sausagepan.prototyp.model.components.TeamComponent;
import org.sausagepan.prototyp.model.components.WeaponComponent;
import org.sausagepan.prototyp.model.items.KeyFragmentItem;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.utils.Array;

/**
 * Created by Bettina on 03.11.2015.
 */
public class InventorySystem extends ObservingEntitySystem {

    /*...................................................................................Atributes*/
    private ImmutableArray<Entity> characters;
    private Maze maze;

    private ComponentMapper<InventoryComponent> im
            = ComponentMapper.getFor(InventoryComponent.class);
    private ComponentMapper<WeaponComponent> wm
            = ComponentMapper.getFor(WeaponComponent.class);
    private ComponentMapper<DynamicBodyComponent> dbm
            = ComponentMapper.getFor(DynamicBodyComponent.class);
    private ComponentMapper<NetworkComponent> nm
            = ComponentMapper.getFor(NetworkComponent.class);
    private ComponentMapper<NetworkTransmissionComponent> ntm
            = ComponentMapper.getFor(NetworkTransmissionComponent.class);

    public InventorySystem(Maze maze) {
        this.maze = maze;
    }

    /*...................................................................................Functions*/
    public void addedToEngine(ObservableEngine engine)
    {
        characters = engine.getEntitiesFor(Family.all(
                WeaponComponent.class,
                InventoryComponent.class,
                TeamComponent.class,
                DynamicBodyComponent.class,
                NetworkComponent.class,
                InjurableAreaComponent.class).get());
    }

    public void update(float deltaTime)
    {
        for (Entity entity : characters) {
            InventoryComponent inventory = im.get(entity);
            NetworkComponent network = nm.get(entity);
            NetworkTransmissionComponent ntc = ntm.get(entity);
            DynamicBodyComponent body = dbm.get(entity);

            // If a new key fragment hast been picked up, notify server
            if(inventory.justPGotKey) {
                ntc.takeKey.add(inventory.recentlyFoundKey.keyFragmentNr);
                inventory.justPGotKey = false;
                inventory.recentlyFoundKey = null;
                if(inventory.keyFragments.size == 3) maze.openTreasureRoom();
            }

            // If a character lost key fragments, notify server
            if(inventory.justLostKey) {
                for(KeyFragmentItem kf : inventory.keyFragments)
                	ntc.loseKey.add(kf.keyFragmentNr);
                inventory.justLostKey = false;
                inventory.keyFragments = new Array<KeyFragmentItem>();
                maze.lockTreasureRoom();
            }
        }
    }

    public void setWeaponInInventory()
    {
        for(Entity character: characters)
        {
            im.get(character).weapon = wm.get(character).weapon;
        }
    }
}
