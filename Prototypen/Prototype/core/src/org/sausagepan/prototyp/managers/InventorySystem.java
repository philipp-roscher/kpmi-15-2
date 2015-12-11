package org.sausagepan.prototyp.managers;

import org.sausagepan.prototyp.model.Maze;
import org.sausagepan.prototyp.model.components.DynamicBodyComponent;
import org.sausagepan.prototyp.model.components.InventoryComponent;
import org.sausagepan.prototyp.model.components.NetworkComponent;
import org.sausagepan.prototyp.model.components.NetworkTransmissionComponent;
import org.sausagepan.prototyp.model.components.WeaponComponent;
import org.sausagepan.prototyp.model.entities.CharacterEntity;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EntitySystem;

/**
 * Created by Bettina on 03.11.2015.
 */
public class InventorySystem extends EntitySystem {

    /*...................................................................................Atributes*/
    private CharacterEntity localCharacter;
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

    public InventorySystem(Maze maze, CharacterEntity character) {
        this.maze = maze;
        localCharacter = character;
    }

    /*...................................................................................Functions*/
    public void addedToEngine(Engine engine) { }

    public void update(float deltaTime)
    {
        InventoryComponent inventory = im.get(localCharacter);
        if(inventory.needsUpdate) {
            if(inventory.getKeyAmount() == 3) maze.openTreasureRoom();
            else maze.lockTreasureRoom();
            
            inventory.needsUpdate = false;
        }
    }
}
